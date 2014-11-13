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
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Discovery.DiscoveryDeleteTask;
import io.github.mthli.Tweetin.Task.Discovery.DiscoveryFavoriteTask;
import io.github.mthli.Tweetin.Task.Discovery.DiscoveryInitTask;
import io.github.mthli.Tweetin.Task.Discovery.DiscoveryRetweetTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

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

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
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
    private DiscoveryDeleteTask discoveryDeleteTask;
    private DiscoveryRetweetTask discoveryRetweetTask;
    private DiscoveryFavoriteTask discoveryFavoriteTask;
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
        if (discoveryDeleteTask != null && discoveryDeleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            discoveryDeleteTask.cancel(true);
        }
        if (discoveryRetweetTask != null && discoveryRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            discoveryRetweetTask.cancel(true);
        }
        if (discoveryFavoriteTask != null && discoveryFavoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            discoveryFavoriteTask.cancel(true);
        }
    }

    private void tweetToDetail(int position) {
        ActivityAnim anim = new ActivityAnim();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(
                getString(R.string.detail_intent_from_position),
                position
        );
        Tweet tweet = tweetList.get(position);
        intent.putExtra(
                getString(R.string.detail_intent_status_id),
                tweet.getStatusId()
        );
        intent.putExtra(
                getString(R.string.detail_intent_reply_to_status_id),
                tweet.getReplyToStatusId()
        );
        intent.putExtra(
                getString(R.string.detail_intent_user_id),
                tweet.getUserId()
        );
        intent.putExtra(
                getString(R.string.detail_intent_retweeted_by_user_id),
                tweet.getRetweetedByUserId()
        );
        intent.putExtra(
                getString(R.string.detail_intent_avatar_url),
                tweet.getAvatarURL()
        );
        intent.putExtra(
                getString(R.string.detail_intent_created_at),
                tweet.getCreatedAt()
        );
        intent.putExtra(
                getString(R.string.detail_intent_name),
                tweet.getName()
        );
        intent.putExtra(
                getString(R.string.detail_intent_screen_name),
                tweet.getScreenName()
        );
        intent.putExtra(
                getString(R.string.detail_intent_protect),
                tweet.isProtect()
        );
        intent.putExtra(
                getString(R.string.detail_intent_check_in),
                tweet.getCheckIn()
        );
        intent.putExtra(
                getString(R.string.detail_intent_photo_url),
                tweet.getPhotoURL()
        );
        intent.putExtra(
                getString(R.string.detail_intent_text),
                tweet.getText()
        );
        intent.putExtra(
                getString(R.string.detail_intent_retweet),
                tweet.isRetweet()
        );
        intent.putExtra(
                getString(R.string.detail_intent_retweeted_by_user_name),
                tweet.getRetweetedByUserName()
        );
        intent.putExtra(
                getString(R.string.detail_intent_favorite),
                tweet.isFavorite()
        );
        startActivityForResult(intent, 0);
        anim.rightIn(getActivity());
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.discovery_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1l
        );
        String consumerKey = sharedPreferences.getString(
                getString(R.string.sp_consumer_key),
                null
        );
        String consumerSecret = sharedPreferences.getString(
                getString(R.string.sp_consumer_secret),
                null
        );
        String accessToken = sharedPreferences.getString(
                getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                getString(R.string.sp_access_token_secret),
                null
        );
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

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

        tweetAdapter = new TweetAdapter(
                getActivity(),
                view.getContext(),
                R.layout.tweet,
                tweetList,
                false
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                tweetToDetail(position);
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
    private void reply(int loaction) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                getString(R.string.post_intent_flag),
                Flag.POST_REPLY
        );
        intent.putExtra(
                getString(R.string.post_intent_status_id),
                tweetList.get(loaction).getStatusId()
        );
        intent.putExtra(
                getString(R.string.post_intent_status_screen_name),
                tweetList.get(loaction).getScreenName()
        );
        startActivity(intent);
        anim.fade(getActivity());
    }
    private void quote(int location) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                getString(R.string.post_intent_flag),
                Flag.POST_QUOTE
        );
        intent.putExtra(
                getString(R.string.post_intent_status_id),
                tweetList.get(location).getStatusId()
        );
        intent.putExtra(
                getString(R.string.post_intent_status_screen_name),
                tweetList.get(location).getScreenName()
        );
        intent.putExtra(
                getString(R.string.post_intent_status_text),
                tweetList.get(location).getText()
        );
        startActivity(intent);
        anim.fade(getActivity());
    }
    private void clip(int location) {
        ClipboardManager manager = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        String text = tweetList.get(location).getText();
        ClipData data = ClipData.newPlainText(
                getString(R.string.tweet_copy_label),
                text
        );
        manager.setPrimaryClip(data);
        Toast.makeText(
                view.getContext(),
                R.string.tweet_notification_copy_successful,
                Toast.LENGTH_SHORT
        ).show();
    }
    private void multipleAtTwo(int flag, int location) {
        switch (flag) {
            case Flag.STATUS_NONE:
                discoveryRetweetTask = new DiscoveryRetweetTask(
                        DiscoveryFragment.this,
                        location
                );
                discoveryRetweetTask.execute();
                break;
            case Flag.STATUS_RETWEETED_BY_ME:
                Toast.makeText(
                        getActivity(),
                        R.string.context_toast_already_retweet,
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case Flag.STATUS_SENT_BY_ME:
                discoveryDeleteTask = new DiscoveryDeleteTask(
                        DiscoveryFragment.this,
                        location
                );
                discoveryDeleteTask.execute();
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
                        reply(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 1:
                        quote(location);
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
                            discoveryFavoriteTask = new DiscoveryFavoriteTask(
                                    DiscoveryFragment.this,
                                    location
                            );
                            discoveryFavoriteTask.execute();
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
                        clip(location);
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
