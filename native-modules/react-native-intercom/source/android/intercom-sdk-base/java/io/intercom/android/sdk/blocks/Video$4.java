package io.intercom.android.sdk.blocks;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import io.intercom.android.sdk.utilities.IntentUtils;

class Video$4 implements OnClickListener {
   // $VF: synthetic field
   final String val$videoUrl;
   // $VF: synthetic field
   final ImageView val$imageView;
   // $VF: synthetic field
   final Video this$0;

   Video$4(Video var1, String var2, ImageView var3) {
      this.this$0 = var1;
      this.val$videoUrl = var2;
      this.val$imageView = var3;
   }

   @Override
   public void onClick(View var1) {
      Uri var2 = Uri.parse(this.val$videoUrl);
      Intent var3 = new Intent("android.intent.action.VIEW", var2);
      var3.setFlags(268435456);
      IntentUtils.safelyOpenIntent(this.val$imageView.getContext(), var3);
   }
}
