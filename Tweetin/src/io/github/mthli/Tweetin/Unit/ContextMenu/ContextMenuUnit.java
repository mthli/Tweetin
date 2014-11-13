package io.github.mthli.Tweetin.Unit.ContextMenu;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;

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

}
