package io.intercom.android.sdk.blocks;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import androidx.appcompat.app.AlertDialog;

class ConversationRatingCard$5 implements OnFocusChangeListener {
   // $VF: synthetic field
   final AlertDialog val$alert;
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$5(ConversationRatingCard var1, AlertDialog var2) {
      this.this$0 = var1;
      this.val$alert = var2;
   }

   @Override
   public void onFocusChange(View var1, boolean var2) {
      if (var2 && this.val$alert.getWindow() != null) {
         this.val$alert.getWindow().setSoftInputMode(5);
      }
   }
}
