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

import org.ligi.gobandroid_hd.logic.GoGame;
import org.ligi.gobandroid_hd.logic.GoGameMetadata;
import org.ligi.gobandroid_hd.logic.GoMove;
import org.ligi.gobandroid_hd.logic.markers.CircleMarker;
import org.ligi.gobandroid_hd.logic.markers.GoMarker;
import org.ligi.gobandroid_hd.logic.markers.SquareMarker;
import org.ligi.gobandroid_hd.logic.markers.TriangleMarker;
import org.ligi.tracedroid.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * class for load games in SGF File-Format
 */
public class SGFReader {


    public final static int BREAKON_NOTHING = 0;
    public final static int BREAKON_FIRSTMOVE = 1;

    public final static int DEFAULT_SGF_TRANSFORM = 0;

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
            Log.i("sgf to process:" + sgf);
            byte size = -1;
            GoGame game = null;

            byte opener = 0;

            boolean escape = false;
            // int param_level=0;
            final List<GoMove> variationList = new ArrayList<>();
            boolean consuming_param = false;

            String act_param = "";
            String act_cmd = "";
            String last_cmd = "";

            int predef_count_b = 0;
            int predef_count_w = 0;

            GoGameMetadata metadata = new GoGameMetadata();

            boolean break_pulled = false;

