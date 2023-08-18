package com.bottom.navview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

public class NavigationItemView extends LinearLayout {
    private ImageView iconImageView;
    private TextView titleTextView;

    @ColorInt
    private int selectedColor = Color.BLACK;
    @ColorInt
    private int unSelectedColor = Color.BLACK;


    public NavigationItemView(Context context) {
        super(context);
        init();
    }

    public NavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    String[] colors = {
            "#FF5733", // Orange
            "#00AEEF", // Blue
            "#FFC300", // Yellow
            "#8E44AD", // Purple
            "#27AE60", // Green
            "#E74C3C", // Red
            "#3498DB", // Sky Blue
            "#F39C12", // Orange-Yellow
            "#2ECC71", // Emerald Green
            "#9B59B6"  // Lavender
    };

    public static String getRandomColor(String[] colors) {
        Random random = new Random();
        int randomIndex = random.nextInt(colors.length - 1);
        return colors[randomIndex];
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        iconImageView = new ImageView(getContext());
        titleTextView = new TextView(getContext());


//        iconImageView.setBackgroundColor(Color.BLACK);
        iconImageView.setImageTintList(ColorStateList.valueOf(unSelectedColor));
        titleTextView.setTextColor(ColorStateList.valueOf(unSelectedColor));

        // Customize iconImageView and titleTextView here

        addView(iconImageView);
        addView(titleTextView);
//        setBackgroundColor(Color.parseColor(getRandomColor(colors)));

    }

    public void setIcon(Drawable icon) {
        iconImageView.setImageDrawable(icon);
    }

    public void setText(CharSequence text) {
        titleTextView.setText(text);
    }

    public void setTextSize(float textSize) {
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void setIconSize(float iconSize) {
        ViewGroup.LayoutParams layoutParams = iconImageView.getLayoutParams();
        layoutParams.height = (int) iconSize;
        layoutParams.width = (int) iconSize;
        iconImageView.setLayoutParams(layoutParams);
    }

    public void setTextColor(@ColorInt int color) {
        unSelectedColor = color;
    }

    public void setActiveTextColor(@ColorInt int color) {
        selectedColor = color;
    }

    public void setFontFamily(@FontRes int fontFamily) {
        if (fontFamily == 0) {
            titleTextView.setTypeface(Typeface.DEFAULT);
            return;
        }
        Typeface fF = ResourcesCompat.getFont(getContext(), fontFamily);
        titleTextView.setTypeface(fF);
    }

    public void setTextColorSelected(@ColorInt int color) {
        titleTextView.setTextColor(color);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isSelected()) {
            iconImageView.setAlpha(0.0F);
            // If selected, adjust the icon's position to the top
            int movedInt = iconImageView.getTop();
            int height = iconImageView.getHeight();
            int topPos = iconImageView.getTop() - movedInt - Math.round(height / 2F);
            iconImageView.layout(
                    iconImageView.getLeft(),
                    topPos, // Set top
                    // position to 0
                    iconImageView.getRight(),
                    topPos + height
            );
            iconImageView.setImageTintList(ColorStateList.valueOf(selectedColor));
            titleTextView.setTextColor(ColorStateList.valueOf(selectedColor));
            iconImageView.animate().alpha(1.0F).setDuration(100L).start();
        } else {
//            iconImageView.layout(
//                    iconImageView.getLeft(),
//                    iconImageView.getTop(), // Reset to initial top margin
//                    iconImageView.getRight(),
//                    iconImageView.getBottom()
//            );
            iconImageView.setImageTintList(ColorStateList.valueOf(unSelectedColor));
            titleTextView.setTextColor(ColorStateList.valueOf(unSelectedColor));
        }
    }

}