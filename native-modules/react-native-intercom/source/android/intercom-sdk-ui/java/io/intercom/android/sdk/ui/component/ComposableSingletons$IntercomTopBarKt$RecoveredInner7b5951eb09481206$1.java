package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomTopBarKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,160:1\n1047#2,6:161\n*S KotlinDebug\n*F\n+ 1 IntercomTopBar.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomTopBarKt$lambda-1$1\n*L\n124#1:161,6\n*E\n"
)
final class ComposableSingletons$IntercomTopBarKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$IntercomTopBarKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$IntercomTopBarKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C123@4517L2,118@4299L307:IntercomTopBar.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1581505149, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomTopBarKt.lambda-1.<anonymous> (IntercomTopBar.kt:118)"
            );
         }

         Object var10000 = null;
         String var10001 = "TopBar Title";
         int var10002 = drawable.intercom_ic_back;
         Object var10003 = null;
         var1.startReplaceGroup(-62762186);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomTopBar.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var13 = null;
            int var12 = var10002;
            String var11 = "TopBar Title";
            Object var10 = null;
            int var9 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$IntercomTopBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            Object var18 = var14;
            var4.updateRememberedValue(var18);
            var10004 = var18;
         } else {
            var10004 = var7;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         Function0 var15 = var3;
         Object var16 = var10003;
         int var17 = var10002;
         IntercomTopBarKt.recovered$4ca9f42ae4e1cbf3(
            (Modifier)var10000,
            var10001,
            new IntercomTopBarIcon(var17, (String)var16, var15),
            Alignment.Companion.getCenterHorizontally(),
            0L,
            0L,
            null,
            null,
            var1,
            3120,
            241
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
