package io.github.mthli.Tweetin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Setting.SettingAdapter;
import io.github.mthli.Tweetin.Unit.Setting.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends ProgressFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.setting_fragment);
        View view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        ListView listView = (ListView) view
                .findViewById(R.id.setting_fragment_listview);

        List<String> titleList = new ArrayList<String>();
        titleList.add(getString(R.string.setting_title_homepage));
        titleList.add(getString(R.string.setting_title_license));
        titleList.add(getString(R.string.setting_title_version));
        titleList.add(getString(R.string.setting_title_rate));
        titleList.add(getString(R.string.setting_title_advice));
        titleList.add(getString(R.string.setting_title_author));
        titleList.add(getString(R.string.setting_title_thanks));
        titleList.add(getString(R.string.setting_title_sign_out));

        List<String> contentList = new ArrayList<String>();
        contentList.add(getString(R.string.setting_content_homepage));
        contentList.add(getString(R.string.setting_content_license));
        contentList.add(getString(R.string.app_version));
        contentList.add(getString(R.string.setting_content_rate));
        contentList.add(getString(R.string.setting_content_advice));
        contentList.add(getString(R.string.setting_content_author));
        contentList.add(getString(R.string.setting_content_thanks));
        contentList.add(getString(R.string.setting_content_sign_out));

        List<SettingItem> settingItemList = new ArrayList<SettingItem>();
        for (int i = 0; i < 8; i++) {
            SettingItem item = new SettingItem();
            item.setTitle(titleList.get(i));
            item.setContent(contentList.get(i));
            item.setShowCheckBox(false);
            item.setChecked(false);
            settingItemList.add(item);
        }

        SettingAdapter settingAdapter = new SettingAdapter(
                getActivity(),
                R.layout.setting_fragment_item,
                settingItemList
        );
        listView.setAdapter(settingAdapter);
        settingAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentToProfile = new Intent(getActivity(), ProfileActivity.class);
                ActivityAnim anim = new ActivityAnim();
                switch (position) {
                    case 0:
                        Uri homepage = Uri.parse(getString(R.string.app_homepage_link));
                        Intent intentToHomepage = new Intent(Intent.ACTION_VIEW, homepage);
                        startActivity(intentToHomepage);
                        break;
                    case 1:
                        Uri license = Uri.parse(getString(R.string.app_license_link));
                        Intent intentToLicense = new Intent(Intent.ACTION_VIEW, license);
                        startActivity(intentToLicense);
                        break;
                    case 2:
                        Uri linkTo42 = Uri.parse(getString(R.string.setting_link_to_42));
                        Intent intentTo42 = new Intent(Intent.ACTION_VIEW, linkTo42);
                        startActivity(intentTo42);
                        break;
                    case 3:
                        Uri linkToGooglePlay = Uri.parse(getString(R.string.setting_link_to_google_play));
                        Intent intentToGooglePlay = new Intent(Intent.ACTION_VIEW, linkToGooglePlay);
                        startActivity(intentToGooglePlay);
                        break;
                    case 4:
                        Intent intentToAdvice = new Intent(getActivity(), PostActivity.class);
                        intentToAdvice.putExtra(
                                getString(R.string.post_intent_flag),
                                Flag.POST_ADVICE
                        );
                        startActivity(intentToAdvice);
                        anim.fade(getActivity());
                        break;
                    case 5:
                        intentToProfile.putExtra(
                                getString(R.string.profile_intent_user_id),
                                Long.valueOf(getString(R.string.app_author_id))
                        );
                        startActivity(intentToProfile);
                        anim.rightIn(getActivity());
                        break;
                    case 6:
                        intentToProfile.putExtra(
                                getString(R.string.profile_intent_user_id),
                                Long.valueOf(getString(R.string.app_thanks_id))
                        );
                        startActivity(intentToProfile);
                        anim.rightIn(getActivity());
                        break;
                    case 7:
                        ((MainActivity) getActivity()).signOut();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
