package io.github.mthli.Tweetin.Tweet.Base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;

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

    private class Holder {
        CircleImageView avatar;
        TextView createdAt;
        TextView name;
        TextView screenName;
        TextView protect;
        TextView text;
        TextView checkIn;
        TextView retweetedByUserName;
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
        Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.avatar = (CircleImageView) view.findViewById(R.id.tweet_avatar);
            holder.createdAt = (TextView) view.findViewById(R.id.tweet_created_at);
            holder.name = (TextView) view.findViewById(R.id.tweet_name);
            holder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            holder.protect = (TextView) view.findViewById(R.id.tweet_protect);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            holder.retweetedByUserName = (TextView) view.findViewById(R.id.tweet_retweeted_by_user_name);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        final Tweet tweet = tweetList.get(position);

        Glide.with(context)
                .load(tweet.getAvatarURL())
                .crossFade()
                .into(holder.avatar);
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
        if (tweet.isProtect()) {
            holder.protect.setVisibility(View.VISIBLE);
        }
        holder.text.setText(tweet.getText());
        if (tweet.getCheckIn() != null) {
            holder.checkIn.setText(tweet.getCheckIn());
            holder.checkIn.setVisibility(View.VISIBLE);
        }
        if (tweet.isRetweet()) {
            holder.retweetedByUserName.setText(tweet.getRetweetedByUserName());
            holder.retweetedByUserName.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
