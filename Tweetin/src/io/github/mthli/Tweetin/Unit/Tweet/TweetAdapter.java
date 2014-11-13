package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {
    private Activity activity;
    private Context context;
    private int layoutResId;
    private List<Tweet> tweetList;
    private boolean detail;

    private RequestQueue requestQueue;

    public TweetAdapter(
            Activity activity,
            Context context,
            int layoutResId,
            List<Tweet> tweetList,
            boolean detail
    ) {
        super(context, layoutResId, tweetList);

        this.activity = activity;
        this.context = context;
        this.layoutResId = layoutResId;
        this.tweetList = tweetList;
        this.detail = detail;

        if (detail) {
            this.requestQueue = Volley.newRequestQueue(activity);
        }
    }

    private class Holder {
        CircleImageView avatar;
        TextView createdAt;
        TextView name;
        TextView screenName;
        TextView protect;
        ImageView photo;
        TextView text;
        TextView checkIn;
        LinearLayout info;
        TextView retweetedByUserName;
        TextView favorite;
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
    private Bitmap fixBitmap(Bitmap bitmap) {
        WindowManager manager = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth < screenWidth) {
            float percent = ((float) screenWidth) / ((float) bitmapWidth);
            if (bitmapHeight * percent <= 2048) {
                Matrix matrix = new Matrix();
                matrix.postScale(percent, percent);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            }
        }

        return bitmap;
    }
    @Override
    public View getView(
            final int position,
            final View convertView,
            ViewGroup viewGroup
    ) {
        final Holder holder;
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
            holder.photo = (ImageView) view.findViewById(R.id.tweet_photo);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            holder.info = (LinearLayout) view.findViewById(R.id.tweet_info);
            holder.retweetedByUserName = (TextView) view
                    .findViewById(R.id.tweet_retweeted_by_user_name);
            holder.favorite = (TextView) view.findViewById(R.id.tweet_favorite);
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
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(
                        context.getString(R.string.profile_intent_user_id),
                        tweet.getUserId()
                );
                ActivityAnim anim = new ActivityAnim();
                context.startActivity(intent);
                anim.rightIn(activity);
            }
        });

        holder.createdAt.setText(
                getShortCreatedAt(tweet.getCreatedAt())
        );
        holder.name.setText(tweet.getName());
        holder.screenName.setText(tweet.getScreenName());
        if (tweet.isProtect()) {
            holder.protect.setVisibility(View.VISIBLE);
        } else {
            holder.protect.setVisibility(View.GONE);
        }
        if (tweet.getCheckIn() != null) {
            holder.checkIn.setText(tweet.getCheckIn());
            holder.checkIn.setVisibility(View.VISIBLE);
        } else {
            holder.checkIn.setVisibility(View.GONE);
        }

        /* Do something */
        if (detail) {
            if (tweet.getPhotoURL() != null) {
                ImageRequest imageRequest = new ImageRequest(
                        tweet.getPhotoURL(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                bitmap = fixBitmap(bitmap);
                                holder.photo.setImageBitmap(bitmap);
                                holder.photo.setVisibility(View.VISIBLE);
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
                holder.photo.setVisibility(View.GONE);
            }
        } else {
            holder.photo.setVisibility(View.GONE);
        }
        if (detail) {
            holder.text.setAutoLinkMask(Linkify.WEB_URLS);
        }
        holder.text.setText(tweet.getText());

        if (tweet.isRetweet() || tweet.isFavorite()) {
            if (tweet.isRetweet()) {
                holder.retweetedByUserName.setText(
                        tweet.getRetweetedByUserName()
                );
                holder.retweetedByUserName.setVisibility(View.VISIBLE);
            } else {
                holder.retweetedByUserName.setVisibility(View.GONE);
            }
            if (tweet.isFavorite()) {
                holder.favorite.setText(
                        context.getString(R.string.tweet_info_favorite)
                );
                holder.favorite.setVisibility(View.VISIBLE);
            } else {
                holder.favorite.setVisibility(View.GONE);
            }
            holder.info.setVisibility(View.VISIBLE);
        } else {
            holder.retweetedByUserName.setVisibility(View.GONE);
            holder.favorite.setVisibility(View.GONE);
            holder.info.setVisibility(View.GONE);
        }

        return view;
    }
}
