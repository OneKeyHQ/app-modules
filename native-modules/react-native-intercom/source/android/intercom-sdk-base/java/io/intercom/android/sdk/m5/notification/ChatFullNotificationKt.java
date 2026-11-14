package io.intercom.android.sdk.m5.notification;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.layout.LayoutModifierKt;
import androidx.compose.ui.layout.Measurable;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.layout.MeasureResult;
import androidx.compose.ui.layout.MeasureScope;
import androidx.compose.ui.layout.Placeable;
import androidx.compose.ui.layout.Placeable.PlacementScope;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.ComposeView;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Constraints;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.TextWithSeparatorKt;
import io.intercom.android.sdk.m5.components.TicketHeaderKt;
import io.intercom.android.sdk.m5.components.TicketStatusHeaderArgs;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationStateSyncSettings;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.overlay.ComposeCompatibilityUtilKt;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u001a\"\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0000\u001a'\u0010\b\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0002\u0010\u000b\u001a \u0010\f\u001a\u00020\n*\u00020\n2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000eH\u0002\u001a\u001d\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u001a\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016\"\u000e\u0010\u0014\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b²\u0006\n\u0010\u001c\u001a\u00020\u000fX\u008a\u008e\u0002",
   d2 = {
         "addChatFullNotificationToView",
         "",
         "composeView",
         "Landroidx/compose/ui/platform/ComposeView;",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "ChatFullNotification",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/models/Conversation;Lio/intercom/android/sdk/identity/AppConfig;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "detectOverflow",
         "onOverflowChanged",
         "Lkotlin/Function1;",
         "",
         "ChatFullContent",
         "context",
         "Landroid/content/Context;",
         "(Lio/intercom/android/sdk/models/Conversation;Landroid/content/Context;Landroidx/compose/runtime/Composer;I)V",
         "previewAppConfig",
         "ChatFullNotificationPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ChatFullNotificationShortMessagePreview",
         "ChatFullNotificationBotPreview",
         "ChatFullNotificationTicketPreview",
         "ChatFullNotificationAssignedTicketPreview",
         "intercom-sdk-base_release",
         "isOverflowing"
   }
)
@SourceDebugExtension(
   "SMAP\nChatFullNotification.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChatFullNotification.kt\nio/intercom/android/sdk/m5/notification/ChatFullNotificationKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 11 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,393:1\n75#2:394\n1047#3,6:395\n1047#3,6:435\n118#4:401\n118#4:402\n118#4:441\n118#4:474\n118#4:503\n118#4:508\n118#4:513\n118#4:518\n70#5:403\n67#5,9:404\n77#5:517\n81#6,6:413\n88#6,6:428\n81#6,6:452\n88#6,6:467\n81#6,6:481\n88#6,6:496\n96#6:506\n96#6:511\n96#6:516\n81#6,6:528\n88#6,6:543\n96#6:554\n402#7,9:419\n411#7:434\n402#7,9:458\n411#7:473\n402#7,9:487\n411#7:502\n412#7,2:504\n412#7,2:509\n412#7,2:514\n402#7,9:534\n411#7:549\n412#7,2:552\n87#8:442\n84#8,9:443\n94#8:512\n87#8:519\n85#8,8:520\n94#8:555\n99#9,6:475\n106#9:507\n1869#10,2:550\n85#11:556\n117#11,2:557\n*S KotlinDebug\n*F\n+ 1 ChatFullNotification.kt\nio/intercom/android/sdk/m5/notification/ChatFullNotificationKt\n*L\n85#1:394\n87#1:395,6\n98#1:435,6\n91#1:401\n92#1:402\n99#1:441\n105#1:474\n109#1:503\n130#1:508\n144#1:513\n182#1:518\n89#1:403\n89#1:404,9\n89#1:517\n89#1:413,6\n89#1:428,6\n95#1:452,6\n95#1:467,6\n102#1:481,6\n102#1:496,6\n102#1:506\n95#1:511\n89#1:516\n180#1:528,6\n180#1:543,6\n180#1:554\n89#1:419,9\n89#1:434\n95#1:458,9\n95#1:473\n102#1:487,9\n102#1:502\n102#1:504,2\n95#1:509,2\n89#1:514,2\n180#1:534,9\n180#1:549\n180#1:552,2\n95#1:442\n95#1:443,9\n95#1:512\n180#1:519\n180#1:520,8\n180#1:555\n102#1:475,6\n102#1:507\n184#1:550,2\n87#1:556\n87#1:557,2\n*E\n"
)
public final class ChatFullNotificationKt {
   @NotNull
   private static final AppConfig previewAppConfig = new AppConfig(
      "",
      -12627531,
      -13615201,
      -15064194,
      false,
      false,
      false,
      100,
      1000L,
      1000L,
      1000L,
      1000L,
      true,
      true,
      "",
      "",
      false,
      true,
      "",
      SetsKt.emptySet(),
      SetsKt.emptySet(),
      "",
      "",
      false,
      true,
      false,
      false,
      false,
      null,
      new NexusConfig(),
      AttachmentSettings.Companion.getDEFAULT(),
      true,
      ConversationStateSyncSettings.Companion.getDEFAULT(),
      true
   );

