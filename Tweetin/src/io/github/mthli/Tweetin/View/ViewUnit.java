package io.github.mthli.Tweetin.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import io.github.mthli.Tweetin.Flag.Flag;
import io.github.mthli.Tweetin.R;

public class ViewUnit {
    public static void setCustomTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getString(R.string.sp_tweetin),
                Context.MODE_PRIVATE
        );

        int spColorValue = sharedPreferences.getInt(
                activity.getString(R.string.sp_color),
                0
        );

        switch (spColorValue) {
            case Flag.COLOR_BLUE:
                activity.setTheme(R.style.BaseAppTheme_Blue);
                break;
            case Flag.COLOR_ORANGE:
                activity.setTheme(R.style.BaseAppTheme_Orange);
                break;
            case Flag.COLOR_PINK:
                activity.setTheme(R.style.BaseAppTheme_Pink);
                break;
            case Flag.COLOR_PURPLE:
                activity.setTheme(R.style.BaseAppTheme_Purple);
                break;
            case Flag.COLOR_TEAL:
                activity.setTheme(R.style.BaseAppTheme_Teal);
                break;
            default:
                activity.setTheme(R.style.BaseAppTheme_Blue);
                break;
        }
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
