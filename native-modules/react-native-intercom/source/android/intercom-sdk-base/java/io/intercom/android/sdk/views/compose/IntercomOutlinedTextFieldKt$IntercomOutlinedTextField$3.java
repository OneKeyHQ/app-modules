package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.text.input.VisualTransformation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3 implements Function3 {
   // $VF: synthetic field
   final String $value;
   // $VF: synthetic field
   final boolean $enabled;
   // $VF: synthetic field
   final boolean $singleLine;
   // $VF: synthetic field
   final VisualTransformation $visualTransformation;
   // $VF: synthetic field
   final MutableInteractionSource $interactionSource;
   // $VF: synthetic field
   final boolean $isError;
   // $VF: synthetic field
   final Function2 $label;
   // $VF: synthetic field
   final Function2 $placeholder;
   // $VF: synthetic field
   final Function2 $leadingIcon;
   // $VF: synthetic field
   final Function2 $trailingIcon;
   // $VF: synthetic field
   final TextFieldColors $colors;
   // $VF: synthetic field
   final PaddingValues $contentPadding;
   // $VF: synthetic field
   final Shape $shape;

   IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3(
      String var1,
      boolean var2,
      boolean var3,
      VisualTransformation var4,
      MutableInteractionSource var5,
      boolean var6,
      Function2 var7,
      Function2 var8,
      Function2 var9,
      Function2 var10,
      TextFieldColors var11,
      PaddingValues var12,
      Shape var13
   ) {
      this.$value = var1;
      this.$enabled = var2;
      this.$singleLine = var3;
      this.$visualTransformation = var4;
      this.$interactionSource = var5;
      this.$isError = var6;
      this.$label = var7;
      this.$placeholder = var8;
      this.$leadingIcon = var9;
      this.$trailingIcon = var10;
      this.$colors = var11;
      this.$contentPadding = var12;
      this.$shape = var13;
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public final void invoke(Function2 var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "innerTextField");
      ComposerKt.sourceInformation(var2, "C113@5146L413,100@4601L1022:IntercomOutlinedTextField.kt#h0e4f2");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               491720992, var4, -1, "io.intercom.android.sdk.views.compose.IntercomOutlinedTextField.<anonymous> (IntercomOutlinedTextField.kt:100)"
            );
         }

         OutlinedTextFieldDefaults.INSTANCE
            .DecorationBox(
               this.$value,
               var1,
               this.$enabled,
               this.$singleLine,
               this.$visualTransformation,
               this.$interactionSource,
               this.$isError,
               this.$label,
               this.$placeholder,
               this.$leadingIcon,
               this.$trailingIcon,
               null,
               null,
               null,
               this.$colors,
               this.$contentPadding,
               ComposableLambdaKt.rememberComposableLambda(
                  888607277,
                  true,
                  new IntercomOutlinedTextFieldKt$IntercomOutlinedTextField$3$1(
                     this.$enabled, this.$isError, this.$interactionSource, this.$colors, this.$shape
                  ),
                  var2,
                  54
               ),
               var2,
               112 & var4 << 3,
               14155776,
               14336
            );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((Function2)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
