package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.usecase.SendMessageUseCase;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableStateFlow;

@DebugMetadata(
   f = "ConversationViewModel.kt",
   l = 691,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ConversationViewModel$onRetryMessageClicked$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$onRetryMessageClicked$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,864:1\n1563#2:865\n1634#2,3:866\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModel$onRetryMessageClicked$1\n*L\n693#1:865\n693#1:866,3\n*E\n"
)
final class ConversationViewModel$onRetryMessageClicked$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationViewModel this$0;
   // $VF: synthetic field
   final Part $part;

   ConversationViewModel$onRetryMessageClicked$1(ConversationViewModel var1, Part var2, Continuation var3) {
      super(2, var3);
      this.this$0 = var1;
      this.$part = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var14 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            SendMessageUseCase var10000 = ConversationViewModel.access$getSendMessageUseCase$p(this.this$0);
            MutableStateFlow var10001 = ConversationViewModel.access$getClientState$p(this.this$0);
            List var10002 = this.$part.getBlocks();
            Intrinsics.checkNotNullExpressionValue(var10002, "getBlocks(...)");
            Iterable var2 = var10002;
            MutableStateFlow var12 = var10001;
            SendMessageUseCase var11 = var10000;
            int var3 = 0;
            Iterable var4 = var2;
            ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
            int var6 = 0;

            for (Object var8 : var4) {
               Block var9 = (Block)var8;
               Collection var13 = var5;
               int var10 = 0;
               var13.add(var9.toBuilder());
            }

            List var15 = (List)var5;
            String var10003 = this.$part.getUuid();
            Intrinsics.checkNotNullExpressionValue(var10003, "getUuid(...)");
            Continuation var10004 = this;
            this.label = 1;
            if (var11.invoke(var12, var15, var10003, var10004) == var14) {
               return var14;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationViewModel$onRetryMessageClicked$1(this.this$0, this.$part, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationViewModel$onRetryMessageClicked$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
