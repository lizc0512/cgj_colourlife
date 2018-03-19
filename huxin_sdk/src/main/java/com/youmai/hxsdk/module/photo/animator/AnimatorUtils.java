package com.youmai.hxsdk.module.photo.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.youmai.hxsdk.view.full.CallFullView;

/**
 * 作者：create by YW
 * 日期：2017.09.08 10:55
 * 描述：
 */

public class AnimatorUtils {

    public final static int MIDDLE_ANIMATION_DURATION = 1200;
    public final static int ANIMATION_DURATION = 600;
    public final static int ANIMATION_RESET_DURATION = 100;
    public final static int MINI_MUN_X_DISTANCE = 200;

    public static void startAnim(final CallFullView context, final View view) {
        final float startX = view.getX();
        AnimatorPath mPath = new AnimatorPath();
        mPath.moveTo(0, 0);
        //mPath.cubicTo(-200, 200, -400, 100, -600, 50); //相对坐标

        mPath.lineTo(0, -1800);

        //属性动画认知：本质是控制一个对象身上的任何属性值 反射setTranslation(xxx)  setAlpha(0, 5);
        final ObjectAnimator animator = ObjectAnimator.ofObject(context, "animationStart",
                new PathEvaluator(), mPath.getPoints().toArray()); //数组
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (Math.abs(startX - view.getX()) > MINI_MUN_X_DISTANCE) {
                }

                view.postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.cancel();
                animator.cancel();
                this.onAnimationCancel(animator);
                view.clearAnimation();

                startAnimReset(context, view);
            }
        });
    }

    public static void startAnimReset(final CallFullView context, final View view) {
        final float startX = view.getX();
        AnimatorPath mPath = new AnimatorPath();
        mPath.moveTo(0, -1800);
        mPath.lineTo(0, 0);

        //属性动画认知：本质是控制一个对象身上的任何属性值 反射setTranslation(xxx)  setAlpha(0, 5);
        final ObjectAnimator animator = ObjectAnimator.ofObject(context, "animationReset",
                new PathEvaluator(), mPath.getPoints().toArray()); //数组
        animator.setDuration(300);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (Math.abs(startX - view.getX()) > MINI_MUN_X_DISTANCE) {
                }

                view.postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.cancel();
                //view.setVisibility(view.GONE);
            }
        });
    }

    public static void startAnimScale(final CallFullView context, final View view) {
        AnimatorPath mPath = new AnimatorPath();
        mPath.moveToAndAlpha(0, 0, 0.0f);
        mPath.lineTo(30, 30);

        //属性动画认知：本质是控制一个对象身上的任何属性值 反射setTranslation(xxx)  setAlpha(0, 5);
        final ObjectAnimator animator = ObjectAnimator.ofObject(context, "animationScale",
                new PathEvaluator(), mPath.getPoints().toArray()); //数组
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
                startAnim(context, view);
            }
        });
    }

    public static void startAnimJoke(final CallFullView context, final View view) {
        AnimatorPath mPath = new AnimatorPath();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, -1200);

        final ObjectAnimator animator = ObjectAnimator.ofObject(context, "animJoke",
                new PathEvaluator(), mPath.getPoints().toArray());
        animator.setDuration(ANIMATION_DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
                stopAnimJoke(context, view);
            }
        });
    }

    public static void stopAnimJoke(final CallFullView context, final View view) {
        AnimatorPath mPath = new AnimatorPath();
        mPath.moveTo(0, -1200);
        mPath.lineTo(0, 0);

        final ObjectAnimator animator = ObjectAnimator.ofObject(context, "animJokeReset",
                new PathEvaluator(), mPath.getPoints().toArray());
        animator.setDuration(ANIMATION_RESET_DURATION);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.postInvalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
            }
        });
    }
}
