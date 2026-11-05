package io.intercom.android.sdk.tickets;

import androidx.compose.animation.AnimationModifierKt;
import androidx.compose.animation.core.AnimateAsStateKt;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.AlphaKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Ticket$TicketAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$DateTimeAttribute;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$PrimitiveAttribute;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.ui.component.IntercomButton;
import io.intercom.android.sdk.ui.component.IntercomButtonKt;
import io.intercom.android.sdk.ui.component.IntercomButton.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\u001aA\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0016\b\u0002\u0010\u0006\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\u0017\u0010\f\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000f\"\u0014\u0010\u0012\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015²\u0006\n\u0010\u0016\u001a\u00020\u0017X\u008a\u008e\u0002²\u0006\n\u0010\u0018\u001a\u00020\u0019X\u008a\u008e\u0002²\u0006\n\u0010\u001a\u001a\u00020\u001bX\u008a\u008e\u0002",
   d2 = {
         "TicketDetailContent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "ticketDetailContentState",
         "Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;",
         "onConversationCTAClicked",
         "Lkotlin/Function1;",
         "",
         "showSubmissionCard",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;Lkotlin/jvm/functions/Function1;ZLandroidx/compose/runtime/Composer;II)V",
         "TicketSubmissionCard",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketSubmissionCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TicketPreview",
         "TicketPreviewSubmittedCard",
         "sampleTicketDetailState",
         "getSampleTicketDetailState",
         "()Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;",
         "intercom-sdk-base_release",
         "cardState",
         "Lio/intercom/android/sdk/tickets/CardState;",
         "submissionCardOffset",
         "Landroidx/compose/ui/unit/Dp;",
         "submissionCardAlpha",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nTicketDetailContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,372:1\n1047#2,6:373\n1047#2,6:379\n1047#2,3:385\n1050#2,3:389\n1047#2,6:392\n1047#2,6:398\n1047#2,6:506\n118#3:388\n118#3:436\n118#3:472\n118#3:505\n118#3:512\n118#3:521\n118#3:522\n118#3:551\n87#4:404\n84#4,9:405\n87#4:473\n84#4,9:474\n94#4:516\n94#4:520\n87#4,6:523\n94#4:555\n81#5,6:414\n88#5,6:429\n81#5,6:446\n88#5,6:461\n96#5:470\n81#5,6:483\n88#5,6:498\n96#5:515\n96#5:519\n81#5,6:529\n88#5,6:544\n96#5:554\n402#6,9:420\n411#6:435\n402#6,9:452\n411#6,3:467\n402#6,9:489\n411#6:504\n412#6,2:513\n412#6,2:517\n402#6,9:535\n411#6:550\n412#6,2:552\n70#7:437\n68#7,8:438\n77#7:471\n85#8:556\n117#8,2:557\n85#8:559\n117#8,2:560\n85#8:562\n117#8,2:563\n*S KotlinDebug\n*F\n+ 1 TicketDetailContent.kt\nio/intercom/android/sdk/tickets/TicketDetailContentKt\n*L\n69#1:373,6\n72#1:379,6\n73#1:385,3\n73#1:389,3\n74#1:392,6\n78#1:398,6\n245#1:506,6\n73#1:388\n95#1:436\n229#1:472\n238#1:505\n247#1:512\n263#1:521\n265#1:522\n271#1:551\n86#1:404\n86#1:405,9\n226#1:473\n226#1:474,9\n226#1:516\n86#1:520\n262#1:523,6\n262#1:555\n86#1:414,6\n86#1:429,6\n91#1:446,6\n91#1:461,6\n91#1:470\n226#1:483,6\n226#1:498,6\n226#1:515\n86#1:519\n262#1:529,6\n262#1:544,6\n262#1:554\n86#1:420,9\n86#1:435\n91#1:452,9\n91#1:467,3\n226#1:489,9\n226#1:504\n226#1:513,2\n86#1:517,2\n262#1:535,9\n262#1:550\n262#1:552,2\n91#1:437\n91#1:438,8\n91#1:471\n72#1:556\n72#1:557,2\n73#1:559\n73#1:560,2\n74#1:562\n74#1:563,2\n*E\n"
)
public final class TicketDetailContentKt {
   @NotNull
   private static final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState sampleTicketDetailState;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailContent(
      @Nullable Modifier var0, @NotNull io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var1, @Nullable Function1 var2, boolean var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "ticketDetailContentState");
      Composer var82 = var4.startRestartGroup(-872031756);
      ComposerKt.sourceInformation(
         var82, "C(TicketDetailContent)P(!1,3)68@3288L2,71@3375L96,71@3358L113,72@3504L37,73@3573L31,88@4017L21,85@3924L7864:TicketDetailContent.kt#3ofz5f"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var82.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var82.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var82.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var82.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var82.getSkipping()) {
         var82.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var82.startReplaceGroup(1639440163);
            ComposerKt.sourceInformation(var82, "CC(remember):TicketDetailContent.kt#9igjgp");
            Composer var9 = var82;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var104 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1531_0) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketDetailContent$lambda$1$lambda$0((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1531_0)));
               var9.updateRememberedValue(var104);
               var10000 = var104;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var82.endReplaceGroup();
            var2 = var8;
         }

         if ((var6 & 8) != 0) {
            var3 = false;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-872031756, var7, -1, "io.intercom.android.sdk.tickets.TicketDetailContent (TicketDetailContent.kt:70)");
         }

         Object[] var165 = new Object[0];
         var82.startReplaceGroup(1639443041);
         ComposerKt.sourceInformation(var82, "CC(remember):TicketDetailContent.kt#9igjgp");
         Composer var86 = var82;
         boolean var89 = (var7 & 7168) == 2048;
         int var94 = 0;
         Object var99 = var86.rememberedValue();
         int var105 = 0;
         Object var175;
         if (!var89 && var99 != Composer.Companion.getEmpty()) {
            var175 = var99;
         } else {
            Object[] var81 = var165;
            int var15 = 0;
            final boolean recoveredExpressionCapture_1533_0 = var3;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketDetailContent$lambda$3$lambda$2(recoveredExpressionCapture_1533_0));
            var165 = var81;
            Object var16 = var10001;
            var86.updateRememberedValue(var16);
            var175 = var16;
         }

         Function0 var84 = (Function0)var175;
         var82.endReplaceGroup();
         MutableState var83 = (MutableState)RememberSaveableKt.rememberSaveable(var165, var84, var82, 0);
         var82.startReplaceGroup(1639447110);
         ComposerKt.sourceInformation(var82, "CC(remember):TicketDetailContent.kt#9igjgp");
         Composer var90 = var82;
         boolean var95 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var90.rememberedValue();
         int var110/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var166;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var114 = 0;
            int var17 = -56;
            int var18 = 0;
            Object var115 = SnapshotStateKt.mutableStateOf$default(Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var17)), null, 2, null);
            var90.updateRememberedValue(var115);
            var166 = var115;
         } else {
            var166 = it$ivx;
         }

         MutableState var87 = (MutableState)var166;
         var82.endReplaceGroup();
         MutableState var85 = var87;
         var82.startReplaceGroup(1639449312);
         ComposerKt.sourceInformation(var82, "CC(remember):TicketDetailContent.kt#9igjgp");
         Composer var96 = var82;
         boolean var101 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var96.rememberedValue();
         int var116/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var167;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var119 = 0;
            Object var120 = SnapshotStateKt.mutableStateOf$default(0.0F, null, 2, null);
            var96.updateRememberedValue(var120);
            var167 = var120;
         } else {
            var167 = it$ivxx;
         }

         MutableState var91 = (MutableState)var167;
         var82.endReplaceGroup();
         MutableState var88 = var91;
         var82.startReplaceGroup(1639450743);
         ComposerKt.sourceInformation(var82, "77@3723L179,75@3662L251");
         if (TicketDetailContent$lambda$4(var83) == CardState.SubmissionCard) {
            Object var168 = null;
            var82.startReplaceGroup(1639454260);
            ComposerKt.sourceInformation(var82, "CC(remember):TicketDetailContent.kt#9igjgp");
            Composer var97 = var82;
            boolean var102 = var82.changed(var83);
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var97.rememberedValue();
            int var117/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var177;
            if (!var102 && it$ivxxx != Composer.Companion.getEmpty()) {
               var177 = it$ivxxx;
            } else {
               Object var164 = null;
               int var121 = 0;
               Function2 var176 = new TicketDetailContentKt$TicketDetailContent$2$1(var85, var88, var83, null);
               var168 = var164;
               Object var123 = var176;
               var97.updateRememberedValue(var123);
               var177 = var123;
            }

            Function2 var92 = (Function2)var177;
            var82.endReplaceGroup();
            EffectsKt.LaunchedEffect(var168, var92, var82, 6);
         }

         var82.endReplaceGroup();
         Modifier var93 = ScrollKt.verticalScroll$default(
            SizeKt.fillMaxHeight$default(var0, 0.0F, 1, null), ScrollKt.rememberScrollState(0, var82, 0, 1), false, null, false, 14, null
         );
         byte var109 = 0;
         int var113 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var82, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var98 = Arrangement.INSTANCE.getTop();
         Horizontal var103 = Alignment.Companion.getStart();
         MeasurePolicy var118 = ColumnKt.columnMeasurePolicy(var98, var103, var82, 14 & var109 >> 3 | 112 & var109 >> 3);
         Modifier var122 = var93;
         MeasurePolicy var124 = var118;
         Composer var19 = var82;
         int var20 = 112 & var109 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var122);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var124, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var34 = 6 | 112 & var109 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, -189839434, "C92@4136L6,90@4057L1606,128@5702L6,128@5755L4788,128@5672L4871:TicketDetailContent.kt#3ofz5f"
         );
         Modifier var169 = SizeKt.fillMaxSize$default(
            BackgroundKt.recovered$42a3174180b3fb7a(
               Modifier.Companion, IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
            ),
            0.0F,
            1,
            null
         );
         int var38 = 194;
         int var39 = 0;
         Modifier var125 = AnimationModifierKt.animateContentSize$default(
            SizeKt.recovered$1c935d354e9159f0(var169, Dp.recovered$95b3d103663bf060(var38), 0.0F, 2, null),
            AnimationSpecKt.tween$default(0, 0, null, 7, null),
            null,
            2,
            null
         );
         Alignment var128 = Alignment.Companion.getCenter();
         byte var40 = 48;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var42 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var128, var42);
         Modifier var44 = var125;
         MeasurePolicy var45 = measurePolicy$ivx;
         Composer var46 = var35;
         int var47 = 112 & var40 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var44);
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
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var45, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var58 = 14 & var54 >> 6;
         Composer $composer$ivx = var53;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var61 = 6 | 112 & var40 >> 6;
         Composer var62 = $composer$ivx;
         BoxScope var63 = BoxScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var62, -1367883561, "C102@4560L195,98@4360L439,111@4922L419,121@5428L181,108@4812L841:TicketDetailContent.kt#3ofz5f"
         );
         TicketTimelineCardKt.TicketTimelineCard(
            var1.getTicketTimelineCardState(),
            AlphaKt.alpha(
               Modifier.Companion,
               ((Number)AnimateAsStateKt.animateFloatAsState(
                        TicketDetailContent$lambda$4(var83) == CardState.TimelineCard ? 1.0F : 0.0F,
                        AnimationSpecKt.spring$default(0.0F, 0.0F, null, 7, null),
                        0.0F,
                        null,
                        null,
                        var62,
                        48,
                        28
                     )
                     .getValue())
                  .floatValue()
            ),
            var62,
            0,
            0
         );
         TicketSubmissionCard(
            OffsetKt.recovered$c9c0be2809f7995f(
               AlphaKt.alpha(
                  Modifier.Companion,
                  ((Number)AnimateAsStateKt.animateFloatAsState(
                           TicketDetailContent$lambda$4(var83) == CardState.SubmissionCard ? TicketDetailContent$lambda$10(var88) : 0.0F,
                           TicketDetailContent$lambda$4(var83) == CardState.SubmissionCard
                              ? AnimationSpecKt.tween$default(1000, 0, null, 6, null)
                              : AnimationSpecKt.spring$default(0.0F, 0.0F, null, 7, null),
                           0.0F,
                           null,
                           null,
                           var62,
                           0,
                           28
                        )
                        .getValue())
                     .floatValue()
               ),
               0.0F,
               ((Dp)AnimateAsStateKt.recovered$b63f920e95265746(
                        TicketDetailContent$lambda$7(var85), AnimationSpecKt.tween$default(1000, 0, null, 6, null), null, null, var62, 48, 12
                     )
                     .getValue())
                  .recovered$3537cef520440dc0(),
               1,
               null
            ),
            var62,
            0,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var35);
         long var65 = IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null),
            null,
            var65,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(925724611, true, new TicketDetailContentKt$TicketDetailContent$3$2(var1), var35, 54),
            var35,
            12582918,
            122
         );
         var35.startReplaceGroup(1933740142);
         ComposerKt.sourceInformation(var35, "224@10631L38,225@10682L1090");
         if (var1.getConversationButtonState().getShowButton()) {
            SpacerKt.Spacer(ColumnScope.weight$default(var36, Modifier.Companion, 1.0F, false, 2, null), var35, 0);
            Modifier var170 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            Modifier var127 = PaddingKt.recovered$f3b9f2e94c07c806(var170, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 1, null);
            Horizontal var133 = Alignment.Companion.getCenterHorizontally();
            short var131 = 390;
            int $i$f$Columnx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var35, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
            MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(verticalArrangement$ivx, var133, var35, 14 & var131 >> 3 | 112 & var131 >> 3);
            Modifier var135 = var127;
            MeasurePolicy var136 = measurePolicy$ivxx;
            Composer var137 = var35;
            int var138 = 112 & var131 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var137, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var137, 0));
            CompositionLocalMap localMap$iv$ivxx = var137.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var137, var135);
            Function0 var143 = ComposeUiNode.Companion.getConstructor();
            Composer var144 = var137;
            int var145 = 6 | 896 & var138 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var144, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var144.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var144.startReusableNode();
            if (var144.getInserting()) {
               var144.createNode(var143);
            } else {
               var144.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var144);
            int var148/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var136, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var149 = 14 & var145 >> 6;
            Composer $composer$ivxx = var144;
            int var151/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var152 = 6 | 112 & var131 >> 6;
            Composer var153 = $composer$ivxx;
            ColumnScope var154 = ColumnScopeInstance.INSTANCE;
            int var155 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var153,
               -1361399756,
               "C234@11049L55,235@11148L10,235@11199L6,231@10910L326,237@11253L40,241@11453L14,242@11513L9,244@11612L69,240@11394L306,246@11717L41:TicketDetailContent.kt#3ofz5f"
            );
            Modifier var67 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int var68 = TextAlign.Companion.recovered$d707b9dd64876179();
            String var69 = StringResources_androidKt.stringResource(string.intercom_tickets_cta_text, var153, 0);
            TextStyle var70 = TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var153, IntercomTheme.$stable).getType04Point5(),
               IntercomTheme.INSTANCE.getColors(var153, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
               0L,
               null,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777214,
               null
            );
            TextKt.recovered$5b114ab32a4f4190(
               var69,
               var67,
               0L,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var68),
               0L,
               0,
               false,
               0,
               0,
               null,
               var70,
               var153,
               48,
               0,
               130044
            );
            Modifier var171 = Modifier.Companion;
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var171, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var153, 6);
            ConversationButtonState var157 = var1.getConversationButtonState();
            Object var172 = null;
            Style var178 = IntercomButton.INSTANCE.recovered$bae4000e51c7826f(0L, 0L, null, var153, IntercomButton.$stable << 9, 7);
            String var10002 = var157.getText().getText(var153, StringProvider.$stable);
            Integer var10003 = var157.getIconRes();
            var153.startReplaceGroup(233200228);
            ComposerKt.sourceInformation(var153, "CC(remember):TicketDetailContent.kt#9igjgp");
            Composer var161 = var153;
            boolean var163 = (var7 & 896) == 256 | var82.changedInstance(var1);
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var161.rememberedValue();
            int var73/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var10004;
            if (!var163 && it$ivxxx != Composer.Companion.getEmpty()) {
               var10004 = it$ivxxx;
            } else {
               Integer var74 = var10003;
               String var75 = var10002;
               Style var76 = var178;
               Object var77 = null;
               int var78 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1530_0 = var2;
               final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState recoveredExpressionCapture_1530_1 = var1;
               Function0 var79 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketDetailContent$lambda$17$lambda$16$lambda$15$lambda$14(recoveredExpressionCapture_1530_0, recoveredExpressionCapture_1530_1));
               var172 = var77;
               var178 = var76;
               var10002 = var75;
               var10003 = var74;
               Object var80 = var79;
               var161.updateRememberedValue(var80);
               var10004 = var80;
            }

            Function0 var159 = (Function0)var10004;
            var153.endReplaceGroup();
            IntercomButtonKt.IntercomButton((Modifier)var172, var178, var10002, var10003, var159, var153, Style.$stable << 3, 1);
            Modifier var173 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var173, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var153, 6);
            ComposerKt.sourceInformationMarkerEnd(var153);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var144.endNode();
            ComposerKt.sourceInformationMarkerEnd(var144);
            ComposerKt.sourceInformationMarkerEnd(var137);
            ComposerKt.sourceInformationMarkerEnd(var35);
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var82);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var174 = var82.endRestartGroup();
      if (var174 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1532_0 = var0;
         final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState recoveredExpressionCapture_1532_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1532_2 = var2;
         final boolean recoveredExpressionCapture_1532_3 = var3;
         final int recoveredExpressionCapture_1532_4 = var5;
         final int recoveredExpressionCapture_1532_5 = var6;
         var174.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1532_0, recoveredExpressionArgument_1532_1) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketDetailContent$lambda$18(recoveredExpressionCapture_1532_0, recoveredExpressionCapture_1532_1, recoveredExpressionCapture_1532_2, recoveredExpressionCapture_1532_3, recoveredExpressionCapture_1532_4, recoveredExpressionCapture_1532_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1532_0), ((java.lang.Integer)recoveredExpressionArgument_1532_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TicketSubmissionCard(Modifier var0, Composer var1, int var2, int var3) {
      Composer var42 = var1.startRestartGroup(-2022209692);
      ComposerKt.sourceInformation(var42, "C(TicketSubmissionCard)261@11953L948:TicketDetailContent.kt#3ofz5f");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var42.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var42.getSkipping()) {
         var42.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2022209692, var4, -1, "io.intercom.android.sdk.tickets.TicketSubmissionCard (TicketDetailContent.kt:260)");
         }

         int var6 = 16;
         int var7 = 0;
         HorizontalOrVertical var5 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var6));
         Horizontal var43 = Alignment.Companion.getCenterHorizontally();
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var44 = PaddingKt.recovered$eb6a744cd98cdbca(var0, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         short var45 = 432;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var42, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var10 = ColumnKt.columnMeasurePolicy(var5, var43, var42, 14 & var45 >> 3 | 112 & var45 >> 3);
         Modifier var11 = var44;
         MeasurePolicy var12 = var10;
         Composer var13 = var42;
         int var14 = 112 & var45 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var28 = 6 | 112 & var45 >> 6;
         Composer var29 = var26;
         ColumnScope var30 = ColumnScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var29,
            1552633678,
            "C267@12168L51,266@12140L212,273@12386L74,275@12538L10,276@12591L6,272@12361L260,279@12655L79,281@12812L10,282@12865L6,278@12630L265:TicketDetailContent.kt#3ofz5f"
         );
         Painter var32 = PainterResources_androidKt.painterResource(drawable.intercom_submitted, var29, 0);
         long var33 = ColorKt.Color(4279072050L);
         Modifier var10000 = Modifier.Companion;
         int $this$dp$ivxx = 48;
         int $i$f$getDpxx = 0;
         Modifier var37 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         IconKt.recovered$e00adffb7fe45b02(var32, null, var37, var33, var29, 3504 | Painter.$stable, 0);
         String var47 = StringResources_androidKt.stringResource(string.intercom_tickets_created_confirmation_header, var29, 0);
         int var38 = TextAlign.Companion.recovered$d707b9dd64876179();
         TextStyle var39 = IntercomTheme.INSTANCE.getTypography(var29, IntercomTheme.$stable).getType04();
         long var40 = IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var47,
            null,
            var40,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var38),
            0L,
            0,
            false,
            0,
            0,
            null,
            var39,
            var29,
            0,
            0,
            130042
         );
         String var48 = StringResources_androidKt.stringResource(string.intercom_tickets_submitted_confirmation_paragraph, var29, 0);
         int var49 = TextAlign.Companion.recovered$d707b9dd64876179();
         TextStyle var50 = IntercomTheme.INSTANCE.getTypography(var29, IntercomTheme.$stable).getType04();
         long var51 = IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var48,
            null,
            var51,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var49),
            0L,
            0,
            false,
            0,
            0,
            null,
            var50,
            var29,
            0,
            0,
            130042
         );
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var42);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var52 = var42.endRestartGroup();
      if (var52 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1536_0 = var0;
         final int recoveredExpressionCapture_1536_1 = var2;
         final int recoveredExpressionCapture_1536_2 = var3;
         var52.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1536_0, recoveredExpressionArgument_1536_1) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketSubmissionCard$lambda$20(recoveredExpressionCapture_1536_0, recoveredExpressionCapture_1536_1, recoveredExpressionCapture_1536_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1536_0), ((java.lang.Integer)recoveredExpressionArgument_1536_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketSubmissionCardPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-981393609);
      ComposerKt.sourceInformation(var2, "C(TicketSubmissionCardPreview)290@12984L84:TicketDetailContent.kt#3ofz5f");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-981393609, var1, -1, "io.intercom.android.sdk.tickets.TicketSubmissionCardPreview (TicketDetailContent.kt:289)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.tickets.ComposableSingletons$TicketDetailContentKt.INSTANCE.recovered$1400af5e34386102(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1537_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1537_0, recoveredExpressionArgument_1537_1) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketSubmissionCardPreview$lambda$21(recoveredExpressionCapture_1537_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1537_0), ((java.lang.Integer)recoveredExpressionArgument_1537_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1759013677);
      ComposerKt.sourceInformation(var2, "C(TicketPreview)300@13137L124:TicketDetailContent.kt#3ofz5f");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1759013677, var1, -1, "io.intercom.android.sdk.tickets.TicketPreview (TicketDetailContent.kt:299)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.tickets.ComposableSingletons$TicketDetailContentKt.INSTANCE.recovered$fd838d44303763da(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1534_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1534_0, recoveredExpressionArgument_1534_1) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketPreview$lambda$22(recoveredExpressionCapture_1534_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1534_0), ((java.lang.Integer)recoveredExpressionArgument_1534_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketPreviewSubmittedCard(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2122497154);
      ComposerKt.sourceInformation(var2, "C(TicketPreviewSubmittedCard)310@13343L163:TicketDetailContent.kt#3ofz5f");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2122497154, var1, -1, "io.intercom.android.sdk.tickets.TicketPreviewSubmittedCard (TicketDetailContent.kt:309)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.tickets.ComposableSingletons$TicketDetailContentKt.INSTANCE.recovered$54bdcf11cafa5012(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1535_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1535_0, recoveredExpressionArgument_1535_1) -> io.intercom.android.sdk.tickets.TicketDetailContentKt.TicketPreviewSubmittedCard$lambda$23(recoveredExpressionCapture_1535_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1535_0), ((java.lang.Integer)recoveredExpressionArgument_1535_1).intValue())));
      }
   }

   @NotNull
   public static final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState getSampleTicketDetailState() {
      return sampleTicketDetailState;
   }

   private static final Unit TicketDetailContent$lambda$1$lambda$0(String var0) {
      return Unit.INSTANCE;
   }

   private static final MutableState TicketDetailContent$lambda$3$lambda$2(boolean var0) {
      return SnapshotStateKt.mutableStateOf$default(var0 ? CardState.SubmissionCard : CardState.TimelineCard, null, 2, null);
   }

   private static final CardState TicketDetailContent$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (CardState)var1.getValue();
   }

   private static final void TicketDetailContent$lambda$5(MutableState var0, CardState var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final float TicketDetailContent$lambda$7(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Dp)var1.getValue()).recovered$3537cef520440dc0();
   }

   private static final void TicketDetailContent$lambda$8(MutableState var0, float var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = Dp.recovered$89bc2ea8129b5eb8(var1);
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final float TicketDetailContent$lambda$10(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final void TicketDetailContent$lambda$11(MutableState var0, float var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit TicketDetailContent$lambda$17$lambda$16$lambda$15$lambda$14(Function1 var0, io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var1) {
      var0.invoke(var1.getConversationId());
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailContent$lambda$18(
      Modifier var0, io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var1, Function1 var2, boolean var3, int var4, int var5, Composer var6, int var7
   ) {
      TicketDetailContent(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TicketSubmissionCard$lambda$20(Modifier var0, int var1, int var2, Composer var3, int var4) {
      TicketSubmissionCard(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit TicketSubmissionCardPreview$lambda$21(int var0, Composer var1, int var2) {
      TicketSubmissionCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketPreview$lambda$22(int var0, Composer var1, int var2) {
      TicketPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketPreviewSubmittedCard$lambda$23(int var0, Composer var1, int var2) {
      TicketPreviewSubmittedCard(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$TicketSubmissionCard(Modifier var0, Composer var1, int var2, int var3) {
      TicketSubmissionCard(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$TicketDetailContent$lambda$8(MutableState var0, float var1) {
      TicketDetailContent$lambda$8(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$TicketDetailContent$lambda$11(MutableState var0, float var1) {
      TicketDetailContent$lambda$11(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$TicketDetailContent$lambda$5(MutableState var0, CardState var1) {
      TicketDetailContent$lambda$5(var0, var1);
   }

   static {
      Avatar var10007 = Avatar.create("https://static.intercomassets.com/avatars/5355644/square_128/002Bizimply-Headshots-1641919551.jpeg", "");
      Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
      List var10005 = CollectionsKt.listOf(new AvatarWrapper(var10007, false, 2, null));
      long var10008 = Color.Companion.recovered$ff90a8751967f2f7();
      io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection[] var0 = new io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection[]{new io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection(true, false), new io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection(false, true), new io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection(false, false)};
      TicketTimelineCardState var10003 = new TicketTimelineCardState(
         var10005,
         "Hannah will pick this up soon",
         "\ud83d\udd51  Estimated to be resolved today at 4pm",
         var10008,
         CollectionsKt.listOf(var0),
         "Submitted",
         System.currentTimeMillis(),
         null
      );
      io.intercom.android.sdk.models.Ticket$TicketAttribute[] var1 = new io.intercom.android.sdk.models.Ticket$TicketAttribute[]{
         new io.intercom.android.sdk.models.Ticket$TicketAttribute$PrimitiveAttribute(
            null,
            null,
            "Description",
            false,
            null,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et, ut hendrerit et lacus, dictumst ridiculus morbi elementum.",
            27,
            null
         ),
         new io.intercom.android.sdk.models.Ticket$TicketAttribute$PrimitiveAttribute(null, null, "API Version", false, null, "1.2", 27, null),
         new io.intercom.android.sdk.models.Ticket$TicketAttribute$DateTimeAttribute(null, null, "When did the issue occur?", false, null, "1676555323", 27, null)
      };
      sampleTicketDetailState = new io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState(
         "API issue",
         var10003,
         CollectionsKt.listOf(var1),
         "test@gmail.com",
         "123",
         new ConversationButtonState(true, drawable.intercom_send_message_icon, new StringRes(string.intercom_send_us_a_message, null, 2, null))
      );
   }
}
