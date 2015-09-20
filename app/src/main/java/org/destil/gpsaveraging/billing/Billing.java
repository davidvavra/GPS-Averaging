package org.destil.gpsaveraging.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.squareup.otto.Bus;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.billing.event.BecomePremiumEvent;
import org.destil.gpsaveraging.data.Preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TODO: add documentation
 *
 * @author David Vávra (david@vavra.me)
 */
@Singleton
public class Billing {

    private final Context mContext;
    private final Bus mBus;
    private final Preferences mPreferences;
    private BillingProcessor mBillingProcessor;
    private boolean mFullVersion = false;

    @Inject
    public Billing(Context context, Bus bus, Preferences preferences) {
        mContext = context;
        mBus = bus;
        mPreferences = preferences;
        mFullVersion = preferences.isFullVersion();
    }

    public void activityOnCreate() {
        mBillingProcessor = new BillingProcessor(mContext, mContext.getString(R.string.google_play_license_key), new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String s, TransactionDetails transactionDetails) {
                fullVersion();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                onBillingInitialized();
            }

            @Override
            public void onBillingError(int i, Throwable throwable) {
                checkLegacyPremium();
            }

            @Override
            public void onBillingInitialized() {
                if (mBillingProcessor.isPurchased(mContext.getString(R.string.google_play_product_id))) {
                    fullVersion();
                } else {
                    checkLegacyPremium();
                }
            }
        });
        mBillingProcessor.loadOwnedPurchasesFromGoogle();
    }

    public void activityOnDestroy() {
        if (mBillingProcessor != null) {
            mBillingProcessor.release();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mBillingProcessor.handleActivityResult(requestCode, resultCode, data);
    }

    public void purchase(Activity activity) {
        mBillingProcessor.purchase(activity, mContext.getString(R.string.google_play_product_id));
    }

    private void fullVersion() {
        mFullVersion = true;
        mBus.post(new BecomePremiumEvent());
        mPreferences.setFullVersion(true);
    }

    private void checkLegacyPremium() {
        mFullVersion = false;
        if (mContext.getPackageManager().checkSignatures("org.destil.gpsaveraging", "cz.destil.gpsaveraging") == PackageManager.SIGNATURE_MATCH) {
            fullVersion();
            return;
        }
        mPreferences.setFullVersion(false);
    }

    public boolean isFullVersion() {
        return mFullVersion;
    }
}
