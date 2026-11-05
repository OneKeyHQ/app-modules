package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomPreviewActivity$setupPreviewUI$1 implements Function2 {
   // $VF: synthetic field
   final IntercomPreviewActivity this$0;

   IntercomPreviewActivity$setupPreviewUI$1(IntercomPreviewActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C136@5685L16,141@5884L528,141@5870L542:IntercomPreviewActivity.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2127503266,
               var2,
               -1,
               "io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity.setupPreviewUI.<anonymous> (IntercomPreviewActivity.kt:136)"
            );
         }

         State var3 = SnapshotStateKt.collectAsState(
            IntercomPreviewActivity.access$getViewModel(this.this$0).getState$intercom_sdk_base_release(), null, var1, 0, 1
         );
         PreviewUiState var4 = (PreviewUiState)var3.getValue();
         if (var4.getFiles().isEmpty()) {
            IntercomPreviewActivity.access$finishWithResult(this.this$0, 0, CollectionsKt.emptyList());
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1919074696, true, new IntercomPreviewActivity$setupPreviewUI$1$1(this.this$0), var1, 54),
            var1,
            3072,
            7
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
