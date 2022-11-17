package edu.jakubkt.soundpressurelevelmeter.logic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final boolean renderedTopDown;

    public VerticalTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int textViewGravity = getGravity();
        // render text from top to bottom, if android.gravity="bottom" render from bottom to top
        if (Gravity.isVertical(textViewGravity) && (textViewGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((textViewGravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
            renderedTopDown = false;
        }
        else
            renderedTopDown = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // swap width with height and vice versa
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t, l + (b - t), t + (r - l));
    }

    @Override
    public void draw(Canvas canvas) {
        // translate and rotate text upon rendering textView
        if(renderedTopDown) {
            canvas.translate(getHeight(), 0);
            canvas.rotate(90);
        }
        else {
            canvas.translate(0, getWidth());
            canvas.rotate(-90);
        }
        canvas.clipRect(0, 0, getWidth(), getHeight(), Region.Op.REPLACE);
        super.draw(canvas);
    }
}
