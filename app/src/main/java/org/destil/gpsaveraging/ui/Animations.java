package org.destil.gpsaveraging.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import org.destil.gpsaveraging.App;
import org.destil.gpsaveraging.R;

public class Animations {

    public static void showFromTop(final View view) {
        animate(view, R.anim.show_from_top, false, true);
    }

    public static void hideToTop(final View view) {
        animate(view, R.anim.hide_to_top, true, false);
    }

    public static void moveUpAndExpand(final AverageLocationCardView vAverageLocation) {
        animate(vAverageLocation, R.anim.move_to_top, true, true, new AnimationEndCallback() {
            @Override
            public void onAnimationEnd() {
                animate(vAverageLocation.getActionsView(), R.anim.expand, false, true);
            }
        });
    }

    public static void collapseAndMoveDown(final AverageLocationCardView averageLocation, final LocationCardView currentLocation) {
        animate(averageLocation.getActionsView(), R.anim.collapse, true, false, new AnimationEndCallback() {
            @Override
            public void onAnimationEnd() {
                animate(averageLocation, R.anim.move_to_bottom, true, true);
                animate(currentLocation, R.anim.show_from_top, false, true);
            }
        });
    }

    private static void animate(View view, int animationRes, boolean visibleStart, boolean visibleEnd) {
        animate(view, animationRes, visibleStart, visibleEnd, null);
    }

    private static void animate(final View view, int animationRes, final boolean visibleStart, final boolean visibleEnd, final AnimationEndCallback animationEndCallback) {
        final Animation animation = AnimationUtils.loadAnimation(App.get(), animationRes);
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
