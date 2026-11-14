package io.intercom.android.sdk.ui.component;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,128:1\n1047#2,6:129\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-2$1\n*L\n109#1:129,6\n*E\n"
)
final class ComposableSingletons$IntercomButtonKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$IntercomButtonKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$IntercomButtonKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C105@3327L14,108@3437L2,104@3276L163:IntercomButton.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1036108563, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomButtonKt.lambda-2.<anonymous> (IntercomButton.kt:104)"
            );
         }

         Object var10000 = null;
         IntercomButton$Style var10001 = IntercomButton.INSTANCE.recovered$bae4000e51c7826f(0L, 0L, null, var1, 3072, 7);
         String var10002 = "Button";
         Integer var10003 = drawable.intercom_send;
         var1.startReplaceGroup(-1427650493);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomButton.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Integer var13 = var10003;
            String var12 = "Button";
            IntercomButton$Style var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$IntercomButtonKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            Object var15 = var14;
            var4.updateRememberedValue(var15);
            var10004 = var15;
         } else {
            var10004 = var7;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         IntercomButtonKt.IntercomButton((Modifier)var10000, var10001, var10002, var10003, var3, var1, 24960, 1);
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
