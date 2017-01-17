package fernandez.quentin.todolist.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;

/**
 * Created by quent on 09/01/2017.
 */

public class PictureTools {
    /**
     * Decrypt bitmap from base64 string
     * @param base bitmap in base64 encoded string
     * @return Decrypted bitmap
     */

    public static Bitmap base64ToBitmap(String base) {
        byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Encrypt bitmap in base64 string
     * @param b bitmap you want to encode
     * @return bitmap as base64 string
     */

    public static String bitmapToBase64(Bitmap b) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }
}
