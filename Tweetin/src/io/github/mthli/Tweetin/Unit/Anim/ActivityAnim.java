package io.github.mthli.Tweetin.Unit.Anim;

import android.app.Activity;
import io.github.mthli.Tweetin.R;

public class ActivityAnim {
    public ActivityAnim() {}

    public void rightIn(Activity a) {
        a.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void rightOut(Activity a) {
        a.overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

	public void flipHorizontal(Activity a) {
		a.overridePendingTransition(R.anim.flip_horizontal_in, R.anim.flip_horizontal_out);
	}
	
	public void flipVertical(Activity a) {
		a.overridePendingTransition(R.anim.flip_vertical_in, R.anim.flip_vertical_out);
	}
	
	public void fade(Activity a)
	{
		a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	public void disappearTopLeft(Activity a) {
		a.overridePendingTransition(R.anim.disappear_top_left_in, R.anim.disappear_top_left_out);
	}
	
	public void appearTopLeft(Activity a) {
		a.overridePendingTransition(R.anim.appear_top_left_in, R.anim.appear_top_left_out);
	}
	
	public void disappearBottomRight(Activity a) {
		a.overridePendingTransition(R.anim.disappear_bottom_right_in, R.anim.disappear_bottom_right_out);
	}
	
	public void appearBottomRight(Activity a) {
		a.overridePendingTransition(R.anim.appear_bottom_right_in, R.anim.appear_bottom_right_out);
	}
	
	public void unzoom(Activity a)
	{
		a.overridePendingTransition(R.anim.unzoom_in, R.anim.unzoom_out);
	}
}
