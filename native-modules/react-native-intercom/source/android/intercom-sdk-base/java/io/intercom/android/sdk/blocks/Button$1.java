package io.intercom.android.sdk.blocks;

import android.view.View;
import android.view.View.OnClickListener;

class Button$1 implements OnClickListener {
   // $VF: synthetic field
   final String val$url;
   // $VF: synthetic field
   final Button this$0;

   Button$1(Button var1, String var2) {
      this.this$0 = var1;
      this.val$url = var2;
   }

   @Override
   public void onClick(View var1) {
      Button.access$000(this.this$0).onButtonClicked(var1, this.val$url);
   }
}
