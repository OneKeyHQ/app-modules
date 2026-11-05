package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.tickets.list.reducers.TicketRowReducerKt;
import io.intercom.android.sdk.tickets.list.ui.TicketRowData;
import io.intercom.android.sdk.tickets.list.ui.TicketRowKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationHistoryCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ConversationHistoryCardKt$ConversationHistoryCard$2\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,112:1\n1869#2:113\n1870#2:128\n1047#3,6:114\n1047#3,6:122\n118#4:120\n118#4:121\n*S KotlinDebug\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ConversationHistoryCardKt$ConversationHistoryCard$2\n*L\n30#1:113\n30#1:128\n36#1:114,6\n46#1:122,6\n38#1:120\n47#1:121\n*E\n"
)
final class ConversationHistoryCardKt$ConversationHistoryCard$2 implements Function2 {
   // $VF: synthetic field
   final List $conversations;
   // $VF: synthetic field
   final Function1 $onConversationClick;

   ConversationHistoryCardKt$ConversationHistoryCard$2(List var1, Function1 var2) {
      this.$conversations = var1;
      this.$onConversationClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C:ConversationHistoryCard.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1614953259, var2, -1, "io.intercom.android.sdk.m5.components.ConversationHistoryCard.<anonymous> (ConversationHistoryCard.kt:29)"
            );
         }

         Iterable var3 = this.$conversations;
         Function1 var4 = this.$onConversationClick;
         int var5 = 0;

         for (Object var7 : var3) {
            Conversation var8 = (Conversation)var7;
            int var9 = 0;
            if (var8.getTicket() != null) {
               var1.startReplaceGroup(1734859093);
               ComposerKt.sourceInformation(var1, "35@1394L37,32@1268L404");
               Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               boolean var10001 = false;
               Object var10002 = null;
               Object var10003 = null;
               Object var10004 = null;
               var1.startReplaceGroup(610158718);
               ComposerKt.sourceInformation(var1, "CC(remember):ConversationHistoryCard.kt#9igjgp");
               Composer var10 = var1;
               boolean var11 = var1.changed(var4) | var1.changedInstance(var8);
               int var12 = 0;
               Object var13 = var10.rememberedValue();
               int var14 = 0;
               Object var10005;
               if (!var11 && var13 != Composer.Companion.getEmpty()) {
                  var10005 = var13;
               } else {
                  Object var15 = null;
                  Object var16 = null;
                  Object var17 = null;
                  boolean var18 = false;
                  Modifier var19 = var10000;
                  int var20 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_185_0 = var4;
                  final io.intercom.android.sdk.models.Conversation recoveredCapture_185_1 = var8;
                  Function0 var21 = ((kotlin.jvm.functions.Function0) () -> ConversationHistoryCardKt$ConversationHistoryCard$2.invoke$lambda$4$lambda$1$lambda$0(recoveredCapture_185_0, recoveredCapture_185_1));
                  var10000 = var19;
                  var10001 = var18;
                  var10002 = var17;
                  var10003 = var16;
                  var10004 = var15;
                  Object var22 = var21;
                  var10.updateRememberedValue(var22);
                  var10005 = var22;
               }

               Function0 var23 = (Function0)var10005;
               var1.endReplaceGroup();
               var10000 = ClickableKt.recovered$1c9660c41fd850dc(
                  var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var23, 15, null
               );
               TicketRowData var52 = TicketRowReducerKt.reduceTicketRowData(var8.getTicket());
               int var26 = 8;
               var11 = false;
               float var48 = Dp.recovered$95b3d103663bf060(var26);
               int recoveredHorizontalPadding = 20;
               var12 = 0;
               TicketRowKt.TicketRow(
                  var10000, var52, PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060(recoveredHorizontalPadding), var48), !var8.isRead(), var1, 0, 0
               );
               var1.endReplaceGroup();
            } else {
               var1.startReplaceGroup(1735357821);
               ComposerKt.sourceInformation(var1, "45@1929L37,42@1776L298");
               Modifier var49 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int var33 = 8;
               int var35 = 0;
               float var30 = Dp.recovered$95b3d103663bf060(var33);
               var35 = 20;
               int $i$f$getDpx = 0;
               PaddingValues var27 = PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060(var35), var30);
               Conversation var51 = var8;
               Modifier var53 = var49;
               Object var54 = null;
               PaddingValues var55 = var27;
               boolean var56 = false;
               var1.startReplaceGroup(610175838);
               ComposerKt.sourceInformation(var1, "CC(remember):ConversationHistoryCard.kt#9igjgp");
               Composer var34 = var1;
               boolean var37 = var1.changed(var4) | var1.changedInstance(var8);
               $i$f$getDpx = 0;
               Object var45 = var34.rememberedValue();
               int var47 = 0;
               Object var57;
               if (!var37 && var45 != Composer.Companion.getEmpty()) {
                  var57 = var45;
               } else {
                  boolean var40 = false;
                  PaddingValues var41 = var27;
                  Object var42 = null;
                  Modifier var43 = var49;
                  Conversation var44 = var8;
                  int var24 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_186_0 = var4;
                  final io.intercom.android.sdk.models.Conversation recoveredCapture_186_1 = var8;
                  Function0 var46 = ((kotlin.jvm.functions.Function0) () -> ConversationHistoryCardKt$ConversationHistoryCard$2.invoke$lambda$4$lambda$3$lambda$2(recoveredCapture_186_0, recoveredCapture_186_1));
                  var51 = var44;
                  var53 = var43;
                  var54 = var42;
                  var55 = var41;
                  var56 = var40;
                  Object var25 = var46;
                  var34.updateRememberedValue(var25);
                  var57 = var25;
               }

               Function0 var31 = (Function0)var57;
               var1.endReplaceGroup();
               ConversationItemKt.ConversationItem(var51, var53, (CurrentlyTypingState)var54, var55, var56, var31, var1, 48, 20);
               var1.endReplaceGroup();
            }
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$1$lambda$0(Function1 var0, Conversation var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$2(Function1 var0, Conversation var1) {
      var0.invoke(var1);
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
