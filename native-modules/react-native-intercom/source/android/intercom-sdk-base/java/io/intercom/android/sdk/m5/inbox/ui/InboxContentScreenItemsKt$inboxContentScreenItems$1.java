package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.m5.components.ConversationItemKt;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.tickets.list.reducers.TicketRowReducerKt;
import io.intercom.android.sdk.tickets.list.ui.TicketRowData;
import io.intercom.android.sdk.tickets.list.ui.TicketRowKt;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxContentScreenItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxContentScreenItems.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxContentScreenItemsKt$inboxContentScreenItems$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,95:1\n1047#2,6:96\n1047#2,6:104\n118#3:102\n118#3:103\n118#3:110\n*S KotlinDebug\n*F\n+ 1 InboxContentScreenItems.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxContentScreenItemsKt$inboxContentScreenItems$1\n*L\n41#1:96,6\n53#1:104,6\n43#1:102\n52#1:103\n56#1:110\n*E\n"
)
final class InboxContentScreenItemsKt$inboxContentScreenItems$1 implements Function4 {
   // $VF: synthetic field
   final LazyPagingItems $inboxConversations;
   // $VF: synthetic field
   final Function1 $onConversationClick;
   // $VF: synthetic field
   final Map $typingConversations;

   InboxContentScreenItemsKt$inboxContentScreenItems$1(LazyPagingItems var1, Function1 var2, Map var3) {
      this.$inboxConversations = var1;
      this.$onConversationClick = var2;
      this.$typingConversations = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$items");
      ComposerKt.sourceInformation(var3, "C*55@2727L64:InboxContentScreenItems.kt#po70xe");
      int var5 = var4;
      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if ((var5 & 145) == 144 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               328400605, var5, -1, "io.intercom.android.sdk.m5.inbox.ui.inboxContentScreenItems.<anonymous> (InboxContentScreenItems.kt:34)"
            );
         }

         Conversation var10000 = (Conversation)this.$inboxConversations.get(var2);
         if (var10000 != null) {
            Conversation var6 = var10000;
            Function1 var7 = this.$onConversationClick;
            Map var8 = this.$typingConversations;
            Conversation var9 = var6;
            int var10 = 0;
            if (var9.getTicket() != null) {
               var3.startReplaceGroup(-1068649010);
               ComposerKt.sourceInformation(var3, "40@1946L37,37@1820L405");
               Modifier var52 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               boolean var10001 = false;
               Object var10002 = null;
               Object var10003 = null;
               Object var10004 = null;
               var3.startReplaceGroup(796817636);
               ComposerKt.sourceInformation(var3, "CC(remember):InboxContentScreenItems.kt#9igjgp");
               Composer var11 = var3;
               boolean var12 = var3.changed(var7) | var3.changedInstance(var9);
               int var13 = 0;
               Object var14 = var11.rememberedValue();
               int var15 = 0;
               Object var10005;
               if (!var12 && var14 != Composer.Companion.getEmpty()) {
                  var10005 = var14;
               } else {
                  Object var16 = null;
                  Object var17 = null;
                  Object var18 = null;
                  boolean var19 = false;
                  Modifier var20 = var52;
                  int var21 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_971_0 = var7;
                  final io.intercom.android.sdk.models.Conversation recoveredCapture_971_1 = var9;
                  Function0 var22 = ((kotlin.jvm.functions.Function0) () -> InboxContentScreenItemsKt$inboxContentScreenItems$1.invoke$lambda$4$lambda$1$lambda$0(recoveredCapture_971_0, recoveredCapture_971_1));
                  var52 = var20;
                  var10001 = var19;
                  var10002 = var18;
                  var10003 = var17;
                  var10004 = var16;
                  Object var23 = var22;
                  var11.updateRememberedValue(var23);
                  var10005 = var23;
               }

               Function0 var24 = (Function0)var10005;
               var3.endReplaceGroup();
               Modifier var53 = ClickableKt.recovered$1c9660c41fd850dc(
                  var52, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var24, 15, null
               );
               TicketRowData var57 = TicketRowReducerKt.reduceTicketRowData(var9.getTicket());
               int var48 = 20;
               int var28 = 0;
               float var59 = Dp.recovered$95b3d103663bf060(var48);
               int $this$dp$ivx = 16;
               int $i$f$getDpx = 0;
               TicketRowKt.TicketRow(
                  var53, var57, PaddingKt.recovered$4ebb4826aa57abc6(var59, Dp.recovered$95b3d103663bf060($this$dp$ivx)), !var9.isRead(), var3, 384, 0
               );
               var3.endReplaceGroup();
            } else {
               var3.startReplaceGroup(-1068147058);
               ComposerKt.sourceInformation(var3, "52@2644L37,47@2329L371");
               Modifier var50 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               CurrentlyTypingState var30 = (CurrentlyTypingState)var8.get(var9.getId());
               int var33 = 20;
               int var36 = 0;
               float var54 = Dp.recovered$95b3d103663bf060(var33);
               var33 = 16;
               var36 = 0;
               PaddingValues var32 = PaddingKt.recovered$4ebb4826aa57abc6(var54, Dp.recovered$95b3d103663bf060(var33));
               var10000 = var9;
               Modifier var58 = var50;
               CurrentlyTypingState var60 = var30;
               PaddingValues var61 = var32;
               boolean var62 = false;
               var3.startReplaceGroup(796839972);
               ComposerKt.sourceInformation(var3, "CC(remember):InboxContentScreenItems.kt#9igjgp");
               Composer var38 = var3;
               boolean var39 = var3.changed(var7) | var3.changedInstance(var9);
               int var45 = 0;
               Object var47 = var38.rememberedValue();
               int var25 = 0;
               Object var63;
               if (!var39 && var47 != Composer.Companion.getEmpty()) {
                  var63 = var47;
               } else {
                  boolean var40 = false;
                  PaddingValues var41 = var32;
                  CurrentlyTypingState var42 = var30;
                  Modifier var43 = var50;
                  Conversation var44 = var9;
                  int var26 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_972_0 = var7;
                  final io.intercom.android.sdk.models.Conversation recoveredCapture_972_1 = var9;
                  Function0 var46 = ((kotlin.jvm.functions.Function0) () -> InboxContentScreenItemsKt$inboxContentScreenItems$1.invoke$lambda$4$lambda$3$lambda$2(recoveredCapture_972_0, recoveredCapture_972_1));
                  var10000 = var44;
                  var58 = var43;
                  var60 = var42;
                  var61 = var41;
                  var62 = var40;
                  Object var27 = var46;
                  var38.updateRememberedValue(var27);
                  var63 = var27;
               }

               Function0 var35 = (Function0)var63;
               var3.endReplaceGroup();
               ConversationItemKt.ConversationItem(var10000, var58, var60, var61, var62, var35, var3, 3120, 16);
               var3.endReplaceGroup();
            }

            Modifier var56 = Modifier.Companion;
            int var51 = 20;
            int var31 = 0;
            IntercomDividerKt.IntercomDivider(PaddingKt.recovered$f3b9f2e94c07c806(var56, Dp.recovered$95b3d103663bf060(var51), 0.0F, 2, null), var3, 6, 0);
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
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
