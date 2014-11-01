package io.github.mthli.Tweetin.Mention;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class MentionMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public MentionMoreTask(MentionFragment mentionFragment) {
        this.mentionFragment = mentionFragment;
    }

    @Override
    protected void onPreExecute() {
        if (mentionFragment.getRefreshFlag() == Flag.MENTION_TASK_RUNNING) {
            onCancelled();
        } else {
            mentionFragment.setRefreshFlag(Flag.MENTION_TASK_RUNNING);
        }

        context = mentionFragment.getContentView().getContext();
        twitter = mentionFragment.getTwitter();
        useId = mentionFragment.getUseId();

        tweetAdapter = mentionFragment.getTweetAdapter();
        tweetList = mentionFragment.getTweetList();

        swipeRefreshLayout = mentionFragment.getSwipeRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 40);
            statusList = twitter.getMentionsTimeline(paging);
            count++;
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
            SimpleDateFormat format = new SimpleDateFormat(
                    context.getString(R.string.tweet_date_format)
            );
            for (twitter4j.Status status : statusList) {
                Tweet tweet = new Tweet();
                if (status.isRetweet()) {
                    tweet.setStatusId(status.getId());
                    tweet.setReplyToStatusId(
                            status.getRetweetedStatus().getInReplyToStatusId()
                    );
                    tweet.setUserId(
                            status.getRetweetedStatus().getUser().getId()
                    );
                    tweet.setRetweetedByUserId(status.getUser().getId());
                    tweet.setAvatarURL(
                            status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                    );
                    tweet.setCreatedAt(
                            format.format(status.getRetweetedStatus().getCreatedAt())
                    );
                    tweet.setName(
                            status.getRetweetedStatus().getUser().getName()
                    );
                    tweet.setScreenName(
                            "@" + status.getRetweetedStatus().getUser().getScreenName()
                    );
                    tweet.setProtect(
                            status.getRetweetedStatus().getUser().isProtected()
                    );
                    Place place = status.getRetweetedStatus().getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setText(
                            status.getRetweetedStatus().getText()
                    );
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserName(
                            status.getUser().getName()
                    );
                    tweet.setFavorite(
                            status.getRetweetedStatus().isFavorited()
                    );
                } else {
                    tweet.setStatusId(status.getId());
                    tweet.setReplyToStatusId(status.getInReplyToStatusId());
                    tweet.setUserId(status.getUser().getId());
                    tweet.setRetweetedByUserId(-1);
                    tweet.setAvatarURL(status.getUser().getBiggerProfileImageURL());
                    tweet.setCreatedAt(
                            format.format(status.getCreatedAt())
                    );
                    tweet.setName(status.getUser().getName());
                    tweet.setScreenName("@" + status.getUser().getScreenName());
                    tweet.setProtect(status.getUser().isProtected());
                    Place place = status.getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setText(status.getText());
                    tweet.setRetweet(false);
                    tweet.setRetweetedByUserName(null);
                    tweet.setFavorite(status.isFavorited());
                }
                if (status.isRetweetedByMe() || status.isRetweeted()) {
                    tweet.setRetweetedByUserId(useId);
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserName(
                            context.getString(R.string.tweet_retweet_by_me)
                    );
                }
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
        mentionFragment.setRefreshFlag(Flag.MENTION_TASK_IDLE);
    }
}
