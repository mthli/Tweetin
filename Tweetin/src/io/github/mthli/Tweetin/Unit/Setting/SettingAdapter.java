package io.github.mthli.Tweetin.Unit.Setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import io.github.mthli.Tweetin.R;

import java.util.List;

public class SettingAdapter extends ArrayAdapter<SettingItem> {

    private Activity activity;
    private int layoutResId;
    private List<SettingItem> settingItemList;

    public SettingAdapter(
            Activity activity,
            int layoutResId,
            List<SettingItem> settingItemList
    ) {
        super(activity, layoutResId, settingItemList);

        this.activity = activity;
        this.layoutResId = layoutResId;
        this.settingItemList = settingItemList;
    }

    private class Holder {
        TextView title;
        TextView content;
        CheckBox checkBox;
    }

    @Override
    public View getView(
            final int position,
            final View convertView,
            ViewGroup viewGroup
    ) {
        final Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.setting_fragment_item_title);
            holder.content = (TextView) view.findViewById(R.id.setting_fragment_item_content);
            holder.checkBox = (CheckBox) view.findViewById(R.id.setting_fragment_item_check);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        final SettingItem item = settingItemList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        if (item.isShowCheckBox()) {
            holder.checkBox.setChecked(item.isChecked());
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setChecked(false);
            holder.checkBox.setVisibility(View.GONE);
        }

        /* Do something */
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences(
                        activity.getString(R.string.sp_name),
                        Context.MODE_PRIVATE
                );
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (b) {
                    if (item.getTitle().equals(activity.getString(R.string.setting_title_detail))) {
                        editor.putBoolean(
                                activity.getString(R.string.sp_is_tweet_with_detail),
                                true
                        ).commit();
                    }
                } else {
                    if (item.getTitle().equals(activity.getString(R.string.setting_title_detail))) {
                        editor.putBoolean(
                                activity.getString(R.string.sp_is_tweet_with_detail),
                                false
                        ).commit();
                    }
                }
            }
        });

        return view;
    }
}
