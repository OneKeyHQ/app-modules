package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.SnapshotStateKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = 560,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$37$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$37$1\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,1034:1\n49#2:1035\n51#2:1039\n46#3:1036\n51#3:1038\n105#4:1037\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$37$1\n*L\n553#1:1035\n553#1:1039\n553#1:1036\n553#1:1038\n553#1:1037\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$37$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final float $lazyListJumpToBottomScrollOffset;
   // $VF: synthetic field
   final Function1 $onConversationScrolled;
   // $VF: synthetic field
   final boolean $isLandscape;
   // $VF: synthetic field
   final boolean $isLargeFont;

   ConversationScreenKt$ConversationScreenContent$37$1(LazyListState var1, float var2, Function1 var3, boolean var4, boolean var5, Continuation var6) {
      super(2, var6);
      this.$lazyListState = var1;
      this.$lazyListJumpToBottomScrollOffset = var2;
      this.$onConversationScrolled = var3;
      this.$isLandscape = var4;
      this.$isLargeFont = var5;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_378_0 = ((io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$37$1) this).$lazyListState;
            Flow var2 = SnapshotStateKt.snapshotFlow(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$37$1.invokeSuspend$lambda$0(recoveredExpressionCapture_378_0)));
            float var3 = this.$lazyListJumpToBottomScrollOffset;
            LazyListState var4 = this.$lazyListState;
            int var5 = 0;
            Flow var6 = var2;
            int var7 = 0;
            int var8 = 0;
            Flow var10000 = FlowKt.debounce(
               new ConversationScreenKt$ConversationScreenContent$37$1$invokeSuspend$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var6, var3, var4),
               ((kotlin.jvm.functions.Function1) (recoveredArgument_379_0) -> ConversationScreenKt$ConversationScreenContent$37$1.invokeSuspend$lambda$2(((java.lang.Boolean)recoveredArgument_379_0).booleanValue()))
            );
            FlowCollector var10001 = new ConversationScreenKt$ConversationScreenContent$37$1$4(
               this.$onConversationScrolled, this.$lazyListState, this.$isLandscape, this.$isLargeFont
            );
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var9) {
               return var9;
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
      return new ConversationScreenKt$ConversationScreenContent$37$1(
         this.$lazyListState, this.$lazyListJumpToBottomScrollOffset, this.$onConversationScrolled, this.$isLandscape, this.$isLargeFont, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationScreenKt$ConversationScreenContent$37$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final List invokeSuspend$lambda$0(LazyListState var0) {
      return var0.getLayoutInfo().getVisibleItemsInfo();
   }

   private static final long invokeSuspend$lambda$2(boolean var0) {
      return var0 ? 200L : 0L;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
