package io.github.mthli.Tweetin.Custom;

import android.content.Context;
import android.util.TypedValue;
import io.github.mthli.Tweetin.R;

public class ViewUnit {

    public static float getElevation(Context context, int degree) {
        return context.getResources().getDisplayMetrics().density * degree;
    }

    public static int getToolbarSize(Context context) {
        TypedValue typedValue = new TypedValue();

        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }

        return 0;
    }

    public static int getTabHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.tab_height);
    }
}
