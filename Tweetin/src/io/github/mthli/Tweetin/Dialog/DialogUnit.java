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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        detail(context, tweet);
                        break;
                    case 1:
                        share(context, tweet);
                        break;
                    case 2:
                        copy(context, tweet);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static void detail(Context context, Tweet tweet) {

    }

    private static void share(Context context, Tweet tweet) {

    }

    private static void copy(Context context, Tweet tweet) {

    }
}
