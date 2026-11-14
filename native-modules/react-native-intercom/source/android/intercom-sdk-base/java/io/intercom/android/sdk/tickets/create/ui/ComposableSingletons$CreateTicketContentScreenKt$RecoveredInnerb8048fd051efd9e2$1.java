package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Loading;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,374:1\n1047#2,6:375\n1047#2,6:381\n1047#2,6:387\n1047#2,6:393\n1047#2,6:399\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-3$1\n*L\n245#1:375,6\n246#1:381,6\n247#1:387,6\n248#1:393,6\n249#1:399,6\n*E\n"
)
final class ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C244@10172L2,245@10205L2,246@10232L2,247@10266L2,248@10298L2,242@10049L262:CreateTicketContentScreen.kt#xh6pmz");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -426521688,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.lambda-3.<anonymous> (CreateTicketContentScreen.kt:242)"
            );
         }

         CreateTicketViewModel$CreateTicketFormUiState var10000 = CreateTicketViewModel$CreateTicketFormUiState$Loading.INSTANCE;
         var1.startReplaceGroup(-1409384766);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var62;
         if (var7 == Composer.Companion.getEmpty()) {
            CreateTicketViewModel$CreateTicketFormUiState var10 = var10000;
            int var9 = 0;
            var62 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            Object var40 = var62;
            var4.updateRememberedValue(var40);
            var62 = (Function0)var40;
         } else {
            var62 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var62;
         var1.endReplaceGroup();
         var62 = var3;
         var1.startReplaceGroup(-1409383710);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var11 = var3;
            CreateTicketViewModel$CreateTicketFormUiState var49 = var10000;
            int var41 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$3$lambda$2());
            var10000 = var49;
            var62 = var11;
            Object var42 = var12;
            var4.updateRememberedValue(var42);
            var10002 = (Function0)var42;
         } else {
            var10002 = (Function0)var7;
         }

         var3 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(-1409382846);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var56 = var3;
            Function0 var53 = (kotlin.jvm.functions.Function0) var62;
            CreateTicketViewModel$CreateTicketFormUiState var50 = var10000;
            int var43 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$5$lambda$4());
            var10000 = var50;
            var62 = var53;
            var10002 = var56;
            Object var44 = var13;
            var4.updateRememberedValue(var44);
            var10003 = (Function0)var44;
         } else {
            var10003 = (Function0)var7;
         }

         var3 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var3;
         var1.startReplaceGroup(-1409381758);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var59 = var3;
            Function0 var57 = (kotlin.jvm.functions.Function0) var10002;
            Function0 var54 = (kotlin.jvm.functions.Function0) var62;
            CreateTicketViewModel$CreateTicketFormUiState var51 = var10000;
            int var45 = 0;
            Function1 var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1584_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$7$lambda$6((java.lang.String)recoveredArgument_1584_0));
            var10000 = var51;
            var62 = var54;
            var10002 = var57;
            var10003 = var59;
            Object var46 = var14;
            var4.updateRememberedValue(var46);
            var10004 = (Function1)var46;
         } else {
            var10004 = (Function1)var7;
         }

         Function1 var18 = (kotlin.jvm.functions.Function1) var10004;
         var1.endReplaceGroup();
         var10004 = var18;
         var1.startReplaceGroup(-1409380734);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var61 = var18;
            Function0 var60 = (kotlin.jvm.functions.Function0) var10003;
            Function0 var58 = (kotlin.jvm.functions.Function0) var10002;
            Function0 var55 = (kotlin.jvm.functions.Function0) var62;
            CreateTicketViewModel$CreateTicketFormUiState var52 = var10000;
            int var47 = 0;
            Function1 var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1585_0) -> ComposableSingletons$CreateTicketContentScreenKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$9$lambda$8((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredArgument_1585_0));
            var10000 = var52;
            var62 = var55;
            var10002 = var58;
            var10003 = var60;
            var10004 = var61;
            Object var48 = var15;
            var4.updateRememberedValue(var48);
            var10005 = var48;
         } else {
            var10005 = var7;
         }

         Function1 var19 = (Function1)var10005;
         var1.endReplaceGroup();
         CreateTicketContentScreenKt.CreateTicketScreen(var10000, (kotlin.jvm.functions.Function0) var62, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, (kotlin.jvm.functions.Function1) var10004, var19, var1, 224694);
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

   private static final Unit invoke$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$9$lambda$8(AnswerClickData var0) {
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
