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
 * @author <a href="http://ligi.de">Marcus -LiGi- Bueschleb </a>
 *         <p/>
 *         This software is licenced with GPLv3
 */
public class GoGameMetadata {

    private String name = "";
    private String result = "";

    private String source = "";
    private String filename = "";

    private String black_name = "";
    private String black_rank = "";
    private String white_name = "";
    private String white_rank = "";

    private String difficulty = "";

    private String date = "";

    /**
     * set the game name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the game name
     *
     * @return the game name
     */
    public String getName() {
        return name;
    }

    /**
     * set the white rank
     *
     * @param white_rank - rank of white
     */
    public void setWhiteRank(String white_rank) {
        this.white_rank = white_rank;
    }

    /**
     * get whites rank
     *
     * @return whites rank
     */
    public String getWhiteRank() {
        return white_rank;
    }

    /**
     * set the name of white
     *
     * @param white_name
     */
    public void setWhiteName(String white_name) {
        this.white_name = white_name;
    }

    /**
     * get whites name
     *
     * @return whites name
     */
    public String getWhiteName() {
        return white_name;
    }

    /**
     * set blacks name
     *
     * @param black_name the name of black
     */
    public void setBlackName(String black_name) {
        this.black_name = black_name;
    }

    /**
     * get blacks name
     *
     * @return the name of black
     */
    public String getBlackName() {
        return black_name;
    }

    /**
     * set blacks rank
     *
     * @param black_rank the rank of black
     */
    public void setBlackRank(String black_rank) {
        this.black_rank = black_rank;
    }

    /**
     * get the rank of black
     *
     * @return blacks rank
     */
    public String getBlackRank() {
        return black_rank;
    }

    /**
     * set the result of the game
     *
     * @param result the games result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * get the result of the game
     *
     * @return
     */
    public String getResult() {
        return result;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * if the game is from a SGF we have the filename here ( often a lot of
     * information encoded here )
     *
     * @return
     */
    public String getFileName() {
        return filename;
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}