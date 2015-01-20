package io.github.mthli.Tweetin.Fragment.Picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PictureFragment extends Fragment {
    private ProgressWheel progressWheel;
    private TextView reloadView;
    private ImageView pictureView;

    private TextView descriptionView;
    public TextView getDescriptionView() {
        return descriptionView;
    }

    private String pictureURL = null;
    private RequestQueue requestQueue;

    private Bitmap originBitmap = null;
    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    private int taskStatus = FlagUnit.TASK_IDLE;
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_fragment, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressWheel = (ProgressWheel) getView().findViewById(R.id.picture_fragment_progress_bar);
        progressWheel.setVisibility(View.VISIBLE);

        reloadView = (TextView) getView().findViewById(R.id.picture_fragment_reload);
        reloadView.setVisibility(View.GONE);

        pictureView = (ImageView) getView().findViewById(R.id.picture_fragment_picture);
        pictureView.setVisibility(View.GONE);

        descriptionView = (TextView) getView().findViewById(R.id.picture_fragment_description);
        descriptionView.getBackground().setAlpha(153);
        descriptionView.setVisibility(View.VISIBLE);

        Intent intent = getActivity().getIntent();
        pictureURL = intent.getStringExtra(getString(R.string.picture_intent_picture_url));
        if (pictureURL == null) {
            pictureURL = getString(R.string.picture_default_picture_url);
        }
        String description = intent.getStringExtra(getString(R.string.picture_intent_description));
        if (description == null) {
            description = getString(R.string.picture_default_desciption);
        }

        reloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
        reloadView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), R.string.picture_toast_reload, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        descriptionView.setText((new TweetUnit(getActivity())).getSpanFromText(description));

        requestQueue = Volley.newRequestQueue(getActivity());

        load();
    }

    private void load() {
        progressWheel.setVisibility(View.VISIBLE);
        reloadView.setVisibility(View.GONE);
        pictureView.setVisibility(View.GONE);

        ImageRequest imageRequest = new ImageRequest(
                pictureURL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        originBitmap = bitmap;

                        pictureView.setImageBitmap(bitmap);
                        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(pictureView);
                        photoViewAttacher.setZoomable(true);
                        photoViewAttacher.update();

                        progressWheel.setVisibility(View.GONE);
                        reloadView.setVisibility(View.GONE);
                        pictureView.setVisibility(View.VISIBLE);
                    }
                },
                0,
                0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressWheel.setVisibility(View.GONE);
                        reloadView.setVisibility(View.VISIBLE);
                        pictureView.setVisibility(View.GONE);

                        Toast.makeText(getActivity(), R.string.tweet_toast_get_picture_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(imageRequest);
    }

    public void cancelAllTasks() {
        requestQueue.stop();
    }
}
