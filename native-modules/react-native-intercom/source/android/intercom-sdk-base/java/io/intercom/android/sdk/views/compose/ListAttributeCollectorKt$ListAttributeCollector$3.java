package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.ExposedDropdownMenuAnchorType;
import androidx.compose.material3.ExposedDropdownMenuBoxScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nListAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,224:1\n118#2:225\n118#2:232\n1047#3,6:226\n1047#3,6:233\n*S KotlinDebug\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt$ListAttributeCollector$3\n*L\n60#1:225\n107#1:232\n63#1:226,6\n104#1:233,6\n*E\n"
)
final class ListAttributeCollectorKt$ListAttributeCollector$3 implements Function3 {
   // $VF: synthetic field
   final boolean $disabled;
   // $VF: synthetic field
   final boolean $showDropdownMenu;
   // $VF: synthetic field
   final AttributeData $attributeData;
   // $VF: synthetic field
   final MutableState $value$delegate;
   // $VF: synthetic field
   final boolean $submitted;
   // $VF: synthetic field
   final boolean $loading;
   // $VF: synthetic field
   final MutableState $expanded$delegate;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;

   ListAttributeCollectorKt$ListAttributeCollector$3(
      boolean var1, boolean var2, AttributeData var3, MutableState var4, boolean var5, boolean var6, MutableState var7, Function1 var8
   ) {
      this.$disabled = var1;
      this.$showDropdownMenu = var2;
      this.$attributeData = var3;
      this.$value$delegate = var4;
      this.$submitted = var5;
      this.$loading = var6;
      this.$expanded$delegate = var7;
      this.$onSubmitAttribute = var8;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ExposedDropdownMenuBoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$ExposedDropdownMenuBox");
      ComposerKt.sourceInformation(
         var2, "C62@2530L2,66@2659L1084,56@2274L1641,104@4156L6,105@4212L6,103@4087L20,107@4280L744,101@3961L1063:ListAttributeCollector.kt#h0e4f2"
      );
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= ((var3 & 8) == 0 ? var2.changed(var1) : var2.changedInstance(var1)) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1992435426, var4, -1, "io.intercom.android.sdk.views.compose.ListAttributeCollector.<anonymous> (ListAttributeCollector.kt:56)"
            );
         }

         Modifier var10001 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var6 = 40;
         int var7 = 0;
         Modifier var5 = ExposedDropdownMenuBoxScope.recovered$9624f2bf69c1fd02(
            var1,
            SizeKt.recovered$3f6c6a76aa292717(var10001, Dp.recovered$95b3d103663bf060(var6)),
            ExposedDropdownMenuAnchorType.Companion.recovered$c79194ef042af8ca(),
            false,
            2,
            null
         );
         String var23 = ListAttributeCollectorKt.access$ListAttributeCollector$lambda$7(this.$value$delegate);
         boolean var25 = !this.$disabled;
         String var10000 = var23;
         var2.startReplaceGroup(-742063936);
         ComposerKt.sourceInformation(var2, "CC(remember):ListAttributeCollector.kt#9igjgp");
         Composer var9 = var2;
         int $this$dp$ivx = 0;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         kotlin.jvm.functions.Function1 var39;
         if (var12 == Composer.Companion.getEmpty()) {
            String var19 = var23;
            int var14 = 0;
            var39 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1652_0) -> ListAttributeCollectorKt$ListAttributeCollector$3.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_1652_0));
            var10000 = var19;
            Object var34 = var39;
            var9.updateRememberedValue(var34);
            var39 = (Function1)var34;
         } else {
            var39 = (Function1)var12;
         }

         Function1 var8 = var39;
         var2.endReplaceGroup();
         IntercomOutlinedTextFieldKt.IntercomOutlinedTextField(
            var10000,
            var8,
            var5,
            var25,
            true,
            null,
            null,
            ComposableSingletons$ListAttributeCollectorKt.INSTANCE.recovered$eac478aa65045e5d(),
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -1903726860, true, new ListAttributeCollectorKt$ListAttributeCollector$3$2(this.$submitted, this.$loading), var2, 54
            ),
            false,
            null,
            null,
            null,
            true,
            0,
            0,
            null,
            null,
            null,
            null,
            var2,
            817913904,
            24576,
            0,
            2080096
         );
         if (this.$showDropdownMenu) {
            boolean var22 = !this.$attributeData.isFormDisabled() && ListAttributeCollectorKt.access$ListAttributeCollector$lambda$3(this.$expanded$delegate);
            long var24 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            CornerBasedShape var26 = IntercomTheme.INSTANCE.getShapes(var2, IntercomTheme.$stable).getSmall();
            $this$dp$ivx = 8;
            var11 = 0;
            float var27 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            ExposedDropdownMenuBoxScope var37 = var1;
            boolean var40 = var22;
            var2.startReplaceGroup(-742014094);
            ComposerKt.sourceInformation(var2, "CC(remember):ListAttributeCollector.kt#9igjgp");
            Composer var31 = var2;
            var12 = false;
            MutableState var33 = this.$expanded$delegate;
            int $i$f$cachex = 0;
            Object it$ivx = var31.rememberedValue();
            int var16/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10002;
            if (it$ivx == Composer.Companion.getEmpty()) {
               boolean var20 = var22;
               ExposedDropdownMenuBoxScope var36 = var1;
               int var17 = 0;
               final androidx.compose.runtime.MutableState recoveredCapture_1653_0 = var33;
               Function0 var21 = ((kotlin.jvm.functions.Function0) () -> ListAttributeCollectorKt$ListAttributeCollector$3.invoke$lambda$3$lambda$2(recoveredCapture_1653_0));
               var37 = var36;
               var40 = var20;
               Object var18 = var21;
               var31.updateRememberedValue(var18);
               var10002 = var18;
            } else {
               var10002 = it$ivx;
            }

            Function0 var29 = (Function0)var10002;
            var2.endReplaceGroup();
            var37.recovered$f04be1f200cd8e73(
               var40,
               var29,
               null,
               null,
               false,
               var26,
               var24,
               0.0F,
               var27,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  -1185294213,
                  true,
                  new ListAttributeCollectorKt$ListAttributeCollector$3$4(
                     this.$attributeData, this.$value$delegate, this.$onSubmitAttribute, this.$expanded$delegate
                  ),
                  var2,
                  54
               ),
               var2,
               100663344,
               6 | ExposedDropdownMenuBoxScope.$stable << 3 | 112 & var4 << 3,
               668
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(MutableState var0) {
      ListAttributeCollectorKt.access$ListAttributeCollector$lambda$4(var0, false);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ExposedDropdownMenuBoxScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
