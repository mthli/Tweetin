package io.github.mthli.Tweetin.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Activity.SearchActivity;
import io.github.mthli.Tweetin.View.ViewUnit;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.*;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends ProgressFragment {
    private int fragmentFlag;
    public int getFragmentFlag() {
        return fragmentFlag;
    }

    private View contentView;

    private SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private ImageButton fab;
    private Animator animator;

    private TweetAdapter tweetAdapter;
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }

    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private LoadFirstTask loadFirstTask;
    public void startLoadFirstTask(boolean swipeRefresh) {
        loadFirstTask = new LoadFirstTask(this, swipeRefresh);
        loadFirstTask.execute();
    }

    private LoadMoreTask loadMoreTask;

    private int taskStatus;
    public int getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
    public boolean isSomeTaskRunning() {
        if (taskStatus == FlagUnit.TASK_RUNNING) {
            return true;
        }

        return false;
    }
    public void cancelAllTask() {
        if (loadFirstTask != null && loadFirstTask.getStatus() == AsyncTask.Status.RUNNING) {
            loadFirstTask.cancel(true);
        }

        if (loadMoreTask != null && loadMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            loadMoreTask.cancel(true);
        }
    }

    private int nextPage = 2;
    public int getNextPage() {
        return nextPage;
    }
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getActivity() instanceof SearchActivity) {
            fragmentFlag = FlagUnit.IN_SEARCH_FRAGMENT;
        } else {
            fragmentFlag = getArguments().getInt(getString(R.string.bundle_fragment_flag), FlagUnit.IN_TIMELINE_FRAGMENT);
        }

        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.base_fragment);
        contentView = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        initUI();

        loadFirstTask = new LoadFirstTask(this, false);
        loadFirstTask.execute();
    }

    private void showFAB(boolean show) {
        if ((fragmentFlag != FlagUnit.IN_TIMELINE_FRAGMENT) || (animator != null && animator.isRunning())) {
            return;
        }

        if (show && fab.getVisibility() == View.INVISIBLE) {
            animator = ViewAnimationUtils.createCircularReveal(
                    fab,
                    (int) fab.getPivotX(),
                    (int) fab.getPivotY(),
                    0,
                    fab.getWidth()
            );

            fab.setVisibility(View.VISIBLE);
            animator.start();
        } else if (!show && fab.getVisibility() == View.VISIBLE) {
            animator = ViewAnimationUtils.createCircularReveal(
                    fab,
                    (int) fab.getPivotX(),
                    (int) fab.getPivotY(),
                    fab.getWidth(),
                    0
            );

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    fab.setVisibility(View.INVISIBLE);
                }
            });

            animator.start();
        }
    }

    private int lastDetailPosition = 0;
    private int currentFirst = 0;
    private int currentCount = 0;

    private void initUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.base_fragment_swipe_container);
        swipeRefreshLayout.setProgressViewOffset(false, 0, ViewUnit.getToolbarHeight(getActivity()));
        ViewUnit.setSwipeRefreshLayoutTheme(getActivity(), swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstTask = new LoadFirstTask(BaseFragment.this, true);
                loadFirstTask.execute();
            }
        });

        fab = (ImageButton) contentView.findViewById(R.id.base_fragment_fab);
        if (fragmentFlag == FlagUnit.IN_TIMELINE_FRAGMENT) {
            fab.setVisibility(View.VISIBLE);
            ViewCompat.setElevation(fab, ViewUnit.getElevation(getActivity(), 2));

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(getString(R.string.post_intent_post_flag), FlagUnit.POST_NEW);
                    startActivity(intent);
                }
            });
            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(getActivity(), R.string.fragment_toast_post_a_new_tweet, Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        final ListView listView = (ListView) contentView.findViewById(R.id.base_fragment_listview);

        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean moveToBottom = false;
            private int previous = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && (lastDetailPosition < currentFirst || lastDetailPosition > currentFirst + currentCount)) {
                    tweetList.get(lastDetailPosition).setDetail(false);
                    tweetList.get(lastDetailPosition).setLoad(false);
                    tweetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                }
                if (previous > firstVisibleItem) {
                    moveToBottom = false;
                }
                previous = firstVisibleItem;

                currentFirst = firstVisibleItem;
                currentCount = visibleItemCount;

                showFAB(!moveToBottom);

                if (totalItemCount > 7 && totalItemCount == firstVisibleItem + visibleItemCount && !isSomeTaskRunning() && moveToBottom) {
                    loadMoreTask = new LoadMoreTask(BaseFragment.this);
                    loadMoreTask.execute();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (tweetList.get(lastDetailPosition).isDetail() && position != lastDetailPosition) {
                    tweetList.get(lastDetailPosition).setDetail(false);
                    tweetList.get(position).setDetail(true);
                    tweetAdapter.notifyDataSetChanged();

                    if (tweetList.get(lastDetailPosition).isLoad() && lastDetailPosition < position) {
                        listView.setSelection(lastDetailPosition);
                    }
                    tweetList.get(lastDetailPosition).setLoad(false);

                    lastDetailPosition = position;
                } else if (!tweetList.get(position).isDetail()) {
                    tweetList.get(position).setDetail(true);
                    tweetAdapter.notifyDataSetChanged();

                    lastDetailPosition = position;
                }
            }
        });
    }
}
