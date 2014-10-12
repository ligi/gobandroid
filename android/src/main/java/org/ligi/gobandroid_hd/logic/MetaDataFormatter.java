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
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class MetaDataFormatter {

    private GoGameMetadata meta;
    private GoGame game;

    public MetaDataFormatter(GoGame game) {
        this.meta = game.getMetaData();
        this.game = game;
    }

    public String getBlackPlayerString() {
        StringBuilder res = new StringBuilder();
        res.append(meta.getBlackName());

        if (!meta.getBlackRank().equals(""))
            res.append(" (" + meta.getBlackRank() + ")");

        return res.toString();
    }

    public String getWhitePlayerString() {
        StringBuilder res = new StringBuilder();
        res.append(meta.getWhiteName());

        if (!meta.getWhiteRank().equals(""))
            res.append(" (" + meta.getWhiteRank() + ")");

        return res.toString();
    }

    public String getExtrasString() {
        StringBuilder res = new StringBuilder("Komi: " + game.getKomi());

        if (!meta.getResult().equals(""))
            res.append(" Result: " + meta.getResult());

        return res.toString();
    }
}