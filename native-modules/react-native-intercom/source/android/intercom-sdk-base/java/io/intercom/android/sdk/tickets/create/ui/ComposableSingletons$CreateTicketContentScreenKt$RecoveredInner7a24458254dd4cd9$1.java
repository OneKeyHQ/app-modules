package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,374:1\n1047#2,6:375\n1047#2,6:381\n1047#2,6:387\n1047#2,6:393\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-2$1\n*L\n231#1:375,6\n232#1:381,6\n233#1:387,6\n234#1:393,6\n*E\n"
)
final class ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C230@9832L2,231@9859L2,232@9893L2,233@9925L2,223@9555L383:CreateTicketContentScreen.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               458285887,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.lambda-2.<anonymous> (CreateTicketContentScreen.kt:223)"
            );
         }

         Object var10000 = null;
         CreateTicketViewModel$CreateTicketFormUiState$Content var10001 = new CreateTicketViewModel$CreateTicketFormUiState$Content(
            "All form attribute types", CreateTicketContentScreenKt.access$getQuestions$p(), false, true
         );
         var1.startReplaceGroup(1378019841);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            CreateTicketViewModel$CreateTicketFormUiState$Content var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var34 = var12;
            var4.updateRememberedValue(var34);
            var10002 = (Function0)var34;
         } else {
            var10002 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(1378020705);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var47 = var3;
            CreateTicketViewModel$CreateTicketFormUiState$Content var44 = var10001;
            Object var41 = var10000;
            int var35 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$3$lambda$2());
            var10000 = var41;
            var10001 = var44;
            var10002 = var47;
            Object var36 = var13;
            var4.updateRememberedValue(var36);
            var10003 = (Function0)var36;
         } else {
            var10003 = (Function0)var7;
         }

         var3 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var3;
         var1.startReplaceGroup(1378021793);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var50 = var3;
            Function0 var48 = (kotlin.jvm.functions.Function0) var10002;
            CreateTicketViewModel$CreateTicketFormUiState$Content var45 = var10001;
            Object var42 = var10000;
            int var37 = 0;
            Function1 var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1579_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$5$lambda$4((java.lang.String)recoveredArgument_1579_0));
            var10000 = var42;
            var10001 = var45;
            var10002 = var48;
            var10003 = var50;
            Object var38 = var14;
            var4.updateRememberedValue(var38);
            var10004 = (Function1)var38;
         } else {
            var10004 = (Function1)var7;
         }

         Function1 var17 = (kotlin.jvm.functions.Function1) var10004;
         var1.endReplaceGroup();
         var10004 = var17;
         var1.startReplaceGroup(1378022817);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var52 = var17;
            Function0 var51 = (kotlin.jvm.functions.Function0) var10003;
            Function0 var49 = (kotlin.jvm.functions.Function0) var10002;
            CreateTicketViewModel$CreateTicketFormUiState$Content var46 = var10001;
            Object var43 = var10000;
            int var39 = 0;
            Function1 var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1580_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$7$lambda$6((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredArgument_1580_0));
            var10000 = var43;
            var10001 = var46;
            var10002 = var49;
            var10003 = var51;
            var10004 = var52;
            Object var40 = var15;
            var4.updateRememberedValue(var40);
            var10005 = var40;
         } else {
            var10005 = var7;
         }

         Function1 var18 = (Function1)var10005;
         var1.endReplaceGroup();
         CreateTicketContentScreenKt.CreateTicketContentScreen((Modifier)var10000, var10001, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, (kotlin.jvm.functions.Function1) var10004, var18, var1, 224640, 1);
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

   private static final Unit invoke$lambda$5$lambda$4(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6(AnswerClickData var0) {
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
