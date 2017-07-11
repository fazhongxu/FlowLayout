package com.example.xxl.flowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.icu.util.Measure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxl on 17-7-4.
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写此方法，返回margin实例，这样ViewGroup的layoutParams就指定为marginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 测量
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heigthSize = MeasureSpec.getSize(heightMeasureSpec);

        int cCount = getChildCount();
        //记录view宽和高
        int cWidth = 0;
        int cHeight = 0;

        //记录view行宽和行高取最大宽度为width，最大高度累加为height
        int lineWidth = 0;
        int lineHeight = 0;

        //记录总的行高
        int width = 0;
        int heigth = 0;

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            //测量view
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            //获取View实际宽高
            cWidth = child.getWidth() + lp.leftMargin + lp.rightMargin;
            cHeight = child.getHeight() + lp.topMargin + lp.bottomMargin;

            //判断，如果加入child之后，这一行就装不下了，则需要换行摆放
            if (lineWidth + cWidth > widthSize) {
                //记录最大宽度
                width = Math.max(lineWidth, cWidth);

                //累加当前高度
                heigth += lineHeight;

                //开启记录下一行的宽度
                lineWidth = cWidth;

                //开启记录下一行的高度
                lineHeight = cHeight;
            } else {
                lineWidth += cWidth;
                heigth = Math.max(lineHeight, cHeight);
            }

            //如果是最后一个
            if (i == cCount - 1) {
                heigth += lineHeight;
                width = Math.max(lineWidth, cWidth);
            }

            setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width
                    , heigthMode == MeasureSpec.EXACTLY ? heigthSize : heigth);
        }
    }

    /**
     * 布局，摆放
     */
    //定义List用来存放所有的chidView,根据行存储
    List<List<View>> allViews = new ArrayList<List<View>>();

    //存储每一行的最大高度
    List<Integer> mLineHeight = new ArrayList<>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = getWidth();

        //记录每一行的行宽和最大行高
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();

        List<View> lineViews = new ArrayList<>();//存放每个View

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            //如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth> width) {
                //记录该行的行宽和行高，存储所有的chidView 和最大高度
                allViews.add(lineViews);
                mLineHeight.add(lineHeight);

                lineWidth = 0;//重置行宽
                lineViews = new ArrayList<>();
            }

            //如果不需要换行，继续添加
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(childHeight
                    + lp.topMargin + lp.bottomMargin,lineHeight);

            lineViews.add(child);
        }

        //记录最后一行
        mLineHeight.add(lineHeight);
        allViews.add(lineViews);

        int left = 0;
        int top = 0;

        //遍历集合
        int lineNums = allViews.size();
        for (int i = 0; i < lineNums; i++) {
            //每一行的Views
            lineViews = allViews.get(i);
            //当前行的最大高度
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                //计算childView的left,top,right,bottom

                int cl = left + lp.leftMargin;
                int ct = top + lp.topMargin;
                int cr = cl + child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int cb = ct + child.getMeasuredHeight();

                Log.e("aaa","cl==="+cl+"ct==="+ct+"cr==="+cr+"ct==="+cb);

                child.layout(cl,ct,cr,cb);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            }
            left = 0;
            top += lineHeight;
        }
    }

}
