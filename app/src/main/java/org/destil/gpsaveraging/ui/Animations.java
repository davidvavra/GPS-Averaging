package org.destil.gpsaveraging.ui;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import org.destil.gpsaveraging.R;
import org.destil.gpsaveraging.ui.view.AverageLocationCardView;
import org.destil.gpsaveraging.ui.view.LocationCardView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Animations {

    private final Context mContext;

    @Inject
    public Animations(Context context) {
        mContext = context;
    }

    public void showFromTop(final View view) {
        animate(view, R.anim.show_from_top, false, true);
    }

    public void hideToTop(final View view) {
        animate(view, R.anim.hide_to_top, true, false);
    }

    public void moveUpAndExpand(final AverageLocationCardView vAverageLocation) {
        animate(vAverageLocation, R.anim.move_to_top, true, true, new AnimationEndCallback() {
            @Override
            public void onAnimationEnd() {
                animate(vAverageLocation.getActionsView(), R.anim.expand, false, true);
            }
        });
    }

    public void collapseAndMoveDown(final AverageLocationCardView averageLocation, final LocationCardView currentLocation) {
        animate(averageLocation.getActionsView(), R.anim.collapse, true, false, new AnimationEndCallback() {
            @Override
            public void onAnimationEnd() {
                animate(averageLocation, R.anim.move_to_bottom, true, true);
                animate(currentLocation, R.anim.show_from_top, false, true);
            }
        });
    }

    private void animate(View view, int animationRes, boolean visibleStart, boolean visibleEnd) {
        animate(view, animationRes, visibleStart, visibleEnd, null);
    }

    private void animate(final View view, int animationRes, final boolean visibleStart, final boolean visibleEnd, final AnimationEndCallback animationEndCallback) {
        final Animation animation = AnimationUtils.loadAnimation(mContext, animationRes);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (visibleStart) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (visibleEnd) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                if (animationEndCallback != null) {
                    animationEndCallback.onAnimationEnd();
                }
                // animation flicker hack via http://stackoverflow.com/questions/9387711/android-animation-flicker
                animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                animation.setDuration(1);
                view.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    private interface AnimationEndCallback {
        void onAnimationEnd();
    }
}
