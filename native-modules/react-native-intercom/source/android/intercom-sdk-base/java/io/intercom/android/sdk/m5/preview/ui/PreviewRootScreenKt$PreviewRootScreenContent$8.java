package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.os.Build.VERSION;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.pager.PagerKt;
import androidx.compose.foundation.pager.PagerState;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.LayoutDirection;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPreviewRootScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt$PreviewRootScreenContent$8\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,189:1\n75#2:190\n75#2:191\n87#3:192\n84#3,9:193\n94#3:263\n81#4,6:202\n88#4,6:217\n96#4:262\n402#5,9:208\n411#5:223\n412#5,2:260\n1047#6,6:224\n1047#6,6:230\n1047#6,6:236\n1047#6,6:242\n1047#6,6:248\n1047#6,6:254\n1563#7:264\n1634#7,3:265\n1563#7:268\n1634#7,3:269\n1563#7:272\n1634#7,3:273\n*S KotlinDebug\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt$PreviewRootScreenContent$8\n*L\n121#1:190\n122#1:191\n118#1:192\n118#1:193,9\n118#1:263\n118#1:202,6\n118#1:217,6\n118#1:262\n118#1:208,9\n118#1:223\n118#1:260,2\n132#1:224,6\n133#1:230,6\n134#1:236,6\n135#1:242,6\n171#1:248,6\n170#1:254,6\n132#1:264\n132#1:265,3\n134#1:268\n134#1:269,3\n170#1:272\n170#1:273,3\n*E\n"
)
final class PreviewRootScreenKt$PreviewRootScreenContent$8 implements Function3 {
   // $VF: synthetic field
   final PreviewUiState $state;
   // $VF: synthetic field
   final Function1 $onBackCLick;
   // $VF: synthetic field
   final Function1 $onDeleteClick;
   // $VF: synthetic field
   final Function1 $onSendClick;
   // $VF: synthetic field
   final Function0 $onDownloadClick;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final ManagedActivityResultLauncher $permissionLauncher;
   // $VF: synthetic field
   final PagerState $pagerState;
   // $VF: synthetic field
   final CoroutineScope $scope;
   // $VF: synthetic field
   final MutableState $isCurrentPageZoomed$delegate;

