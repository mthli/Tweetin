package io.github.mthli.Tweetin.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.R;

public class ViewUnit {
    public static void setCustomTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);

        int spColorValue = sharedPreferences.getInt(activity.getString(R.string.sp_color), 0);

        switch (spColorValue) {
            case FlagUnit.COLOR_BLUE:
                activity.setTheme(R.style.BaseAppTheme_Blue);
                break;
            case FlagUnit.COLOR_ORANGE:
                activity.setTheme(R.style.BaseAppTheme_Orange);
                break;
            case FlagUnit.COLOR_PINK:
                activity.setTheme(R.style.BaseAppTheme_Pink);
                break;
            case FlagUnit.COLOR_PURPLE:
                activity.setTheme(R.style.BaseAppTheme_Purple);
                break;
            case FlagUnit.COLOR_TEAL:
                activity.setTheme(R.style.BaseAppTheme_Teal);
                break;
            default:
                activity.setTheme(R.style.BaseAppTheme_Blue);
                break;
        }
    }

    public static int getCustomThemeColorValue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);

        int spColorValue = sharedPreferences.getInt(context.getString(R.string.sp_color), 0);

        switch (spColorValue) {
            case FlagUnit.COLOR_BLUE:
                return context.getResources().getColor(R.color.blue_500);
            case FlagUnit.COLOR_ORANGE:
                return context.getResources().getColor(R.color.orange_500);
            case FlagUnit.COLOR_PINK:
                return context.getResources().getColor(R.color.pink_500);
            case FlagUnit.COLOR_PURPLE:
                return context.getResources().getColor(R.color.purple_500);
            case FlagUnit.COLOR_TEAL:
                return context.getResources().getColor(R.color.teal_500);
            default:
                return context.getResources().getColor(R.color.blue_500);
        }
    }

    public static String getCustomThemeColorName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);

        int spColorValue = sharedPreferences.getInt(context.getString(R.string.sp_color), 0);

        switch (spColorValue) {
            case FlagUnit.COLOR_BLUE:
                return context.getString(R.string.setting_theme_blue);
            case FlagUnit.COLOR_ORANGE:
                return context.getString(R.string.setting_theme_orange);
            case FlagUnit.COLOR_PINK:
                return context.getString(R.string.setting_theme_pink);
            case FlagUnit.COLOR_PURPLE:
                return context.getString(R.string.setting_theme_purple);
            case FlagUnit.COLOR_TEAL:
                return context.getString(R.string.setting_theme_teal);
            default:
                return context.getString(R.string.setting_theme_blue);
        }
    }

    public static void setSwipeRefreshLayoutTheme(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sp_tweetin), Context.MODE_PRIVATE);

        int spColorValue = sharedPreferences.getInt(context.getString(R.string.sp_color), 0);

        switch (spColorValue) {
            case FlagUnit.COLOR_BLUE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.blue_700,
                        R.color.blue_500,
                        R.color.blue_700,
                        R.color.blue_500
                );
                break;
            case FlagUnit.COLOR_ORANGE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.orange_700,
                        R.color.orange_500,
                        R.color.orange_700,
                        R.color.orange_500
                );
                break;
            case FlagUnit.COLOR_PINK:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.pink_700,
                        R.color.pink_500,
                        R.color.pink_700,
                        R.color.pink_500
                );
                break;
            case FlagUnit.COLOR_PURPLE:
                swipeRefreshLayout.setColorSchemeResources(
                        R.color.purple_700,
                        R.color.purple_500,
                        R.color.purple_700,
                        R.color.purple_500
                );
                break;
            case FlagUnit.COLOR_TEAL:
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

    public static float getElevation(Context context, float degree) {
        return context.getResources().getDisplayMetrics().density * degree;
    }

    public static float getElevation(Context context, int degree) {
        return context.getResources().getDisplayMetrics().density * degree;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static int getToolbarHeight(Context context) {
        TypedValue typedValue = new TypedValue();

        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }

        return 0;
    }
}
