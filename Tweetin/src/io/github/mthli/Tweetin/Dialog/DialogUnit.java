package io.github.mthli.Tweetin.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Holder;
import io.github.mthli.Tweetin.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class DialogUnit {
    public static void show(Activity activity, Holder holder, Tweet tweet) {
        LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog, null);

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);

        List<String> list = new ArrayList<String>();

        list.add(activity.getString(R.string.tweet_more_detail));
        if (holder.bitmap != null) {
            list.add(activity.getString(R.string.tweet_more_save_picture));
        }
        list.add(activity.getString(R.string.tweet_more_share));

        DialogAdapter dialogAdapter = new DialogAdapter(activity, R.layout.dialog_item, list);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /* Do something */
            }
        });
    }

    public static void detail(Activity activity, Tweet tweet) {

    }

    public static void savePicture(Activity activity, Holder holder) {

    }

    public static void share(Activity activity, Tweet tweet) {

    }
}
