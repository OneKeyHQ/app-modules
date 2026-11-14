package io.intercom.android.sdk.blocks;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import coil.request.ErrorResult;
import coil.request.ImageRequest;
import coil.request.SuccessResult;
import coil.request.ImageRequest.Listener;

class ConversationRatingCard$7 implements Listener {
   // $VF: synthetic field
   final String val$unicode;
   // $VF: synthetic field
   final ImageView val$imageView;
   // $VF: synthetic field
   final ConversationRatingCard this$0;

   ConversationRatingCard$7(ConversationRatingCard var1, String var2, ImageView var3) {
      this.this$0 = var1;
      this.val$unicode = var2;
      this.val$imageView = var3;
   }

   @Override
   public void onStart(@NonNull ImageRequest var1) {
   }

   @Override
   public void onCancel(@NonNull ImageRequest var1) {
   }

   @Override
   public void onError(@NonNull ImageRequest var1, @NonNull ErrorResult var2) {
      int var3 = ConversationRatingCard.getEmojiDrawableForUnicode(this.val$unicode);
      if (var3 != 0) {
         this.val$imageView.setImageResource(var3);
      }
   }

   @Override
   public void onSuccess(@NonNull ImageRequest var1, @NonNull SuccessResult var2) {
   }
}
