package io.intercom.android.sdk.m5.navigation;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.tickets.TicketDetailContentKt;
import io.intercom.android.sdk.tickets.TicketDetailErrorScreenKt;
import io.intercom.android.sdk.tickets.TicketDetailState;
import io.intercom.android.sdk.tickets.TicketDetailState$Error;
import io.intercom.android.sdk.tickets.TicketDetailState$Initial;
import io.intercom.android.sdk.tickets.TicketDetailState$Loading;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.tickets.TicketDetailsLoadingScreenKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketDetailDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$TicketDetailScreen$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,263:1\n1047#2,6:264\n*S KotlinDebug\n*F\n+ 1 TicketDetailDestination.kt\nio/intercom/android/sdk/m5/navigation/TicketDetailDestinationKt$TicketDetailScreen$4\n*L\n251#1:264,6\n*E\n"
)
final class TicketDetailDestinationKt$TicketDetailScreen$4 implements Function3 {
   // $VF: synthetic field
   final TicketDetailState $ticketDetailState;
   // $VF: synthetic field
   final Function2 $onConversationCTAClicked;
   // $VF: synthetic field
   final boolean $isLaunchedProgrammatically;
   // $VF: synthetic field
   final boolean $showSubmissionCard;

   TicketDetailDestinationKt$TicketDetailScreen$4(TicketDetailState var1, Function2 var2, boolean var3, boolean var4) {
      this.$ticketDetailState = var1;
      this.$onConversationCTAClicked = var2;
      this.$isLaunchedProgrammatically = var3;
      this.$showSubmissionCard = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "contentPadding");
      ComposerKt.sourceInformation(var2, "C:TicketDetailDestination.kt#m1zs2i");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1401868325, var4, -1, "io.intercom.android.sdk.m5.navigation.TicketDetailScreen.<anonymous> (TicketDetailDestination.kt:234)"
            );
         }

         TicketDetailState var5 = this.$ticketDetailState;
         if (Intrinsics.areEqual(var5, TicketDetailState$Initial.INSTANCE) || Intrinsics.areEqual(var5, TicketDetailState$Loading.INSTANCE)) {
            var2.startReplaceGroup(1534755126);
            ComposerKt.sourceInformation(var2, "237@9779L102");
            TicketDetailsLoadingScreenKt.TicketDetailsLoadingScreen(PaddingKt.padding(Modifier.Companion, var1), var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var5 instanceof TicketDetailState$Error) {
            var2.startReplaceGroup(332915065);
            ComposerKt.sourceInformation(var2, "241@9925L153");
            Modifier var20 = PaddingKt.padding(Modifier.Companion, var1);
            TicketDetailErrorScreenKt.TicketDetailErrorScreen(((TicketDetailState$Error)this.$ticketDetailState).getErrorState(), var20, var2, 0, 0);
            var2.endReplaceGroup();
         } else {
            if (!(var5 instanceof TicketDetailState$TicketDetailContentState)) {
               var2.startReplaceGroup(1534752204);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(333140962);
            ComposerKt.sourceInformation(var2, "250@10358L217,247@10159L496");
            Modifier var10000 = PaddingKt.padding(Modifier.Companion, var1);
            TicketDetailState$TicketDetailContentState var10001 = (TicketDetailState$TicketDetailContentState)this.$ticketDetailState;
            var2.startReplaceGroup(1534773769);
            ComposerKt.sourceInformation(var2, "CC(remember):TicketDetailDestination.kt#9igjgp");
            Composer var7 = var2;
            boolean var8 = var2.changed(this.$onConversationCTAClicked)
               | var2.changed(this.$ticketDetailState)
               | var2.changed(this.$isLaunchedProgrammatically);
            Function2 var9 = this.$onConversationCTAClicked;
            TicketDetailState var10 = this.$ticketDetailState;
            boolean var11 = this.$isLaunchedProgrammatically;
            int var12 = 0;
            Object var13 = var7.rememberedValue();
            int var14 = 0;
            Object var10002;
            if (!var8 && var13 != Composer.Companion.getEmpty()) {
               var10002 = var13;
            } else {
               TicketDetailState$TicketDetailContentState var18 = var10001;
               Modifier var17 = var10000;
               int var15 = 0;
               final kotlin.jvm.functions.Function2 recoveredCapture_1088_0 = var9;
               final io.intercom.android.sdk.tickets.TicketDetailState recoveredCapture_1088_1 = var10;
               final boolean recoveredCapture_1088_2 = var11;
               Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1088_0) -> TicketDetailDestinationKt$TicketDetailScreen$4.invoke$lambda$1$lambda$0(recoveredCapture_1088_0, recoveredCapture_1088_1, recoveredCapture_1088_2, (java.lang.String)recoveredArgument_1088_0));
               var10000 = var17;
               var10001 = var18;
               Object var16 = var19;
               var7.updateRememberedValue(var16);
               var10002 = var16;
            }

            Function1 var6 = (Function1)var10002;
            var2.endReplaceGroup();
            TicketDetailContentKt.TicketDetailContent(var10000, var10001, var6, this.$showSubmissionCard, var2, 0, 0);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function2 var0, TicketDetailState var1, boolean var2, String var3) {
      var0.invoke(((TicketDetailState$TicketDetailContentState)var1).getConversationId(), var2);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
