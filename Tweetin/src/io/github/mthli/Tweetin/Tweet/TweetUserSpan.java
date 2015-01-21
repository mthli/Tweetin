package io.github.mthli.Tweetin.Tweet;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.github.mthli.Tweetin.Activity.InReplyToActivity;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.Activity.SearchActivity;
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
        textPaint.setColor(activity.getResources().getColor(R.color.secondary_text));
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    @Override
    public void onClick(View view) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).getCurrentListFragment().showProfile(user);
            return;
        }

        if (activity instanceof InReplyToActivity) {
            ((InReplyToActivity) activity).getInReplyToFragment().showProfile(user);
            return;
        }

        if (activity instanceof PictureActivity) {
            ((PictureActivity) activity).getPictureFragment().showProfile(user);
            return;
        }

        if (activity instanceof SearchActivity) {
            ((SearchActivity) activity).getSearchFragment().showProfile(user);
            return;
        }
    }
}
