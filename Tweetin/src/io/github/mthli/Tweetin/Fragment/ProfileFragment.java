package io.github.mthli.Tweetin.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Profile.ProfileFollowTask;
import io.github.mthli.Tweetin.Task.Profile.ProfileLoadTask;

public class ProfileFragment extends ProgressFragment {
    private ProfileLoadTask profileLoadTask;
    private ProfileFollowTask profileFollowTask;
    public void setProfileFollowTask(ProfileFollowTask profileFollowTask) {
        this.profileFollowTask = profileFollowTask;
    }
    public void allTaskDown() {
        if (profileLoadTask != null && profileLoadTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileLoadTask.cancel(true);
        }
        if (profileFollowTask != null && profileFollowTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileFollowTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.profile_fragment);
        setContentEmpty(false);
        setContentShown(true);

        profileLoadTask = new ProfileLoadTask(this);
        profileLoadTask.execute();
    }
}
