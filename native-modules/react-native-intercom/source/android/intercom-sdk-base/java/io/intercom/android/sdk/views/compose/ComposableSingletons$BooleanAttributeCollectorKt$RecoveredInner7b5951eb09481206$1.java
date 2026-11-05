package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.Attribute;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBooleanAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BooleanAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$BooleanAttributeCollectorKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,263:1\n118#2:264\n*S KotlinDebug\n*F\n+ 1 BooleanAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$BooleanAttributeCollectorKt$lambda-1$1\n*L\n180#1:264\n*E\n"
)
final class ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$BooleanAttributeCollectorKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C178@6038L457:BooleanAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -791227948,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.lambda-1.<anonymous> (BooleanAttributeCollector.kt:178)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 8;
         int var4 = 0;
         BooleanAttributeCollectorKt.BooleanAttributeCollector(
            PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3)),
            new AttributeData(new Attribute("", "", "", false, null, null, null, null, 240, null), "123", false),
            false,
            false,
            null,
            var1,
            6,
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
