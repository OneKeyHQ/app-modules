package io.intercom.android.sdk.m5.components;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
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
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$IconWithIndicator;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$IndicatorWithCount;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$None;
import io.intercom.android.sdk.m5.shapes.CutIconWithIndicatorShape;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001a\u0080\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0003\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0013\b\u0002\u0010\u0006\u001a\r\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\n\b\u0003\u0010\n\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0017",
   d2 = {
         "HomeItem",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "icon",
         "",
         "avatarIcon",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "iconModifier",
         "text",
         "textString",
         "",
         "subtitle",
         "badge",
         "Lio/intercom/android/sdk/m5/home/states/HomeItemBadge;",
         "onClick",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/Integer;Lkotlin/jvm/functions/Function2;Landroidx/compose/ui/Modifier;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/m5/home/states/HomeItemBadge;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "HomeSendMessagePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "HomeMessagesPreview",
         "HomeTicketsPreview",
         "HomeTicketsRTLPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeItem.kt\nio/intercom/android/sdk/m5/components/HomeItemKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n*L\n1#1,184:1\n118#2:185\n118#2:198\n118#2:231\n118#2:267\n118#2:338\n118#2:343\n1047#3,6:186\n1047#3,6:192\n1047#3,6:344\n99#4:199\n96#4,9:200\n99#4:268\n95#4,10:269\n106#4:304\n106#4:353\n81#5,6:209\n88#5,6:224\n81#5,6:241\n88#5,6:256\n96#5:265\n81#5,6:279\n88#5,6:294\n96#5:303\n81#5,6:316\n88#5,6:331\n96#5:341\n96#5:352\n402#6,9:215\n411#6:230\n402#6,9:247\n411#6,3:262\n402#6,9:285\n411#6,3:300\n402#6,9:322\n411#6:337\n412#6,2:339\n412#6,2:350\n87#7:232\n85#7,8:233\n94#7:266\n70#8:305\n66#8,10:306\n77#8:342\n*S KotlinDebug\n*F\n+ 1 HomeItem.kt\nio/intercom/android/sdk/m5/components/HomeItemKt\n*L\n42#1:185\n53#1:198\n58#1:231\n77#1:267\n104#1:338\n112#1:343\n47#1:186,6\n52#1:192,6\n113#1:344,6\n49#1:199\n49#1:200,9\n75#1:268\n75#1:269,10\n75#1:304\n49#1:353\n49#1:209,6\n49#1:224,6\n56#1:241,6\n56#1:256,6\n56#1:265\n75#1:279,6\n75#1:294,6\n75#1:303\n92#1:316,6\n92#1:331,6\n92#1:341\n49#1:352\n49#1:215,9\n49#1:230\n56#1:247,9\n56#1:262,3\n75#1:285,9\n75#1:300,3\n92#1:322,9\n92#1:337\n92#1:339,2\n49#1:350,2\n56#1:232\n56#1:233,8\n56#1:266\n92#1:305\n92#1:306,10\n92#1:342\n*E\n"
)
public final class HomeItemKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void HomeItem(
      @Nullable Modifier var0,
      @DrawableRes @Nullable Integer var1,
      @Nullable Function2 var2,
      @Nullable Modifier var3,
      @StringRes @Nullable Integer var4,
      @Nullable String var5,
      @Nullable String var6,
      @Nullable HomeItemBadge var7,
      @Nullable Function0 var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      var9 = var9.startRestartGroup(-360748364);
      ComposerKt.sourceInformation(var9, "C(HomeItem)P(4,2!1,3,7,8,6)46@1915L2,51@2011L13,48@1927L2932:HomeItem.kt#arqjag");
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var9.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var9.changedInstance(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var9.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var9.changed(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var9.changed(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var9.changed(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= ((var10 & 16777216) == 0 ? var9.changed(var7) : var9.changedInstance(var7)) ? 8388608 : 4194304;
      }

      if ((var11 & 256) != 0) {
         var12 |= 100663296;
      } else if ((var10 & 100663296) == 0) {
         var12 |= var9.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 38347923) == 38347922 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var11 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var11 & 2) != 0) {
            var1 = null;
         }

         if ((var11 & 4) != 0) {
            var2 = ComposableSingletons$HomeItemKt.INSTANCE.recovered$eac478aa65045e5d();
         }

         if ((var11 & 8) != 0) {
            Modifier var10000 = Modifier.Companion;
            int var13 = 16;
            int var14 = 0;
            var3 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var13));
         }

         if ((var11 & 16) != 0) {
            var4 = null;
         }

         if ((var11 & 32) != 0) {
            var5 = null;
         }

         if ((var11 & 64) != 0) {
            var6 = null;
         }

         if ((var11 & 128) != 0) {
            var7 = HomeItemBadge$None.INSTANCE;
         }

         if ((var11 & 256) != 0) {
            var9.startReplaceGroup(-244016614);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeItem.kt#9igjgp");
            Composer var100 = var9;
            boolean var15 = false;
            int var16 = 0;
            Object var17 = var100.rememberedValue();
            int var18 = 0;
            Object var196;
            if (var17 == Composer.Companion.getEmpty()) {
               int var19 = 0;
               Object var113 = ((kotlin.jvm.functions.Function0) () -> HomeItemKt.HomeItem$lambda$1$lambda$0());
               var100.updateRememberedValue(var113);
               var196 = var113;
            } else {
               var196 = var17;
            }

            Function0 var95 = (Function0)var196;
            var9.endReplaceGroup();
            var8 = var95;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-360748364, var12, -1, "io.intercom.android.sdk.m5.components.HomeItem (HomeItem.kt:47)");
         }

         Modifier var197 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var9.startReplaceGroup(-244013531);
         ComposerKt.sourceInformation(var9, "CC(remember):HomeItem.kt#9igjgp");
         Composer var101 = var9;
         boolean var105 = (var12 & 234881024) == 67108864;
         int var107 = 0;
         Object var109 = var101.rememberedValue();
         int var111 = 0;
         Object var10005;
         if (!var105 && var109 != Composer.Companion.getEmpty()) {
            var10005 = var109;
         } else {
            Object var92 = null;
            Object var91 = null;
            Object var90 = null;
            boolean var89 = false;
            Modifier var88 = var197;
            int var114 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_213_0 = var8;
            Function0 var93 = ((kotlin.jvm.functions.Function0) () -> HomeItemKt.HomeItem$lambda$3$lambda$2(recoveredCapture_213_0));
            var197 = var88;
            var10001 = var89;
            var10002 = var90;
            var10003 = var91;
            var10004 = var92;
            Object var20 = var93;
            var101.updateRememberedValue(var20);
            var10005 = var20;
         }

         Function0 var96 = (Function0)var10005;
         var9.endReplaceGroup();
         var197 = ClickableKt.recovered$1c9660c41fd850dc(
            var197, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var96, 15, null
         );
         int var97 = 16;
         int var102 = 0;
         float var204 = Dp.recovered$95b3d103663bf060(var97);
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         Modifier var99 = PaddingKt.recovered$904403cea4c49d95(var197, var204, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Vertical var106 = Alignment.Companion.getCenterVertically();
         short var108 = 384;
         var109 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var9, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var104 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var112 = RowKt.rowMeasurePolicy(var104, var106, var9, 14 & var108 >> 3 | 112 & var108 >> 3);
         Modifier var115 = var99;
         MeasurePolicy var116 = var112;
         Composer var21 = var9;
         int var22 = 112 & var108 << 3;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var24 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var21, 0));
         CompositionLocalMap var25 = var21.getCurrentCompositionLocalMap();
         Modifier var26 = ComposedModifierKt.materializeModifier(var21, var115);
         Function0 var27 = ComposeUiNode.Companion.getConstructor();
         Composer var28 = var21;
         int var29 = 6 | 896 & var22 << 6;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var28.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var28.startReusableNode();
         if (var28.getInserting()) {
            var28.createNode(var27);
         } else {
            var28.useNode();
         }

         Composer var31 = Updater.recovered$676aa17b44fd6280(var28);
         int var32 = 0;
         Updater.recovered$89682b199a142880(var31, var116, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var31, var25, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var31, var24, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var31, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var31, var26, ComposeUiNode.Companion.getSetModifier());
         int var33 = 14 & var29 >> 6;
         Composer var34 = var28;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var36 = 6 | 112 & var108 >> 6;
         Composer var37 = var34;
         RowScope var38 = RowScopeInstance.INSTANCE;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 941255428, "C55@2157L637:HomeItem.kt#arqjag");
         Modifier var40 = RowScope.weight$default(var38, Modifier.Companion, 2.0F, false, 2, null);
         int $this$dp$ivxx = 4;
         int $i$f$getDpxx = 0;
         androidx.compose.foundation.layout.Arrangement.Vertical var118 = Arrangement.INSTANCE
            .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         int $this$dp$ivxxx = 48;
         int var44 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var37, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         androidx.compose.ui.Alignment.Horizontal var121 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var118, var121, var37, 14 & $this$dp$ivxxx >> 3 | 112 & $this$dp$ivxxx >> 3);
         Modifier var46 = var40;
         MeasurePolicy var47 = measurePolicy$ivx;
         Composer var48 = var37;
         int var49 = 112 & $this$dp$ivxxx << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var48, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var48, 0));
         CompositionLocalMap localMap$iv$ivx = var48.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var48, var46);
         Function0 var54 = ComposeUiNode.Companion.getConstructor();
         Composer var55 = var48;
         int $i$f$Layoutxx = 6 | 896 & var49 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var55.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var55.startReusableNode();
         if (var55.getInserting()) {
            var55.createNode(var54);
         } else {
            var55.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var55);
         int var59/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var47, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var60/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 14 & $i$f$Layoutxx >> 6;
         Composer $composer$ivx = var55;
         int var62 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var63/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 6 | 112 & $this$dp$ivxxx >> 6;
         Composer $this$Layout_u24lambda_u240$iv$ivxx = $composer$ivx;
         ColumnScope var65 = ColumnScopeInstance.INSTANCE;
         int var66 = 0;
         ComposerKt.sourceInformationMarkerStart($this$Layout_u24lambda_u240$iv$ivxx, 193560292, "C61@2429L10,59@2295L254:HomeItem.kt#arqjag");
         $this$Layout_u24lambda_u240$iv$ivxx.startReplaceGroup(-1379228937);
         ComposerKt.sourceInformation($this$Layout_u24lambda_u240$iv$ivxx, "60@2342L25");
         String var199;
         if (var4 != null) {
            var199 = StringResources_androidKt.stringResource(var4, $this$Layout_u24lambda_u240$iv$ivxx, 14 & var12 >> 12);
         } else {
            var199 = var5;
            if (var5 == null) {
               var199 = "";
            }
         }

         String var67 = var199;
         $this$Layout_u24lambda_u240$iv$ivxx.endReplaceGroup();
         String var68 = var67;
         TextStyle var184 = IntercomTheme.INSTANCE.getTypography($this$Layout_u24lambda_u240$iv$ivxx, IntercomTheme.$stable).getType04SemiBold();
         int var69 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var68,
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            var69,
            false,
            2,
            0,
            null,
            var184,
            $this$Layout_u24lambda_u240$iv$ivxx,
            0,
            24960,
            110590
         );
         $this$Layout_u24lambda_u240$iv$ivxx.startReplaceGroup(-1379221164);
         ComposerKt.sourceInformation($this$Layout_u24lambda_u240$iv$ivxx, "68@2687L10,66@2602L168");
         if (var6 != null) {
            TextStyle var187 = IntercomTheme.INSTANCE.getTypography($this$Layout_u24lambda_u240$iv$ivxx, IntercomTheme.$stable).getType04();
            long var70 = ColorKt.Color(4285887861L);
            TextKt.recovered$5b114ab32a4f4190(
               var6,
               null,
               var70,
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
               var187,
               $this$Layout_u24lambda_u240$iv$ivxx,
               384 | 14 & var12 >> 18,
               0,
               131066
            );
         }

         $this$Layout_u24lambda_u240$iv$ivxx.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd($this$Layout_u24lambda_u240$iv$ivxx);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var55.endNode();
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var37);
         HomeItemBadge var117 = var7;
         if (Intrinsics.areEqual(var117, HomeItemBadge$None.INSTANCE)) {
            var37.startReplaceGroup(941879891);
            ComposerKt.sourceInformation(var37, "74@2852L600");
            Vertical var119 = Alignment.Companion.getCenterVertically();
            $this$dp$ivxxx = 8;
            var44 = 0;
            HorizontalOrVertical var122 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            short var131 = 432;
            int var134 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var37, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var127 = Modifier.Companion;
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var122, var119, var37, 14 & var131 >> 3 | 112 & var131 >> 3);
            Modifier var140 = var127;
            MeasurePolicy var144 = measurePolicy$ivxx;
            Composer var148 = var37;
            $i$f$Layoutx = 112 & var131 << 3;
            compositeKeyHash$iv$ivx = 0;
            ComposerKt.sourceInformationMarkerStart(var148, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var148, 0));
            CompositionLocalMap localMap$iv$ivxx = var148.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var148, var140);
            Function0 var160 = ComposeUiNode.Companion.getConstructor();
            Composer var162 = var148;
            $i$f$ReusableComposeNodex = 6 | 896 & $i$f$Layoutx << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var162, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var162.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var162.startReusableNode();
            if (var162.getInserting()) {
               var162.createNode(var160);
            } else {
               var162.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var162);
            var60/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var144, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
            );
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var172 = 14 & $i$f$ReusableComposeNodex >> 6;
            Composer $composer$ivxx = var162;
            var63/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var178 = 6 | 112 & var131 >> 6;
            Composer var180 = $composer$ivxx;
            RowScope var182 = RowScopeInstance.INSTANCE;
            int var185 = 0;
            ComposerKt.sourceInformationMarkerStart(var180, 194278252, "C78@3021L12:HomeItem.kt#arqjag");
            var2.invoke(var180, 14 & var12 >> 6);
            var180.startReplaceGroup(-1379205196);
            ComposerKt.sourceInformation(var180, "*83@3225L26,84@3298L6,80@3082L338");
            if (var1 == null) {
               Object var200 = null;
            } else {
               int var188 = var1.intValue();
               var69 = 0;
               Modifier var72 = TestTagKt.testTag(var3, var1.toString());
               Painter var73 = PainterResources_androidKt.painterResource(var1, var180, 14 & var12 >> 3);
               long var74 = IntercomTheme.INSTANCE.getColors(var180, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
               IconKt.recovered$e00adffb7fe45b02(var73, null, var72, var74, var180, 48 | Painter.$stable, 0);
            }

            var180.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var180);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var162.endNode();
            ComposerKt.sourceInformationMarkerEnd(var162);
            ComposerKt.sourceInformationMarkerEnd(var148);
            ComposerKt.sourceInformationMarkerEnd(var37);
            var37.endReplaceGroup();
         } else if (Intrinsics.areEqual(var117, HomeItemBadge$IconWithIndicator.INSTANCE)) {
            var37.startReplaceGroup(942534549);
            ComposerKt.sourceInformation(var37, "*91@3529L722");
            if (var1 == null) {
               Object var201 = null;
            } else {
               var44 = var1.intValue();
               int var135 = 0;
               byte var138 = 0;
               int var141 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var37, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
               );
               Modifier var145 = Modifier.Companion;
               Alignment var149 = Alignment.Companion.getTopStart();
               boolean var151 = false;
               MeasurePolicy measurePolicy$ivxx = BoxKt.maybeCachedBoxMeasurePolicy(var149, var151);
               Modifier var155 = var145;
               MeasurePolicy var157 = measurePolicy$ivxx;
               Composer var159 = var37;
               int var161 = 112 & var138 << 3;
               $i$f$Layoutxx = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var159, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh"
               );
               $i$f$ReusableComposeNodex = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var159, 0));
               CompositionLocalMap localMap$iv$ivxx = var159.getCurrentCompositionLocalMap();
               Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var159, var155);
               Function0 var171 = ComposeUiNode.Companion.getConstructor();
               $composer$ivx = var159;
               var62 = 6 | 896 & var161 << 6;
               var63/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
               ComposerKt.sourceInformationMarkerStart(
                  $composer$ivx, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp"
               );
               if (!($composer$ivx.getApplier() instanceof Applier)) {
                  ComposablesKt.invalidApplier();
               }

               $composer$ivx.startReusableNode();
               if ($composer$ivx.getInserting()) {
                  $composer$ivx.createNode(var171);
               } else {
                  $composer$ivx.useNode();
               }

               $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280($composer$ivx);
               int var181/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var157, ComposeUiNode.Companion.getSetMeasurePolicy());
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
               );
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, $i$f$ReusableComposeNodex, ComposeUiNode.Companion.getSetCompositeKeyHash()
               );
               Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
               var66 = 14 & var62 >> 6;
               Composer $composer$ivxx = $composer$ivx;
               int var189 = 0;
               ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
               var69 = 6 | 112 & var138 >> 6;
               Composer var192 = $composer$ivxx;
               BoxScope var193 = BoxScopeInstance.INSTANCE;
               int var76 = 0;
               ComposerKt.sourceInformationMarkerStart(var192, -1578883288, "C96@3761L26,97@3834L6,92@3555L401,100@3977L256:HomeItem.kt#arqjag");
               Modifier var77 = TestTagKt.testTag(ClipKt.clip(var3, new CutIconWithIndicatorShape(0.0F, 1, null)), var1.toString());
               Painter var78 = PainterResources_androidKt.painterResource(var1, var192, 14 & var12 >> 3);
               long var79 = IntercomTheme.INSTANCE.getColors(var192, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
               IconKt.recovered$e00adffb7fe45b02(var78, null, var77, var79, var192, 48 | Painter.$stable, 0);
               Modifier var205 = BackgroundKt.recovered$60bc47fb440df483(Modifier.Companion, ColorKt.Color(4292544041L), RoundedCornerShapeKt.getCircleShape());
               int $this$dp$ivxxxx = 7;
               int $i$f$getDpxxx = 0;
               BoxKt.Box(
                  var193.align(SizeKt.recovered$111ef94c93e78c04(var205, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), Alignment.Companion.getTopEnd()),
                  var192,
                  0
               );
               ComposerKt.sourceInformationMarkerEnd(var192);
               ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
               $composer$ivx.endNode();
               ComposerKt.sourceInformationMarkerEnd($composer$ivx);
               ComposerKt.sourceInformationMarkerEnd(var159);
               ComposerKt.sourceInformationMarkerEnd(var37);
            }

            var37.endReplaceGroup();
         } else {
            if (!(var117 instanceof HomeItemBadge$IndicatorWithCount)) {
               var37.startReplaceGroup(861667097);
               var37.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var37.startReplaceGroup(943331838);
            ComposerKt.sourceInformation(var37, "112@4433L182,109@4318L525");
            RowScope var202 = var38;
            Modifier var206 = Modifier.Companion;
            $i$f$getDpxx = 6;
            $this$dp$ivxxx = 0;
            Modifier var207 = PaddingKt.recovered$03b481d31d62885a(var206, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($i$f$getDpxx), 0.0F, 11, null);
            var37.startReplaceGroup(861717389);
            ComposerKt.sourceInformation(var37, "CC(remember):HomeItem.kt#9igjgp");
            Composer var129 = var37;
            boolean var133 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var129.rememberedValue();
            int var142/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            if (it$ivx == Composer.Companion.getEmpty()) {
               Modifier var81 = var207;
               RowScope var82 = var38;
               int var146 = 0;
               Function1 var83 = ((kotlin.jvm.functions.Function1) (recoveredArgument_210_0) -> HomeItemKt.HomeItem$lambda$11$lambda$10$lambda$9((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredArgument_210_0));
               var202 = var82;
               var207 = var81;
               Object var147 = var83;
               var129.updateRememberedValue(var147);
               var10002 = var147;
            } else {
               var10002 = it$ivx;
            }

            Function1 var124 = (Function1)var10002;
            var37.endReplaceGroup();
            Modifier var120 = var202.align(DrawModifierKt.drawBehind(var207, var124), Alignment.Companion.getCenterVertically());
            String var125 = String.valueOf(((HomeItemBadge$IndicatorWithCount)var7).getCount());
            long var84 = Color.Companion.recovered$95989221dad41c4d();
            long var86 = TextUnitKt.getSp(11);
            FontWeight var143 = FontWeight.Companion.getW800();
            TextKt.recovered$5b114ab32a4f4190(
               var125, var120, var84, null, var86, null, var143, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var37, 1597824, 0, 262056
            );
            var37.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var9);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var203 = var9.endRestartGroup();
      if (var203 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_211_0 = var0;
         final java.lang.Integer recoveredCapture_211_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_211_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_211_3 = var3;
         final java.lang.Integer recoveredCapture_211_4 = var4;
         final java.lang.String recoveredCapture_211_5 = var5;
         final java.lang.String recoveredCapture_211_6 = var6;
         final io.intercom.android.sdk.m5.home.states.HomeItemBadge recoveredCapture_211_7 = var7;
         final kotlin.jvm.functions.Function0 recoveredCapture_211_8 = var8;
         final int recoveredCapture_211_9 = var10;
         final int recoveredCapture_211_10 = var11;
         var203.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_211_0, recoveredArgument_211_1) -> HomeItemKt.HomeItem$lambda$12(recoveredCapture_211_0, recoveredCapture_211_1, recoveredCapture_211_2, recoveredCapture_211_3, recoveredCapture_211_4, recoveredCapture_211_5, recoveredCapture_211_6, recoveredCapture_211_7, recoveredCapture_211_8, recoveredCapture_211_9, recoveredCapture_211_10, (androidx.compose.runtime.Composer)recoveredArgument_211_0, ((java.lang.Integer)recoveredArgument_211_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeSendMessagePreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(861055120);
      ComposerKt.sourceInformation(var0, "C(HomeSendMessagePreview)131@4927L271:HomeItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(861055120, var1, -1, "io.intercom.android.sdk.m5.components.HomeSendMessagePreview (HomeItem.kt:130)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeItemKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_215_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_215_0, recoveredArgument_215_1) -> HomeItemKt.HomeSendMessagePreview$lambda$13(recoveredCapture_215_0, (androidx.compose.runtime.Composer)recoveredArgument_215_0, ((java.lang.Integer)recoveredArgument_215_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeMessagesPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1318695433);
      ComposerKt.sourceInformation(var0, "C(HomeMessagesPreview)145@5263L245:HomeItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1318695433, var1, -1, "io.intercom.android.sdk.m5.components.HomeMessagesPreview (HomeItem.kt:144)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeItemKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_214_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_214_0, recoveredArgument_214_1) -> HomeItemKt.HomeMessagesPreview$lambda$14(recoveredCapture_214_0, (androidx.compose.runtime.Composer)recoveredArgument_214_0, ((java.lang.Integer)recoveredArgument_214_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeTicketsPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-279860840);
      ComposerKt.sourceInformation(var0, "C(HomeTicketsPreview)159@5572L245:HomeItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-279860840, var1, -1, "io.intercom.android.sdk.m5.components.HomeTicketsPreview (HomeItem.kt:158)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeItemKt.INSTANCE.recovered$3faf0e22fa74c6a7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_216_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_216_0, recoveredArgument_216_1) -> HomeItemKt.HomeTicketsPreview$lambda$15(recoveredCapture_216_0, (androidx.compose.runtime.Composer)recoveredArgument_216_0, ((java.lang.Integer)recoveredArgument_216_1).intValue())));
      }
   }

   @Preview(locale = "ar")
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeTicketsRTLPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1844314002);
      ComposerKt.sourceInformation(var0, "C(HomeTicketsRTLPreview)173@5899L245:HomeItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1844314002, var1, -1, "io.intercom.android.sdk.m5.components.HomeTicketsRTLPreview (HomeItem.kt:172)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeItemKt.INSTANCE.recovered$db063fabb945fca0(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_217_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_217_0, recoveredArgument_217_1) -> HomeItemKt.HomeTicketsRTLPreview$lambda$16(recoveredCapture_217_0, (androidx.compose.runtime.Composer)recoveredArgument_217_0, ((java.lang.Integer)recoveredArgument_217_1).intValue())));
      }
   }

   private static final Unit HomeItem$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit HomeItem$lambda$3$lambda$2(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit HomeItem$lambda$11$lambda$10$lambda$9(DrawScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$drawBehind");
      DrawScope.recovered$34dce0a89fdcb963(var0, ColorKt.Color(4292544041L), 22.0F, 0L, 0.0F, null, null, 0, 124, null);
      return Unit.INSTANCE;
   }

   private static final Unit HomeItem$lambda$12(
      Modifier var0,
      Integer var1,
      Function2 var2,
      Modifier var3,
      Integer var4,
      String var5,
      String var6,
      HomeItemBadge var7,
      Function0 var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      HomeItem(var0, var1, var2, var3, var4, var5, var6, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit HomeSendMessagePreview$lambda$13(int var0, Composer var1, int var2) {
      HomeSendMessagePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HomeMessagesPreview$lambda$14(int var0, Composer var1, int var2) {
      HomeMessagesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HomeTicketsPreview$lambda$15(int var0, Composer var1, int var2) {
      HomeTicketsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HomeTicketsRTLPreview$lambda$16(int var0, Composer var1, int var2) {
      HomeTicketsRTLPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
