package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;

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
        String[] suffixes = activity
                .getResources()
                .getStringArray(R.array.detail_picture_suffix);
        for (String suffix : suffixes) {
            if (url.endsWith(suffix)) {
                ActivityAnim anim = new ActivityAnim();
                Intent intent = new Intent(activity, PictureActivity.class);
                intent.putExtra(
                        activity.getString(R.string.detail_intent_picture_url),
                        url
                );
                activity.startActivity(intent);
                anim.fade(activity);
                return;
            }
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
