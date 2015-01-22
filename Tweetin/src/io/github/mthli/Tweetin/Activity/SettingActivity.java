package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.*;
import android.widget.*;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {
    private class SettingItem {
        private String title;
        private String content;

        public SettingItem() {
            this.title = null;
            this.content = null;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
    }

    private class SettingHolder {
        protected TextView title;
        protected TextView content;
    }

    private class SettingAdapter extends ArrayAdapter<SettingItem> {
        private Context context;
        private int layoutResId;
        private List<SettingItem> list;

        public SettingAdapter(Context context, int layoutResId, List<SettingItem> list) {
            super(context, layoutResId, list);
            this.context = context;
            this.layoutResId = layoutResId;
            this.list = list;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            SettingHolder holder;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(layoutResId, viewGroup, false);
                holder = new SettingHolder();

                holder.title = (TextView) view.findViewById(R.id.setting_item_title);
                holder.content = (TextView) view.findViewById(R.id.setting_item_content);

                view.setTag(holder);
            } else {
                holder = (SettingHolder) view.getTag();
            }

            SettingItem item = list.get(position);
            holder.title.setText(item.getTitle());
            holder.content.setText(item.getContent());

            return view;
        }
    }

    private ListView listView;
    private SettingAdapter settingAdapter;
    private List<SettingItem> settingItemList = new ArrayList<SettingItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.setting_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.setting_listview);

        settingAdapter = new SettingAdapter(this, R.layout.setting_item, settingItemList);
        listView.setAdapter(settingAdapter);
        settingAdapter.notifyDataSetChanged();

        SettingItem theme = new SettingItem();
        theme.setTitle(getString(R.string.setting_theme));
        theme.setContent(ViewUnit.getCustomThemeColorName(this));
        settingItemList.add(theme);

        SettingItem homepage = new SettingItem();
        homepage.setTitle(getString(R.string.setting_homepage));
        homepage.setContent(getString(R.string.setting_homepage_content));
        settingItemList.add(homepage);

        SettingItem author = new SettingItem();
        author.setTitle(getString(R.string.setting_author));
        author.setContent(getString(R.string.setting_author_content));
        settingItemList.add(author);

        SettingItem advice = new SettingItem();
        advice.setTitle(getString(R.string.setting_advice));
        advice.setContent(getString(R.string.setting_advice_content));
        settingItemList.add(advice);

        SettingItem signOut = new SettingItem();
        signOut.setTitle(getString(R.string.setting_sign_out));
        signOut.setContent(getString(R.string.setting_sign_out_content));
        settingItemList.add(signOut);

        settingAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // TODO
                        break;
                    case 1:
                        // TODO
                        break;
                    case 2:
                        // TODO
                        break;
                    case 3:
                        // TODO
                        break;
                    case 4:
                        // TODO
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // TODO
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
