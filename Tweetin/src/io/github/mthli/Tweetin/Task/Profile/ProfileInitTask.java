package io.github.mthli.Tweetin.Task.Profile;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.Fragment.ProfileFragment;
import io.github.mthli.Tweetin.R;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.User;

public class ProfileInitTask extends AsyncTask<Void, Integer, Boolean> {
    private ProfileFragment profileFragment;
    private Context context;
    private View view;

    private Twitter twitter;
    private long useId;
    private long userId;
    private User user;
    private boolean isFollowing;

    public ProfileInitTask(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
        this.isFollowing = false;
    }

    @Override
    protected void onPreExecute() {
        context = profileFragment.getContentView().getContext();
        view = profileFragment.getContentView();
        twitter = ((ProfileActivity) profileFragment.getActivity()).getTwitter();
        useId = ((ProfileActivity) profileFragment.getActivity()).getUseId();
        userId = ((ProfileActivity) profileFragment.getActivity()).getUserId();

        profileFragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            user = twitter.showUser(userId);
            Relationship relationship = twitter.friendsFollowers().showFriendship(useId, userId);
            isFollowing = relationship.isSourceFollowingTarget();
        } catch (Exception e) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }


    private void profileFragmentAdapter() {
        ImageView background = (ImageView) view
                .findViewById(R.id.profile_background);
        if (user.getProfileBackgroundImageURL() != null) {
            Glide.with(context)
                    .load(user.getProfileBackgroundImageURL())
                    .centerCrop()
                    .into(background);
        } else {
            background.setVisibility(View.GONE);
        }

        CircleImageView avatar = (CircleImageView) view
                .findViewById(R.id.profile_avatar);
        String avatarURL = user.getBiggerProfileImageURL();
        Glide.with(context).load(avatarURL).crossFade().into(avatar);

        TextView name = (TextView) view.findViewById(R.id.profile_name);
        name.setText(user.getName());

        TextView screenName = (TextView) view.findViewById(R.id.profile_screen_name);
        screenName.setText("@" + user.getScreenName());

        if (user.isProtected()) {
            TextView protect = (TextView) view.findViewById(R.id.profile_protect);
            protect.setVisibility(View.VISIBLE);
        }

        if (user.getDescription().length() > 0) {
            TextView description = (TextView) view.findViewById(R.id.profile_description);
            description.setText(user.getDescription());
            description.setVisibility(View.VISIBLE);
        }

        TextView location = (TextView) view.findViewById(R.id.profile_location);
        if (user.getLocation().length() > 0) {
            location.setText(user.getLocation());
            location.setVisibility(View.VISIBLE);
        }

        Button follow = (Button) view.findViewById(R.id.profile_follow);
        /* RippleEffect */
        MaterialRippleLayout.on(follow)
                .rippleOverlay(true)
                .rippleColor(context.getResources().getColor(R.color.text))
                .rippleAlpha(0.1f)
                .rippleDiameterDp(10)
                .rippleDuration(350)
                .create();
        if (isFollowing) {
            follow.setText(context.getString(R.string.profile_un_follow));
        } else {
            follow.setText(context.getString(R.string.profile_follow));
        }
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileUpdateTask profileUpdateTask = new ProfileUpdateTask(
                        profileFragment,
                        isFollowing,
                        user
                );
                profileFragment.setProfileUpdateTask(profileUpdateTask);
                profileUpdateTask.execute();
            }
        });
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            profileFragment.setContentEmpty(false);
            profileFragmentAdapter();
            profileFragment.setContentShown(true);
        } else {
            profileFragment.setContentEmpty(true);
            profileFragment.setEmptyText(R.string.profile_error_get_user_profile_failed);
            profileFragment.setContentShown(true);
        }
    }
}
