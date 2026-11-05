package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.models.Conversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxContentScreenItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxContentScreenItems.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,95:1\n1047#2,6:96\n*S KotlinDebug\n*F\n+ 1 InboxContentScreenItems.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1\n*L\n68#1:96,6\n*E\n"
)
final class InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1 implements Function2 {
   // $VF: synthetic field
   final LazyPagingItems $lazyPagingItems;

   InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1(LazyPagingItems var1) {
      this.$lazyPagingItems = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C67@3070L178,67@3059L189:InboxContentScreenItems.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               853574228, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContentScreenPreview.DisplayPaging.<anonymous> (InboxContentScreenItems.kt:67)"
            );
         }

         Object var10000 = null;
         Object var10001 = null;
         Object var10002 = null;
         boolean var10003 = false;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var1.startReplaceGroup(-614447302);
         ComposerKt.sourceInformation(var1, "CC(remember):InboxContentScreenItems.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.$lazyPagingItems);
         LazyPagingItems var6 = this.$lazyPagingItems;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var10009;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10009 = var8;
         } else {
            Object var20 = null;
            boolean var19 = false;
            Object var18 = null;
            Object var17 = null;
            Object var16 = null;
            boolean var15 = false;
            Object var14 = null;
            Object var13 = null;
            Object var12 = null;
            int var10 = 0;
            final androidx.paging.compose.LazyPagingItems recoveredExpressionCapture_969_0 = var6;
            Function1 var21 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_969_0) -> io.intercom.android.sdk.m5.inbox.ui.InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1.invoke$lambda$2$lambda$1(recoveredExpressionCapture_969_0, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_969_0)));
            var10000 = var12;
            var10001 = var13;
            var10002 = var14;
            var10003 = var15;
            var10004 = var16;
            var10005 = var17;
            var10006 = var18;
            var10007 = var19;
            var10008 = var20;
            Object var11 = var21;
            var4.updateRememberedValue(var11);
            var10009 = var11;
         }

         Function1 var3 = (Function1)var10009;
         var1.endReplaceGroup();
         LazyDslKt.LazyColumn(
            (Modifier)var10000,
            (LazyListState)var10001,
            (PaddingValues)var10002,
            var10003,
            (Vertical)var10004,
            (Horizontal)var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var3,
            var1,
            0,
            511
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$2$lambda$1(LazyPagingItems var0, LazyListScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$LazyColumn");
      InboxContentScreenItemsKt.inboxContentScreenItems$default(
         var1, var0, null, ((kotlin.jvm.functions.Function1) (recoveredArgument_970_0) -> InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1.invoke$lambda$2$lambda$1$lambda$0((io.intercom.android.sdk.models.Conversation)recoveredArgument_970_0)), 2, null
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
