package io.github.mthli.Tweetin.Main;

import android.os.Bundle;
import android.view.View;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import com.twotoasters.jazzylistview.JazzyListView;
import io.github.mthli.Tweetin.R;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainFragment extends ProgressFragment {
    private View view;

    private FloatingActionButton fab;
    private PullToRefreshLayout ptr;
    private JazzyListView timeLine;

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

        ptr = (PullToRefreshLayout) view.findViewById(
                R.id.ptr_layout
        );
        /* Do something */
        ActionBarPullToRefresh.from(getActivity())
                .theseChildrenArePullable(timeLine)
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        /* Do something */
                    }
                }).setup(ptr);

        /* Do something */
    }
}
