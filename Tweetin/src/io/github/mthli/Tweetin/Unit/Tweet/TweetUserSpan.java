package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.R;

public class TweetUserSpan extends ClickableSpan {
    private Activity activity;
    private String user;

    public TweetUserSpan(Activity activity, String user) {
        this.activity = activity;
        this.user = user;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(false);
        textPaint.setColor(
                activity
                        .getResources()
                        .getColor(R.color.secondary_text)
        );
        textPaint.setTypeface(
                Typeface.create(
                        Typeface.SANS_SERIF,
                        Typeface.ITALIC
                )
        );
    }

    @Override
    public void onClick(View view) {
        /* Do something */
    }
}
