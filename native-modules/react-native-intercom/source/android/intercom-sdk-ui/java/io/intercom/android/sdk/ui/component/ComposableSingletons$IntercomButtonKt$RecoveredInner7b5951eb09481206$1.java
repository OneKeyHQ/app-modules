package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,128:1\n1047#2,6:129\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-1$1\n*L\n97#1:129,6\n*E\n"
)
final class ComposableSingletons$IntercomButtonKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$IntercomButtonKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$IntercomButtonKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C95@3127L14,96@3153L2,93@3047L108:IntercomButton.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2136303672, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomButtonKt.lambda-1.<anonymous> (IntercomButton.kt:93)"
            );
         }

         IntercomButton$Style var3 = IntercomButton.INSTANCE.recovered$bae4000e51c7826f(0L, 0L, null, var1, 3072, 7);
         Object var10000 = null;
         IntercomButton$Style var10001 = var3;
         String var10002 = "Button";
         Object var10003 = null;
         var1.startReplaceGroup(1359755906);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomButton.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10004;
         if (var8 == Composer.Companion.getEmpty()) {
            Object var14 = null;
            String var13 = "Button";
            IntercomButton$Style var12 = var3;
            Object var11 = null;
            int var10 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$IntercomButtonKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var11;
            var10001 = var12;
            var10002 = var13;
            var10003 = var14;
            Object var16 = var15;
            var5.updateRememberedValue(var16);
            var10004 = var16;
         } else {
            var10004 = var8;
         }

         Function0 var4 = (Function0)var10004;
         var1.endReplaceGroup();
         IntercomButtonKt.IntercomButton((Modifier)var10000, var10001, var10002, (Integer)var10003, var4, var1, 24960, 9);
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
