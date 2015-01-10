package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Dialog.DialogUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Picture.PictureUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;

import java.io.FileOutputStream;
import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {
    private Activity activity;
    private int layoutResId;
    private List<Tweet> tweetList;

    private RequestQueue requestQueue;

    private String useScreenName;

    private TweetUnit tweetUnit;

    public TweetAdapter(
            Activity activity,
            int layoutResId,
            List<Tweet> tweetList
    ) {
        super(activity, layoutResId, tweetList);

        this.activity = activity;
        this.layoutResId = layoutResId;
        this.tweetList = tweetList;

        this.requestQueue = Volley.newRequestQueue(activity);

        this.useScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(activity);

        this.tweetUnit = new TweetUnit(activity);
    }

    @Override
    public View getView(
            final int position,
            View convertView,
            ViewGroup viewGroup
    ) {
        View view = convertView;

        final Holder holder;

        if (view == null) {
            view = activity.getLayoutInflater().inflate(layoutResId, viewGroup, false);

            holder = new Holder();

            holder.avatar = (CircleImageView) view.findViewById(R.id.tweet_avatar);
            holder.name = (TextView) view.findViewById(R.id.tweet_name);
            holder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            holder.createdAt = (RelativeTimeTextView) view.findViewById(R.id.tweet_created_at);
            holder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            holder.protect = (TextView) view.findViewById(R.id.tweet_protect);
            holder.loading = (ProgressBar) view.findViewById(R.id.tweet_loading);
            holder.picture = (ImageView) view.findViewById(R.id.tweet_picture);
            holder.text = (TextView) view.findViewById(R.id.tweet_text);
            holder.info = (LinearLayout) view.findViewById(R.id.tweet_info);
            holder.retweetedBy = (TextView) view.findViewById(R.id.tweet_info_retweeted_by);
            holder.favorite = (TextView) view.findViewById(R.id.tweet_info_favorite);

            holder.action = (LinearLayout) view.findViewById(R.id.tweet_action);
            holder.replyButton = (ImageButton) view.findViewById(R.id.tweet_action_reply);
            holder.quoteButton = (ImageButton) view.findViewById(R.id.tweet_action_quote);
            holder.retweetButton = (ImageButton) view.findViewById(R.id.tweet_action_retweet);
            holder.favoriteButton = (ImageButton) view.findViewById(R.id.tweet_action_favorite);
            holder.deleteButton = (ImageButton) view.findViewById(R.id.tweet_action_delete);
            holder.moreButton = (ImageButton) view.findViewById(R.id.tweet_action_more);

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

        if (!tweet.isDetail()) {
            holder.loading.setVisibility(View.GONE);
            holder.picture.setVisibility(View.GONE);

            holder.text.setText(tweet.getText());

            holder.action.setVisibility(View.GONE);
        } else {
            if (tweet.getPictureURL() != null) {
                holder.loading.setVisibility(View.VISIBLE);

                ImageRequest imageRequest = new ImageRequest(
                        tweet.getPictureURL(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                if (!tweet.isDetail()) {
                                    return;
                                }

                                holder.bitmap = bitmap; //

                                holder.picture.setImageBitmap(PictureUnit.fixBitmap(activity, bitmap));

                                holder.loading.setVisibility(View.GONE);

                                holder.picture.setVisibility(View.VISIBLE);
                                holder.picture.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
                            }
                        },
                        0,
                        0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                holder.loading.setVisibility(View.GONE);
                                holder.picture.setVisibility(View.GONE);

                                if (tweet.isDetail()) {
                                    Toast.makeText(activity, R.string.tweet_toast_get_picture_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                requestQueue.add(imageRequest);
            } else {
                holder.loading.setVisibility(View.GONE);
                holder.picture.setVisibility(View.GONE);
            }

            holder.text.setText(tweetUnit.getSpanFromTweet(tweet));

            if (tweet.isProtect() || tweet.getScreenName().equals(useScreenName)) {
                holder.retweetButton.setVisibility(View.GONE);
            } else if (tweet.getRetweetedByScreenName() != null && tweet.getRetweetedByScreenName().equals(useScreenName)) {
                holder.retweetButton.setImageResource(R.drawable.ic_action_retweet_active);
                holder.retweetButton.setVisibility(View.VISIBLE);
            } else {
                holder.retweetButton.setImageResource(R.drawable.ic_action_retweet_default);
                holder.retweetButton.setVisibility(View.VISIBLE);
            }

            if (tweet.isFavorite()) {
                holder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_active);
            } else {
                holder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_default);
            }

            if (tweet.getScreenName().equals(useScreenName)) {
                holder.deleteButton.setVisibility(View.VISIBLE);
            } else {
                holder.deleteButton.setVisibility(View.GONE);
            }

            holder.action.setVisibility(View.VISIBLE);
            holder.action.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
        }

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String pictureName = PictureUnit.getPictureName(tweet.getPictureURL());

                    FileOutputStream fileOutputStream = activity.openFileOutput(pictureName, Context.MODE_PRIVATE);
                    String[] suffixes = activity.getResources().getStringArray(R.array.picture_suffixes);
                    if (tweet.getPictureURL().endsWith(suffixes[0])) {
                        holder.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    } else {
                        holder.bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    }
                    fileOutputStream.close();

                    Intent intent = new Intent(activity, PictureActivity.class);
                    intent.putExtra(
                            activity.getString(R.string.picture_intent_picture_name),
                            pictureName
                    );
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } catch (Exception e) {
                    Toast.makeText(activity, R.string.picture_toast_can_not_open_this_picture, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(activity.getString(R.string.post_intent_post_flag), FlagUnit.POST_REPLY);
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_status_id), tweet.getStatusId());
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_screen_name), tweet.getScreenName());
                activity.startActivity(intent);
            }
        });
        holder.replyButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_reply, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        holder.quoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(activity.getString(R.string.post_intent_post_flag), FlagUnit.POST_QUOTE);
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_status_id), tweet.getStatusId());
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_screen_name), tweet.getScreenName());
                intent.putExtra(activity.getString(R.string.post_intent_quote), tweet.getText());
                activity.startActivity(intent);
            }
        });
        holder.quoteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_quote, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        holder.retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });
        holder.retweetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_retweet, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });
        holder.favoriteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_favorite, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });
        holder.deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_delete, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUnit.showContextDialog(activity, holder, tweet);
            }
        });
        holder.moreButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_more, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        return view;
    }
}
