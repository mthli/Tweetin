package io.github.mthli.Tweetin.Unit.ContextMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.DetailActivity;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Unit.CancelTask;
import io.github.mthli.Tweetin.Task.Unit.DeleteTask;
import io.github.mthli.Tweetin.Task.Unit.FavoriteTask;
import io.github.mthli.Tweetin.Task.Unit.RetweetTask;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import io.github.mthli.Tweetin.Unit.Tweet.TweetUnit;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuUnit {

    public static void reply(
            Activity activity,
            List<Tweet> tweetList,
            int location
    ) {
        Intent intent = new Intent(activity, PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                activity.getString(R.string.post_intent_flag),
                Flag.POST_REPLY
        );
        intent.putExtra(
                activity.getString(R.string.post_intent_status_id),
                tweetList.get(location).getStatusId()
        );
        intent.putExtra(
                activity.getString(R.string.post_intent_status_screen_name),
                tweetList.get(location).getScreenName()
        );
        activity.startActivity(intent);
        anim.fade(activity);
    }

    public static void quote(
            Activity activity,
            List<Tweet> tweetList,
            int location
    ) {
        Intent intent = new Intent(activity, PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                activity.getString(R.string.post_intent_flag),
                Flag.POST_QUOTE
        );
        intent.putExtra(
                activity.getString(R.string.post_intent_status_id),
                tweetList.get(location).getStatusId()
        );
        intent.putExtra(
                activity.getString(R.string.post_intent_status_screen_name),
                tweetList.get(location).getScreenName()
        );
        intent.putExtra(
                activity.getString(R.string.post_intent_status_text),
                tweetList.get(location).getText()
        );
        activity.startActivity(intent);
        anim.fade(activity);
    }

    public static void clip(
            Activity activity,
            List<Tweet> tweetList,
            int location
    ) {
        ClipboardManager manager = (ClipboardManager) activity
                .getSystemService(Context.CLIPBOARD_SERVICE);
        String text = tweetList.get(location).getText();
        ClipData data = ClipData.newPlainText(
                activity.getString(R.string.tweet_copy_label),
                text
        );
        manager.setPrimaryClip(data);
        Toast.makeText(
                activity,
                R.string.tweet_notification_copy_successful,
                Toast.LENGTH_SHORT
        ).show();
    }

    public static void detele(
            Activity activity,
            Twitter twitter,
            TweetAdapter tweetAdapter,
            List<Tweet> tweetList,
            int position
    ) {
        DeleteTask deleteTask = new DeleteTask(
                activity,
                twitter,
                tweetAdapter,
                tweetList,
                position
        );
        if (activity instanceof MainActivity) {
            int flag = ((MainActivity) activity).fragmentFlag;
            switch (flag) {
                case Flag.IN_TIMELINE_FRAGMENT:
                    ((MainActivity) activity).getTimelineFragment()
                            .setDeleteTask(deleteTask);
                    deleteTask.execute();
                    break;
                case Flag.IN_MENTION_FRAGMENT:
                    ((MainActivity) activity).getMentionFragment()
                            .setDeleteTask(deleteTask);
                    deleteTask.execute();
                    break;
                case Flag.IN_FAVORITE_FRAGMENT:
                    ((MainActivity) activity).getFavoriteFragment()
                            .setDeleteTask(deleteTask);
                    deleteTask.execute();
                    break;
                case Flag.IN_DISCOVERY_FRAGMENT:
                    ((MainActivity) activity).getDiscoveryFragment()
                            .setDeleteTask(deleteTask);
                    deleteTask.execute();
                    break;
                default:
                    break;
            }
        } else if (activity instanceof DetailActivity) {
            ((DetailActivity) activity).setDeleteTask(deleteTask);
            deleteTask.execute();
        }
    }
    
    public static void retweet(
            Activity activity,
            Twitter twitter,
            TweetAdapter tweetAdapter,
            List<Tweet> tweetList,
            int position
    ) {
        RetweetTask retweetTask = new RetweetTask(
                activity,
                twitter,
                tweetAdapter,
                tweetList,
                position
        );
        if (activity instanceof MainActivity) {
            int flag = ((MainActivity) activity).fragmentFlag;
            switch (flag) {
                case Flag.IN_TIMELINE_FRAGMENT:
                    ((MainActivity) activity).getTimelineFragment()
                            .setRetweetTask(retweetTask);
                    retweetTask.execute();
                    break;
                case Flag.IN_MENTION_FRAGMENT:
                    ((MainActivity) activity).getMentionFragment()
                            .setRetweetTask(retweetTask);
                    retweetTask.execute();
                    break;
                case Flag.IN_FAVORITE_FRAGMENT:
                    ((MainActivity) activity).getFavoriteFragment()
                            .setRetweetTask(retweetTask);
                    retweetTask.execute();
                    break;
                case Flag.IN_DISCOVERY_FRAGMENT:
                    ((MainActivity) activity).getDiscoveryFragment()
                            .setRetweetTask(retweetTask);
                    retweetTask.execute();
                    break;
                default:
                    break;
            }
        } else if (activity instanceof DetailActivity) {
            ((DetailActivity) activity).setRetweetTask(retweetTask);
            retweetTask.execute();
        }
    }
    
    public static void favorite(
            Activity activity,
            Twitter twitter,
            TweetAdapter tweetAdapter,
            List<Tweet> tweetList,
            int position
    ) {
        FavoriteTask favoriteTask = new FavoriteTask(
                activity,
                twitter,
                tweetAdapter,
                tweetList,
                position
        );
        if (activity instanceof MainActivity) {
            int flag = ((MainActivity) activity).fragmentFlag;
            switch (flag) {
                case Flag.IN_TIMELINE_FRAGMENT:
                    ((MainActivity) activity).getTimelineFragment()
                            .setFavoriteTask(favoriteTask);
                    favoriteTask.execute();
                    break;
                case Flag.IN_MENTION_FRAGMENT:
                    ((MainActivity) activity).getMentionFragment()
                            .setFavoriteTask(favoriteTask);
                    favoriteTask.execute();
                    break;
                case Flag.IN_DISCOVERY_FRAGMENT:
                    ((MainActivity) activity).getDiscoveryFragment()
                            .setFavoriteTask(favoriteTask);
                    favoriteTask.execute();
                    break;
                default:
                    break;
            }
        } else if (activity instanceof DetailActivity) {
            ((DetailActivity) activity).setFavoriteTask(favoriteTask);
            favoriteTask.execute();
        }
    }
    
    public static void cancel(
            Activity activity,
            Twitter twitter,
            TweetAdapter tweetAdapter,
            List<Tweet> tweetList,
            int position
    ) {
        CancelTask cancelTask = new CancelTask(
                activity,
                twitter,
                tweetAdapter,
                tweetList,
                position
        );
        if (activity instanceof MainActivity) {
            int flag = ((MainActivity) activity).fragmentFlag;
            switch (flag) {
                case Flag.IN_TIMELINE_FRAGMENT:
                    ((MainActivity) activity).getTimelineFragment()
                            .setCancelTask(cancelTask);
                    cancelTask.execute();
                    break;
                case Flag.IN_MENTION_FRAGMENT:
                    ((MainActivity) activity).getMentionFragment()
                            .setCancelTask(cancelTask);
                    cancelTask.execute();
                    break;
                case Flag.IN_FAVORITE_FRAGMENT:
                    ((MainActivity) activity).getFavoriteFragment()
                            .setCancelTask(cancelTask);
                    cancelTask.execute();
                    break;
                case Flag.IN_DISCOVERY_FRAGMENT:
                    ((MainActivity) activity).getDiscoveryFragment()
                            .setCancelTask(cancelTask);
                    cancelTask.execute();
                    break;
                default:
                    break;
            }
        } else if (activity instanceof DetailActivity) {
            ((DetailActivity) activity).setCancelTask(cancelTask);
            cancelTask.execute();
        }
    }

    public static void show(
            final Activity activity,
            final Twitter twitter,
            long useId,
            final TweetAdapter tweetAdapter,
            final List<Tweet> tweetList,
            final int location
    ) {
        LinearLayout linearLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(
                        R.layout.context_menu,
                        null
                );
        ListView contextMenu = (ListView) linearLayout.findViewById(R.id.context_menu_listview);

        final List<ContextMenuItem> contextMenuItemList = new ArrayList<ContextMenuItem>();
        final Tweet tweet = tweetList.get(location);
        /* Reply */
        contextMenuItemList.add(
                new ContextMenuItem(
                        activity.getResources().getDrawable(R.drawable.ic_context_menu_item_reply),
                        activity.getString(R.string.context_menu_item_reply),
                        Flag.CONTEXT_MENU_ITEM_REPLY,
                        true
                )
        );
        /* Quote */
        contextMenuItemList.add(
                new ContextMenuItem(
                        activity.getResources().getDrawable(R.drawable.ic_context_menu_item_quote),
                        activity.getString(R.string.context_menu_item_quote),
                        Flag.CONTEXT_MENU_ITEM_QUOTE,
                        true
                )
        );
        /* Retweet and delete */
        if (tweet.getRetweetedByUserId() != -1l && tweet.getRetweetedByUserId() == useId) {
            contextMenuItemList.add(
                    new ContextMenuItem(
                            activity.getResources().getDrawable(R.drawable.ic_context_menu_item_retweet),
                            activity.getString(R.string.context_menu_item_retweet),
                            Flag.CONTEXT_MENU_ITEM_RETWEET,
                            false
                    )
            );
        } else {
            if (tweet.getUserId() != useId) {
                contextMenuItemList.add(
                        new ContextMenuItem(
                                activity.getResources().getDrawable(R.drawable.ic_context_menu_item_retweet),
                                activity.getString(R.string.context_menu_item_retweet),
                                Flag.CONTEXT_MENU_ITEM_RETWEET,
                                true
                        )
                );
            } else {
                contextMenuItemList.add(
                        new ContextMenuItem(
                                activity.getResources().getDrawable(R.drawable.ic_context_menu_item_delete),
                                activity.getString(R.string.context_menu_item_delete),
                                Flag.CONTEXT_MENU_ITEM_DELETE,
                                true
                        )
                );
            }
        }
        /* Favorite and cancel favorite */
        if (!tweet.isFavorite()) {
            contextMenuItemList.add(
                    new ContextMenuItem(
                            activity.getResources().getDrawable(R.drawable.ic_context_menu_item_favorite),
                            activity.getString(R.string.context_menu_item_favorite),
                            Flag.CONTEXT_MENU_ITEM_FAVORITE,
                            true
                    )
            );
        } else {
            contextMenuItemList.add(
                    new ContextMenuItem(
                            activity.getResources().getDrawable(R.drawable.ic_context_menu_item_cancel),
                            activity.getString(R.string.context_menu_item_cancel),
                            Flag.CONTEXT_MENU_ITEM_CANCEL,
                            true
                    )
            );
        }
        /* Detail */
        if (!(activity instanceof DetailActivity)) {
            contextMenuItemList.add(
                    new ContextMenuItem(
                            activity.getResources().getDrawable(R.drawable.ic_context_menu_item_detail),
                            activity.getString(R.string.context_menu_item_detail),
                            Flag.CONTEXT_MENU_ITEM_DETAIL,
                            true
                    )
            );
        }
        /* Copy */
        contextMenuItemList.add(
                new ContextMenuItem(
                        activity.getResources().getDrawable(R.drawable.ic_context_menu_item_copy),
                        activity.getString(R.string.context_menu_item_copy),
                        Flag.CONTEXT_MENU_ITEM_COPY,
                        true
                )
        );

        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(
                activity,
                R.layout.context_menu_item,
                contextMenuItemList
        );
        contextMenu.setAdapter(contextMenuAdapter);
        contextMenuAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        contextMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ContextMenuItem contextMenuItem = contextMenuItemList.get(position);
                switch (contextMenuItem.getFlag()) {
                    case Flag.CONTEXT_MENU_ITEM_REPLY:
                        reply(activity, tweetList, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_DELETE:
                        detele(activity, twitter, tweetAdapter, tweetList, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_RETWEET:
                        if (contextMenuItem.isActive()) {
                            retweet(activity, twitter, tweetAdapter, tweetList, location);
                        } else {
                            Toast.makeText(
                                    activity,
                                    R.string.context_menu_toast_already_retweet,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_QUOTE:
                        quote(activity, tweetList, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_FAVORITE:
                        favorite(activity, twitter, tweetAdapter, tweetList, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_CANCEL:
                        cancel(activity, twitter, tweetAdapter, tweetList, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_DETAIL:
                        TweetUnit.tweetToDetailActivity(
                                activity,
                                tweetList,
                                position
                        );
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case Flag.CONTEXT_MENU_ITEM_COPY:
                        clip(activity, tweetList, location);
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
