package io.github.mthli.Tweetin.Picture;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
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

    public static String getPictureName(Context context, String pictureURL) {
        String[] suffixes = context.getResources().getStringArray(R.array.picture_suffixes);

        String[] array = pictureURL.split("/");
        String pictureName = array[array.length - 1];

        for (String suffix : suffixes) {
            if (pictureName.endsWith(suffix)) {
                return pictureName;
            }
        }

        return pictureName + suffixes[0];
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;

        String column = "_data";

        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(column));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public static String getPicturePath(Context context, Uri pictureUri) {
        if (DocumentsContract.isDocumentUri(context, pictureUri)) {
            if (isExternalStorageDocument(pictureUri)) {
                String[] split = DocumentsContract.getDocumentId(pictureUri).split(":");
                
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(pictureUri)) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), 
                        Long.valueOf(DocumentsContract.getDocumentId(pictureUri))
                );
                
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(pictureUri)) {
                String[] split = DocumentsContract.getDocumentId(pictureUri).split(":");
                
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                
                String[] selectionArgs = new String[] {split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(pictureUri.getScheme())) {
            return getDataColumn(context, pictureUri, null, null);
        } else if ("file".equalsIgnoreCase(pictureUri.getScheme())) {
            return pictureUri.getPath();
        }

        return null;
    }

    public static void save(Context context, Bitmap bitmap, String pictureURL) {
        if (bitmap == null) {
            Toast.makeText(context, R.string.picture_toast_save_picture_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        String pictureName = getPictureName(context, pictureURL);

        File appDir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, pictureName);
        try {
            String[] suffixes = context.getResources().getStringArray(R.array.picture_suffixes);
            FileOutputStream outputStream = new FileOutputStream(file);
            if (pictureName.endsWith(suffixes[0])) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, R.string.picture_toast_save_picture_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        Toast.makeText(context, R.string.picture_toast_save_picture_successful, Toast.LENGTH_SHORT).show();
    }
}