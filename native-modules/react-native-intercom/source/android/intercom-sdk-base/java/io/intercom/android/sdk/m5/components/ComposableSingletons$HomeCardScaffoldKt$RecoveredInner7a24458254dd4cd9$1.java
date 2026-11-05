package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HomeCardScaffoldKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$HomeCardScaffoldKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$HomeCardScaffoldKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C41@1211L386:HomeCardScaffold.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               277938312,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$HomeCardScaffoldKt.lambda-2.<anonymous> (HomeCardScaffold.kt:41)"
            );
         }

         HomeCardScaffoldKt.HomeCardScaffold(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            "Recent X",
            ComposableSingletons$HomeCardScaffoldKt.INSTANCE.recovered$eac478aa65045e5d(),
            var1,
            438,
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
