package io.github.mthli.Tweetin.Main;

import android.app.AlertDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.Detail.DetailActivity;
import io.github.mthli.Tweetin.Post.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.*;
import io.github.mthli.Tweetin.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends ProgressFragment {

    private View view;

    private long useId = 0;
    public long getUseId() {
        return useId;
    }

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
    }

    private void reply(int location) {
        Intent intent = new Intent(view.getContext(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                view.getContext().getString(R.string.post_flag),
                Flag.POST_REPLY
        );
        intent.putExtra(
                view.getContext().getString(R.string.post_reply_status_id),
                tweetList.get(location).getTweetId()
        );
        intent.putExtra(
                view.getContext().getString(R.string.post_reply_screen_name),
                tweetList.get(location).getScreenName()
        );
        startActivity(intent);
        anim.fade(getActivity());
    }
    private void quote(int location) {
        Intent intent = new Intent(view.getContext(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                view.getContext().getString(R.string.post_flag),
                Flag.POST_RETWEET_QUOTE
        );
        intent.putExtra(
                view.getContext().getString(R.string.post_quote_screen_name),
                tweetList.get(location).getScreenName()
        );
        intent.putExtra(
                view.getContext().getString(R.string.post_quote_text),
                tweetList.get(location).getText()
        );
        startActivity(intent);
        anim.fade(getActivity());
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
    private void showItemLongClickDialog(final int location) {
        LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(
                R.layout.context_menu,
                null
        );
        ListView menu = (ListView) layout.findViewById(R.id.context_menu);
        List<String> menuItem = new ArrayList<String>();

        final int flag;
        final Tweet tweet = tweetList.get(location);
        if (tweet.getRetweetedById() != 0 && tweet.getRetweetedById() == useId) {
            flag = Flag.TWEET_STATUS_BY_ME;
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_reply));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_quote));
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_copy));
        } else {
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_reply));
            if (tweet.getUserId() != useId) {
                flag = Flag.TWEET_STATUS_NONE;
                if (!tweet.isProtected()) {
                    menuItem.add(view.getContext().getString(R.string.tweet_menu_item_retweet));
                }
            } else {
                flag = Flag.TWEET_STATUS_BY_ME;
            }
            menuItem.add(view.getContext().getString(R.string.tweet_menu_item_quote));
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
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                ActivityAnim anim = new ActivityAnim();
                switch (flag) {
                    case Flag.TWEET_STATUS_BY_ME:
                        switch (position) {
                            case 0:
                                reply(location);
                                break;
                            case 1:
                                quote(location);
                                break;
                            case 2:
                                clip(location);
                                break;
                            default:
                                break;
                        }
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    case Flag.TWEET_STATUS_NONE:
                        if (tweet.isProtected()) {
                            switch (position) {
                                case 0:
                                    reply(location);
                                    break;
                                case 1:
                                    quote(location);
                                    break;
                                case 2:
                                    clip(location);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            switch (position) {
                                case 0:
                                    reply(location);
                                    break;
                                case 1:
                                    tweetRetweetTask = new TweetRetweetTask(
                                            MainFragment.this,
                                            location
                                    );
                                    tweetRetweetTask.execute();
                                    break;
                                case 2:
                                    quote(location);
                                    break;
                                case 3:
                                    clip(location);
                                    break;
                                default:
                                    break;
                            }
                        }
                        dialog.hide();
                        dialog.dismiss();
                        break;
                    default:
                        dialog.hide();
                        dialog.dismiss();
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                intent.putExtra(
                        view.getContext().getString(R.string.post_flag),
                        Flag.POST_ORIGINAL
                );
                ActivityAnim anim = new ActivityAnim();
                startActivity(intent);
                anim.fade(getActivity());
            }
        });

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
                ActivityAnim anim = new ActivityAnim();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(
                        getString(R.string.detail_intent_user_id),
                        tweetList.get(position).getUserId()
                );
                intent.putExtra(
                        view.getContext().getString(R.string.detail_intent_tweet_id),
                        tweetList.get(position).getTweetId()
                );
                intent.putExtra(
                        view.getContext().getString(R.string.detail_intent_reply_to),
                        tweetList.get(position).getReplyTo()
                );
                startActivity(intent);
                anim.rightIn(getActivity());
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
