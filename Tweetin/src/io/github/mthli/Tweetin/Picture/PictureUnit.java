package io.github.mthli.Tweetin.Picture;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

    public static void save(Context context, Bitmap bitmap, String pcitureName) {

    }
}