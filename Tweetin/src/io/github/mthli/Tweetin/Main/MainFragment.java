package io.github.mthli.Tweetin.Main;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.*;
import io.github.mthli.Tweetin.Unit.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.Flag;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {
    private View view;
    private long useId = 0;

    private boolean isMoveToButton = false;
    private FloatingActionButton fab;
    private SwipeRefreshLayout srl;
    public SwipeRefreshLayout getSrl() {
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

    private TweetInitTask tweetInitTask;
    private TweetMoreTask tweetMoreTask;
    private TweetRetweetTask tweetRetweetTask;
    private TweetCancelTask tweetCancelTask;
    private TweetDeleteTask tweetDeleteTask;
    private int refreshFlag = Flag.TWEET_TASK_DIED;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    public boolean isSomeTaskAlive() {
        if ((tweetInitTask != null && tweetInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (tweetMoreTask != null && tweetMoreTask.getStatus() == AsyncTask.Status.RUNNING)) {
            return true;
        }
        return false;
    }
    public void allTaskDown() {
        if (tweetInitTask != null && tweetInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetInitTask.cancel(true);
        }
        if (tweetMoreTask != null && tweetMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetMoreTask.cancel(true);
        }
        if (tweetRetweetTask != null && tweetRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetRetweetTask.cancel(true);
        }
        if (tweetCancelTask != null && tweetCancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetCancelTask.cancel(true);
        }
        if (tweetDeleteTask != null && tweetDeleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            tweetDeleteTask.cancel(true);
        }
    }

    private void clip(int location) {
        ClipboardManager manager = (ClipboardManager) view.getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        String text = tweetList.get(location).getText();
        ClipData data = ClipData.newPlainText(
                getString(R.string.tweet_copy_label),
                text
        );
        manager.setPrimaryClip(data);
        Toast.makeText(
                view.getContext(),
                R.string.tweet_copy_successful,
                Toast.LENGTH_SHORT
        ).show();
    }

    private List<Status> statusList;
    public List<Status> getStatusList() {
        return statusList;
    }
    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
    private void showItemLongClickDialog(final int location) {
        LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(
                R.layout.context_menu,
                null
        );
        ListView menu = (ListView) layout.findViewById(R.id.context_menu);
        List<String> menuItem = new ArrayList<String>();

        Status status = statusList.get(location);
        final int flag;
        if (status.isRetweetedByMe() || status.isRetweeted()) {
            flag = Flag.TWEET_STATUS_RETWEET_BY_ME;
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_reply));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_cancel_retweet));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_retweet_with_comment));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_copy));
        } else {
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_reply));
            if (status.getUser().getId() == useId) {
                flag = Flag.TWEET_STATUS_POST_BY_ME;
                menuItem.add(view.getContext().getString(R.string.tweet_menu_item_delete));
            } else {
                flag = Flag.TWEET_STATUS_NONE;
                menuItem.add(view.getContext().getString(R.string.tweet_menu_item_retweet));
            }
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_retweet_with_comment));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_copy));
        }

        ContextMenuAdapter adapter = new ContextMenuAdapter(
                view.getContext(),
                R.layout.context_menu_item,
                menuItem
        );
        menu.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(layout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        /* Do something */
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    case 1:
                        switch (flag) {
                            case Flag.TWEET_STATUS_RETWEET_BY_ME:
                                tweetCancelTask = new TweetCancelTask(
                                        MainFragment.this,
                                        location
                                );
                                tweetCancelTask.execute();
                                break;
                            case Flag.TWEET_STATUS_POST_BY_ME:
                                tweetDeleteTask = new TweetDeleteTask(
                                        MainFragment.this,
                                        location
                                );
                                tweetDeleteTask.execute();
                                break;
                            case Flag.TWEET_STATUS_NONE:
                                tweetRetweetTask = new TweetRetweetTask(
                                        MainFragment.this,
                                        location
                                );
                                tweetRetweetTask.execute();
                                break;
                            default:
                                break;
                        }
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    case 2:
                        /* Do something */
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    case 3:
                        clip(location);
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = preferences.getLong(getString(R.string.sp_use_id), 0);

        ListView listView = (ListView) view.findViewById(R.id.main_fragment_timeline);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        fab = (FloatingActionButton) view.findViewById(
                R.id.button_floating_action
        );
        fab.attachToListView(listView);
        fab.show();

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        srl.setColorSchemeResources(
                R.color.tumblr_ptr_red,
                R.color.tumblr_ptr_yellow,
                R.color.tumblr_ptr_blue,
                R.color.tumblr_ptr_green
        );
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetInitTask = new TweetInitTask(MainFragment.this, true);
                tweetInitTask.execute();
            }
        });

        tweetInitTask = new TweetInitTask(MainFragment.this, false);
        tweetInitTask.execute();

        /* Do something */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Do something */
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showItemLongClickDialog(position);
                return true;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int previous = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    /* Do nothing */
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    isMoveToButton = true;
                    fab.hide();
                }
                if (previous > firstVisibleItem) {
                    isMoveToButton = false;
                    fab.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskAlive() && isMoveToButton) {
                        tweetMoreTask = new TweetMoreTask(MainFragment.this);
                        tweetMoreTask.execute();
                    }
                }
            }
        });


    }
}
