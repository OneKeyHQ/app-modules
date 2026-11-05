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
final class ComposableSingletons$EmptyStateKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$EmptyStateKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$EmptyStateKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C93@3043L444:EmptyState.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1715597885, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$EmptyStateKt.lambda-5.<anonymous> (EmptyState.kt:93)"
            );
         }

         EmptyStateKt.EmptyState(
            "No messages",
            null,
            "Messages from the team, queries and support tickets will be visible here",
            drawable.intercom_messages_icon,
            ComposableSingletons$EmptyStateKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
