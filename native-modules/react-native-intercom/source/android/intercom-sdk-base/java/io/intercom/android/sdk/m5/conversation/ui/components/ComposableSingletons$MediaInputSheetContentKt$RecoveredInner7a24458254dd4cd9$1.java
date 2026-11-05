package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$MediaInputSheetContentKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$MediaInputSheetContentKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$MediaInputSheetContentKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C78@3398L51,77@3339L195:MediaInputSheetContent.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -702852745,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.lambda-2.<anonymous> (MediaInputSheetContent.kt:77)"
            );
         }

         MediaInputSheetContentKt.access$MediaInputSheetContentItem(
            StringResources_androidKt.stringResource(string.intercom_take_a_photo, var1, 0), drawable.intercom_ic_camera, var1, 0
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
