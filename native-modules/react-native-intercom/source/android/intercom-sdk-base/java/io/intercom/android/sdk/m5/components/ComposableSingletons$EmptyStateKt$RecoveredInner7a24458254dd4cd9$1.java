package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$EmptyStateKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$EmptyStateKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$EmptyStateKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C74@2447L444:EmptyState.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               393587139, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$EmptyStateKt.lambda-2.<anonymous> (EmptyState.kt:74)"
            );
         }

         EmptyStateKt.EmptyState(
            "No messages",
            null,
            "Messages from the team, queries and support tickets will be visible here",
            drawable.intercom_messages_icon,
            ComposableSingletons$EmptyStateKt.INSTANCE.recovered$eac478aa65045e5d(),
            var1,
            24966,
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
