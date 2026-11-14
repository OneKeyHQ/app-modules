package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.helpcenter.sections.Author;
import io.intercom.android.sdk.helpcenter.sections.Avatar;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$CollectionSummaryComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$CollectionSummaryComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$CollectionSummaryComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C118@4549L1085:CollectionSummaryComponent.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               309452486,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$CollectionSummaryComponentKt.lambda-1.<anonymous> (CollectionSummaryComponent.kt:118)"
            );
         }

         List var3 = CollectionsKt.emptyList();
         Author[] var5 = new Author[]{
            new Author("", "Hannah", new Avatar("", "SK")), new Author("", "Bob", new Avatar("", "PS")), new Author("", "Anna", new Avatar("", "VR"))
         };
         List var4 = CollectionsKt.listOf(var5);
         CollectionSummaryComponentKt.CollectionSummaryComponent(
            new CollectionDetailsUiState$Content(
               "123456", "Recording videos", "How to set up screen and camera recording through our app or Chrome extension.", 5, var4, var3
            ),
            null,
            var1,
            0,
            2
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
