/*
 * Copyright 2015 David Vávra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destil.gpsaveraging.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Util class for showing various animations.
 *
 * @author David Vávra (vavra@avast.com)
 */
public class Animations {

    public static void showFromTop(final View view) {
        view.setAlpha(0);
        animateAfterHeightIsKnown(view, new HeightKnownListener() {
            @Override
            public void onHeightKnown(ViewPropertyAnimator animator) {
                view.setTranslationY(-view.getHeight());
                animator.alpha(1).translationY(0);
            }
        });
    }

    public static void showFromBottom(final View view) {
        view.setAlpha(0);
        animateAfterHeightIsKnown(view, new HeightKnownListener() {
            @Override
            public void onHeightKnown(ViewPropertyAnimator animator) {
                view.setTranslationY(view.getHeight());
                animator.alpha(1).translationY(0);
            }
        });
    }

    public static void hide(View view) {
        view.setAlpha(1);
        animate(view).alpha(0);
    }

    public static void hideToTop(final View view) {
        view.setAlpha(1);
        view.setTranslationY(0);
        animate(view).alpha(0).translationY(-view.getHeight());
    }

    public static void moveToBottom(View view) {
        view.setTranslationY(-view.getHeight());
        animate(view).translationY(0);
    }

    public static void moveToTop(final View view, final AnimationEndCallback callback) {
        view.setTranslationY(0);
        animate(view, callback).translationY(-view.getHeight());
    }

    public static void expand(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    public static void collapse(final View view, final AnimationEndCallback callback) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callback.onAnimationEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 1dp/ms
        a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(a);
    }

    private static ViewPropertyAnimator animate(final View view) {
        return animate(view, null);
    }

    private static ViewPropertyAnimator animate(final View view, final AnimationEndCallback callback) {
        view.setVisibility(View.VISIBLE);
        return view.animate().setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.onAnimationEnd();
                }
            }
        });
    }

    private static void animateAfterHeightIsKnown(final View view, final HeightKnownListener listener) {
        view.setVisibility(View.VISIBLE);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                listener.onHeightKnown(animate(view));
            }
        });
    }

    public interface AnimationEndCallback {
        void onAnimationEnd();
    }

    interface HeightKnownListener {
        void onHeightKnown(ViewPropertyAnimator animator);
    }
}
