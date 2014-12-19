package io.github.mthli.Tweetin.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Activity.Flag;

public class FavoriteFragment extends ProgressFragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private View contentView;

    private SwipeRefreshLayout swipeRefreshLayout;
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private ListView listView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        editor = sharedPreferences.edit();
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.favorite_fragment);
        contentView = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        initUserInterface();
        setCustomTheme();
    }

    private void initUserInterface() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView
                .findViewById(R.id.favorite_swipe_container);

        swipeRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Do something */
            }
        });

        listView = (ListView) contentView.findViewById(R.id.favorite_fragment_listview);
    }

    private void setCustomTheme() {
        int spColorValue = sharedPreferences.getInt(
                getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.blue_700,
                        R.color.blue_500,
                        R.color.blue_700,
                        R.color.blue_500
                );
                break;
            case Flag.COLOR_ORANGE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.orange_700,
                        R.color.orange_500,
                        R.color.orange_700,
                        R.color.orange_500
                );
                break;
            case Flag.COLOR_PINK:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.pink_700,
                        R.color.pink_500,
                        R.color.pink_700,
                        R.color.pink_500
                );
                break;
            case Flag.COLOR_PURPLE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.purple_700,
                        R.color.purple_500,
                        R.color.purple_700,
                        R.color.purple_500
                );
                break;
            case Flag.COLOR_TEAL:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.teal_700,
                        R.color.teal_500,
                        R.color.teal_700,
                        R.color.teal_500
                );
                break;
            default:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.blue_700,
                        R.color.blue_500,
                        R.color.blue_700,
                        R.color.blue_500
                );
                break;
        }
    }
}
