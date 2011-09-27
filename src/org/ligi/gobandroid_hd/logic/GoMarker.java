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
 * class to mark a pos on the board 
 * useful for go problems - e.g. from SGF
 * 
 * @author ligi
 *
 */
public class GoMarker {

	private byte x,y;
	private String text;
	
	public GoMarker(byte x,byte y , String text) {
		this.x=x;
		this.y=y;
		this.text=text;
	}
	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public String getText() {
		return text;
	}

}
