package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C119@3652L158:BottomBarButtonComponent.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1877738862,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$BottomBarButtonComponentKt.lambda-5.<anonymous> (BottomBarButtonComponent.kt:119)"
            );
         }

         BottomBarButtonComponentKt.BottomBarButtonComponent(
            null, CollectionsKt.listOf(BottomBarUiState$BottomBarButton$MediaInsert.INSTANCE), false, null, null, null, var1, 48, 61
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
