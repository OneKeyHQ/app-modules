package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,374:1\n1047#2,6:375\n1047#2,6:381\n1047#2,6:387\n1047#2,6:393\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-5$1\n*L\n288#1:375,6\n289#1:381,6\n290#1:387,6\n291#1:393,6\n*E\n"
)
final class ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C287@11438L2,288@11465L2,289@11499L2,290@11531L2,277@10979L565:CreateTicketContentScreen.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -259524439,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.lambda-5.<anonymous> (CreateTicketContentScreen.kt:277)"
            );
         }

         List var3 = CollectionsKt.take(CreateTicketContentScreenKt.access$getQuestions$p(), 4);
         List var4 = var3;
         String var13 = "All form attribute types";
         Object var10 = null;
         int var5 = 0;
         ((QuestionState)CollectionsKt.first(var4))
            .setValidationError(new ValidationError$ValidationStringError(string.intercom_surveys_email_error, null, 2, null));
         Unit var14 = Unit.INSTANCE;
         Object var10000 = var10;
         boolean var16 = false;
         boolean var17 = false;
         List var18 = var3;
         String var19 = var13;
         CreateTicketViewModel$CreateTicketFormUiState$Content var10001 = new CreateTicketViewModel$CreateTicketFormUiState$Content(var19, var18, var17, var16);
         var1.startReplaceGroup(1605759364);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var24 = var1;
         boolean var28 = false;
         int var6 = 0;
         Object var7 = var24.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            CreateTicketViewModel$CreateTicketFormUiState$Content var11 = var10001;
            var10 = var10;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$2$lambda$1());
            var10000 = var10;
            var10001 = var11;
            Object var41 = var12;
            var24.updateRememberedValue(var41);
            var10002 = (Function0)var41;
         } else {
            var10002 = (Function0)var7;
         }

         Function0 var20 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var20;
         var1.startReplaceGroup(1605760228);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var25 = var1;
         var28 = false;
         var6 = 0;
         var7 = var25.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var55 = var20;
            CreateTicketViewModel$CreateTicketFormUiState$Content var52 = var10001;
            var10 = var10000;
            int var42 = 0;
            Function0 var58 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$4$lambda$3());
            var10000 = var10;
            var10001 = var52;
            var10002 = var55;
            Object var43 = var58;
            var25.updateRememberedValue(var43);
            var10003 = (Function0)var43;
         } else {
            var10003 = (Function0)var7;
         }

         Function0 var21 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var21;
         var1.startReplaceGroup(1605761316);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var26 = var1;
         var28 = false;
         var6 = 0;
         var7 = var26.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var59 = var21;
            Function0 var56 = (kotlin.jvm.functions.Function0) var10002;
            CreateTicketViewModel$CreateTicketFormUiState$Content var53 = var10001;
            var10 = var10000;
            int var44 = 0;
            Function1 var61 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1570_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$6$lambda$5((java.lang.String)recoveredArgument_1570_0));
            var10000 = var10;
            var10001 = var53;
            var10002 = var56;
            var10003 = var59;
            Object var45 = var61;
            var26.updateRememberedValue(var45);
            var10004 = (Function1)var45;
         } else {
            var10004 = (Function1)var7;
         }

         Function1 var22 = (kotlin.jvm.functions.Function1) var10004;
         var1.endReplaceGroup();
         var10004 = var22;
         var1.startReplaceGroup(1605762340);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var27 = var1;
         var28 = false;
         var6 = 0;
         var7 = var27.rememberedValue();
         var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var62 = var22;
            Function0 var60 = (kotlin.jvm.functions.Function0) var10003;
            Function0 var57 = (kotlin.jvm.functions.Function0) var10002;
            CreateTicketViewModel$CreateTicketFormUiState$Content var54 = var10001;
            var10 = var10000;
            int var46 = 0;
            Function1 var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1571_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$8$lambda$7((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredArgument_1571_0));
            var10000 = var10;
            var10001 = var54;
            var10002 = var57;
            var10003 = var60;
            var10004 = var62;
            Object var47 = var15;
            var27.updateRememberedValue(var47);
            var10005 = var47;
         } else {
            var10005 = var7;
         }

         Function1 var23 = (Function1)var10005;
         var1.endReplaceGroup();
         CreateTicketContentScreenKt.CreateTicketContentScreen((Modifier)var10000, var10001, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, (kotlin.jvm.functions.Function1) var10004, var23, var1, 224640, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$4$lambda$3() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$8$lambda$7(AnswerClickData var0) {
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
