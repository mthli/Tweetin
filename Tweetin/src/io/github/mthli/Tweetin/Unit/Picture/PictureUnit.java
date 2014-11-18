package io.github.mthli.Tweetin.Unit.Picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import io.github.mthli.Tweetin.R;

import java.io.File;
import java.io.FileOutputStream;

public class PictureUnit {

    public static Bitmap fixBitmap(Context context, Bitmap bitmap) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth < screenWidth) {
            float percent = ((float) screenWidth) / ((float) bitmapWidth);
            if (bitmapHeight * percent <= 2048) {
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            }
        }

        return bitmap;
    }

    public static void save(Context context, Bitmap bitmap, String filename) {
        File appDir = new File(
                Environment.getExternalStorageDirectory()
                ,
                context.getString(R.string.app_name)
        );
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, filename);
        try {
            String[] suffixes = context.getResources().getStringArray(
                    R.array.detail_picture_suffix
            );
            FileOutputStream outputStream = new FileOutputStream(file);
            if (filename.endsWith(suffixes[0])) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    R.string.cotext_menu_toast_save_failed,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {
            MediaStore.Images.Media.insertImage(
                    context.getContentResolver(),
                    file.getAbsolutePath(),
                    filename,
                    null
            );
        } catch (Exception e) {
            /* Do nothing */
        }
        context.sendBroadcast(
                new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)
                )
        );

        Toast.makeText(
                context,
                R.string.context_menu_toast_save_successful,
                Toast.LENGTH_SHORT
        ).show();
    }
}
