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
 * class to store metadata for game - e.g. data from SGF header
 * 
 * @author ligi
 *
 */
public class GoGameMetadata {

	// the game name (GN)
	private String name="";
	
	// the result of the game
	private String result="";
	
	private String black_name="";
	private String black_rank="";
	private String white_name="";
	private String white_rank="";
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setWhiteRank(String white_rank) {
		this.white_rank = white_rank;
	}
	public String getWhiteRank() {
		return white_rank;
	}
	public void setWhiteName(String white_name) {
		this.white_name = white_name;
	}
	public String getWhiteName() {
		return white_name;
	}
	public void setBlackName(String black_name) {
		this.black_name = black_name;
	}
	public String getBlackName() {
		return black_name;
	}
	public void setBlackRank(String black_rank) {
		this.black_rank = black_rank;
	}
	public String getBlackRank() {
		return black_rank;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
}