   public static final void addChatFullNotificationToView(@Nullable ComposeView var0, @NotNull Conversation var1, @NotNull AppConfig var2) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      if (var0 != null && !ComposeCompatibilityUtilKt.isLegacyActivity(var0)) {
         ComposeView var3 = var0;
         ComposeView var4 = var3;
         int var5 = 0;
         var4.setContent(
            ComposableLambdaKt.composableLambdaInstance(1805122629, true, new ChatFullNotificationKt$addChatFullNotificationToView$1$1(var1, var2))
         );
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotification(
      @NotNull Conversation var0, @NotNull AppConfig var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversation");
      Intrinsics.checkNotNullParameter(var1, "appConfig");
      var3 = var3.startRestartGroup(1263781866);
      ComposerKt.sourceInformation(var3, "C(ChatFullNotification)P(1)84@3403L7,86@3479L34,92@3693L6,88@3519L2297:ChatFullNotification.kt#1zjczn");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 131) == 130 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1263781866, var6, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotification (ChatFullNotification.kt:83)");
         }

         CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var3.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var7 = (Context)var11;
         Part var97 = var0.lastPart();
         var3.startReplaceGroup(-1543305275);
         ComposerKt.sourceInformation(var3, "CC(remember):ChatFullNotification.kt#9igjgp");
         Composer var104 = var3;
         boolean var12 = false;
         int var13 = 0;
         Object var14 = var104.rememberedValue();
         int var15 = 0;
         Object var10000;
         if (var14 == Composer.Companion.getEmpty()) {
            int var16 = 0;
            Object var113 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var104.updateRememberedValue(var113);
            var10000 = var113;
         } else {
            var10000 = var14;
         }

