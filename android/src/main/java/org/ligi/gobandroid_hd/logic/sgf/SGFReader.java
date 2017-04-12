/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package org.ligi.gobandroid_hd.logic.sgf;

import android.text.TextUtils;
import org.ligi.gobandroid_hd.logic.CellImpl;
import org.ligi.gobandroid_hd.logic.GoDefinitions;
import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.logic.markers.CircleMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TextMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ligi.gobandroid_hd.logic.GoGame.MoveStatus.VALID;

/**
 * class for load games in SGF File-Format
 */
public class SGFReader {

    public final static int BREAKON_NOTHING = 0;
    public final static int BREAKON_FIRSTMOVE = 1;

    public final static int DEFAULT_SGF_TRANSFORM = 0;

    private String act_param = "";
    private String act_cmd = "";
    private String last_cmd = "";
    private GoGame game = null;
    private byte size = -1;
    private int predef_count_b = 0;
    private int predef_count_w = 0;
    private boolean break_pulled = false;

    private final GoGameMetadata metadata;
    private final List<GoMove> variationList;

    private final String sgf;
    private final ISGFLoadProgressCallback callback;
    private final int breakon;
    private final int transform;

    private SGFReader(String sgf, ISGFLoadProgressCallback callback, int breakon, int transform) {
        this.sgf = sgf;
        this.callback = callback;
        this.breakon = breakon;

        this.transform = transform;
        metadata = new GoGameMetadata();
        variationList = new ArrayList<>();

    }

    public static GoGame sgf2game(String sgf, ISGFLoadProgressCallback callback) {
        return sgf2game(sgf, callback, BREAKON_NOTHING, DEFAULT_SGF_TRANSFORM);
    }

    public static GoGame sgf2game(String sgf, ISGFLoadProgressCallback callback, int breakon) {
        return sgf2game(sgf, callback, breakon, DEFAULT_SGF_TRANSFORM);
    }

    /**
     * @param sgf
     * @param callback
     * @param breakon
     * @param transform - bit 1 => mirror y ; bit 2 => mirror x ; bit 3 => swap x/y
     * @return
     */
    public static GoGame sgf2game(String sgf, ISGFLoadProgressCallback callback, int breakon, int transform) {
        try {
            return new SGFReader(sgf, callback, breakon, transform).getGame();
        } catch (Exception e) { // some weird sgf - we want to catch to not FC
            // and have the chance to send the sgf to analysis
            e.printStackTrace();
            Log.w("Problem parsing SGF " + e);
            return null;
        }
    }

    public interface ISGFLoadProgressCallback {
        void progress(int act, int max, int progress_val);
    }

