package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.MutableState;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "StreamingMessageList.kt",
   l = 179,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$StreamingMessageList$14$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$14$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,578:1\n1761#2,3:579\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$14$1\n*L\n177#1:579,3\n*E\n"
)
final class StreamingMessageListKt$StreamingMessageList$14$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final List $contentRows;
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final MutableState $isListAtTheBottom$delegate;

   StreamingMessageListKt$StreamingMessageList$14$1(List var1, LazyListState var2, MutableState var3, Continuation var4) {
      super(2, var4);
      this.$contentRows = var1;
      this.$lazyListState = var2;
      this.$isListAtTheBottom$delegate = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (!StreamingMessageListKt.access$StreamingMessageList$lambda$31(this.$isListAtTheBottom$delegate)) {
               var var2 = this.$contentRows;
               int var3 = 0;
               boolean var10000;
               if (var2 instanceof Collection && ((Collection)var2).isEmpty()) {
                  var10000 = false;
               } else {
                  label66: {
                     for (Object var5 : var2) {
                        ContentRow var6 = (ContentRow)var5;
                        int var7 = 0;
                        if ((var6 instanceof ContentRow$BubbleMessageRow ? (ContentRow$BubbleMessageRow)var6 : null) != null
                           ? (var6 instanceof ContentRow$BubbleMessageRow ? (ContentRow$BubbleMessageRow)var6 : null).isPending()
                           : false) {
                           var10000 = true;
                           break label66;
                        }
                     }

                     var10000 = false;
                  }
               }

               if (!var10000) {
                  break;
               }
            }

            LazyListState var9 = this.$lazyListState;
            Continuation var10003 = this;
            this.label = 1;
            if (LazyListState.animateScrollToItem$default(var9, Integer.MAX_VALUE, 0, var10003, 2, null) == var8) {
               return var8;
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
      return new StreamingMessageListKt$StreamingMessageList$14$1(this.$contentRows, this.$lazyListState, this.$isListAtTheBottom$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((StreamingMessageListKt$StreamingMessageList$14$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
