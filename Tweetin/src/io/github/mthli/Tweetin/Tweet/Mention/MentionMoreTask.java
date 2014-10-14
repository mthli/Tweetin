package io.github.mthli.Tweetin.Tweet.Mention;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.List;

public class MentionMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private MentionFragment mentionFragment;
    private Context context;
    private long useId;

    private SwipeRefreshLayout srl;

    private Twitter twitter;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public MentionMoreTask(MentionFragment mentionFragment) {
        this.mentionFragment = mentionFragment;
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
            srl.setRefreshing(true);
        }
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 20);
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
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(status.getRetweetedStatus().getUser().getId());
                    tweet.setAvatarUrl(
                            status.getRetweetedStatus().getUser().getBiggerProfileImageURL()
                    );
                    tweet.setCreatedAt(
                            format.format(status.getRetweetedStatus().getCreatedAt())
                    );
                    tweet.setName(status.getRetweetedStatus().getUser().getName());
                    tweet.setScreenName(
                            "@" + status.getRetweetedStatus().getUser().getScreenName()
                    );
                    tweet.setProtect(status.getRetweetedStatus().getUser().isProtected());
                    tweet.setText(status.getRetweetedStatus().getText());
                    Place place = status.getRetweetedStatus().getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setRetweet(true);
                    tweet.setRetweetedByName(status.getUser().getName());
                    tweet.setRetweetedById(status.getUser().getId());
                    tweet.setReplyTo(
                            status.getRetweetedStatus().getInReplyToStatusId()
                    );
                } else {
                    tweet.setTweetId(status.getId());
                    tweet.setUserId(status.getUser().getId());
                    tweet.setAvatarUrl(status.getUser().getBiggerProfileImageURL());
                    tweet.setCreatedAt(format.format(status.getCreatedAt()));
                    tweet.setName(status.getUser().getName());
                    tweet.setScreenName("@" + status.getUser().getScreenName());
                    tweet.setProtect(status.getUser().isProtected());
                    tweet.setText(status.getText());
                    Place place = status.getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setRetweet(false);
                    tweet.setRetweetedByName(null);
                    tweet.setRetweetedById(0);
                    tweet.setReplyTo(status.getInReplyToStatusId());
                }
                if (status.isRetweetedByMe() || status.isRetweeted()) {
                    tweet.setRetweet(true);
                    tweet.setRetweetedByName(context.getString(R.string.tweet_retweeted_by_me));
                    tweet.setRetweetedById(useId);
                }
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
        srl.setRefreshing(false);
        mentionFragment.setRefreshFlag(Flag.MENTION_TASK_DIED);
    }
}
