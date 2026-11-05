package io.intercom.android.sdk.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.ScrollView;
import io.intercom.android.sdk.R.styleable;

public class LockableScrollView extends ScrollView {
   private boolean scrollable = true;
   private int maxHeight = 0;
   private boolean isExpanded = true;
   private boolean interceptTouch = false;

   public LockableScrollView(Context var1) {
      super(var1);
   }

   public LockableScrollView(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setUp(var2);
   }

   public LockableScrollView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.setUp(var2);
   }

   public void setUp(AttributeSet var1) {
      TypedArray var2 = this.getContext().obtainStyledAttributes(var1, styleable.LockableScrollView);
      this.maxHeight = var2.getDimensionPixelSize(styleable.LockableScrollView_intercomHeightLimit, 0);
      this.isExpanded = var2.getBoolean(styleable.LockableScrollView_intercomExpanded, true);
      this.interceptTouch = var2.getBoolean(styleable.LockableScrollView_intercomInterceptTouch, true);
      var2.recycle();
   }

   public void setScrollingEnabled(boolean var1) {
      this.scrollable = var1;
   }

   @Override
   protected void onMeasure(int var1, int var2) {
      int var3 = var2;
      if (!this.isExpanded) {
         int var4 = MeasureSpec.getMode(var2);
         switch (var4) {
            case Integer.MIN_VALUE:
            case 1073741824:
               int var5 = Math.min(MeasureSpec.getSize(var2), this.maxHeight);
               var3 = MeasureSpec.makeMeasureSpec(var5, var4);
               break;
            case 0:
            default:
               var3 = MeasureSpec.makeMeasureSpec(this.maxHeight, Integer.MIN_VALUE);
         }
      }

      super.onMeasure(var1, var3);
   }

   @Override
   public boolean onTouchEvent(MotionEvent var1) {
      return this.scrollable && super.onTouchEvent(var1);
   }

   @Override
   public void scrollTo(int var1, int var2) {
      if (this.scrollable) {
         super.scrollTo(var1, var2);
      }
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent var1) {
      return this.interceptTouch;
   }

   public void toggleExpanded() {
      this.setExpanded(!this.isExpanded);
   }

   public void setExpanded(boolean var1) {
      if (var1 != this.isExpanded) {
         this.isExpanded = var1;
         this.requestLayout();
         this.invalidate();
      }
   }

   public int getMaxHeight() {
      return this.maxHeight;
   }

   public boolean isExpanded() {
      return this.isExpanded;
   }
}
