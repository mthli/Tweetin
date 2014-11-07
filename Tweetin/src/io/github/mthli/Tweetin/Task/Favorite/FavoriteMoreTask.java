package io.github.mthli.Tweetin.Task.Favorite;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import io.github.mthli.Tweetin.Fragment.Favorite.FavoriteFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class FavoriteMoreTask extends AsyncTask<Void, Integer, Boolean> {
    private FavoriteFragment favoriteFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    public FavoriteMoreTask(FavoriteFragment favoriteFragment) {
        this.favoriteFragment = favoriteFragment;
    }

    @Override
    protected void onPreExecute() {
        if (favoriteFragment.getRefreshFlag() == Flag.FAVORITE_TASK_RUNNING) {
            onCancelled();
        } else {
            favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_RUNNING);
        }

        context = favoriteFragment.getContentView().getContext();
        twitter = favoriteFragment.getTwitter();
        useId = favoriteFragment.getUseId();

        tweetAdapter = favoriteFragment.getTweetAdapter();
        tweetList = favoriteFragment.getTweetList();

        swipeRefreshLayout = favoriteFragment.getSwipeRefreshLayout();
        swipeRefreshLayout.setRefreshing(true);
    }

    private List<twitter4j.Status> statusList;
    private static int count = 2;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Paging paging = new Paging(count, 40);
            statusList = twitter.getFavorites(paging);
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
                    tweet.setOriginalStatusId(status.getId());
                    tweet.setAfterRetweetStatusId(-1); //
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
                    if (status.getRetweetedStatus().isFavorited()) {
                        tweet.setAfterFavoriteStatusId(status.getRetweetedStatus().getId()); //
                        tweet.setFavorite(true);
                    } else {
                        tweet.setAfterFavoriteStatusId(-1); //
                        tweet.setFavorite(false);
                    }
                } else {
                    tweet.setOriginalStatusId(status.getId());
                    tweet.setAfterRetweetStatusId(-1); //
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
                    if (status.isFavorited()) {
                        tweet.setAfterFavoriteStatusId(status.getId()); //
                        tweet.setFavorite(true);
                    } else {
                        tweet.setAfterFavoriteStatusId(-1); //
                        tweet.setFavorite(false);
                    }
                }
                if (status.isRetweetedByMe() || status.isRetweeted()) {
                    tweet.setAfterRetweetStatusId(status.getId()); //
                    tweet.setRetweetedByUserId(useId);
                    tweet.setRetweet(true);
                    tweet.setRetweetedByUserName(
                            context.getString(R.string.tweet_info_retweeted_by_me)
                    );
                }
                tweetList.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
        favoriteFragment.setRefreshFlag(Flag.FAVORITE_TASK_IDLE);
    }
}
