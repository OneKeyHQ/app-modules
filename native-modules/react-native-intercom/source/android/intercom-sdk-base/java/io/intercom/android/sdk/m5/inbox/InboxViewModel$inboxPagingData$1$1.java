package io.intercom.android.sdk.m5.inbox;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.EmptyState$Action;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@DebugMetadata(
   f = "InboxViewModel.kt",
   l = 77,
   i = 0,
   s = "L$0",
   n = "it",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.inbox.InboxViewModel$inboxPagingData$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "it", "Lio/intercom/android/sdk/models/EmptyState;"}
)
@SourceDebugExtension(
   "SMAP\nInboxViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxViewModel.kt\nio/intercom/android/sdk/m5/inbox/InboxViewModel$inboxPagingData$1$1\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,177:1\n1#2:178\n*E\n"
)
final class InboxViewModel$inboxPagingData$1$1 extends SuspendLambda implements Function2 {
   Object L$1;
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   final InboxViewModel this$0;

   InboxViewModel$inboxPagingData$1$1(InboxViewModel var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      InboxViewModel var10;
      EmptyState var16;
      label40: {
         Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         EmptyState var2;
         InboxViewModel var7;
         Object var10000;
         switch (this.label) {
            case 0:
               ResultKt.throwOnFailure(var1);
               var2 = (EmptyState)this.L$0;
               var10 = this.this$0;
               if (Intrinsics.areEqual(var2, EmptyState.Companion.getNULL())) {
                  var16 = var2;
                  break label40;
               }

               var7 = var10;
               CommonRepository var11 = InboxViewModel.access$getCommonRepository$p(this.this$0);
               Continuation var10001 = this;
               this.L$0 = var2;
               this.L$1 = var7;
               this.label = 1;
               var10000 = var11.openMessenger(var10001);
               if (var10000 == var8) {
                  return var8;
               }
               break;
            case 1:
               var7 = (InboxViewModel)this.L$1;
               var2 = (EmptyState)this.L$0;
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         String var13;
         var10 = var7;
         NetworkResponse var3 = (NetworkResponse)var10000;
         label30:
         if (var3 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success) {
            io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var14 = ((OpenMessengerResponse)((io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)var3).getBody()).getNewConversationData();
            if (var14 != null) {
               io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta var15 = var14.getCta();
               if (var15 != null) {
                  var13 = var15.getText();
                  break label30;
               }
            }

            var13 = null;
         } else {
            var13 = null;
         }

         String var4 = var13;
         if (var4 != null) {
            String var5 = var4;
            InboxViewModel var9 = var7;
            int var6 = 0;
            var16 = EmptyState.copy$default(var2, null, null, io.intercom.android.sdk.models.EmptyState$Action.copy$default(var2.getAction(), null, var5, null, 5, null), 3, null);
            var10 = var9;
            if (var16 != null) {
               break label40;
            }
         }

         var16 = var2;
      }

      InboxViewModel.access$setEmptyState$p(var10, var16);
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      InboxViewModel$inboxPagingData$1$1 var3 = new InboxViewModel$inboxPagingData$1$1(this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(EmptyState var1, Continuation var2) {
      return ((InboxViewModel$inboxPagingData$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((EmptyState)var1, (Continuation)var2);
   }
}