   PreviewRootScreenKt$PreviewRootScreenContent$8(
      PreviewUiState var1,
      Function1 var2,
      Function1 var3,
      Function1 var4,
      Function0 var5,
      Context var6,
      ManagedActivityResultLauncher var7,
      PagerState var8,
      CoroutineScope var9,
      MutableState var10
   ) {
      this.$state = var1;
      this.$onBackCLick = var2;
      this.$onDeleteClick = var3;
      this.$onSendClick = var4;
      this.$onDownloadClick = var5;
      this.$context = var6;
      this.$permissionLauncher = var7;
      this.$pagerState = var8;
      this.$scope = var9;
      this.$isCurrentPageZoomed$delegate = var10;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "it");
      ComposerKt.sourceInformation(var2, "C120@4378L7,121@4458L7,117@4238L2534:PreviewRootScreen.kt#hhbc0c");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               612610579, var4, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenContent.<anonymous> (PreviewRootScreen.kt:117)"
            );
         }

         Companion var5 = Modifier.Companion;
         CompositionLocal var7 = CompositionLocalsKt.getLocalLayoutDirection();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         float var6 = PaddingKt.calculateStartPadding(var1, (LayoutDirection)var10);
         CompositionLocal var63 = CompositionLocalsKt.getLocalLayoutDirection();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var2.consume(var63);
         ComposerKt.sourceInformationMarkerEnd(var2);
         float var61 = PaddingKt.calculateEndPadding(var1, (LayoutDirection)var11);
         float var64 = var1.recovered$00be2fd8b9cc26a4();
         float var67 = var1.recovered$7be19aa93c4abc66();
         Modifier var59 = SizeKt.fillMaxSize$default(PaddingKt.recovered$f39cb96e94c47cf6(var5, var6, var64, var61, var67), 0.0F, 1, null);
         PreviewUiState var65 = this.$state;
         Function1 var68 = this.$onBackCLick;
         Function1 var70 = this.$onDeleteClick;
         Function1 var71 = this.$onSendClick;
         Function0 var12 = this.$onDownloadClick;
         Context var13 = this.$context;
         ManagedActivityResultLauncher var14 = this.$permissionLauncher;
         PagerState var15 = this.$pagerState;
         CoroutineScope var16 = this.$scope;
         MutableState var17 = this.$isCurrentPageZoomed$delegate;
         int $changed$ivxx = 0;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var60 = Arrangement.INSTANCE.getTop();
         Horizontal var62 = Alignment.Companion.getStart();
         MeasurePolicy var20 = ColumnKt.columnMeasurePolicy(var60, var62, var2, 14 & $changed$ivxx >> 3 | 112 & $changed$ivxx >> 3);
         Modifier var21 = var59;
         MeasurePolicy var22 = var20;
         Composer var23 = var2;
         int var24 = 112 & $changed$ivxx << 3;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var26 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var23, 0));
         CompositionLocalMap var27 = var23.getCurrentCompositionLocalMap();
         Modifier var28 = ComposedModifierKt.materializeModifier(var23, var21);
         Function0 var29 = ComposeUiNode.Companion.getConstructor();
         Composer var30 = var23;
         int var31 = 6 | 896 & var24 << 6;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var30.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var30.startReusableNode();
         if (var30.getInserting()) {
            var30.createNode(var29);
         } else {
            var30.useNode();
         }

         Composer var33 = Updater.recovered$676aa17b44fd6280(var30);
         int var34 = 0;
         Updater.recovered$89682b199a142880(var33, var22, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var33, var27, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var33, var26, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var33, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var33, var28, ComposeUiNode.Companion.getSetModifier());
         int var35 = 14 & var31 >> 6;
         Composer var36 = var30;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var38 = 6 | 112 & $changed$ivxx >> 6;
         Composer var39 = var36;
         ColumnScope var40 = ColumnScopeInstance.INSTANCE;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var39, 656460322, "C131@4874L43,132@4951L49,133@5032L43,134@5111L548,127@4652L1022,152@5881L480,147@5687L674:PreviewRootScreen.kt#hhbc0c"
         );
         Object var10000 = null;
         boolean var10001 = var65.getShowDeleteAction();
         boolean var10002 = var65.getShowSendAction();
         boolean var10003 = var65.getShowDownloadAction();
         var39.startReplaceGroup(1822296494);
         ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var42 = var39;
         boolean var43 = var39.changed(var68) | var39.changedInstance(var65);
         int var44 = 0;
         Object var45 = var42.rememberedValue();
         int var46 = 0;
         Object var10004;
         if (!var43 && var45 != Composer.Companion.getEmpty()) {
            var10004 = var45;
         } else {
            boolean var47 = var10003;
            boolean var48 = var10002;
            boolean var49 = var10001;
            Object var50 = null;
            int var51 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1154_0 = var68;
            final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1154_1 = var65;
            Function0 var52 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$2$lambda$1(recoveredExpressionCapture_1154_0, recoveredExpressionCapture_1154_1));
            var10000 = var50;
            var10001 = var49;
            var10002 = var48;
            var10003 = var47;
            Object var53 = var52;
            var42.updateRememberedValue(var53);
            var10004 = var53;
         }

         Function0 var54 = (Function0)var10004;
         var39.endReplaceGroup();
         Function0 var142 = var54;
         var39.startReplaceGroup(1822298964);
         ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var72 = var39;
         boolean var77 = var39.changed(var70) | var39.changedInstance(var65);
         int $i$f$cachex = 0;
         Object it$ivx = var72.rememberedValue();
         int var92/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10005;
         if (!var77 && it$ivx != Composer.Companion.getEmpty()) {
            var10005 = it$ivx;
         } else {
            Function0 var121 = var54;
            boolean var97 = var10003;
            boolean var101 = var10002;
            boolean var106 = var10001;
            Object var111 = var10000;
            int var116 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1155_0 = var70;
            final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1155_1 = var65;
            Function0 var55 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$4$lambda$3(recoveredExpressionCapture_1155_0, recoveredExpressionCapture_1155_1));
            var10000 = var111;
            var10001 = var106;
            var10002 = var101;
            var10003 = var97;
            var142 = var121;
            Object var124 = var55;
            var72.updateRememberedValue(var124);
            var10005 = var124;
         }

         Function0 var129 = (Function0)var10005;
         var39.endReplaceGroup();
         Function0 var143 = var129;
         var39.startReplaceGroup(1822301550);
         ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var73 = var39;
         boolean var78 = var39.changed(var71) | var39.changedInstance(var65);
         int $i$f$cachexx = 0;
         Object it$ivxx = var73.rememberedValue();
         int var93/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10006;
         if (!var78 && it$ivxx != Composer.Companion.getEmpty()) {
            var10006 = it$ivxx;
         } else {
            Function0 var133 = var129;
            Function0 var122 = var142;
            boolean var98 = var10003;
            boolean var102 = var10002;
            boolean var107 = var10001;
            Object var112 = var10000;
            int var117 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1156_0 = var71;
            final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1156_1 = var65;
            Function0 var56 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$7$lambda$6(recoveredExpressionCapture_1156_0, recoveredExpressionCapture_1156_1));
            var10000 = var112;
            var10001 = var107;
            var10002 = var102;
            var10003 = var98;
            var142 = var122;
            var143 = var133;
            Object var125 = var56;
            var73.updateRememberedValue(var125);
            var10006 = var125;
         }

         Function0 var130 = (Function0)var10006;
         var39.endReplaceGroup();
         Function0 var144 = var130;
         var39.startReplaceGroup(1822304583);
         ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var74 = var39;
         boolean var79 = var39.changed(var12) | var39.changedInstance(var13) | var39.changedInstance(var14);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var74.rememberedValue();
         int var94/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10007;
         if (!var79 && it$ivxxx != Composer.Companion.getEmpty()) {
            var10007 = it$ivxxx;
         } else {
            Function0 var135 = var130;
            Function0 var134 = var143;
            Function0 var123 = var142;
            boolean var99 = var10003;
            boolean var103 = var10002;
            boolean var108 = var10001;
            Object var113 = var10000;
            int var118 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1157_0 = var12;
            final android.content.Context recoveredExpressionCapture_1157_1 = var13;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_1157_2 = var14;
            Function0 var57 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$9$lambda$8(recoveredExpressionCapture_1157_0, recoveredExpressionCapture_1157_1, recoveredExpressionCapture_1157_2));
            var10000 = var113;
            var10001 = var108;
            var10002 = var103;
            var10003 = var99;
            var142 = var123;
            var143 = var134;
            var144 = var135;
            Object var126 = var57;
            var74.updateRememberedValue(var126);
            var10007 = var126;
         }

         label123: {
            Function0 var131 = (Function0)var10007;
            var39.endReplaceGroup();
            PreviewTopBarKt.PreviewTopBar((Modifier)var10000, var10001, var10002, var10003, var142, var143, var144, var131, var39, 0, 1);
            PagerKt.recovered$954d15abae2deb4b(
               var15,
               ColumnScope.weight$default(var40, Modifier.Companion, 1.0F, false, 2, null),
               null,
               null,
               0,
               0.0F,
               null,
               null,
               !PreviewRootScreenKt.access$PreviewRootScreenContent$lambda$17(var17),
               false,
               null,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  570504714, true, new PreviewRootScreenKt$PreviewRootScreenContent$8$1$5(var65, var15, var17), var39, 54
               ),
               var39,
               0,
               24576,
               16124
            );
            var39.startReplaceGroup(1822344839);
            ComposerKt.sourceInformation(var39, "170@6674L55,169@6590L43,166@6461L287");
            if (var65.getFiles().size() <= 1) {
               CharSequence var132 = var65.getConfirmationText();
               if (var132 == null || StringsKt.isBlank(var132)) {
                  break label123;
               }
            }

            Modifier var137 = Modifier.Companion;
            PreviewUiState var138 = var65;
            var39.startReplaceGroup(1822354106);
            ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var80 = var39;
            boolean var85 = var39.changedInstance(var16) | var39.changed(var15);
            int $i$f$cachexxxx = 0;
            Object it$ivxxxx = var80.rememberedValue();
            int var119/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
            Object var139;
            if (!var85 && it$ivxxxx != Composer.Companion.getEmpty()) {
               var139 = it$ivxxxx;
            } else {
               PreviewUiState var109 = var65;
               Modifier var114 = var137;
               int var127 = 0;
               final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1158_0 = var16;
               final androidx.compose.foundation.pager.PagerState recoveredExpressionCapture_1158_1 = var15;
               Function1 var104 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1158_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$11$lambda$10(recoveredExpressionCapture_1158_0, recoveredExpressionCapture_1158_1, ((java.lang.Integer)recoveredExpressionArgument_1158_0).intValue()));
               var137 = var114;
               var138 = var109;
               Object var58 = var104;
               var80.updateRememberedValue(var58);
               var139 = var58;
            }

            Function1 var75 = (Function1)var139;
            var39.endReplaceGroup();
            Function1 var140 = var75;
            var39.startReplaceGroup(1822351406);
            ComposerKt.sourceInformation(var39, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var81 = var39;
            boolean var86 = var39.changed(var71) | var39.changedInstance(var65);
            int $i$f$cachexxxxx = 0;
            Object it$ivxxxxx = var81.rememberedValue();
            int var120/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
            Object var141;
            if (!var86 && it$ivxxxxx != Composer.Companion.getEmpty()) {
               var141 = it$ivxxxxx;
            } else {
               Function1 var105 = var75;
               PreviewUiState var110 = var138;
               Modifier var115 = var137;
               int var128 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1153_0 = var71;
               final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1153_1 = var65;
               Function0 var100 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt$PreviewRootScreenContent$8.invoke$lambda$15$lambda$14$lambda$13(recoveredExpressionCapture_1153_0, recoveredExpressionCapture_1153_1));
               var137 = var115;
               var138 = var110;
               var140 = var105;
               Object var136 = var100;
               var81.updateRememberedValue(var136);
               var141 = var136;
            }

            Function0 var76 = (Function0)var141;
            var39.endReplaceGroup();
            PreviewBottomBarKt.PreviewBottomBar(var137, var138, var140, var76, var39, 6, 0);
         }

         var39.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var36);
         var30.endNode();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$15$lambda$2$lambda$1(Function1 var0, PreviewUiState var1) {
      Iterable var2 = var1.getFiles();
      Function1 var11 = var0;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         IntercomPreviewFile var9 = (IntercomPreviewFile)var8;
         Collection var12 = var5;
         int var10 = 0;
         var12.add(var9.getUri());
      }

      var11.invoke((List)var5);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$4$lambda$3(Function1 var0, PreviewUiState var1) {
      var0.invoke(var1.getFiles().get(var1.getCurrentPage()));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$7$lambda$6(Function1 var0, PreviewUiState var1) {
      Iterable var2 = var1.getFiles();
      Function1 var11 = var0;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         IntercomPreviewFile var9 = (IntercomPreviewFile)var8;
         Collection var12 = var5;
         int var10 = 0;
         var12.add(var9.getUri());
      }

      var11.invoke((List)var5);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$9$lambda$8(Function0 var0, Context var1, ManagedActivityResultLauncher var2) {
      if (VERSION.SDK_INT >= 29) {
         var0.invoke();
      } else {
         int var3 = ContextCompat.checkSelfPermission(var1, "android.permission.WRITE_EXTERNAL_STORAGE");
         if (var3 == 0) {
            var0.invoke();
         } else {
            var2.launch("android.permission.WRITE_EXTERNAL_STORAGE");
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$11$lambda$10(CoroutineScope var0, PagerState var1, int var2) {
      BuildersKt.launch$default(var0, null, null, new PreviewRootScreenKt$PreviewRootScreenContent$8$1$6$1$1(var1, var2, null), 3, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$14$lambda$13(Function1 var0, PreviewUiState var1) {
      Iterable var2 = var1.getFiles();
      Function1 var11 = var0;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         IntercomPreviewFile var9 = (IntercomPreviewFile)var8;
         Collection var12 = var5;
         int var10 = 0;
         var12.add(var9.getUri());
      }

      var11.invoke((List)var5);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
