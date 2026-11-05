package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.CornerBasedShape;
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
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a/\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "PrivacyPolicy",
         "",
         "text",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onCloseClicked",
         "Lkotlin/Function0;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "PrivacyPolicyPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPrivacyPolicy.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrivacyPolicy.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/PrivacyPolicyKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,80:1\n1047#2,6:81\n118#3:87\n118#3:88\n118#3:121\n118#3:122\n99#4:89\n97#4,8:90\n106#4:126\n81#5,6:98\n88#5,6:113\n96#5:125\n402#6,9:104\n411#6:119\n412#6,2:123\n75#7:120\n*S KotlinDebug\n*F\n+ 1 PrivacyPolicy.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/PrivacyPolicyKt\n*L\n35#1:81,6\n43#1:87\n44#1:88\n56#1:121\n59#1:122\n38#1:89\n38#1:90,8\n38#1:126\n38#1:98,6\n38#1:113,6\n38#1:125\n38#1:104,9\n38#1:119\n38#1:123,2\n47#1:120\n*E\n"
)
public final class PrivacyPolicyKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PrivacyPolicy(@NotNull String var0, @Nullable Modifier var1, @Nullable Function0 var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var0, "text");
      var3 = var3.startRestartGroup(-2047732965);
      ComposerKt.sourceInformation(var3, "C(PrivacyPolicy)P(2)34@1445L2,36@1483L6,41@1657L6,37@1500L1144:PrivacyPolicy.kt#yb6g9");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var3.startReplaceGroup(-1753709179);
            ComposerKt.sourceInformation(var3, "CC(remember):PrivacyPolicy.kt#9igjgp");
            Composer var8 = var3;
            boolean var9 = false;
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10000;
            if (var11 == Composer.Companion.getEmpty()) {
               int var13 = 0;
               Object var57 = ((kotlin.jvm.functions.Function0) () -> PrivacyPolicyKt.PrivacyPolicy$lambda$1$lambda$0());
               var8.updateRememberedValue(var57);
               var10000 = var57;
            } else {
               var10000 = var11;
            }

            Function0 var7 = (Function0)var10000;
            var3.endReplaceGroup();
            var2 = var7;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2047732965, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.PrivacyPolicy (PrivacyPolicy.kt:35)"
            );
         }

         CornerBasedShape var45 = IntercomTheme.INSTANCE.getShapes(var3, IntercomTheme.$stable).getSmall();
         HorizontalOrVertical var46 = Arrangement.INSTANCE.getSpaceBetween();
         Modifier var48 = BackgroundKt.recovered$60bc47fb440df483(
            SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null), IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).recovered$f6e7a825ec69c94c(), var45
         );
         int var54 = 12;
         int var58 = 0;
         float var51 = Dp.recovered$95b3d103663bf060(var54);
         var58 = 16;
         int $i$f$getDpx = 0;
         float var55 = Dp.recovered$95b3d103663bf060(var58);
         Modifier var71 = PaddingKt.recovered$904403cea4c49d95(var48, var55, var51);
         int $this$dp$ivx = 70;
         int $i$f$getDpxx = 0;
         Modifier var47 = SizeKt.recovered$1c935d354e9159f0(var71, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 1, null);
         byte var53 = 48;
         var54 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Vertical var50 = Alignment.Companion.getTop();
         MeasurePolicy var60 = RowKt.rowMeasurePolicy(var46, var50, var3, 14 & var53 >> 3 | 112 & var53 >> 3);
         Modifier var61 = var47;
         MeasurePolicy var15 = var60;
         Composer var16 = var3;
         int var17 = 112 & var53 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var61);
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
         int var31 = 6 | 112 & var53 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32,
            1421785187,
            "C46@1942L7,49@2024L21,51@2116L6,52@2169L10,48@1960L237,55@2207L39,61@2449L47,62@2531L44,63@2610L6,56@2255L383:PrivacyPolicy.kt#yb6g9"
         );
         Spanned var72 = HtmlCompat.fromHtml(var0, 0);
         Intrinsics.checkNotNullExpressionValue(var72, "fromHtml(...)");
         CharSequence var73 = var72;
         CompositionLocal var35 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var36 = 0;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var38 = var32.consume(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         AnnotatedString var39 = BlockExtensionsKt.recovered$c71786fddfec5215(var73, (Context)var38, null, 0L, 6, null);
         Modifier var62 = ScrollKt.verticalScroll$default(
            RowScope.weight$default(var33, Modifier.Companion, 1.0F, false, 2, null),
            ScrollKt.rememberScrollState(0, var32, 0, 1),
            false,
            null,
            false,
            14,
            null
         );
         long var40 = IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextStyle var70 = IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType05();
         TextKt.recovered$489343e57da77a35(
            var39, var62, var40, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var70, var32, 0, 0, 262136
         );
         var71 = Modifier.Companion;
         int $this$dp$ivxx = 8;
         var36 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var71, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var32, 6);
         Modifier var10001 = Modifier.Companion;
         var36 = 10;
         var37 = 0;
         Modifier var64 = ClickableKt.recovered$1c9660c41fd850dc(
            var33.align(SizeKt.recovered$111ef94c93e78c04(var10001, Dp.recovered$95b3d103663bf060(var36)), Alignment.Companion.getCenterVertically()),
            false,
            null,
            null,
            null,
            var2,
            15,
            null
         );
         Painter var67 = PainterResources_androidKt.painterResource(drawable.intercom_close, var32, 0);
         String var69 = StringResources_androidKt.stringResource(string.intercom_close, var32, 0);
         long var42 = IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122();
         IconKt.recovered$e00adffb7fe45b02(var67, var69, var64, var42, var32, Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var75 = var3.endRestartGroup();
      if (var75 != null) {
         final java.lang.String recoveredCapture_636_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_636_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_636_2 = var2;
         final int recoveredCapture_636_3 = var4;
         final int recoveredCapture_636_4 = var5;
         var75.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_636_0, recoveredArgument_636_1) -> PrivacyPolicyKt.PrivacyPolicy$lambda$3(recoveredCapture_636_0, recoveredCapture_636_1, recoveredCapture_636_2, recoveredCapture_636_3, recoveredCapture_636_4, (androidx.compose.runtime.Composer)recoveredArgument_636_0, ((java.lang.Integer)recoveredArgument_636_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PrivacyPolicyPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-520891099);
      ComposerKt.sourceInformation(var0, "C(PrivacyPolicyPreview)71@2720L340:PrivacyPolicy.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -520891099, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.PrivacyPolicyPreview (PrivacyPolicy.kt:70)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PrivacyPolicyKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_637_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_637_0, recoveredArgument_637_1) -> PrivacyPolicyKt.PrivacyPolicyPreview$lambda$4(recoveredCapture_637_0, (androidx.compose.runtime.Composer)recoveredArgument_637_0, ((java.lang.Integer)recoveredArgument_637_1).intValue())));
      }
   }

   private static final Unit PrivacyPolicy$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit PrivacyPolicy$lambda$3(String var0, Modifier var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      PrivacyPolicy(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit PrivacyPolicyPreview$lambda$4(int var0, Composer var1, int var2) {
      PrivacyPolicyPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
