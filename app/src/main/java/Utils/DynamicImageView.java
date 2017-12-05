package Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by root on 25/11/16.
 */

public class DynamicImageView extends ImageView {

    final int MAX_SCALE_FACTOR = 2;

    public DynamicImageView(final Context context) {
        super(context);
    }
    public DynamicImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (width > (d.getIntrinsicWidth() * MAX_SCALE_FACTOR))
                width = d.getIntrinsicWidth() * MAX_SCALE_FACTOR;
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
