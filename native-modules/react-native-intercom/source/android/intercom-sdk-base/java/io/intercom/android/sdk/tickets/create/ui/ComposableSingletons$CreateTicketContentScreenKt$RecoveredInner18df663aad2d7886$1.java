package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Error;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,374:1\n1047#2,6:375\n1047#2,6:381\n1047#2,6:387\n1047#2,6:393\n1047#2,6:399\n1047#2,6:405\n*S KotlinDebug\n*F\n+ 1 CreateTicketContentScreen.kt\nio/intercom/android/sdk/tickets/create/ui/ComposableSingletons$CreateTicketContentScreenKt$lambda-4$1\n*L\n262#1:375,6\n265#1:381,6\n266#1:387,6\n267#1:393,6\n268#1:399,6\n269#1:405,6\n*E\n"
)
final class ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(
         var1, "C261@10654L2,264@10718L2,265@10751L2,266@10778L2,267@10812L2,268@10844L2,257@10420L437:CreateTicketContentScreen.kt#xh6pmz"
      );
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               125945436,
               var2,
               -1,
               "io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt.lambda-4.<anonymous> (CreateTicketContentScreen.kt:257)"
            );
         }

         byte var10000 = 0;
         byte var10001 = 0;
         Object var10002 = null;
         int var10003 = string.intercom_reload;
         var1.startReplaceGroup(98182467);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            int var17 = var10003;
            Object var16 = null;
            byte var15 = 0;
            byte var14 = 0;
            int var9 = 0;
            Function0 var18 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var14;
            var10001 = var15;
            var10002 = var16;
            var10003 = var17;
            Object var57 = var18;
            var4.updateRememberedValue(var57);
            var10004 = var57;
         } else {
            var10004 = var7;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         Object var19 = null;
         byte var20 = 7;
         Function0 var21 = var3;
         int var22 = var10003;
         Object var23 = var10002;
         byte var24 = var10001;
         byte var25 = var10000;
         ErrorState var26 = new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(var25, var24, (Integer)var23, var22, var21, var20, (DefaultConstructorMarker)var19);
         io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var83 = new io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Error(var26);
         var1.startReplaceGroup(98184515);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var32 = var1;
         boolean var37 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var32.rememberedValue();
         int var52/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var85;
         if (it$ivx == Composer.Companion.getEmpty()) {
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var10 = var83;
            int var58 = 0;
            Function0 var84 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$3$lambda$2());
            var83 = var10;
            Object var59 = var84;
            var32.updateRememberedValue(var59);
            var85 = var59;
         } else {
            var85 = it$ivx;
         }

         Function0 var27 = (Function0)var85;
         var1.endReplaceGroup();
         Function0 var86 = var27;
         var1.startReplaceGroup(98185571);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var33 = var1;
         boolean var38 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var33.rememberedValue();
         int var53/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var87;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            Function0 var11 = var27;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var68 = var83;
            int var60 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$5$lambda$4());
            var83 = var68;
            var86 = var11;
            Object var61 = var12;
            var33.updateRememberedValue(var61);
            var87 = var61;
         } else {
            var87 = it$ivxx;
         }

         Function0 var28 = (Function0)var87;
         var1.endReplaceGroup();
         Function0 var88 = var28;
         var1.startReplaceGroup(98186435);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var34 = var1;
         boolean var39 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var34.rememberedValue();
         int var54/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var89;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            Function0 var75 = var28;
            Function0 var72 = var86;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var69 = var83;
            int var62 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$7$lambda$6());
            var83 = var69;
            var86 = var72;
            var88 = var75;
            Object var63 = var13;
            var34.updateRememberedValue(var63);
            var89 = var63;
         } else {
            var89 = it$ivxxx;
         }

         Function0 var29 = (Function0)var89;
         var1.endReplaceGroup();
         Function0 var90 = var29;
         var1.startReplaceGroup(98187523);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var35 = var1;
         boolean var40 = false;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var35.rememberedValue();
         int var55/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var91;
         if (it$ivxxxx == Composer.Companion.getEmpty()) {
            Function0 var78 = var29;
            Function0 var76 = var88;
            Function0 var73 = var86;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var70 = var83;
            int var64 = 0;
            Function1 var80 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1575_0) -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$9$lambda$8((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1575_0)));
            var83 = var70;
            var86 = var73;
            var88 = var76;
            var90 = var78;
            Object var65 = var80;
            var35.updateRememberedValue(var65);
            var91 = var65;
         } else {
            var91 = it$ivxxxx;
         }

         Function1 var30 = (Function1)var91;
         var1.endReplaceGroup();
         Function1 var92 = var30;
         var1.startReplaceGroup(98188547);
         ComposerKt.sourceInformation(var1, "CC(remember):CreateTicketContentScreen.kt#9igjgp");
         Composer var36 = var1;
         boolean var41 = false;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var36.rememberedValue();
         int var56/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var10005;
         if (it$ivxxxxx == Composer.Companion.getEmpty()) {
            Function1 var81 = var30;
            Function0 var79 = var90;
            Function0 var77 = var88;
            Function0 var74 = var86;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var71 = var83;
            int var66 = 0;
            Function1 var82 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1576_0) -> io.intercom.android.sdk.tickets.create.ui.ComposableSingletons$CreateTicketContentScreenKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$11$lambda$10((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData) ((io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredExpressionArgument_1576_0)));
            var83 = var71;
            var86 = var74;
            var88 = var77;
            var90 = var79;
            var92 = var81;
            Object var67 = var82;
            var36.updateRememberedValue(var67);
            var10005 = var67;
         } else {
            var10005 = it$ivxxxxx;
         }

         Function1 var31 = (Function1)var10005;
         var1.endReplaceGroup();
         CreateTicketContentScreenKt.CreateTicketScreen(var83, var86, var88, var90, var92, var31, var1, 224688);
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

   private static final Unit invoke$lambda$7$lambda$6() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$9$lambda$8(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10(AnswerClickData var0) {
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
