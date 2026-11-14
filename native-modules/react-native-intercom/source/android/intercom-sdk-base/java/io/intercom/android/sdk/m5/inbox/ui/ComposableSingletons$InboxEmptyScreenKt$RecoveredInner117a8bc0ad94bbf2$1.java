package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.ActionType;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.models.EmptyState$Action;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxEmptyScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxEmptyScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/ComposableSingletons$InboxEmptyScreenKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,134:1\n1047#2,6:135\n*S KotlinDebug\n*F\n+ 1 InboxEmptyScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/ComposableSingletons$InboxEmptyScreenKt$lambda-5$1\n*L\n107#1:135,6\n*E\n"
)
final class ComposableSingletons$InboxEmptyScreenKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$InboxEmptyScreenKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$InboxEmptyScreenKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C106@3746L2,96@3267L496:InboxEmptyScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2067673359,
               var2,
               -1,
               "io.intercom.android.sdk.m5.inbox.ui.ComposableSingletons$InboxEmptyScreenKt.lambda-5.<anonymous> (InboxEmptyScreen.kt:96)"
            );
         }

         EmptyState var10000 = new EmptyState(
            "No messages",
            "Messages from the team, queries and support tickets will be visible here",
            new EmptyState$Action(ActionType.MESSAGE, "Send us a message", null, 4, null)
         );
         boolean var10001 = false;
         var1.startReplaceGroup(765248147);
         ComposerKt.sourceInformation(var1, "CC(remember):InboxEmptyScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            boolean var11 = false;
            EmptyState var10 = var10000;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$InboxEmptyScreenKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var13 = var12;
            var4.updateRememberedValue(var13);
            var10002 = var13;
         } else {
            var10002 = var7;
         }

         Function0 var3 = (Function0)var10002;
         var1.endReplaceGroup();
         InboxEmptyScreenKt.InboxEmptyScreen(var10000, var10001, var3, null, var1, 432, 8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
