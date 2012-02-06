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
 * store the metadata ( such as player names/ranks) for a game 
 * 
 * @author Marcus -ligi- Bueschleb
 *
 */
public class MetaDataFormater {

	private GoGameMetadata meta;
	
	public MetaDataFormater(GoGameMetadata meta) {
		this.meta=meta;
	}
	
	
	public String getBlackPlayerString() {
		StringBuilder res=new StringBuilder();
		res.append(meta.getBlackName());
		
		if (!meta.getBlackRank().equals(""))
			res.append(" (" + meta.getBlackRank() + ")");
		
		return res.toString();
	}
	
	public String getWhitePlayerString() {
		StringBuilder res=new StringBuilder();
		res.append(meta.getWhiteName());
		
		if (!meta.getWhiteRank().equals(""))
			res.append(" (" + meta.getWhiteRank() + ")");
		
		return res.toString();
	}
}