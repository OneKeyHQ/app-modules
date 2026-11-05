package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
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
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u001a%\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "ErrorMessageLayout",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "errorMessages",
         "",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nErrorMessageLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ErrorMessageLayout.kt\nio/intercom/android/sdk/ui/component/ErrorMessageLayoutKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,55:1\n118#2:56\n118#2:89\n118#2:93\n99#3:57\n96#3,9:58\n106#3:97\n81#4,6:67\n88#4,6:82\n96#4:96\n402#5,9:73\n411#5:88\n412#5,2:94\n1878#6,3:90\n*S KotlinDebug\n*F\n+ 1 ErrorMessageLayout.kt\nio/intercom/android/sdk/ui/component/ErrorMessageLayoutKt\n*L\n27#1:56\n31#1:89\n47#1:93\n24#1:57\n24#1:58,9\n24#1:97\n24#1:67,6\n24#1:82,6\n24#1:96\n24#1:73,9\n24#1:88\n24#1:94,2\n37#1:90,3\n*E\n"
)
public final class ErrorMessageLayoutKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorMessageLayout(@Nullable Modifier var0, @NotNull List var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "errorMessages");
      var2 = var2.startRestartGroup(-1308212592);
      ComposerKt.sourceInformation(var2, "C(ErrorMessageLayout)P(1)23@846L1036:ErrorMessageLayout.kt#yr602f");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1308212592, var5, -1, "io.intercom.android.sdk.ui.component.ErrorMessageLayout (ErrorMessageLayout.kt:22)");
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         int var6 = 4;
         int var7 = 0;
         float var10002 = Dp.recovered$95b3d103663bf060(var6);
         var6 = 4;
         var7 = 0;
         Modifier var49 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060(var6), 5, null);
         Vertical var8 = Alignment.Companion.getCenterVertically();
         short var9 = 384;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var51 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var51, var8, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var49;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
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
         ComposerKt.sourceInformationMarkerStart(var27, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 1749435540, "C31@1102L50,32@1187L6,29@1028L221,47@1728L6,48@1776L10,42@1539L337:ErrorMessageLayout.kt#yr602f"
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var35 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Painter var52 = PainterResources_androidKt.painterResource(drawable.intercom_ic_error, var30, 0);
         long var36 = IntercomTheme.INSTANCE.getColors(var30, 6).recovered$2bcfe565407cdfb8();
         IconKt.recovered$e00adffb7fe45b02(var52, null, var35, var36, var30, 432 | Painter.$stable, 0);
         StringBuilder var57 = new StringBuilder();
         var30.startReplaceGroup(-1467578667);
         ComposerKt.sourceInformation(var30, "*37@1402L9");
         Iterable var53 = var1;
         $i$f$getDpx = 0;
         int var38 = 0;

         for (Object var40 : var53) {
            int var41 = var38++;
            if (var41 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            StringProvider var42 = (StringProvider)var40;
            int var43 = var41;
            int var44 = 0;
            var57.append(var42.getText(var30, 0));
            if (var43 != var1.size() - 1) {
               var57.append(". ");
            }
         }

         var30.endReplaceGroup();
         String var64 = var57.toString();
         Intrinsics.checkNotNullExpressionValue(var64, "toString(...)");
         String var54 = var64;
         var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         var38 = 4;
         int $i$f$getDpxx = 0;
         Modifier var56 = PaddingKt.recovered$03b481d31d62885a(var10000, Dp.recovered$95b3d103663bf060(var38), 0.0F, 0.0F, 0.0F, 14, null);
         long var45 = IntercomTheme.INSTANCE.getColors(var30, 6).recovered$2bcfe565407cdfb8();
         TextStyle var60 = IntercomTheme.INSTANCE.getTypography(var30, 6).getType04();
         int var61 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var54, var56, var45, null, 0L, null, null, null, 0L, null, null, 0L, var61, false, 1, 0, null, var60, var30, 48, 24960, 110584
         );
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var66 = var2.endRestartGroup();
      if (var66 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_9_0 = var0;
         final java.util.List recoveredCapture_9_1 = var1;
         final int recoveredCapture_9_2 = var3;
         final int recoveredCapture_9_3 = var4;
         var66.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_9_0, recoveredArgument_9_1) -> ErrorMessageLayoutKt.ErrorMessageLayout$lambda$2(recoveredCapture_9_0, recoveredCapture_9_1, recoveredCapture_9_2, recoveredCapture_9_3, (androidx.compose.runtime.Composer)recoveredArgument_9_0, ((java.lang.Integer)recoveredArgument_9_1).intValue())));
      }
   }

   private static final Unit ErrorMessageLayout$lambda$2(Modifier var0, List var1, int var2, int var3, Composer var4, int var5) {
      ErrorMessageLayout(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }
}
