package org.destil.gpsaveraging.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.destil.gpsaveraging.R;

/**
 * Base activity for all others.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShowUpArrow());
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, getFragment()).commit();
        }
    }

    public abstract Fragment getFragment();

    public abstract boolean shouldShowUpArrow();

    public void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }
}
