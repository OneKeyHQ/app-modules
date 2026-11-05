package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$GroupedMediaKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$GroupedMediaKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$GroupedMediaKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C303@11722L236:GroupedMedia.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1051510088,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$GroupedMediaKt.lambda-3.<anonymous> (GroupedMedia.kt:303)"
            );
         }

         Block var10002 = BubbleMessageRowKt.getVideoBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10002, "build(...)");
         GroupedMediaKt.GroupedMedia(
            CollectionsKt.listOf(new MediaItem(var10002, true)),
            BubbleMessageRowKt.recovered$f595cb41dc6eb0a1(GroupingPosition.BOTTOM, 0.0F, 0.0F, 6, null),
            null,
            null,
            null,
            var1,
            0,
            28
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
