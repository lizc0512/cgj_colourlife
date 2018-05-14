package com.tg.coloursteward.inter;

/**
 * 动画监听接口
 */
public interface MyObjectAnimatorListener {
    void onUpdate(MyObjectAnimtor anim, float value);

    void animatorStart(MyObjectAnimtor anim);

    void animatorEnd(MyObjectAnimtor anim);
}
