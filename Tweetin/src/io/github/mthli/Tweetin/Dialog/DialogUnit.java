package io.github.mthli.Tweetin.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetUnit;

import java.util.ArrayList;
import java.util.List;

public class DialogUnit {
    public static void show(final Context context, final Tweet tweet) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog, null);

        ListView listView = (ListView) linearLayout.findViewById(R.id.dialog_listview);

        final List<String> stringList = new ArrayList<String>();

        stringList.add(context.getString(R.string.dialog_item_detail));
        stringList.add(context.getString(R.string.dialog_item_share));
        stringList.add(context.getString(R.string.dialog_item_copy));

        DialogAdapter dialogAdapter = new DialogAdapter(context, R.layout.dialog_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(linearLayout);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final TweetUnit tweetUnit = new TweetUnit(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String text = stringList.get(position);
                if (text.equals(context.getString(R.string.dialog_item_detail))) {
                    // TODO
                } else if (text.equals(context.getString(R.string.dialog_item_share))) {
                    tweetUnit.share(tweet);
                } else if (text.equals(context.getString(R.string.dialog_item_copy))) {
                    tweetUnit.copy(tweet);
                }
                alertDialog.hide();
                alertDialog.dismiss();
            }
        });
    }

    private static void detail(Context context, Tweet tweet) {

    }
}
