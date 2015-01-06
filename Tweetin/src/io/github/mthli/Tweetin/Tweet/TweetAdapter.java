package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.R;

import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {

    private Activity activity;
    private int layoutResId;
    private List<Tweet> tweetList;

    private boolean detail;

    private RequestQueue requestQueue;

    private String useScreenName;

    public TweetAdapter(
            Activity activity,
            int layoutResId,
            List<Tweet> tweetList,
            boolean detail
    ) {
        super(activity, layoutResId, tweetList);

        this.activity = activity;
        this.layoutResId = layoutResId;
        this.tweetList = tweetList;

        this.detail = detail;

        if (detail) {
            this.requestQueue = Volley.newRequestQueue(activity);
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );
        useScreenName = sharedPreferences.getString(
                activity.getString(R.string.sp_use_screen_name),
                null
        );
    }

    private class Holder {
        CircleImageView avatar;
        TextView name;
        TextView screenName;
        RelativeTimeTextView createdAt;
        TextView checkIn;
        TextView protect;
        ImageView picture;
        TextView text;
        LinearLayout info;
        TextView retweetedBy;
        TextView favorite;

        Bitmap bitmap;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup viewGroup
    ) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            view = layoutInflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();

            holder.avatar = (CircleImageView) view.findViewById(R.id.tweet_avatar);
            holder.name = (TextView) view.findViewById(R.id.tweet_name);
            holder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            holder.createdAt = (RelativeTimeTextView) view.findViewById(R.id.tweet_created_at);
            holder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            holder.protect = (TextView) view.findViewById(R.id.tweet_protect);
            holder.picture = (ImageView) view.findViewById(R.id.tweet_picture);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.info = (LinearLayout) view.findViewById(R.id.tweet_info);
            holder.retweetedBy = (TextView) view.findViewById(R.id.tweet_info_retweeted_by);
            holder.favorite = (TextView) view.findViewById(R.id.tweet_info_favorite);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        Tweet tweet = tweetList.get(position);

        Glide.with(activity)
                .load(tweet.getAvatarURL())
                .crossFade()
                .into(holder.avatar);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });

        holder.name.setText(tweet.getName());

        holder.screenName.setText("@" + tweet.getScreenName());

        holder.createdAt.setReferenceTime(tweet.getCreatedAt());

        if (tweet.getCheckIn() != null) {
            holder.checkIn.setText(tweet.getCheckIn());
            holder.checkIn.setVisibility(View.VISIBLE);
        } else {
            holder.checkIn.setVisibility(View.GONE);
        }

        if (tweet.isProtect()) {
            holder.protect.setVisibility(View.VISIBLE);
        } else {
            holder.protect.setVisibility(View.GONE);
        }

        if (detail) {
            /* Do something */
        } else {
            holder.picture.setVisibility(View.GONE);
        }
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });

        /* Do something */
        holder.text.setText(tweet.getText());

        if (tweet.getRetweetedByName() != null || tweet.isFavorite()) {
            if (tweet.getRetweetedByName() != null) {
                holder.retweetedBy.setText(tweet.getRetweetedByName());
                holder.retweetedBy.setVisibility(View.VISIBLE);
            } else {
                holder.retweetedBy.setVisibility(View.GONE);
            }

            if (tweet.isFavorite()) {
                holder.favorite.setVisibility(View.VISIBLE);
            } else {
                holder.favorite.setVisibility(View.GONE);
            }

            holder.info.setVisibility(View.VISIBLE);
        } else {
            holder.info.setVisibility(View.GONE);
            holder.retweetedBy.setVisibility(View.GONE);
            holder.favorite.setVisibility(View.GONE);
        }

        return view;
    }
}
