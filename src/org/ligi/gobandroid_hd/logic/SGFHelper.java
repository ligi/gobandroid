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

package org.ligi.gobandroid_hd.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.ligi.tracedroid.logging.Log;

/**
 * class for (de)serializing games to/from SGF
 * 
 * @author Marcus -Ligi- Bueschleb
 * 
 *         This software is licenced with GPLv3
 **/
public class SGFHelper {

	/**
	 * convert tree of moves to a string to use in SGF next moves are processed
	 * recursive
	 * 
	 * @param move
	 *            - the start move
	 * @param black_to_move
	 * @return
	 */
	private static String moves2string(GoMove move) {
		String res = "";

		GoMove act_move = move;

		while (act_move != null) {

			// add the move
			if (!act_move.isFirstMove()) {
				res += ";" + (act_move.isBlackToMove() ? "B" : "W");
				if (act_move.isPassMove())
					res += "[]";
				else
					res += coords2SGFFragment(+act_move.getX(), act_move.getY())
							+ "\n";
			}

			// add the comment
			if (!act_move.getComment().equals(""))
				res += "C[" + act_move.getComment() + "]\n";

			GoMove next_move = null;

			if (act_move.hasNextMove()) {
				if (act_move.hasNextMoveVariations())
					for (GoMove var : act_move.getNextMoveVariations())
						res += "(" + moves2string(var) + ")";
				else
					next_move = act_move.getnextMove(0);
			}

			act_move = next_move;
		}
		return res;
	}

	public static String escapeSGF(String txt) {
		txt.replace("]", "\\]");
		txt.replace(")", "\\)");
		txt.replace("\\", "\\\\");
		return txt;
	}

	private static String getSGFSnippet(String cmd, String param) {
		if ((param == null) || (param.equals("")) || (cmd == null)
				|| (cmd.equals("")))
			return "";
		return cmd + "[" + escapeSGF(param) + "]";
	}

	public static String game2sgf(GoGame game) {
		String res = "";
		res = "(;FF[4]GM[1]AP[gobandroid:0]"; // header
		res += getSGFSnippet("SZ", "" + game.getBoardSize()); // board_size;
		res += getSGFSnippet("GN", escapeSGF(game.getMetaData().getName()));
		res += getSGFSnippet("PB", escapeSGF(game.getMetaData().getBlackName()));
		res += getSGFSnippet("PW", escapeSGF(game.getMetaData().getWhiteName()));
		res += getSGFSnippet("BR", escapeSGF(game.getMetaData().getBlackRank()));
		res += getSGFSnippet("WR", escapeSGF(game.getMetaData().getWhiteRank()));
		res += getSGFSnippet("KM", escapeSGF(Float.toString(game.getKomi())));
		res += getSGFSnippet("RE", escapeSGF(game.getMetaData().getResult()));
		res += getSGFSnippet("SO", escapeSGF(game.getMetaData().getSource()));
		res += "\n";

		for (int x = 0; x < game.getHandicapBoard().getSize(); x++)
			for (int y = 0; y < game.getHandicapBoard().getSize(); y++)
				if (game.getHandicapBoard().isCellWhite(x, y))
					res += "AW" + coords2SGFFragment(x, y) + "\n";
				else if (game.getHandicapBoard().isCellBlack(x, y))
					res += "AB" + coords2SGFFragment(x, y) + "\n";

		res += moves2string(game.getFirstMove()) + ")";

		return res;
	}

	private final static String coords2SGFFragment(int x, int y) {
		return "[" + (char) ('a' + x) + (char) ('a' + y) + "]";
	}

	public final static int BREAKON_NOTHING = 0;
	public final static int BREAKON_FIRSTMOVE = 1;

	public final static int DEFAULT_SGF_TRANSFORM = 0;

	public static GoGame sgf2game(String sgf, ISGFLoadProgressCallback callback) {
		return sgf2game(sgf, callback, BREAKON_NOTHING, DEFAULT_SGF_TRANSFORM);
	}

	public static GoGame sgf2game(String sgf,
			ISGFLoadProgressCallback callback, int breakon) {
		return sgf2game(sgf, callback, breakon, DEFAULT_SGF_TRANSFORM);
	}

