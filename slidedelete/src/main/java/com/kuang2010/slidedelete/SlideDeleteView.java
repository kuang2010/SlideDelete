package com.kuang2010.slidedelete;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

/**
 * author: kuangzeyu2019
 * date: 2020/5/12
 * time: 11:36
 * desc: 使用ViewDragHelper封装的事件机制实现滑动删除
 */
public class SlideDeleteView extends ViewGroup {

    private View mMain_child;
    private View mDelete_child;
    private int mDeleteChildWidth;
    private ViewDragHelper mViewDragHelper;

    public SlideDeleteView(Context context) {
        super(context);
    }

    public SlideDeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MyViewDragHelperCallBack callBack = new MyViewDragHelperCallBack();
        mViewDragHelper = ViewDragHelper.create(this, callBack);

    }


    /**
     * 布局完成的回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMain_child = getChildAt(0);
        mDelete_child = getChildAt(1);
        mDeleteChildWidth = mDelete_child.getLayoutParams().width;
    }

    /**
     * 测量，测量子控件的大小和设置自定义控件的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMain_child.measure(widthMeasureSpec,heightMeasureSpec);
        mDelete_child.measure(MeasureSpec.makeMeasureSpec(mDeleteChildWidth,MeasureSpec.EXACTLY),heightMeasureSpec);
        setMeasuredDimension( MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));

    }


    /**
     * 布局子控件的位置 （ViewGroup）
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布局mainContent的位置
        int test_dx = 0;//-mDelete_child.getMeasuredWidth();
        int main_left = 0 + test_dx;
        int main_top = 0;
        int main_right = mMain_child.getMeasuredWidth() + test_dx;
        int main_bottom = mMain_child.getMeasuredHeight();
        mMain_child.layout(main_left,main_top,main_right,main_bottom);

        //布局rightDelete的位置
        int delete_left = mMain_child.getMeasuredWidth() + test_dx;
        int delete_top = 0;
        int delete_right = mMain_child.getMeasuredWidth()+mDelete_child.getMeasuredWidth() + test_dx;
        int delete_bottom = mDelete_child.getMeasuredHeight();
        mDelete_child.layout(delete_left,delete_top,delete_right,delete_bottom);
    }


    //处理事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);//把事件交给viewdraghelp处理
        return true;//消费事件，不回传
    }

    class MyViewDragHelperCallBack extends ViewDragHelper.Callback{

        //分析哪些子控件
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mMain_child || child == mDelete_child;
        }

        //水平拖动控件X坐标到返回值的位置
        //处理拖动和越界事件
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            // left child组件的左上点的横坐标
            //控制范围
            if (child == mMain_child){
                if (left< -mDelete_child.getMeasuredWidth()){
                    left = -mDelete_child.getMeasuredWidth();
                }else if (left > 0){
                    left = 0;
                }
            }else if (child == mDelete_child){

                if (left < mMain_child.getMeasuredWidth() - mDelete_child.getMeasuredWidth()){
                    left = mMain_child.getMeasuredWidth() - mDelete_child.getMeasuredWidth();
                }else if (left>mMain_child.getMeasuredWidth()){
                    left = mMain_child.getMeasuredWidth();
                }

            }
            return left;
        }

        //child位置改变的回调，处理连体动作事件
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            if (changedView == mMain_child){
                //处理mDelete_child的位置
                int delete_left = left + mMain_child.getMeasuredWidth();
                int delete_top = 0;
                int delete_right = delete_left+mDelete_child.getMeasuredWidth();
                int delete_bottom = mDelete_child.getMeasuredHeight();
                mDelete_child.layout(delete_left,delete_top,delete_right,delete_bottom);
            }

            if (changedView == mDelete_child){
                //处理mMain_child的位置
                int main_left = left - mMain_child.getMeasuredWidth();
                int main_top = 0;
                int main_right = main_left + mMain_child.getMeasuredWidth();
                int main_bottom = mMain_child.getMeasuredHeight();
                mMain_child.layout(main_left,main_top,main_right,main_bottom);
            }
        }

        //事件的松开,显示边界效果
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            float x = mDelete_child.getX();//mDelete_child控件的在屏幕上的x坐标
            if (x<mMain_child.getMeasuredWidth()-mDelete_child.getMeasuredWidth()/2){
                //完全显示delete
                showDeleteChild();

                if (mOnDragViewlistener!=null){
                    mOnDragViewlistener.onDrag(SlideDeleteView.this);
                }
            }else {
                //完全隐藏delete
                hideDeleteVChild();
            }
        }
    }
    //完全隐藏delete + 动画
    public void hideDeleteVChild() {
        mViewDragHelper.smoothSlideViewTo(mMain_child,0,0);
        mViewDragHelper.smoothSlideViewTo(mDelete_child,mMain_child.getMeasuredWidth(),0);
        invalidate();
    }
    //完全显示delete + 动画
    public void showDeleteChild() {
        mViewDragHelper.smoothSlideViewTo(mMain_child,-mDelete_child.getMeasuredWidth(),0);
        mViewDragHelper.smoothSlideViewTo(mDelete_child,mMain_child.getMeasuredWidth()-mDelete_child.getMeasuredWidth(),0);
        invalidate();
    }
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }


    public void hideDeleteVChild2() {
        int delete_left = mMain_child.getMeasuredWidth();
        int delete_top = 0;
        int delete_right = delete_left+mDelete_child.getMeasuredWidth();
        int delete_bottom = mDelete_child.getMeasuredHeight();
        mDelete_child.layout(delete_left,delete_top,delete_right,delete_bottom);

        int main_left = 0;
        int main_top = 0;
        int main_right = main_left + mMain_child.getMeasuredWidth();
        int main_bottom = mMain_child.getMeasuredHeight();
        mMain_child.layout(main_left,main_top,main_right,main_bottom);
    }

    public void showDeleteChild2() {
        int delete_left = mMain_child.getMeasuredWidth() - mDelete_child.getMeasuredWidth();
        int delete_top = 0;
        int delete_right = delete_left+mDelete_child.getMeasuredWidth();
        int delete_bottom = mDelete_child.getMeasuredHeight();
        mDelete_child.layout(delete_left,delete_top,delete_right,delete_bottom);

        int main_left = -mDelete_child.getMeasuredWidth();
        int main_top = 0;
        int main_right = main_left + mMain_child.getMeasuredWidth();
        int main_bottom = mMain_child.getMeasuredHeight();
        mMain_child.layout(main_left,main_top,main_right,main_bottom);
    }

    public interface OnDragViewlistener{
        void onDrag(SlideDeleteView view);
    }
    private OnDragViewlistener mOnDragViewlistener;
    public void setOnDragViewlistener(OnDragViewlistener onDragViewlistener){
        mOnDragViewlistener = onDragViewlistener;
    }
}
