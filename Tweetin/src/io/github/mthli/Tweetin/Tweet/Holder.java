package io.github.mthli.Tweetin.Tweet;

import android.graphics.Bitmap;
import android.widget.*;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Holder {
    public CircleImageView avatar;
    public TextView name;
    public TextView screenName;
    public RelativeTimeTextView createdAt;
    public TextView checkIn;
    public TextView protect;
    public ProgressBar loading;
    public ImageView picture;
    public TextView text;
    public LinearLayout info;
    public TextView retweetedBy;
    public TextView favorite;

    public LinearLayout action;
    public ImageButton replyButton;
    public ImageButton quoteButton;
    public ImageButton retweetButton;
    public ImageButton favoriteButton;
    public ImageButton deleteButton;
    public ImageButton moreButton;

    public Bitmap bitmap;
}
