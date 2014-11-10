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
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Favorite.*;
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

public class FavoriteFragment extends ProgressFragment {
    private View view;

    private int refreshFlag = Flag.FAVORITE_TASK_IDLE;
    private boolean moveToBottom = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private SharedPreferences sharedPreferences;
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private FavoriteInitTask favoriteInitTask;
    private FavoriteMoreTask favoriteMoreTask;
    private FavoriteDeleteTask favoriteDeleteTask;
    private FavoriteRetweetTask favoriteRetweetTask;
    private FavoriteCancelTask favoriteCancelTask;
    public boolean isSomeTaskRunning() {
        if (
                (favoriteInitTask != null && favoriteInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (favoriteMoreTask != null && favoriteMoreTask.getStatus() == AsyncTask.Status.RUNNING)

        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (favoriteInitTask != null && favoriteInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteInitTask.cancel(true);
        }
        if (favoriteMoreTask != null && favoriteMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteMoreTask.cancel(true);
        }
        if (favoriteDeleteTask != null && favoriteDeleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteDeleteTask.cancel(true);
        }
        if (favoriteRetweetTask != null && favoriteRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteRetweetTask.cancel(true);
        }
        if (favoriteCancelTask != null && favoriteCancelTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteCancelTask.cancel(true);
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
                getString(R.string.detail_intent_text),
                tweet.getText()
        );
        intent.putExtra(
                getString(R.string.detail_intent_photo_url),
                tweet.getPhotoURL()
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
        setContentView(R.layout.favorite_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1
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

        ListView listView = (ListView) view
                .findViewById(R.id.favorite_fragment_listview);
        tweetAdapter = new TweetAdapter(
                getActivity(),
                view.getContext(),
                R.layout.tweet,
                tweetList,
                false
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.favorite_swipe_container);
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
                favoriteInitTask = new FavoriteInitTask(
                        FavoriteFragment.this,
                        true
                );
                favoriteInitTask.execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tweetToDetail(position);
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
                /* Do nothing */
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

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && moveToBottom) {
                        favoriteMoreTask = new FavoriteMoreTask(FavoriteFragment.this);
                        favoriteMoreTask.execute();
                    }
                }
            }
        });

        favoriteInitTask = new FavoriteInitTask(
                FavoriteFragment.this,
                false
        );
        favoriteInitTask.execute();
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
                favoriteRetweetTask = new FavoriteRetweetTask(
                        FavoriteFragment.this,
                        location
                );
                favoriteRetweetTask.execute();
                break;
            case Flag.STATUS_RETWEETED_BY_ME:
                Toast.makeText(
                        getActivity(),
                        R.string.context_toast_already_retweet,
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case Flag.STATUS_SENT_BY_ME:
                favoriteDeleteTask = new FavoriteDeleteTask(
                        FavoriteFragment.this,
                        location
                );
                favoriteDeleteTask.execute();
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
        if (tweet.getRetweetedByUserId() != -1 && tweet.getRetweetedByUserId() == useId) {
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
        menuItemList.add(getString(R.string.context_menu_item_un_favorite));
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
                        favoriteCancelTask = new FavoriteCancelTask(
                                FavoriteFragment.this,
                                location
                        );
                        favoriteCancelTask.execute();
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
