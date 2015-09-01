package org.destil.gpsaveraging;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import org.destil.gpsaveraging.base.BaseActivity;
import org.destil.gpsaveraging.ui.activity.AboutActivity;
import org.destil.gpsaveraging.ui.fragment.MainFragment;
import org.destil.gpsaveraging.ui.activity.SettingsActivity;

/**
 * Activity which displays current and averaged location.
 *
 * @author David Vávra (david@vavra.me)
 */

public class MainActivity extends BaseActivity {

    @Override
    public Fragment getFragment() {
        return new MainFragment();
    }

    @Override
    public boolean shouldShowUpArrow() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(SettingsActivity.class);
                break;
            case R.id.menu_about:
                startActivity(AboutActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
