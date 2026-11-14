package io.intercom.android.sdk.conversation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import coil.target.Target;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReactionInputView$1 implements Target {
   // $VF: synthetic field
   final ImageView val$reactionImageView;
   // $VF: synthetic field
   final int val$reactionCount;
   // $VF: synthetic field
   final boolean val$animated;
   // $VF: synthetic field
   final Context val$context;
   // $VF: synthetic field
   final ReactionInputView this$0;

   ReactionInputView$1(ReactionInputView var1, ImageView var2, int var3, boolean var4, Context var5) {
      this.this$0 = var1;
      this.val$reactionImageView = var2;
      this.val$reactionCount = var3;
      this.val$animated = var4;
      this.val$context = var5;
   }

   @Override
   public void onStart(@Nullable Drawable var1) {
   }

   @Override
   public void onError(@Nullable Drawable var1) {
   }

   @Override
   public void onSuccess(@NotNull Drawable var1) {
      this.val$reactionImageView.setImageDrawable(var1);
      this.this$0.reactionsLoaded++;
      if (this.this$0.reactionsLoaded == this.val$reactionCount) {
         if (this.val$animated) {
            this.this$0.setVisibility(0);
            this.this$0.setY(this.this$0.getHeight() + ScreenUtils.dpToPx(60.0F, this.val$context));
            this.this$0.animate().setInterpolator(new OvershootInterpolator(0.6F)).translationY(0.0F).setDuration(300L).start();
         } else {
            this.this$0.setVisibility(0);
         }
      }
   }
}
