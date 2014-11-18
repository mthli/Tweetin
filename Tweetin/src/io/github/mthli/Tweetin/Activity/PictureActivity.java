package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.Picture.PictureUnit;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends Activity {

    private Bitmap bitmap;
    private String filename;

    private AlertDialog alertDialog;
    private void showPictureLongClickDialog() {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
                .inflate(
                        R.layout.context_menu,
                        null
                );
        ListView menu = (ListView) linearLayout.findViewById(R.id.context_menu_listview);
        List<String> menuItemList = new ArrayList<String>();

        menuItemList.add(
                getString(R.string.context_menu_item_save)
        );

        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(
                this,
                R.layout.context_menu_item,
                menuItemList
        );
        menu.setAdapter(contextMenuAdapter);
        contextMenuAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        PictureUnit.save(
                                PictureActivity.this,
                                bitmap,
                                filename
                        );
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        Intent intent = getIntent();
        filename = intent.getStringExtra(
                getString(R.string.detail_intent_original_bitmap_filename)
        );

        ImageView picture = (ImageView) findViewById(R.id.picture_view);
        try {
            FileInputStream originalStream = openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(originalStream);
            originalStream.close();

            picture.setImageBitmap(PictureUnit.fixBitmap(this, bitmap));
            PhotoViewAttacher attacher = new PhotoViewAttacher(picture);
            attacher.setZoomable(true);
            attacher.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPictureLongClickDialog();
                    return true;
                }
            });
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
            deleteFile(filename);

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
