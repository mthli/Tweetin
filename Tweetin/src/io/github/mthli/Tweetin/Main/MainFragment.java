package io.github.mthli.Tweetin.Main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.devspark.progressfragment.ProgressFragment;
import com.etiennelawlor.quickreturn.library.views.NotifyingListView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {
    private View view;
    private SwipeRefreshLayout refresh;
    private SwipeRefreshLayout getRefresh() {
        return refresh;
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

        refresh = (SwipeRefreshLayout) view
                .findViewById(R.id.main_swipe_container);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do something */
            }
        });

        /* Do something */
        NotifyingListView listView = (NotifyingListView) view
                .findViewById(R.id.main_fragment_listview);

    }
}
