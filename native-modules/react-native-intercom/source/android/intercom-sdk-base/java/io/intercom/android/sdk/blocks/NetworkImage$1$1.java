package io.intercom.android.sdk.blocks;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

class NetworkImage$1$1 implements OnClickListener {
   // $VF: synthetic field
   final NetworkImage$1 this$1;

   NetworkImage$1$1(NetworkImage$1 var1) {
      this.this$1 = var1;
   }

   @Override
   public void onClick(View var1) {
      InputMethodManager var2 = (InputMethodManager)this.this$1.val$context.getSystemService("input_method");
      var2.hideSoftInputFromWindow(var1.getWindowToken(), 0);
      NetworkImage.access$100(this.this$1.this$0)
         .onImageClicked(this.this$1.val$url, this.this$1.val$linkUrl, this.this$1.val$imageView, this.this$1.val$widthPx, this.this$1.val$heightPx);
   }
}
