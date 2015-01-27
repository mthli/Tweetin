package io.github.mthli.Tweetin.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.*;
import android.widget.*;
import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends FragmentActivity {
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

    private Toolbar toolbar;

    private SettingAdapter settingAdapter;
    private List<SettingItem> settingItemList = new ArrayList<SettingItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.setting);

        setTaskDescription(
                new ActivityManager.TaskDescription(
                        getString(R.string.app_name),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                        ViewUnit.getCustomThemeColorValue(this)
                )
        );

        toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(getString(R.string.setting_label));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.setting_listview);

        settingAdapter = new SettingAdapter(this, R.layout.setting_item, settingItemList);
        listView.setAdapter(settingAdapter);
        settingAdapter.notifyDataSetChanged();

        SettingItem theme = new SettingItem();
        theme.setTitle(getString(R.string.setting_theme));
        theme.setContent(ViewUnit.getCustomThemeColorName(this));
        settingItemList.add(theme);

        SettingItem homepage = new SettingItem();
        homepage.setTitle(getString(R.string.setting_homepage));
        homepage.setContent(getString(R.string.app_homepage));
        settingItemList.add(homepage);

        SettingItem author = new SettingItem();
        author.setTitle(getString(R.string.setting_author));
        author.setContent(getString(R.string.app_author_email));
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
                        showColorPicker();
                        break;
                    case 1:
                        Intent toHomepage = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_homepage)));
                        startActivity(toHomepage);
                        break;
                    case 2:
                        Intent toEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.app_author_email)));
                        startActivity(toEmail);
                        break;
                    case 3:
                        Intent toAdvice = new Intent(SettingActivity.this, PostActivity.class);
                        toAdvice.putExtra(getString(R.string.post_intent_post_flag), FlagUnit.POST_ADVICE);
                        startActivity(toAdvice);
                        break;
                    case 4:
                        signOut();
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void showColorPicker() {
        final int[] colors = new int[5];
        colors[0] = getResources().getColor(R.color.blue_500);
        colors[1] = getResources().getColor(R.color.orange_500);
        colors[2] = getResources().getColor(R.color.pink_500);
        colors[3] = getResources().getColor(R.color.purple_500);
        colors[4] = getResources().getColor(R.color.teal_500);

        ColorPickerDialog dialog = new ColorPickerDialog();

        dialog.initialize(
                R.string.color_picker_default_title,
                colors,
                ViewUnit.getCustomThemeColorValue(this),
                3,
                2
        );

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                if (color == colors[0]) {
                    setCustomTheme(FlagUnit.COLOR_BLUE);
                    return;
                }

                if (color == colors[1]) {
                    setCustomTheme(FlagUnit.COLOR_ORANGE);
                    return;
                }

                if (color == colors[2]) {
                    setCustomTheme(FlagUnit.COLOR_PINK);
                    return;
                }

                if (color == colors[3]) {
                    setCustomTheme(FlagUnit.COLOR_PURPLE);
                    return;
                }

                if (color == colors[4]) {
                    setCustomTheme(FlagUnit.COLOR_TEAL);
                    return;
                }
            }
        });

        dialog.show(getSupportFragmentManager(), getString(R.string.color_picker_tag));
    }

    private void setCustomTheme(int flag) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sp_tweetin), MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.sp_color), flag).commit();

        settingItemList.get(0).setContent(ViewUnit.getCustomThemeColorName(this));
        settingAdapter.notifyDataSetChanged();

        switch (flag) {
            case FlagUnit.COLOR_BLUE:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue_500));
                break;
            case FlagUnit.COLOR_ORANGE:
                getWindow().setStatusBarColor(getResources().getColor(R.color.orange_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.orange_500));
                break;
            case FlagUnit.COLOR_PINK:
                getWindow().setStatusBarColor(getResources().getColor(R.color.pink_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink_500));
                break;
            case FlagUnit.COLOR_PURPLE:
                getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple_500));
                break;
            case FlagUnit.COLOR_TEAL:
                getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.teal_500));
                break;
            default:
                getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue_500));
                break;
        }
    }

    private void signOut() {
        getSharedPreferences(getString(R.string.sp_tweetin), MODE_PRIVATE).edit().clear().commit();

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
