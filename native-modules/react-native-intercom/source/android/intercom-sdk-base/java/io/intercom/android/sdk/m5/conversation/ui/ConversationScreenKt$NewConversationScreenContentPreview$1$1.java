package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$SpecialNoticeRow;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState$None;
import io.intercom.android.sdk.m5.conversation.states.InputTypeState;
import io.intercom.android.sdk.m5.conversation.states.TeamPresenceState;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$NewConversationScreenContentPreview$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,1034:1\n1047#2,6:1035\n1047#2,6:1041\n1047#2,6:1047\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$NewConversationScreenContentPreview$1$1\n*L\n1027#1:1035,6\n1028#1:1041,6\n1029#1:1047,6\n*E\n"
)
final class ConversationScreenKt$NewConversationScreenContentPreview$1$1 implements Function2 {
   // $VF: synthetic field
   final ExpandedTeamPresenceState $expandedTeamPresenceState;

   ConversationScreenKt$NewConversationScreenContentPreview$1$1(ExpandedTeamPresenceState var1) {
      this.$expandedTeamPresenceState = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C1026@48165L2,1027@48210L8,1028@48266L8,989@45986L2303:ConversationScreen.kt#fnibcj");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1434887623,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.NewConversationScreenContentPreview.<anonymous>.<anonymous> (ConversationScreen.kt:989)"
            );
         }

         Object var10000 = null;
         ContentRow[] var4 = new ContentRow[]{
            new ContentRow$SpecialNoticeRow("Our response times are slower than usual. We’re working hard to get to your message"), null
         };
         ReplyOption[] var6 = new ReplyOption[]{new ReplyOption("Option 1", "1"), new ReplyOption("Option 2", "2"), new ReplyOption("Option 3", "3")};
         List var5 = CollectionsKt.listOf(var6);
         var4[1] = new ContentRow$QuickRepliesRow(var5, "1");
         List var3 = CollectionsKt.listOf(var4);
         BottomBarUiState var43 = new BottomBarUiState(
            new ComposerState$TextInput("", new StringRes(string.intercom_reply_to_conversation, null, 2, null), false, null, null, null, 60, null),
            new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null),
            InputTypeState.Companion.getDEFAULT(),
            null,
            8,
            null
         );
         StringProvider var10003 = new ActualString("Banana");
         StringProvider var10005 = new ActualString("Active 15m ago");
         Integer var10006 = drawable.intercom_clock;
         Avatar var10009 = Avatar.create("", "S");
         Intrinsics.checkNotNullExpressionValue(var10009, "create(...)");
         TopAppBarUiState var47 = new TopAppBarUiState(
            var10003,
            null,
            var10005,
            var10006,
            CollectionsKt.listOf(new AvatarWrapper(var10009, false, 2, null)),
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            16354,
            null
         );
         FloatingIndicatorState$None var51 = FloatingIndicatorState$None.INSTANCE;
         TeamPresenceState var7 = new TeamPresenceState(
            this.$expandedTeamPresenceState,
            "Our team is here to help you with any questions you have.",
            "Our response times are slower than usual. We’re working hard to get to your message"
         );
         ConversationUiState var10001 = new ConversationUiState$Content(var47, var3, var43, null, null, var51, var7, null, 152, null);
         Object var10002 = null;
         Object var111 = null;
         Object var10004 = null;
         Object var112 = null;
         Object var113 = null;
         Object var10007 = null;
         Object var10008 = null;
         Object var114 = null;
         Object var10010 = null;
         Object var10011 = null;
         Object var10012 = null;
         Object var10013 = null;
         Object var10014 = null;
         Object var10015 = null;
         var1.startReplaceGroup(-680520483);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var44 = var1;
         boolean var48 = false;
         int var52 = 0;
         Object var55 = var44.rememberedValue();
         int var8 = 0;
         Object var10016;
         if (var55 == Composer.Companion.getEmpty()) {
            Object var25 = null;
            Object var24 = null;
            Object var23 = null;
            Object var22 = null;
            Object var21 = null;
            Object var20 = null;
            Object var19 = null;
            Object var18 = null;
            Object var17 = null;
            Object var16 = null;
            Object var15 = null;
            Object var14 = null;
            Object var13 = null;
            Object var12 = null;
            ConversationUiState var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function0 var26 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt$NewConversationScreenContentPreview$1$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var111 = var13;
            var10004 = var14;
            var112 = var15;
            var113 = var16;
            var10007 = var17;
            var10008 = var18;
            var114 = var19;
            var10010 = var20;
            var10011 = var21;
            var10012 = var22;
            var10013 = var23;
            var10014 = var24;
            var10015 = var25;
            Object var60 = var26;
            var44.updateRememberedValue(var60);
            var10016 = (Function0)var60;
         } else {
            var10016 = (Function0)var55;
         }

         Function0 var40 = (kotlin.jvm.functions.Function0) var10016;
         var1.endReplaceGroup();
         var10016 = var40;
         Object var10017 = null;
         Object var10018 = null;
         Object var10019 = null;
         Object var10020 = null;
         Object var10021 = null;
         Object var10022 = null;
         Object var10023 = null;
         Object var10024 = null;
         Object var10025 = null;
         Object var10026 = null;
         Object var10027 = null;
         var1.startReplaceGroup(-680519037);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var45 = var1;
         boolean var49 = false;
         int $i$f$cachex = 0;
         var55 = var45.rememberedValue();
         var8 = 0;
         Object var10028;
         if (var55 == Composer.Companion.getEmpty()) {
            Object var37 = null;
            Object var36 = null;
            Object var35 = null;
            Object var34 = null;
            Object var33 = null;
            Object var32 = null;
            Object var31 = null;
            Object var30 = null;
            Object var29 = null;
            Object var28 = null;
            Object var27 = null;
            Function0 var97 = var40;
            Object var95 = var10015;
            Object var93 = var10014;
            Object var91 = var10013;
            Object var89 = var10012;
            Object var87 = var10011;
            Object var85 = var10010;
            Object var83 = var114;
            Object var81 = var10008;
            Object var79 = var10007;
            Object var77 = var113;
            Object var75 = var112;
            Object var73 = var10004;
            Object var71 = var111;
            Object var69 = var10002;
            ConversationUiState var67 = var10001;
            Object var65 = var10000;
            int var61 = 0;
            Function1 var38 = ((kotlin.jvm.functions.Function1) (recoveredArgument_396_0) -> ConversationScreenKt$NewConversationScreenContentPreview$1$1.invoke$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.usecase.ConversationScrolledState)recoveredArgument_396_0));
            var10000 = var65;
            var10001 = var67;
            var10002 = var69;
            var111 = var71;
            var10004 = var73;
            var112 = var75;
            var113 = var77;
            var10007 = var79;
            var10008 = var81;
            var114 = var83;
            var10010 = var85;
            var10011 = var87;
            var10012 = var89;
            var10013 = var91;
            var10014 = var93;
            var10015 = var95;
            var10016 = var97;
            var10017 = var27;
            var10018 = var28;
            var10019 = var29;
            var10020 = var30;
            var10021 = var31;
            var10022 = var32;
            var10023 = var33;
            var10024 = var34;
            var10025 = var35;
            var10026 = var36;
            var10027 = var37;
            Object var62 = var38;
            var45.updateRememberedValue(var62);
            var10028 = (Function1)var62;
         } else {
            var10028 = (Function1)var55;
         }

         Function1 var41 = (kotlin.jvm.functions.Function1) var10028;
         var1.endReplaceGroup();
         var10028 = var41;
         var1.startReplaceGroup(-680517245);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var46 = var1;
         boolean var50 = false;
         int $i$f$cachexx = 0;
         var55 = var46.rememberedValue();
         var8 = 0;
         Object var10029;
         if (var55 == Composer.Companion.getEmpty()) {
            Function1 var110 = var41;
            Object var109 = var10027;
            Object var108 = var10026;
            Object var107 = var10025;
            Object var106 = var10024;
            Object var105 = var10023;
            Object var104 = var10022;
            Object var103 = var10021;
            Object var102 = var10020;
            Object var101 = var10019;
            Object var100 = var10018;
            Object var99 = var10017;
            Function0 var98 = (kotlin.jvm.functions.Function0) var10016;
            Object var96 = var10015;
            Object var94 = var10014;
            Object var92 = var10013;
            Object var90 = var10012;
            Object var88 = var10011;
            Object var86 = var10010;
            Object var84 = var114;
            Object var82 = var10008;
            Object var80 = var10007;
            Object var78 = var113;
            Object var76 = var112;
            Object var74 = var10004;
            Object var72 = var111;
            Object var70 = var10002;
            ConversationUiState var68 = var10001;
            Object var66 = var10000;
            int var63 = 0;
            Function1 var39 = ((kotlin.jvm.functions.Function1) (recoveredArgument_397_0) -> ConversationScreenKt$NewConversationScreenContentPreview$1$1.invoke$lambda$5$lambda$4(((java.lang.Integer)recoveredArgument_397_0).intValue()));
            var10000 = var66;
            var10001 = var68;
            var10002 = var70;
            var111 = var72;
            var10004 = var74;
            var112 = var76;
            var113 = var78;
            var10007 = var80;
            var10008 = var82;
            var114 = var84;
            var10010 = var86;
            var10011 = var88;
            var10012 = var90;
            var10013 = var92;
            var10014 = var94;
            var10015 = var96;
            var10016 = var98;
            var10017 = var99;
            var10018 = var100;
            var10019 = var101;
            var10020 = var102;
            var10021 = var103;
            var10022 = var104;
            var10023 = var105;
            var10024 = var106;
            var10025 = var107;
            var10026 = var108;
            var10027 = var109;
            var10028 = var110;
            Object var64 = var39;
            var46.updateRememberedValue(var64);
            var10029 = var64;
         } else {
            var10029 = var55;
         }

         Function1 var42 = (Function1)var10029;
         var1.endReplaceGroup();
         ConversationScreenKt.ConversationScreenContent(
            (Modifier)var10000,
            var10001,
            (SnackbarHostState)var10002,
            (Function1)var111,
            (Function1)var10004,
            (Function2)var112,
            (Function1)var113,
            (Function1)var10007,
            (Function1)var10008,
            (Function0)var114,
            (Function1)var10010,
            (Function0)var10011,
            (Function0)var10012,
            (Function0)var10013,
            (Function0)var10014,
            (Function0)var10015,
            (kotlin.jvm.functions.Function0) var10016,
            (Function1)var10017,
            (Function1)var10018,
            (Function1)var10019,
            (Function0)var10020,
            (Function1)var10021,
            (Function0)var10022,
            (Function1)var10023,
            (Function1)var10024,
            (Function1)var10025,
            (Function1)var10026,
            (Function1)var10027,
            (kotlin.jvm.functions.Function1) var10028,
            var42,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var1,
            0,
            1572864,
            905969664,
            0,
            0,
            1342111741,
            131071
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(ConversationScrolledState var0) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(int var0) {
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
