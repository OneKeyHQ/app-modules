package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.material3.AndroidMenu_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.Attribute;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nListAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3$4\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,224:1\n1869#2:225\n1870#2:232\n1047#3,6:226\n*S KotlinDebug\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3$4\n*L\n109#1:225\n109#1:232\n111#1:226,6\n*E\n"
)
final class ListAttributeCollectorKt$ListAttributeCollector$3$4 implements Function3 {
   // $VF: synthetic field
   final AttributeData $attributeData;
   // $VF: synthetic field
   final MutableState $value$delegate;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;
   // $VF: synthetic field
   final MutableState $expanded$delegate;

   ListAttributeCollectorKt$ListAttributeCollector$3$4(AttributeData var1, MutableState var2, Function1 var3, MutableState var4) {
      this.$attributeData = var1;
      this.$value$delegate = var2;
      this.$onSubmitAttribute = var3;
      this.$expanded$delegate = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$ExposedDropdownMenu");
      ComposerKt.sourceInformation(var2, "C*117@4758L211,110@4423L302,109@4371L621:ListAttributeCollector.kt#h0e4f2");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1185294213, var3, -1, "io.intercom.android.sdk.views.compose.ListAttributeCollector.<anonymous>.<anonymous> (ListAttributeCollector.kt:108)"
            );
         }

         List var10000 = this.$attributeData.getAttribute().getOptions();
         if (var10000 != null) {
            Iterable var4 = var10000;
            MutableState var5 = this.$value$delegate;
            Function1 var6 = this.$onSubmitAttribute;
            AttributeData var7 = this.$attributeData;
            MutableState var8 = this.$expanded$delegate;
            int var9 = 0;

            for (Object var11 : var4) {
               String var12 = (String)var11;
               int var13 = 0;
               Function2 var23 = ComposableLambdaKt.rememberComposableLambda(
                  1941734396, true, new ListAttributeCollectorKt$ListAttributeCollector$3$4$1$1(var12), var2, 54
               );
               var2.startReplaceGroup(-1795403254);
               ComposerKt.sourceInformation(var2, "CC(remember):ListAttributeCollector.kt#9igjgp");
               Composer var14 = var2;
               boolean var15 = var2.changed(var5) | var2.changed(var12) | var2.changed(var6) | var2.changedInstance(var7);
               int var16 = 0;
               Object var17 = var14.rememberedValue();
               int var18 = 0;
               kotlin.jvm.functions.Function0 var24;
               if (!var15 && var17 != Composer.Companion.getEmpty()) {
                  var24 = (Function0)var17;
               } else {
                  Function2 var19 = var23;
                  int var20 = 0;
                  final java.lang.String recoveredCapture_1654_0 = var12;
                  final kotlin.jvm.functions.Function1 recoveredCapture_1654_1 = var6;
                  final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1654_2 = var7;
                  final androidx.compose.runtime.MutableState recoveredCapture_1654_3 = var5;
                  final androidx.compose.runtime.MutableState recoveredCapture_1654_4 = var8;
                  var24 = ((kotlin.jvm.functions.Function0) () -> ListAttributeCollectorKt$ListAttributeCollector$3$4.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_1654_0, recoveredCapture_1654_1, recoveredCapture_1654_2, recoveredCapture_1654_3, recoveredCapture_1654_4));
                  var23 = var19;
                  Object var21 = var24;
                  var14.updateRememberedValue(var21);
                  var24 = (Function0)var21;
               }

               Function0 var22 = var24;
               var2.endReplaceGroup();
               AndroidMenu_androidKt.DropdownMenuItem(var23, var22, null, null, null, false, null, null, null, var2, 6, 508);
            }
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(String var0, Function1 var1, AttributeData var2, MutableState var3, MutableState var4) {
      ListAttributeCollectorKt.access$ListAttributeCollector$lambda$8(var3, var0);
      ListAttributeCollectorKt.access$ListAttributeCollector$lambda$4(var4, false);
      var1.invoke(
         AttributeData.copy$default(
            var2, Attribute.copy$default(var2.getAttribute(), null, null, null, false, null, null, null, var0, 127, null), null, false, 6, null
         )
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
