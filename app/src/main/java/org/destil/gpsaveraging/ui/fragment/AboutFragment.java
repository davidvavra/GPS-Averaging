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

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.base.BaseFragment;
import org.destil.gpsaveraging.databinding.FragmentAboutBinding;
import org.destil.gpsaveraging.ui.viewmodel.AboutViewModel;

import java.util.Locale;

/**
 * Fragment displaying about information.
 */
public class AboutFragment extends BaseFragment implements AboutViewModel.ClickListener {

    private String mVersion;
    private AboutViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutBinding binding = FragmentAboutBinding.inflate(inflater, container, false);
        mViewModel = new AboutViewModel();
        mViewModel.setClickListener(this);
        binding.setViewModel(mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mVersion = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mVersion = "Unknown";
        }
        mViewModel.version.set(mVersion);
        //TODO if (!OldActivity.isFullVersion) {
        //	findViewById(R.id.thank_you).setVisibility(View.GONE);
        //}
    }

    @Override
    public void onMailClicked() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"gps-averaging-app@googlegroups.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.problem_report));
        String usersPhone = Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ") " + "v"
                + mVersion + "-" + Locale.getDefault();
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.problem_report_body, usersPhone));
        startActivity(i);
    }

    @Override
    public void onRateClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=org.destil.gpsaveraging"));
        startActivity(intent);
    }

    @Override
    public void onGithubClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/destil/GPS-Averaging"));
        startActivity(intent);
    }
}
