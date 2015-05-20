package com.messi.languagehelper.util;

import android.view.View;

import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public class AnimationUtil {

	public static void Alpha(View view, AnimatorListener mAnimatorListener, float from, float to, long startDelay, long duration){
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view, "alpha", from, to);
		if(mAnimatorListener != null){
			mObjectAnimator.addListener(mAnimatorListener);
		}
		mObjectAnimator.setStartDelay(startDelay);
		mObjectAnimator.setDuration(duration).start();
	}
	
	public static void ScaleXY(View view, AnimatorListener mAnimatorListener, float from, float to, long startDelay, long duration){
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", from, to);
		if(mAnimatorListener != null){
			mObjectAnimator.addListener(mAnimatorListener);
		}
		mObjectAnimator.setDuration(duration).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(view, "scaleY", from, to);
		mObjectAnimator1.setDuration(duration).start();
	}
	
	public static void ScaleXYAndAlpha(View view, AnimatorListener mAnimatorListener, float from, float to, long startDelay, long duration){
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", from, to);
		if(mAnimatorListener != null){
			mObjectAnimator.addListener(mAnimatorListener);
		}
		mObjectAnimator.setDuration(duration).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(view, "scaleY", from, to);
		mObjectAnimator1.setDuration(duration).start();
		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
		mObjectAnimator2.setDuration(duration).start();
	}
	
	public static void ScaleXYAndAlpha(View view, AnimatorListener mAnimatorListener, float from, float to, float afrom, float ato,long startDelay, long duration){
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view, "scaleX", from, to);
		if(mAnimatorListener != null){
			mObjectAnimator.addListener(mAnimatorListener);
		}
		mObjectAnimator.setDuration(duration).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(view, "scaleY", from, to);
		mObjectAnimator1.setDuration(duration).start();
		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(view, "alpha", afrom, ato);
		mObjectAnimator2.setDuration(duration).start();
	}
}
