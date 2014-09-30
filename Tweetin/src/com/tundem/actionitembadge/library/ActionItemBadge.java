package com.tundem.actionitembadge.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import io.github.mthli.Tweetin.R;

/**
 * Created by mikepenz on 23.07.14.
 */
public class ActionItemBadge {
    public enum BadgeStyle {
        BLUE(Style.DEFAULT, R.drawable.menu_badge_blue, R.layout.menu_badge),
        BLUE_LARGE(Style.LARGE, R.drawable.menu_badge_blue_large, R.layout.menu_badge_large);

        private Style style;
        private int drawable;
        private int layout;

        private BadgeStyle(Style style, int drawable, int layout) {
            this.style = style;
            this.drawable = drawable;
            this.layout = layout;
        }

        public Style getStyle() {
            return style;
        }

        public int getDrawable() {
            return drawable;
        }

        public int getLayout() {
            return layout;
        }


        public enum Style {
            DEFAULT(1),
            LARGE(2);

            private int style;

            private Style(int style) {
                this.style = style;
            }

            public int getStyle() {
                return style;
            }
        }
    }

    public static class Add {
        public Add() {

        }

        public Add(Activity activity, Menu menu, String title) {
            this.activity = activity;
            this.menu = menu;
            this.title = title;
        }

        private Activity activity;

        public Add act(Activity activity) {
            this.activity = activity;
            return this;
        }

        private Menu menu;

        public Add menu(Menu menu) {
            this.menu = menu;
            return this;
        }

        private String title;

        public Add title(String title) {
            this.title = title;
            return this;
        }

        public Add title(int resId) {
            if (activity == null) {
                throw new RuntimeException("Activity not set");
            }

            this.title = activity.getString(resId);
            return this;
        }

        private Integer groupId;
        private Integer itemId;
        private Integer order;

        public Add itemDetails(int groupId, int itemId, int order) {
            this.groupId = groupId;
            this.itemId = itemId;
            this.order = order;
            return this;
        }

        private Integer showAsAction;

        public Add showAsAction(int showAsAction) {
            this.showAsAction = showAsAction;
            return this;
        }

        public Menu build(int badgeCount) {
            return build((Drawable) null, BadgeStyle.BLUE_LARGE, badgeCount);
        }

        public Menu build(BadgeStyle style, int badgeCount) {
            return build((Drawable) null, style, badgeCount);
        }

        public Menu build(Iconify.IconValue icon, int badgeCount) {
            return build(new IconDrawable(activity, icon).colorRes(R.color.action_bar_text)
                    .actionBarSize(), BadgeStyle.BLUE, badgeCount);
        }

        public Menu build(Drawable icon, int badgeCount) {
            return build(icon, BadgeStyle.BLUE, badgeCount);
        }

        public Menu build(Iconify.IconValue icon, BadgeStyle style, int badgeCount) {
            return build(new IconDrawable(activity, icon).colorRes(R.color.action_bar_text)
                    .actionBarSize(), style, badgeCount);
        }

        public Menu build(Drawable icon, BadgeStyle style, int badgeCount) {
            MenuItem item;
            if (groupId != null && itemId != null && order != null) {
                item = menu.add(groupId, itemId, order, title);
            } else {
                item = menu.add(title);
            }

            if (showAsAction != null) {
                item.setShowAsAction(showAsAction);
            }

            item.setActionView(style.getLayout());
            update(activity, item, icon, style, badgeCount);
            return menu;
        }
    }

    public static void update(final Activity act, final MenuItem menu, int badgeCount) {
        update(act, menu, (Drawable) null, BadgeStyle.BLUE_LARGE, badgeCount);
    }

    public static void update(final Activity act, final MenuItem menu, BadgeStyle style, int badgeCount) {
        if (style.getStyle() != BadgeStyle.Style.LARGE) {
            throw new RuntimeException("You are not allowed to call update without an icon on a Badge with default style");
        }
        update(act, menu, (Drawable) null, style, badgeCount);
    }

    public static void update(final Activity act, final MenuItem menu, Iconify.IconValue icon, int badgeCount) {
        update(act, menu, new IconDrawable(act, icon).colorRes(R.color.action_bar_text)
                .actionBarSize(), BadgeStyle.BLUE, badgeCount);
    }

    public static void update(final Activity act, final MenuItem menu, Drawable icon, int badgeCount) {
        update(act, menu, icon, BadgeStyle.BLUE, badgeCount);
    }

    public static void update(final Activity act, final MenuItem menu, Iconify.IconValue icon, BadgeStyle style, int badgeCount) {
        update(act, menu, new IconDrawable(act, icon).colorRes(R.color.action_bar_text)
                .actionBarSize(), style, badgeCount);
    }

    public static void update(final Activity act, final MenuItem menu, Drawable icon, BadgeStyle style, int badgeCount) {
        View badge = menu.getActionView();

        if (style.getStyle() == BadgeStyle.Style.DEFAULT) {
            ImageView imageView = (ImageView) badge.findViewById(R.id.menu_badge_icon);
            ActionItemBadge.setBackground(imageView, icon);

            TextView textView = (TextView) badge.findViewById(R.id.menu_badge_text);
            if (badgeCount < 0) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(String.valueOf(badgeCount));
                textView.setBackgroundResource(style.getDrawable());
            }
        } else {
            Button button = (Button) badge.findViewById(R.id.menu_badge_button);
            button.setBackgroundResource(style.getDrawable());
            button.setText(String.valueOf(badgeCount));
        }

        badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.onOptionsItemSelected(menu);
            }
        });

        menu.setVisible(true);
    }

    public static void hide(MenuItem menu) {
        menu.setVisible(false);
    }

    @SuppressLint("NewApi")
    private static void setBackground(View v, Drawable d) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(d);
        } else {
            v.setBackground(d);
        }
    }
}
