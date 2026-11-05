package io.intercom.android.sdk.views.compose;

import android.content.res.Resources;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.utilities.AttributeCollectorValidatorKt;
import io.intercom.android.sdk.utilities.AttributeValidatorUtils;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTextAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/TextAttributeCollectorKt$TextAttributeCollector$6\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,367:1\n1047#2,6:368\n*S KotlinDebug\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/TextAttributeCollectorKt$TextAttributeCollector$6\n*L\n117#1:368,6\n*E\n"
)
final class TextAttributeCollectorKt$TextAttributeCollector$6 implements Function2 {
   // $VF: synthetic field
   final boolean $disabled;
   // $VF: synthetic field
   final boolean $submitted;
   // $VF: synthetic field
   final boolean $loading;
   // $VF: synthetic field
   final CornerBasedShape $shape;
   // $VF: synthetic field
   final MutableState $value$delegate;
   // $VF: synthetic field
   final Function1 $onValidationError;
   // $VF: synthetic field
   final Resources $resources;
   // $VF: synthetic field
   final AttributeData $attributeData;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;

   TextAttributeCollectorKt$TextAttributeCollector$6(
      boolean var1, boolean var2, boolean var3, CornerBasedShape var4, MutableState var5, Function1 var6, Resources var7, AttributeData var8, Function1 var9
   ) {
      this.$disabled = var1;
      this.$submitted = var2;
      this.$loading = var3;
      this.$shape = var4;
      this.$value$delegate = var5;
      this.$onValidationError = var6;
      this.$resources = var7;
      this.$attributeData = var8;
      this.$onSubmitAttribute = var9;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C116@4373L1168,111@4173L1383:TextAttributeCollector.kt#h0e4f2");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               930248561, var2, -1, "io.intercom.android.sdk.views.compose.TextAttributeCollector.<anonymous> (TextAttributeCollector.kt:111)"
            );
         }

         boolean var10000 = this.$disabled;
         boolean var10001 = this.$submitted;
         boolean var10002 = this.$loading;
         CornerBasedShape var10003 = this.$shape;
         var1.startReplaceGroup(201032765);
         ComposerKt.sourceInformation(var1, "CC(remember):TextAttributeCollector.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changed(this.$value$delegate)
            | var1.changed(this.$onValidationError)
            | var1.changedInstance(this.$resources)
            | var1.changedInstance(this.$attributeData)
            | var1.changed(this.$onSubmitAttribute);
         Function1 var6 = this.$onValidationError;
         Resources var7 = this.$resources;
         AttributeData var8 = this.$attributeData;
         Function1 var9 = this.$onSubmitAttribute;
         MutableState var10 = this.$value$delegate;
         int var11 = 0;
         Object var12 = var4.rememberedValue();
         int var13 = 0;
         Object var10004;
         if (!var5 && var12 != Composer.Companion.getEmpty()) {
            var10004 = var12;
         } else {
            CornerBasedShape var19 = var10003;
            boolean var18 = var10002;
            boolean var17 = var10001;
            boolean var16 = var10000;
            int var14 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_1668_0 = var6;
            final android.content.res.Resources recoveredCapture_1668_1 = var7;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1668_2 = var8;
            final kotlin.jvm.functions.Function1 recoveredCapture_1668_3 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_1668_4 = var10;
            Function0 var20 = ((kotlin.jvm.functions.Function0) () -> TextAttributeCollectorKt$TextAttributeCollector$6.invoke$lambda$1$lambda$0(recoveredCapture_1668_0, recoveredCapture_1668_1, recoveredCapture_1668_2, recoveredCapture_1668_3, recoveredCapture_1668_4));
            var10000 = var16;
            var10001 = var17;
            var10002 = var18;
            var10003 = var19;
            Object var15 = var20;
            var4.updateRememberedValue(var15);
            var10004 = var15;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         TextAttributeCollectorKt.access$TextAttributeTrailingComponent(var10000, var10001, var10002, var10003, var3, var1, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function1 var0, Resources var1, AttributeData var2, Function1 var3, MutableState var4) {
      String var5 = StringsKt.trim(TextAttributeCollectorKt.access$TextAttributeCollector$lambda$6(var4)).toString();
      if (var5.length() == 0) {
         String var10001 = var1.getString(string.intercom_string_is_incorrect);
         Intrinsics.checkNotNullExpressionValue(var10001, "getString(...)");
         var0.invoke(var10001);
      } else {
         Attribute var6 = var2.getAttribute();
         int var7 = AttributeValidatorUtils.validateAttribute(var5, var6.getRenderType());
         if (var7 == 0) {
            var0.invoke("");
            var3.invoke(
               AttributeData.copy$default(var2, Attribute.copy$default(var6, null, null, null, false, null, null, null, var5, 127, null), null, false, 6, null)
            );
         } else {
            Intrinsics.checkNotNull(var1);
            var0.invoke(AttributeCollectorValidatorKt.getErrorStringFromCode(var1, var7));
         }
      }

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
