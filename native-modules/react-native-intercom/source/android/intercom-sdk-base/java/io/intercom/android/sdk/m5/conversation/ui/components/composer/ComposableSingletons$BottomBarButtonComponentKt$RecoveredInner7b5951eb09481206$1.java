package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$BottomBarButtonComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C89@2909L293:BottomBarButtonComponent.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               682629698,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$BottomBarButtonComponentKt.lambda-1.<anonymous> (BottomBarButtonComponent.kt:89)"
            );
         }

         BottomBarUiState$BottomBarButton[] var3 = new BottomBarUiState$BottomBarButton[]{
            BottomBarUiState$BottomBarButton$MediaInsert.INSTANCE,
            BottomBarUiState$BottomBarButton$GifInsert.INSTANCE,
            BottomBarUiState$BottomBarButton$VoiceNotesInsert.INSTANCE
         };
         BottomBarButtonComponentKt.BottomBarButtonComponent(null, CollectionsKt.listOf(var3), false, null, null, null, var1, 48, 61);
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
