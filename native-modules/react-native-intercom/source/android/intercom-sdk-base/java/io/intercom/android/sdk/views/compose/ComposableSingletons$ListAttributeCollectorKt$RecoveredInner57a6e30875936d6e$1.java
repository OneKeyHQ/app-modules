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
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nListAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$ListAttributeCollectorKt$lambda-8$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,224:1\n118#2:225\n*S KotlinDebug\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$ListAttributeCollectorKt$lambda-8$1\n*L\n207#1:225\n*E\n"
)
final class ComposableSingletons$ListAttributeCollectorKt$RecoveredInner57a6e30875936d6e$1 implements Function2 {
   public static final ComposableSingletons$ListAttributeCollectorKt$RecoveredInner57a6e30875936d6e$1 INSTANCE = new ComposableSingletons$ListAttributeCollectorKt$RecoveredInner57a6e30875936d6e$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C205@7163L553:ListAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1068973666,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.ComposableSingletons$ListAttributeCollectorKt.lambda-8.<anonymous> (ListAttributeCollector.kt:205)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 8;
         int var4 = 0;
         var10000 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var3));
         String[] var5 = new String[]{"Android", "iOS"};
         ListAttributeCollectorKt.ListAttributeCollector(
            var10000,
            new AttributeData(new Attribute("", "Name", "string", true, null, CollectionsKt.listOf(var5), null, "Android", 80, null), "123", true),
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
