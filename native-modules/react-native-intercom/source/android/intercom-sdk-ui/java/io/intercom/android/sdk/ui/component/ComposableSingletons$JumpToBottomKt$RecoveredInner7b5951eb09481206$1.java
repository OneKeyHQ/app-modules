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
   "SMAP\nJumpToBottom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$JumpToBottomKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,137:1\n1047#2,6:138\n*S KotlinDebug\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/ComposableSingletons$JumpToBottomKt$lambda-1$1\n*L\n110#1:138,6\n*E\n"
)
final class ComposableSingletons$JumpToBottomKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$JumpToBottomKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$JumpToBottomKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C109@4135L2,109@4112L26:JumpToBottom.kt#yr602f");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               217802459, var2, -1, "io.intercom.android.sdk.ui.component.ComposableSingletons$JumpToBottomKt.lambda-1.<anonymous> (JumpToBottom.kt:109)"
            );
         }

         Object var10000 = null;
         long var10001 = 0L;
         long var10002 = 0L;
         Object var10003 = null;
         long var10004 = 0L;
         long var10005 = 0L;
         var1.startReplaceGroup(1493622213);
         ComposerKt.sourceInformation(var1, "CC(remember):JumpToBottom.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10006;
         if (var7 == Composer.Companion.getEmpty()) {
            long var18 = 0L;
            long var16 = 0L;
            Object var15 = null;
            long var13 = 0L;
            long var11 = 0L;
            Object var10 = null;
            int var9 = 0;
            Function0 var20 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$JumpToBottomKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var13;
            var10003 = var15;
            var10004 = var16;
            var10005 = var18;
            Object var21 = var20;
            var4.updateRememberedValue(var21);
            var10006 = var21;
         } else {
            var10006 = var7;
         }

         Function0 var3 = (Function0)var10006;
         var1.endReplaceGroup();
         JumpToBottomKt.recovered$611c8eb088dfc7a6((Modifier)var10000, var10001, var10002, (String)var10003, var10004, var10005, var3, var1, 1572864, 63);
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
