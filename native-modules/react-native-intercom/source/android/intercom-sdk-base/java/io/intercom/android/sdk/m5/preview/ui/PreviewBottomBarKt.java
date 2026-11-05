package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.lazy.LazyListStateKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
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
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u001aA\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001a7\u0010\f\u001a\u00020\u00012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\b2\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003¢\u0006\u0002\u0010\u0011¨\u0006\u0012",
   d2 = {
         "PreviewBottomBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "uiState",
         "Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;",
         "onThumbnailClick",
         "Lkotlin/Function1;",
         "",
         "onCtaClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "ThumbnailList",
         "files",
         "",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "currentPage",
         "(Ljava/util/List;ILkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPreviewBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 7 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 10 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n*L\n1#1,135:1\n118#2:136\n118#2:137\n118#2:138\n118#2:203\n118#2:234\n99#3,6:139\n106#3:207\n81#4,6:145\n88#4,6:160\n81#4,6:177\n88#4,6:192\n96#4:201\n96#4:206\n402#5,9:151\n411#5:166\n402#5,9:183\n411#5,3:198\n412#5,2:204\n70#6:167\n67#6,9:168\n77#6:202\n615#7:208\n612#7,6:209\n1047#8,3:215\n1050#8,3:219\n1047#8,6:222\n1047#8,6:228\n1047#8,6:235\n613#9:218\n204#10,13:241\n*S KotlinDebug\n*F\n+ 1 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt\n*L\n48#1:136\n50#1:137\n52#1:138\n70#1:203\n107#1:234\n45#1:139,6\n45#1:207\n45#1:145,6\n45#1:160,6\n54#1:177,6\n54#1:192,6\n54#1:201\n45#1:206\n45#1:151,9\n45#1:166\n54#1:183,9\n54#1:198,3\n45#1:204,2\n54#1:167\n54#1:168,9\n54#1:202\n89#1:208\n89#1:209,6\n89#1:215,3\n89#1:219,3\n91#1:222,6\n94#1:228,6\n109#1:235,6\n89#1:218\n110#1:241,13\n*E\n"
)
public final class PreviewBottomBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewBottomBar(
      @Nullable Modifier var0, @NotNull PreviewUiState var1, @NotNull Function1 var2, @NotNull Function0 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "uiState");
      Intrinsics.checkNotNullParameter(var2, "onThumbnailClick");
      Intrinsics.checkNotNullParameter(var3, "onCtaClick");
      Composer var62 = var4.startRestartGroup(1265800885);
      ComposerKt.sourceInformation(var62, "C(PreviewBottomBar)P(!1,3,2)44@1845L1204:PreviewBottomBar.kt#hhbc0c");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var62.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var62.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var62.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var62.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var62.getSkipping()) {
         var62.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1265800885, var7, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewBottomBar (PreviewBottomBar.kt:43)");
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         int var9 = 100;
         int var10 = 0;
         Modifier var75 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var9)),
            Color.recovered$41aa0ee03c2282fb(Color.Companion.recovered$235b30c090a399c5(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            null,
            2,
            null
         );
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var8 = PaddingKt.recovered$eb6a744cd98cdbca(var75, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Vertical var64 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         HorizontalOrVertical var66 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         short var67 = 432;
         int var68 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var62, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var13 = RowKt.rowMeasurePolicy(var66, var64, var62, 14 & var67 >> 3 | 112 & var67 >> 3);
         Modifier var14 = var8;
         MeasurePolicy var15 = var13;
         Composer var16 = var62;
         int var17 = 112 & var67 << 3;
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
         int var31 = 6 | 112 & var67 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         var32.startReplaceGroup(374923407);
         ComposerKt.sourceInformation(var32, "C53@2224L347,71@2831L6,73@2942L6,72@2881L89,75@2982L61,68@2701L342:PreviewBottomBar.kt#hhbc0c");
         Modifier var35 = RowScope.weight$default(var33, Modifier.Companion, 1.0F, false, 2, null);
         byte var36 = 0;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var38 = Alignment.Companion.getTopStart();
         boolean var39 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var38, var39);
         Modifier var41 = var35;
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
         int var57 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var58 = 6 | 112 & var36 >> 6;
         Composer var59 = $composer$ivx;
         BoxScope var60 = BoxScopeInstance.INSTANCE;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var59, 1301536620, "C:PreviewBottomBar.kt#hhbc0c");
         var59.startReplaceGroup(-1204940937);
         ComposerKt.sourceInformation(var59, "58@2360L187");
         if (var1.getFiles().size() > 1) {
            ThumbnailList(var1.getFiles(), var1.getCurrentPage(), var2, var59, 896 & var7);
         }

         var59.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var50.endNode();
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var32);
         CharSequence var69 = var1.getConfirmationText();
         if (var69 == null || StringsKt.isBlank(var69)) {
            var32.endReplaceGroup();
         } else {
            Modifier var76 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            Modifier var70 = PaddingKt.recovered$03b481d31d62885a(var76, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxx), 0.0F, 11, null);
            CornerBasedShape var72 = IntercomTheme.INSTANCE.getShapes(var32, IntercomTheme.$stable).getSmall();
            ButtonColors var74 = ButtonDefaults.INSTANCE
               .recovered$6a8c28fb0b42a2fc(
                  IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).recovered$202acdf3dbb5c020(),
                  0L,
                  0L,
                  0L,
                  var32,
                  ButtonDefaults.$stable << 12,
                  14
               );
            ButtonKt.Button(
               var3,
               var70,
               false,
               var72,
               var74,
               null,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-1096022047, true, new PreviewBottomBarKt$PreviewBottomBar$1$2(var1), var32, 54),
               var32,
               805306416 | 14 & var7 >> 9,
               484
            );
            var32.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var62);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var77 = var62.endRestartGroup();
      if (var77 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1137_0 = var0;
         final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1137_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1137_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1137_3 = var3;
         final int recoveredExpressionCapture_1137_4 = var5;
         final int recoveredExpressionCapture_1137_5 = var6;
         var77.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1137_0, recoveredExpressionArgument_1137_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt.PreviewBottomBar$lambda$2(recoveredExpressionCapture_1137_0, recoveredExpressionCapture_1137_1, recoveredExpressionCapture_1137_2, recoveredExpressionCapture_1137_3, recoveredExpressionCapture_1137_4, recoveredExpressionCapture_1137_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1137_0), ((java.lang.Integer)recoveredExpressionArgument_1137_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ThumbnailList(List var0, int var1, Function1 var2, Composer var3, int var4) {
      Composer var31 = var3.startRestartGroup(-1330621562);
      ComposerKt.sourceInformation(
         var31, "C(ThumbnailList)P(1)87@3213L23,88@3253L24,90@3302L42,93@3400L187,91@3349L245,108@3854L892,102@3600L1146:PreviewBottomBar.kt#hhbc0c"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var31.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var31.changed(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var31.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var31.getSkipping()) {
         var31.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1330621562, var5, -1, "io.intercom.android.sdk.m5.preview.ui.ThumbnailList (PreviewBottomBar.kt:86)");
         }

         LazyListState var6 = LazyListStateKt.rememberLazyListState(0, 0, var31, 0, 3);
         boolean var9 = false;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var11 = var31;
         ComposerKt.sourceInformationMarkerStart(var31, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var12 = var31;
         boolean var13 = false;
         int var14 = 0;
         Object var15 = var12.rememberedValue();
         int var16 = 0;
         Object var10000;
         if (var15 == Composer.Companion.getEmpty()) {
            int var17 = 0;
            int var18 = 0;
            Object var61 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var11);
            var12.updateRememberedValue(var61);
            var10000 = var61;
         } else {
            var10000 = var15;
         }

         CoroutineScope var19 = (CoroutineScope)var10000;
         ComposerKt.sourceInformationMarkerEnd(var31);
         CoroutineScope var20 = var19;
         ComposerKt.sourceInformationMarkerEnd(var31);
         CoroutineScope var7 = var20;
         var31.startReplaceGroup(702750354);
         ComposerKt.sourceInformation(var31, "CC(remember):PreviewBottomBar.kt#9igjgp");
         Composer var35 = var31;
         boolean var38 = false;
         int $i$f$cachex = 0;
         Object var44 = var35.rememberedValue();
         int var49 = 0;
         Object var66;
         if (var44 == Composer.Companion.getEmpty()) {
            int var54 = 0;
            Object var55 = SnapshotStateKt.mutableStateOf$default(CollectionsKt.emptyList(), null, 2, null);
            var35.updateRememberedValue(var55);
            var66 = var55;
         } else {
            var66 = var44;
         }

         MutableState var32 = (MutableState)var66;
         var31.endReplaceGroup();
         MutableState var8 = var32;
         String var67 = "";
         var31.startReplaceGroup(702753635);
         ComposerKt.sourceInformation(var31, "CC(remember):PreviewBottomBar.kt#9igjgp");
         Composer var36 = var31;
         boolean var39 = var31.changed(var6);
         int $i$f$cachexx = 0;
         Object it$ivx = var36.rememberedValue();
         int var50/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var71;
         if (!var39 && it$ivx != Composer.Companion.getEmpty()) {
            var71 = it$ivx;
         } else {
            String var21 = "";
            int var56 = 0;
            Function2 var10001 = new PreviewBottomBarKt$ThumbnailList$1$1(var6, var8, null);
            var67 = var21;
            Object var58 = var10001;
            var36.updateRememberedValue(var58);
            var71 = var58;
         }

         Function2 var33 = (Function2)var71;
         var31.endReplaceGroup();
         EffectsKt.LaunchedEffect(var67, var33, var31, 6);
         Companion var34 = Modifier.Companion;
         Horizontal var37 = Arrangement.INSTANCE.getStart();
         Vertical var40 = Alignment.Companion.getCenterVertically();
         int var46 = 8;
         int var51 = 0;
         float var68 = Dp.recovered$95b3d103663bf060(var46);
         int $this$dp$ivx = 4;
         int $i$f$getDpx = 0;
         PaddingValues var43 = PaddingKt.recovered$4ebb4826aa57abc6(var68, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Modifier var69 = var34;
         LazyListState var72 = var6;
         PaddingValues var10002 = var43;
         boolean var10003 = false;
         Horizontal var10004 = var37;
         Vertical var10005 = var40;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var31.startReplaceGroup(702768868);
         ComposerKt.sourceInformation(var31, "CC(remember):PreviewBottomBar.kt#9igjgp");
         Composer var53 = var31;
         boolean var57 = var31.changedInstance(var0) | (var5 & 112) == 32 | (var5 & 896) == 256 | var31.changedInstance(var7) | var31.changed(var6);
         int $i$f$cachexxx = 0;
         Object it$ivxx = var53.rememberedValue();
         int var62/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10009;
         if (!var57 && it$ivxx != Composer.Companion.getEmpty()) {
            var10009 = it$ivxx;
         } else {
            Object var29 = null;
            boolean var28 = false;
            Object var27 = null;
            Vertical var26 = var40;
            Horizontal var25 = var37;
            boolean var24 = false;
            PaddingValues var23 = var43;
            LazyListState var22 = var6;
            Modifier var65 = var69;
            int var63 = 0;
            final java.util.List recoveredExpressionCapture_1138_0 = var0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1138_1 = var8;
            final int recoveredExpressionCapture_1138_2 = var1;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1138_3 = var7;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1138_4 = var2;
            final androidx.compose.foundation.lazy.LazyListState recoveredExpressionCapture_1138_5 = var6;
            Function1 var30 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1138_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt.ThumbnailList$lambda$9$lambda$8(recoveredExpressionCapture_1138_0, recoveredExpressionCapture_1138_1, recoveredExpressionCapture_1138_2, recoveredExpressionCapture_1138_3, recoveredExpressionCapture_1138_4, recoveredExpressionCapture_1138_5, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_1138_0)));
            var69 = var65;
            var72 = var22;
            var10002 = var23;
            var10003 = var24;
            var10004 = var25;
            var10005 = var26;
            var10006 = var27;
            var10007 = var28;
            var10008 = var29;
            Object var64 = var30;
            var53.updateRememberedValue(var64);
            var10009 = var64;
         }

         Function1 var48 = (Function1)var10009;
         var31.endReplaceGroup();
         LazyDslKt.LazyRow(
            var69, var72, var10002, var10003, var10004, var10005, (FlingBehavior)var10006, var10007, (OverscrollEffect)var10008, var48, var31, 221574, 456
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var70 = var31.endRestartGroup();
      if (var70 != null) {
         final java.util.List recoveredExpressionCapture_1139_0 = var0;
         final int recoveredExpressionCapture_1139_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1139_2 = var2;
         final int recoveredExpressionCapture_1139_3 = var4;
         var70.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1139_0, recoveredExpressionArgument_1139_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewBottomBarKt.ThumbnailList$lambda$10(recoveredExpressionCapture_1139_0, recoveredExpressionCapture_1139_1, recoveredExpressionCapture_1139_2, recoveredExpressionCapture_1139_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1139_0), ((java.lang.Integer)recoveredExpressionArgument_1139_1).intValue())));
      }
   }

   private static final Unit PreviewBottomBar$lambda$2(
      Modifier var0, PreviewUiState var1, Function1 var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      PreviewBottomBar(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit ThumbnailList$lambda$9$lambda$8(
      List var0, MutableState var1, int var2, CoroutineScope var3, Function1 var4, LazyListState var5, LazyListScope var6
   ) {
      Intrinsics.checkNotNullParameter(var6, "$this$LazyRow");
      LazyListScope var7 = var6;
      Function2 var8 = null;
      int var9 = 0;
      var7.items(
         var0.size(),
         null,
         new PreviewBottomBarKt$ThumbnailList$lambda$9$lambda$8$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$2(var0),
         ComposableLambdaKt.composableLambdaInstance(
            2039820996,
            true,
            new PreviewBottomBarKt$ThumbnailList$lambda$9$lambda$8$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
               var0, var2, var4
            )
         )
      );
      if (!((List)var1.getValue()).contains(var2)) {
         BuildersKt.launch$default(var3, null, null, new PreviewBottomBarKt$ThumbnailList$2$1$2(var5, var2, null), 3, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ThumbnailList$lambda$10(List var0, int var1, Function1 var2, int var3, Composer var4, int var5) {
      ThumbnailList(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }
}
