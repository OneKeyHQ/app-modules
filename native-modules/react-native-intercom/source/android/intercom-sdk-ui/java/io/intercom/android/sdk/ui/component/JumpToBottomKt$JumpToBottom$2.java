package io.intercom.android.sdk.ui.component;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.core.MutableTransitionState;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import io.intercom.android.sdk.ui.common.TransitionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nJumpToBottom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$2\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,137:1\n1047#2,6:138\n75#3:144\n1#4:145\n*S KotlinDebug\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$2\n*L\n60#1:138,6\n66#1:144\n*E\n"
)
final class JumpToBottomKt$JumpToBottom$2 implements Function3 {
   // $VF: synthetic field
   final Function0 $onClick;
   // $VF: synthetic field
   final long $backgroundColor;
   // $VF: synthetic field
   final long $contentColor;

   JumpToBottomKt$JumpToBottom$2(Function0 var1, long var2, long var4) {
      this.$onClick = var1;
      this.$backgroundColor = var2;
      this.$contentColor = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BadgedBox");
      ComposerKt.sourceInformation(var2, "C59@2311L191,*65@2549L7,69@2752L963,66@2603L1112:JumpToBottom.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1814183329, var3, -1, "io.intercom.android.sdk.ui.component.JumpToBottom.<anonymous> (JumpToBottom.kt:58)");
         }

         var2.startReplaceGroup(-1483768055);
         ComposerKt.sourceInformation(var2, "CC(remember):JumpToBottom.kt#9igjgp");
         Composer var6 = var2;
         int var7 = 0;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (var9 == Composer.Companion.getEmpty()) {
            int var11 = 0;
            MutableTransitionState var12 = new MutableTransitionState(false);
            MutableTransitionState var13 = var12;
            int var14 = 0;
            var13.setTargetState(true);
            Object var22 = var12;
            var6.updateRememberedValue(var22);
            var10000 = var22;
         } else {
            var10000 = var9;
         }

         MutableTransitionState var5 = (MutableTransitionState)var10000;
         var2.endReplaceGroup();
         MutableTransitionState var4 = var5;
         CompositionLocal var16 = CompositionLocalsKt.getLocalDensity();
         var7 = 0;
         var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         var9 = var2.consume(var16);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Density var18 = (Density)var9;
         var8 = 0;
         int var15 = (int)var18.recovered$245d7c913f591981(JumpToBottomKt.getJumpBottomPadding());
         AnimatedVisibilityKt.AnimatedVisibility(
            var4,
            null,
            TransitionsKt.floatingButtonEnterTransition(var15),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               600504953, true, new JumpToBottomKt$JumpToBottom$2$1(this.$onClick, this.$backgroundColor, this.$contentColor), var2, 54
            ),
            var2,
            196608 | MutableTransitionState.$stable,
            26
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
      this.invoke((BoxScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
