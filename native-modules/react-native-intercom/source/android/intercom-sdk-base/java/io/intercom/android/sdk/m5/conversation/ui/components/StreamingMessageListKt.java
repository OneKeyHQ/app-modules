package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.lazy.LazyListStateKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.FloatState;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.PrimitiveSnapshotStateKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.components.PoweredByBadgeKt;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
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
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerKt;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardState;
import io.intercom.android.sdk.m5.conversation.utils.KeyboardStateKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.models.Part$MessageState;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.common.IntercomArrangement;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.flow.StateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u009c\u0001\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\u001aã\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00030\u00112\u0014\b\u0002\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00030\u00112\u0014\b\u0002\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00030\u00112\u0014\b\u0002\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00030\u00112\u0014\b\u0002\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00030\u00112\u0014\b\u0002\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u00030\u00112\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u001d2\u0014\b\u0002\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u00030\u00112\b\b\u0002\u0010 \u001a\u00020\u000b2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00030\u00112\b\b\u0002\u0010\"\u001a\u00020\u000b2(\b\u0002\u0010#\u001a\"\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020&0\u0005\u0012\u0004\u0012\u00020\u0003\u0018\u00010$2\b\b\u0002\u0010'\u001a\u00020\u000b2\u000e\b\u0002\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00030\u001dH\u0001¢\u0006\u0002\u0010)\u001a?\u0010*\u001a\u00020\u0003*\u00020+2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u000b0\u001d2\u0006\u0010.\u001a\u00020/H\u0002¢\u0006\u0004\b0\u00101\u001a+\u00102\u001a\u00020/2\u0006\u00103\u001a\u00020\u00062\u0006\u00104\u001a\u0002052\f\u00106\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0000¢\u0006\u0002\u00107\u001a\f\u0010A\u001a\u00020\u000b*\u00020\u0006H\u0000\u001a\f\u0010B\u001a\u00020\u000b*\u00020\u0006H\u0000\u001a\r\u0010C\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010D\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u0016\u00108\u001a\u00020/X\u0080\u0004¢\u0006\n\n\u0002\u0010;\u001a\u0004\b9\u0010:\"\u0016\u0010<\u001a\u00020/X\u0080\u0004¢\u0006\n\n\u0002\u0010;\u001a\u0004\b=\u0010:\"\u0016\u0010>\u001a\u00020/X\u0080\u0004¢\u0006\n\n\u0002\u0010;\u001a\u0004\b?\u0010:\"\u0010\u0010@\u001a\u00020/X\u0082\u0004¢\u0006\u0004\n\u0002\u0010;¨\u0006E²\u0006\n\u0010F\u001a\u00020\u000bX\u008a\u0084\u0002²\u0006\n\u0010G\u001a\u00020HX\u008a\u0084\u0002²\u0006\n\u0010I\u001a\u00020JX\u008a\u008e\u0002²\u0006\n\u0010K\u001a\u00020\u000bX\u008a\u008e\u0002²\u0006\n\u0010L\u001a\u000205X\u008a\u008e\u0002²\u0006\n\u0010M\u001a\u00020\u000bX\u008a\u008e\u0002",
   d2 = {
         "StreamingItemKey",
         "",
         "StreamingMessageList",
         "",
         "contentRows",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "streamingStateFlow",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/streaming/StreamingMessageState;",
         "isStreamingActiveFlow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "lazyListState",
         "Landroidx/compose/foundation/lazy/LazyListState;",
         "onSuggestionClick",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "onReplyClicked",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "onRetryMessageClicked",
         "Lio/intercom/android/sdk/models/Part;",
         "onFailedMessageOptionsClicked",
         "onRetryMediaClicked",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "onSubmitAttribute",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "navigateToTicketDetail",
         "Lkotlin/Function0;",
         "onCreateTicket",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "addComposerPadding",
         "navigateToAnotherConversation",
         "isIntercomBadgeVisible",
         "onInlineSourcesClick",
         "Lkotlin/Function2;",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "Lio/intercom/android/sdk/models/Source;",
         "showReportAiAnswerButton",
         "onReportAiAnswer",
         "(Ljava/util/List;Lkotlinx/coroutines/flow/StateFlow;Lkotlinx/coroutines/flow/StateFlow;Landroidx/compose/ui/Modifier;Landroidx/compose/foundation/lazy/LazyListState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ZLkotlin/jvm/functions/Function1;ZLkotlin/jvm/functions/Function2;ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;III)V",
         "streamingAnswerItem",
         "Landroidx/compose/foundation/lazy/LazyListScope;",
         "stateFlow",
         "isAtBottom",
         "topGap",
         "Landroidx/compose/ui/unit/Dp;",
         "streamingAnswerItem-M2VBTUQ",
         "(Landroidx/compose/foundation/lazy/LazyListScope;Lkotlinx/coroutines/flow/StateFlow;Landroidx/compose/foundation/lazy/LazyListState;Lkotlin/jvm/functions/Function0;F)V",
         "gapWithPrevious",
         "contentRow",
         "contentRowIndex",
         "",
         "allContentRows",
         "(Lio/intercom/android/sdk/m5/conversation/states/ContentRow;ILjava/util/List;)F",
         "GroupedMessageGap",
         "getGroupedMessageGap",
         "()F",
         "F",
         "DefaultMessageGap",
         "getDefaultMessageGap",
         "BigMessageGap",
         "getBigMessageGap",
         "LargeMessageGap",
         "shouldFadeInItem",
         "shouldFadeOutItem",
         "StreamingMessageListPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "isStreamingActive",
         "keyboardAsState",
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;",
         "keyboardScrollOffset",
         "",
         "isListAtTheBottom",
         "contentHeight",
         "following"
   }
)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 9 SnapshotFloatState.kt\nandroidx/compose/runtime/PrimitiveSnapshotStateKt__SnapshotFloatStateKt\n*L\n1#1,578:1\n1047#2,6:579\n1047#2,6:585\n1047#2,6:591\n1047#2,6:597\n1047#2,6:603\n1047#2,6:609\n1047#2,6:615\n1047#2,6:621\n1047#2,6:627\n1047#2,6:633\n1047#2,3:640\n1050#2,3:651\n1047#2,6:654\n1047#2,6:660\n1047#2,6:667\n1047#2,6:673\n1047#2,6:679\n1047#2,6:685\n1047#2,6:691\n1047#2,6:699\n75#3:639\n75#3:666\n360#4,7:643\n1878#4,3:713\n1#5:650\n118#6:697\n118#6:716\n118#6:717\n118#6:718\n118#6:719\n49#7:698\n85#8:705\n85#8:706\n85#8:710\n117#8,2:711\n80#9:707\n113#9,2:708\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt\n*L\n101#1:579,6\n102#1:585,6\n103#1:591,6\n104#1:597,6\n105#1:603,6\n106#1:609,6\n107#1:615,6\n108#1:621,6\n110#1:627,6\n114#1:633,6\n123#1:640,3\n123#1:651,3\n128#1:654,6\n137#1:660,6\n144#1:667,6\n149#1:673,6\n157#1:679,6\n166#1:685,6\n174#1:691,6\n203#1:699,6\n116#1:639\n139#1:666\n124#1:643,7\n204#1:713,3\n187#1:697\n530#1:716\n531#1:717\n532#1:718\n533#1:719\n187#1:698\n119#1:705\n136#1:706\n144#1:710\n144#1:711,2\n137#1:707\n137#1:708,2\n*E\n"
)
public final class StreamingMessageListKt {
   @NotNull
   private static final String StreamingItemKey = "streaming_message";
   private static final float GroupedMessageGap;
   private static final float DefaultMessageGap;
   private static final float BigMessageGap;
   private static final float LargeMessageGap;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void StreamingMessageList(
      @NotNull List var0,
      @NotNull StateFlow var1,
      @NotNull StateFlow var2,
      @Nullable Modifier var3,
      @Nullable LazyListState var4,
      @Nullable Function1 var5,
      @Nullable Function1 var6,
      @Nullable Function1 var7,
      @Nullable Function1 var8,
      @Nullable Function1 var9,
      @Nullable Function1 var10,
      @Nullable Function0 var11,
      @Nullable Function1 var12,
      boolean var13,
      @Nullable Function1 var14,
      boolean var15,
      @Nullable Function2 var16,
      boolean var17,
      @Nullable Function0 var18,
      @Nullable Composer var19,
      int var20,
      int var21,
      int var22
   ) {
      Intrinsics.checkNotNullParameter(var0, "contentRows");
      Intrinsics.checkNotNullParameter(var1, "streamingStateFlow");
      Intrinsics.checkNotNullParameter(var2, "isStreamingActiveFlow");
      Composer var55 = var19.startRestartGroup(2044549819);
      ComposerKt.sourceInformation(
         var55,
         "C(StreamingMessageList)P(1,18,3,5,4,16,11,14,9,13,15,7,8!1,6!2,17)99@5578L23,100@5654L2,101@5702L2,102@5750L2,103@5806L2,104@5884L2,105@5937L2,106@5982L2,107@6029L2,109@6128L2,113@6338L2,115@6377L7,118@6537L16,122@6735L196,127@7055L256,135@7398L17,136@7448L36,143@7677L34,148@7811L293,145@7717L387,156@8154L213,156@8110L257,165@8403L218,165@8373L248,173@8655L312,173@8627L340,202@9913L10357,182@8973L11297:StreamingMessageList.kt#o19g89"
      );
      int var23 = var20;
      int var24 = var21;
      if ((var22 & 1) != 0) {
         var23 |= 6;
      } else if ((var20 & 6) == 0) {
         var23 |= var55.changedInstance(var0) ? 4 : 2;
      }

      if ((var22 & 2) != 0) {
         var23 |= 48;
      } else if ((var20 & 48) == 0) {
         var23 |= var55.changedInstance(var1) ? 32 : 16;
      }

      if ((var22 & 4) != 0) {
         var23 |= 384;
      } else if ((var20 & 384) == 0) {
         var23 |= var55.changedInstance(var2) ? 256 : 128;
      }

      if ((var22 & 8) != 0) {
         var23 |= 3072;
      } else if ((var20 & 3072) == 0) {
         var23 |= var55.changed(var3) ? 2048 : 1024;
      }

      if ((var20 & 24576) == 0) {
         var23 |= (var22 & 16) == 0 && var55.changed(var4) ? 16384 : 8192;
      }

      if ((var22 & 32) != 0) {
         var23 |= 196608;
      } else if ((var20 & 196608) == 0) {
         var23 |= var55.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var22 & 64) != 0) {
         var23 |= 1572864;
      } else if ((var20 & 1572864) == 0) {
         var23 |= var55.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var22 & 128) != 0) {
         var23 |= 12582912;
      } else if ((var20 & 12582912) == 0) {
         var23 |= var55.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var22 & 256) != 0) {
         var23 |= 100663296;
      } else if ((var20 & 100663296) == 0) {
         var23 |= var55.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var22 & 512) != 0) {
         var23 |= 805306368;
      } else if ((var20 & 805306368) == 0) {
         var23 |= var55.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var22 & 1024) != 0) {
         var24 |= 6;
      } else if ((var21 & 6) == 0) {
         var24 |= var55.changedInstance(var10) ? 4 : 2;
      }

      if ((var22 & 2048) != 0) {
         var24 |= 48;
      } else if ((var21 & 48) == 0) {
         var24 |= var55.changedInstance(var11) ? 32 : 16;
      }

      if ((var22 & 4096) != 0) {
         var24 |= 384;
      } else if ((var21 & 384) == 0) {
         var24 |= var55.changedInstance(var12) ? 256 : 128;
      }

      if ((var22 & 8192) != 0) {
         var24 |= 3072;
      } else if ((var21 & 3072) == 0) {
         var24 |= var55.changed(var13) ? 2048 : 1024;
      }

      if ((var22 & 16384) != 0) {
         var24 |= 24576;
      } else if ((var21 & 24576) == 0) {
         var24 |= var55.changedInstance(var14) ? 16384 : 8192;
      }

      if ((var22 & 32768) != 0) {
         var24 |= 196608;
      } else if ((var21 & 196608) == 0) {
         var24 |= var55.changed(var15) ? 131072 : 65536;
      }

      if ((var22 & 65536) != 0) {
         var24 |= 1572864;
      } else if ((var21 & 1572864) == 0) {
         var24 |= var55.changedInstance(var16) ? 1048576 : 524288;
      }

      if ((var22 & 131072) != 0) {
         var24 |= 12582912;
      } else if ((var21 & 12582912) == 0) {
         var24 |= var55.changed(var17) ? 8388608 : 4194304;
      }

      if ((var22 & 262144) != 0) {
         var24 |= 100663296;
      } else if ((var21 & 100663296) == 0) {
         var24 |= var55.changedInstance(var18) ? 67108864 : 33554432;
      }

      if ((var23 & 306783379) == 306783378 && (var24 & 38347923) == 38347922 && var55.getSkipping()) {
         var55.skipToGroupEnd();
      } else {
         var55.startDefaults();
         if ((var20 & 1) != 0 && !var55.getDefaultsInvalid()) {
            var55.skipToGroupEnd();
            if ((var22 & 16) != 0) {
               var23 &= -57345;
            }
         } else {
            if ((var22 & 8) != 0) {
               var3 = Modifier.Companion;
            }

            if ((var22 & 16) != 0) {
               var4 = LazyListStateKt.rememberLazyListState(0, 0, var55, 0, 3);
               var23 &= -57345;
            }

            if ((var22 & 32) != 0) {
               var55.startReplaceGroup(1721060572);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var26 = var55;
               boolean var27 = false;
               int var28 = 0;
               Object var29 = var26.rememberedValue();
               int var30 = 0;
               Object var10000;
               if (var29 == Composer.Companion.getEmpty()) {
                  int var31 = 0;
                  Object var125 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_471_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.ReplySuggestion) ((io.intercom.android.sdk.m5.conversation.states.ReplySuggestion)recoveredExpressionArgument_471_0)));
                  var26.updateRememberedValue(var125);
                  var10000 = var125;
               } else {
                  var10000 = var29;
               }

               Function1 var25 = (Function1)var10000;
               var55.endReplaceGroup();
               var5 = var25;
            }

            if ((var22 & 64) != 0) {
               var55.startReplaceGroup(1721062108);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var66 = var55;
               boolean var77 = false;
               int var88 = 0;
               Object var100 = var66.rememberedValue();
               int var113 = 0;
               Object var235;
               if (var100 == Composer.Companion.getEmpty()) {
                  int var126 = 0;
                  Object var127 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_472_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$3$lambda$2((io.intercom.android.sdk.models.ReplyOption) ((io.intercom.android.sdk.models.ReplyOption)recoveredExpressionArgument_472_0)));
                  var66.updateRememberedValue(var127);
                  var235 = var127;
               } else {
                  var235 = var100;
               }

               Function1 var56 = (Function1)var235;
               var55.endReplaceGroup();
               var6 = var56;
            }

            if ((var22 & 128) != 0) {
               var55.startReplaceGroup(1721063644);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var67 = var55;
               boolean var78 = false;
               int var89 = 0;
               Object var101 = var67.rememberedValue();
               int var114 = 0;
               Object var236;
               if (var101 == Composer.Companion.getEmpty()) {
                  int var128 = 0;
                  Object var129 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_473_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$5$lambda$4((io.intercom.android.sdk.models.Part) ((io.intercom.android.sdk.models.Part)recoveredExpressionArgument_473_0)));
                  var67.updateRememberedValue(var129);
                  var236 = var129;
               } else {
                  var236 = var101;
               }

               Function1 var57 = (Function1)var236;
               var55.endReplaceGroup();
               var7 = var57;
            }

            if ((var22 & 256) != 0) {
               var55.startReplaceGroup(1721065436);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var68 = var55;
               boolean var79 = false;
               int var90 = 0;
               Object var102 = var68.rememberedValue();
               int var115 = 0;
               Object var237;
               if (var102 == Composer.Companion.getEmpty()) {
                  int var130 = 0;
                  Object var131 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_474_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$7$lambda$6((io.intercom.android.sdk.models.Part) ((io.intercom.android.sdk.models.Part)recoveredExpressionArgument_474_0)));
                  var68.updateRememberedValue(var131);
                  var237 = var131;
               } else {
                  var237 = var102;
               }

               Function1 var58 = (Function1)var237;
               var55.endReplaceGroup();
               var8 = var58;
            }

            if ((var22 & 512) != 0) {
               var55.startReplaceGroup(1721067932);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var69 = var55;
               boolean var80 = false;
               int var91 = 0;
               Object var103 = var69.rememberedValue();
               int var116 = 0;
               Object var238;
               if (var103 == Composer.Companion.getEmpty()) {
                  int var132 = 0;
                  Object var133 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_475_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$9$lambda$8((io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData) ((io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData)recoveredExpressionArgument_475_0)));
                  var69.updateRememberedValue(var133);
                  var238 = var133;
               } else {
                  var238 = var103;
               }

               Function1 var59 = (Function1)var238;
               var55.endReplaceGroup();
               var9 = var59;
            }

            if ((var22 & 1024) != 0) {
               var55.startReplaceGroup(1721069628);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var70 = var55;
               boolean var81 = false;
               int var92 = 0;
               Object var104 = var70.rememberedValue();
               int var117 = 0;
               Object var239;
               if (var104 == Composer.Companion.getEmpty()) {
                  int var134 = 0;
                  Object var135 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_464_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$11$lambda$10((io.intercom.android.sdk.m5.conversation.states.AttributeData) ((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredExpressionArgument_464_0)));
                  var70.updateRememberedValue(var135);
                  var239 = var135;
               } else {
                  var239 = var104;
               }

               Function1 var60 = (Function1)var239;
               var55.endReplaceGroup();
               var10 = var60;
            }

            if ((var22 & 2048) != 0) {
               var55.startReplaceGroup(1721071068);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var71 = var55;
               boolean var82 = false;
               int var93 = 0;
               Object var105 = var71.rememberedValue();
               int var118 = 0;
               Object var240;
               if (var105 == Composer.Companion.getEmpty()) {
                  int var136 = 0;
                  Object var137 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$13$lambda$12());
                  var71.updateRememberedValue(var137);
                  var240 = var137;
               } else {
                  var240 = var105;
               }

               Function0 var61 = (Function0)var240;
               var55.endReplaceGroup();
               var11 = var61;
            }

            if ((var22 & 4096) != 0) {
               var55.startReplaceGroup(1721072572);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var72 = var55;
               boolean var83 = false;
               int var94 = 0;
               Object var106 = var72.rememberedValue();
               int var119 = 0;
               Object var241;
               if (var106 == Composer.Companion.getEmpty()) {
                  int var138 = 0;
                  Object var139 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_466_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$15$lambda$14((io.intercom.android.sdk.blocks.lib.models.TicketType) ((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredExpressionArgument_466_0)));
                  var72.updateRememberedValue(var139);
                  var241 = var139;
               } else {
                  var241 = var106;
               }

               Function1 var62 = (Function1)var241;
               var55.endReplaceGroup();
               var12 = var62;
            }

            if ((var22 & 8192) != 0) {
               var13 = false;
            }

            if ((var22 & 16384) != 0) {
               var55.startReplaceGroup(1721075740);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var73 = var55;
               boolean var84 = false;
               int var95 = 0;
               Object var107 = var73.rememberedValue();
               int var120 = 0;
               Object var242;
               if (var107 == Composer.Companion.getEmpty()) {
                  int var140 = 0;
                  Object var141 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_467_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$17$lambda$16((java.lang.String) ((java.lang.String)recoveredExpressionArgument_467_0)));
                  var73.updateRememberedValue(var141);
                  var242 = var141;
               } else {
                  var242 = var107;
               }

               Function1 var63 = (Function1)var242;
               var55.endReplaceGroup();
               var14 = var63;
            }

            if ((var22 & 32768) != 0) {
               var15 = false;
            }

            if ((var22 & 65536) != 0) {
               var16 = null;
            }

            if ((var22 & 131072) != 0) {
               var17 = true;
            }

            if ((var22 & 262144) != 0) {
               var55.startReplaceGroup(1721082460);
               ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
               Composer var74 = var55;
               boolean var85 = false;
               int var96 = 0;
               Object var108 = var74.rememberedValue();
               int var121 = 0;
               Object var243;
               if (var108 == Composer.Companion.getEmpty()) {
                  int var142 = 0;
                  Object var143 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$19$lambda$18());
                  var74.updateRememberedValue(var143);
                  var243 = var143;
               } else {
                  var243 = var108;
               }

               Function0 var64 = (Function0)var243;
               var55.endReplaceGroup();
               var18 = var64;
            }
         }

         var55.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2044549819, var23, var24, "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageList (StreamingMessageList.kt:114)"
            );
         }

         CompositionLocal var75 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var86 = 0;
         int var97 = 0;
         ComposerKt.sourceInformationMarkerStart(var55, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var109 = var55.consume(var75);
         ComposerKt.sourceInformationMarkerEnd(var55);
         Context var65 = (Context)var109;
         State var76 = SnapshotStateKt.collectAsState(var2, null, var55, 14 & var23 >> 6, 1);
         var55.startReplaceGroup(1721095358);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var110 = var55;
         boolean var122 = var55.changed(var0);
         int var144 = 0;
         Object var32 = var110.rememberedValue();
         int var33 = 0;
         Object var245;
         if (!var122 && var32 != Composer.Companion.getEmpty()) {
            var245 = var32;
         } else {
            int var34 = 0;
            List var35 = var0;
            int var36 = 0;
            int var37 = 0;
            Iterator var38 = var35.iterator();

            int var244;
            while (true) {
               if (!var38.hasNext()) {
                  var244 = -1;
                  break;
               }

               Object var39 = var38.next();
               ContentRow var40 = (ContentRow)var39;
               int var41 = 0;
               if (var40 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow || var40 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow) {
                  var244 = var37;
                  break;
               }

               var37++;
            }

            int var181 = var244;
            int var196 = 0;
            Object var42 = var181 < 0 ? var0.size() : var181;
            var110.updateRememberedValue(var42);
            var245 = var42;
         }

         int var98 = ((Number)var245).intValue();
         var55.endReplaceGroup();
         int var87 = var98;
         var55.startReplaceGroup(1721105658);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var123 = var55;
         boolean invalid$ivx = var55.changed(var0) | var55.changed(var87);
         int $i$f$cachex = 0;
         Object it$ivx = var123.rememberedValue();
         int var160/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var246;
         if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
            var246 = it$ivx;
         } else {
            int var169 = 0;
            ContentRow var182 = (ContentRow)CollectionsKt.getOrNull(var0, var87 - 1);
            Object var197 = Dp.recovered$89bc2ea8129b5eb8(
               !(var182 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TicketStatusRow) && !(var182 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$MergedConversationRow) ? DefaultMessageGap : BigMessageGap
            );
            var123.updateRememberedValue(var197);
            var246 = var197;
         }

         float var111 = ((Dp)var246).recovered$3537cef520440dc0();
         var55.endReplaceGroup();
         float var99 = var111;
         State var112 = KeyboardStateKt.keyboardAsState(var55, 0);
         var55.startReplaceGroup(1721118014);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var149 = var55;
         boolean var153 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var149.rememberedValue();
         int var183/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var247;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var198 = 0;
            Object var199 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var149.updateRememberedValue(var199);
            var247 = var199;
         } else {
            var247 = it$ivxx;
         }

         MutableFloatState var146 = (MutableFloatState)var247;
         var55.endReplaceGroup();
         MutableFloatState var124 = var146;
         var55.startReplaceGroup(1721120253);
         ComposerKt.sourceInformation(var55, "*138@7571L7,138@7582L25");
         float var248;
         if (var15) {
            CompositionLocal var162 = CompositionLocalsKt.getLocalDensity();
            int $changed$ivx = 0;
            int $i$f$getCurrentx = 0;
            ComposerKt.sourceInformationMarkerStart(var55, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var200 = var55.consume(var162);
            ComposerKt.sourceInformationMarkerEnd(var55);
            Density var172 = (Density)var200;
            int var185 = 0;
            var248 = var172.recovered$245d7c913f591981(PoweredByBadgeKt.getPoweredByBadgeHeight(var55, 0));
         } else {
            var248 = 0.0F;
         }

         float var150 = var248;
         var55.endReplaceGroup();
         float var147 = var150;
         var55.startReplaceGroup(1721125340);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var163 = var55;
         boolean var173 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var163.rememberedValue();
         int var209/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var249;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            int var215 = 0;
            Object var216 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var163.updateRememberedValue(var216);
            var249 = var216;
         } else {
            var249 = it$ivxxx;
         }

         MutableState var154 = (MutableState)var249;
         var55.endReplaceGroup();
         MutableState var151 = var154;
         Boolean var250 = StreamingMessageList$lambda$25(var112).isVisible();
         Boolean var10001 = StreamingMessageList$lambda$25(var112).isAnimating();
         var55.startReplaceGroup(1721129887);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var164 = var55;
         boolean var174 = var55.changed(var112) | var55.changed(var147) | ((var23 & 57344 ^ 24576) > 16384 && var55.changed(var4) || (var23 & 24576) == 16384);
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var164.rememberedValue();
         int var210/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var10002;
         if (!var174 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var10002 = it$ivxxxx;
         } else {
            Boolean var46 = var10001;
            Boolean var45 = var250;
            int var217 = 0;
            Function2 var47 = new StreamingMessageListKt$StreamingMessageList$11$1(var147, var4, var112, var124, null);
            var250 = var45;
            var10001 = var46;
            Object var222 = var47;
            var164.updateRememberedValue(var222);
            var10002 = var222;
         }

         Function2 var155 = (Function2)var10002;
         var55.endReplaceGroup();
         EffectsKt.LaunchedEffect(var250, var10001, var155, var55, 0);
         Boolean var251 = StreamingMessageList$lambda$25(var112).isDismissed();
         var55.startReplaceGroup(1721140783);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var165 = var55;
         boolean invalid$ivxx = var55.changed(var112) | ((var23 & 57344 ^ 24576) > 16384 && var55.changed(var4) || (var23 & 24576) == 16384);
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var165.rememberedValue();
         int var211/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var258;
         if (!invalid$ivxx && it$ivxxxxx != Composer.Companion.getEmpty()) {
            var258 = it$ivxxxxx;
         } else {
            Boolean var229 = var251;
            int var218 = 0;
            Function2 var257 = new StreamingMessageListKt$StreamingMessageList$12$1(var4, var112, var151, var124, null);
            var251 = var229;
            Object var223 = var257;
            var165.updateRememberedValue(var223);
            var258 = var223;
         }

         Function2 var156 = (Function2)var258;
         var55.endReplaceGroup();
         EffectsKt.LaunchedEffect(var251, var156, var55, 0);
         LazyListState var252 = var4;
         var55.startReplaceGroup(1721148756);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var166 = var55;
         boolean invalid$ivxxx = (var23 & 57344 ^ 24576) > 16384 && var55.changed(var4) || (var23 & 24576) == 16384;
         int $i$f$cachexxxxxx = 0;
         Object it$ivxxxxxx = var166.rememberedValue();
         int var212/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxx */ = 0;
         Object var260;
         if (!invalid$ivxxx && it$ivxxxxxx != Composer.Companion.getEmpty()) {
            var260 = it$ivxxxxxx;
         } else {
            LazyListState var230 = var4;
            int var219 = 0;
            Function2 var259 = new StreamingMessageListKt$StreamingMessageList$13$1(var4, var151, null);
            var252 = var230;
            Object var224 = var259;
            var166.updateRememberedValue(var224);
            var260 = var224;
         }

         Function2 var157 = (Function2)var260;
         var55.endReplaceGroup();
         EffectsKt.LaunchedEffect(var252, var157, var55, 14 & var23 >> 12);
         List var253 = var0;
         var55.startReplaceGroup(1721156914);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var167 = var55;
         boolean invalid$ivxxxx = var55.changedInstance(var0) | ((var23 & 57344 ^ 24576) > 16384 && var55.changed(var4) || (var23 & 24576) == 16384);
         int $i$f$cachexxxxxxx = 0;
         Object it$ivxxxxxxx = var167.rememberedValue();
         int var213/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxx */ = 0;
         Object var262;
         if (!invalid$ivxxxx && it$ivxxxxxxx != Composer.Companion.getEmpty()) {
            var262 = it$ivxxxxxxx;
         } else {
            List var231 = var0;
            int var220 = 0;
            Function2 var261 = new StreamingMessageListKt$StreamingMessageList$14$1(var0, var4, var151, null);
            var253 = var231;
            Object var225 = var261;
            var167.updateRememberedValue(var225);
            var262 = var225;
         }

         Function2 var158 = (Function2)var262;
         var55.endReplaceGroup();
         EffectsKt.LaunchedEffect(var253, var158, var55, 14 & var23);
         Modifier var159 = SizeKt.fillMaxSize$default(var3, 0.0F, 1, null);
         int var178 = 16;
         int var191 = 0;
         float var179 = Dp.recovered$95b3d103663bf060(var178);
         float var10003;
         if (var13) {
            var10003 = MessageComposerKt.getComposerHalfSize();
         } else {
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var193 = var10003;
         int var207 = 0;
         PaddingValues var168 = PaddingKt.recovered$0229d0d9fcc63c3d(0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var179 + var193), 7, null);
         Vertical var254;
         if (StreamingMessageList$lambda$20(var76) && var87 >= var0.size()) {
            var254 = Arrangement.INSTANCE.getTop();
         } else {
            ContentRow var194 = (ContentRow)CollectionsKt.lastOrNull(var0);
            var254 = !(var194 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow) && !(var194 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$ComposerSuggestionRow) && !(var194 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow)
               ? Arrangement.INSTANCE.getTop()
               : IntercomArrangement.INSTANCE.itemAtBottom(CollectionsKt.getLastIndex(var0) + (StreamingMessageList$lambda$20(var76) ? 1 : 0));
         }

         Vertical var180 = var254;
         Horizontal var195 = Alignment.Companion.getCenterHorizontally();
         Modifier var255 = var159;
         LazyListState var263 = var4;
         PaddingValues var264 = var168;
         boolean var265 = false;
         Vertical var10004 = var180;
         Horizontal var10005 = var195;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var55.startReplaceGroup(1721207215);
         ComposerKt.sourceInformation(var55, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var214 = var55;
         boolean var221 = var55.changedInstance(var0)
            | var55.changed(var87)
            | var55.changed(var76)
            | var55.changedInstance(var1)
            | ((var23 & 57344 ^ 24576) > 16384 && var55.changed(var4) || (var23 & 24576) == 16384)
            | var55.changed(var99)
            | (var24 & 112) == 32
            | (var23 & 458752) == 131072
            | (var23 & 3670016) == 1048576
            | var55.changedInstance(var65)
            | (var23 & 29360128) == 8388608
            | (var23 & 234881024) == 67108864
            | (var24 & 14) == 4
            | (var23 & 1879048192) == 536870912
            | (var24 & 896) == 256
            | (var24 & 3670016) == 1048576
            | (var24 & 29360128) == 8388608
            | (var24 & 234881024) == 67108864
            | (var24 & 57344) == 16384;
         int $i$f$cachexxxxxxxx = 0;
         Object it$ivxxxxxxxx = var214.rememberedValue();
         int var228/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxxxx */ = 0;
         Object var10009;
         if (!var221 && it$ivxxxxxxxx != Composer.Companion.getEmpty()) {
            var10009 = it$ivxxxxxxxx;
         } else {
            Object var53 = null;
            boolean var52 = false;
            Object var51 = null;
            Horizontal var50 = var195;
            Vertical var49 = var180;
            boolean var48 = false;
            PaddingValues var234 = var168;
            LazyListState var233 = var4;
            Modifier var232 = var159;
            int var43 = 0;
            final java.util.List recoveredExpressionCapture_469_0 = var0;
            final int recoveredExpressionCapture_469_1 = var87;
            final kotlinx.coroutines.flow.StateFlow recoveredExpressionCapture_469_2 = var1;
            final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_469_3 = var4;
            final float recoveredExpressionCapture_469_4 = var99;
            final androidx.compose.runtime.State recoveredExpressionCapture_469_5 = var76;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_469_6 = var151;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_469_7 = var11;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_8 = var5;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_9 = var6;
            final android.content.Context recoveredExpressionCapture_469_10 = var65;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_11 = var7;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_12 = var8;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_13 = var10;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_14 = var9;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_15 = var12;
            final kotlin.jvm.functions.Function2 recoveredExpressionCapture_469_16 = var16;
            final boolean recoveredExpressionCapture_469_17 = var17;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_469_18 = var18;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_469_19 = var14;
            Function1 var54 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_469_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$41$lambda$40(recoveredExpressionCapture_469_0, recoveredExpressionCapture_469_1, recoveredExpressionCapture_469_2, recoveredExpressionCapture_469_3, recoveredExpressionCapture_469_4, recoveredExpressionCapture_469_5, recoveredExpressionCapture_469_6, recoveredExpressionCapture_469_7, recoveredExpressionCapture_469_8, recoveredExpressionCapture_469_9, recoveredExpressionCapture_469_10, recoveredExpressionCapture_469_11, recoveredExpressionCapture_469_12, recoveredExpressionCapture_469_13, recoveredExpressionCapture_469_14, recoveredExpressionCapture_469_15, recoveredExpressionCapture_469_16, recoveredExpressionCapture_469_17, recoveredExpressionCapture_469_18, recoveredExpressionCapture_469_19, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_469_0)));
            var255 = var232;
            var263 = var233;
            var264 = var234;
            var265 = var48;
            var10004 = var49;
            var10005 = var50;
            var10006 = var51;
            var10007 = var52;
            var10008 = var53;
            Object var44 = var54;
            var214.updateRememberedValue(var44);
            var10009 = var44;
         }

         Function1 var208 = (Function1)var10009;
         var55.endReplaceGroup();
         LazyDslKt.LazyColumn(
            var255,
            var263,
            var264,
            var265,
            var10004,
            var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var208,
            var55,
            199680 | 112 & var23 >> 9,
            448
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var256 = var55.endRestartGroup();
      if (var256 != null) {
         final java.util.List recoveredExpressionCapture_470_0 = var0;
         final kotlinx.coroutines.flow.StateFlow recoveredExpressionCapture_470_1 = var1;
         final kotlinx.coroutines.flow.StateFlow recoveredExpressionCapture_470_2 = var2;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_470_3 = var3;
         final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_470_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_7 = var7;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_8 = var8;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_9 = var9;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_10 = var10;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_470_11 = var11;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_12 = var12;
         final boolean recoveredExpressionCapture_470_13 = var13;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_470_14 = var14;
         final boolean recoveredExpressionCapture_470_15 = var15;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_470_16 = var16;
         final boolean recoveredExpressionCapture_470_17 = var17;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_470_18 = var18;
         final int recoveredExpressionCapture_470_19 = var20;
         final int recoveredExpressionCapture_470_20 = var21;
         final int recoveredExpressionCapture_470_21 = var22;
         var256.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_470_0, recoveredExpressionArgument_470_1) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$42(recoveredExpressionCapture_470_0, recoveredExpressionCapture_470_1, recoveredExpressionCapture_470_2, recoveredExpressionCapture_470_3, recoveredExpressionCapture_470_4, recoveredExpressionCapture_470_5, recoveredExpressionCapture_470_6, recoveredExpressionCapture_470_7, recoveredExpressionCapture_470_8, recoveredExpressionCapture_470_9, recoveredExpressionCapture_470_10, recoveredExpressionCapture_470_11, recoveredExpressionCapture_470_12, recoveredExpressionCapture_470_13, recoveredExpressionCapture_470_14, recoveredExpressionCapture_470_15, recoveredExpressionCapture_470_16, recoveredExpressionCapture_470_17, recoveredExpressionCapture_470_18, recoveredExpressionCapture_470_19, recoveredExpressionCapture_470_20, recoveredExpressionCapture_470_21, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_470_0), ((java.lang.Integer)recoveredExpressionArgument_470_1).intValue())));
      }
   }

   private static final void recovered$cfc12e18ed678c85(LazyListScope var0, StateFlow var1, LazyListState var2, Function0 var3, float var4) {
      LazyListScope.item$default(
         var0,
         "streaming_message",
         null,
         ComposableLambdaKt.composableLambdaInstance(339555046, true, new StreamingMessageListKt$streamingAnswerItem$1(var2, var3, var4, var1)),
         2,
         null
      );
   }

   public static final float gapWithPrevious(@NotNull ContentRow var0, int var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var0, "contentRow");
      Intrinsics.checkNotNullParameter(var2, "allContentRows");
      ContentRow var3 = var0;
      float var10000;
      if (var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$DayDividerRow) {
         var10000 = LargeMessageGap;
      } else if (!(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$SpecialNoticeRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TeamIntroRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$AskedAboutRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$BigTicketRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$ComposerSuggestionRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$EventRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$NewMessagesRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FooterNoticeRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$PostCardRow)
         && !(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$NoteCardRow)) {
         if (var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TicketStatusRow) {
            var10000 = BigMessageGap;
         } else {
            if (!(var3 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$MergedConversationRow)) {
               throw new NoWhenBranchMatchedException();
            }

            var10000 = BigMessageGap;
         }
      } else {
         GroupingPosition var8;
         label80: {
            io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow var7 = var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow ? (io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow)var0 : null;
            if ((var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow ? (io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow)var0 : null) != null) {
               var8 = var7.getGroupingPosition();
               if (var8 != null) {
                  break label80;
               }
            }

            var8 = (var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow ? (io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow)var0 : null) != null
               ? (var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow ? (io.intercom.android.sdk.m5.conversation.states.ContentRow$FinAnswerRow)var0 : null).getGroupingPosition()
               : GroupingPosition.STANDALONE;
         }

         GroupingPosition var4 = var8;
         switch (var4) {
            case STANDALONE:
            case TOP:
               ContentRow var5 = (ContentRow)CollectionsKt.getOrNull(var2, var1 - 1);
               ContentRow var6 = var5;
               var10000 = var6 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TicketStatusRow ? BigMessageGap : (var6 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$MergedConversationRow ? BigMessageGap : DefaultMessageGap);
               break;
            case MIDDLE:
            case BOTTOM:
               var10000 = GroupedMessageGap;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }

      return var10000;
   }

   public static final float getGroupedMessageGap() {
      return GroupedMessageGap;
   }

   public static final float getDefaultMessageGap() {
      return DefaultMessageGap;
   }

   public static final float getBigMessageGap() {
      return BigMessageGap;
   }

   public static final boolean shouldFadeInItem(@NotNull ContentRow var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow
         || var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow
            && ((io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow)var0).getPartWrapper().isLastPart()
            && ((io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow)var0).getPartWrapper().getPart().getMessageState() == io.intercom.android.sdk.models.Part$MessageState.SENDING
         || var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow;
   }

   public static final boolean shouldFadeOutItem(@NotNull ContentRow var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$TypingIndicatorRow || var0 instanceof io.intercom.android.sdk.m5.conversation.states.ContentRow$QuickRepliesRow;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void StreamingMessageListPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-178274929);
      ComposerKt.sourceInformation(var2, "C(StreamingMessageListPreview)544@24431L1433:StreamingMessageList.kt#o19g89");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -178274929, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListPreview (StreamingMessageList.kt:543)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$StreamingMessageListKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_478_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_478_0, recoveredExpressionArgument_478_1) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageListPreview$lambda$43(recoveredExpressionCapture_478_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_478_0), ((java.lang.Integer)recoveredExpressionArgument_478_1).intValue())));
      }
   }

   private static final Unit StreamingMessageList$lambda$1$lambda$0(ReplySuggestion var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$3$lambda$2(ReplyOption var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$5$lambda$4(Part var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$7$lambda$6(Part var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$9$lambda$8(io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$11$lambda$10(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$13$lambda$12() {
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$15$lambda$14(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$17$lambda$16(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$19$lambda$18() {
      return Unit.INSTANCE;
   }

   private static final boolean StreamingMessageList$lambda$20(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final KeyboardState StreamingMessageList$lambda$25(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (KeyboardState)var1.getValue();
   }

   private static final float StreamingMessageList$lambda$27(MutableFloatState var0) {
      FloatState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getFloatValue();
   }

   private static final void StreamingMessageList$lambda$28(MutableFloatState var0, float var1) {
      MutableFloatState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setFloatValue(var1);
   }

   private static final boolean StreamingMessageList$lambda$31(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void StreamingMessageList$lambda$32(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final boolean StreamingMessageList$lambda$41$lambda$40$lambda$38$lambda$37(MutableState var0) {
      return StreamingMessageList$lambda$31(var0);
   }

   private static final boolean StreamingMessageList$lambda$41$lambda$40$lambda$39(MutableState var0) {
      return StreamingMessageList$lambda$31(var0);
   }

   private static final Unit StreamingMessageList$lambda$41$lambda$40(
      List var0,
      int var1,
      StateFlow var2,
      LazyListState var3,
      float var4,
      State var5,
      MutableState var6,
      Function0 var7,
      Function1 var8,
      Function1 var9,
      Context var10,
      Function1 var11,
      Function1 var12,
      Function1 var13,
      Function1 var14,
      Function1 var15,
      Function2 var16,
      boolean var17,
      Function0 var18,
      Function1 var19,
      LazyListScope var20
   ) {
      Intrinsics.checkNotNullParameter(var20, "$this$LazyColumn");
      Iterable var21 = var0;
      int var22 = 0;
      int var23 = 0;

      for (Object var25 : var21) {
         int var26 = var23++;
         if (var26 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         ContentRow var27 = (ContentRow)var25;
         int var28 = var26;
         int var29 = 0;
         if (var28 == var1 && StreamingMessageList$lambda$20(var5)) {
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_477_0 = var6;
            recovered$cfc12e18ed678c85(var20, var2, var3, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$41$lambda$40$lambda$38$lambda$37(recoveredExpressionCapture_477_0)), var4);
         }

         LazyListScope.item$default(
            var20,
            var27.getKey(),
            null,
            ComposableLambdaKt.composableLambdaInstance(
               -1908385488,
               true,
               new StreamingMessageListKt$StreamingMessageList$15$1$1$2(
                  var28, var1, var27, var0, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var5
               )
            ),
            2,
            null
         );
      }

      if (var1 >= var0.size() && StreamingMessageList$lambda$20(var5)) {
         final androidx.compose.runtime.MutableState recoveredExpressionCapture_476_0 = var6;
         recovered$cfc12e18ed678c85(var20, var2, var3, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt.StreamingMessageList$lambda$41$lambda$40$lambda$39(recoveredExpressionCapture_476_0)), var4);
      }

      LazyListScope.item$default(
         var20,
         "last_anchor_row",
         null,
         io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$StreamingMessageListKt.INSTANCE.recovered$b26556cd5c386b55(),
         2,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageList$lambda$42(
      List var0,
      StateFlow var1,
      StateFlow var2,
      Modifier var3,
      LazyListState var4,
      Function1 var5,
      Function1 var6,
      Function1 var7,
      Function1 var8,
      Function1 var9,
      Function1 var10,
      Function0 var11,
      Function1 var12,
      boolean var13,
      Function1 var14,
      boolean var15,
      Function2 var16,
      boolean var17,
      Function0 var18,
      int var19,
      int var20,
      int var21,
      Composer var22,
      int var23
   ) {
      StreamingMessageList(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var22,
         RecomposeScopeImplKt.updateChangedFlags(var19 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var20),
         var21
      );
      return Unit.INSTANCE;
   }

   private static final Unit StreamingMessageListPreview$lambda$43(int var0, Composer var1, int var2) {
      StreamingMessageListPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final KeyboardState access$StreamingMessageList$lambda$25(State var0) {
      return StreamingMessageList$lambda$25(var0);
   }

   // $VF: synthetic method
   public static final void access$StreamingMessageList$lambda$28(MutableFloatState var0, float var1) {
      StreamingMessageList$lambda$28(var0, var1);
   }

   // $VF: synthetic method
   public static final float access$StreamingMessageList$lambda$27(MutableFloatState var0) {
      return StreamingMessageList$lambda$27(var0);
   }

   // $VF: synthetic method
   public static final boolean access$StreamingMessageList$lambda$31(MutableState var0) {
      return StreamingMessageList$lambda$31(var0);
   }

   // $VF: synthetic method
   public static final void access$StreamingMessageList$lambda$32(MutableState var0, boolean var1) {
      StreamingMessageList$lambda$32(var0, var1);
   }

   // $VF: synthetic method
   public static final boolean access$StreamingMessageList$lambda$20(State var0) {
      return StreamingMessageList$lambda$20(var0);
   }

   static {
      int var0 = 2;
      int var1 = 0;
      GroupedMessageGap = Dp.recovered$95b3d103663bf060(var0);
      int $this$dp$ivx = 16;
      int $i$f$getDpx = 0;
      DefaultMessageGap = Dp.recovered$95b3d103663bf060($this$dp$ivx);
      int $this$dp$ivxx = 24;
      int $i$f$getDpxx = 0;
      BigMessageGap = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
      int $this$dp$ivxxx = 32;
      int $i$f$getDpxxx = 0;
      LargeMessageGap = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
   }
}
