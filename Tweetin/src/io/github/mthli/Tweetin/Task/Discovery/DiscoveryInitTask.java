package io.github.mthli.Tweetin.Task.Discovery;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.mthli.Tweetin.Fragment.DiscoveryFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class DiscoveryInitTask extends AsyncTask<Void, Integer, Boolean> {
    private DiscoveryFragment discoveryFragment;
    private Context context;
    private Twitter twitter;
    private long useId;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;

    private EditText editText;
    private CircularProgressBar progressBar;
    private ListView listView;
    private String queryStr;

    public DiscoveryInitTask(DiscoveryFragment discoveryFragment) {
        this.discoveryFragment = discoveryFragment;
    }

    @Override
    protected void onPreExecute() {
        if (discoveryFragment.getRefreshFlag() == Flag.DISCOVERY_TASK_RUNNING) {
            onCancelled();
        } else {
            discoveryFragment.setRefreshFlag(Flag.DISCOVERY_TASK_RUNNING);
        }

        context = discoveryFragment.getContentView().getContext();
        twitter = discoveryFragment.getTwitter();
        useId = discoveryFragment.getUseId();

        tweetAdapter = discoveryFragment.getTweetAdapter();
        tweetList = discoveryFragment.getTweetList();

        editText = discoveryFragment.getSearchBox();
        progressBar = discoveryFragment.getProgressBar();
        listView = discoveryFragment.getListView();
        queryStr = editText.getText().toString();

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    List<twitter4j.Status> statusList;
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Query query = new Query(queryStr);
            query.setCount(100);
            statusList = twitter.search(query).getTweets();
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
            tweetList.clear();
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
                    tweet.setFavorite(status.getRetweetedStatus().isFavorited());
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
                            context.getString(R.string.tweet_info_retweeted_by_me)
                    );
                }
                tweetList.add(tweet);
            }

            progressBar.setVisibility(View.GONE);
            if (tweetList.size() <= 0) {
                tweetAdapter.notifyDataSetChanged();
                listView.setVisibility(View.GONE);
                Toast.makeText(
                        context,
                        R.string.discovery_toast_nothing,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                tweetAdapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            Toast.makeText(
                    context,
                    R.string.discovery_toast_discovery_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
        discoveryFragment.setRefreshFlag(Flag.DISCOVERY_TASK_IDLE);
    }
}
