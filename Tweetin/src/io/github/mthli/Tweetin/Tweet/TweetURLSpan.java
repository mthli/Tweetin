package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.R;

public class TweetURLSpan extends ClickableSpan {
    private Context context;

    private String url;

    public TweetURLSpan(Context context, String url) {
        this.context = context;

        this.url = url;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);

        textPaint.setUnderlineText(false);
        textPaint.setColor(context.getResources().getColor(R.color.secondary_text));
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    @Override
    public void onClick(View view) {

    }
}
