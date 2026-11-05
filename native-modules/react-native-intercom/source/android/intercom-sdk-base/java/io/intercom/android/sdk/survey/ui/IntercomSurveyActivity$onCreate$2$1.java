package io.intercom.android.sdk.survey.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.survey.CloseEventTrigger;
import io.intercom.android.sdk.survey.SurveyState;
import io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModel;
import io.intercom.android.sdk.survey.ui.components.SurveyComponentKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.LinkOpener;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomSurveyActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomSurveyActivity.kt\nio/intercom/android/sdk/survey/ui/IntercomSurveyActivity$onCreate$2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,96:1\n1047#2,6:97\n1047#2,6:103\n1047#2,6:109\n1047#2,6:115\n*S KotlinDebug\n*F\n+ 1 IntercomSurveyActivity.kt\nio/intercom/android/sdk/survey/ui/IntercomSurveyActivity$onCreate$2$1\n*L\n70#1:97,6\n71#1:103,6\n78#1:109,6\n74#1:115,6\n*E\n"
)
final class IntercomSurveyActivity$onCreate$2$1 implements Function2 {
   // $VF: synthetic field
   final IntercomSurveyActivity this$0;

   IntercomSurveyActivity$onCreate$2$1(IntercomSurveyActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C63@2586L16,69@2949L26,70@3007L104,77@3361L31,73@3157L164,67@2858L553:IntercomSurveyActivity.kt#gv1svo");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2107771943,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.IntercomSurveyActivity.onCreate.<anonymous>.<anonymous> (IntercomSurveyActivity.kt:63)"
            );
         }

         State var3 = SnapshotStateKt.collectAsState(IntercomSurveyActivity.access$getViewModel(this.this$0).getState(), null, var1, 0, 1);
         SurveyUiColors var4 = ((SurveyState)var3.getValue()).getSurveyUiColors();
         WindowCompat.getInsetsController(this.this$0.getWindow(), this.this$0.getWindow().getDecorView())
            .setAppearanceLightStatusBars(
               ColorExtensionsKt.recovered$4886b56a29c1c487(ColorExtensionsKt.recovered$da172df9fccf1b21(var4.recovered$a7e7bdd99c6c8d3d()))
            );
         SurveyState var5 = (SurveyState)var3.getValue();
         SurveyViewModel var7 = IntercomSurveyActivity.access$getViewModel(this.this$0);
         var1.startReplaceGroup(1710823161);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomSurveyActivity.kt#9igjgp");
         Composer var9 = var1;
         boolean var10 = var1.changedInstance(var7);
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var10000 = var12;
         } else {
            int var14 = 0;
            Object var15 = new IntercomSurveyActivity$onCreate$2$1$1$1(var7);
            var9.updateRememberedValue(var15);
            var10000 = var15;
         }

         KFunction var8 = (KFunction)var10000;
         var1.endReplaceGroup();
         KFunction var6 = var8;
         SurveyState var55 = var5;
         Function1 var10001 = (Function1)var6;
         var1.startReplaceGroup(1710825095);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomSurveyActivity.kt#9igjgp");
         Composer var24 = var1;
         boolean var27 = var1.changedInstance(this.this$0);
         IntercomSurveyActivity var30 = this.this$0;
         var11 = 0;
         var12 = var24.rememberedValue();
         var13 = 0;
         Object var10002;
         if (!var27 && var12 != Composer.Companion.getEmpty()) {
            var10002 = (Function0)var12;
         } else {
            Function1 var17 = var10001;
            SurveyState var16 = var5;
            int var42 = 0;
            final io.intercom.android.sdk.survey.ui.IntercomSurveyActivity recoveredCapture_1319_0 = var30;
            Function0 var18 = ((kotlin.jvm.functions.Function0) () -> IntercomSurveyActivity$onCreate$2$1.invoke$lambda$2$lambda$1(recoveredCapture_1319_0));
            var55 = var16;
            var10001 = var17;
            Object var45 = var18;
            var24.updateRememberedValue(var45);
            var10002 = (Function0)var45;
         }

         Function0 var21 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var21;
         var1.startReplaceGroup(1710836350);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomSurveyActivity.kt#9igjgp");
         Composer var25 = var1;
         boolean var28 = var1.changedInstance(this.this$0);
         IntercomSurveyActivity var31 = this.this$0;
         var11 = 0;
         var12 = var25.rememberedValue();
         var13 = 0;
         Object var10003;
         if (!var28 && var12 != Composer.Companion.getEmpty()) {
            var10003 = (Function1)var12;
         } else {
            Function0 var52 = var21;
            Function1 var50 = var10001;
            SurveyState var48 = var55;
            int var43 = 0;
            final io.intercom.android.sdk.survey.ui.IntercomSurveyActivity recoveredCapture_1320_0 = var31;
            Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1320_0) -> IntercomSurveyActivity$onCreate$2$1.invoke$lambda$4$lambda$3(recoveredCapture_1320_0, (java.lang.String)recoveredArgument_1320_0));
            var55 = var48;
            var10001 = var50;
            var10002 = var52;
            Object var46 = var19;
            var25.updateRememberedValue(var46);
            var10003 = (Function1)var46;
         }

         Function1 var22 = (kotlin.jvm.functions.Function1) var10003;
         var1.endReplaceGroup();
         var10003 = var22;
         var1.startReplaceGroup(1710829955);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomSurveyActivity.kt#9igjgp");
         Composer var26 = var1;
         boolean var29 = var1.changedInstance(this.this$0);
         IntercomSurveyActivity var32 = this.this$0;
         var11 = 0;
         var12 = var26.rememberedValue();
         var13 = 0;
         Object var10004;
         if (!var29 && var12 != Composer.Companion.getEmpty()) {
            var10004 = var12;
         } else {
            Function1 var54 = var22;
            Function0 var53 = (kotlin.jvm.functions.Function0) var10002;
            Function1 var51 = var10001;
            SurveyState var49 = var55;
            int var44 = 0;
            final io.intercom.android.sdk.survey.ui.IntercomSurveyActivity recoveredCapture_1321_0 = var32;
            Function1 var20 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1321_0) -> IntercomSurveyActivity$onCreate$2$1.invoke$lambda$6$lambda$5(recoveredCapture_1321_0, (io.intercom.android.sdk.survey.SurveyState$Content$SecondaryCta)recoveredArgument_1321_0));
            var55 = var49;
            var10001 = var51;
            var10002 = var53;
            var10003 = var54;
            Object var47 = var20;
            var26.updateRememberedValue(var47);
            var10004 = var47;
         }

         Function1 var23 = (Function1)var10004;
         var1.endReplaceGroup();
         SurveyComponentKt.SurveyComponent(var55, var10001, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function1) var10003, var23, var1, 0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1(IntercomSurveyActivity var0) {
      IntercomSurveyActivity.access$getViewModel(var0).onCloseClicked(CloseEventTrigger.CLOSE_BUTTON);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3(IntercomSurveyActivity var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      IntercomSurveyActivity.access$getViewModel(var0).onAnswerUpdated();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5(IntercomSurveyActivity var0, SurveyState$Content$SecondaryCta var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      IntercomSurveyActivity.access$getViewModel(var0).onSecondaryCtaClicked(var1);
      LinkOpener.handleUrl(var1.getDestination(), var0, IntercomSurveyActivity.access$getInjector$p(var0).getApi());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
