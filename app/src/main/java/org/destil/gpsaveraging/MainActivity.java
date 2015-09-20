package org.destil.gpsaveraging;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import org.destil.gpsaveraging.base.BaseActivity;
import org.destil.gpsaveraging.billing.Billing;
import org.destil.gpsaveraging.location.event.FirstFixEvent;
import org.destil.gpsaveraging.ui.activity.AboutActivity;
import org.destil.gpsaveraging.ui.fragment.MainFragment;
import org.destil.gpsaveraging.ui.activity.SettingsActivity;

/**
 * Activity which displays current and averaged location.
 *
 * @author David Vávra (david@vavra.me)
 */

public class MainActivity extends BaseActivity {

    @Inject
    Billing mBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component().injectToMainActivity(this);
        mBilling.activityOnCreate();
    }

    @Override
    protected void onDestroy() {
        mBilling.activityOnDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBilling.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_remove_ads).setVisible(!mBilling.isFullVersion());
        return true;
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
            case R.id.menu_remove_ads:
                mBilling.purchase(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
