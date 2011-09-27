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

/**
 * singleton to hold an instance of a Game
 * 
 * @author ligi
 *
 */
public class GoGameProvider {

	private static GoGame game;
	
	/**
	 * set the game instance
	 * @param p_game the game to set as current
	 * 
	 */
	public static void setGame(GoGame p_game) {
		game=p_game;
	}
	
	/**
	 * @return the game instance
	 */
	public static GoGame getGame() {
		return game;
	}
	
}
