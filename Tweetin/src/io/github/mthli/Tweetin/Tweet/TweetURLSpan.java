package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.R;

public class TweetURLSpan extends ClickableSpan {
    private Activity activity;
    private String url;

    public TweetURLSpan(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);

        textPaint.setUnderlineText(false);
        textPaint.setColor(activity.getResources().getColor(R.color.secondary_text));
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    @Override
    public void onClick(View view) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