    private GoGame getGame() {
        byte opener = 0;
        boolean escape = false;
        boolean consuming_param = false;

        for (int p = 0; ((p < sgf.length()) && (!break_pulled)); p++) {
            char act_char = sgf.charAt(p);
            if (!consuming_param) {
                // non-consuming command
                switch (act_char) {
                    case '\r':
                    case '\n':
                    case ';':
                    case '\t':
                    case ' ':
                        if (!act_cmd.isEmpty()) {
                            last_cmd = act_cmd;
                        }
                        act_cmd = "";
                        break;

                    case '[':
                        if (act_cmd.isEmpty()) {
                            act_cmd = last_cmd;
                        }

                        // for files without SZ - e.g. ggg-intermediate-11.sgf
                        List<Marker> toCheck = Arrays.asList(Marker.ADD_BLACK, Marker.ADD_WHITE, Marker.MARK_X, Marker.MARK_TRIANGLE, Marker.MARK_SQUARE, Marker.MARK_POINT);
                        if(game == null && toCheck.contains(Marker.withCode(act_cmd))) {
                            size = 19;
                            game = new GoGame(size);
                        }
                        consuming_param = true;
                        act_param = "";
                        break;

                    case '(':
                        if (!consuming_param) {
                            // for files without SZ
                            if (opener == 1 && game == null) {
                                size = 19;
                                game = new GoGame((byte) 19);
                                variationList.add(getOrCreateGame().getActMove());
                            }

                            opener++;

                            // push the move we where to the stack to return
                            // here after the variation
                            // if (param_level!=0) break;
                            Log.i("   !!! opening variation" + game);
                            if (game != null) {
                                variationList.add(getOrCreateGame().getActMove());
                            }

                            last_cmd = "";
                            act_cmd = "";
                        }
                        break;
                    case ')':
                        if (variationList.isEmpty()) {
                            Log.w("variation vector underrun!!");
                        } else {
                            GoMove lastMove = variationList.get(variationList.size() - 1);
                            getOrCreateGame().jump(lastMove);
                            variationList.remove(lastMove);
                            Log.w("popping variaton from stack");
                        }

                        last_cmd = "";
                        act_cmd = "";
                        break;
                    default:
                        act_cmd += Character.toString(sgf.charAt(p));
                }
            } else {
                // consuming param
                switch (act_char) {
                    case ']': // closing command parameter -> can process command now
                        if ((game != null) && (callback != null)) {
                            callback.progress(p, sgf.length(), getOrCreateGame().getActMove().getMovePos());
                        }
                        if (!escape) {
                            consuming_param = false;
                            processCommand();
                        }
                        break;
                    case '\\':
                        if (escape) {
                            act_param += Character.toString(act_char);
                            escape = false;
                        } else
                            escape = true;
                        break;
                    default:
                        act_param += Character.toString(act_char);
                        escape = false;
                        break;
                }

            }
        }

        if (game != null) {
            game.setMetadata(metadata);
            if(game.getActMove().isFirstMove() && predef_count_w == 0 && predef_count_b > 0) {
                game.getActMove().setPlayer(GoDefinitions.PLAYER_BLACK); // probably handicap - so  make white
                // to  move - very  important for cloud game and handicap
            }
        }

        //if (game.getFirstMove())
        return game;
    }

