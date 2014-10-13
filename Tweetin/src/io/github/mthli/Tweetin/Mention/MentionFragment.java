package io.github.mthli.Tweetin.Mention;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Base.Tweet;
import io.github.mthli.Tweetin.Tweet.Base.TweetAdapter;
import io.github.mthli.Tweetin.Tweet.Mention.MentionInitTask;
import io.github.mthli.Tweetin.Tweet.Mention.MentionMoreTask;
import io.github.mthli.Tweetin.Tweet.Mention.MentionRetweetTask;

import java.util.ArrayList;
import java.util.List;

public class MentionFragment extends ProgressFragment {

    private View view;

    private long useId = 0;
    public long getUseId() {
        return useId;
    }

    private SwipeRefreshLayout srl;
    private SwipeRefreshLayout getSrl() {
        return srl;
    }

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private MentionInitTask mentionInitTask;
    private MentionMoreTask mentionMoreTask;
    private MentionRetweetTask mentionRetweetTask;
    public boolean isSomeTaskAlive() {
        if ((mentionInitTask != null && mentionInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (mentionMoreTask != null && mentionMoreTask.getStatus() == AsyncTask.Status.RUNNING)) {
            return true;
        }
        return false;
    }
    public void allTaskDown() {
        if (mentionInitTask != null && mentionInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionInitTask.cancel(true);
        }
        if (mentionMoreTask != null && mentionMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionMoreTask.cancel(true);
        }
        if (mentionRetweetTask != null && mentionRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            mentionRetweetTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.mention_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        useId = ((MentionActivity) getActivity()).getUseId();

        ListView listView = (ListView) view.findViewById(R.id.mention_fragment_timeline);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        srl = (SwipeRefreshLayout) view.findViewById(R.id.mention_swipe_container);
        srl.setColorSchemeResources(
                R.color.tumblr_ptr_red,
                R.color.tumblr_ptr_yellow,
                R.color.tumblr_ptr_blue,
                R.color.tumblr_ptr_green
        );
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do something */
            }
        });

        /* Do something */
    }
}