	/**
	 * @param sgf
	 * @param callback
	 * @param breakon
	 * @param transform
	 *            - bit 1 => mirror y ; bit 2 => mirror x ; bit 3 => swap x/y
	 * @return
	 */
	public static GoGame sgf2game(String sgf,
			ISGFLoadProgressCallback callback, int breakon, int transform) {
		try {
			Log.i("sgf to process:" + sgf);
			byte size = -1;
			GoGame game = null;

			byte opener = 0;

			boolean escape = false;
			// int param_level=0;
			Vector<GoMove> var_vect = new Vector<GoMove>();
			boolean consuming_param = false;

			String act_param = "";
			String act_cmd = "";
			String last_cmd = "";

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
						if ((game == null)
								&& (act_cmd.equals("AB")
										|| act_cmd.equals("AW")
										|| act_cmd.equals("TR")
										|| act_cmd.equals("SQ")
										|| act_cmd.equals("LB") || act_cmd
											.equals("MA"))) {
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
								var_vect.add(game.getActMove());
							}

							opener++;

							// push the move we where to the stack to return
							// here
							// after the variation

							// if (param_level!=0) break;
							Log.i("   !!! opening variation" + game);
							if (game != null) {

								var_vect.add(game.getActMove());
							}

							last_cmd = "";
							act_cmd = "";
						}
						break;
					case ')':
						if (var_vect.size() > 0) {
							game.jump(var_vect.lastElement());
							var_vect.remove(var_vect.lastElement());
							Log.w("popping variaton from stack");
						} else
							Log.w("variation vector underrun!!");

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
							callback.progress(p, sgf.length(), game
									.getActMove().getMovePos());
						if (!escape) {
							consuming_param = false;

							byte param_x = 0, param_y = 0;

							// if we have a minimum of 2 chars in param - could
							// be
							// coords - so parse
							if (act_param.length() >= 2) {
								param_x = (byte) (act_param
										.charAt(((transform & 4) == 0) ? 0 : 1) - 'a');
								param_y = (byte) (act_param
										.charAt(((transform & 4) == 0) ? 1 : 0) - 'a');

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
								if (inner.length > 1)
									txt = inner[1];

								game.getActMove().addMarker(
										new GoMarker(param_x, param_y, txt));
							}

							// mark with x
							if (act_cmd.equals("Mark") | act_cmd.equals("MA"))
								game.getActMove().addMarker(
										new GoMarker(param_x, param_y, "X"));

							// mark with triangle
							if (act_cmd.equals("TR")) {
								String mark = "\u25b3";
								game.getActMove().addMarker(
										new GoMarker(param_x, param_y, mark));
							}

							// mark with square
							if (act_cmd.equals("SQ")) {
								String mark = "\u25a1";
								game.getActMove().addMarker(
										new GoMarker(param_x, param_y, mark));
							}

							// mark with circle
							if (act_cmd.equals("CR")) {
								String mark = "\u25cb";
								game.getActMove().addMarker(
										new GoMarker(param_x, param_y, mark));
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
								if ((game == null)
										|| (game.getBoardSize() != size)) {
									game = new GoGame(size);
									var_vect.add(game.getActMove());
								}
							}

							// comment command
							if (act_cmd.equals("Comment")
									|| act_cmd.equals("C")) {
								if (game != null)
									game.getActMove().setComment(act_param);
							}

							// move command
							if (act_cmd.equals("Black") || act_cmd.equals("B")
									|| act_cmd.equals("W")
									|| act_cmd.equals("White")) {

								// if still no game open -> open one with
								// default
								// size
								if (game == null) {
									game = new GoGame((byte) 19);
									var_vect.add(game.getActMove());
								}

								if ((breakon & BREAKON_FIRSTMOVE) > 0)
									break_pulled = true;

								if (game.getActMove().isFirstMove())
									game.apply_handicap();

								if (act_param.length() == 0)
									game.pass();
								else
									game.do_move(param_x, param_y);

								game.getActMove().setIsBlackToMove(
										act_cmd.equals("Black")
												|| act_cmd.equals("B"));

							}

							// TODO support AddEmpty
							// handle predefined stones ( mostly handicap stones
							// )
							// in SGF
							if (act_cmd.equals("AddBlack")
									|| act_cmd.equals("AB")
									|| act_cmd.equals("AW")
									|| act_cmd.equals("AddWhite")) {

								if (game == null) { // create a game if it is
													// not
													// there yet
									game = new GoGame((byte) 19);
									var_vect.add(game.getActMove());
								}

								if (act_param.length() != 0) {
									if (game.isBlackToMove()
											&& (act_cmd.equals("AB") || act_cmd
													.equals("AddBlack")))
										game.getHandicapBoard().setCellBlack(
												param_x, param_y);
									if (game.isBlackToMove()
											&& (act_cmd.equals("AW") || act_cmd
													.equals("AddWhite")))
										game.getHandicapBoard().setCellWhite(
												param_x, param_y);
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

			if (game != null)
				game.setMetadata(metadata);
			return game;

		} catch (Exception e) { // some weird sgf - we want to catch to not FC
								// and have the chance to send the sgf to
								// analysis
		}

		return null;
	}

	public interface ISGFLoadProgressCallback {
		public void progress(int act, int max, int progress_val);
	}

	public static boolean saveSGF(GoGame game, String fname) {

		File f = new File(fname);

		if (f.isDirectory())
			throw new IllegalArgumentException(
					"cannot write - fname is a directory");

		if (f.getParentFile() == null) // not really sure when this can be the
										// case ( perhaps only / ) - but the doc
										// says it can be null and I would get
										// NPE then
			throw new IllegalArgumentException("bad filename " + fname);

		if (f.getParentFile() != null && !f.getParentFile().isDirectory()) // if
																			// the
																			// path
																			// is
																			// not
																			// there
																			// yet
			f.getParentFile().mkdirs();

		try {
			// f=new File(path+ "/"+fname);
			f.createNewFile();

			FileWriter sgf_writer = new FileWriter(f);

			BufferedWriter out = new BufferedWriter(sgf_writer);

			out.write(SGFHelper.game2sgf(game));
			out.close();
			sgf_writer.close();

		} catch (IOException e) {
			Log.i("" + e);
			return false;
		}

		game.getMetaData().setFileName(fname);
		return true;

	}
}
