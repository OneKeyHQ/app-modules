package io.intercom.android.sdk.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ContentAwareScrollView extends ScrollView {
   private ContentAwareScrollView$Listener listener;

   public ContentAwareScrollView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   public ContentAwareScrollView(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   public ContentAwareScrollView(Context var1) {
      super(var1);
   }

   @Override
   protected void onScrollChanged(int var1, int var2, int var3, int var4) {
      super.onScrollChanged(var1, var2, var3, var4);
      this.notifyListenerIfAtBottom();
      this.notifyListenerScrollChanged(var2);
   }

   @Override
   protected void onDraw(Canvas var1) {
      super.onDraw(var1);
      this.notifyListenerIfAtBottom();
   }

   protected void notifyListenerScrollChanged(int var1) {
      if (this.listener != null) {
         this.listener.onScrollChanged(var1);
      }
   }

   public boolean isAtBottom() {
      int var1 = this.getChildAt(0).getBottom();
      if (var1 == 0) {
         return false;
      }

      int var2 = this.getBottom() + this.getScrollY();
      return var2 >= var1;
   }

   public ContentAwareScrollView$Listener getListener() {
      return this.listener;
   }

   public void setListener(ContentAwareScrollView$Listener var1) {
      this.listener = var1;
      this.notifyListenerIfAtBottom();
   }

   protected void notifyListenerIfAtBottom() {
      if (this.listener != null && this.isAtBottom()) {
         this.listener.onBottomReached();
      }
   }
}
