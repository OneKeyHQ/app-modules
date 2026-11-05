package io.intercom.android.sdk.overlay;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.id;

class ChatFull$1 implements OnPreDrawListener {
   // $VF: synthetic field
   final ViewGroup val$blockContainer;
   // $VF: synthetic field
   final ChatFull this$0;

   ChatFull$1(ChatFull var1, ViewGroup var2) {
      this.this$0 = var1;
      this.val$blockContainer = var2;
   }

   @Override
   public boolean onPreDraw() {
      this.val$blockContainer.getViewTreeObserver().removeOnPreDrawListener(this);
      int var1 = this.val$blockContainer.getMeasuredHeight();
      int var2 = ((MarginLayoutParams)this.val$blockContainer.getLayoutParams()).bottomMargin;
      Resources var3 = this.this$0.localisedContext.getResources();
      int var4 = var3.getDimensionPixelSize(dimen.intercom_chat_full_top_margin);
      int var5 = var3.getDimensionPixelSize(dimen.intercom_bottom_padding);
      int var6 = this.this$0.screenHeight - var4 - var2 - var5;
      if (var1 >= var6) {
         View var7 = this.this$0.overlayRoot.findViewById(id.chat_overlay_overflow_fade);
         var7.setVisibility(0);
         return false;
      } else {
         return true;
      }
   }
}
