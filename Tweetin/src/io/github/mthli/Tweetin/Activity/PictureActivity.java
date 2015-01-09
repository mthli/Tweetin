package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;
import io.github.mthli.Tweetin.R;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.FileInputStream;

public class PictureActivity extends Activity {
    private String pictureName;

    private void initUI() {
        ImageView pictureView = (ImageView) findViewById(R.id.picture_view);

        pictureName = getIntent().getStringExtra(getString(R.string.picture_intent_picture_name));
        try {
            FileInputStream fileInputStream = openFileInput(pictureName);

            pictureView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));

            fileInputStream.close();

            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(pictureView);
            photoViewAttacher.setZoomable(true);
            photoViewAttacher.update();
        } catch (Exception e) {
            Toast.makeText(this, R.string.picture_toast_can_not_open_this_picture, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        initUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deleteFile(pictureName);

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        return true;
    }
}
