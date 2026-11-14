package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFileUploadErrorComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileUploadErrorComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileUploadErrorComponentKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,163:1\n1047#2,6:164\n1047#2,6:170\n*S KotlinDebug\n*F\n+ 1 FileUploadErrorComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/ComposableSingletons$FileUploadErrorComponentKt$lambda-1$1\n*L\n142#1:164,6\n143#1:170,6\n*E\n"
)
final class ComposableSingletons$FileUploadErrorComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$FileUploadErrorComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$FileUploadErrorComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C141@4983L2,142@5019L2,138@4771L265:FileUploadErrorComponent.kt#lphomb");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               307228952,
               var2,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$FileUploadErrorComponentKt.lambda-1.<anonymous> (FileUploadErrorComponent.kt:138)"
            );
         }

         String var10000 = "Error";
         Answer$MediaAnswer$FileUploadError var10001 = new Answer$MediaAnswer$FileUploadError$FileLimitExceeded(
            CollectionsKt.listOf(new StringRes(string.intercom_upload_failed, null, 2, null))
         );
         var1.startReplaceGroup(113246203);
         ComposerKt.sourceInformation(var1, "CC(remember):FileUploadErrorComponent.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Answer$MediaAnswer$FileUploadError var11 = var10001;
            String var10 = "Error";
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$FileUploadErrorComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var20 = var12;
            var4.updateRememberedValue(var20);
            var10002 = (Function0)var20;
         } else {
            var10002 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(113247355);
         ComposerKt.sourceInformation(var1, "CC(remember):FileUploadErrorComponent.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var25 = var3;
            Answer$MediaAnswer$FileUploadError var24 = var10001;
            String var23 = var10000;
            int var21 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$FileUploadErrorComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$3$lambda$2());
            var10000 = var23;
            var10001 = var24;
            var10002 = var25;
            Object var22 = var13;
            var4.updateRememberedValue(var22);
            var10003 = var22;
         } else {
            var10003 = var7;
         }

         var3 = (Function0)var10003;
         var1.endReplaceGroup();
         FileUploadErrorComponentKt.FileUploadErrorComponent(var10000, var10001, (kotlin.jvm.functions.Function0) var10002, var3, var1, 3462);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2() {
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
