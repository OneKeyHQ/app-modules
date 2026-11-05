package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.foundation.shape.CornerSize;
import androidx.compose.foundation.shape.CornerSizeKt;
import androidx.compose.material3.DividerKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aI\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001aQ\u0010\f\u001a\u00020\u0001*\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\u0015H\u0003¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001a\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018¨\u0006\u001c²\u0006\f\u0010\u000e\u001a\u0004\u0018\u00010\u0007X\u008a\u008e\u0002",
   d2 = {
         "BooleanAttributeCollector",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "attributeData",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "hasErrors",
         "",
         "loading",
         "onSubmitAttribute",
         "Lkotlin/Function1;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/AttributeData;ZZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "BooleanAttributeCollectorOption",
         "Landroidx/compose/foundation/layout/RowScope;",
         "value",
         "yesOption",
         "shape",
         "Landroidx/compose/foundation/shape/CornerBasedShape;",
         "disabled",
         "submitted",
         "onClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/foundation/layout/RowScope;Ljava/lang/Boolean;ZLandroidx/compose/foundation/shape/CornerBasedShape;ZZZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "BooleanAttributePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SubmittedBooleanAttributePreview",
         "DisabledBooleanAttributePreview",
         "SubmittedAndDisabledBooleanAttributePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBooleanAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BooleanAttributeCollector.kt\nio/intercom/android/sdk/views/compose/BooleanAttributeCollectorKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,263:1\n1047#2,6:264\n1047#2,6:270\n1047#2,6:306\n1047#2,6:312\n118#3:276\n118#3:277\n118#3:322\n118#3:351\n118#3:352\n118#3:353\n99#4,6:278\n106#4:321\n99#4,6:323\n106#4:357\n81#5,6:284\n88#5,6:299\n96#5:320\n81#5,6:329\n88#5,6:344\n96#5:356\n402#6,9:290\n411#6:305\n412#6,2:318\n402#6,9:335\n411#6:350\n412#6,2:354\n85#7:358\n117#7,2:359\n*S KotlinDebug\n*F\n+ 1 BooleanAttributeCollector.kt\nio/intercom/android/sdk/views/compose/BooleanAttributeCollectorKt\n*L\n47#1:264,6\n52#1:270,6\n86#1:306,6\n104#1:312,6\n63#1:276\n70#1:277\n123#1:322\n151#1:351\n152#1:352\n154#1:353\n66#1:278,6\n66#1:321\n133#1:323,6\n133#1:357\n66#1:284,6\n66#1:299,6\n66#1:320\n133#1:329,6\n133#1:344,6\n133#1:356\n66#1:290,9\n66#1:305\n66#1:318,2\n133#1:335,9\n133#1:350\n133#1:354,2\n52#1:358\n52#1:359,2\n*E\n"
)
public final class BooleanAttributeCollectorKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BooleanAttributeCollector(
      @Nullable Modifier var0, @NotNull AttributeData var1, boolean var2, boolean var3, @Nullable Function1 var4, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "attributeData");
      Composer var62 = var5.startRestartGroup(2100686120);
      ComposerKt.sourceInformation(
         var62, "C(BooleanAttributeCollector)P(3)46@2035L2,51@2180L210,51@2163L227,61@2428L6,63@2508L6,65@2526L1442:BooleanAttributeCollector.kt#h0e4f2"
      );
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var62.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var62.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var62.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var62.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var62.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var62.getSkipping()) {
         var62.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 4) != 0) {
            var2 = false;
         }

         if ((var7 & 8) != 0) {
            var3 = false;
         }

         if ((var7 & 16) != 0) {
            var62.startReplaceGroup(-1684918389);
            ComposerKt.sourceInformation(var62, "CC(remember):BooleanAttributeCollector.kt#9igjgp");
            Composer var10 = var62;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var71 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1634_0) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollector$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.AttributeData) ((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredExpressionArgument_1634_0)));
               var10.updateRememberedValue(var71);
               var10000 = var71;
            } else {
               var10000 = var13;
            }

            Function1 var9 = (Function1)var10000;
            var62.endReplaceGroup();
            var4 = var9;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2100686120, var8, -1, "io.intercom.android.sdk.views.compose.BooleanAttributeCollector (BooleanAttributeCollector.kt:47)"
            );
         }

         boolean var63 = var1.isFormDisabled();
         boolean var64 = var1.getAttribute().getSubmitted();
         Object[] var100 = new Object[0];
         var62.startReplaceGroup(-1684913541);
         ComposerKt.sourceInformation(var62, "CC(remember):BooleanAttributeCollector.kt#9igjgp");
         Composer var68 = var62;
         boolean var69 = var62.changed(var64) | var62.changed(var63) | var62.changedInstance(var1);
         int var72 = 0;
         Object var16 = var68.rememberedValue();
         int var17 = 0;
         Object var105;
         if (!var69 && var16 != Composer.Companion.getEmpty()) {
            var105 = var16;
         } else {
            Object[] var61 = var100;
            int var18 = 0;
            final boolean recoveredExpressionCapture_1635_0 = var64;
            final boolean recoveredExpressionCapture_1635_1 = var63;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1635_2 = var1;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollector$lambda$3$lambda$2(recoveredExpressionCapture_1635_0, recoveredExpressionCapture_1635_1, recoveredExpressionCapture_1635_2));
            var100 = var61;
            Object var19 = var10001;
            var68.updateRememberedValue(var19);
            var105 = var19;
         }

         Function0 var66 = (Function0)var105;
         var62.endReplaceGroup();
         MutableState var65 = (MutableState)RememberSaveableKt.rememberSaveable(var100, var66, var62, 0);
         long var67 = IntercomTheme.INSTANCE.getColors(var62, IntercomTheme.$stable).recovered$a35e0f0c0a5b2483();
         int var73 = 1;
         int var75 = 0;
         float var70 = Dp.recovered$95b3d103663bf060(var73);
         CornerBasedShape var74 = IntercomTheme.INSTANCE.getShapes(var62, IntercomTheme.$stable).getSmall();
         Modifier var101 = SizeKt.fillMaxWidth$default(ClipKt.clip(var0, var74), 0.0F, 1, null);
         int $this$dp$ivx = 40;
         int $i$f$getDpx = 0;
         Modifier var77 = BorderKt.recovered$1227359600bc50fd(
            SizeKt.recovered$3f6c6a76aa292717(var101, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var70, var67, var74
         );
         Horizontal var79 = Arrangement.INSTANCE.getSpaceEvenly();
         Vertical var80 = Alignment.Companion.getCenterVertically();
         short var81 = 432;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var62, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var21 = RowKt.rowMeasurePolicy(var79, var80, var62, 14 & var81 >> 3 | 112 & var81 >> 3);
         Modifier var22 = var77;
         MeasurePolicy var23 = var21;
         Composer var24 = var62;
         int var25 = 112 & var81 << 3;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var27 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var24, 0));
         CompositionLocalMap var28 = var24.getCurrentCompositionLocalMap();
         Modifier var29 = ComposedModifierKt.materializeModifier(var24, var22);
         Function0 var30 = ComposeUiNode.Companion.getConstructor();
         Composer var31 = var24;
         int var32 = 6 | 896 & var25 << 6;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var31.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var31.startReusableNode();
         if (var31.getInserting()) {
            var31.createNode(var30);
         } else {
            var31.useNode();
         }

         Composer var34 = Updater.recovered$676aa17b44fd6280(var31);
         int var35 = 0;
         Updater.recovered$89682b199a142880(var34, var23, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var34, var28, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var34, var27, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var34, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var34, var29, ComposeUiNode.Companion.getSetModifier());
         int var36 = 14 & var32 >> 6;
         Composer var37 = var31;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var39 = 6 | 112 & var81 >> 6;
         Composer var40 = var37;
         RowScope var41 = RowScopeInstance.INSTANCE;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var40, 1872183102, "C85@3177L175,78@2909L443,91@3361L145,103@3785L177,96@3515L447:BooleanAttributeCollector.kt#h0e4f2"
         );
         Boolean var43 = var2 ? null : BooleanAttributeCollector$lambda$4(var65);
         boolean var44 = var3 && Intrinsics.areEqual(BooleanAttributeCollector$lambda$4(var65), true);
         RowScope var102 = var41;
         Boolean var106 = var43;
         boolean var10002 = true;
         CornerBasedShape var10003 = var74;
         boolean var10004 = var63;
         boolean var10005 = var64;
         boolean var10006 = var44;
         var40.startReplaceGroup(2000063351);
         ComposerKt.sourceInformation(var40, "CC(remember):BooleanAttributeCollector.kt#9igjgp");
         Composer var45 = var40;
         boolean var46 = var62.changed(var65) | (var8 & 57344) == 16384 | var62.changedInstance(var1);
         int $i$f$cachex = 0;
         Object it$ivx = var45.rememberedValue();
         int var49/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10007;
         if (!var46 && it$ivx != Composer.Companion.getEmpty()) {
            var10007 = it$ivx;
         } else {
            boolean var50 = var44;
            boolean var51 = var64;
            boolean var52 = var63;
            CornerBasedShape var53 = var74;
            boolean var54 = true;
            Boolean var55 = var43;
            RowScope var56 = var41;
            int var57 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1636_0 = var4;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1636_1 = var1;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1636_2 = var65;
            Function0 var58 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollector$lambda$10$lambda$7$lambda$6(recoveredExpressionCapture_1636_0, recoveredExpressionCapture_1636_1, recoveredExpressionCapture_1636_2));
            var102 = var56;
            var106 = var55;
            var10002 = var54;
            var10003 = var53;
            var10004 = var52;
            var10005 = var51;
            var10006 = var50;
            Object var59 = var58;
            var45.updateRememberedValue(var59);
            var10007 = var59;
         }

         Function0 var60 = (Function0)var10007;
         var40.endReplaceGroup();
         BooleanAttributeCollectorOption(var102, var106, var10002, var10003, var10004, var10005, var10006, var60, var40, 384 | 14 & var39);
         DividerKt.recovered$de200ab17b12aad5(SizeKt.fillMaxHeight$default(Modifier.Companion, 0.0F, 1, null), var70, var67, var40, 54, 0);
         Boolean var82 = var2 ? null : BooleanAttributeCollector$lambda$4(var65);
         boolean var83 = var3 && Intrinsics.areEqual(BooleanAttributeCollector$lambda$4(var65), false);
         RowScope var103 = var41;
         Boolean var107 = var82;
         boolean var108 = false;
         CornerBasedShape var109 = var74;
         boolean var110 = var63;
         boolean var111 = var64;
         boolean var112 = var83;
         var40.startReplaceGroup(2000082809);
         ComposerKt.sourceInformation(var40, "CC(remember):BooleanAttributeCollector.kt#9igjgp");
         Composer var84 = var40;
         boolean var85 = var62.changed(var65) | (var8 & 57344) == 16384 | var62.changedInstance(var1);
         int $i$f$cachexx = 0;
         Object it$ivxx = var84.rememberedValue();
         int var88/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var113;
         if (!var85 && it$ivxx != Composer.Companion.getEmpty()) {
            var113 = it$ivxx;
         } else {
            boolean var89 = var83;
            boolean var90 = var64;
            boolean var91 = var63;
            CornerBasedShape var92 = var74;
            boolean var93 = false;
            Boolean var94 = var82;
            RowScope var95 = var41;
            int var96 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1632_0 = var4;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1632_1 = var1;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1632_2 = var65;
            Function0 var97 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollector$lambda$10$lambda$9$lambda$8(recoveredExpressionCapture_1632_0, recoveredExpressionCapture_1632_1, recoveredExpressionCapture_1632_2));
            var103 = var95;
            var107 = var94;
            var108 = var93;
            var109 = var92;
            var110 = var91;
            var111 = var90;
            var112 = var89;
            Object var98 = var97;
            var84.updateRememberedValue(var98);
            var113 = var98;
         }

         Function0 var99 = (Function0)var113;
         var40.endReplaceGroup();
         BooleanAttributeCollectorOption(var103, var107, var108, var109, var110, var111, var112, var99, var40, 384 | 14 & var39);
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var37);
         var31.endNode();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var62);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var104 = var62.endRestartGroup();
      if (var104 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1633_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1633_1 = var1;
         final boolean recoveredExpressionCapture_1633_2 = var2;
         final boolean recoveredExpressionCapture_1633_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1633_4 = var4;
         final int recoveredExpressionCapture_1633_5 = var6;
         final int recoveredExpressionCapture_1633_6 = var7;
         var104.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1633_0, recoveredExpressionArgument_1633_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollector$lambda$11(recoveredExpressionCapture_1633_0, recoveredExpressionCapture_1633_1, recoveredExpressionCapture_1633_2, recoveredExpressionCapture_1633_3, recoveredExpressionCapture_1633_4, recoveredExpressionCapture_1633_5, recoveredExpressionCapture_1633_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1633_0), ((java.lang.Integer)recoveredExpressionArgument_1633_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BooleanAttributeCollectorOption(
      RowScope var0, Boolean var1, boolean var2, CornerBasedShape var3, boolean var4, boolean var5, boolean var6, Function0 var7, Composer var8, int var9
   ) {
      Composer var56 = var8.startRestartGroup(1323902640);
      ComposerKt.sourceInformation(
         var56, "C(BooleanAttributeCollectorOption)P(5,6,3!1,4)129@4492L6,130@4555L6,132@4600L1313:BooleanAttributeCollector.kt#h0e4f2"
      );
      int var10 = var9;
      if ((var9 & 6) == 0) {
         var10 |= var56.changed(var0) ? 4 : 2;
      }

      if ((var9 & 48) == 0) {
         var10 |= var56.changed(var1) ? 32 : 16;
      }

      if ((var9 & 384) == 0) {
         var10 |= var56.changed(var2) ? 256 : 128;
      }

      if ((var9 & 3072) == 0) {
         var10 |= var56.changed(var3) ? 2048 : 1024;
      }

      if ((var9 & 24576) == 0) {
         var10 |= var56.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 196608) == 0) {
         var10 |= var56.changed(var5) ? 131072 : 65536;
      }

      if ((var9 & 1572864) == 0) {
         var10 |= var56.changed(var6) ? 1048576 : 524288;
      }

      if ((var9 & 12582912) == 0) {
         var10 |= var56.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var10 & 4793491) == 4793490 && var56.getSkipping()) {
         var56.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1323902640, var10, -1, "io.intercom.android.sdk.views.compose.BooleanAttributeCollectorOption (BooleanAttributeCollector.kt:121)"
            );
         }

         int var12 = 0;
         int var13 = 0;
         CornerSize var11 = CornerSizeKt.recovered$ea3a4e3d7147c535(Dp.recovered$95b3d103663bf060(var12));
         CornerBasedShape var57 = var2
            ? CornerBasedShape.copy$default(var3, null, var11, var11, null, 9, null)
            : CornerBasedShape.copy$default(var3, var11, null, null, var11, 6, null);
         long var58 = IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).recovered$e5be759ff86c80c5();
         long var15 = Color.recovered$41aa0ee03c2282fb(
            IntercomTheme.INSTANCE.getColors(var56, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.38F, 0.0F, 0.0F, 0.0F, 14, null
         );
         Modifier var17 = RowScope.weight$default(
            var0,
            ClickableKt.recovered$1c9660c41fd850dc(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  ClipKt.clip(SizeKt.fillMaxHeight$default(Modifier.Companion, 0.0F, 1, null), var57),
                  Intrinsics.areEqual(var1, var2) ? var58 : Color.Companion.recovered$562f21ab17d9b4c7(),
                  null,
                  2,
                  null
               ),
               !var4 && !var5,
               null,
               null,
               null,
               var7,
               14,
               null
            ),
            1.0F,
            false,
            2,
            null
         );
         Horizontal var18 = Arrangement.INSTANCE.getCenter();
         Vertical var19 = Alignment.Companion.getCenterVertically();
         short var20 = 432;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var56, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var22 = RowKt.rowMeasurePolicy(var18, var19, var56, 14 & var20 >> 3 | 112 & var20 >> 3);
         Modifier var23 = var17;
         MeasurePolicy var24 = var22;
         Composer var25 = var56;
         int var26 = 112 & var20 << 3;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var28 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var25, 0));
         CompositionLocalMap var29 = var25.getCurrentCompositionLocalMap();
         Modifier var30 = ComposedModifierKt.materializeModifier(var25, var23);
         Function0 var31 = ComposeUiNode.Companion.getConstructor();
         Composer var32 = var25;
         int var33 = 6 | 896 & var26 << 6;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var32.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var32.startReusableNode();
         if (var32.getInserting()) {
            var32.createNode(var31);
         } else {
            var32.useNode();
         }

         Composer var35 = Updater.recovered$676aa17b44fd6280(var32);
         int var36 = 0;
         Updater.recovered$89682b199a142880(var35, var24, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var35, var29, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var35, var28, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var35, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var35, var30, ComposeUiNode.Companion.getSetModifier());
         int var37 = 14 & var33 >> 6;
         Composer var38 = var32;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var40 = 6 | 112 & var20 >> 6;
         Composer var41 = var38;
         RowScope var42 = RowScopeInstance.INSTANCE;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, 1286556833, "C156@5409L245,155@5384L523:BooleanAttributeCollector.kt#h0e4f2");
         var41.startReplaceGroup(872785294);
         ComposerKt.sourceInformation(var41, "149@5194L6,148@5129L184,153@5326L39");
         if (var6) {
            long var44 = IntercomTheme.INSTANCE.getColors(var41, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            Modifier var10000 = Modifier.Companion;
            int $this$dp$ivx = 20;
            int $i$f$getDpx = 0;
            Modifier var48 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            int $this$dp$ivxx = 3;
            int $i$f$getDpxx = 0;
            float var59 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var48, var44, var59, 0L, 0, 0.0F, var41, 390, 56);
            Modifier var63 = Modifier.Companion;
            int $this$dp$ivxxx = 4;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var63, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var41, 6);
         }

         var41.endReplaceGroup();
         String var61 = StringResources_androidKt.stringResource(
            var2 ? string.intercom_attribute_collector_positive : string.intercom_attribute_collector_negative, var41, 0
         );
         int var62 = TextAlign.Companion.recovered$d707b9dd64876179();
         var41.startReplaceGroup(872804911);
         ComposerKt.sourceInformation(var41, "167@5863L6");
         long var52 = var4
            ? var15
            : (Intrinsics.areEqual(var1, !var2) ? var15 : IntercomTheme.INSTANCE.getColors(var41, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a());
         var41.endReplaceGroup();
         long var54 = var52;
         TextKt.recovered$5b114ab32a4f4190(
            var61,
            null,
            var54,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var62),
            0L,
            0,
            false,
            0,
            0,
            null,
            null,
            var41,
            0,
            0,
            261114
         );
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var38);
         var32.endNode();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var56);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var64 = var56.endRestartGroup();
      if (var64 != null) {
         final androidx.compose.foundation.layout.RowScope recoveredExpressionCapture_1637_0 = var0;
         final java.lang.Boolean recoveredExpressionCapture_1637_1 = var1;
         final boolean recoveredExpressionCapture_1637_2 = var2;
         final androidx.compose.foundation.shape.CornerBasedShape recoveredExpressionCapture_1637_3 = var3;
         final boolean recoveredExpressionCapture_1637_4 = var4;
         final boolean recoveredExpressionCapture_1637_5 = var5;
         final boolean recoveredExpressionCapture_1637_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1637_7 = var7;
         final int recoveredExpressionCapture_1637_8 = var9;
         var64.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1637_0, recoveredExpressionArgument_1637_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributeCollectorOption$lambda$13(recoveredExpressionCapture_1637_0, recoveredExpressionCapture_1637_1, recoveredExpressionCapture_1637_2, recoveredExpressionCapture_1637_3, recoveredExpressionCapture_1637_4, recoveredExpressionCapture_1637_5, recoveredExpressionCapture_1637_6, recoveredExpressionCapture_1637_7, recoveredExpressionCapture_1637_8, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1637_0), ((java.lang.Integer)recoveredExpressionArgument_1637_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BooleanAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1269323591);
      ComposerKt.sourceInformation(var2, "C(BooleanAttributePreview)176@5992L519:BooleanAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1269323591, var1, -1, "io.intercom.android.sdk.views.compose.BooleanAttributePreview (BooleanAttributeCollector.kt:175)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_1638_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1638_0, recoveredExpressionArgument_1638_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.BooleanAttributePreview$lambda$14(recoveredExpressionCapture_1638_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1638_0), ((java.lang.Integer)recoveredExpressionArgument_1638_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedBooleanAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-875849702);
      ComposerKt.sourceInformation(var2, "C(SubmittedBooleanAttributePreview)198@6590L558:BooleanAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -875849702, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedBooleanAttributePreview (BooleanAttributeCollector.kt:197)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1641_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1641_0, recoveredExpressionArgument_1641_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.SubmittedBooleanAttributePreview$lambda$15(recoveredExpressionCapture_1641_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1641_0), ((java.lang.Integer)recoveredExpressionArgument_1641_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DisabledBooleanAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-2015578211);
      ComposerKt.sourceInformation(var2, "C(DisabledBooleanAttributePreview)221@7226L518:BooleanAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2015578211, var1, -1, "io.intercom.android.sdk.views.compose.DisabledBooleanAttributePreview (BooleanAttributeCollector.kt:220)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.INSTANCE.recovered$5a1850a8ff371bfb(),
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
         final int recoveredExpressionCapture_1639_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1639_0, recoveredExpressionArgument_1639_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.DisabledBooleanAttributePreview$lambda$16(recoveredExpressionCapture_1639_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1639_0), ((java.lang.Integer)recoveredExpressionArgument_1639_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedAndDisabledBooleanAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1476435233);
      ComposerKt.sourceInformation(var2, "C(SubmittedAndDisabledBooleanAttributePreview)243@7834L558:BooleanAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1476435233, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedAndDisabledBooleanAttributePreview (BooleanAttributeCollector.kt:242)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$BooleanAttributeCollectorKt.INSTANCE.recovered$a427d7fccaa25a17(),
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
         final int recoveredExpressionCapture_1640_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1640_0, recoveredExpressionArgument_1640_1) -> io.intercom.android.sdk.views.compose.BooleanAttributeCollectorKt.SubmittedAndDisabledBooleanAttributePreview$lambda$17(recoveredExpressionCapture_1640_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1640_0), ((java.lang.Integer)recoveredExpressionArgument_1640_1).intValue())));
      }
   }

   private static final Unit BooleanAttributeCollector$lambda$1$lambda$0(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final MutableState BooleanAttributeCollector$lambda$3$lambda$2(boolean var0, boolean var1, AttributeData var2) {
      Boolean var3;
      if (!var0 && !var1) {
         var3 = null;
      } else {
         String var10000 = var2.getAttribute().getValue();
         var3 = var10000 != null ? StringsKt.toBooleanStrictOrNull(var10000) : null;
      }

      return SnapshotStateKt.mutableStateOf$default(var3, null, 2, null);
   }

   private static final Boolean BooleanAttributeCollector$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void BooleanAttributeCollector$lambda$5(MutableState var0, Boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit BooleanAttributeCollector$lambda$10$lambda$7$lambda$6(Function1 var0, AttributeData var1, MutableState var2) {
      BooleanAttributeCollector$lambda$5(var2, true);
      var0.invoke(
         AttributeData.copy$default(
            var1, Attribute.copy$default(var1.getAttribute(), null, null, null, false, null, null, null, "true", 127, null), null, false, 6, null
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit BooleanAttributeCollector$lambda$10$lambda$9$lambda$8(Function1 var0, AttributeData var1, MutableState var2) {
      BooleanAttributeCollector$lambda$5(var2, false);
      var0.invoke(
         AttributeData.copy$default(
            var1, Attribute.copy$default(var1.getAttribute(), null, null, null, false, null, null, null, "false", 127, null), null, false, 6, null
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit BooleanAttributeCollector$lambda$11(
      Modifier var0, AttributeData var1, boolean var2, boolean var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      BooleanAttributeCollector(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit BooleanAttributeCollectorOption$lambda$13(
      RowScope var0,
      Boolean var1,
      boolean var2,
      CornerBasedShape var3,
      boolean var4,
      boolean var5,
      boolean var6,
      Function0 var7,
      int var8,
      Composer var9,
      int var10
   ) {
      BooleanAttributeCollectorOption(var0, var1, var2, var3, var4, var5, var6, var7, var9, RecomposeScopeImplKt.updateChangedFlags(var8 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BooleanAttributePreview$lambda$14(int var0, Composer var1, int var2) {
      BooleanAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedBooleanAttributePreview$lambda$15(int var0, Composer var1, int var2) {
      SubmittedBooleanAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DisabledBooleanAttributePreview$lambda$16(int var0, Composer var1, int var2) {
      DisabledBooleanAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedAndDisabledBooleanAttributePreview$lambda$17(int var0, Composer var1, int var2) {
      SubmittedAndDisabledBooleanAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
