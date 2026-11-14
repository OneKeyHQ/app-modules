package io.intercom.android.sdk.blocks;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

class ConversationRatingCard$6 implements OnClickListener {
   // $VF: synthetic field
   final TextView val$errorTextView;
   // $VF: synthetic field
   final android.widget.Button val$okayButton;
   // $VF: synthetic field
   final EditText val$input;
   // $VF: synthetic field
   final AlertDialog val$alert;
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$6(ConversationRatingCard var1, TextView var2, android.widget.Button var3, EditText var4, AlertDialog var5) {
      this.this$0 = var1;
      this.val$errorTextView = var2;
      this.val$okayButton = var3;
      this.val$input = var4;
      this.val$alert = var5;
   }

   @Override
   public void onClick(View var1) {
      this.val$errorTextView.setVisibility(8);
      this.val$okayButton.setEnabled(false);
      boolean var2 = ConversationRatingCard.access$900(
         this.this$0, ConversationRatingCard.access$100(this.this$0), this.val$input.getText().toString(), new ConversationRatingCard$6$1(this)
      );
      if (!var2) {
         if (!TextUtils.isEmpty(ConversationRatingCard.access$100(this.this$0).getRemark())) {
            this.val$alert.dismiss();
         } else {
            ConversationRatingCard.access$800(this.val$errorTextView, this.val$okayButton, ConversationRatingCard.access$100(this.this$0).isBlockRating());
         }
      }
   }
}
