package io.github.mthli.Tweetin.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.Holder;
import io.github.mthli.Tweetin.Tweet.Tweet;

import java.util.ArrayList;
import java.util.List;

public class DialogUnit {
    public static void showContextDialog(Activity activity, Holder holder, Tweet tweet) {
        LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.context_dialog, null);

        ListView listView = (ListView) linearLayout.findViewById(R.id.context_dialog_listview);

        List<String> list = new ArrayList<String>();

        if (activity instanceof MainActivity) {
            list.add(activity.getString(R.string.dialog_item_detail));
        }
        if (holder.bitmap != null) {
            list.add(activity.getString(R.string.dialog_item_save_picture));
        }
        list.add(activity.getString(R.string.dialog_item_share));
        list.add(activity.getString(R.string.dialog_item_copy));

        ContextDialogAdapter contextDialogAdapter = new ContextDialogAdapter(activity, R.layout.context_dialog_item, list);
        listView.setAdapter(contextDialogAdapter);
        contextDialogAdapter.notifyDataSetChanged();

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

    private static void detail(Activity activity, Tweet tweet) {

    }

    private static void savePicture(Activity activity, Holder holder) {

    }

    private static void share(Activity activity, Holder holder, Tweet tweet) {

    }

    private static void copy(Activity activity, Tweet tweet) {

    }

    public static void showUserDialog(Activity activity, String screenName) {

    }
}