    private void processCommand() {
        int paramX = 0;
        int paramY = 0;

        // if we have a minimum of 2 chars in param - could be coords - so parse
        if (act_param.length() >= 2) {
            paramX = act_param.charAt(((transform & 4) == 0) ? 0 : 1) - 'a';
            paramY = act_param.charAt(((transform & 4) == 0) ? 1 : 0) - 'a';

            if (size>-1) {
                if ((transform & 1) > 0)
                    paramY = (byte) (size - 1 - paramY);

                if ((transform & 2) > 0)
                    paramX = (byte) (size - 1 - paramX);
            }
        }

        final CellImpl cell = new CellImpl(paramX, paramY);
        // if command is empty -> use the last command
        if (act_cmd.isEmpty()) {
            act_cmd = last_cmd;
        }

        // marker section - info here http://www.red-bean.com/sgf/properties.html
        Marker marker = Marker.withCode(act_cmd);
        switch(marker) {
            case BLACK_MOVE:
            case WHITE_MOVE:
                // if still no game open -> open one with default size
                if (game == null) {
                    game = new GoGame((byte) 19);
                    variationList.add(game.getActMove());
                }

                if(game.actMove.isFirstMove()) {
                    byte lastPlayer = marker == Marker.WHITE_MOVE
                        ? GoDefinitions.PLAYER_BLACK : GoDefinitions.PLAYER_WHITE;
                    game.actMove.setPlayer(lastPlayer);
                }

                if ((breakon & BREAKON_FIRSTMOVE) > 0) {
                    break_pulled = true;
                }

                if (act_param.length() == 0) {
                    game.pass();
                } else {
                    final GoGame.MoveStatus moveStatus = game.do_move(cell);
                    if (moveStatus != VALID) {
                        Log.w("There was a problem in this game");
                    }
                }
                break;
            case ADD_BLACK:
            case ADD_WHITE:
                Log.i("Adding stone " + act_cmd + " " + act_param + " at " + cell);
                if (game == null) { // create a game if it is not there yet
                    game = new GoGame((byte) 19);
                    variationList.add(game.getActMove());
                }

                if (act_param.length() != 0) {
                    if (game.isBlackToMove() && marker == Marker.ADD_BLACK) {
                        predef_count_b++;
                        game.getHandicapBoard().setCell(cell, GoDefinitions.STONE_BLACK);
                    } else if (game.isBlackToMove() && marker == Marker.ADD_WHITE) {
                        predef_count_w++;
                        game.getHandicapBoard().setCell(cell, GoDefinitions.STONE_WHITE);
                    }
                } else {
                    Log.w("AB / AW command without param");
                }
                break;
            case WRITE_TEXT:
                final String[] inner = act_param.split(":");
                final String txt = inner.length > 1 ? inner[1] : "X";
                getOrCreateGame().getActMove().addMarker(new TextMarker(cell, txt));
                break;
            case MARK_X:
                getOrCreateGame().getActMove().addMarker(new TextMarker(cell, "X"));
                break;
            case MARK_TRIANGLE:
                getOrCreateGame().getActMove().addMarker(new TriangleMarker(cell));
                break;
            case MARK_SQUARE:
                getOrCreateGame().getActMove().addMarker(new SquareMarker(cell));
                break;
            case MARK_CIRCLE:
                getOrCreateGame().getActMove().addMarker(new CircleMarker(cell));
                break;
            case MARK_POINT:
                getOrCreateGame().getActMove().addMarker(new TextMarker(cell, "+"));
                break;
            case GAME_NAME:
                metadata.setName(act_param);
                break;
            case DIFFICULTY:
                metadata.setDifficulty(act_param);
                break;
            case WHITE_NAME:
                metadata.setWhiteName(act_param);
                break;
            case BLACK_NAME:
                metadata.setBlackName(act_param);
                break;
            case WHITE_RANK:
                metadata.setWhiteRank(act_param);
                break;
            case BLACK_RANK:
                metadata.setBlackRank(act_param);
                break;
            case GAME_RESULT:
                metadata.setResult(act_param);
                break;
            case DATE:
                metadata.setDate(act_param);
                break;
            case SOURCE:
                metadata.setSource(act_param);
                break;
            case KOMI:
                try {
                    getOrCreateGame().setKomi(Float.parseFloat(act_param));
                } catch (NumberFormatException ignored) {
                    // catch a bad komi-statement like seen KM[] - would now even catch KM[crashme]
                }
                break;
            case SIZE:
                act_param = act_param.replaceAll("[^0-9]", "");
                if (!act_param.isEmpty()) { // got a SGF with SZ[]
                    size = Byte.parseByte(act_param);
                    if (game == null || game.getBoardSize() != size) {
                        game = new GoGame(size);
                        variationList.add(game.getActMove());
                    }
                }
                break;
            case COMMENT:
                if (game != null) {
                    game.getActMove().setComment(act_param);
                }
                break;
            case UNKNOWN:
            case NONE:
                break;
        }

        last_cmd = act_cmd;
        act_cmd = "";
        act_param = "";
    }

    private GoGame getOrCreateGame() {
        if (game == null) {
            size = 19;
            game = new GoGame(size);
        }
        return game;
    }

    enum Marker {
        //Move Properties
        BLACK_MOVE("B", "Black"), WHITE_MOVE("W", "White"),

        //Setup Properties
        ADD_BLACK("AB", "AddBlack"), ADD_WHITE("AW", "AddWhite"),

        //Node Annotation Properties
        COMMENT("C", "Comment"),

        //Markup Properties
        WRITE_TEXT("LB"), MARK_X("MA", "Mark"), MARK_TRIANGLE("TR"),
        MARK_SQUARE("SQ"), MARK_CIRCLE("CR"), MARK_POINT("SL"),

        //Game Info Properties
        GAME_NAME("GN"), DIFFICULTY("DI"), WHITE_NAME("PW"),
        BLACK_NAME("PB"), WHITE_RANK("WR"), BLACK_RANK("BR"),
        GAME_RESULT("RE"), DATE("DT"), SOURCE("SO"),
        KOMI("KM"), SIZE("SZ", "SiZe"),

        UNKNOWN("?"),
        NONE("");

        private String code;
        private String alternateCode;

        Marker(String code) {
            this.code = code;
        }

        Marker(String code, String alternateCode) {
            this.code = code;
            this.alternateCode = alternateCode;
        }

        public static Marker withCode(String code) {
            if(TextUtils.isEmpty(code)) {
                return NONE;
            }

            for(Marker marker : values()) {
                if(code.equals(marker.code) || code.equals(marker.alternateCode)) {
                    return marker;
                }
            }

            return UNKNOWN;
        }
    }
}