         MutableState var99 = (MutableState)var10000;
         var3.endReplaceGroup();
         MutableState var98 = var99;
         var10 = 4;
         int var105 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var10);
         var10 = 8;
         int $i$f$getDpx = 0;
         Modifier var135 = ShadowKt.recovered$04904c26f7ded95f(
            var2, var10001, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var10)), false, 0L, 0L, 24, null
         );
         var10 = 8;
         int $i$f$getDpxx = 0;
         Modifier var103 = BackgroundKt.recovered$42a3174180b3fb7a(
            ClipKt.clip(var135, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var10))),
            IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         byte var110 = 0;
         var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var108 = Alignment.Companion.getTopStart();
         var12 = false;
         MeasurePolicy var112 = BoxKt.maybeCachedBoxMeasurePolicy(var108, var12);
         Modifier var114 = var103;
         MeasurePolicy var17 = var112;
         Composer var18 = var3;
         int var19 = 112 & var110 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var114);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var33 = 6 | 112 & var110 >> 6;
         Composer var34 = var31;
         BoxScope var35 = BoxScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, -818927585, "C97@3832L22,94@3728L1617:ChatFullNotification.kt#1zjczn");
         var135 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         var34.startReplaceGroup(-1134794408);
         ComposerKt.sourceInformation(var34, "CC(remember):ChatFullNotification.kt#9igjgp");
         Composer var37 = var34;
         boolean var38 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var37.rememberedValue();
         int var41/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         kotlin.jvm.functions.Function1 var144;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Modifier var42 = var135;
            int var43 = 0;
            final androidx.compose.runtime.MutableState recoveredCapture_1108_0 = var98;
            var144 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1108_0) -> ChatFullNotificationKt.ChatFullNotification$lambda$8$lambda$5$lambda$4(recoveredCapture_1108_0, ((java.lang.Boolean)recoveredArgument_1108_0).booleanValue()));
            var135 = var42;
            Object var44 = var144;
            var37.updateRememberedValue(var44);
            var144 = (Function1)var44;
         } else {
            var144 = (Function1)it$ivx;
         }

         Function1 var45 = var144;
         var34.endReplaceGroup();
         var135 = detectOverflow(var135, var45);
         int var124 = 16;
         int $i$f$getDpxxx = 0;
         Modifier var125 = PaddingKt.recovered$eb6a744cd98cdbca(var135, Dp.recovered$95b3d103663bf060(var124));
         byte var119 = 0;
         it$ivx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var116 = Arrangement.INSTANCE.getTop();
         Horizontal var118 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var116, var118, var34, 14 & var119 >> 3 | 112 & var119 >> 3);
         Modifier var122 = var125;
         MeasurePolicy var123 = measurePolicy$ivx;
         Composer var46 = var34;
         int var47 = 112 & var119 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var122);
         Function0 var52 = ComposeUiNode.Companion.getConstructor();
         Composer var53 = var46;
         int var54 = 6 | 896 & var47 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var53, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var53.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var53.startReusableNode();
         if (var53.getInserting()) {
            var53.createNode(var52);
         } else {
            var53.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var53);
         int var57/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var123, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var58 = 14 & var54 >> 6;
         Composer $composer$ivx = var53;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var61 = 6 | 112 & var119 >> 6;
         Composer var62 = $composer$ivx;
         ColumnScope var63 = ColumnScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var62, -952153564, "C101@3961L1164,129@5139L41,132@5225L110:ChatFullNotification.kt#1zjczn");
         Modifier var65 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.ui.Alignment.Vertical var66 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivx = 12;
         int $i$f$getDpxxxx = 0;
         HorizontalOrVertical var69 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         short var130 = 438;
         $i$f$getDpxxxx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var62, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var69, var66, var62, 14 & var130 >> 3 | 112 & var130 >> 3);
         Modifier var71 = var65;
         MeasurePolicy var72 = measurePolicy$ivxx;
         Composer var73 = var62;
         int var74 = 112 & var130 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var73, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var73, 0));
         CompositionLocalMap localMap$iv$ivxx = var73.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var73, var71);
         Function0 var79 = ComposeUiNode.Companion.getConstructor();
         Composer var80 = var73;
         int var81 = 6 | 896 & var74 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var80, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var80.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var80.startReusableNode();
         if (var80.getInserting()) {
            var80.createNode(var79);
         } else {
            var80.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var80);
         int var84/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var72, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var85 = 14 & var81 >> 6;
         Composer $composer$ivxx = var80;
         int var87 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var88 = 6 | 112 & var130 >> 6;
         Composer var89 = $composer$ivxx;
         RowScope var90 = RowScopeInstance.INSTANCE;
         int var91 = 0;
         ComposerKt.sourceInformationMarkerStart(var89, 2114918339, "C106@4183L152:ChatFullNotification.kt#1zjczn");
         AvatarWrapper var92 = PartExtensionsKt.resolvedAvatarWrapper(var97);
         var135 = Modifier.Companion;
         int $this$dp$ivxx = 32;
         int $i$f$getDpxxxxx = 0;
         AvatarIconKt.recovered$f5ec3c81e3146789(
            SizeKt.recovered$111ef94c93e78c04(var135, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var92, null, false, 0L, null, var89, 6, 60
         );
         if (var0.getTicket() != null) {
            var89.startReplaceGroup(2115101176);
            ComposerKt.sourceInformation(var89, "112@4408L290");
            TicketHeaderKt.TicketHeader(
               null,
               new TicketStatusHeaderArgs(var0.getTicket().getTitle(), var0.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold()),
               var89,
               0,
               1
            );
            var89.endReplaceGroup();
         } else {
            var89.startReplaceGroup(2115436317);
            ComposerKt.sourceInformation(var89, "122@4949L10,119@4744L349");
            String var132 = PartExtensionsKt.resolvedDisplayTitle(var97);
            String var95 = PartExtensionsKt.resolvedDisplaySuffix(var97);
            TextStyle var133 = IntercomTheme.INSTANCE.getTypography(var89, IntercomTheme.$stable).getType04SemiBold();
            $i$f$getDpxxxxx = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextWithSeparatorKt.recovered$741c28fc37ac0ca8(var132, var95, null, null, var133, 0L, $i$f$getDpxxxxx, 1, null, var89, 14155776, 300);
            var89.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var89);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var80.endNode();
         ComposerKt.sourceInformationMarkerEnd(var80);
         ComposerKt.sourceInformationMarkerEnd(var73);
         ComposerKt.sourceInformationMarkerEnd(var62);
         var135 = Modifier.Companion;
         int $this$dp$ivxxx = 12;
         int $i$f$getDpxxxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var135, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var62, 6);
         ChatFullContent(var0, var7, var62, 14 & var6);
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var34.startReplaceGroup(-1134745239);
         ComposerKt.sourceInformation(var34, "146@5718L6,139@5388L412");
         if (ChatFullNotification$lambda$2(var98)) {
            var135 = SizeKt.fillMaxWidth$default(var35.align(Modifier.Companion, Alignment.Companion.getBottomCenter()), 0.0F, 1, null);
            int $this$dp$ivxxxx = 100;
            int $i$f$getDpxxxxxxx = 0;
            var135 = SizeKt.recovered$3f6c6a76aa292717(var135, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx));
            Companion var145 = Brush.Companion;
            Color[] var127 = new Color[]{
               Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$562f21ab17d9b4c7()),
               Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05())
            };
            BoxKt.Box(
               BackgroundKt.background$default(
                  var135, Companion.recovered$ea3ca130c5d0d624(var145, CollectionsKt.listOf(var127), 0.0F, 0.0F, 0, 14, null), null, 0.0F, 6, null
               ),
               var34,
               0
            );
         }

         var34.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var142 = var3.endRestartGroup();
      if (var142 != null) {
         final io.intercom.android.sdk.models.Conversation recoveredCapture_1107_0 = var0;
         final io.intercom.android.sdk.identity.AppConfig recoveredCapture_1107_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_1107_2 = var2;
         final int recoveredCapture_1107_3 = var4;
         final int recoveredCapture_1107_4 = var5;
         var142.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1107_0, recoveredArgument_1107_1) -> ChatFullNotificationKt.ChatFullNotification$lambda$9(recoveredCapture_1107_0, recoveredCapture_1107_1, recoveredCapture_1107_2, recoveredCapture_1107_3, recoveredCapture_1107_4, (androidx.compose.runtime.Composer)recoveredArgument_1107_0, ((java.lang.Integer)recoveredArgument_1107_1).intValue())));
      }
   }

   private static final Modifier detectOverflow(Modifier var0, Function1 var1) {
      final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1114_0 = var1;
      return LayoutModifierKt.layout(var0, ((kotlin.jvm.functions.Function3) (recoveredExpressionArgument_1114_0, recoveredExpressionArgument_1114_1, recoveredExpressionArgument_1114_2) -> io.intercom.android.sdk.m5.notification.ChatFullNotificationKt.detectOverflow$lambda$11(recoveredExpressionCapture_1114_0, (androidx.compose.ui.layout.MeasureScope) ((androidx.compose.ui.layout.MeasureScope)recoveredExpressionArgument_1114_0), (androidx.compose.ui.layout.Measurable) ((androidx.compose.ui.layout.Measurable)recoveredExpressionArgument_1114_1), (androidx.compose.ui.unit.Constraints) ((androidx.compose.ui.unit.Constraints)recoveredExpressionArgument_1114_2))));
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ChatFullContent(Conversation var0, Context var1, Composer var2, int var3) {
      var2 = var2.startRestartGroup(649010099);
      ComposerKt.sourceInformation(var2, "C(ChatFullContent)P(1):ChatFullNotification.kt#1zjczn");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(649010099, var4, -1, "io.intercom.android.sdk.m5.notification.ChatFullContent (ChatFullNotification.kt:173)");
         }

         Part var5 = var0.lastPart();
         List var6 = var5.getBlocks();
         if (var6 != null && !var6.isEmpty()) {
            var2.startReplaceGroup(343127856);
            ComposerKt.sourceInformation(var2, "179@6734L481");
            Modifier var42 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int var44 = 12;
            int var46 = 0;
            Vertical var45 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var44));
            byte var10 = 54;
            int var11 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Horizontal var47 = Alignment.Companion.getStart();
            MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var45, var47, var2, 14 & var10 >> 3 | 112 & var10 >> 3);
            Modifier var13 = var42;
            MeasurePolicy var14 = var12;
            Composer var15 = var2;
            int var16 = 112 & var10 << 3;
            int var17 = 0;
            ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
            CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
            Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
            Function0 var21 = ComposeUiNode.Companion.getConstructor();
            Composer var22 = var15;
            int var23 = 6 | 896 & var16 << 6;
            int var24 = 0;
            ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var22.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var22.startReusableNode();
            if (var22.getInserting()) {
               var22.createNode(var21);
            } else {
               var22.useNode();
            }

            Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
            int var26 = 0;
            Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
            int var27 = 14 & var23 >> 6;
            Composer var28 = var22;
            int var29 = 0;
            ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var30 = 6 | 112 & var10 >> 6;
            Composer var31 = var28;
            ColumnScope var32 = ColumnScopeInstance.INSTANCE;
            int var33 = 0;
            ComposerKt.sourceInformationMarkerStart(var31, -1329550597, "C:ChatFullNotification.kt#1zjczn");
            var31.startReplaceGroup(1065490145);
            ComposerKt.sourceInformation(var31, "*188@7130L6,184@6919L272");
            Iterable var34 = var6;
            int var35 = 0;

            for (Object var37 : var34) {
               Block var38 = (Block)var37;
               int var39 = 0;
               Modifier var49 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               Intrinsics.checkNotNull(var38);
               BlockViewKt.recovered$5709084683a88fd9(
                  var49,
                  new BlockRenderData(
                     var38,
                     Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a()),
                     null,
                     null,
                     null,
                     28,
                     null
                  ),
                  false,
                  null,
                  false,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  false,
                  null,
                  null,
                  false,
                  null,
                  var31,
                  6,
                  0,
                  32764
               );
            }

            var31.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var31);
            ComposerKt.sourceInformationMarkerEnd(var28);
            var22.endNode();
            ComposerKt.sourceInformationMarkerEnd(var22);
            ComposerKt.sourceInformationMarkerEnd(var15);
            ComposerKt.sourceInformationMarkerEnd(var2);
            var2.endReplaceGroup();
         } else {
            String var10000 = var5.getSummary();
            Intrinsics.checkNotNullExpressionValue(var10000, "getSummary(...)");
            if (!StringsKt.isBlank(var10000)) {
               var2.startReplaceGroup(343653213);
               ComposerKt.sourceInformation(var2, "197@7408L10,195@7331L153");
               var10000 = var5.getSummary();
               Intrinsics.checkNotNullExpressionValue(var10000, "getSummary(...)");
               String var7 = var10000;
               TextStyle var8 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType05();
               Modifier var9 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               TextKt.recovered$5b114ab32a4f4190(
                  var7, var9, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var8, var2, 48, 0, 131068
               );
               var2.endReplaceGroup();
            } else {
               var2.startReplaceGroup(343892502);
               ComposerKt.sourceInformation(var2, "205@7726L213");
               if (var0.getTicket() != null) {
                  Ticket var41 = var0.getTicket();
                  String var43 = Intrinsics.areEqual(var41.getCurrentStatus().getType(), "submitted") ? null : var41.getAssignee().build().getForename();
                  InAppNotificationCardKt.TicketInAppNotificationContent(var43, var41.getCurrentStatus().getTitle(), "Just now", var2, 384, 0);
               }

               var2.endReplaceGroup();
            }
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var50 = var2.endRestartGroup();
      if (var50 != null) {
         final io.intercom.android.sdk.models.Conversation recoveredCapture_1106_0 = var0;
         final android.content.Context recoveredCapture_1106_1 = var1;
         final int recoveredCapture_1106_2 = var3;
         var50.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1106_0, recoveredArgument_1106_1) -> ChatFullNotificationKt.ChatFullContent$lambda$14(recoveredCapture_1106_0, recoveredCapture_1106_1, recoveredCapture_1106_2, (androidx.compose.runtime.Composer)recoveredArgument_1106_0, ((java.lang.Integer)recoveredArgument_1106_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotificationPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1120680649);
      ComposerKt.sourceInformation(var0, "C(ChatFullNotificationPreview)255@9389L1516:ChatFullNotification.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1120680649, var1, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotificationPreview (ChatFullNotification.kt:254)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChatFullNotificationKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1111_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1111_0, recoveredArgument_1111_1) -> ChatFullNotificationKt.ChatFullNotificationPreview$lambda$15(recoveredCapture_1111_0, (androidx.compose.runtime.Composer)recoveredArgument_1111_0, ((java.lang.Integer)recoveredArgument_1111_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotificationShortMessagePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(735919252);
      ComposerKt.sourceInformation(var0, "C(ChatFullNotificationShortMessagePreview)291@11000L697:ChatFullNotification.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               735919252, var1, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotificationShortMessagePreview (ChatFullNotification.kt:290)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChatFullNotificationKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1112_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1112_0, recoveredArgument_1112_1) -> ChatFullNotificationKt.ChatFullNotificationShortMessagePreview$lambda$16(recoveredCapture_1112_0, (androidx.compose.runtime.Composer)recoveredArgument_1112_0, ((java.lang.Integer)recoveredArgument_1112_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotificationBotPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1053171888);
      ComposerKt.sourceInformation(var0, "C(ChatFullNotificationBotPreview)315@11783L730:ChatFullNotification.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1053171888, var1, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotificationBotPreview (ChatFullNotification.kt:314)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChatFullNotificationKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1110_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1110_0, recoveredArgument_1110_1) -> ChatFullNotificationKt.ChatFullNotificationBotPreview$lambda$17(recoveredCapture_1110_0, (androidx.compose.runtime.Composer)recoveredArgument_1110_0, ((java.lang.Integer)recoveredArgument_1110_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotificationTicketPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(202840309);
      ComposerKt.sourceInformation(var0, "C(ChatFullNotificationTicketPreview)339@12602L899:ChatFullNotification.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               202840309, var1, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotificationTicketPreview (ChatFullNotification.kt:338)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChatFullNotificationKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1113_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1113_0, recoveredArgument_1113_1) -> ChatFullNotificationKt.ChatFullNotificationTicketPreview$lambda$18(recoveredCapture_1113_0, (androidx.compose.runtime.Composer)recoveredArgument_1113_0, ((java.lang.Integer)recoveredArgument_1113_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ChatFullNotificationAssignedTicketPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-671495709);
      ComposerKt.sourceInformation(var0, "C(ChatFullNotificationAssignedTicketPreview)367@13598L1023:ChatFullNotification.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -671495709, var1, -1, "io.intercom.android.sdk.m5.notification.ChatFullNotificationAssignedTicketPreview (ChatFullNotification.kt:366)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ChatFullNotificationKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1109_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1109_0, recoveredArgument_1109_1) -> ChatFullNotificationKt.ChatFullNotificationAssignedTicketPreview$lambda$19(recoveredCapture_1109_0, (androidx.compose.runtime.Composer)recoveredArgument_1109_0, ((java.lang.Integer)recoveredArgument_1109_1).intValue())));
      }
   }

   private static final boolean ChatFullNotification$lambda$2(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void ChatFullNotification$lambda$3(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit ChatFullNotification$lambda$8$lambda$5$lambda$4(MutableState var0, boolean var1) {
      ChatFullNotification$lambda$3(var0, var1);
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotification$lambda$9(Conversation var0, AppConfig var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      ChatFullNotification(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit detectOverflow$lambda$11$lambda$10(Placeable var0, PlacementScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$layout");
      PlacementScope.place$default(var1, var0, 0, 0, 0.0F, 4, null);
      return Unit.INSTANCE;
   }

   private static final MeasureResult detectOverflow$lambda$11(Function1 var0, MeasureScope var1, Measurable var2, Constraints var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$layout");
      Intrinsics.checkNotNullParameter(var2, "measurable");
      Placeable var4 = var2.recovered$ffe562697a83387a(
         Constraints.recovered$76ae2c951e1c6e8a(var3.recovered$45f9de7b85e0e0b5(), 0, 0, 0, Integer.MAX_VALUE, 7, null)
      );
      var0.invoke(var4.getHeight() > Constraints.recovered$d4a6310cd710783c(var3.recovered$45f9de7b85e0e0b5()));
      int var5 = RangesKt.coerceAtMost(var4.getHeight(), Constraints.recovered$d4a6310cd710783c(var3.recovered$45f9de7b85e0e0b5()));
      final androidx.compose.ui.layout.Placeable recoveredExpressionCapture_1115_0 = var4;
      return MeasureScope.layout$default(var1, var4.getWidth(), var5, null, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1115_0) -> io.intercom.android.sdk.m5.notification.ChatFullNotificationKt.detectOverflow$lambda$11$lambda$10(recoveredExpressionCapture_1115_0, (androidx.compose.ui.layout.Placeable.PlacementScope) ((androidx.compose.ui.layout.Placeable.PlacementScope)recoveredExpressionArgument_1115_0))), 4, null);
   }

   private static final Unit ChatFullContent$lambda$14(Conversation var0, Context var1, int var2, Composer var3, int var4) {
      ChatFullContent(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotificationPreview$lambda$15(int var0, Composer var1, int var2) {
      ChatFullNotificationPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotificationShortMessagePreview$lambda$16(int var0, Composer var1, int var2) {
      ChatFullNotificationShortMessagePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotificationBotPreview$lambda$17(int var0, Composer var1, int var2) {
      ChatFullNotificationBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotificationTicketPreview$lambda$18(int var0, Composer var1, int var2) {
      ChatFullNotificationTicketPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ChatFullNotificationAssignedTicketPreview$lambda$19(int var0, Composer var1, int var2) {
      ChatFullNotificationAssignedTicketPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final AppConfig access$getPreviewAppConfig$p() {
      return previewAppConfig;
   }
}
