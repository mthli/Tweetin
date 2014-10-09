package io.github.mthli.Tweetin.Detail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;

public class DetailFragment extends ProgressFragment {
    private View view;
    
    private Button detailQuote;
    private Button detailRetweet;
    private Button detailReply;

    private long userId;
    private long tweetId;
    private long replyTo;
    public long getUserId() {
        return userId;
    }
    public long getTweetId() {
        return tweetId;
    }
    public long getReplyTo() {
        return replyTo;
    }

    private DetailTask detailTask;
    public void allTaskDown() {
        if (detailTask != null && detailTask.getStatus() == AsyncTask.Status.RUNNING) {
            detailTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.detail_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        /* Do something with click */
        detailQuote = (Button) view.findViewById(
                R.id.detail_quote
        );
        detailRetweet = (Button) view.findViewById(
                R.id.detail_retweet
        );
        detailReply = (Button) view.findViewById(
                R.id.detail_reply
        );

        Intent intent = getActivity().getIntent();
        userId = intent.getLongExtra(
                getString(R.string.detail_intent_user_id),
                0
        );
        tweetId = intent.getLongExtra(
                view.getContext().getString(R.string.detail_intent_tweet_id),
                0
        );
        replyTo = intent.getLongExtra(
                view.getContext().getString(R.string.detail_intent_reply_to),
                -1
        );

        /* Do something */
        detailTask = new DetailTask(this);
        detailTask.execute();
    }
}
