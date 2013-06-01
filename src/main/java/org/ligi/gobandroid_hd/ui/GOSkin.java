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

package org.ligi.gobandroid_hd.ui;

import android.graphics.*;
import org.ligi.tracedroid.logging.Log;

import java.io.File;

public class GOSkin {

    private static boolean do_board_skin = false;
    private static String board_skin_name = "none";

    private static boolean do_stone_skin = false;
    private static String stone_skin_name = "none";

    public final static String skin_base_path = "/sdcard/gobandroid/skins/";

    /**
     * set the current skin for the board if the skin does not exist switch of
     * board skin usage
     *
     * @param skin_name
     * @return success
     */
    public static boolean setBoardSkin(String skin_name) {
        if ((new File(skin_base_path + skin_name).exists())) {
            board_skin_name = skin_name;
            do_board_skin = true;
        } else
            do_board_skin = false;

        return do_board_skin;
    }

    /**
     * set the current skin for stones if the skin does not exist switch of skin
     * usage for stones
     *
     * @param skin_name
     * @return success
     */
    public static boolean setStoneSkin(String skin_name) {
        if ((new File(skin_base_path + skin_name).exists())) {
            stone_skin_name = skin_name;
            do_stone_skin = true;
        } else
            do_stone_skin = false;

        return do_stone_skin;
    }

    private static String getBoardFname() {
        return skin_base_path + board_skin_name + "/board.jpg";
    }

    public static Bitmap getBoard() {
        return BitmapFactory.decodeFile(getBoardFname());
    }

    public static Bitmap getBoard(int width, int height) {
        if (do_board_skin)
            return Bitmap.createScaledBitmap(getBoard(), width, height, true);
        else
            return null;
    }

    public static Bitmap getWhiteStone(float size) {
        return getStone("white", size);
    }

    public static Bitmap getBlackStone(float size) {
        return getStone("black", size);
    }

    /**
     * returns a image for a go stone - either a selected ( by color/size ) and
     * scaled image from the skin or a drawn circle in the right color (
     * fallback / no skin )
     *
     * @param name "white" or "black"
     * @param size the size in pixels
     * @return the scaled image
     */
    public static Bitmap getStone(String name, float size) {

        if (size < 1)
            size = 1;

        if (do_stone_skin)
            try {

                int size_append = 17;

                if (size > 50)
                    size_append = 64;
                else if (size > 23)
                    size_append = 32;

                Log.i("scale to size" + size);
                Bitmap unscaled_bitmap = BitmapFactory.decodeFile(skin_base_path + stone_skin_name + "/" + name + size_append + ".png");
                return Bitmap.createScaledBitmap(unscaled_bitmap, (int) size, (int) size, true);
            } catch (Exception e) {
                Log.w("problem scaling the " + name + " stone bitmap to" + size);
            }

        Bitmap btm = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_4444);

        Canvas c = new Canvas(btm);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (name.equals("white"))
            mPaint.setColor(Color.WHITE);
        else
            mPaint.setColor(Color.BLACK);

        c.drawCircle(c.getWidth() / 2, c.getHeight() / 2, size / 2.0f, mPaint);
        return btm;
    } // getStone

}
