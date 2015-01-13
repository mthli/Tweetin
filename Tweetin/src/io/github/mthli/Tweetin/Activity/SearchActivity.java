package io.github.mthli.Tweetin.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import io.github.mthli.Tweetin.Fragment.BaseFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.View.ViewUnit;

public class SearchActivity extends FragmentActivity {
    private BaseFragment searchFragment;

    private String keyWord;
    public String getKeyWord() {
        if (keyWord == null) {
            return getString(R.string.search_defauft_key_word);
        }

        return keyWord;
    }

    private void initUI() {
        keyWord = getIntent().getStringExtra(getString(R.string.search_intent_key_word));
        if (keyWord == null) {
            keyWord = getString(R.string.search_defauft_key_word);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        ViewCompat.setElevation(toolbar, ViewUnit.getElevation(this, 2));

        setActionBar(toolbar);
        getActionBar().setTitle(keyWord);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSharedPreferences(getString(R.string.sp_tweetin), MODE_PRIVATE).edit().putBoolean(getString(R.string.sp_is_search_first), true).commit();

        ViewUnit.setCustomTheme(this);
        setContentView(R.layout.search);

        initUI();

        searchFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
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
                searchFragment.cancelAllTask();

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
            searchFragment.cancelAllTask();

            finish();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        searchFragment.cancelAllTask();

        super.onDestroy();
    }

    // TODO: onActivityResult()
}
