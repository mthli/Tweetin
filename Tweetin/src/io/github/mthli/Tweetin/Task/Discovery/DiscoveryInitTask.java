package io.github.mthli.Tweetin.Task.Discovery;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.mthli.Tweetin.Fragment.DiscoveryFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Query;
import twitter4j.Twitter;

import java.util.List;

public class DiscoveryInitTask extends AsyncTask<Void, Integer, Boolean> {
    private DiscoveryFragment discoveryFragment;
    private Context context;
    private Twitter twitter;

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private boolean tweetWithDetail;

    private EditText editText;
    private TextView introduction;
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

        tweetAdapter = discoveryFragment.getTweetAdapter();
        tweetList = discoveryFragment.getTweetList();
        tweetWithDetail = discoveryFragment.isTweetWithDetail();

        editText = discoveryFragment.getSearchBox();
        introduction = discoveryFragment.getIntroduction();
        progressBar = discoveryFragment.getProgressBar();
        listView = discoveryFragment.getListView();
        queryStr = editText.getText().toString();

        introduction.setVisibility(View.GONE);
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
            TweetUnit tweetUnit = new TweetUnit(context);
            tweetList.clear();
            for (twitter4j.Status status : statusList) {
                tweetList.add(tweetUnit.getTweetFromStatus(status, tweetWithDetail));
            }

            progressBar.setVisibility(View.GONE);
            if (tweetList.size() <= 0) {
                tweetAdapter.notifyDataSetChanged();
                listView.setVisibility(View.GONE);
                introduction.setVisibility(View.VISIBLE);
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
            introduction.setVisibility(View.VISIBLE);
            Toast.makeText(
                    context,
                    R.string.discovery_toast_discovery_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
        discoveryFragment.setRefreshFlag(Flag.DISCOVERY_TASK_IDLE);
    }
}
