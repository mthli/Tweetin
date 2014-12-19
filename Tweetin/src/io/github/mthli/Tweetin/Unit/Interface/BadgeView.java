package io.github.mthli.Tweetin.Unit.Interface;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.github.mthli.Tweetin.R;

public class BadgeView extends RelativeLayout {

    private Context context;

    private TextView textView;
    private View bubble;

    public BadgeView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(
                R.layout.badge_view,
                this,
                true
        );

        this.context = context;

        this.textView = (TextView) findViewById(R.id.badge_view_textview);
        this.bubble = findViewById(R.id.badge_view_bubble);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setCustomTheme(ColorStateList colorStateList) {
        textView.setTextColor(colorStateList);
    }

    public void showBubble(boolean show) {
        if (show) {
            bubble.setVisibility(VISIBLE);
        } else {
            bubble.setVisibility(GONE);
        }
    }
}
