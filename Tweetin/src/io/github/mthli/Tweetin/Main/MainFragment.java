package io.github.mthli.Tweetin.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.devspark.progressfragment.ProgressFragment;
import com.etiennelawlor.quickreturn.library.views.NotifyingListView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private SharedPreferences sharedPreferences;
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private List<Tweet> tweetList = new ArrayList<Tweet>();
    private TweetAdapter tweetAdapter;
    public List<Tweet> getTweetList() {
        return tweetList;
    }
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1
        );
        String conKey = sharedPreferences.getString(
                getString(R.string.sp_consumer_key),
                null
        );
        String conSecret = sharedPreferences.getString(
                getString(R.string.sp_consumer_secret),
                null
        );
        String accessToken = sharedPreferences.getString(
                getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                getString(R.string.sp_access_token_secret),
                null
        );
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(conKey, conSecret);
        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.main_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do something */
            }
        });

        /* Do something */
        NotifyingListView listView = (NotifyingListView) view
                .findViewById(R.id.main_fragment_listview);

        /* Do something */
    }

    /* Do something */
}
