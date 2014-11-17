package io.github.mthli.Tweetin.Fragment;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Timeline.*;
import io.github.mthli.Tweetin.Task.Unit.DeleteTask;
import io.github.mthli.Tweetin.Task.Unit.FavoriteTask;
import io.github.mthli.Tweetin.Task.Unit.RetweetTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuUnit;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends ProgressFragment {
    private View view;

    private int refreshFlag = Flag.TIMELINE_TASK_IDLE;
    private boolean moveToBottom = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private boolean tweetWithDetail;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public boolean isTweetWithDetail() {
        return tweetWithDetail;
    }
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private TimelineInitTask timelineInitTask;
    private TimelineMoreTask timelineMoreTask;
    private DeleteTask deleteTask;
    private RetweetTask retweetTask;
    private FavoriteTask favoriteTask;
    public boolean isSomeTaskRunning() {
        if (
                (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING)
        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineInitTask.cancel(true);
        }
        if (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineMoreTask.cancel(true);
        }
        if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            deleteTask.cancel(true);
        }
        if (retweetTask != null && retweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            retweetTask.cancel(true);
        }
        if (favoriteTask != null && favoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.timeline_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        twitter = TweetUnit.getTwitterFromSharedPreferences(getActivity());
        useId = TweetUnit.getUseIdFromeSharedPreferences(getActivity());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        tweetWithDetail = sharedPreferences.getBoolean(
                getString(R.string.sp_is_tweet_with_detail),
                false
        );

        ListView listView = (ListView) view
                .findViewById(R.id.timeline_fragment_listview);
        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList,
                tweetWithDetail
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.timeline_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.text,
                R.color.secondary_text,
                R.color.text,
                R.color.secondary_text
        );
        Display display = getActivity()
                .getWindowManager()
                .getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        swipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                height / 10
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timelineInitTask = new TimelineInitTask(
                        TimelineFragment.this,
                        true
                );
                timelineInitTask.execute();
            }
        });

        floatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.timeline_floating_action_button);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(
                        getString(R.string.post_intent_flag),
                        Flag.POST_ORIGINAL
                );
                ActivityAnim anim = new ActivityAnim();
                startActivity(intent);
                anim.fade(getActivity());
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        getActivity(),
                        R.string.timeline_toast_fab_add,
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TweetUnit.tweetToDetailActivity(
                        getActivity(),
                        tweetList,
                        position
                );
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
                    moveToBottom = true;
                    floatingActionButton.hide();
                }
                if (previous > firstVisibleItem) {
                    moveToBottom = false;
                    floatingActionButton.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && moveToBottom) {
                        timelineMoreTask = new TimelineMoreTask(TimelineFragment.this);
                        timelineMoreTask.execute();
                    }
                }
            }
        });

        timelineInitTask = new TimelineInitTask(
                TimelineFragment.this,
                false
        );
        timelineInitTask.execute();
    }

    private AlertDialog alertDialog;
    private void multipleAtTwo(int flag, int location) {
        switch (flag) {
            case Flag.STATUS_NONE:
                retweetTask = new RetweetTask(
                        getActivity(),
                        twitter,
                        tweetAdapter,
                        tweetList,
                        location
                );
                retweetTask.execute();
                break;
            case Flag.STATUS_RETWEETED_BY_ME:
                Toast.makeText(
                        getActivity(),
                        R.string.context_toast_already_retweet,
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case Flag.STATUS_SENT_BY_ME:
                deleteTask = new DeleteTask(
                        getActivity(),
                        twitter,
                        tweetAdapter,
                        tweetList,
                        location
                );
                deleteTask.execute();
                break;
            default:
                break;
        }
    }
    private void showItemLongClickDialog(final int location) {
        LinearLayout linearLayout = (LinearLayout) getActivity()
                .getLayoutInflater().inflate(
                        R.layout.context_menu,
                        null
                );
        ListView menu = (ListView) linearLayout.findViewById(R.id.context_menu_listview);
        List<String> menuItemList = new ArrayList<String>();

        final int flag;
        final Tweet tweet = tweetList.get(location);
        menuItemList.add(getString(R.string.context_menu_item_reply));
        menuItemList.add(getString(R.string.context_menu_item_quote));
        if (tweet.getRetweetedByUserId() != -1l && tweet.getRetweetedByUserId() == useId) {
            flag = Flag.STATUS_RETWEETED_BY_ME;
            menuItemList.add(getString(R.string.context_menu_item_retweet));
        } else {
            if (tweet.getUserId() != useId) {
                flag = Flag.STATUS_NONE;
                menuItemList.add(getString(R.string.context_menu_item_retweet));
            } else {
                flag = Flag.STATUS_SENT_BY_ME;
                menuItemList.add(getString(R.string.context_menu_item_delete));
            }
        }
        menuItemList.add(getString(R.string.context_menu_item_favorite));
        menuItemList.add(getString(R.string.context_menu_item_copy));

        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(
                view.getContext(),
                R.layout.context_menu_item,
                menuItemList
        );
        menu.setAdapter(contextMenuAdapter);
        contextMenuAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(linearLayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ContextMenuUnit.reply(
                                getActivity(),
                                tweetList,
                                location
                        );
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 1:
                        ContextMenuUnit.quote(
                                getActivity(),
                                tweetList,
                                location
                        );
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 2:
                        multipleAtTwo(flag, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 3:
                        if (!tweet.isFavorite()) {
                            favoriteTask = new FavoriteTask(
                                    getActivity(),
                                    twitter,
                                    tweetAdapter,
                                    tweetList,
                                    location
                            );
                            favoriteTask.execute();
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    R.string.context_toast_already_favorite,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 4:
                        ContextMenuUnit.clip(
                                getActivity(),
                                tweetList,
                                location
                        );
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    default:
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                }
            }
        });
    }
}
