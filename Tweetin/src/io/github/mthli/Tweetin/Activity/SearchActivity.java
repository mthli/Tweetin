package io.github.mthli.Tweetin.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import io.github.mthli.Tweetin.Fragment.TweetList.SearchFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class SearchActivity extends FragmentActivity {
    private SearchFragment searchFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        String keyWord = getIntent().getStringExtra(getString(R.string.search_intent_key_word));
        if (keyWord == null) {
            keyWord = getString(R.string.search_defauft_key_word);
        }

        setActionBar(toolbar);
        getActionBar().setTitle(keyWord);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                searchFragment.cancelAllTasks();
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            searchFragment.cancelAllTasks();
            finish();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        searchFragment.cancelAllTasks();
        super.onDestroy();
    }

    // TODO: onActivityResult()
}
