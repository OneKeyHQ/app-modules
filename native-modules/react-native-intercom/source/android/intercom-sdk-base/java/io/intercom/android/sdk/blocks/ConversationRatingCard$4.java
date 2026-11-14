package io.intercom.android.sdk.blocks;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

class ConversationRatingCard$4 implements OnClickListener {
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$4(ConversationRatingCard var1) {
      this.this$0 = var1;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      var1.cancel();
   }
}
