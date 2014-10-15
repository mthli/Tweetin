package io.github.mthli.Tweetin.Profile;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.mthli.Tweetin.R;
import twitter4j.Twitter;
import twitter4j.User;

public class ProfileLoadTask extends AsyncTask<Void, Integer, Boolean> {
    private ProfileFragment profileFragment;
    private Context context;
    private View view;

    private Twitter twitter;
    private long userId;
    private User user;

    public ProfileLoadTask(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    @Override
    protected void onPreExecute() {
        context = profileFragment.getContentView().getContext();
        view = profileFragment.getContentView();
        twitter = ((ProfileActivity) profileFragment.getActivity()).getTwitter();
        userId = ((ProfileActivity) profileFragment.getActivity()).getUserId();

        profileFragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            user = twitter.showUser(userId);
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
        /* Do something */
        System.out.println("------------------------------");
        System.out.println(user.getProfileBackgroundColor());
        System.out.println("------------------------------");

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
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            profileFragment.setContentEmpty(false);
            profileFragmentAdapter();
            profileFragment.setContentShown(true);
        } else {
            profileFragment.setContentEmpty(true);
            profileFragment.setEmptyText(R.string.profile_get_user_data_failed);
            profileFragment.setContentShown(true);
        }
    }
}
