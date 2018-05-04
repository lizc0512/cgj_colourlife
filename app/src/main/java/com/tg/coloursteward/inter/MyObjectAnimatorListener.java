package com.tg.coloursteward.inter;

public interface MyObjectAnimatorListener {
	void onUpdate(MyObjectAnimtor anim, float value);
	void animatorStart(MyObjectAnimtor anim);
	void animatorEnd(MyObjectAnimtor anim);
}
