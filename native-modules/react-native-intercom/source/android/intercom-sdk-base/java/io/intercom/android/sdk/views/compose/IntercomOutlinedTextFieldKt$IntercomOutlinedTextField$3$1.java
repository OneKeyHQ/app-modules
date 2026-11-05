package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.interaction.InteractionSource;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomOutlinedTextField.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomOutlinedTextField.kt\nio/intercom/android/sdk/views/compose/IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,132:1\n118#2:133\n118#2:134\n*S KotlinDebug\n*F\n+ 1 IntercomOutlinedTextField.kt\nio/intercom/android/sdk/views/compose/IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3$1\n*L\n121#1:133\n122#1:134\n*E\n"
)
final class IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3$1 implements Function2 {
   // $VF: synthetic field
   final boolean $enabled;
   // $VF: synthetic field
   final boolean $isError;
   // $VF: synthetic field
   final MutableInteractionSource $interactionSource;
   // $VF: synthetic field
   final TextFieldColors $colors;
   // $VF: synthetic field
   final Shape $shape;

   IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3$1(boolean var1, boolean var2, MutableInteractionSource var3, TextFieldColors var4, Shape var5) {
      this.$enabled = var1;
      this.$isError = var2;
      this.$interactionSource = var3;
      this.$colors = var4;
      this.$shape = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C114@5168L373:IntercomOutlinedTextField.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               888607277,
               var2,
               -1,
               "io.intercom.android.sdk.views.compose.IntercomOutlinedTextField.<anonymous>.<anonymous> (IntercomOutlinedTextField.kt:114)"
            );
         }

         OutlinedTextFieldDefaults var10000 = OutlinedTextFieldDefaults.INSTANCE;
         boolean var10001 = this.$enabled;
         boolean var10002 = this.$isError;
         InteractionSource var10003 = this.$interactionSource;
         TextFieldColors var10005 = this.$colors;
         Shape var10006 = this.$shape;
         int var3 = 1;
         int var4 = 0;
         float var10007 = Dp.recovered$95b3d103663bf060(var3);
         var3 = 1;
         var4 = 0;
         var10000.recovered$b08b5733c3fdda4b(
            var10001, var10002, var10003, null, var10005, var10006, var10007, Dp.recovered$95b3d103663bf060(var3), var1, 114819072, 8
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
