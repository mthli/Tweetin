package io.github.mthli.Tweetin.Task.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Picture.PictureUnit;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;
import twitter4j.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainBackgroundInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MainActivity mainActivity;
    private Twitter twitter;
    private long useId;

    private SharedPreferences.Editor editor;
    private String filename;
    private String url;
    private RequestQueue requestQueue;

    private ResideMenu resideMenu;

    public MainBackgroundInitTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.filename = null;
        this.url = null;
    }

    @Override
    protected void onPreExecute() {
        SharedPreferences sharedPreferences = mainActivity
                .getSharedPreferences(
                        mainActivity.getString(R.string.sp_name),
                        Context.MODE_PRIVATE
                );
        editor = sharedPreferences.edit();
        filename = sharedPreferences.getString(
                mainActivity.getString(R.string.sp_background_filename),
                null
        );
        requestQueue = Volley.newRequestQueue(mainActivity);
        resideMenu = mainActivity.getResideMenu();
        if (!(filename == null)) {
            try {
                FileInputStream stream = mainActivity.openFileInput(filename);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                ImageView background = (ImageView) resideMenu.findViewById(R.id.iv_background);
                background.setImageBitmap(bitmap);
            } catch (Exception e) {
                /* Do nothing */
            }
            onCancelled();
        }

        twitter = TweetUnit.getTwitterFromSharedPreferences(mainActivity);
        useId = TweetUnit.getUseIdFromeSharedPreferences(mainActivity);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            User user = twitter.showUser(useId);
            url = user.getProfileBackgroundImageURL();
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            try {
                                String[] array = url.split("/");
                                filename = array[array.length - 1];
                                // bitmap = PictureUnit.fixBitmap(mainActivity, bitmap);

                                FileOutputStream originalStream = mainActivity
                                        .openFileOutput(filename, Context.MODE_PRIVATE);
                                String[] suffixes = mainActivity.getResources().getStringArray(
                                        R.array.detail_picture_suffix
                                );
                                if (url.endsWith(suffixes[0])) {
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, originalStream);
                                } else {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, originalStream);
                                }
                                originalStream.close();

                                editor.putString(
                                        mainActivity.getString(R.string.sp_background_filename),
                                        filename
                                ).commit();

                                ImageView background = (ImageView) resideMenu.findViewById(R.id.iv_background);
                                background.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                /* Do nothing */
                            }
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
        }
    }
}
