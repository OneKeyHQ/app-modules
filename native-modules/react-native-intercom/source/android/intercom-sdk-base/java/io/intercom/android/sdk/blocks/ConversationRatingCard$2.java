package io.intercom.android.sdk.blocks;

import android.widget.Toast;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.api.ErrorObject;
import io.intercom.android.sdk.api.RatingCallback;
import io.intercom.android.sdk.blocks.lib.models.ConversationRating;
import io.intercom.android.sdk.blocks.lib.models.ConversationRatingOption;

class ConversationRatingCard$2 implements RatingCallback {
   // $VF: synthetic field
   final ConversationRating val$conversationRating;
   // $VF: synthetic field
   final ConversationRatingOption val$conversationRatingOption;
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$2(ConversationRatingCard var1, ConversationRating var2, ConversationRatingOption var3) {
      this.this$0 = var1;
      this.val$conversationRating = var2;
      this.val$conversationRatingOption = var3;
   }

   @Override
   public void onSuccess() {
      ConversationRatingCard.access$202(this.this$0, false);
      this.val$conversationRating.setRatingIndex(this.val$conversationRatingOption.getIndex());
      ConversationRatingCard.access$300(this.this$0);
      ConversationRatingCard.access$400(this.this$0);
      ConversationRatingCard.access$500(this.this$0);
   }

   @Override
   public void onFailure(ErrorObject var1) {
      ConversationRatingCard.access$202(this.this$0, false);
      if (ConversationRatingCard.access$600(var1)) {
         this.val$conversationRating.setBlockRating(true);
         ConversationRatingCard.access$400(this.this$0);
      } else {
         Toast.makeText(ConversationRatingCard.access$700(this.this$0).getContext(), string.intercom_something_went_wrong_try_again, 1).show();
      }

      ConversationRatingCard.access$300(this.this$0);
      ConversationRatingCard.access$500(this.this$0);
   }
}
