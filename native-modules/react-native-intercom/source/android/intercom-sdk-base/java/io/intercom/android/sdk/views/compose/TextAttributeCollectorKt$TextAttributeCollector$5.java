package io.intercom.android.sdk.views.compose;

import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.CountryAreaCode;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class TextAttributeCollectorKt$TextAttributeCollector$5 implements Function2 {
   // $VF: synthetic field
   final AttributeData $attributeData;
   // $VF: synthetic field
   final CountryAreaCode $countryAreaCode;

   TextAttributeCollectorKt$TextAttributeCollector$5(AttributeData var1, CountryAreaCode var2) {
      this.$attributeData = var1;
      this.$countryAreaCode = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C102@3949L6,100@3824L157:TextAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1290485581, var2, -1, "io.intercom.android.sdk.views.compose.TextAttributeCollector.<anonymous> (TextAttributeCollector.kt:100)"
            );
         }

         AttributeData var10000 = this.$attributeData;
         CountryAreaCode var3 = this.$countryAreaCode;
         Intrinsics.checkNotNull(var3);
         TextKt.recovered$5b114ab32a4f4190(
            TextAttributeCollectorKt.access$getHint(var10000, var3),
            null,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            null,
            var1,
            0,
            0,
            262138
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
