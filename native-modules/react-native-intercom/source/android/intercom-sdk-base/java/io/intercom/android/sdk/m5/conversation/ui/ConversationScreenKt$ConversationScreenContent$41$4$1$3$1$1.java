package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.foundation.lazy.LazyListItemInfo;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.ui.unit.Density;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$JumpToBottomIndicator;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ConversationScreen.kt",
   l = {809, 812},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1034:1\n360#2,7:1035\n1#3:1042\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1\n*L\n803#1:1035,7\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ConversationUiState $uiState;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final Density $density;
   // $VF: synthetic field
   final Function1 $onJumpToBottomButtonClicked;

   ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1(ConversationUiState var1, LazyListState var2, Density var3, Function1 var4, Continuation var5) {
      super(2, var5);
      this.$uiState = var1;
      this.$lazyListState = var2;
      this.$density = var3;
      this.$onJumpToBottomButtonClicked = var4;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            List var3 = ((ConversationUiState$Content)this.$uiState).getContentRows();
            int var4 = 0;
            int var5 = 0;
            Iterator var6 = var3.iterator();

            int var10000;
            while (true) {
               if (!var6.hasNext()) {
                  var10000 = -1;
                  break;
               }

               Object var7 = var6.next();
               ContentRow var8 = (ContentRow)var7;
               int var9 = 0;
               if (StringsKt.startsWith$default(var8.getKey(), "new_messages_row", false, 2, null)) {
                  var10000 = var5;
                  break;
               }

               var5++;
            }

            int var2 = var10000;
            int var11 = ((FloatingIndicatorState$JumpToBottomIndicator)((ConversationUiState$Content)this.$uiState).getFloatingIndicatorState())
               .getJumpToBottomButtonState()
               .getLastSeenItemIndex();
            LazyListItemInfo var18 = (LazyListItemInfo)CollectionsKt.lastOrNull(this.$lazyListState.getLayoutInfo().getVisibleItemsInfo());
            var4 = var18 != null ? var18.getIndex() : 0;
            Density var16 = this.$density;
            int var17 = 0;
            var5 = var16.recovered$308ab30a2dba1f24(ConversationScreenKt.access$getJumpToBottomNewMessagesTopOffset$p());
            if (var2 > var11) {
               LazyListState var20 = this.$lazyListState;
               int var22 = -var5;
               Continuation var23 = this;
               this.label = 1;
               if (var20.animateScrollToItem(var2, var22, var23) == var10) {
                  return var10;
               }
            } else {
               int var14 = Math.max(var11, var4);
               LazyListState var19 = this.$lazyListState;
               int var10001 = var14 + 1;
               int var10002 = -var5;
               Continuation var10003 = this;
               this.label = 2;
               if (var19.animateScrollToItem(var10001, var10002, var10003) == var10) {
                  return var10;
               }
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      LazyListItemInfo var21 = (LazyListItemInfo)CollectionsKt.lastOrNull(this.$lazyListState.getLayoutInfo().getVisibleItemsInfo());
      int var15 = var21 != null ? var21.getIndex() : 0;
      this.$onJumpToBottomButtonClicked.invoke(Boxing.boxInt(var15));
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1(
         this.$uiState, this.$lazyListState, this.$density, this.$onJumpToBottomButtonClicked, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ConversationScreenKt$ConversationScreenContent$41$4$1$3$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
