package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.ui.components.row.FileStyle;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,438:1\n118#2:439\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-2$1\n*L\n268#1:439\n*E\n"
)
final class ComposableSingletons$MediaItemsKt$RecoveredInner7a24458254dd4cd9$1 implements Function3 {
   public static final ComposableSingletons$MediaItemsKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$MediaItemsKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(FileStyle var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "fileStyle");
      ComposerKt.sourceInformation(var2, "C266@9679L152:MediaItems.kt#yb6g9");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1081903419,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.lambda-2.<anonymous> (MediaItems.kt:266)"
            );
         }

         int var5 = 20;
         int var6 = 0;
         MediaItemsKt.recovered$8892cb5149e057df(Dp.recovered$95b3d103663bf060(var5), var1.recovered$01833f34228693e4(), var2, 6, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((FileStyle)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
