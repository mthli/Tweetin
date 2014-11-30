package io.github.mthli.Tweetin.Unit.Tweet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;

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
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(
                activity.getString(R.string.profile_intent_user_screen_name),
                user
        );
        ActivityAnim anim = new ActivityAnim();
        activity.startActivity(intent);
        anim.rightIn(activity);
    }
}
