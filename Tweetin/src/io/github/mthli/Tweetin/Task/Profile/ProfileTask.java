package io.github.mthli.Tweetin.Task.Profile;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.InReplyToActivity;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.PictureActivity;
import io.github.mthli.Tweetin.Activity.SearchActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Tweet.TweetUnit;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class ProfileTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private View profile;

    private ProgressBar progressBar;
    private TextView reload;

    private RelativeLayout profileAll;
    private View background;
    private CircleImageView avatar;
    private TextView name;
    private TextView screenName;
    private TextView protect;
    private TextView description;
    private TextView location;
    private Button follow;

    private String sn;
    private String usn;
    private User user;
    private boolean fo;

    public ProfileTask(Activity activity, View profile, String sn) {
        this.activity = activity;
        this.profile = profile;
        this.sn = sn;
        this.usn = TwitterUnit.getUseScreenNameFromSharedPreferences(activity);
        this.fo = false;
    }

    @Override
    protected void onPreExecute() {
        progressBar = (ProgressBar) profile.findViewById(R.id.profile_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        reload = (TextView) profile.findViewById(R.id.profile_reload);
        reload.setVisibility(View.GONE);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getCurrentListFragment().showProfile(sn);
                    return;
                }

                if (activity instanceof InReplyToActivity) {
                    ((InReplyToActivity) activity).getInReplyToFragment().showProfile(sn);
                    return;
                }

                if (activity instanceof PictureActivity) {
                    ((PictureActivity) activity).getPictureFragment().showProfile(sn);
                    return;
                }

                if (activity instanceof SearchActivity) {
                    ((SearchActivity) activity).getSearchFragment().showProfile(sn);
                    return;
                }
            }
        });
        reload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(activity, R.string.profile_toast_reload, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        profileAll = (RelativeLayout) profile.findViewById(R.id.profile_all);
        profileAll.setVisibility(View.GONE);

        background = profile.findViewById(R.id.profile_background);
        avatar = (CircleImageView) profile.findViewById(R.id.profile_avatar);
        name = (TextView) profile.findViewById(R.id.profile_name);
        screenName = (TextView) profile.findViewById(R.id.profile_screen_name);
        protect = (TextView) profile.findViewById(R.id.profile_protect);
        description = (TextView) profile.findViewById(R.id.profile_description);
        location = (TextView) profile.findViewById(R.id.profile_location);

        follow = (Button) profile.findViewById(R.id.profile_follow);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Twitter twitter = TwitterUnit.getTwitterFromSharedPreferences(activity);
            user = twitter.showUser(sn);
            if (!sn.equals(usn)) {
                Relationship relationship = twitter.friendsFollowers().showFriendship(usn, sn);
                fo = relationship.isSourceFollowingTarget();
            }
        } catch (TwitterException t) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {}

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            progressBar.setVisibility(View.GONE);
            reload.setVisibility(View.GONE);
            showProfile();
        } else {
            progressBar.setVisibility(View.GONE);
            reload.setVisibility(View.VISIBLE);
            profileAll.setVisibility(View.GONE);
        }
    }

    private void showProfile() {
        if (user.getProfileBackgroundColor() != null) {
            background.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColor()));
        } else {
            background.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

        Glide.with(activity).load(user.getOriginalProfileImageURL()).crossFade().into(avatar);

        name.setText(user.getName());
        screenName.setText("@" + user.getScreenName());

        if (user.isProtected()) {
            protect.setVisibility(View.VISIBLE);
        } else {
            protect.setVisibility(View.GONE);
        }

        TweetUnit tweetUnit = new TweetUnit(activity);
        if (user.getDescription().length() > 0) {
            description.setMovementMethod(LinkMovementMethod.getInstance());
            description.setText(tweetUnit.getSpanFromText(tweetUnit.getDescriptionFromUser(user)));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }

        if (user.getLocation().length() > 0) {
            location.setText(user.getLocation());
            location.setVisibility(View.VISIBLE);
        } else {
            location.setVisibility(View.GONE);
        }

        if (fo) {
            follow.setText(activity.getString(R.string.profile_follow_unfollow));
        } else {
            follow.setText(activity.getString(R.string.profile_follow_follow));
        }

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = follow.getText().toString();
                if (text.equals(activity.getString(R.string.profile_follow_follow))) {
                    (new FollowTask(activity, profile, sn)).execute();
                } else {
                    (new UnFollowTask(activity, profile, sn)).execute();
                }
            }
        });

        if (sn.equals(usn)) {
            follow.setVisibility(View.GONE);
        }

        profileAll.setVisibility(View.VISIBLE);
    }
}