            for (int p = 0; ((p < sgf.length()) && (!break_pulled)); p++) {
                char act_char = sgf.charAt(p);
                if (!consuming_param)
                    // consuming command
                    switch (act_char) {
                        case '\r':
                        case '\n':
                        case ';':
                        case '\t':
                        case ' ':
                            if (!act_cmd.equals(""))
                                last_cmd = act_cmd;
                            act_cmd = "";
                            break;

                        case '[':
                            if (act_cmd.equals(""))
                                act_cmd = last_cmd;

                            // for files without SZ - e.g. ggg-intermediate-11.sgf
                            if ((game == null) && (act_cmd.equals("AB") || act_cmd.equals("AW") || act_cmd.equals("TR") || act_cmd.equals("SQ") || act_cmd.equals("LB") || act_cmd.equals("MA"))) {
                                size = 19;
                                game = new GoGame((byte) 19);
                            }
                            consuming_param = true;
                            act_param = "";
                            break;

                        case '(':

                            if (!consuming_param) {
                                // for files without SZ
                                if ((opener == 1) && (game == null)) {
                                    size = 19;
                                    game = new GoGame((byte) 19);
                                    variationList.add(game.getActMove());
                                }

                                opener++;

                                // push the move we where to the stack to return
                                // here
                                // after the variation

                                // if (param_level!=0) break;
                                Log.i("   !!! opening variation" + game);
                                if (game != null) {

                                    variationList.add(game.getActMove());
                                }

                                last_cmd = "";
                                act_cmd = "";
                            }
                            break;
                        case ')':
                            if (variationList.size() > 0) {
                                GoMove lastMove = variationList.get(variationList.size() - 1);
                                game.jump(lastMove);
                                variationList.remove(lastMove);
                                Log.w("popping variaton from stack");
                            } else {
                                Log.w("variation vector underrun!!");
                            }

                            last_cmd = "";
                            act_cmd = "";

                            break;

                        default:
                            act_cmd += sgf.charAt(p);

                    }
                else {

                    // consuming param
                    switch (act_char) {
                        case ']': // closing command parameter -> can process
                            // command
                            // now
                            if ((game != null) && (callback != null))
                                callback.progress(p, sgf.length(), game.getActMove().getMovePos());
                            if (!escape) {
                                consuming_param = false;

                                byte param_x = 0, param_y = 0;

                                // if we have a minimum of 2 chars in param - could
                                // be
                                // coords - so parse
                                if (act_param.length() >= 2) {
                                    param_x = (byte) (act_param.charAt(((transform & 4) == 0) ? 0 : 1) - 'a');
                                    param_y = (byte) (act_param.charAt(((transform & 4) == 0) ? 1 : 0) - 'a');

                                    if ((transform & 1) > 0)
                                        param_y = (byte) (size - 1 - param_y);

                                    if ((transform & 2) > 0)
                                        param_x = (byte) (size - 1 - param_x);

                                }

                                // if command is empty -> use the last command
                                if (act_cmd.length() == 0)
                                    act_cmd = last_cmd;

                                // marker section - infos here
                                // http://www.red-bean.com/sgf/properties.html

                                // marker with text
                                if (act_cmd.equals("LB")) {
                                    String[] inner = act_param.split(":");
                                    String txt = "X";
                                    // if (inner.length > 1) TODO check why this was
                                    // done once
                                    txt = inner[1];

                                    game.getActMove().addMarker(new GoMarker(param_x, param_y, txt));
                                }

                                // mark with x
                                if (act_cmd.equals("Mark") | act_cmd.equals("MA"))
                                    game.getActMove().addMarker(new GoMarker(param_x, param_y, "X"));

                                // mark with triangle
                                if (act_cmd.equals("TR")) {
                                    game.getActMove().addMarker(new TriangleMarker(param_x, param_y));
                                }

                                // mark with square
                                if (act_cmd.equals("SQ")) {
                                    game.getActMove().addMarker(new SquareMarker(param_x, param_y));
                                }

                                // mark with circle
                                if (act_cmd.equals("CR")) {
                                    game.getActMove().addMarker(new CircleMarker(param_x, param_y));
                                }

                                if (act_cmd.equals("GN")) // Game Name
                                    metadata.setName(act_param);

                                if (act_cmd.equals("PW")) // Player White Name
                                    metadata.setWhiteName(act_param);

                                if (act_cmd.equals("PB")) // Player Black Name
                                    metadata.setBlackName(act_param);

                                if (act_cmd.equals("WR")) // Player White Rank
                                    metadata.setWhiteRank(act_param);

                                if (act_cmd.equals("BR")) // Player Black Rank
                                    metadata.setBlackRank(act_param);

                                if (act_cmd.equals("RE")) // Game Result
                                    metadata.setResult(act_param);

                                if (act_cmd.equals("SO")) // Source
                                    metadata.setResult(act_param);

                                // size command
                                if (act_cmd.equals("SiZe") || act_cmd.equals("SZ")) {
                                    act_param = act_param.replaceAll("[^0-9]", ""); // had
                                    // a
                                    // case
                                    // of
                                    // SiZe[
                                    // 19
                                    // ]
                                    // was
                                    // throwing
                                    // java.lang.NumberFormatException
                                    // -
                                    // so
                                    // fixing
                                    size = Byte.parseByte(act_param);
                                    if ((game == null) || (game.getBoardSize() != size)) {
                                        game = new GoGame(size);
                                        variationList.add(game.getActMove());
                                    }
                                }

                                // comment command
                                if (act_cmd.equals("Comment") || act_cmd.equals("C")) {
                                    if (game != null)
                                        game.getActMove().setComment(act_param);
                                }

                                // move command
                                if (act_cmd.equals("Black") || act_cmd.equals("B") || act_cmd.equals("W") || act_cmd.equals("White")) {

                                    // if still no game open -> open one with
                                    // default
                                    // size
                                    if (game == null) {
                                        game = new GoGame((byte) 19);
                                        variationList.add(game.getActMove());
                                    }

                                    if ((breakon & BREAKON_FIRSTMOVE) > 0)
                                        break_pulled = true;

                                    if (game.getActMove().isFirstMove()) {
                                        game.apply_handicap();
                                    }

                                    game.getActMove().setIsBlackToMove(!(act_cmd.equals("Black") || act_cmd.equals("B")));

                                    if (act_param.length() == 0)
                                        game.pass();
                                    else {
/*                                        if (game.getActMove().isFirstMove()) {
                                            game.getActMove().setIsBlackToMove();
                                        }
                                        */
                                        final byte b = game.do_move(param_x, param_y);
                                        if (b != GoGame.MOVE_VALID) {
                                            Log.w("There was a problem in this game");
                                        }
                                    }


                                }

                                // TODO support AddEmpty
                                // handle predefined stones ( mostly handicap stones
                                // )
                                // in SGF
                                if (act_cmd.equals("AddBlack") || act_cmd.equals("AB") || act_cmd.equals("AW") || act_cmd.equals("AddWhite")) {

                                    if (game == null) { // create a game if it is
                                        // not
                                        // there yet
                                        game = new GoGame((byte) 19);
                                        variationList.add(game.getActMove());
                                    }

                                    if (act_param.length() != 0) {
                                        if (game.isBlackToMove() && (act_cmd.equals("AB") || act_cmd.equals("AddBlack"))) {
                                            predef_count_b++;
                                            game.getHandicapBoard().setCellBlack(param_x, param_y);
                                        }

                                        if (game.isBlackToMove() && (act_cmd.equals("AW") || act_cmd.equals("AddWhite"))) {
                                            predef_count_w++;
                                            game.getHandicapBoard().setCellWhite(param_x, param_y);

                                        }
                                    } else
                                        Log.w("AB / AW command without param");

                                }

                                last_cmd = act_cmd;
                                act_cmd = "";
                                act_param = "";

                            }
                            break;
                        case '\\':
                            if (escape) {
                                act_param += (char) act_char;
                                escape = false;
                            } else
                                escape = true;
                            break;
                        default:
                            act_param += (char) act_char;
                            escape = false;
                            break;

                    }
                }

            }

            if (game != null) {
                if (game.getActMove().isFirstMove() && predef_count_w == 0 && predef_count_b > 0) {
                    game.getActMove().setIsBlackToMove(true); // propably
                    // handycap - so
                    // make white to
                    // move - very
                    // imortant for
                    // cloud game
                    // and handycap
                }
                game.setMetadata(metadata);
            }
            return game;

        } catch (Exception e) { // some weird sgf - we want to catch to not FC
            // and have the chance to send the sgf to
            // analysis
            Log.w("Problem parsing SGF " + e);
        }

        return null;
    }

    public interface ISGFLoadProgressCallback {
        public void progress(int act, int max, int progress_val);
    }


}
