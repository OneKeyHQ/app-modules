package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSegmentedFinLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$SegmentedFinLayoutKt$lambda-2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,168:1\n118#2:169\n*S KotlinDebug\n*F\n+ 1 SegmentedFinLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ComposableSingletons$SegmentedFinLayoutKt$lambda-2$1\n*L\n138#1:169\n*E\n"
)
final class ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C131@5855L43,136@6129L6,142@6368L229,148@6629L205,133@6003L842:SegmentedFinLayout.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               980615181,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$SegmentedFinLayoutKt.lambda-2.<anonymous> (SegmentedFinLayout.kt:131)"
            );
         }

         FinRowStyle var3 = FinAnswerRowKt.getFinRowStyle(GroupingPosition.STANDALONE, var1, 6);
         Block[] var5 = new Block[]{BubbleMessageRowKt.getCodeBlock().build(), BubbleMessageRowKt.getAnswerBlock().build()};
         List var4 = ContentSegmentKt.segmentStandaloneBlocks(CollectionsKt.listOf(var5));
         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         int var6 = 16;
         int var7 = 0;
         Modifier var8 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var6));
         SegmentedFinLayoutKt.SegmentedFinLayout(
            var4,
            var3,
            true,
            ComposableSingletons$SegmentedFinLayoutKt.INSTANCE.recovered$b26556cd5c386b55(),
            ComposableLambdaKt.rememberComposableLambda(
               -379625014, true, new ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$1(var3), var1, 54
            ),
            ComposableLambdaKt.rememberComposableLambda(
               767924054, true, new ComposableSingletons$SegmentedFinLayoutKt$RecoveredInner7a24458254dd4cd9$1$2(var3), var1, 54
            ),
            var8,
            var1,
            224640,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
