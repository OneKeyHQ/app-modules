package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.lazy.LazyListItemInfo;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
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
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

@DebugMetadata(
   f = "PreviewBottomBar.kt",
   l = 97,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt$ThumbnailList$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nPreviewBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt$ThumbnailList$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,135:1\n1563#2:136\n1634#2,3:137\n*S KotlinDebug\n*F\n+ 1 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt$ThumbnailList$1$1\n*L\n96#1:136\n96#1:137,3\n*E\n"
)
final class PreviewBottomBarKt$ThumbnailList$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final LazyListState $listState;
   // $VF: synthetic field
   final MutableState $visibleItems;

   PreviewBottomBarKt$ThumbnailList$1$1(LazyListState var1, MutableState var2, Continuation var3) {
      super(2, var3);
      this.$listState = var1;
      this.$visibleItems = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_1140_0 = ((io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt$ThumbnailList$1$1) this).$listState;
            Flow var10000 = SnapshotStateKt.snapshotFlow(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt$ThumbnailList$1$1.invokeSuspend$lambda$1(recoveredExpressionCapture_1140_0)));
            FlowCollector var10001 = new PreviewBottomBarKt$ThumbnailList$1$1$2(this.$visibleItems);
            Continuation var10002 = this;
            this.label = 1;
            if (var10000.collect(var10001, var10002) == var2) {
               return var2;
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
      return new PreviewBottomBarKt$ThumbnailList$1$1(this.$listState, this.$visibleItems, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreviewBottomBarKt$ThumbnailList$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final List invokeSuspend$lambda$1(LazyListState var0) {
      Iterable var1 = var0.getLayoutInfo().getVisibleItemsInfo();
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         LazyListItemInfo var8 = (LazyListItemInfo)var7;
         Collection var10 = var4;
         int var9 = 0;
         var10.add(var8.getIndex());
      }

      return (List)var4;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
