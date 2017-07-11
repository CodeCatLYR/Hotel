package com.tgcyber.hotelmobile.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._interface.CategoryTabListener;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;

import java.util.ArrayList;


public class CategoryTabStrip extends HorizontalScrollView {
    private LayoutInflater mLayoutInflater;
    private final PageListener pageListener = new PageListener();
    private ViewPager pager;
    private LinearLayout tabsContainer;
    private CategoryTabListener categoryTabListener;
    private int tabCount;
    private boolean hasBottom=true;//是否需要底部ＲＥＣＴ标识
    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Rect indicatorRect;

    private LinearLayout.LayoutParams defaultTabLayoutParams;

    private int scrollOffset = 10;
    private int lastScrollX = 0;

    private Drawable indicator;
    private TextDrawable[] drawables;
    // private Drawable left_edge;
    // private Drawable right_edge;

    private ArrayList<Fragment> list_fragments;

    public CategoryTabStrip(Context context) {
        this(context, null);
    }

    public CategoryTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoryTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mLayoutInflater = LayoutInflater.from(context);
        drawables = new TextDrawable[3];
        int i = 0;
        while (i < drawables.length) {
            drawables[i] = new TextDrawable(context);
            i++;
        }

        indicatorRect = new Rect();

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setPadding(0,0,0,0);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        scrollOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        /**
         * @author liangzgva
         *
         *         为了兼容只有“有数”，“轻应用”这两个标题版本，因此加上weight，如果日后拥有多个标题，应该去掉该句
         */
        defaultTabLayoutParams.weight = 1;
        // 绘制高亮区域作为滑动分页指示器
        indicator = getResources()  .getDrawable(R.drawable.bg_category_indicator);

        // 左右边界阴影效果
        // left_edge =
        // getResources().getDrawable(R.drawable.ic_category_left_edge);
        // right_edge =
        // getResources().getDrawable(R.drawable.ic_category_right_edge);
    }

    // 绑定与CategoryTabStrip控件对应的ViewPager控件，实现联动
    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void resetTabs(int pos)
    {
        tabsContainer.removeAllViews();
       currentPosition = pos;
        currentPositionOffset = 0f;
    }
    // 当附加在ViewPager适配器上的数据发生变化时,应该调用该方法通知CategoryTabStrip刷新数据,原先settextColor为０.
    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            addTab(i, pager.getAdapter().getPageTitle(i).toString());
        }
        setTextColor(currentPosition, getResources().getColor(R.color.category_tab_text2));
    }

    public void setCategoryTabListener(CategoryTabListener categoryTabListener){
        this.categoryTabListener = categoryTabListener;
    }

    private void addTab(final int position, final String title) {
        ViewGroup tab = (ViewGroup) mLayoutInflater.inflate(
                R.layout.category_tab, this, false);
        TextView category_text = (TextView) tab
                .findViewById(R.id.category_text);
        category_text.setText(title);
        category_text.setGravity(Gravity.CENTER);
        category_text.setSingleLine();
        category_text.setFocusable(true);
        category_text.setTextColor(getResources().getColor(
                R.color.category_tab_text));
      /*  if(position==tabCount-1&&tabCount>1)
        {
            //LogCat.i("CategoryTabStrip","pos="+position+" tabCount="+tabCount);
           // category_text.setPadding(DensityUtil.dip2px(getContext(),12),DensityUtil.dip2px(getContext(),4),DensityUtil.dip2px(getContext(),12),DensityUtil.dip2px(getContext(),4));
           tab.setPadding(DensityUtil.dip2px(getContext(),4),0,DensityUtil.dip2px(getContext(),0),0);
        }*/
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
                if (categoryTabListener != null){
                    categoryTabListener.setCancelListener(title);
                }
            }
        });

        tabsContainer.addView(tab, position, defaultTabLayoutParams);
    }

    // 计算滑动过程中矩形高亮区域的上下左右位置
    private void calculateIndicatorRect(Rect rect,float positionOffset) {
        if(!hasBottom) return;
        if (tabCount == 0)
            return;

        ViewGroup currentTab = (ViewGroup) tabsContainer
                .getChildAt(currentPosition);
        TextView category_text = (TextView) currentTab
                .findViewById(R.id.category_text);

        float left = (float) (currentTab.getLeft() + category_text.getLeft());
        float width = ((float) category_text.getWidth()) + left;

        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            ViewGroup nextTab = (ViewGroup) tabsContainer
                    .getChildAt(currentPosition + 1);
            TextView next_category_text = (TextView) nextTab
                    .findViewById(R.id.category_text);

            float next_left = (float) (nextTab.getLeft() + next_category_text
                    .getLeft());
            left = left * (1.0f - currentPositionOffset) + next_left
                    * currentPositionOffset;
            width = width * (1.0f - currentPositionOffset)
                    + currentPositionOffset
                    * (((float) next_category_text.getWidth()) + next_left);
        }

        int height = 6 * Constants.screenHeight / 1920;
        //改变导航栏指针
