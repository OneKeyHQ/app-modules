package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxErrorScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxErrorScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/ComposableSingletons$InboxErrorScreenKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,31:1\n1047#2,6:32\n*S KotlinDebug\n*F\n+ 1 InboxErrorScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/ComposableSingletons$InboxErrorScreenKt$lambda-1$1\n*L\n20#1:32,6\n*E\n"
)
final class ComposableSingletons$InboxErrorScreenKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$InboxErrorScreenKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$InboxErrorScreenKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C19@663L2,19@619L47:InboxErrorScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2040574336,
               var2,
               -1,
               "io.intercom.android.sdk.m5.inbox.ui.ComposableSingletons$InboxErrorScreenKt.lambda-1.<anonymous> (InboxErrorScreen.kt:19)"
            );
         }

         byte var10000 = 0;
         byte var10001 = 0;
         Object var10002 = null;
         byte var10003 = 0;
         var1.startReplaceGroup(337671188);
         ComposerKt.sourceInformation(var1, "CC(remember):InboxErrorScreen.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            byte var13 = 0;
            Object var12 = null;
            byte var11 = 0;
            byte var10 = 0;
            int var9 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$InboxErrorScreenKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            Object var22 = var14;
            var4.updateRememberedValue(var22);
            var10004 = var22;
         } else {
            var10004 = var7;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         Object var15 = null;
         byte var16 = 15;
         Function0 var17 = var3;
         byte var18 = var10003;
         Object var19 = var10002;
         byte var20 = var10001;
         byte var21 = var10000;
         InboxErrorScreenKt.InboxErrorScreen(
            new ErrorState$WithCTA(var21, var20, (Integer)var19, var18, var17, var16, (DefaultConstructorMarker)var15), null, var1, 0, 2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
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
