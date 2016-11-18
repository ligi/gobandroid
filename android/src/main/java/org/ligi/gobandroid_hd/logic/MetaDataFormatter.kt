/**
 * gobandroid
 * by Marcus -Ligi- Bueschleb
 * http://ligi.de

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation;

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.

 */

package org.ligi.gobandroid_hd.logic

/**
 * store the metadata ( such as player names/ranks) for a game
 */
class MetaDataFormatter(private val game: GoGame, val meta: GoGameMetadata = game.metaData) {

    fun getBlackPlayerString() = getPlayerString(meta.blackName, meta.blackRank)
    fun getWhitePlayerString() = getPlayerString(meta.whiteName, meta.whiteRank)

    private fun getPlayerString(name: String, rank: String): String {
        val res = StringBuilder()
        res.append(name)

        if (!rank.isEmpty()) {
            res.append(" (").append(rank).append(")")
        }

        return res.toString()
    }

    val extrasString: String
        get() {
            val res = StringBuilder("Komi: " + game.komi)

            if (!meta.result.isEmpty()) {
                res.append(" Result: ").append(meta.result)
            }

            return res.toString()
        }
}