//        rect.set(((int) left) + getPaddingLeft()+50, getHeight() - height-7,
//                ((int) width) + getPaddingLeft()-50, getHeight()-7);

        double offset = 0.5 - Math.abs(positionOffset - 0.5);
        int half = (((int) left) + 2*getPaddingLeft() + ((int) width))/2 ;

        rect.set(half - 20 - (int)(offset*40),   getHeight() - height - 7,
                 half + 20 + (int)(offset*40),   getHeight() - 7);
    }

    // 计算滚动范围
    private int getScrollRange() {
        int i = getChildCount() > 0 ? Math.max(0, getChildAt(0).getWidth()
                - getWidth() + getPaddingLeft() + getPaddingRight()) : 0;
        return i;
    }

    private void scrollToChild(int position, int offset,float positionOffset) {
//        LogCat.i("CategoryTabStrip" , "offset:"+offset +" ,position:"+position+" ,tabCount:"+tabCount);

        if (tabCount == 0) {
            return;
        }

        calculateIndicatorRect(indicatorRect,positionOffset);

        if (position == 0 || offset == 0)
            return;

        int newScrollX = tabsContainer.getChildAt(position - 1).getLeft()
                + offset;

//        if (position == 0)
//            return;
//
//        int newScrollX = tabsContainer.getChildAt(position).getLeft()
//                + offset + Constants.screenWidth/2;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

        // int newScrollX = lastScrollX;
        // if (indicatorRect.left < getScrollX() + scrollOffset) {
        // newScrollX = indicatorRect.left - scrollOffset;
        // } else if (indicatorRect.right > getScrollX() + getWidth() -
        // scrollOffset) {
        // newScrollX = indicatorRect.right - getWidth() + scrollOffset;
        // }
        // if (newScrollX != lastScrollX) {
        //
        // lastScrollX = newScrollX;
        // scrollTo(newScrollX, 0);
        // }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        calculateIndicatorRect(indicatorRect,0f);

        if (indicator != null) {
            indicator.setBounds(indicatorRect);
            indicator.draw(canvas);
        }

        // int i = 0;
        // while (i < tabsContainer.getChildCount()) {
        // if (i < currentPosition - 1 || i > currentPosition + 1) {
        // i++;
        // } else {
        // ViewGroup tab = (ViewGroup)tabsContainer.getChildAt(i);
        // TextView category_text = (TextView)
        // tab.findViewById(R.id.category_text);
        // if (category_text != null) {
        // TextDrawable textDrawable = drawables[i - currentPosition + 1];
        // int save = canvas.save();
        // calculateIndicatorRect(indicatorRect);
        // canvas.clipRect(indicatorRect);
        // textDrawable.setText(category_text.getText());
        // textDrawable.setTextSize(0, category_text.getTextSize());
        // textDrawable.setTextColor(getResources().getColor(R.color.category_tab_highlight_text));
        // int left = tab.getLeft() + category_text.getLeft() +
        // (category_text.getWidth() - textDrawable.getIntrinsicWidth()) / 2 +
        // getPaddingLeft();
        // int top = tab.getTop() + category_text.getTop() +
        // (category_text.getHeight() - textDrawable.getIntrinsicHeight()) / 2 +
        // getPaddingTop();
        // textDrawable.setBounds(left, top, textDrawable.getIntrinsicWidth() +
        // left, textDrawable.getIntrinsicHeight() + top);
        // textDrawable.draw(canvas);
        // canvas.restoreToCount(save);
        // }
        // i++;
        // }
        // }
        //
        // i = canvas.save();
        // int top = getScrollX();
        // int height = getHeight();
        // int width = getWidth();
        // canvas.translate((float) top, 0.0f);
        /*
         * if (left_edge == null || top <= 0) { if (right_edge == null || top >=
		 * getScrollRange()) { canvas.restoreToCount(i); }
		 * right_edge.setBounds(width - right_edge.getIntrinsicWidth(), 0,
		 * width, height); right_edge.draw(canvas); canvas.restoreToCount(i); }
		 * left_edge.setBounds(0, 0, left_edge.getIntrinsicWidth(), height);
		 * left_edge.draw(canvas); if (right_edge == null || top >=
		 * getScrollRange()) { canvas.restoreToCount(i); }
		 * right_edge.setBounds(width - right_edge.getIntrinsicWidth(), 0,
		 * width, height); right_edge.draw(canvas);
		 */
        // canvas.restoreToCount(i);
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LogCat.i("CategoryTabStrip","positionOffset===="+positionOffset +"    ,positionOffsetPixels====="+positionOffsetPixels);
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()), positionOffset);
            invalidate();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
