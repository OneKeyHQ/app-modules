package io.intercom.android.sdk.blocks;

import android.view.View;
import android.view.View.OnClickListener;
import io.intercom.android.sdk.blocks.lib.models.ConversationRatingOption;

class ConversationRatingCard$1 implements OnClickListener {
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$1(ConversationRatingCard var1) {
      this.this$0 = var1;
   }

   @Override
   public void onClick(View var1) {
      int var2 = ConversationRatingCard.access$000(this.this$0).indexOf(var1);
      if (var2 != -1 && ConversationRatingCard.access$000(this.this$0).size() == ConversationRatingCard.access$100(this.this$0).getOptions().size()) {
         this.this$0
            .rateConversation(
               ConversationRatingCard.access$100(this.this$0), (ConversationRatingOption)ConversationRatingCard.access$100(this.this$0).getOptions().get(var2)
            );
      }
   }
}
