package io.github.mthli.Tweetin.Tweet.Timeline;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Main.MainFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class TimelineInitTask extends AsyncTask<Void, Integer, Boolean> {

    private MainFragment mainFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public TimelineInitTask(
            MainFragment mainFragment
    ) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        context = mainFragment.getView().getContext();
        twitter = mainFragment.getTwitter();
        useId = mainFragment.getUseId();

        tweetAdapter = mainFragment.getTweetAdapter();
        tweetList = mainFragment.getTweetList();

        /* Do something */
        mainFragment.setContentShown(false);
    }

    private List<twitter4j.Status> statusList;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(1, 40);
            statusList = twitter.getHomeTimeline(paging);
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
            /* Do something */
            tweetList.clear();
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
                    tweet.setText(
                            status.getRetweetedStatus().getText()
                    );
                    Place place = status.getRetweetedStatus().getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserName(
                            status.getUser().getName()
                    );
                } else {
                    tweet.setStatusId(status.getId());
                    tweet.setReplyToStatusId(status.getInReplyToStatusId());
                    tweet.setUserId(status.getUser().getId());
                    tweet.setRetweetedByUserId(-1);
                    tweet.setAvatarURL(
                            status.getUser().getBiggerProfileImageURL()
                    );
                    tweet.setCreatedAt(
                            format.format(status.getCreatedAt())
                    );
                    tweet.setName(status.getUser().getName());
                    tweet.setScreenName(
                            "@" + status.getUser().getScreenName()
                    );
                    tweet.setProtect(status.getUser().isProtected());
                    tweet.setText(status.getText());
                    Place place = status.getPlace();
                    if (place != null) {
                        tweet.setCheckIn(place.getFullName());
                    } else {
                        tweet.setCheckIn(null);
                    }
                    tweet.setRetweet(false);
                    tweet.setRetweetedByUserName(null);
                }
                if (status.isRetweetedByMe() || status.isRetweeted()) {
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserId(useId);
                    tweet.setRetweetedByUserName(
                            context.getString(R.string.tweet_retweet_by_me)
                    );
                }
                tweetList.add(tweet);
            }

            /* Do something */
            mainFragment.setContentEmpty(false);
            tweetAdapter.notifyDataSetChanged();
            mainFragment.setContentShown(true);
        } else {
            /* Do something */
            mainFragment.setContentEmpty(true);
            mainFragment.setEmptyText(
                    R.string.timeline_error_get_timeline_failed
            );
            mainFragment.setContentShown(true);
        }
        /* Do something */
    }
}
