package io.intercom.android.sdk.survey.block;

import android.content.Context;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a'\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a+\u0010\t\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0001¢\u0006\u0004\b\u000e\u0010\u000f\u001a\u001f\u0010\u0010\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0014",
   d2 = {
         "AttachmentBlock",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "blockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "isAdmin",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/BlockRenderData;ZLandroidx/compose/runtime/Composer;II)V",
         "TextAttachmentBlock",
         "blockAttachment",
         "Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;",
         "tintColor",
         "Landroidx/compose/ui/graphics/Color;",
         "TextAttachmentBlock-FNF3uiM",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;JLandroidx/compose/runtime/Composer;II)V",
         "VideoAttachmentBlock",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;Landroidx/compose/runtime/Composer;II)V",
         "AttachmentBlockPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAttachmentBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AttachmentBlock.kt\nio/intercom/android/sdk/survey/block/AttachmentBlockKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,104:1\n118#2:105\n118#2:150\n87#3:106\n85#3,8:107\n94#3:142\n81#4,6:115\n88#4,6:130\n96#4:141\n81#4,6:157\n88#4,6:172\n96#4:181\n402#5,9:121\n411#5:136\n412#5,2:139\n402#5,9:163\n411#5,3:178\n1869#6,2:137\n75#7:143\n1047#8,6:144\n99#9,6:151\n106#9:182\n*S KotlinDebug\n*F\n+ 1 AttachmentBlock.kt\nio/intercom/android/sdk/survey/block/AttachmentBlockKt\n*L\n30#1:105\n61#1:150\n30#1:106\n30#1:107,8\n30#1:142\n30#1:115,6\n30#1:130,6\n30#1:141\n54#1:157,6\n54#1:172,6\n54#1:181\n30#1:121,9\n30#1:136\n30#1:139,2\n54#1:163,9\n54#1:178,3\n35#1:137,2\n53#1:143\n56#1:144,6\n54#1:151,6\n54#1:182\n*E\n"
)
public final class AttachmentBlockKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AttachmentBlock(@Nullable Modifier var0, @NotNull BlockRenderData var1, boolean var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "blockRenderData");
      Composer var44 = var3.startRestartGroup(-1719159681);
      ComposerKt.sourceInformation(var44, "C(AttachmentBlock)P(2)29@1280L732:AttachmentBlock.kt#jxhu3x");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var44.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var44.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var44.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var44.getSkipping()) {
         var44.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1719159681, var6, -1, "io.intercom.android.sdk.survey.block.AttachmentBlock (AttachmentBlock.kt:28)");
         }

         int var7 = 8;
         int var8 = 0;
         Vertical var45 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var7));
         int var9 = 48 | 14 & var6;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var44, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var46 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var45, var46, var44, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var0;
         MeasurePolicy var13 = var11;
         Composer var14 = var44;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1103337202, "C:AttachmentBlock.kt#jxhu3x");
         Color var10000 = var1.getTextStyle().recovered$1b39cf3894296c39();
         if (var10000 == null) {
            var10000 = var1.recovered$1b39cf3894296c39();
         }

         Color var33 = var10000;
         var30.startReplaceGroup(-1626976078);
         ComposerKt.sourceInformation(var30, "33@1504L6");
         long var34 = var33 == null
            ? IntercomTheme.INSTANCE.getColors(var30, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a()
            : var33.recovered$45f9de7b85e0e0b5();
         var30.endReplaceGroup();
         long var36 = var34;
         var30.startReplaceGroup(-1626969984);
         ComposerKt.sourceInformation(var30, "");
         List var48 = var1.getBlock().getAttachments();
         Intrinsics.checkNotNullExpressionValue(var48, "getAttachments(...)");
         Iterable var47 = var48;
         int var38 = 0;

         for (Object var40 : var47) {
            BlockAttachment var41 = (BlockAttachment)var40;
            int var42 = 0;
            if (ContentTypeExtensionKt.isVideo(var41.getContentType())) {
               var30.startReplaceGroup(1321276699);
               ComposerKt.sourceInformation(var30, "36@1718L6,36@1636L96");
               Intrinsics.checkNotNull(var41);
               BlockAttachment var43 = var41;
               VideoAttachmentBlock(
                  ClipKt.clip(Modifier.Companion, IntercomTheme.INSTANCE.getShapes(var30, IntercomTheme.$stable).getSmall()), var43, var30, 0, 0
               );
               var30.endReplaceGroup();
            } else if (ContentTypeExtensionKt.isPdf(var41.getContentType())) {
               var30.startReplaceGroup(1321436969);
               ComposerKt.sourceInformation(var30, "38@1798L82");
               Intrinsics.checkNotNull(var41);
               PdfAttachmentBlockKt.recovered$913feb9b639e321a(var41, var2, null, var36, var30, 112 & var6 >> 3, 4);
               var30.endReplaceGroup();
            } else {
               var30.startReplaceGroup(1321555451);
               ComposerKt.sourceInformation(var30, "40@1918L64");
               Intrinsics.checkNotNull(var41);
               recovered$fe2716e960239e24(null, var41, var36, var30, 0, 1);
               var30.endReplaceGroup();
            }
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var44);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var49 = var44.endRestartGroup();
      if (var49 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1218_0 = var0;
         final io.intercom.android.sdk.survey.block.BlockRenderData recoveredExpressionCapture_1218_1 = var1;
         final boolean recoveredExpressionCapture_1218_2 = var2;
         final int recoveredExpressionCapture_1218_3 = var4;
         final int recoveredExpressionCapture_1218_4 = var5;
         var49.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1218_0, recoveredExpressionArgument_1218_1) -> io.intercom.android.sdk.survey.block.AttachmentBlockKt.AttachmentBlock$lambda$2(recoveredExpressionCapture_1218_0, recoveredExpressionCapture_1218_1, recoveredExpressionCapture_1218_2, recoveredExpressionCapture_1218_3, recoveredExpressionCapture_1218_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1218_0), ((java.lang.Integer)recoveredExpressionArgument_1218_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$fe2716e960239e24(
      @Nullable Modifier var0, @NotNull BlockAttachment var1, long var2, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "blockAttachment");
      Composer var42 = var4.startRestartGroup(-1146554998);
      ComposerKt.sourceInformation(
         var42, "C(TextAttachmentBlock)P(1!,2:c#ui.graphics.Color)50@2172L6,52@2228L7,55@2306L102,53@2240L692:AttachmentBlock.kt#jxhu3x"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var42.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var42.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var42.changed(var2) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var42.getSkipping()) {
         var42.skipToGroupEnd();
      } else {
         var42.startDefaults();
         if ((var5 & 1) != 0 && !var42.getDefaultsInvalid()) {
            var42.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getColors(var42, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var7 &= -897;
            }
         }

         var42.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1146554998, var7, -1, "io.intercom.android.sdk.survey.block.TextAttachmentBlock (AttachmentBlock.kt:51)");
         }

         CompositionLocal var9 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var10 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var42, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var12 = var42.consume(var9);
         ComposerKt.sourceInformationMarkerEnd(var42);
         Context var8 = (Context)var12;
         Modifier var10000 = var0;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var42.startReplaceGroup(1473534466);
         ComposerKt.sourceInformation(var42, "CC(remember):AttachmentBlock.kt#9igjgp");
         Composer var46 = var42;
         boolean var48 = (var7 & 112) == 32 | var42.changedInstance(var8);
         int var13 = 0;
         Object var14 = var46.rememberedValue();
         int var15 = 0;
         Object var10005;
         if (!var48 && var14 != Composer.Companion.getEmpty()) {
            var10005 = var14;
         } else {
            Object var40 = null;
            Object var39 = null;
            Object var38 = null;
            boolean var37 = false;
            Modifier var36 = var0;
            int var16 = 0;
            final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1221_0 = var1;
            final android.content.Context recoveredExpressionCapture_1221_1 = var8;
            Function0 var41 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.block.AttachmentBlockKt.TextAttachmentBlock_FNF3uiM$lambda$4$lambda$3(recoveredExpressionCapture_1221_0, recoveredExpressionCapture_1221_1));
            var10000 = var36;
            var10001 = var37;
            var10002 = var38;
            var10003 = var39;
            var10004 = var40;
            Object var17 = var41;
            var46.updateRememberedValue(var17);
            var10005 = var17;
         }

         Function0 var44 = (Function0)var10005;
         var42.endReplaceGroup();
         Modifier var43 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var44, 15, null
         );
         androidx.compose.ui.Alignment.Vertical var45 = Alignment.Companion.getCenterVertically();
         int var49 = 4;
         int var51 = 0;
         HorizontalOrVertical var47 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var49));
         short var50 = 432;
         int var52 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var42, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var53 = RowKt.rowMeasurePolicy(var47, var45, var42, 14 & var50 >> 3 | 112 & var50 >> 3);
         Modifier var54 = var43;
         MeasurePolicy var55 = var53;
         Composer var56 = var42;
         int var18 = 112 & var50 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var56, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var56, 0));
         CompositionLocalMap var21 = var56.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var56, var54);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var56;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var55, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var32 = 6 | 112 & var50 >> 6;
         Composer var33 = var30;
         RowScope var34 = RowScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 470526995, "C63@2581L55,62@2553L176,70@2850L10,67@2738L188:AttachmentBlock.kt#jxhu3x");
         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_ic_attachment, var33, 0),
            "Attachment Icon",
            null,
            var2,
            var33,
            48 | Painter.$stable | 7168 & var7 << 3,
            4
         );
         TextKt.recovered$5b114ab32a4f4190(
            var1.getName(),
            null,
            var2,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType04(),
               0L,
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
               TextDecoration.Companion.getUnderline(),
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
               16773119,
               null
            ),
            var33,
            896 & var7,
            0,
            131066
         );
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var56);
         ComposerKt.sourceInformationMarkerEnd(var42);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var57 = var42.endRestartGroup();
      if (var57 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1222_0 = var0;
         final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1222_1 = var1;
         final long recoveredExpressionCapture_1222_2 = var2;
         final int recoveredExpressionCapture_1222_3 = var5;
         final int recoveredExpressionCapture_1222_4 = var6;
         var57.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1222_0, recoveredExpressionArgument_1222_1) -> io.intercom.android.sdk.survey.block.AttachmentBlockKt.TextAttachmentBlock_FNF3uiM$lambda$6(recoveredExpressionCapture_1222_0, recoveredExpressionCapture_1222_1, recoveredExpressionCapture_1222_2, recoveredExpressionCapture_1222_3, recoveredExpressionCapture_1222_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1222_0), ((java.lang.Integer)recoveredExpressionArgument_1222_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void VideoAttachmentBlock(@Nullable Modifier var0, @NotNull BlockAttachment var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "blockAttachment");
      Composer var6 = var2.startRestartGroup(-745319067);
      ComposerKt.sourceInformation(var6, "C(VideoAttachmentBlock)P(1)77@3053L119:AttachmentBlock.kt#jxhu3x");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-745319067, var5, -1, "io.intercom.android.sdk.survey.block.VideoAttachmentBlock (AttachmentBlock.kt:76)");
         }

         VideoFileBlockKt.VideoFileBlock(var0, var1.getUrl(), null, var6, 384 | 14 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var6.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1220_0 = var0;
         final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1220_1 = var1;
         final int recoveredExpressionCapture_1220_2 = var3;
         final int recoveredExpressionCapture_1220_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1220_0, recoveredExpressionArgument_1220_1) -> io.intercom.android.sdk.survey.block.AttachmentBlockKt.VideoAttachmentBlock$lambda$7(recoveredExpressionCapture_1220_0, recoveredExpressionCapture_1220_1, recoveredExpressionCapture_1220_2, recoveredExpressionCapture_1220_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1220_0), ((java.lang.Integer)recoveredExpressionArgument_1220_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AttachmentBlockPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-550090117);
      ComposerKt.sourceInformation(var2, "C(AttachmentBlockPreview)87@3249L431:AttachmentBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-550090117, var1, -1, "io.intercom.android.sdk.survey.block.AttachmentBlockPreview (AttachmentBlock.kt:86)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$AttachmentBlockKt.INSTANCE.recovered$eac478aa65045e5d(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1219_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1219_0, recoveredExpressionArgument_1219_1) -> io.intercom.android.sdk.survey.block.AttachmentBlockKt.AttachmentBlockPreview$lambda$8(recoveredExpressionCapture_1219_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1219_0), ((java.lang.Integer)recoveredExpressionArgument_1219_1).intValue())));
      }
   }

   private static final Unit AttachmentBlock$lambda$2(Modifier var0, BlockRenderData var1, boolean var2, int var3, int var4, Composer var5, int var6) {
      AttachmentBlock(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit TextAttachmentBlock_FNF3uiM$lambda$4$lambda$3(BlockAttachment var0, Context var1) {
      LinkOpener.handleUrl(var0.getUrl(), var1, Injector.get().getApi());
      return Unit.INSTANCE;
   }

   private static final Unit TextAttachmentBlock_FNF3uiM$lambda$6(Modifier var0, BlockAttachment var1, long var2, int var4, int var5, Composer var6, int var7) {
      recovered$fe2716e960239e24(var0, var1, var2, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit VideoAttachmentBlock$lambda$7(Modifier var0, BlockAttachment var1, int var2, int var3, Composer var4, int var5) {
      VideoAttachmentBlock(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit AttachmentBlockPreview$lambda$8(int var0, Composer var1, int var2) {
      AttachmentBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
