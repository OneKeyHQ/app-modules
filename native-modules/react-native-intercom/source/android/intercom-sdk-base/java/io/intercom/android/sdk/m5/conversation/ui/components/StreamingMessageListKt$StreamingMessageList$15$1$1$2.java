package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.SpringSpec;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.FooterNoticeKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$AskedAboutRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BigTicketRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$ComposerSuggestionRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$DayDividerRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$EventRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$MergedConversationRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$NewMessagesRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$NoteCardRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$PostCardRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$SpecialNoticeRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TeamIntroRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TicketStatusRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.ui.components.row.AskedAboutRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.BubbleMessageRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.DayDividerKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.EventRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.FailedMessage;
import io.intercom.android.sdk.m5.conversation.ui.components.row.FinAnswerRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.NewMessagesRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.NoteCardRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.PostCardRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.SpecialNoticeKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.TeamIntroKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.TicketStatusRowKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicatorKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$15$1$1$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,578:1\n118#2:579\n118#2:580\n118#2:599\n118#2:600\n118#2:601\n118#2:602\n118#2:603\n118#2:604\n1047#3,6:581\n1047#3,6:587\n1047#3,6:593\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$StreamingMessageList$15$1$1$2\n*L\n264#1:579\n272#1:580\n339#1:599\n381#1:600\n388#1:601\n398#1:602\n408#1:603\n410#1:604\n296#1:581,6\n298#1:587,6\n329#1:593,6\n*E\n"
)
final class StreamingMessageListKt$StreamingMessageList$15$1$1$2 implements Function3 {
   // $VF: synthetic field
   final int $index;
   // $VF: synthetic field
   final int $streamingSlotIndex;
   // $VF: synthetic field
   final ContentRow $row;
   // $VF: synthetic field
   final List $contentRows;
   // $VF: synthetic field
   final Function0 $navigateToTicketDetail;
   // $VF: synthetic field
   final Function1 $onSuggestionClick;
   // $VF: synthetic field
   final Function1 $onReplyClicked;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Function1 $onRetryMessageClicked;
   // $VF: synthetic field
   final Function1 $onFailedMessageOptionsClicked;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;
   // $VF: synthetic field
   final Function1 $onRetryMediaClicked;
   // $VF: synthetic field
   final Function1 $onCreateTicket;
   // $VF: synthetic field
   final Function2 $onInlineSourcesClick;
   // $VF: synthetic field
   final boolean $showReportAiAnswerButton;
   // $VF: synthetic field
   final Function0 $onReportAiAnswer;
   // $VF: synthetic field
   final Function1 $navigateToAnotherConversation;
   // $VF: synthetic field
   final State $isStreamingActive$delegate;

