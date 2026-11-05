package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.Attribute;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nAttributeCollectorCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$AttributeCollectorCardKt$lambda-3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,238:1\n118#2:239\n*S KotlinDebug\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/ComposableSingletons$AttributeCollectorCardKt$lambda-3$1\n*L\n167#1:239\n*E\n"
)
final class ComposableSingletons$AttributeCollectorCardKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$AttributeCollectorCardKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$AttributeCollectorCardKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C165@7188L470:AttributeCollectorCard.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               956990790,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.ComposableSingletons$AttributeCollectorCardKt.lambda-3.<anonymous> (AttributeCollectorCard.kt:165)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 8;
         int var5 = 0;
         Modifier var3 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var4));
         String[] var7 = new String[]{"Apple", "Orange", "Kiwi"};
         List var6 = CollectionsKt.listOf(new Attribute("list", "Choose one", "string", false, null, CollectionsKt.listOf(var7), null, null, 208, null));
         AttributeCollectorCardKt.AttributeCollectorCard(var3, var6, null, null, "", false, null, var1, 24582, 108);
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
