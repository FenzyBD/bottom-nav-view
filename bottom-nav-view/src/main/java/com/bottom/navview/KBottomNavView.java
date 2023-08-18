package com.bottom.navview;

import static com.bottom.navview.KUtil.dpToPx;
import static com.bottom.navview.KUtil.spToPx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;

public class KBottomNavView extends ViewGroup {
    private MenuInflater menuInflater;
    private final Menu menu;
    private int avgWidth = 0;

    private final Path bgCurvePath = new Path();
    private final Paint bgCurvePaint = new Paint();

    private int selected = 0;
    private KBottomNavSelectedListener clickListener = null;
    private KBottomNavReSelectedListener reClickListener = null;

    // --------- KBottomNavViewDefault Values
    @ColorInt
    private int background = Color.WHITE;
    @ColorInt
    private int shadowColor = Color.GRAY;
    private float cradleOffset = dpToPx(10);
    private float shadowRadius = dpToPx(6);
    private float circleRadius = dpToPx(42);

    // --------- Menu Item Default Values
    private float iconSize = dpToPx(38);
    private float textSize = spToPx(14);
    @ColorInt
    private int textColor = Color.BLACK;
    private int fontFamily = 0;
    @ColorInt
    private int selectedTextColor = Color.BLACK;

    public KBottomNavView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Context viewContext = getContext();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KBottomNavView,
                0, 0);
        int menuRes = a.getResourceId(R.styleable.KBottomNavView_menu, R.menu.default_menu);
        selected = a.getInt(R.styleable.KBottomNavView_selected, selected);
        circleRadius = a.getColor(R.styleable.KBottomNavView_android_background, background);
        background = a.getColor(R.styleable.KBottomNavView_android_background, background);
        shadowColor = a.getColor(R.styleable.KBottomNavView_shadowColor, shadowColor);
        cradleOffset = a.getDimension(R.styleable.KBottomNavView_cradleOffset, cradleOffset);
        shadowRadius = a.getDimension(R.styleable.KBottomNavView_shadowRadius, shadowRadius);
        // ---------- Menu Item Properties -----------------------------------------
        iconSize = a.getDimension(R.styleable.KBottomNavView_iconSize, iconSize);
        textSize = a.getDimension(R.styleable.KBottomNavView_android_textSize, textSize);
        textColor = a.getColor(R.styleable.KBottomNavView_android_textColor, textColor);
        fontFamily = a.getResourceId(R.styleable.KBottomNavView_android_fontFamily,R.font.sans_thin);
        selectedTextColor = a.getColor(R.styleable.KBottomNavView_activeColor,
                selectedTextColor);
        a.recycle();


        menu = newMenuInstance(viewContext);
        getMenuInflater(viewContext).inflate(menuRes, menu);
        int count = menu.size();

        // --------------- Manipulate Some Measurement
        if (textSize > spToPx(14)) {
            textSize = spToPx(14);
        }
        if (iconSize > dpToPx(38)) {
            iconSize = dpToPx(38);
        }
        // ------------ Add menu item as child
        for (int i = 0; i < count; i++) {
            MenuItem menuItem = menu.getItem(i);
            View menuItemView = createMenuItemView(menuItem, (selected == i)); // Create a custom
            // view for the
            int finalI = i;
            menuItemView.setOnClickListener(v -> {
                if (reClickListener != null) {
                    reClickListener.onClick(menuItem.getItemId(), finalI);
                }
                if (clickListener != null && selected != finalI) { // Single Click Response
                    clickListener.onClick(menuItem.getItemId(), finalI);
                }
                changeSelection(finalI);
            });
            // menu item
            addView(menuItemView); // Add the menu item view as a child
        }
        init();
    }

    private void changeSelection(int updateSelected) {
        float preCenterX = ((selected + 1) * avgWidth - (avgWidth * 0.5F));
        selected = updateSelected;
        float centerX = ((selected + 1) * avgWidth - (avgWidth * 0.5F));
//        calculatePath();
        an(preCenterX, centerX);
    }

    private void init() {
        setClipChildren(false);
        setBackgroundColor(Color.TRANSPARENT);
        bgCurvePath.setFillType(Path.FillType.EVEN_ODD);
        bgCurvePaint.setAntiAlias(true);
        bgCurvePaint.setColor(background);
        bgCurvePaint.setStyle(Paint.Style.FILL);
        //TODO: Uncomment to apply a shadow on path
        bgCurvePaint.setShadowLayer(shadowRadius, 0, -3,
                shadowColor);
    }

    private ValueAnimator vl = null;

    private void an(float from, float to) {
        if (vl != null) {
            vl.cancel();
        }
        vl = ValueAnimator.ofFloat(from, to);
        vl.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            calculatePath(animatedValue);
            invalidate();
            requestLayout();

        });
        vl.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                vl.removeAllUpdateListeners();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                vl.removeAllListeners();
            }
        });
        vl.start();
    }


    private final Rect mTmpContainerRect = new Rect();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = menu.size();
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        if (count > 0) {
            avgWidth = getMeasuredWidth() / count;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.setSelected(selected == i);
            child.requestLayout();

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

            maxWidth += avgWidth;

            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }
        // Check against our minimum height and width
        maxHeight = Math.max(Math.max(maxHeight, getSuggestedMinimumHeight()), dpToPx(68));
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    private final Path halfCircleCutter = new Path();
    private RectF bgRect;
    private float cutterCircleRadius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bgRect = new RectF(0, 0, w, h);
        circleRadius = avgWidth / 2F - 40;


        // --------------- Manipulate Some Measurement
        if (circleRadius > dpToPx(42)) {
            circleRadius = dpToPx(42);
        }
        if (textSize > spToPx(14)) {
            textSize = spToPx(14);
        }
        if (iconSize > dpToPx(38)) {
            iconSize = dpToPx(38);
        }


        // ---------- Calculate for first time
        cutterCircleRadius = circleRadius + cradleOffset;
        float centerX = ((selected + 1) * avgWidth - (avgWidth * 0.5F));
        calculatePath(centerX);

    }

    private void calculatePath(float centerX) {

        // ------------------- Selected Item Circle CenterX
        // -- moved to function call to work in animation
//        float centerX = ((selected + 1) * avgWidth - (avgWidth * 0.5F));

        bgCurvePath.reset();
        bgCurvePath.addRect(bgRect, Path.Direction.CW);

        halfCircleCutter.reset();
        halfCircleCutter.moveTo(centerX - cutterCircleRadius, 0);
        halfCircleCutter.arcTo(centerX - cutterCircleRadius, -cutterCircleRadius,
                centerX + cutterCircleRadius,
                cutterCircleRadius, 180, -180, false);
        halfCircleCutter.close();
        bgCurvePath.addPath(halfCircleCutter);
        // -------- Selected Item Circle
        bgCurvePath.addCircle(centerX, 0, circleRadius, Path.Direction.CW);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = menu.size();

        // These are the far left and right edges in which we are performing layout.
        int leftPos = getPaddingLeft();

        // These are the top and bottom edges in which we are performing layout.
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                mTmpContainerRect.left = leftPos + lp.leftMargin;
                mTmpContainerRect.right = leftPos + avgWidth + lp.rightMargin;

                leftPos = mTmpContainerRect.right;

                mTmpContainerRect.top = parentTop + lp.topMargin;
                mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;

                // Place the child.
                child.layout(mTmpContainerRect.left, mTmpContainerRect.top,
                        mTmpContainerRect.right, mTmpContainerRect.bottom);
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(bgCurvePath, bgCurvePaint);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    // --------------------------------------------------------------

    private MenuInflater getMenuInflater(Context context) {
        if (menuInflater == null) {
            menuInflater = new MenuInflater(context);
        }
        return menuInflater;
    }

    protected Menu newMenuInstance(Context context) {
        try {
            Class<?> menuBuilderClass = Class.forName("androidx.appcompat.view.menu.MenuBuilder");

            Constructor<?> constructor = menuBuilderClass.getDeclaredConstructor(Context.class);

            return (Menu) constructor.newInstance(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Helper method to create a custom view for a menu item
    private View createMenuItemView(MenuItem menuItem, boolean selected) {
        // Create a custom view for the menu item
        NavigationItemView customMenuItemView = new NavigationItemView(getContext());

        // Customize the view based on the menuItem (e.g., set icon, text, etc.)
        customMenuItemView.setIcon(menuItem.getIcon());
        customMenuItemView.setText(menuItem.getTitle());
        customMenuItemView.setTextSize(textSize);
        customMenuItemView.setIconSize(iconSize);
        customMenuItemView.setTextColor(textColor);
        customMenuItemView.setActiveTextColor(selectedTextColor);
        customMenuItemView.setSelected(selected);
        customMenuItemView.setFontFamily(fontFamily);

        return customMenuItemView;
    }


    public void OnItemSelectedListener(KBottomNavSelectedListener lst) {
        this.clickListener = lst;
    }

    public void OnItemReSelectedListener(KBottomNavReSelectedListener lst) {
        this.reClickListener = lst;
    }

}
