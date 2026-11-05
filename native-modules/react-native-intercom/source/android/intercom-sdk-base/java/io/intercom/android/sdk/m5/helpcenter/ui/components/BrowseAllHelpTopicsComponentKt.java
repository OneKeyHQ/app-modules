package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.IntercomChevronKt;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\u0017\u0010\u0005\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0006\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0007\u001a\r\u0010\b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0007¨\u0006\t",
   d2 = {
         "BrowseAllHelpTopicsComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "BrowseAllHelpTopicsAsItem",
         "BrowseAllHelpTopicsComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "BrowseAllHelpTopicsASItemPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBrowseAllHelpTopicsComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BrowseAllHelpTopicsComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/BrowseAllHelpTopicsComponentKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,114:1\n75#2:115\n75#2:124\n118#3:116\n118#3:117\n118#3:131\n118#3:233\n1047#4,6:118\n1047#4,6:125\n87#5:132\n84#5,9:133\n87#5:197\n84#5,9:198\n94#5:232\n94#5:241\n81#6,6:142\n88#6,6:157\n81#6,6:175\n88#6,6:190\n81#6,6:207\n88#6,6:222\n96#6:231\n96#6:236\n96#6:240\n402#7,9:148\n411#7:163\n402#7,9:181\n411#7:196\n402#7,9:213\n411#7,3:228\n412#7,2:234\n412#7,2:238\n99#8:164\n95#8,10:165\n106#8:237\n*S KotlinDebug\n*F\n+ 1 BrowseAllHelpTopicsComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/BrowseAllHelpTopicsComponentKt\n*L\n35#1:115\n69#1:124\n49#1:116\n50#1:117\n80#1:131\n94#1:233\n38#1:118,6\n73#1:125,6\n70#1:132\n70#1:133,9\n85#1:197\n85#1:198,9\n85#1:232\n70#1:241\n70#1:142,6\n70#1:157,6\n82#1:175,6\n82#1:190,6\n85#1:207,6\n85#1:222,6\n85#1:231\n82#1:236\n70#1:240\n70#1:148,9\n70#1:163\n82#1:181,9\n82#1:196\n85#1:213,9\n85#1:228,3\n82#1:234,2\n70#1:238,2\n82#1:164\n82#1:165,10\n82#1:237\n*E\n"
)
public final class BrowseAllHelpTopicsComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BrowseAllHelpTopicsComponent(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      Composer var17 = var1.startRestartGroup(-1332059523);
      ComposerKt.sourceInformation(
         var17,
         "C(BrowseAllHelpTopicsComponent)34@1476L7,45@1851L6,46@1910L6,44@1786L154,48@1992L6,37@1551L201,35@1488L1068:BrowseAllHelpTopicsComponent.kt#ngglre"
      );
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var17.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var17.getSkipping()) {
         var17.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1332059523, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponent (BrowseAllHelpTopicsComponent.kt:33)"
            );
         }

         CompositionLocal var6 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var17.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var17);
         Context var5 = (Context)var9;
         ButtonColors var18 = ButtonDefaults.INSTANCE
            .recovered$e5c6acecc304a3a6(
               IntercomTheme.INSTANCE.getColors(var17, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               IntercomTheme.INSTANCE.getColors(var17, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
               0L,
               0L,
               var17,
               ButtonDefaults.$stable << 12,
               12
            );
         int var20 = 1;
         int var22 = 0;
         BorderStroke var19 = BorderStrokeKt.recovered$91879a1bef2fb536(
            Dp.recovered$95b3d103663bf060(var20),
            Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var17, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.12F, 0.0F, 0.0F, 0.0F, 14, null
            )
         );
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var10000 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         PaddingValues var21 = PaddingKt.recovered$4ebb4826aa57abc6(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         var17.startReplaceGroup(-405240498);
         ComposerKt.sourceInformation(var17, "CC(remember):BrowseAllHelpTopicsComponent.kt#9igjgp");
         Composer var27 = var17;
         boolean var11 = var17.changedInstance(var5);
         int var12 = 0;
         Object var13 = var27.rememberedValue();
         int var14 = 0;
         Object var28;
         if (!var11 && var13 != Composer.Companion.getEmpty()) {
            var28 = var13;
         } else {
            int var15 = 0;
            final android.content.Context recoveredExpressionCapture_863_0 = var5;
            Object var16 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsComponent$lambda$1$lambda$0(recoveredExpressionCapture_863_0));
            var27.updateRememberedValue(var16);
            var28 = var16;
         }

         Function0 var25 = (Function0)var28;
         var17.endReplaceGroup();
         ButtonKt.OutlinedButton(
            var25,
            var0,
            false,
            null,
            var18,
            null,
            var19,
            var21,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$BrowseAllHelpTopicsComponentKt.INSTANCE.recovered$b26556cd5c386b55(),
            var17,
            817889280 | 112 & var4 << 3,
            300
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var29 = var17.endRestartGroup();
      if (var29 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_864_0 = var0;
         final int recoveredExpressionCapture_864_1 = var2;
         final int recoveredExpressionCapture_864_2 = var3;
         var29.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_864_0, recoveredExpressionArgument_864_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsComponent$lambda$2(recoveredExpressionCapture_864_0, recoveredExpressionCapture_864_1, recoveredExpressionCapture_864_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_864_0), ((java.lang.Integer)recoveredExpressionArgument_864_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BrowseAllHelpTopicsAsItem(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      Composer var96 = var1.startRestartGroup(-384199751);
      ComposerKt.sourceInformation(var96, "C(BrowseAllHelpTopicsAsItem)68@2682L7,72@2781L225,69@2694L897:BrowseAllHelpTopicsComponent.kt#ngglre");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var96.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var96.getSkipping()) {
         var96.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -384199751, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsAsItem (BrowseAllHelpTopicsComponent.kt:67)"
            );
         }

         CompositionLocal var6 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var96, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var96.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var96);
         Context var5 = (Context)var9;
         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var96.startReplaceGroup(1227874750);
         ComposerKt.sourceInformation(var96, "CC(remember):BrowseAllHelpTopicsComponent.kt#9igjgp");
         Composer var102 = var96;
         boolean var106 = var96.changedInstance(var5);
         int var10 = 0;
         Object var11 = var102.rememberedValue();
         int var12 = 0;
         Object var10005;
         if (!var106 && var11 != Composer.Companion.getEmpty()) {
            var10005 = var11;
         } else {
            Object var94 = null;
            Object var93 = null;
            Object var92 = null;
            boolean var91 = false;
            Modifier var90 = var10000;
            int var13 = 0;
            final android.content.Context recoveredExpressionCapture_862_0 = var5;
            Function0 var95 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsAsItem$lambda$4$lambda$3(recoveredExpressionCapture_862_0));
            var10000 = var90;
            var10001 = var91;
            var10002 = var92;
            var10003 = var93;
            var10004 = var94;
            Object var14 = var95;
            var102.updateRememberedValue(var14);
            var10005 = var14;
         }

         Function0 var99 = (Function0)var10005;
         var96.endReplaceGroup();
         Modifier var97 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var99, 15, null
         );
         int var103 = 16;
         int var107 = 0;
         float var100 = Dp.recovered$95b3d103663bf060(var103);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var104 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         Modifier var98 = PaddingKt.recovered$03b481d31d62885a(var97, var104, var100, 0.0F, 0.0F, 12, null);
         byte var109 = 0;
         int var111 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var96, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var101 = Arrangement.INSTANCE.getTop();
         Horizontal var105 = Alignment.Companion.getStart();
         MeasurePolicy var112 = ColumnKt.columnMeasurePolicy(var101, var105, var96, 14 & var109 >> 3 | 112 & var109 >> 3);
         Modifier var113 = var98;
         MeasurePolicy var114 = var112;
         Composer var115 = var96;
         int var15 = 112 & var109 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var115, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var115, 0));
         CompositionLocalMap var18 = var115.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var115, var113);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var115;
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
         Updater.recovered$89682b199a142880(var24, var114, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var109 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -191339097, "C81@3073L512:BrowseAllHelpTopicsComponent.kt#ngglre");
         androidx.compose.ui.Alignment.Vertical var33 = Alignment.Companion.getCenterVertically();
         short var34 = 384;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var36 = Modifier.Companion;
         androidx.compose.foundation.layout.Arrangement.Horizontal var37 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var37, var33, var30, 14 & var34 >> 3 | 112 & var34 >> 3);
         Modifier var39 = var36;
         MeasurePolicy var40 = measurePolicy$ivx;
         Composer var41 = var30;
         int var42 = 112 & var34 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var41, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var41, 0));
         CompositionLocalMap localMap$iv$ivx = var41.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var41, var39);
         Function0 var47 = ComposeUiNode.Companion.getConstructor();
         Composer var48 = var41;
         int var49 = 6 | 896 & var42 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var48, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var48.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var48.startReusableNode();
         if (var48.getInserting()) {
            var48.createNode(var47);
         } else {
            var48.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var48);
         int var52/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var40, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var53 = 14 & var49 >> 6;
         Composer $composer$ivx = var48;
         int var55 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var56 = 6 | 112 & var34 >> 6;
         Composer var57 = $composer$ivx;
         RowScope var58 = RowScopeInstance.INSTANCE;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart(var57, -3331477, "C84@3162L336,93@3511L64:BrowseAllHelpTopicsComponent.kt#ngglre");
         Modifier var60 = RowScope.weight$default(var58, Modifier.Companion, 1.0F, false, 2, null);
         byte var61 = 0;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var57, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, horizontalAlignment$ivx, var57, 14 & var61 >> 3 | 112 & var61 >> 3
         );
         Modifier var66 = var60;
         MeasurePolicy var67 = measurePolicy$ivxx;
         Composer var68 = var57;
         int var69 = 112 & var61 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var68, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var68, 0));
         CompositionLocalMap localMap$iv$ivxx = var68.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var68, var66);
         Function0 var74 = ComposeUiNode.Companion.getConstructor();
         Composer var75 = var68;
         int var76 = 6 | 896 & var69 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var75, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var75.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var75.startReusableNode();
         if (var75.getInserting()) {
            var75.createNode(var74);
         } else {
            var75.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var75);
         int var79/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var67, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var80 = 14 & var76 >> 6;
         Composer $composer$ivxx = var75;
         int var82/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var83 = 6 | 112 & var61 >> 6;
         Composer var84 = $composer$ivxx;
         ColumnScope var85 = ColumnScopeInstance.INSTANCE;
         int var86 = 0;
         ComposerKt.sourceInformationMarkerStart(var84, 1605376031, "C88@3283L61,89@3388L10,87@3250L234:BrowseAllHelpTopicsComponent.kt#ngglre");
         String var87 = StringResources_androidKt.stringResource(string.intercom_browse_all_help_topics, var84, 0);
         TextStyle var88 = IntercomTheme.INSTANCE.getTypography(var84, IntercomTheme.$stable).getType04Point5();
         FontWeight var89 = FontWeight.Companion.getSemiBold();
         TextKt.recovered$5b114ab32a4f4190(
            var87, null, 0L, null, 0L, null, var89, null, 0L, null, null, 0L, 0, false, 0, 0, null, var88, var84, 1572864, 0, 131006
         );
         ComposerKt.sourceInformationMarkerEnd(var84);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var75.endNode();
         ComposerKt.sourceInformationMarkerEnd(var75);
         ComposerKt.sourceInformationMarkerEnd(var68);
         ComposerKt.sourceInformationMarkerEnd(var57);
         Modifier var118 = Modifier.Companion;
         int $this$dp$ivxx = 22;
         int $i$f$getDpxx = 0;
         IntercomChevronKt.IntercomChevron(
            PaddingKt.recovered$f3b9f2e94c07c806(var118, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null), var57, 6, 0
         );
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var48.endNode();
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var115);
         ComposerKt.sourceInformationMarkerEnd(var96);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var119 = var96.endRestartGroup();
      if (var119 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_861_0 = var0;
         final int recoveredExpressionCapture_861_1 = var2;
         final int recoveredExpressionCapture_861_2 = var3;
         var119.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_861_0, recoveredExpressionArgument_861_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsAsItem$lambda$8(recoveredExpressionCapture_861_0, recoveredExpressionCapture_861_1, recoveredExpressionCapture_861_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_861_0), ((java.lang.Integer)recoveredExpressionArgument_861_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BrowseAllHelpTopicsComponentPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1843811940);
      ComposerKt.sourceInformation(var2, "C(BrowseAllHelpTopicsComponentPreview)101@3681L60:BrowseAllHelpTopicsComponent.kt#ngglre");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1843811940,
               var1,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentPreview (BrowseAllHelpTopicsComponent.kt:100)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$BrowseAllHelpTopicsComponentKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_865_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_865_0, recoveredExpressionArgument_865_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsComponentPreview$lambda$9(recoveredExpressionCapture_865_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_865_0), ((java.lang.Integer)recoveredExpressionArgument_865_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BrowseAllHelpTopicsASItemPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1268619456);
      ComposerKt.sourceInformation(var2, "C(BrowseAllHelpTopicsASItemPreview)109@3828L57:BrowseAllHelpTopicsComponent.kt#ngglre");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1268619456,
               var1,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsASItemPreview (BrowseAllHelpTopicsComponent.kt:108)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$BrowseAllHelpTopicsComponentKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_860_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_860_0, recoveredExpressionArgument_860_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsASItemPreview$lambda$10(recoveredExpressionCapture_860_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_860_0), ((java.lang.Integer)recoveredExpressionArgument_860_1).intValue())));
      }
   }

   private static final Unit BrowseAllHelpTopicsComponent$lambda$1$lambda$0(Context var0) {
      IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollections(var0, CollectionsKt.emptyList(), "collection_list");
      return Unit.INSTANCE;
   }

   private static final Unit BrowseAllHelpTopicsComponent$lambda$2(Modifier var0, int var1, int var2, Composer var3, int var4) {
      BrowseAllHelpTopicsComponent(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit BrowseAllHelpTopicsAsItem$lambda$4$lambda$3(Context var0) {
      IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollections(var0, CollectionsKt.emptyList(), "collection_list");
      return Unit.INSTANCE;
   }

   private static final Unit BrowseAllHelpTopicsAsItem$lambda$8(Modifier var0, int var1, int var2, Composer var3, int var4) {
      BrowseAllHelpTopicsAsItem(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit BrowseAllHelpTopicsComponentPreview$lambda$9(int var0, Composer var1, int var2) {
      BrowseAllHelpTopicsComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BrowseAllHelpTopicsASItemPreview$lambda$10(int var0, Composer var1, int var2) {
      BrowseAllHelpTopicsASItemPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
