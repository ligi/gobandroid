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
 */
public class MetaDataFormatter {

    private final GoGameMetadata meta;
    private final GoGame game;

    public MetaDataFormatter(GoGame game) {
        this.meta = game.getMetaData();
        this.game = game;
    }

    public String getBlackPlayerString() {
        return getPlayerString(meta.getBlackName(),  meta.getBlackRank());
    }

    public String getWhitePlayerString() {
        return getPlayerString(meta.getWhiteName(), meta.getWhiteRank());
    }

    private String getPlayerString(String name, String rank) {
        final StringBuilder res = new StringBuilder();
        res.append(name);

        if (!rank.isEmpty()) {
            res.append(" (").append(rank).append(")");
        }

        return res.toString();
    }

    public String getExtrasString() {
        final StringBuilder res = new StringBuilder("Komi: " + game.getKomi());

        if (!meta.getResult().isEmpty()) {
            res.append(" Result: ").append(meta.getResult());
        }

        return res.toString();
    }
}