package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nQuickReplies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$QuickRepliesKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,144:1\n1047#2,6:145\n*S KotlinDebug\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$QuickRepliesKt$lambda-1$1\n*L\n139#1:145,6\n*E\n"
)
final class ComposableSingletons$QuickRepliesKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$QuickRepliesKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$QuickRepliesKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C138@4757L2,130@4404L370:QuickReplies.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1955973464,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$QuickRepliesKt.lambda-1.<anonymous> (QuickReplies.kt:130)"
            );
         }

         QuickReply[] var3 = new QuickReply[]{
            new QuickReply("1", "Option 1"),
            new QuickReply("2", "Option 2"),
            new QuickReply("3", "Option 3"),
            new QuickReply("4", "Option 4"),
            new QuickReply("5", "Option 5")
         };
         List var10000 = CollectionsKt.listOf(var3);
         var1.startReplaceGroup(2125390642);
         ComposerKt.sourceInformation(var1, "CC(remember):QuickReplies.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function1 var13;
         if (var7 == Composer.Companion.getEmpty()) {
            List var10 = var10000;
            int var9 = 0;
            var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_698_0) -> ComposableSingletons$QuickRepliesKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply)recoveredArgument_698_0));
            var10000 = var10;
            Object var12 = var13;
            var4.updateRememberedValue(var12);
            var13 = (Function1)var12;
         } else {
            var13 = (Function1)var7;
         }

         Function1 var11 = var13;
         var1.endReplaceGroup();
         QuickRepliesKt.QuickReplies(var10000, var11, null, var1, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(QuickReply var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
