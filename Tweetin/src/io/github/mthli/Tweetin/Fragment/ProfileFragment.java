package io.github.mthli.Tweetin.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Profile.ProfileFollowTask;
import io.github.mthli.Tweetin.Task.Profile.ProfileInitTask;

public class ProfileFragment extends ProgressFragment {
    private ProfileInitTask profileInitTask;
    private ProfileFollowTask profileFollowTask;
    public void setProfileFollowTask(ProfileFollowTask profileFollowTask) {
        this.profileFollowTask = profileFollowTask;
    }
    public void cancelAllTask() {
        if (profileInitTask != null && profileInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileInitTask.cancel(true);
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

        profileInitTask = new ProfileInitTask(this);
        profileInitTask.execute();
    }
}
