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
   "SMAP\nTextAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$TextAttributeCollectorKt$lambda-5$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,367:1\n118#2:368\n*S KotlinDebug\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$TextAttributeCollectorKt$lambda-5$1\n*L\n283#1:368\n*E\n"
)
final class ComposableSingletons$TextAttributeCollectorKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$TextAttributeCollectorKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$TextAttributeCollectorKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C281@9731L508:TextAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1749763811,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.ComposableSingletons$TextAttributeCollectorKt.lambda-5.<anonymous> (TextAttributeCollector.kt:281)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 4;
         int var4 = 0;
         TextAttributeCollectorKt.TextAttributeCollector(
            PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3)),
            new AttributeData(new Attribute("", "Name", "string", true, null, null, null, "Submitted", 112, null), "123", false),
            false,
            null,
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
