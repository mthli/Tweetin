package io.github.mthli.Tweetin.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class PictureUnit {
    public static Bitmap fixBitmap(Context context, Bitmap bitmap) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (bitmapWidth > screenWidth || bitmapHeight > screenHeight) {
            float percentW = ((float) screenWidth) / ((float) bitmapWidth);
            float percentH = ((float) screenHeight) / ((float) bitmapHeight);
            float percent = percentW < percentH ? percentW : percentH;
            Matrix matrix = new Matrix();
            matrix.postScale(percent, percent);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

        }

        return bitmap;
    }

    public static String getPictureName(String pictureURL) {
        String[] array = pictureURL.split("/");

        return array[array.length - 1];
    }

    public static void savePicture(Context context, Bitmap bitmap, String pcitureName) {

    }
}
