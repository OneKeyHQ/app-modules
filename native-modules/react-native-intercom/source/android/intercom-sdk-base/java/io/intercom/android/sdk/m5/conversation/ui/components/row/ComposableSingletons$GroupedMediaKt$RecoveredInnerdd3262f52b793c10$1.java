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
final class ComposableSingletons$GroupedMediaKt$RecoveredInnerdd3262f52b793c10$1 implements Function2 {
   public static final ComposableSingletons$GroupedMediaKt$RecoveredInnerdd3262f52b793c10$1 INSTANCE = new ComposableSingletons$GroupedMediaKt$RecoveredInnerdd3262f52b793c10$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C408@15182L690:GroupedMedia.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -839142036,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$GroupedMediaKt.lambda-15.<anonymous> (GroupedMedia.kt:408)"
            );
         }

         MediaItem[] var3 = new MediaItem[7];
         Block var10004 = BubbleMessageRowKt.getImageBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[0] = new MediaItem(var10004, false);
         var10004 = BubbleMessageRowKt.getVideoBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[1] = new MediaItem(var10004, true);
         var10004 = BubbleMessageRowKt.getImageBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[2] = new MediaItem(var10004, false);
         var10004 = BubbleMessageRowKt.getImageBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[3] = new MediaItem(var10004, false);
         var10004 = BubbleMessageRowKt.getVideoBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[4] = new MediaItem(var10004, true);
         var10004 = BubbleMessageRowKt.getImageBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[5] = new MediaItem(var10004, false);
         var10004 = BubbleMessageRowKt.getVideoBlock().build();
         Intrinsics.checkNotNullExpressionValue(var10004, "build(...)");
         var3[6] = new MediaItem(var10004, true);
         GroupedMediaKt.GroupedMedia(
            CollectionsKt.listOf(var3),
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
