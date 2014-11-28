package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Picture.PictureUnit;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {

    private Activity activity;
    private int layoutResId;
    private List<Tweet> tweetList;
    private boolean detail;

    private RequestQueue requestQueue;

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
    }

    private class Holder {
        CircleImageView avatar;
        TextView createdAt;
        TextView name;
        TextView screenName;
        TextView protect;
        ImageView picture;
        TextView text;
        TextView checkIn;
        LinearLayout info;
        TextView infoPicture;
        TextView infoRetweetedByUserName;
        TextView infoFavorite;

        Bitmap bitmap;
    }

    private String getShortCreatedAt(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat(
                activity.getString(R.string.tweet_date_format)
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
        final Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.avatar = (CircleImageView) view.findViewById(R.id.tweet_avatar);
            holder.createdAt = (TextView) view.findViewById(R.id.tweet_created_at);
            holder.name = (TextView) view.findViewById(R.id.tweet_name);
            holder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            holder.protect = (TextView) view.findViewById(R.id.tweet_protect);
            holder.picture = (ImageView) view.findViewById(R.id.tweet_picture);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            holder.info = (LinearLayout) view.findViewById(R.id.tweet_info);
            holder.infoPicture = (TextView) view.findViewById(R.id.tweet_info_picture);
            holder.infoRetweetedByUserName = (TextView) view
                    .findViewById(R.id.tweet_info_retweeted_by_user_name);
            holder.infoFavorite = (TextView) view.findViewById(R.id.tweet_info_favorite);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        final Tweet tweet = tweetList.get(position);

        Glide.with(activity)
                .load(tweet.getAvatarURL())
                .crossFade()
                .into(holder.avatar);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra(
                        activity.getString(R.string.profile_intent_user_id),
                        tweet.getUserId()
                );
                ActivityAnim anim = new ActivityAnim();
                activity.startActivity(intent);
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

        if (detail) {
            if (tweet.getPictureURL() != null) {
                ImageRequest imageRequest = new ImageRequest(
                        tweet.getPictureURL(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                tweet.setBitmap(bitmap);
                                holder.bitmap = bitmap;
                                bitmap = PictureUnit.fixBitmap(activity, bitmap);
                                holder.picture.setImageBitmap(bitmap);
                                holder.picture.setVisibility(View.VISIBLE);
                            }
                        },
                        0,
                        0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                holder.picture.setVisibility(View.GONE);
                            }
                        }
                );
                requestQueue.add(imageRequest);
            } else {
                holder.picture.setVisibility(View.GONE);
            }
        } else {
            holder.picture.setVisibility(View.GONE);
        }
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String[] array = tweet.getPictureURL().split("/");
                    String filename = array[array.length - 1];
                    FileOutputStream originalStream = activity
                            .openFileOutput(filename, Context.MODE_PRIVATE);

                    String[] suffixes = activity.getResources().getStringArray(
                            R.array.detail_picture_suffix
                    );
                    if (tweet.getPictureURL().endsWith(suffixes[0])) {
                        holder.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, originalStream);
                    } else {
                        holder.bitmap.compress(Bitmap.CompressFormat.PNG, 100, originalStream);
                    }
                    originalStream.close();

                    ActivityAnim anim = new ActivityAnim();
                    Intent intent = new Intent(activity, PictureActivity.class);
                    intent.putExtra(
                            activity.getString(R.string.detail_intent_original_bitmap_filename),
                            filename
                    );
                    activity.startActivity(intent);
                    anim.fade(activity);
                } catch (Exception e) {
                    Toast.makeText(
                            activity,
                            R.string.tweet_toast_can_not_open_this_picture,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        holder.picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tweet.getBitmap() != null) {
                    String[] array = tweet.getPictureURL().split("/");
                    String filename = array[array.length - 1];
                    PictureUnit.save(activity, tweet.getBitmap(), filename);
                }
                return true;
            }
        });

        if (detail) {
            holder.text.setAutoLinkMask(Linkify.WEB_URLS);
        }
        holder.text.setText(tweet.getText());

        if (tweet.isRetweet() || tweet.isFavorite() || tweet.getPictureURL() != null) {
            if (tweet.isRetweet()) {
                holder.infoRetweetedByUserName.setText(
                        tweet.getRetweetedByUserName()
                );
                holder.infoRetweetedByUserName.setVisibility(View.VISIBLE);
            } else {
                holder.infoRetweetedByUserName.setVisibility(View.GONE);
            }

            if (tweet.isFavorite()) {
                holder.infoFavorite.setVisibility(View.VISIBLE);
            } else {
                holder.infoFavorite.setVisibility(View.GONE);
            }

            if (tweet.getPictureURL() != null) {
                holder.infoPicture.setVisibility(View.VISIBLE);
            } else {
                holder.infoPicture.setVisibility(View.GONE);
            }

            holder.info.setVisibility(View.VISIBLE);
        } else {
            holder.infoRetweetedByUserName.setVisibility(View.GONE);
            holder.infoFavorite.setVisibility(View.GONE);
            holder.info.setVisibility(View.GONE);
        }

        return view;
    }
}
