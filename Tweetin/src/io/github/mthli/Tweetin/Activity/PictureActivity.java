package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Picture.PictureUnit;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.FileInputStream;

public class PictureActivity extends Activity {

    private String filename;
    private Bitmap bitmap;
    private boolean fromURL = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        final CircularProgressBar circularProgressBar = (CircularProgressBar)
                findViewById(android.R.id.progress);
        final ImageView pictureView = (ImageView)
                findViewById(R.id.picture_view);

        Intent intent = getIntent();
        final String url = intent.getStringExtra(
                getString(R.string.detail_intent_picture_url)
        );
        if (url != null) {
            fromURL = true;
            circularProgressBar.setVisibility(View.VISIBLE);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(final Bitmap bitmap) {
                            pictureView.setImageBitmap(
                                    PictureUnit.fixBitmap(PictureActivity.this, bitmap)
                            );
                            PhotoViewAttacher attacher = new PhotoViewAttacher(pictureView);
                            attacher.setZoomable(true);
                            String[] array = url.split("/");
                            filename = array[array.length - 1];
                            attacher.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    PictureUnit.save(
                                            PictureActivity.this,
                                            bitmap,
                                            filename
                                    );
                                    return true;
                                }
                            });
                            attacher.update();
                            circularProgressBar.setVisibility(View.GONE);
                            pictureView.setVisibility(View.VISIBLE);
                        }
                    },
                    0,
                    0,
                    Bitmap.Config.ARGB_8888,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            /* Do nothing */
                        }
                    }
            );
            requestQueue.add(imageRequest);
        } else {
            fromURL = false;
            filename = intent.getStringExtra(
                    getString(R.string.detail_intent_original_bitmap_filename)
            );
            try {
                FileInputStream originalStream = openFileInput(filename);
                bitmap = BitmapFactory.decodeStream(originalStream);
                originalStream.close();

                pictureView.setImageBitmap(PictureUnit.fixBitmap(this, bitmap));
                PhotoViewAttacher attacher = new PhotoViewAttacher(pictureView);
                attacher.setZoomable(true);
                attacher.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PictureUnit.save(
                                PictureActivity.this,
                                bitmap,
                                filename
                        );
                        return true;
                    }
                });
                attacher.update();
                pictureView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        R.string.detail_toast_can_not_open_this_picture,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!fromURL) {
                deleteFile(filename);
            }

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
