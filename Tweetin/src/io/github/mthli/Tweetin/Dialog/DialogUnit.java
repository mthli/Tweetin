package io.github.mthli.Tweetin.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import io.github.mthli.Tweetin.Activity.InReplyToActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetUnit;

import java.util.ArrayList;
import java.util.List;

public class DialogUnit {
    public static void show(final Activity activity, final Tweet tweet) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.dialog, null);

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);

        final List<String> stringList = new ArrayList<String>();

        if (!(activity instanceof InReplyToActivity)) {
            stringList.add(activity.getString(R.string.dialog_item_in_reply_to));
        }
        stringList.add(activity.getString(R.string.dialog_item_share));
        stringList.add(activity.getString(R.string.dialog_item_copy));

        DialogAdapter dialogAdapter = new DialogAdapter(activity, R.layout.dialog_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final TweetUnit tweetUnit = new TweetUnit(activity);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String text = stringList.get(position);
                if (text.equals(activity.getString(R.string.dialog_item_in_reply_to))) {
                    Intent intent = tweetUnit.getIntentFromTweet(tweet, InReplyToActivity.class);
                    activity.startActivity(intent);
                    // TODO: startActivityForResult();
                } else if (text.equals(activity.getString(R.string.dialog_item_share))) {
                    tweetUnit.share(tweet);
                } else if (text.equals(activity.getString(R.string.dialog_item_copy))) {
                    tweetUnit.copy(tweet);
                }
                alertDialog.hide();
                alertDialog.dismiss();
            }
        });
    }
}
