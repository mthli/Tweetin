package io.github.mthli.Tweetin.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import com.twotoasters.jazzylistview.JazzyListView;
import io.github.mthli.Tweetin.R;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;

public class MainFragment extends ProgressFragment {
    private View view;

    private FloatingActionButton fab;
    private SwipeRefreshLayout srl;
    private JazzyListView timeLine;

    private Twitter twitter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        timeLine = (JazzyListView) view.findViewById(R.id.main_fragment_timeline);

        fab = (FloatingActionButton) view.findViewById(
                R.id.button_floating_action
        );
        fab.attachToListView(timeLine);
        fab.show();

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        srl.setColorSchemeResources(
                R.color.red_default,
                R.color.orange_default,
                R.color.blue_default,
                R.color.teal_default
        );
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* Do something */
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        String conKey = preferences.getString(getString(R.string.sp_consumer_key), null);
        String conSecret = preferences.getString(getString(R.string.sp_consumer_secret), null);
        String accToken = preferences.getString(getString(R.string.sp_access_token), null);
        String accTokenSecret = preferences.getString(getString(R.string.sp_access_token_secret), null);
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(conKey, conSecret);
        AccessToken token = new AccessToken(accToken, accTokenSecret);
        twitter.setOAuthAccessToken(token);

        /* Do something */
        // new Thread(test).start();
    }

    Runnable test = new Runnable() {
        @Override
        public void run() {
            try {
                long sinceId = 517839042969206786L;
                Paging paging = new Paging(1, 1024, sinceId);
                List<Status> statusList = twitter.getHomeTimeline(paging);
                System.out.println("---------------------------------");
                for (Status status : statusList) {
                    String text = status.getText();
                    System.out.println(text);
                    System.out.println(status.getId());
                    System.out.println("---------------------------------");
                }
            } catch (Exception e){
                    e.printStackTrace();
            }
        }
    };
}
