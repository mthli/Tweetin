package io.github.mthli.Tweetin.Tweet.Mention;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Database.Mention.MentionAction;
import io.github.mthli.Tweetin.Database.Mention.MentionData;
import io.github.mthli.Tweetin.Mention.MentionActivity;
import io.github.mthli.Tweetin.Mention.MentionFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MentionInitTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private long useId;

    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout srl;
    private boolean isFirstMention;
    private boolean isPullToRefresh = false;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private List<MentionData> mentionDataList = new ArrayList<MentionData>();

    public MentionInitTask(
            MentionFragment mentionFragment,
            boolean isPullToRefresh
    ) {
        this.mentionFragment = mentionFragment;
        this.useId = 0;
        this.isFirstMention = false;
        this.isPullToRefresh = isPullToRefresh;
    }

    @Override
    protected void onPreExecute() {
        context = mentionFragment.getContentView().getContext();
        useId = mentionFragment.getUseId();

        srl = mentionFragment.getSrl();
        twitter = ((MentionActivity) mentionFragment.getActivity()).getTwitter();
        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();

        if (mentionFragment.getRefreshFlag() == Flag.MENTION_TASK_ALIVE) {
            onCancelled();
        } else {
            mentionFragment.setRefreshFlag(Flag.MENTION_TASK_ALIVE);
        }

        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        editor = preferences.edit();

        if (preferences.getLong(context.getString(R.string.sp_latest_mention_id), 0) == 0) {
            isFirstMention = true;
            mentionFragment.setContentShown(false);
        } else {
            isFirstMention = false;
            if (!srl.isRefreshing()) {
                srl.setRefreshing(true);
            }
        }

        if (!isPullToRefresh) {
            MentionAction action = new MentionAction(context);
            action.opewDatabase(false);
            mentionDataList = action.getMentionDataList();
            action.closeDatabase();
            tweetList.clear();
            for (MentionData data : mentionDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setProtect(data.isProtected());
                tweet.setText(data.getText());
                tweet.setCheckIn(data.getCheckIn());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedByName(data.getRetweetedByName());
                tweet.setRetweetedById(data.getRetweetedById());
                tweet.setReplyTo(data.getReplyTo());
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
    }

    private twitter4j.Status mention;
    @Override
    protected Boolean doInBackground(Void... params) {
        MentionAction action = new MentionAction(context);
        action.opewDatabase(true);

        List<twitter4j.Status> statusList;
        try {
            Paging paging = new Paging(1, 20);
            statusList = twitter.getMentionsTimeline(paging);
            mention = statusList.get(0);
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        action.deleteAll();
        mentionDataList.clear();
        SimpleDateFormat format = new SimpleDateFormat(
                context.getString(R.string.tweet_date_format)
        );
        for (twitter4j.Status status : statusList) {
            MentionData data = new MentionData();
            if (status.isRetweet()) {
                data.setTweetId(status.getId());
                data.setUserId(status.getRetweetedStatus().getUser().getId());
                data.setAvatarUrl(
                        status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                );
                data.setCreatedAt(
                        format.format(status.getRetweetedStatus().getCreatedAt())
                );
                data.setName(status.getRetweetedStatus().getUser().getName());
                data.setScreenName(
                        "@" + status.getRetweetedStatus().getUser().getScreenName()
                );
                data.setProtect(status.getRetweetedStatus().getUser().isProtected());
                data.setText(status.getRetweetedStatus().getText());
                Place place = status.getRetweetedStatus().getPlace();
                if (place != null) {
                    data.setCheckIn(place.getFullName());
                } else {
                    data.setCheckIn(null);
                }
                data.setRetweet(true);
                data.setRetweetedByName(status.getUser().getName());
                data.setRetweetedById(status.getUser().getId());
                data.setReplyTo(status.getRetweetedStatus().getInReplyToStatusId());
            } else {
                data.setTweetId(status.getId());
                data.setUserId(status.getUser().getId());
                data.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                data.setCreatedAt(format.format(status.getCreatedAt()));
                data.setName(status.getUser().getName());
                data.setScreenName("@" + status.getUser().getScreenName());
                data.setProtect(status.getUser().isProtected());
                data.setText(status.getText());
                Place place = status.getPlace();
                if (place != null) {
                    data.setCheckIn(place.getFullName());
                } else {
                    data.setCheckIn(null);
                }
                data.setRetweet(false);
                data.setRetweetedByName(null);
                data.setRetweetedById(0);
                data.setReplyTo(status.getInReplyToStatusId());
            }
            if (status.isRetweetedByMe() || status.isRetweeted()) {
                data.setRetweet(true);
                data.setRetweetedByName(context.getString(R.string.tweet_retweeted_by_me));
                data.setRetweetedById(useId);
            }
            action.addTweet(data);
            mentionDataList.add(data);
        }
        action.closeDatabase();

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
            tweetList.clear();
            for (MentionData data : mentionDataList) {
                Tweet tweet = new Tweet();
                tweet.setTweetId(data.getTweetId());
                tweet.setUserId(data.getUserId());
                tweet.setAvatarUrl(data.getAvatarUrl());
                tweet.setCreatedAt(data.getCreatedAt());
                tweet.setName(data.getName());
                tweet.setScreenName(data.getScreenName());
                tweet.setProtect(data.isProtected());
                tweet.setText(data.getText());
                tweet.setCheckIn(data.getCheckIn());
                tweet.setRetweet(data.isRetweet());
                tweet.setRetweetedByName(data.getRetweetedByName());
                tweet.setRetweetedById(data.getRetweetedById());
                tweet.setReplyTo(data.getReplyTo());
                tweetList.add(tweet);
            }

            editor.putLong(
                    context.getString(R.string.sp_latest_mention_id),
                    mention.getId()
            ).commit();

            if (isFirstMention) {
                mentionFragment.setContentEmpty(false);
                tweetAdapter.notifyDataSetChanged();
                mentionFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
                tweetAdapter.notifyDataSetChanged();
            }
        } else {
            if (isFirstMention) {
                mentionFragment.setContentEmpty(true);
                mentionFragment.setEmptyText(R.string.mention_get_mention_failed);
                mentionFragment.setContentShown(true);
            } else {
                srl.setRefreshing(false);
            }
        }
        mentionFragment.setRefreshFlag(Flag.MENTION_TASK_DIED);
    }
}
