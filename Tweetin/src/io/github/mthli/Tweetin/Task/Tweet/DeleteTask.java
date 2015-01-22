package io.github.mthli.Tweetin.Task.Tweet;

import android.app.Activity;
import android.os.AsyncTask;
import io.github.mthli.Tweetin.Activity.InReplyToActivity;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.SearchActivity;
import io.github.mthli.Tweetin.Data.DataUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.Notification.NotificationUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.TwitterException;

import java.util.List;

public class DeleteTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private int position;
    private Tweet tweet;

    public DeleteTask(Activity activity, TweetAdapter tweetAdapter, List<Tweet> tweetList, int position) {
        this.activity = activity;

        this.tweetAdapter = tweetAdapter;
        this.tweetList = tweetList;
        this.position = position;
        this.tweet = tweetList.get(position);
    }

    @Override
    protected void onPreExecute() {
        NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_ing, tweet.getText());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            TwitterUnit.getTwitterFromSharedPreferences(activity).destroyStatus(tweet.getStatusId());

            DataUnit.updateByDelete(activity, tweet);

            NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_successful, tweet.getText());
            NotificationUnit.cancel(activity);
        } catch (TwitterException t) {
            NotificationUnit.show(activity, R.drawable.ic_notification_delete, R.string.notification_delete_failed, tweet.getText());
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {}

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tweetList.remove(tweet);
            tweetAdapter.notifyDataSetChanged();

            if (tweetList.size() <= 0) {
                if (activity instanceof MainActivity) {
                    ListFragment listFragment = ((MainActivity) activity).getCurrentListFragment();
                    listFragment.setContentEmpty(true);
                    listFragment.setEmptyText(R.string.fragment_list_empty);
                    listFragment.setContentShown(true);
                    return;
                }

                if (activity instanceof InReplyToActivity) {
                    ListFragment listFragment = ((InReplyToActivity) activity).getInReplyToFragment();
                    listFragment.setContentEmpty(true);
                    listFragment.setEmptyText(R.string.fragment_list_empty);
                    listFragment.setContentShown(true);
                    return;
                }

                if (activity instanceof SearchActivity) {
                    ListFragment listFragment = ((SearchActivity) activity).getSearchFragment();
                    listFragment.setContentEmpty(true);
                    listFragment.setEmptyText(R.string.fragment_list_empty);
                    listFragment.setContentShown(true);
                    return;
                }
            }
        }
    }
}
