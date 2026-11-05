package io.intercom.android.sdk.m5.conversation.ui;

import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreen$19$1 extends FunctionReferenceImpl implements Function1 {
   ConversationScreenKt$ConversationScreen$19$1(Object var1) {
      super(
         1,
         var1,
         ConversationViewModel.class,
         "onFailedMediaUploadItem",
         "onFailedMediaUploadItem(Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;)V",
         0
      );
   }

   public final void invoke(MediaUploadItem var1) {
      Intrinsics.checkNotNullParameter(var1, "p0");
      ((ConversationViewModel)this.receiver).onFailedMediaUploadItem(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      this.invoke((MediaUploadItem)var1);
      return Unit.INSTANCE;
   }
}
