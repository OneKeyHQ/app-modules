package io.intercom.android.sdk.ui.component;

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
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconButtonKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a\u0081\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2 \b\u0002\u0010\u000f\u001a\u001a\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0010¢\u0006\u0002\b\u0012¢\u0006\u0002\b\u0013H\u0007¢\u0006\u0004\b\u0014\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0017\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0017\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0017¨\u0006\u001a",
   d2 = {
         "IntercomTopBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "title",
         "",
         "navIcon",
         "Lio/intercom/android/sdk/ui/component/IntercomTopBarIcon;",
         "contentAlignment",
         "Landroidx/compose/ui/Alignment$Horizontal;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "onContentClick",
         "Lkotlin/Function0;",
         "menuItems",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "IntercomTopBar-bogVsAg",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/ui/component/IntercomTopBarIcon;Landroidx/compose/ui/Alignment$Horizontal;JJLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "IntercomTopBarPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "IntercomTopBarPreviewWithoutNavIcon",
         "IntercomTopBarPreviewWithMenuItems",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/IntercomTopBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 8 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,160:1\n1047#2,6:161\n1047#2,6:202\n87#3:167\n83#3,10:168\n87#3,6:244\n94#3:275\n94#3:319\n81#4,6:178\n88#4,6:193\n81#4,6:218\n88#4,6:233\n81#4,6:250\n88#4,6:265\n96#4:274\n81#4,6:286\n88#4,6:301\n96#4:310\n96#4:314\n96#4:318\n402#5,9:184\n411#5:199\n402#5,9:224\n411#5:239\n402#5,9:256\n411#5,3:271\n402#5,9:292\n411#5,3:307\n412#5,2:312\n412#5,2:316\n118#6:200\n118#6:201\n118#6:240\n118#6:242\n118#6:243\n70#7:208\n67#7,9:209\n77#7:315\n49#8:241\n99#9:276\n96#9,9:277\n106#9:311\n*S KotlinDebug\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/IntercomTopBarKt\n*L\n41#1:161,6\n52#1:202,6\n44#1:167\n44#1:168,10\n62#1:244,6\n62#1:275\n44#1:319\n44#1:178,6\n44#1:193,6\n45#1:218,6\n45#1:233,6\n62#1:250,6\n62#1:265,6\n62#1:274\n94#1:286,6\n94#1:301,6\n94#1:310\n45#1:314\n44#1:318\n44#1:184,9\n44#1:199\n45#1:224,9\n45#1:239\n62#1:256,9\n62#1:271,3\n94#1:292,9\n94#1:307,3\n45#1:312,2\n44#1:316,2\n50#1:200\n51#1:201\n57#1:240\n59#1:242\n61#1:243\n45#1:208\n45#1:209,9\n45#1:315\n57#1:241\n94#1:276\n94#1:277,9\n94#1:311\n*E\n"
)
public final class IntercomTopBarKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void recovered$4ca9f42ae4e1cbf3(
      @Nullable Modifier var0,
      @Nullable String var1,
      @Nullable IntercomTopBarIcon var2,
      @Nullable Horizontal var3,
      long var4,
      long var6,
      @Nullable Function0 var8,
      @Nullable Function3 var9,
      @Nullable Composer var10,
      int var11,
      int var12
   ) {
      var10 = var10.startRestartGroup(832461575);
      ComposerKt.sourceInformation(
         var10, "C(IntercomTopBar)P(4,7,5,1,0:c#ui.graphics.Color,2:c#ui.graphics.Color,6)38@1655L6,39@1710L6,40@1760L2,43@1830L2227:IntercomTopBar.kt#yr602f"
      );
      int var13 = var11;
      if ((var12 & 1) != 0) {
         var13 |= 6;
      } else if ((var11 & 6) == 0) {
         var13 |= var10.changed(var0) ? 4 : 2;
      }

      if ((var12 & 2) != 0) {
         var13 |= 48;
      } else if ((var11 & 48) == 0) {
         var13 |= var10.changed(var1) ? 32 : 16;
      }

      if ((var12 & 4) != 0) {
         var13 |= 384;
      } else if ((var11 & 384) == 0) {
         var13 |= var10.changed(var2) ? 256 : 128;
      }

      if ((var12 & 8) != 0) {
         var13 |= 3072;
      } else if ((var11 & 3072) == 0) {
         var13 |= var10.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 24576) == 0) {
         var13 |= (var12 & 16) == 0 && var10.changed(var4) ? 16384 : 8192;
      }

      if ((var11 & 196608) == 0) {
         var13 |= (var12 & 32) == 0 && var10.changed(var6) ? 131072 : 65536;
      }

      if ((var12 & 64) != 0) {
         var13 |= 1572864;
      } else if ((var11 & 1572864) == 0) {
         var13 |= var10.changedInstance(var8) ? 1048576 : 524288;
      }

      if ((var12 & 128) != 0) {
         var13 |= 12582912;
      } else if ((var11 & 12582912) == 0) {
         var13 |= var10.changedInstance(var9) ? 8388608 : 4194304;
      }

      if ((var13 & 4793491) == 4793490 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         var10.startDefaults();
         if ((var11 & 1) != 0 && !var10.getDefaultsInvalid()) {
            var10.skipToGroupEnd();
            if ((var12 & 16) != 0) {
               var13 &= -57345;
            }

            if ((var12 & 32) != 0) {
               var13 &= -458753;
            }
         } else {
            if ((var12 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var12 & 2) != 0) {
               var1 = null;
            }

            if ((var12 & 4) != 0) {
               var2 = null;
            }

            if ((var12 & 8) != 0) {
               var3 = Alignment.Companion.getStart();
            }

            if ((var12 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getColors(var10, 6).recovered$a4be33b4cee7fcc4();
               var13 &= -57345;
            }

            if ((var12 & 32) != 0) {
               var6 = IntercomTheme.INSTANCE.getColors(var10, 6).recovered$156c30ff4ea0f1e5();
               var13 &= -458753;
            }

            if ((var12 & 64) != 0) {
               var10.startReplaceGroup(-883388004);
               ComposerKt.sourceInformation(var10, "CC(remember):IntercomTopBar.kt#9igjgp");
               Composer var15 = var10;
               boolean var16 = false;
               int var17 = 0;
               Object var18 = var15.rememberedValue();
               int var19 = 0;
               Object var10000;
               if (var18 == Composer.Companion.getEmpty()) {
                  int var20 = 0;
                  Object var110 = ((kotlin.jvm.functions.Function0) () -> IntercomTopBarKt.IntercomTopBar_bogVsAg$lambda$1$lambda$0());
                  var15.updateRememberedValue(var110);
                  var10000 = var110;
               } else {
                  var10000 = var18;
               }

               Function0 var14 = (Function0)var10000;
               var10.endReplaceGroup();
               var8 = var14;
            }

            if ((var12 & 128) != 0) {
               var9 = null;
            }
         }

         var10.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(832461575, var13, -1, "io.intercom.android.sdk.ui.component.IntercomTopBar (IntercomTopBar.kt:42)");
         }

         byte var107 = 0;
         int var108 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var10, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var104 = Modifier.Companion;
         Vertical var105 = Arrangement.INSTANCE.getTop();
         Horizontal var106 = Alignment.Companion.getStart();
         MeasurePolicy var109 = ColumnKt.columnMeasurePolicy(var105, var106, var10, 14 & var107 >> 3 | 112 & var107 >> 3);
         Modifier var111 = var104;
         MeasurePolicy var21 = var109;
         Composer var22 = var10;
         int var23 = 112 & var107 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var111);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var107 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, -1604472178, "C51@2100L20,44@1847L2178,104@4034L17:IntercomTopBar.kt#yr602f");
         Modifier var162 = SizeKt.fillMaxWidth$default(
            WindowInsetsPadding_androidKt.statusBarsPadding(BackgroundKt.recovered$42a3174180b3fb7a(var0, var4, null, 2, null)), 0.0F, 1, null
         );
         int var41 = 56;
         int var42 = 0;
         var162 = SizeKt.recovered$3f6c6a76aa292717(var162, Dp.recovered$95b3d103663bf060(var41));
         var41 = 4;
         var42 = 0;
         var162 = PaddingKt.recovered$f3b9f2e94c07c806(var162, Dp.recovered$95b3d103663bf060(var41), 0.0F, 2, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var38.startReplaceGroup(86796077);
         ComposerKt.sourceInformation(var38, "CC(remember):IntercomTopBar.kt#9igjgp");
         Composer var116 = var38;
         boolean var43 = (var13 & 3670016) == 1048576;
         int var44 = 0;
         Object var45 = var116.rememberedValue();
         int var46 = 0;
         Object var10005;
         if (!var43 && var45 != Composer.Companion.getEmpty()) {
            var10005 = var45;
         } else {
            Object var47 = null;
            Object var48 = null;
            Object var49 = null;
            boolean var50 = false;
            Modifier var51 = var162;
            int var52 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_22_0 = var8;
            Function0 var53 = ((kotlin.jvm.functions.Function0) () -> IntercomTopBarKt.IntercomTopBar_bogVsAg$lambda$7$lambda$3$lambda$2(recoveredCapture_22_0));
            var162 = var51;
            var10001 = var50;
            var10002 = var49;
            var10003 = var48;
            var10004 = var47;
            Object var54 = var53;
            var116.updateRememberedValue(var54);
            var10005 = var54;
         }

         Function0 var113 = (Function0)var10005;
         var38.endReplaceGroup();
         Modifier var114 = ClickableKt.recovered$1c9660c41fd850dc(
            var162, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var113, 15, null
         );
         byte var119 = 0;
         var45 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var38, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var117 = Alignment.Companion.getTopStart();
         var43 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var117, var43);
         Modifier var122 = var114;
         MeasurePolicy var123 = measurePolicy$ivx;
         Composer var55 = var38;
         int var56 = 112 & var119 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var55, 0));
         CompositionLocalMap localMap$iv$ivx = var55.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var55, var122);
         Function0 var61 = ComposeUiNode.Companion.getConstructor();
         Composer var62 = var55;
         int var63 = 6 | 896 & var56 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var62, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var62.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var62.startReusableNode();
         if (var62.getInserting()) {
            var62.createNode(var61);
         } else {
            var62.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var62);
         int var66/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var123, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var67 = 14 & var63 >> 6;
         Composer $composer$ivx = var62;
         int var69 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var70 = 6 | 112 & var119 >> 6;
         Composer var71 = $composer$ivx;
         BoxScope var72 = BoxScopeInstance.INSTANCE;
         int var73 = 0;
         ComposerKt.sourceInformationMarkerStart(var71, 2034160213, "C61@2429L621:IntercomTopBar.kt#yr602f");
         float var165;
         if (var2 != null && !Intrinsics.areEqual(var3, Alignment.Companion.getCenterHorizontally())) {
            int $this$dp$ivx = 48;
            int $i$f$getDpx = 0;
            float var125 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            $i$f$getDpx = 16;
            int $i$f$getDpxx = 0;
            float var129 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
            $i$f$getDpxx = 0;
            var165 = Dp.recovered$95b3d103663bf060(var125 + var129);
         } else {
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var165 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var77 = var165;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var126 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         Modifier var131 = PaddingKt.recovered$03b481d31d62885a(
            SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), var77, 0.0F, var126, 0.0F, 10, null
         );
         HorizontalOrVertical var136 = Arrangement.INSTANCE.getCenter();
         int var78 = 48 | 896 & var13 >> 3;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var71, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(var136, var3, var71, 14 & var78 >> 3 | 112 & var78 >> 3);
         Modifier var81 = var131;
         MeasurePolicy var82 = measurePolicy$ivxx;
         Composer var83 = var71;
         int var84 = 112 & var78 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var83, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var83, 0));
         CompositionLocalMap localMap$iv$ivxx = var83.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var83, var81);
         Function0 var89 = ComposeUiNode.Companion.getConstructor();
         Composer var90 = var83;
         int var91 = 6 | 896 & var84 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var90, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var90.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var90.startReusableNode();
         if (var90.getInserting()) {
            var90.createNode(var89);
         } else {
            var90.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var90);
         int var94/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var82, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var95/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 14 & var91 >> 6;
         Composer $composer$ivxx = var90;
         int var97/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var98 = 6 | 112 & var78 >> 6;
         Composer var99 = $composer$ivxx;
         ColumnScope var100 = ColumnScopeInstance.INSTANCE;
         int var101 = 0;
         ComposerKt.sourceInformationMarkerStart(var99, 192930068, "C:IntercomTopBar.kt#yr602f");
         var99.startReplaceGroup(560412879);
         ComposerKt.sourceInformation(var99, "73@2951L10,69@2765L253");
         if (var1 != null) {
            TextKt.recovered$5b114ab32a4f4190(
               var1,
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
               TextOverflow.Companion.recovered$0ef33c02f8eeacd2(),
               false,
               1,
               0,
               null,
               TextStyle.recovered$cf01d8b71690b5c5(
                  IntercomTheme.INSTANCE.getTypography(var99, 6).getType03(),
                  var6,
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
               ),
               var99,
               14 & var13 >> 3,
               24960,
               110590
            );
         }

         var99.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var99);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var90.endNode();
         ComposerKt.sourceInformationMarkerEnd(var90);
         ComposerKt.sourceInformationMarkerEnd(var83);
         ComposerKt.sourceInformationMarkerEnd(var71);
         var71.startReplaceGroup(-1319827237);
         ComposerKt.sourceInformation(var71, "82@3272L315,79@3125L462");
         if (var2 != null) {
            Modifier var132 = var72.align(Modifier.Companion, Alignment.Companion.getCenterStart());
            IconButtonKt.IconButton(
               var2.getOnClick(),
               var132,
               false,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-423583748, true, new IntercomTopBarKt$IntercomTopBar$2$2$2(var2, var6), var71, 54),
               var71,
               1572864,
               60
            );
         }

         var71.endReplaceGroup();
         var71.startReplaceGroup(-1319809649);
         ComposerKt.sourceInformation(var71, "93@3681L320");
         if (var9 != null) {
            Modifier var133 = var72.align(
               SizeKt.wrapContentWidth$default(SizeKt.fillMaxHeight$default(Modifier.Companion, 0.0F, 1, null), null, false, 3, null),
               Alignment.Companion.getCenterEnd()
            );
            androidx.compose.ui.Alignment.Vertical var138 = Alignment.Companion.getCenterVertically();
            short var139 = 384;
            int var140 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var71, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Horizontal var137 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivxxx = RowKt.rowMeasurePolicy(var137, var138, var71, 14 & var139 >> 3 | 112 & var139 >> 3);
            Modifier var142 = var133;
            MeasurePolicy var143 = measurePolicy$ivxxx;
            Composer var144 = var71;
            $i$f$Layoutxx = 112 & var139 << 3;
            compositeKeyHash$iv$ivxx = 0;
            ComposerKt.sourceInformationMarkerStart(var144, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var144, 0));
            CompositionLocalMap localMap$iv$ivxxx = var144.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var144, var142);
            Function0 var150 = ComposeUiNode.Companion.getConstructor();
            Composer var151 = var144;
            $i$f$ReusableComposeNodexx = 6 | 896 & $i$f$Layoutxx << 6;
            int $i$f$ReusableComposeNodexxx = 0;
            ComposerKt.sourceInformationMarkerStart(var151, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var151.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var151.startReusableNode();
            if (var151.getInserting()) {
               var151.createNode(var150);
            } else {
               var151.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var151);
            var95/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var143, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
            );
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
            );
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
            int var156 = 14 & $i$f$ReusableComposeNodexx >> 6;
            Composer $composer$ivxxx = var151;
            var98 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var159 = 6 | 112 & var139 >> 6;
            Composer var160 = $composer$ivxxx;
            RowScope var161 = RowScopeInstance.INSTANCE;
            int var102 = 0;
            ComposerKt.sourceInformationMarkerStart(var160, 194158753, "C100@3972L11:IntercomTopBar.kt#yr602f");
            var9.invoke(var161, var160, 14 & var159 | 112 & var13 >> 18);
            ComposerKt.sourceInformationMarkerEnd(var160);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
            var151.endNode();
            ComposerKt.sourceInformationMarkerEnd(var151);
            ComposerKt.sourceInformationMarkerEnd(var144);
            ComposerKt.sourceInformationMarkerEnd(var71);
         }

         var71.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var71);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var62.endNode();
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd(var38);
         IntercomDividerKt.IntercomDivider(null, var38, 0, 1);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var10);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var166 = var10.endRestartGroup();
      if (var166 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_20_0 = var0;
         final java.lang.String recoveredCapture_20_1 = var1;
         final io.intercom.android.sdk.ui.component.IntercomTopBarIcon recoveredCapture_20_2 = var2;
         final androidx.compose.ui.Alignment.Horizontal recoveredCapture_20_3 = var3;
         final long recoveredCapture_20_4 = var4;
         final long recoveredCapture_20_5 = var6;
         final kotlin.jvm.functions.Function0 recoveredCapture_20_6 = var8;
         final kotlin.jvm.functions.Function3 recoveredCapture_20_7 = var9;
         final int recoveredCapture_20_8 = var11;
         final int recoveredCapture_20_9 = var12;
         var166.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_20_0, recoveredArgument_20_1) -> IntercomTopBarKt.IntercomTopBar_bogVsAg$lambda$8(recoveredCapture_20_0, recoveredCapture_20_1, recoveredCapture_20_2, recoveredCapture_20_3, recoveredCapture_20_4, recoveredCapture_20_5, recoveredCapture_20_6, recoveredCapture_20_7, recoveredCapture_20_8, recoveredCapture_20_9, (androidx.compose.runtime.Composer)recoveredArgument_20_0, ((java.lang.Integer)recoveredArgument_20_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void IntercomTopBarPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-358701081);
      ComposerKt.sourceInformation(var0, "C(IntercomTopBarPreview)117@4275L337:IntercomTopBar.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-358701081, var1, -1, "io.intercom.android.sdk.ui.component.IntercomTopBarPreview (IntercomTopBar.kt:116)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomTopBarKt.INSTANCE.recovered$a54a3cf5c9caf09f(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_17_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_17_0, recoveredArgument_17_1) -> IntercomTopBarKt.IntercomTopBarPreview$lambda$9(recoveredCapture_17_0, (androidx.compose.runtime.Composer)recoveredArgument_17_0, ((java.lang.Integer)recoveredArgument_17_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void IntercomTopBarPreviewWithoutNavIcon(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-110907621);
      ComposerKt.sourceInformation(var0, "C(IntercomTopBarPreviewWithoutNavIcon)133@4702L91:IntercomTopBar.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-110907621, var1, -1, "io.intercom.android.sdk.ui.component.IntercomTopBarPreviewWithoutNavIcon (IntercomTopBar.kt:132)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomTopBarKt.INSTANCE.recovered$16fb19f16412fc3e(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_19_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_19_0, recoveredArgument_19_1) -> IntercomTopBarKt.IntercomTopBarPreviewWithoutNavIcon$lambda$10(recoveredCapture_19_0, (androidx.compose.runtime.Composer)recoveredArgument_19_0, ((java.lang.Integer)recoveredArgument_19_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void IntercomTopBarPreviewWithMenuItems(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2022840780);
      ComposerKt.sourceInformation(var0, "C(IntercomTopBarPreviewWithMenuItems)143@4882L500:IntercomTopBar.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2022840780, var1, -1, "io.intercom.android.sdk.ui.component.IntercomTopBarPreviewWithMenuItems (IntercomTopBar.kt:142)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$IntercomTopBarKt.INSTANCE.recovered$2bef68661847c3f7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_18_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_18_0, recoveredArgument_18_1) -> IntercomTopBarKt.IntercomTopBarPreviewWithMenuItems$lambda$11(recoveredCapture_18_0, (androidx.compose.runtime.Composer)recoveredArgument_18_0, ((java.lang.Integer)recoveredArgument_18_1).intValue())));
      }
   }

   private static final Unit IntercomTopBar_bogVsAg$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit IntercomTopBar_bogVsAg$lambda$7$lambda$3$lambda$2(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit IntercomTopBar_bogVsAg$lambda$8(
      Modifier var0,
      String var1,
      IntercomTopBarIcon var2,
      Horizontal var3,
      long var4,
      long var6,
      Function0 var8,
      Function3 var9,
      int var10,
      int var11,
      Composer var12,
      int var13
   ) {
      recovered$4ca9f42ae4e1cbf3(var0, var1, var2, var3, var4, var6, var8, var9, var12, RecomposeScopeImplKt.updateChangedFlags(var10 | 1), var11);
      return Unit.INSTANCE;
   }

   private static final Unit IntercomTopBarPreview$lambda$9(int var0, Composer var1, int var2) {
      IntercomTopBarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit IntercomTopBarPreviewWithoutNavIcon$lambda$10(int var0, Composer var1, int var2) {
      IntercomTopBarPreviewWithoutNavIcon(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit IntercomTopBarPreviewWithMenuItems$lambda$11(int var0, Composer var1, int var2) {
      IntercomTopBarPreviewWithMenuItems(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
