package io.intercom.android.sdk.blocks;

import io.intercom.android.sdk.api.ErrorObject;
import io.intercom.android.sdk.api.RatingCallback;
import io.intercom.android.sdk.blocks.lib.models.ConversationRating;

class ConversationRatingCard$3 implements RatingCallback {
   // $VF: synthetic field
   final ConversationRating val$conversationRating;
   // $VF: synthetic field
   final String val$remark;
   // $VF: synthetic field
   final ConversationRatingCard$RemarkSubmissionListener val$listener;
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$3(ConversationRatingCard var1, ConversationRating var2, String var3, ConversationRatingCard$RemarkSubmissionListener var4) {
      this.this$0 = var1;
      this.val$conversationRating = var2;
      this.val$remark = var3;
      this.val$listener = var4;
   }

   @Override
   public void onSuccess() {
      ConversationRatingCard.access$202(this.this$0, false);
      this.val$conversationRating.setRemark(this.val$remark);
      ConversationRatingCard.access$300(this.this$0);
      ConversationRatingCard.access$400(this.this$0);
      if (this.val$listener != null) {
         this.val$listener.onSuccess();
      }
   }

   @Override
   public void onFailure(ErrorObject var1) {
      ConversationRatingCard.access$202(this.this$0, false);
      boolean var2 = ConversationRatingCard.access$600(var1);
      if (var2) {
         this.val$conversationRating.setBlockRating(true);
         ConversationRatingCard.access$400(this.this$0);
      }

      ConversationRatingCard.access$300(this.this$0);
      if (this.val$listener != null) {
         this.val$listener.onFailure(var2);
      }
   }
}
