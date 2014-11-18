package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.FileInputStream;

public class PictureActivity extends Activity {

    private Bitmap originalBitmap;

    private Bitmap fixBitmap(Bitmap bitmap) {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        Intent intent = getIntent();
        String originalFilename = intent.getStringExtra(
                getString(R.string.detail_intent_original_bitmap_filename)
        );

        try {
            FileInputStream originalStream = this.openFileInput(originalFilename);
            originalBitmap = BitmapFactory.decodeStream(originalStream);

            ImageView picture = (ImageView) findViewById(R.id.picture_view);
            picture.setImageBitmap(fixBitmap(originalBitmap));

            PhotoViewAttacher attacher = new PhotoViewAttacher(picture);
            attacher.setZoomable(true);
            attacher.update();
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    R.string.detail_toast_can_not_open_this_picture,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityAnim anim = new ActivityAnim();
            finish();
            anim.fade(PictureActivity.this);
        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            /* Do nothing */
        }
        else{
            /* Do nothing */
        }
    }
}
