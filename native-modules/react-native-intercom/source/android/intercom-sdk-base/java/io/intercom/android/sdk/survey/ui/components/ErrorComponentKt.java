package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.SurveyState$Error;
import io.intercom.android.sdk.survey.SurveyState$Error$WithCTA;
import io.intercom.android.sdk.survey.SurveyState$Error$WithoutCTA;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.TopBarState;
import io.intercom.android.sdk.survey.TopBarState$NoTopBarState;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\n",
   d2 = {
         "SurveyError",
         "",
         "state",
         "Lio/intercom/android/sdk/survey/SurveyState$Error;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/survey/SurveyState$Error;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "ErrorStateWithCTA",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ErrorStateWithoutCTA",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nErrorComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ErrorComponent.kt\nio/intercom/android/sdk/survey/ui/components/ErrorComponentKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,83:1\n70#2:84\n68#2,8:85\n77#2:120\n81#3,6:93\n88#3,6:108\n96#3:119\n402#4,9:99\n411#4:114\n412#4,2:117\n118#5:115\n118#5:116\n1047#6,6:121\n*S KotlinDebug\n*F\n+ 1 ErrorComponent.kt\nio/intercom/android/sdk/survey/ui/components/ErrorComponentKt\n*L\n27#1:84\n27#1:85,8\n27#1:120\n27#1:93,6\n27#1:108,6\n27#1:119\n27#1:99,9\n27#1:114\n27#1:117,2\n38#1:115\n45#1:116\n65#1:121,6\n*E\n"
)
public final class ErrorComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyError(@NotNull SurveyState$Error var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(805293226);
      ComposerKt.sourceInformation(var2, "C(SurveyError)P(1)26@980L911:ErrorComponent.kt#bmpoeo");
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
            ComposerKt.traceEventStart(805293226, var5, -1, "io.intercom.android.sdk.survey.ui.components.SurveyError (ErrorComponent.kt:25)");
         }

         Modifier var6 = SizeKt.fillMaxSize$default(var1, 0.0F, 1, null);
         Alignment var7 = Alignment.Companion.getCenter();
         byte var9 = 48;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var8 = false;
         MeasurePolicy var11 = BoxKt.maybeCachedBoxMeasurePolicy(var7, var8);
         Modifier var12 = var6;
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
         ComposerKt.sourceInformationMarkerStart(var27, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         BoxScope var31 = BoxScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 860959896, "C31@1114L39,30@1089L384:ErrorComponent.kt#bmpoeo");
         String var33 = StringResources_androidKt.stringResource(var0.getMessageResId(), var30, 0);
         long var34 = var0.getSurveyUiColors().recovered$f126774a6443d8d2();
         long var36 = TextUnitKt.getSp(36);
         FontWeight var38 = FontWeight.Companion.getBold();
         int var39 = TextAlign.Companion.recovered$d707b9dd64876179();
         Modifier var10001 = Modifier.Companion;
         int var40 = 32;
         int var41 = 0;
         float var10002 = Dp.recovered$95b3d103663bf060(var40);
         var40 = 32;
         var41 = 0;
         Modifier var42 = var31.align(
            PaddingKt.recovered$904403cea4c49d95(var10001, var10002, Dp.recovered$95b3d103663bf060(var40)), Alignment.Companion.getTopCenter()
         );
         TextKt.recovered$5b114ab32a4f4190(
            var33,
            var42,
            var34,
            null,
            var36,
            null,
            var38,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var39),
            0L,
            0,
            false,
            0,
            0,
            null,
            null,
            var30,
            1597440,
            0,
            261032
         );
         var30.startReplaceGroup(-803498879);
         ComposerKt.sourceInformation(var30, "48@1816L44,42@1525L350");
         if (var0 instanceof SurveyState$Error$WithCTA) {
            var10001 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            Modifier var47 = var31.align(
               PaddingKt.recovered$eb6a744cd98cdbca(var10001, Dp.recovered$95b3d103663bf060($this$dp$ivx)), Alignment.Companion.getBottomCenter()
            );
            Function0 var50 = ((SurveyState$Error$WithCTA)var0).getOnClick();
            SurveyUiColors var51 = ((SurveyState$Error$WithCTA)var0).getSurveyUiColors();
            String var45 = StringResources_androidKt.stringResource(string.intercom_retry, var30, 0);
            SurveyCtaButtonComponentKt.SurveyCtaButtonComponent(var47, var45, null, var50, null, var51, var30, 0, 20);
         }

         var30.endReplaceGroup();
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

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.SurveyState$Error recoveredCapture_1329_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1329_1 = var1;
         final int recoveredCapture_1329_2 = var3;
         final int recoveredCapture_1329_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1329_0, recoveredArgument_1329_1) -> ErrorComponentKt.SurveyError$lambda$1(recoveredCapture_1329_0, recoveredCapture_1329_1, recoveredCapture_1329_2, recoveredCapture_1329_3, (androidx.compose.runtime.Composer)recoveredArgument_1329_0, ((java.lang.Integer)recoveredArgument_1329_1).intValue())));
      }
   }

   @Composable
   @Preview(name = "Error with CTA")
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorStateWithCTA(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1921062712);
      ComposerKt.sourceInformation(var0, "C(ErrorStateWithCTA)64@2265L2,57@1980L305:ErrorComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1921062712, var1, -1, "io.intercom.android.sdk.survey.ui.components.ErrorStateWithCTA (ErrorComponent.kt:56)");
         }

         byte var10000 = 0;
         SurveyUiColors var10001 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         TopBarState var10002 = new TopBarState$NoTopBarState(
            true, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), null, 4, null
         );
         var0.startReplaceGroup(-737735323);
         ComposerKt.sourceInformation(var0, "CC(remember):ErrorComponent.kt#9igjgp");
         Composer var3 = var0;
         boolean var4 = false;
         int var5 = 0;
         Object var6 = var3.rememberedValue();
         int var7 = 0;
         Object var10003;
         if (var6 == Composer.Companion.getEmpty()) {
            TopBarState var11 = var10002;
            SurveyUiColors var10 = var10001;
            byte var9 = 0;
            int var8 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ErrorComponentKt.ErrorStateWithCTA$lambda$3$lambda$2());
            var10000 = var9;
            var10001 = var10;
            var10002 = var11;
            Object var20 = var12;
            var3.updateRememberedValue(var20);
            var10003 = var20;
         } else {
            var10003 = var6;
         }

         Function0 var2 = (Function0)var10003;
         var0.endReplaceGroup();
         Object var13 = null;
         byte var14 = 1;
         Function0 var15 = var2;
         TopBarState var16 = var10002;
         SurveyUiColors var17 = var10001;
         byte var18 = var10000;
         SurveyError(new SurveyState$Error$WithCTA(var18, var17, var16, var15, var14, (DefaultConstructorMarker)var13), null, var0, 0, 2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var21 = var0.endRestartGroup();
      if (var21 != null) {
         final int recoveredCapture_1326_0 = var1;
         var21.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1326_0, recoveredArgument_1326_1) -> ErrorComponentKt.ErrorStateWithCTA$lambda$4(recoveredCapture_1326_0, (androidx.compose.runtime.Composer)recoveredArgument_1326_0, ((java.lang.Integer)recoveredArgument_1326_1).intValue())));
      }
   }

   @Composable
   @Preview(name = "Error without CTA")
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorStateWithoutCTA(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1056362620);
      ComposerKt.sourceInformation(var0, "C(ErrorStateWithoutCTA)72@2380L282:ErrorComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1056362620, var1, -1, "io.intercom.android.sdk.survey.ui.components.ErrorStateWithoutCTA (ErrorComponent.kt:71)");
         }

         SurveyError(
            new SurveyState$Error$WithoutCTA(
               0,
               SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)),
               new TopBarState$NoTopBarState(true, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), null, 4, null),
               1,
               null
            ),
            null,
            var0,
            0,
            2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1328_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1328_0, recoveredArgument_1328_1) -> ErrorComponentKt.ErrorStateWithoutCTA$lambda$5(recoveredCapture_1328_0, (androidx.compose.runtime.Composer)recoveredArgument_1328_0, ((java.lang.Integer)recoveredArgument_1328_1).intValue())));
      }
   }

   private static final Unit SurveyError$lambda$1(SurveyState$Error var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      SurveyError(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit ErrorStateWithCTA$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit ErrorStateWithCTA$lambda$4(int var0, Composer var1, int var2) {
      ErrorStateWithCTA(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ErrorStateWithoutCTA$lambda$5(int var0, Composer var1, int var2) {
      ErrorStateWithoutCTA(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
