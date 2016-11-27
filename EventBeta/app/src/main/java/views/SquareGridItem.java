package views;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by alfon on 2016-03-28.
 */
public class SquareGridItem extends RelativeLayout {

    public SquareGridItem(Context context) {
        super(context);
    }

    public SquareGridItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}