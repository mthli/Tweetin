package io.github.mthli.Tweetin.Tweet;

import android.widget.*;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TweetHolder {
    protected CircleImageView avatar;
    protected TextView name;
    protected TextView screenName;
    protected RelativeTimeTextView createdAt;
    protected TextView checkIn;
    protected TextView protect;
    protected TextView text;
    protected LinearLayout info;
    protected TextView retweetedBy;
    protected TextView favorite;

    protected LinearLayout action;
    protected ImageButton replyButton;
    protected ImageButton quoteButton;
    protected ImageButton retweetButton;
    protected ImageButton favoriteButton;
    protected ImageButton deleteButton;
    protected ImageButton pictureButton;
    protected ImageButton moreButton;
}
