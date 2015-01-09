package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;
import io.github.mthli.Tweetin.Picture.PictureUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.FileInputStream;

public class PictureActivity extends Activity {
    private Bitmap bitmap;
    private String pictureName;

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.picture_toolbar);
        ((LinearLayout.LayoutParams) toolbar.getLayoutParams()).setMargins(0, ViewUnit.getStatusBarHeight(this), 0, 0);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.picture_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView pictureView = (ImageView) findViewById(R.id.picture_view);

        pictureName = getIntent().getStringExtra(getString(R.string.picture_intent_picture_name));
        try {
            FileInputStream fileInputStream = openFileInput(pictureName);
            bitmap = BitmapFactory.decodeStream(fileInputStream);

            pictureView.setImageBitmap(bitmap);

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

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.picture);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                deleteFile(pictureName);

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                break;
            case R.id.picture_menu_save:
                PictureUnit.savePicture(this, bitmap, pictureName);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
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
