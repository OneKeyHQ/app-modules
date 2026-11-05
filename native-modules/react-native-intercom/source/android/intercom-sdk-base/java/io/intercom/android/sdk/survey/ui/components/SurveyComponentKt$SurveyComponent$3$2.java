package io.intercom.android.sdk.survey.ui.components;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.survey.SurveyState;
import io.intercom.android.sdk.survey.SurveyState$Content;
import io.intercom.android.sdk.survey.SurveyState$Error;
import io.intercom.android.sdk.survey.SurveyState$Initial;
import io.intercom.android.sdk.survey.SurveyState$Loading;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SurveyComponentKt$SurveyComponent$3$2 implements Function3 {
   // $VF: synthetic field
   final SurveyState $state;
   // $VF: synthetic field
   final Function1 $onContinue;
   // $VF: synthetic field
   final Function1 $onAnswerUpdated;
   // $VF: synthetic field
   final Function1 $onSecondaryCtaClicked;

   SurveyComponentKt$SurveyComponent$3$2(SurveyState var1, Function1 var2, Function1 var3, Function1 var4) {
      this.$state = var1;
      this.$onContinue = var2;
      this.$onAnswerUpdated = var3;
      this.$onSecondaryCtaClicked = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "contentPadding");
      ComposerKt.sourceInformation(var2, "C:SurveyComponent.kt#bmpoeo");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1342576427, var4, -1, "io.intercom.android.sdk.survey.ui.components.SurveyComponent.<anonymous>.<anonymous> (SurveyComponent.kt:101)"
            );
         }

         SurveyState var5 = this.$state;
         if (var5 instanceof SurveyState$Content) {
            var2.startReplaceGroup(-509515994);
            ComposerKt.sourceInformation(var2, "102@4817L323");
            SurveyComponentKt.SurveyContent(
               (SurveyState$Content)this.$state,
               this.$onContinue,
               this.$onAnswerUpdated,
               this.$onSecondaryCtaClicked,
               PaddingKt.padding(Modifier.Companion, var1),
               var2,
               0,
               0
            );
            var2.endReplaceGroup();
         } else if (var5 instanceof SurveyState$Error) {
            var2.startReplaceGroup(-509504751);
            ComposerKt.sourceInformation(var2, "110@5174L142");
            ErrorComponentKt.SurveyError((SurveyState$Error)this.$state, PaddingKt.padding(Modifier.Companion, var1), var2, 0, 0);
            var2.endReplaceGroup();
         } else if (var5 instanceof SurveyState$Loading) {
            var2.startReplaceGroup(-509498669);
            ComposerKt.sourceInformation(var2, "115@5364L144");
            LoadingComponentKt.SurveyLoading((SurveyState$Loading)this.$state, PaddingKt.padding(Modifier.Companion, var1), var2, 0, 0);
            var2.endReplaceGroup();
         } else {
            if (!Intrinsics.areEqual(var5, SurveyState$Initial.INSTANCE)) {
               var2.startReplaceGroup(-509517452);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(1385593533);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