//            LogCat.i("CategoryTabStrip","onPageScrollStateChanged------");

            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (pager.getCurrentItem() == 0) {
                    // 滑动到最左边
                    scrollTo(0, 0);
                } else if (pager.getCurrentItem() == tabCount - 1) {
                    // 滑动到最右边
                    scrollTo(getScrollRange(), 0);
                } else {
                    scrollToChild(pager.getCurrentItem(), 0,0);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
//            LogCat.i("CategoryTabStrip","onPageSelected------");

            if (categoryTabListener != null){
                categoryTabListener.onPageSelectedViewPager(position);
            }
            clearTextColor();
            setTextColor(position, getResources()
                    .getColor(R.color.category_tab_text2));
        }

    }

    public void setCityNameText(int postion, String name) {
        TextView textView = (TextView) ((ViewGroup) tabsContainer
                .getChildAt(postion)).getChildAt(0);
        textView.setText(name);
    }

    public void setTextColor(int postion, int color) {
        TextView textView = (TextView) ((ViewGroup) tabsContainer
                .getChildAt(postion)).getChildAt(0);
        textView.setTextColor(color);
       // textView.setBackgroundResource(R.drawable.tag_background3); 背景
    }

    public void clearTextColor() {
        TextView textView;
        for (int i = 0; i < tabsContainer.getChildCount(); i++) {
             textView = (TextView) ((ViewGroup) tabsContainer
                    .getChildAt(i)).getChildAt(0);
            textView.setTextColor(getResources().getColor(
                    R.color.category_tab_text));
           // textView.setBackgroundResource(R.drawable.tag_background2);背景
        }
    }

    public void setInitViewPagerNo(int no) {
        this.pager.setCurrentItem(no);

        if (pager.getCurrentItem() == 0) {
            // 滑动到最左边
            scrollTo(0, 0);
        } else if (pager.getCurrentItem() == pager.getAdapter().getCount() - 1) {
            // 滑动到最右边
            LogCat.i("CategoryTabStrip","setInitViewPagerNo------getScrollRange():"+getScrollRange()+" pos"+no);
            scrollTo(getScrollRange(), 0);
        } else {
            LogCat.i("CategoryTabStrip","setInitViewPagerNo------else:"+pager.getCurrentItem()+" pos"+no);
            //scrollToChild(pager.getCurrentItem(), 0);
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    if (tabsContainer.getChildAt(pager.getCurrentItem()).getWidth() != 0){
                        scrollToChild(pager.getCurrentItem(), -tabsContainer.getChildAt(pager.getCurrentItem()).getWidth()/2,0);
                    }

                    //得到后移除
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

}
