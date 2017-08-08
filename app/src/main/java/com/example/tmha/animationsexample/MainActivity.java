package com.example.tmha.animationsexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIvThum1, mIvThum2, mIvExpanded;
    private int mDuration;
    private Animator mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mIvThum1.setOnClickListener(this);
        mIvThum2.setOnClickListener(this);

        mDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private void initView() {
        mIvThum1    = (ImageView) findViewById(R.id.iv_thum1);
        mIvThum2    = (ImageView) findViewById(R.id.iv_thum2);
        mIvExpanded = (ImageView) findViewById(R.id.iv_expanded);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_thum1:
                zoomImageFromThumb(mIvThum1, R.drawable.image1);
                break;
            case R.id.iv_thum2:
                zoomImageFromThumb(mIvThum2, R.drawable.image2);
                break;
        }
    }

    private void zoomImageFromThumb(final ImageView thumView, int image) {
        if(mAnimation != null){
            mAnimation.cancel();
        }
        mIvExpanded.setImageResource(image);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if((float)finalBounds.width()/finalBounds.height()
                >(float) startBounds.width()/startBounds.height()){
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width())/2;

            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;

        }else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height())/2;

            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumView.setAlpha(0f);
        mIvExpanded.setVisibility(View.VISIBLE);

        mIvExpanded.setPivotX(0f);
        mIvExpanded.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mIvExpanded, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mIvExpanded, View.Y, startBounds.top, startBounds.top))
                .with(ObjectAnimator.ofFloat(mIvExpanded, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(mIvExpanded, View.SCALE_Y, startScale, 1f));

        set.setDuration(mDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimation = null;
            }
        });

        set.start();
        mAnimation = set;

        final float startScaleFinal = startScale;
        mIvExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAnimation != null){
                    mAnimation.cancel();
                }else {
                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator.ofFloat(mIvExpanded, View.X, startBounds.left))
                            .with(ObjectAnimator.ofFloat(mIvExpanded, View.Y, startBounds.top))
                            .with(ObjectAnimator.ofFloat(mIvExpanded, View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator.ofFloat(mIvExpanded, View.SCALE_Y, startScaleFinal));
                    set.setDuration(mDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            thumView.setAlpha(1f);
                            mIvExpanded.setVisibility(View.GONE);
                            mAnimation = null;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            thumView.setAlpha(1f);
                            mIvExpanded.setVisibility(View.GONE);
                            mAnimation = null;
                        }
                    });

                    set.start();
                    mAnimation = set;
                }
            }
        });
    }
}
