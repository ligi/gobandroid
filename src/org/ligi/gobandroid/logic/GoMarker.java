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

package org.ligi.gobandroid.logic;

/**
 * class to mark a position on a board - useful for go problems
 * 
 * @author ligi
 *
 */
public class GoMarker {

	// the position on the board
	private byte x,y;
	private char text;
	
	public GoMarker(byte x,byte y , char chr) {
		this.x=x;
		this.y=y;
		this.text=chr;
	}
	
	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public char getText() {
		return text;
	}

}
