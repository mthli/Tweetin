package io.github.mthli.Tweetin.Custom;

import android.content.Context;
import android.view.View;

public class ViewUnit {

    public static void setElevation(Context context, View view, int degree) {
        float elevation = context.getResources().getDisplayMetrics().density;

        view.setElevation(elevation * degree);
    }
}
