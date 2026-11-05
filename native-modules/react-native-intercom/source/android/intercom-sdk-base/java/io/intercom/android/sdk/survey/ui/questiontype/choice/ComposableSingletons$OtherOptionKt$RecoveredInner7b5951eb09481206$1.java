package io.intercom.android.sdk.survey.ui.questiontype.choice;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nOtherOption.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/ComposableSingletons$OtherOptionKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,86:1\n1047#2,6:87\n1047#2,6:93\n*S KotlinDebug\n*F\n+ 1 OtherOption.kt\nio/intercom/android/sdk/survey/ui/questiontype/choice/ComposableSingletons$OtherOptionKt$lambda-1$1\n*L\n81#1:87,6\n82#1:93,6\n*E\n"
)
final class ComposableSingletons$OtherOptionKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$OtherOptionKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$OtherOptionKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C80@3233L2,81@3265L2,76@3069L209:OtherOption.kt#w8l19x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               531895732,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.choice.ComposableSingletons$OtherOptionKt.lambda-1.<anonymous> (OtherOption.kt:76)"
            );
         }

         boolean var10000 = true;
         SurveyUiColors var10001 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         String var10002 = "none";
         var1.startReplaceGroup(-1133347294);
         ComposerKt.sourceInformation(var1, "CC(remember):OtherOption.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            String var12 = "none";
            SurveyUiColors var11 = var10001;
            boolean var10 = true;
            int var9 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$OtherOptionKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var21 = var13;
            var4.updateRememberedValue(var21);
            var10003 = (Function0)var21;
         } else {
            var10003 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var3;
         var1.startReplaceGroup(-1133346270);
         ComposerKt.sourceInformation(var1, "CC(remember):OtherOption.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var27 = var3;
            String var26 = var10002;
            SurveyUiColors var25 = var10001;
            boolean var24 = var10000;
            int var22 = 0;
            Function1 var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1395_0) -> ComposableSingletons$OtherOptionKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$3$lambda$2((java.lang.String)recoveredArgument_1395_0));
            var10000 = var24;
            var10001 = var25;
            var10002 = var26;
            var10003 = var27;
            Object var23 = var14;
            var4.updateRememberedValue(var23);
            var10004 = var23;
         } else {
            var10004 = var7;
         }

         Function1 var15 = (Function1)var10004;
         var1.endReplaceGroup();
         OtherOptionKt.recovered$ceeec0b30e6e12f1(var10000, var10001, var10002, (kotlin.jvm.functions.Function0) var10003, var15, 0L, 0.0F, 0L, null, 0L, var1, 28038, 992);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
