package io.github.mthli.Tweetin.Fragment;

import android.app.AlertDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Discovery.DiscoveryInitTask;
import io.github.mthli.Tweetin.Task.Unit.DeleteTask;
import io.github.mthli.Tweetin.Task.Unit.FavoriteTask;
import io.github.mthli.Tweetin.Task.Unit.RetweetTask;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuUnit;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryFragment extends ProgressFragment {

    private View view;

    private int refreshFlag = Flag.DISCOVERY_TASK_IDLE;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
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

    private EditText searchBox;
    private TextView introduction;
    private CircularProgressBar progressBar;
    private ListView listView;
    public EditText getSearchBox() {
        return searchBox;
    }
    public TextView getIntroduction() {
        return introduction;
    }
    public CircularProgressBar getProgressBar() {
        return progressBar;
    }
    public ListView getListView() {
        return listView;
    }

    private DiscoveryInitTask discoveryInitTask;
    private DeleteTask deleteTask;
    private RetweetTask retweetTask;
    private FavoriteTask favoriteTask;
    public boolean isSomeTaskRunning() {
        if (discoveryInitTask != null && discoveryInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (discoveryInitTask != null && discoveryInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            discoveryInitTask.cancel(true);
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
        setContentView(R.layout.discovery_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        twitter = TweetUnit.getTwitterFromSharedPreferences(getActivity());
        useId = TweetUnit.getUseIdFromeSharedPreferences(getActivity());

        searchBox = (EditText) view.findViewById(
                R.id.discovery_fragment_search_box
        );
        introduction = (TextView) view.findViewById(
                R.id.discovery_fragment_introduction
        );
        progressBar = (CircularProgressBar) view.findViewById(
                R.id.discovery_fragment_progress_bar
        );
        listView = (ListView) view.findViewById(
                R.id.discovery_fragment_listview
        );

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String queryStr = searchBox.getText().toString();
                    if (queryStr.length() > 0) {
                        ((InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                        discoveryInitTask = new DiscoveryInitTask(DiscoveryFragment.this);
                        discoveryInitTask.execute();
                    } else {
                        Toast.makeText(
                                view.getContext(),
                                R.string.discovery_toast_please_input_something,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
                return false;
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        tweetWithDetail = sharedPreferences.getBoolean(
                getString(R.string.sp_is_tweet_with_detail),
                false
        );
        tweetAdapter = new TweetAdapter(
                getActivity(),
                R.layout.tweet,
                tweetList,
                tweetWithDetail
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TweetUnit.tweetToDetailActivity(
                        getActivity(),
                        tweetList,
                        position
                );
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showItemLongClickDialog(position);
                return true;
            }
        });
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
