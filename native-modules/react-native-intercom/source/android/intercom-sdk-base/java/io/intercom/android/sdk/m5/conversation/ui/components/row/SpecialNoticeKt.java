package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\n",
   d2 = {
         "SpecialNotice",
         "",
         "text",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "SpecialNoticePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SpecialNoticeShortTextPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSpecialNotice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpecialNotice.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SpecialNoticeKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,82:1\n118#2:83\n118#2:84\n118#2:85\n118#2:114\n118#2:115\n99#3,6:86\n106#3:120\n81#4,6:92\n88#4,6:107\n96#4:119\n402#5,9:98\n411#5:113\n412#5,2:117\n75#6:116\n*S KotlinDebug\n*F\n+ 1 SpecialNotice.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/SpecialNoticeKt\n*L\n33#1:83\n40#1:84\n41#1:85\n46#1:114\n49#1:115\n34#1:86,6\n34#1:120\n34#1:92,6\n34#1:107,6\n34#1:119\n34#1:98,9\n34#1:113\n34#1:117,2\n53#1:116\n*E\n"
)
public final class SpecialNoticeKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SpecialNotice(@NotNull String var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "text");
      var2 = var2.startRestartGroup(-1272749637);
      ComposerKt.sourceInformation(var2, "C(SpecialNotice)P(1)38@1515L6,39@1608L6,33@1322L998:SpecialNotice.kt#hr3bi5");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1272749637, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SpecialNotice (SpecialNotice.kt:31)");
         }

         int var7 = 20;
         int var8 = 0;
         RoundedCornerShape var6 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var7));
         Horizontal var41 = Arrangement.INSTANCE.getStart();
         Vertical var42 = Alignment.Companion.getTop();
         Modifier var10000 = BackgroundKt.recovered$60bc47fb440df483(
            SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$8f3937b8d58004d9(),
            var6
         );
         int $this$dp$ivx = 1;
         int $i$f$getDpx = 0;
         Modifier var10 = BorderKt.recovered$1227359600bc50fd(
            var10000,
            Dp.recovered$95b3d103663bf060($this$dp$ivx),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$e610a85b5b67ed0e(),
            var6
         );
         $i$f$getDpx = 12;
         int $i$f$getDpxx = 0;
         float var44 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
         $i$f$getDpxx = 16;
         int $i$f$getDpxxx = 0;
         float var47 = Dp.recovered$95b3d103663bf060($i$f$getDpxx);
         Modifier var9 = PaddingKt.recovered$904403cea4c49d95(var10, var47, var44);
         short var43 = 432;
         $this$dp$ivx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var48 = RowKt.rowMeasurePolicy(var41, var42, var2, 14 & var43 >> 3 | 112 & var43 >> 3);
         Modifier var50 = var9;
         MeasurePolicy var51 = var48;
         Composer var15 = var2;
         int var16 = 112 & var43 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var50);
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
         Updater.recovered$89682b199a142880(var25, var51, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var43 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 1764496947, "C43@1755L58,46@1932L6,42@1727L242,48@1978L39,52@2236L7,53@2280L10,49@2026L288:SpecialNotice.kt#hr3bi5"
         );
         Painter var57 = PainterResources_androidKt.painterResource(drawable.intercom_ic_info_filled, var31, 0);
         Modifier var10002 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxxxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var57,
            null,
            SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060($this$dp$ivxx)),
            IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(),
            var31,
            432 | Painter.$stable,
            0
         );
         var10000 = Modifier.Companion;
         $this$dp$ivxx = 8;
         $i$f$getDpxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var31, 6);
         Modifier var53 = var32.align(Modifier.Companion, Alignment.Companion.getCenterVertically());
         Spanned var59 = HtmlCompat.fromHtml(var0, 0);
         Intrinsics.checkNotNullExpressionValue(var59, "fromHtml(...)");
         CharSequence var60 = var59;
         CompositionLocal var36 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var37 = 0;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var39 = var31.consume(var36);
         ComposerKt.sourceInformationMarkerEnd(var31);
         AnnotatedString var55 = BlockExtensionsKt.recovered$c71786fddfec5215(var60, (Context)var39, null, 0L, 6, null);
         TextStyle var56 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04Point5();
         TextKt.recovered$489343e57da77a35(
            var55, var53, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var56, var31, 0, 0, 262140
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var61 = var2.endRestartGroup();
      if (var61 != null) {
         final java.lang.String recoveredCapture_782_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_782_1 = var1;
         final int recoveredCapture_782_2 = var3;
         final int recoveredCapture_782_3 = var4;
         var61.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_782_0, recoveredArgument_782_1) -> SpecialNoticeKt.SpecialNotice$lambda$1(recoveredCapture_782_0, recoveredCapture_782_1, recoveredCapture_782_2, recoveredCapture_782_3, (androidx.compose.runtime.Composer)recoveredArgument_782_0, ((java.lang.Integer)recoveredArgument_782_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SpecialNoticePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1291680485);
      ComposerKt.sourceInformation(var0, "C(SpecialNoticePreview)61@2396L235:SpecialNotice.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1291680485, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SpecialNoticePreview (SpecialNotice.kt:60)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SpecialNoticeKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_783_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_783_0, recoveredArgument_783_1) -> SpecialNoticeKt.SpecialNoticePreview$lambda$2(recoveredCapture_783_0, (androidx.compose.runtime.Composer)recoveredArgument_783_0, ((java.lang.Integer)recoveredArgument_783_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SpecialNoticeShortTextPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(841465410);
      ComposerKt.sourceInformation(var0, "C(SpecialNoticeShortTextPreview)73@2716L119:SpecialNotice.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               841465410, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SpecialNoticeShortTextPreview (SpecialNotice.kt:72)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SpecialNoticeKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_784_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_784_0, recoveredArgument_784_1) -> SpecialNoticeKt.SpecialNoticeShortTextPreview$lambda$3(recoveredCapture_784_0, (androidx.compose.runtime.Composer)recoveredArgument_784_0, ((java.lang.Integer)recoveredArgument_784_1).intValue())));
      }
   }

   private static final Unit SpecialNotice$lambda$1(String var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      SpecialNotice(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit SpecialNoticePreview$lambda$2(int var0, Composer var1, int var2) {
      SpecialNoticePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SpecialNoticeShortTextPreview$lambda$3(int var0, Composer var1, int var2) {
      SpecialNoticeShortTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