   StreamingMessageListKt$StreamingMessageList$15$1$1$2(
      int var1,
      int var2,
      ContentRow var3,
      List var4,
      Function0 var5,
      Function1 var6,
      Function1 var7,
      Context var8,
      Function1 var9,
      Function1 var10,
      Function1 var11,
      Function1 var12,
      Function1 var13,
      Function2 var14,
      boolean var15,
      Function0 var16,
      Function1 var17,
      State var18
   ) {
      this.$index = var1;
      this.$streamingSlotIndex = var2;
      this.$row = var3;
      this.$contentRows = var4;
      this.$navigateToTicketDetail = var5;
      this.$onSuggestionClick = var6;
      this.$onReplyClicked = var7;
      this.$context = var8;
      this.$onRetryMessageClicked = var9;
      this.$onFailedMessageOptionsClicked = var10;
      this.$onSubmitAttribute = var11;
      this.$onRetryMediaClicked = var12;
      this.$onCreateTicket = var13;
      this.$onInlineSourcesClick = var14;
      this.$showReportAiAnswerButton = var15;
      this.$onReportAiAnswer = var16;
      this.$navigateToAnotherConversation = var17;
      this.$isStreamingActive$delegate = var18;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C:StreamingMessageList.kt#o19g89");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1908385488,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageList.<anonymous>.<anonymous>.<anonymous>.<anonymous> (StreamingMessageList.kt:215)"
            );
         }

         float var5 = StreamingMessageListKt.access$StreamingMessageList$lambda$20(this.$isStreamingActive$delegate) && this.$index == this.$streamingSlotIndex
            ? StreamingMessageListKt.getDefaultMessageGap()
            : StreamingMessageListKt.gapWithPrevious(this.$row, this.$index, this.$contentRows);
         Modifier var7 = PaddingKt.recovered$03b481d31d62885a(Modifier.Companion, 0.0F, var5, 0.0F, 0.0F, 13, null);
         SpringSpec var8 = StreamingMessageListKt.shouldFadeInItem(this.$row) ? AnimationSpecKt.spring$default(0.0F, 400.0F, null, 5, null) : null;
         SpringSpec var9 = StreamingMessageListKt.shouldFadeOutItem(this.$row) ? AnimationSpecKt.spring$default(0.0F, 400.0F, null, 5, null) : null;
         Modifier var6 = var1.animateItem(var7, var8, null, var9);
         ContentRow var38 = this.$row;
         if (var38 instanceof ContentRow$AskedAboutRow) {
            var2.startReplaceGroup(298380003);
            ComposerKt.sourceInformation(var2, "242@11587L153");
            AskedAboutRowKt.AskedAboutRow(SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null), ((ContentRow$AskedAboutRow)this.$row).getPart(), var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$BigTicketRow) {
            var2.startReplaceGroup(298634823);
            ComposerKt.sourceInformation(var2, "249@11840L277");
            BigTicketCardKt.BigTicketCard(
               ((ContentRow$BigTicketRow)this.$row).getTicketDetailContentState(), this.$navigateToTicketDetail, true, var6, var2, 384, 0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$ComposerSuggestionRow) {
            var2.startReplaceGroup(299019502);
            ComposerKt.sourceInformation(var2, "258@12226L334");
            List var52 = ((ContentRow$ComposerSuggestionRow)this.$row).getSuggestions();
            Modifier var103 = SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null);
            int var71 = 16;
            int var73 = 0;
            QuickRepliesKt.ComposerSuggestions(
               PaddingKt.recovered$f3b9f2e94c07c806(var103, Dp.recovered$95b3d103663bf060(var71), 0.0F, 2, null), var52, this.$onSuggestionClick, var2, 0, 0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$QuickRepliesRow) {
            var2.startReplaceGroup(299452913);
            ComposerKt.sourceInformation(var2, "268@12663L331");
            Modifier var102 = SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null);
            int var50 = 16;
            int var64 = 0;
            float var114 = Dp.recovered$95b3d103663bf060(var50);
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            QuickRepliesKt.ReplyOptions(
               PaddingKt.recovered$03b481d31d62885a(var102, var114, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 10, null),
               ((ContentRow$QuickRepliesRow)this.$row).getReplyOptions(),
               this.$onReplyClicked,
               var2,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$BubbleMessageRow) {
            var2.startReplaceGroup(299926779);
            ComposerKt.sourceInformation(var2, "278@13098L1697");
            Modifier var49 = SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null);
            Part var63 = ((ContentRow$BubbleMessageRow)this.$row).getPartWrapper().getPart();
            String var70 = TimeFormatterExtKt.formattedDateFromLong(
               ((ContentRow$BubbleMessageRow)this.$row).getPartWrapper().getPart().getCreatedAt(), this.$context
            );
            SenderMetadata var72 = ((ContentRow$BubbleMessageRow)this.$row).getSenderMetadata();
            List var74 = ((ContentRow$BubbleMessageRow)this.$row).getPartWrapper().getFailedAttributeIdentifiers();
            List var75 = ((ContentRow$BubbleMessageRow)this.$row).getPartWrapper().getLoadingAttributeIdentifiers();
            GroupingPosition var76 = ((ContentRow$BubbleMessageRow)this.$row).getGroupingPosition();
            boolean var77 = ((ContentRow$BubbleMessageRow)this.$row).getPartWrapper().isAdminOrAltParticipant();
            PendingMessage$FailedMediaUploadData var78 = ((ContentRow$BubbleMessageRow)this.$row).getFailedMediaUploadData();
            var2.startReplaceGroup(1810822535);
            ComposerKt.sourceInformation(var2, "293@14224L54");
            FailedMessage var101;
            if (((ContentRow$BubbleMessageRow)this.$row).isFailed()) {
               String var100 = StringResources_androidKt.stringResource(string.intercom_failed_delivery, var2, 0);
               Function0 var111;
               if (((ContentRow$BubbleMessageRow)this.$row).isRetryable()) {
                  var2.startReplaceGroup(301155309);
                  ComposerKt.sourceInformation(var2, "295@14403L47");
                  var2.startReplaceGroup(1810831269);
                  ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
                  Composer var86 = var2;
                  boolean var87 = var2.changed(this.$onRetryMessageClicked) | var2.changedInstance(this.$row);
                  Function1 var88 = this.$onRetryMessageClicked;
                  ContentRow var89 = this.$row;
                  int var90 = 0;
                  Object var91 = var86.rememberedValue();
                  int var92 = 0;
                  if (!var87 && var91 != Composer.Companion.getEmpty()) {
                     var111 = (Function0)var91;
                  } else {
                     String var96 = var100;
                     int var93 = 0;
                     final kotlin.jvm.functions.Function1 recoveredCapture_480_0 = var88;
                     final io.intercom.android.sdk.m5.conversation.states.ContentRow recoveredCapture_480_1 = var89;
                     var111 = ((kotlin.jvm.functions.Function0) () -> StreamingMessageListKt$StreamingMessageList$15$1$1$2.invoke$lambda$1$lambda$0(recoveredCapture_480_0, recoveredCapture_480_1));
                     var100 = var96;
                     Object var94 = var111;
                     var86.updateRememberedValue(var94);
                     var111 = (Function0)var94;
                  }

                  Function0 var84 = var111;
                  var2.endReplaceGroup();
                  Function0 var82 = var84;
                  var2.endReplaceGroup();
                  var111 = var82;
               } else {
                  var2.startReplaceGroup(301287493);
                  ComposerKt.sourceInformation(var2, "297@14536L55");
                  var2.startReplaceGroup(1810835533);
                  ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
                  Composer var85 = var2;
                  boolean var22 = var2.changed(this.$onFailedMessageOptionsClicked) | var2.changedInstance(this.$row);
                  Function1 var23 = this.$onFailedMessageOptionsClicked;
                  ContentRow var24 = this.$row;
                  int var25 = 0;
                  Object var26 = var85.rememberedValue();
                  int var27 = 0;
                  if (!var22 && var26 != Composer.Companion.getEmpty()) {
                     var111 = (Function0)var26;
                  } else {
                     String var95 = var100;
                     int var28 = 0;
                     final kotlin.jvm.functions.Function1 recoveredCapture_481_0 = var23;
                     final io.intercom.android.sdk.m5.conversation.states.ContentRow recoveredCapture_481_1 = var24;
                     var111 = ((kotlin.jvm.functions.Function0) () -> StreamingMessageListKt$StreamingMessageList$15$1$1$2.invoke$lambda$3$lambda$2(recoveredCapture_481_0, recoveredCapture_481_1));
                     var100 = var95;
                     Object var29 = var111;
                     var85.updateRememberedValue(var29);
                     var111 = (Function0)var29;
                  }

                  Function0 var83 = var111;
                  var2.endReplaceGroup();
                  Function0 var81 = var83;
                  var2.endReplaceGroup();
                  var111 = var81;
               }

               Function0 var36 = var111;
               String var37 = var100;
               var101 = new FailedMessage(var37, var36);
            } else {
               var101 = null;
            }

            FailedMessage var80 = var101;
            var2.endReplaceGroup();
            FailedMessage var79 = var80;
            BubbleMessageRowKt.BubbleMessageRow(
               var63,
               var76,
               var77,
               var49,
               var70,
               var72,
               this.$onSubmitAttribute,
               var74,
               var75,
               this.$onRetryMediaClicked,
               var78,
               var79,
               this.$onCreateTicket,
               var2,
               0,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$DayDividerRow) {
            var2.startReplaceGroup(301663709);
            ComposerKt.sourceInformation(var2, "307@14896L191");
            DayDividerKt.DayDivider(
               TimeFormatterExtKt.formattedDateForDayDivider(((ContentRow$DayDividerRow)this.$row).getTimestamp(), this.$context),
               SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null),
               var2,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$EventRow) {
            var2.startReplaceGroup(301949622);
            ComposerKt.sourceInformation(var2, "314@15183L230");
            EventRowKt.EventRow(
               SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null),
               ((ContentRow$EventRow)this.$row).getLabelSegments(),
               new AvatarWrapper(((ContentRow$EventRow)this.$row).getAvatar(), false, 2, null),
               var2,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$FinAnswerRow) {
            var2.startReplaceGroup(302292141);
            ComposerKt.sourceInformation(var2, "328@15913L146,322@15513L719");
            Part var48 = ((ContentRow$FinAnswerRow)this.$row).getPart();
            GroupingPosition var62 = ((ContentRow$FinAnswerRow)this.$row).getGroupingPosition();
            String var69 = TimeFormatterExtKt.formattedDateFromLong(((ContentRow$FinAnswerRow)this.$row).getPart().getCreatedAt(), this.$context);
            SenderMetadata var11 = ((ContentRow$FinAnswerRow)this.$row).getSenderMetadata();
            Part var99 = var48;
            GroupingPosition var108 = var62;
            Modifier var115 = var6;
            String var10003 = var69;
            SenderMetadata var10004 = var11;
            var2.startReplaceGroup(1810879688);
            ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
            Composer var13 = var2;
            boolean var14 = var2.changed(this.$onInlineSourcesClick) | var2.changedInstance(this.$row);
            Function2 var15 = this.$onInlineSourcesClick;
            ContentRow var16 = this.$row;
            int var17 = 0;
            Object var18 = var13.rememberedValue();
            int var19 = 0;
            Object var10005;
            if (!var14 && var18 != Composer.Companion.getEmpty()) {
               var10005 = var18;
            } else {
               SenderMetadata var34 = var11;
               String var33 = var69;
               Modifier var32 = var6;
               GroupingPosition var31 = var62;
               Part var30 = var48;
               int var20 = 0;
               final kotlin.jvm.functions.Function2 recoveredCapture_482_0 = var15;
               final io.intercom.android.sdk.m5.conversation.states.ContentRow recoveredCapture_482_1 = var16;
               Function1 var35 = ((kotlin.jvm.functions.Function1) (recoveredArgument_482_0) -> StreamingMessageListKt$StreamingMessageList$15$1$1$2.invoke$lambda$5$lambda$4(recoveredCapture_482_0, recoveredCapture_482_1, (java.util.List)recoveredArgument_482_0));
               var99 = var30;
               var108 = var31;
               var115 = var32;
               var10003 = var33;
               var10004 = var34;
               Object var21 = var35;
               var13.updateRememberedValue(var21);
               var10005 = var21;
            }

            Function1 var12 = (Function1)var10005;
            var2.endReplaceGroup();
            FinAnswerRowKt.FinAnswerRow(var99, var108, var115, var10003, var10004, var12, this.$showReportAiAnswerButton, this.$onReportAiAnswer, var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var38 instanceof ContentRow$FooterNoticeRow) {
            var2.startReplaceGroup(303096126);
            ComposerKt.sourceInformation(var2, "337@16335L350");
            int var46 = 24;
            int var60 = 0;
            float var107 = Dp.recovered$95b3d103663bf060(var46);
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            FooterNoticeKt.ExpandedFooterNotice(
               PaddingKt.recovered$904403cea4c49d95(var6, var107, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
               ((ContentRow$FooterNoticeRow)this.$row).getFooterNoticeState().getTitle(),
               ((ContentRow$FooterNoticeRow)this.$row).getFooterNoticeState().getSubtitle(),
               ((ContentRow$FooterNoticeRow)this.$row).getFooterNoticeState().getAvatars(),
               var2,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (!(var38 instanceof ContentRow$MergedConversationRow)) {
            if (var38 instanceof ContentRow$NewMessagesRow) {
               var2.startReplaceGroup(304100030);
               ComposerKt.sourceInformation(var2, "357@17355L94");
               NewMessagesRowKt.NewMessagesRow(var6, var2, 0, 0);
               var2.endReplaceGroup();
            } else if (var38 instanceof ContentRow$NoteCardRow) {
               var2.startReplaceGroup(304294617);
               ComposerKt.sourceInformation(var2, "363@17548L195");
               NoteCardRowKt.NoteCardRow(var6, ((ContentRow$NoteCardRow)this.$row).getPart(), ((ContentRow$NoteCardRow)this.$row).getCompanyName(), var2, 0, 0);
               var2.endReplaceGroup();
            } else if (var38 instanceof ContentRow$PostCardRow) {
               var2.startReplaceGroup(304586265);
               ComposerKt.sourceInformation(var2, "371@17842L195");
               PostCardRowKt.PostCardRow(var6, ((ContentRow$PostCardRow)this.$row).getPart(), ((ContentRow$PostCardRow)this.$row).getCompanyName(), var2, 0, 0);
               var2.endReplaceGroup();
            } else if (var38 instanceof ContentRow$TeamIntroRow) {
               var2.startReplaceGroup(304878223);
               ComposerKt.sourceInformation(var2, "379@18137L173");
               int var53 = 16;
               int var10 = 0;
               float var10001 = Dp.recovered$95b3d103663bf060(var53);
               int $this$dp$ivx = 16;
               var10 = 0;
               Modifier var41 = PaddingKt.recovered$03b481d31d62885a(var6, var10001, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 10, null);
               TeamIntroKt.TeamIntro(((ContentRow$TeamIntroRow)this.$row).getMessage(), var41, var2, 0, 0);
               var2.endReplaceGroup();
            } else if (var38 instanceof ContentRow$SpecialNoticeRow) {
               var2.startReplaceGroup(305153131);
               ComposerKt.sourceInformation(var2, "386@18414L177");
               int var55 = 16;
               int var67 = 0;
               float var104 = Dp.recovered$95b3d103663bf060(var55);
               int $this$dp$ivx = 16;
               var67 = 0;
               Modifier var42 = PaddingKt.recovered$03b481d31d62885a(var6, var104, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 10, null);
               SpecialNoticeKt.SpecialNotice(((ContentRow$SpecialNoticeRow)this.$row).getMessage(), var42, var2, 0, 0);
               var2.endReplaceGroup();
            } else if (var38 instanceof ContentRow$TicketStatusRow) {
               var2.startReplaceGroup(305440036);
               ComposerKt.sourceInformation(var2, "393@18694L472");
               String var10000 = ((ContentRow$TicketStatusRow)this.$row).getTicketEventStatus();
               String var105 = ((ContentRow$TicketStatusRow)this.$row).getTicketStatusText();
               String var10002 = TimeFormatterExtKt.formattedDateFromLong(((ContentRow$TicketStatusRow)this.$row).getCreatedAt(), this.$context);
               int var43 = 16;
               int var57 = 0;
               TicketStatusRowKt.TicketStatusRow(
                  var10000,
                  var105,
                  var10002,
                  PaddingKt.recovered$f3b9f2e94c07c806(var6, Dp.recovered$95b3d103663bf060(var43), 0.0F, 2, null),
                  ((ContentRow$TicketStatusRow)this.$row).getCustomStateLabel(),
                  ((ContentRow$TicketStatusRow)this.$row).getCustomStatePrefix(),
                  var2,
                  0,
                  0
               );
               var2.endReplaceGroup();
            } else {
               if (!(var38 instanceof ContentRow$TypingIndicatorRow)) {
                  var2.startReplaceGroup(1810746373);
                  var2.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var2.startReplaceGroup(306008948);
               ComposerKt.sourceInformation(var2, "404@19272L328");
               Modifier var97 = SizeKt.fillMaxWidth$default(var6, 0.0F, 1, null);
               int var44 = 16;
               int var58 = 0;
               var97 = PaddingKt.recovered$f3b9f2e94c07c806(var97, Dp.recovered$95b3d103663bf060(var44), 0.0F, 2, null);
               CurrentlyTypingState var106 = ((ContentRow$TypingIndicatorRow)this.$row).getCurrentlyTypingState();
               int $this$dp$ivx = 36;
               int $i$f$getDpx = 0;
               TypingIndicatorKt.recovered$891ffcbb9c232a50(var97, var106, Dp.recovered$95b3d103663bf060($this$dp$ivx), var2, 384, 0);
               var2.endReplaceGroup();
            }
         } else {
            var2.startReplaceGroup(303554833);
            ComposerKt.sourceInformation(var2, "347@16901L326");
            CharSequence var39 = ((ContentRow$MergedConversationRow)this.$row).getDescription();
            if (var39 != null && var39.length() != 0) {
               CharSequence var40 = ((ContentRow$MergedConversationRow)this.$row).getConversationId();
               if (var40 != null && var40.length() != 0) {
                  MergedConversationRowKt.MergedConversationRow(
                     var6,
                     ((ContentRow$MergedConversationRow)this.$row).getDescription(),
                     ((ContentRow$MergedConversationRow)this.$row).getConversationId(),
                     this.$navigateToAnotherConversation,
                     var2,
                     0,
                     0
                  );
               }
            }

            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function1 var0, ContentRow var1) {
      var0.invoke(((ContentRow$BubbleMessageRow)var1).getPartWrapper().getPart());
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(Function1 var0, ContentRow var1) {
      var0.invoke(((ContentRow$BubbleMessageRow)var1).getPartWrapper().getPart());
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(Function2 var0, ContentRow var1, List var2) {
      Intrinsics.checkNotNullParameter(var2, "selectedSources");
      if (var0 != null) {
         List var10002 = ((ContentRow$FinAnswerRow)var1).getPart().getSources();
         Intrinsics.checkNotNullExpressionValue(var10002, "getSources(...)");
         var0.invoke(var2, var10002);
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
