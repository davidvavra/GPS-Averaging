package org.destil.gpsaveraging.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.base.BaseFragment;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment displaying about information.
 */
public class AboutFragment extends BaseFragment {

    @Bind(R.id.thank_you)
    LinearLayout vThankYou;
    @Bind(R.id.version)
    TextView vVersion;

    private String mVersion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mVersion = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mVersion = "Unknown";
        }
        vVersion.setText(mVersion);
        //TODO if (!OldActivity.isFullVersion) {
        //	findViewById(R.id.thank_you).setVisibility(View.GONE);
        //}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Sends mail to the author.
     */
    @OnClick(R.id.mail)
    public void mailButtonClicked(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"gps-averaging-app@googlegroups.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.problem_report));
        String usersPhone = Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ") " + "v"
                + mVersion + "-" + Locale.getDefault();
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.problem_report_body, usersPhone));
        startActivity(i);
    }

    /**
     * Rates app on Play.
     */
    @OnClick(R.id.rate)
    public void rateButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=org.destil.gpsaveraging"));
        startActivity(intent);
    }

    /**
     * Visits app's web
     */
    @OnClick(R.id.github)
    public void webButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/destil/GPS-Averaging"));
        startActivity(intent);
    }
}
