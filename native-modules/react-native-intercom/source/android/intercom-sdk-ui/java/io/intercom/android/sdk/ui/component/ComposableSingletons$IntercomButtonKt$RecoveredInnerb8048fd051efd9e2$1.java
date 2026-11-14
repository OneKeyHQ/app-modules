package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,128:1\n118#2:129\n1047#3,6:130\n*S KotlinDebug\n*F\n+ 1 IntercomButton.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$IntercomButtonKt$lambda-3$1\n*L\n123#1:129\n125#1:130,6\n*E\n"
)
final class ComposableSingletons$IntercomButtonKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$IntercomButtonKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$IntercomButtonKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C119@3680L176,124@3868L2,116@3545L325:IntercomButton.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -124924744, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$IntercomButtonKt.lambda-3.<anonymous> (IntercomButton.kt:116)"
            );
         }

         int var3 = drawable.intercom_send;
         IntercomButton var10000 = IntercomButton.INSTANCE;
         long var10001 = Color.Companion.recovered$95989221dad41c4d();
         long var10002 = Color.Companion.recovered$235b30c090a399c5();
         int var5 = 1;
         int var6 = 0;
         IntercomButton$Style var4 = var10000.recovered$6b6eaea65c87a6df(
            var10001,
            var10002,
            BorderStrokeKt.recovered$91879a1bef2fb536(Dp.recovered$95b3d103663bf060(var5), Color.Companion.recovered$e94007a61f9d70c2()),
            var1,
            3510,
            0
         );
         Object var20 = null;
         IntercomButton$Style var21 = var4;
         String var22 = "Button";
         Integer var10003 = var3;
         var1.startReplaceGroup(79915108);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomButton.kt#9igjgp");
         Composer var18 = var1;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var18.rememberedValue();
         int var10 = 0;
         Object var10004;
         if (var9 == Composer.Companion.getEmpty()) {
            Integer var15 = var10003;
            String var14 = "Button";
            IntercomButton$Style var13 = var4;
            Object var12 = null;
            int var11 = 0;
            Function0 var16 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$IntercomButtonKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var20 = var12;
            var21 = var13;
            var22 = var14;
            var10003 = var15;
            Object var19 = var16;
            var18.updateRememberedValue(var19);
            var10004 = var19;
         } else {
            var10004 = var9;
         }

         Function0 var17 = (Function0)var10004;
         var1.endReplaceGroup();
         IntercomButtonKt.IntercomButton((Modifier)var20, var21, var22, var10003, var17, var1, 24960, 1);
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
