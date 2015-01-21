package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Dialog.DialogUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Tweet.DeleteTask;
import io.github.mthli.Tweetin.Task.Tweet.FavoriteTask;
import io.github.mthli.Tweetin.Task.Tweet.RemoveTask;
import io.github.mthli.Tweetin.Task.Tweet.RetweetTask;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;

import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {
    private ListFragment listFragment;
    private Activity activity;
    private int layoutResId;
    private List<Tweet> tweetList;

    private String useScreenName;

    private TweetUnit tweetUnit;

    public TweetAdapter(ListFragment listFragment, int layoutResId, List<Tweet> tweetList) {
        super(listFragment.getActivity(), layoutResId, tweetList);

        this.listFragment = listFragment;
        this.activity = listFragment.getActivity();
        this.layoutResId = layoutResId;
        this.tweetList = tweetList;

        this.useScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(activity);

        this.tweetUnit = new TweetUnit(activity);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final TweetHolder tweetHolder;

        if (view == null) {
            view = LayoutInflater.from(activity).inflate(layoutResId, viewGroup, false);

            tweetHolder = new TweetHolder();

            tweetHolder.avatar = (CircleImageView) view.findViewById(R.id.tweet_avatar);
            tweetHolder.name = (TextView) view.findViewById(R.id.tweet_name);
            tweetHolder.screenName = (TextView) view.findViewById(R.id.tweet_screen_name);
            tweetHolder.createdAt = (RelativeTimeTextView) view.findViewById(R.id.tweet_created_at);
            tweetHolder.checkIn = (TextView) view.findViewById(R.id.tweet_check_in);
            tweetHolder.protect = (TextView) view.findViewById(R.id.tweet_protect);
            tweetHolder.text = (TextView) view.findViewById(R.id.tweet_text);
            tweetHolder.info = (LinearLayout) view.findViewById(R.id.tweet_info);
            tweetHolder.retweetedBy = (TextView) view.findViewById(R.id.tweet_info_retweeted_by);
            tweetHolder.favorite = (TextView) view.findViewById(R.id.tweet_info_favorite);

            tweetHolder.action = (LinearLayout) view.findViewById(R.id.tweet_action);
            tweetHolder.replyButton = (ImageButton) view.findViewById(R.id.tweet_action_reply);
            tweetHolder.quoteButton = (ImageButton) view.findViewById(R.id.tweet_action_quote);
            tweetHolder.retweetButton = (ImageButton) view.findViewById(R.id.tweet_action_retweet);
            tweetHolder.favoriteButton = (ImageButton) view.findViewById(R.id.tweet_action_favorite);
            tweetHolder.deleteButton = (ImageButton) view.findViewById(R.id.tweet_action_delete);
            tweetHolder.pictureButton = (ImageButton) view.findViewById(R.id.tweet_action_picture);
            tweetHolder.moreButton = (ImageButton) view.findViewById(R.id.tweet_action_more);

            view.setTag(tweetHolder);
        } else {
            tweetHolder = (TweetHolder) view.getTag();
        }

        final Tweet tweet = tweetList.get(position);

        Glide.with(activity).load(tweet.getAvatarURL()).crossFade().into(tweetHolder.avatar);

        tweetHolder.name.setText(tweet.getName());

        tweetHolder.screenName.setText("@" + tweet.getScreenName());

        tweetHolder.createdAt.setReferenceTime(tweet.getCreatedAt());

        if (tweet.getCheckIn() != null) {
            tweetHolder.checkIn.setText(tweet.getCheckIn());
            tweetHolder.checkIn.setVisibility(View.VISIBLE);
        } else {
            tweetHolder.checkIn.setVisibility(View.GONE);
        }

        if (tweet.isProtect()) {
            tweetHolder.protect.setVisibility(View.VISIBLE);
        } else {
            tweetHolder.protect.setVisibility(View.GONE);
        }

        if (tweet.getRetweetedByName() != null || tweet.isFavorite()) {
            if (tweet.getRetweetedByName() != null) {
                tweetHolder.retweetedBy.setText(tweet.getRetweetedByName());
                tweetHolder.retweetedBy.setVisibility(View.VISIBLE);
            } else {
                tweetHolder.retweetedBy.setVisibility(View.GONE);
            }

            if (tweet.isFavorite()) {
                tweetHolder.favorite.setVisibility(View.VISIBLE);
            } else {
                tweetHolder.favorite.setVisibility(View.GONE);
            }

            tweetHolder.info.setVisibility(View.VISIBLE);
        } else {
            tweetHolder.info.setVisibility(View.GONE);
            tweetHolder.retweetedBy.setVisibility(View.GONE);
            tweetHolder.favorite.setVisibility(View.GONE);
        }

        if (tweet.isDetail()) {
            tweetHolder.text.setMovementMethod(LinkMovementMethod.getInstance());
            tweetHolder.text.setText(tweetUnit.getSpanFromTweet(tweet));

            if (tweet.isProtect() || tweet.getScreenName().equals(useScreenName)) {
                tweetHolder.retweetButton.setVisibility(View.GONE);
            } else if (tweet.getRetweetedByScreenName() != null && tweet.getRetweetedByScreenName().equals(useScreenName)) {
                tweetHolder.retweetButton.setImageResource(R.drawable.ic_action_retweet_active);
                tweetHolder.retweetButton.setVisibility(View.VISIBLE);
            } else {
                tweetHolder.retweetButton.setImageResource(R.drawable.ic_action_retweet_default);
                tweetHolder.retweetButton.setVisibility(View.VISIBLE);
            }

            if (tweet.isFavorite()) {
                tweetHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_active);
            } else {
                tweetHolder.favoriteButton.setImageResource(R.drawable.ic_action_favorite_default);
            }

            if (tweet.getScreenName().equals(useScreenName)) {
                tweetHolder.deleteButton.setVisibility(View.VISIBLE);
            } else {
                tweetHolder.deleteButton.setVisibility(View.GONE);
            }

            if (tweet.getPictureURL() != null) {
                tweetHolder.pictureButton.setVisibility(View.VISIBLE);
            } else {
                tweetHolder.pictureButton.setVisibility(View.GONE);
            }

            tweetHolder.action.setVisibility(View.VISIBLE);
        } else {
            tweetHolder.text.setMovementMethod(null);
            tweetHolder.text.setText(tweet.getText());

            tweetHolder.action.setVisibility(View.GONE);
        }

        tweetHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFragment.showProfile(tweet.getScreenName());
            }
        });
        tweetHolder.avatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, tweet.getName() + " @" + tweet.getScreenName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(activity.getString(R.string.post_intent_post_flag), FlagUnit.POST_REPLY);
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_status_id), tweet.getStatusId());
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_screen_name), tweet.getScreenName());
                activity.startActivity(intent);
            }
        });
        tweetHolder.replyButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_reply, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.quoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(activity.getString(R.string.post_intent_post_flag), FlagUnit.POST_QUOTE);
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_status_id), tweet.getStatusId());
                intent.putExtra(activity.getString(R.string.post_intent_in_reply_to_screen_name), tweet.getScreenName());
                intent.putExtra(activity.getString(R.string.post_intent_text), tweet.getText());
                activity.startActivity(intent);
            }
        });
        tweetHolder.quoteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String screenName = tweet.getRetweetedByScreenName();
                if (screenName != null && screenName.equals(TwitterUnit.getUseScreenNameFromSharedPreferences(activity))) {
                    Toast.makeText(activity, R.string.tweet_toast_already_retweeted, Toast.LENGTH_SHORT).show();
                    return;
                }

                (new RetweetTask(activity, TweetAdapter.this, tweetList, position)).execute();
            }
        });
        tweetHolder.retweetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_retweet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tweet.isFavorite()) {
                    (new FavoriteTask(activity, TweetAdapter.this, tweetList, position)).execute();
                } else {
                    (new RemoveTask(activity, TweetAdapter.this, tweetList, position)).execute();
                }
            }
        });
        tweetHolder.favoriteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_favorite, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new DeleteTask(activity, TweetAdapter.this, tweetList, position)).execute();
            }
        });
        tweetHolder.deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_delete, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(tweetUnit.getIntentFromTweet(tweet, PictureActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        tweetHolder.pictureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, activity.getString(R.string.tweet_toast_picture), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tweetHolder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUnit.show(activity, tweet);
            }
        });
        tweetHolder.moreButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.tweet_toast_more, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return view;
    }
}
