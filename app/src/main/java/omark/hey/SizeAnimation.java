package omark.hey;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SizeAnimation extends Animation {
    int initialHeight;
    int targetHeight;
    int initialWidth;
    int targetWidth;
    View view;

    public SizeAnimation(View view, int targetWidth, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight * (int)(view.getContext().getResources().getDisplayMetrics().density + 0.5f);
        this.targetWidth = targetWidth * (int)(view.getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
        view.getLayoutParams().width = initialWidth + (int) ((targetWidth - initialWidth) * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        this.initialHeight = height * (int)(view.getContext().getResources().getDisplayMetrics().density + 0.5f);
        this.initialWidth = width * (int)(view.getContext().getResources().getDisplayMetrics().density + 0.5f);
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
