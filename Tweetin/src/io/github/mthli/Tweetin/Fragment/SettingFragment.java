package io.github.mthli.Tweetin.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.balysv.materialripple.MaterialRippleLayout;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Tweetin.Activity.MainActivity;
import io.github.mthli.Tweetin.Activity.PostActivity;
import io.github.mthli.Tweetin.Activity.ProfileActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.Flag.Flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Button button = (Button) view
                .findViewById(R.id.setting_fragment_button);
        /* Ripple Effect */
        MaterialRippleLayout.on(button)
                .rippleOverlay(true)
                .rippleColor(getResources().getColor(R.color.text))
                .rippleAlpha(0.1f)
                .rippleDiameterDp(10)
                .rippleDuration(350)
                .create();

        List<String> titleList = new ArrayList<String>();
        titleList.add(getString(R.string.setting_title_homepage));
        titleList.add(getString(R.string.setting_title_license));
        titleList.add(getString(R.string.setting_title_version));
        titleList.add(getString(R.string.setting_title_feedback));
        titleList.add(getString(R.string.setting_title_author));
        titleList.add(getString(R.string.setting_title_thanks));

        List<String> contentList = new ArrayList<String>();
        contentList.add(getString(R.string.setting_content_homepage));
        contentList.add(getString(R.string.setting_content_license));
        contentList.add(getString(R.string.setting_content_version));
        contentList.add(getString(R.string.setting_content_feedback));
        contentList.add(getString(R.string.setting_content_author));
        contentList.add(getString(R.string.setting_content_thanks));

        List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 6; i++) {
            Map<String, String> list = new HashMap<String, String>();
            list.put("title", titleList.get(i));
            list.put("content", contentList.get(i));
            lists.add(list);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(),
                lists,
                R.layout.setting_fragment_item,
                new String[]{"title", "content"},
                new int[]{R.id.setting_fragment_item_title, R.id.setting_fragment_item_content}
        );
        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentToProfile = new Intent(getActivity(), ProfileActivity.class);
                ActivityAnim anim = new ActivityAnim();
                switch (position) {
                    case 0:
                        Uri homepage = Uri.parse(getString(R.string.app_homepage));
                        Intent intentToHomepage = new Intent(Intent.ACTION_VIEW, homepage);
                        startActivity(intentToHomepage);
                        break;
                    case 1:
                        Uri license = Uri.parse(getString(R.string.app_license));
                        Intent intentToLicense = new Intent(Intent.ACTION_VIEW, license);
                        startActivity(intentToLicense);
                        break;
                    case 2:
                        Uri link = Uri.parse(getString(R.string.setting_link_42));
                        Intent intentTo42 = new Intent(Intent.ACTION_VIEW, link);
                        startActivity(intentTo42);
                        break;
                    case 3:
                        Intent intentToFeedback = new Intent(getActivity(), PostActivity.class);
                        intentToFeedback.putExtra(
                                getString(R.string.post_intent_flag),
                                Flag.POST_FEEDBACK
                        );
                        startActivity(intentToFeedback);
                        anim.fade(getActivity());
                        break;
                    case 4:
                        intentToProfile.putExtra(
                                getString(R.string.profile_intent_user_id),
                                Long.valueOf(getString(R.string.app_author_id))
                        );
                        startActivity(intentToProfile);
                        anim.rightIn(getActivity());
                        break;
                    case 5:
                        intentToProfile.putExtra(
                                getString(R.string.profile_intent_user_id),
                                Long.valueOf(getString(R.string.app_thanks_id))
                        );
                        startActivity(intentToProfile);
                        anim.rightIn(getActivity());
                        break;
                    default:
                        break;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).signOut();
            }
        });
    }
}
