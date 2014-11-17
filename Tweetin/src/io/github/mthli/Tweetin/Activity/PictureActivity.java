package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import uk.co.senab.photoview.PhotoView;

public class PictureActivity extends Activity {

    private Bitmap originalBitmap;
    private Bitmap fixBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        byte[] originalByte = getIntent().getByteArrayExtra(
                getString(R.string.detail_intent_original_byte)
        );
        byte[] fixByte = getIntent().getByteArrayExtra(
                getString(R.string.detail_intent_fix_byte)
        );
        originalBitmap = BitmapFactory.decodeByteArray(originalByte, 0, originalByte.length);
        fixBitmap = BitmapFactory.decodeByteArray(fixByte, 0, fixByte.length);

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageBitmap(fixBitmap);

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
