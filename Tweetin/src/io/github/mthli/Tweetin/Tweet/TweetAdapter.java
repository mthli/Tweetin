package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import io.github.mthli.Tweetin.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {
    private Context context;
    private int layoutResId;
    private List<Tweet> tweetList;

    public TweetAdapter(
            Context context,
            int layoutResId,
            List<Tweet> tweetList
    ) {
        super(context, layoutResId, tweetList);

        this.context = context;
        this.layoutResId = layoutResId;
        this.tweetList = tweetList;
    }

    public class Holder {
        ImageView avatar;
        TextView createdAt;
        TextView name;
        TextView screenName;
        TextView text;
        TextView retweetedBy;
    }
    private Holder holder;
    public Holder getHolder() {
        return holder;
    }

    private String getShortCreatedAt(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        Date date = new Date();
        String str = format.format(date);
        String[] arrD = str.split(" ");
        String[] arrC = createdAt.split(" ");
        if (arrD[1].equals(arrC[1])) {
            return arrC[0];
        } else {
            return createdAt;
        }
    }

    @Override
    public View getView(
            final int position,
            final View convertView,
            ViewGroup viewGroup
    ) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.avatar = (ImageView) view.findViewById(R.id.tweet_avatar);
            holder.createdAt = (TextView) view.findViewById(R.id.tweet_created_at);
            holder.name = (TextView) view.findViewById(R.id.tweet_name);
            holder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.retweetedBy = (TextView) view.findViewById(R.id.tweet_retweeted_by);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        Tweet tweet = tweetList.get(position);

        Glide.with(context)
                .load(tweet.getAvatarUrl())
                .crossFade()
                .into(holder.avatar);

        /* Do something */
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Do something */
            }
        });

        holder.createdAt.setText(
                getShortCreatedAt(tweet.getCreatedAt())
        );
        holder.name.setText(tweet.getName());
        holder.screenName.setText(tweet.getScreenName());
        holder.text.setText(tweet.getText());
        if (tweet.isRetweet()) {
            holder.retweetedBy.setText(tweet.getRetweetedBy());
            holder.retweetedBy.setVisibility(View.VISIBLE);
        } else {
            holder.retweetedBy.setVisibility(View.GONE);
        }

        return view;
    }
}
