package io.intercom.android.sdk.conversation;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class ReactionInputView$2 implements OnTouchListener {
   private final Rect touchRect;
   // $VF: synthetic field
   final ReactionInputView this$0;

   ReactionInputView$2(ReactionInputView var1) {
      this.this$0 = var1;
      this.touchRect = new Rect();
   }

   @Override
   public boolean onTouch(View var1, MotionEvent var2) {
      int var3 = var2.getActionMasked();
      switch (var3) {
         case 0:
         case 2:
            this.handleTouchMove(var2);
            break;
         case 1:
            this.this$0.handleTouchUp();
      }

      return true;
   }

   private void handleTouchMove(MotionEvent var1) {
      this.this$0.getHitRect(this.touchRect);
      if (this.touchRect.contains((int)var1.getX(), (int)var1.getY())) {
         for (int var2 = 0; var2 < this.this$0.getChildCount(); var2++) {
            this.this$0.getChildAt(var2).getHitRect(this.touchRect);
            if (this.touchRect.contains((int)var1.getX(), (int)var1.getY())) {
               if (this.this$0.highlightedViewIndex == null || this.this$0.highlightedViewIndex != var2) {
                  this.this$0.vibrateForSelection();
                  this.this$0.selectViewAtIndex(2.5F, var2);
               }

               if (this.this$0.highlightedViewIndex == null) {
                  Integer var3 = this.this$0.getCurrentSelectedIndex();
                  if (var3 != null && var3 != var2) {
                     this.this$0.deselectViewAtIndex(var3);
                  }
               } else if (this.this$0.highlightedViewIndex != var2) {
                  this.this$0.deselectViewAtIndex(this.this$0.highlightedViewIndex);
               }

               this.this$0.highlightedViewIndex = var2;
            }
         }
      } else {
         if (this.this$0.highlightedViewIndex != null) {
            this.this$0.highlightSelectedReaction();
         }

         this.this$0.highlightedViewIndex = null;
      }
   }
}
