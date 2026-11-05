package io.intercom.android.sdk.blocks;

class ConversationRatingCard$6$1 implements ConversationRatingCard$RemarkSubmissionListener {
   // $VF: synthetic field
   final ConversationRatingCard$6 this$1;

   ConversationRatingCard$6$1(ConversationRatingCard$6 var1) {
      this.this$1 = var1;
   }

   @Override
   public void onSuccess() {
      this.this$1.val$alert.dismiss();
   }

   @Override
   public void onFailure(boolean var1) {
      ConversationRatingCard.access$800(this.this$1.val$errorTextView, this.this$1.val$okayButton, var1);
   }
}
