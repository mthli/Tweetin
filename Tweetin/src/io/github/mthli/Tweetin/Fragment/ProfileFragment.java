package io.github.mthli.Tweetin.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Profile.ProfileUpdateTask;
import io.github.mthli.Tweetin.Task.Profile.ProfileInitTask;

public class ProfileFragment extends ProgressFragment {
    private ProfileInitTask profileInitTask;
    private ProfileUpdateTask profileUpdateTask;
    public void setProfileUpdateTask(ProfileUpdateTask profileUpdateTask) {
        this.profileUpdateTask = profileUpdateTask;
    }
    public void cancelAllTask() {
        if (profileInitTask != null && profileInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileInitTask.cancel(true);
        }
        if (profileUpdateTask != null && profileUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            profileUpdateTask.cancel(true);
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
