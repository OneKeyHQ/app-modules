package io.intercom.android.sdk.post;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.survey.ui.components.CircularAvatarComponentKt;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a;\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003¢\u0006\u0002\u0010\u000b\u001a3\u0010\f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u001c\u0010\r\u001a\u0018\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000e¢\u0006\u0002\b\u0010¢\u0006\u0002\b\u0011H\u0001¢\u0006\u0002\u0010\u0012¨\u0006\u0013",
   d2 = {
         "TopBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "title",
         "",
         "subTitle",
         "onCloseClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/models/Avatar;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "BottomBarContent",
         "content",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivityKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,370:1\n118#2:371\n118#2:372\n118#2:434\n118#2:435\n118#2:480\n118#2:481\n99#3,6:373\n99#3:401\n95#3,10:402\n106#3:475\n106#3:479\n99#3,6:482\n106#3:513\n81#4,6:379\n88#4,6:394\n81#4,6:412\n88#4,6:427\n81#4,6:446\n88#4,6:461\n96#4:470\n96#4:474\n96#4:478\n81#4,6:488\n88#4,6:503\n96#4:512\n402#5,9:385\n411#5:400\n402#5,9:418\n411#5:433\n402#5,9:452\n411#5,3:467\n412#5,2:472\n412#5,2:476\n402#5,9:494\n411#5,3:509\n87#6:436\n84#6,9:437\n94#6:471\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivityKt\n*L\n328#1:371\n329#1:372\n333#1:434\n334#1:435\n363#1:480\n364#1:481\n322#1:373,6\n332#1:401\n332#1:402,10\n332#1:475\n322#1:479\n357#1:482,6\n357#1:513\n322#1:379,6\n322#1:394,6\n332#1:412,6\n332#1:427,6\n334#1:446,6\n334#1:461,6\n334#1:470\n332#1:474\n322#1:478\n357#1:488,6\n357#1:503,6\n357#1:512\n322#1:385,9\n322#1:400\n332#1:418,9\n332#1:433\n334#1:452,9\n334#1:467,3\n332#1:472,2\n322#1:476,2\n357#1:494,9\n357#1:509,3\n334#1:436\n334#1:437,9\n334#1:471\n*E\n"
)
public final class IntercomPostActivityKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TopBar(Modifier var0, Avatar var1, String var2, String var3, Function0 var4, Composer var5, int var6) {
      var5 = var5.startRestartGroup(-554464404);
      ComposerKt.sourceInformation(var5, "C(TopBar)P(1!1,4,3)321@14396L1152:IntercomPostActivity.kt#hv1x2k");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 24576) == 0) {
         var7 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-554464404, var7, -1, "io.intercom.android.sdk.post.TopBar (IntercomPostActivity.kt:320)");
         }

         Vertical var8 = Alignment.Companion.getCenterVertically();
         Modifier var10000 = WindowInsetsPadding_androidKt.statusBarsPadding(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), Color.Companion.recovered$235b30c090a399c5(), null, 2, null
            )
         );
         int var10 = 56;
         int var11 = 0;
         var10000 = SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var10));
         var10 = 16;
         var11 = 0;
         Modifier var9 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var10), 0.0F, 2, null);
         HorizontalOrVertical var93 = Arrangement.INSTANCE.getSpaceBetween();
         short var95 = 432;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var13 = RowKt.rowMeasurePolicy(var93, var8, var5, 14 & var95 >> 3 | 112 & var95 >> 3);
         Modifier var14 = var9;
         MeasurePolicy var15 = var13;
         Composer var16 = var5;
         int var17 = 112 & var95 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var31 = 6 | 112 & var95 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -61368708, "C331@14726L505,341@15268L52,342@15355L46,340@15240L302:IntercomPostActivity.kt#hv1x2k");
         Vertical var35 = Alignment.Companion.getCenterVertically();
         int var36 = 384;
         int $i$f$Rowx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var38 = Modifier.Companion;
         Horizontal var39 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var39, var35, var32, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var41 = var38;
         MeasurePolicy var42 = measurePolicy$ivx;
         Composer var43 = var32;
         int var44 = 112 & var36 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var43, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var43, 0));
         CompositionLocalMap localMap$iv$ivx = var43.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var43, var41);
         Function0 var49 = ComposeUiNode.Companion.getConstructor();
         Composer var50 = var43;
         int var51 = 6 | 896 & var44 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var50.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var50.startReusableNode();
         if (var50.getInserting()) {
            var50.createNode(var49);
         } else {
            var50.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var50);
         int var54/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var42, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var55 = 14 & var51 >> 6;
         Composer $composer$ivx = var50;
         int var57/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var58 = 6 | 112 & var36 >> 6;
         Composer var59 = $composer$ivx;
         RowScope var60 = RowScopeInstance.INSTANCE;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var59, -1723658304, "C332@14792L76,333@14881L340:IntercomPostActivity.kt#hv1x2k");
         long var10001 = Color.Companion.recovered$95989221dad41c4d();
         int $this$dp$ivx = 32;
         int $i$f$getDpx = 0;
         CircularAvatarComponentKt.recovered$7baf454f09f9b402(var1, var10001, Dp.recovered$95b3d103663bf060($this$dp$ivx), var59, 432 | 14 & var7 >> 3, 0);
         var10000 = Modifier.Companion;
         $this$dp$ivx = 8;
         $i$f$getDpx = 0;
         Modifier var100 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 2, null);
         int $changed$ivx = 6;
         int var65 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var59, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Vertical var102 = Arrangement.INSTANCE.getTop();
         androidx.compose.ui.Alignment.Horizontal var66 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(var102, var66, var59, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3);
         Modifier var68 = var100;
         MeasurePolicy var69 = measurePolicy$ivxx;
         Composer var70 = var59;
         int var71 = 112 & $changed$ivx << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var70, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var70, 0));
         CompositionLocalMap localMap$iv$ivxx = var70.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var70, var68);
         Function0 var76 = ComposeUiNode.Companion.getConstructor();
         Composer var77 = var70;
         int var78 = 6 | 896 & var71 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var77, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var77.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var77.startReusableNode();
         if (var77.getInserting()) {
            var77.createNode(var76);
         } else {
            var77.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var77);
         int var81/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var69, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var82 = 14 & var78 >> 6;
         Composer $composer$ivxx = var77;
         int var84 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var85 = 6 | 112 & $changed$ivx >> 6;
         Composer var86 = $composer$ivxx;
         ColumnScope var87 = ColumnScopeInstance.INSTANCE;
         int var88 = 0;
         ComposerKt.sourceInformationMarkerStart(var86, -2095477487, "C334@15016L10,334@14954L86:IntercomPostActivity.kt#hv1x2k");
         TextKt.recovered$5b114ab32a4f4190(
            var2,
            null,
            Color.Companion.recovered$95989221dad41c4d(),
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
            IntercomTheme.INSTANCE.getTypography(var86, IntercomTheme.$stable).getType04Point5(),
            var86,
            384 | 14 & var7 >> 6,
            0,
            131066
         );
         var86.startReplaceGroup(-2145802837);
         ComposerKt.sourceInformation(var86, "336@15171L10,336@15106L83");
         if (!StringsKt.isBlank(var3)) {
            TextKt.recovered$5b114ab32a4f4190(
               var3,
               null,
               Color.Companion.recovered$95989221dad41c4d(),
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
               IntercomTheme.INSTANCE.getTypography(var86, IntercomTheme.$stable).getType05(),
               var86,
               384 | 14 & var7 >> 9,
               0,
               131066
            );
         }

         var86.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var86);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var77.endNode();
         ComposerKt.sourceInformationMarkerEnd(var77);
         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var50.endNode();
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var32);
         Painter var97 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var32, 0);
         String var98 = StringResources_androidKt.stringResource(string.intercom_dismiss, var32, 0);
         long var89 = Color.Companion.recovered$95989221dad41c4d();
         Modifier var96 = ClickableKt.recovered$1c9660c41fd850dc(Modifier.Companion, false, null, null, null, var4, 15, null);
         IconKt.recovered$e00adffb7fe45b02(var97, var98, var96, var89, var32, 3072 | Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var105 = var5.endRestartGroup();
      if (var105 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1214_0 = var0;
         final io.intercom.android.sdk.models.Avatar recoveredCapture_1214_1 = var1;
         final java.lang.String recoveredCapture_1214_2 = var2;
         final java.lang.String recoveredCapture_1214_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_1214_4 = var4;
         final int recoveredCapture_1214_5 = var6;
         var105.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1214_0, recoveredArgument_1214_1) -> IntercomPostActivityKt.TopBar$lambda$3(recoveredCapture_1214_0, recoveredCapture_1214_1, recoveredCapture_1214_2, recoveredCapture_1214_3, recoveredCapture_1214_4, recoveredCapture_1214_5, (androidx.compose.runtime.Composer)recoveredArgument_1214_0, ((java.lang.Integer)recoveredArgument_1214_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void BottomBarContent(@NotNull Modifier var0, @NotNull Function3 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "modifier");
      Intrinsics.checkNotNullParameter(var1, "content");
      var2 = var2.startRestartGroup(622304394);
      ComposerKt.sourceInformation(var2, "C(BottomBarContent)P(1)356@15673L349:IntercomPostActivity.kt#hv1x2k");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(622304394, var4, -1, "io.intercom.android.sdk.post.BottomBarContent (IntercomPostActivity.kt:355)");
         }

         Vertical var5 = Alignment.Companion.getCenterVertically();
         Modifier var10000 = WindowInsetsPadding_androidKt.navigationBarsPadding(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), Color.Companion.recovered$235b30c090a399c5(), null, 2, null
            )
         );
         int var7 = 56;
         int var8 = 0;
         var10000 = SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var7));
         var7 = 16;
         var8 = 0;
         Modifier var6 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var7), 0.0F, 2, null);
         HorizontalOrVertical var34 = Arrangement.INSTANCE.getSpaceBetween();
         short var36 = 432;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var10 = RowKt.rowMeasurePolicy(var34, var5, var2, 14 & var36 >> 3 | 112 & var36 >> 3);
         Modifier var11 = var6;
         MeasurePolicy var12 = var10;
         Composer var13 = var2;
         int var14 = 112 & var36 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var26, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var28 = 6 | 112 & var36 >> 6;
         Composer var29 = var26;
         RowScope var30 = RowScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1272666576, "C366@16007L9:IntercomPostActivity.kt#hv1x2k");
         var1.invoke(var30, var29, 14 & var28 | 112 & var4);
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var38 = var2.endRestartGroup();
      if (var38 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1213_0 = var0;
         final kotlin.jvm.functions.Function3 recoveredCapture_1213_1 = var1;
         final int recoveredCapture_1213_2 = var3;
         var38.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1213_0, recoveredArgument_1213_1) -> IntercomPostActivityKt.BottomBarContent$lambda$5(recoveredCapture_1213_0, recoveredCapture_1213_1, recoveredCapture_1213_2, (androidx.compose.runtime.Composer)recoveredArgument_1213_0, ((java.lang.Integer)recoveredArgument_1213_1).intValue())));
      }
   }

   private static final Unit TopBar$lambda$3(Modifier var0, Avatar var1, String var2, String var3, Function0 var4, int var5, Composer var6, int var7) {
      TopBar(var0, var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarContent$lambda$5(Modifier var0, Function3 var1, int var2, Composer var3, int var4) {
      BottomBarContent(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$TopBar(Modifier var0, Avatar var1, String var2, String var3, Function0 var4, Composer var5, int var6) {
      TopBar(var0, var1, var2, var3, var4, var5, var6);
   }
}
