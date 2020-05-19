package com.apicloud.labelview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtils {
    /*
     * bitmap转base64
     * */
    public static Object bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = "data:image/jpg;base64," + Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = new byte[0];
        try {
            if (base64Data.startsWith("data:image/")) {
                base64Data = base64Data.substring(base64Data.indexOf(","));
            }
            bytes = Base64.decode(base64Data, Base64.DEFAULT);
        } catch (Exception e) {

        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
