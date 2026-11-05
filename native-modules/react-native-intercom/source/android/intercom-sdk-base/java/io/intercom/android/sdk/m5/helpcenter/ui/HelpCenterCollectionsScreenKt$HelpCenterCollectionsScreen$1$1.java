package io.intercom.android.sdk.m5.helpcenter.ui;

import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
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
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "HelpCenterCollectionsScreen.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final HelpCenterViewModel $viewModel;
   // $VF: synthetic field
   final List $collectionIds;

   HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1(HelpCenterViewModel var1, List var2, Continuation var3) {
      super(2, var3);
      this.$viewModel = var1;
      this.$collectionIds = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            this.$viewModel.fetchCollections(CollectionsKt.toSet(this.$collectionIds));
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1(this.$viewModel, this.$collectionIds, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
