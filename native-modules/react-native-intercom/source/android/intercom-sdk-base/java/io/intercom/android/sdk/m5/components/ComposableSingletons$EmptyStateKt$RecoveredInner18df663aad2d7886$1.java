package io.intercom.android.sdk.m5.components;

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
   "SMAP\nEmptyState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EmptyState.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$EmptyStateKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,107:1\n1047#2,6:108\n*S KotlinDebug\n*F\n+ 1 EmptyState.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$EmptyStateKt$lambda-4$1\n*L\n102#1:108,6\n*E\n"
)
final class ComposableSingletons$EmptyStateKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$EmptyStateKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$EmptyStateKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@3471L2,98@3292L181:EmptyState.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2062436936, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$EmptyStateKt.lambda-4.<anonymous> (EmptyState.kt:98)"
            );
         }

         String var10000 = "Send us a message";
         Object var10001 = null;
         Integer var10002 = drawable.intercom_send;
         var1.startReplaceGroup(1386990567);
         ComposerKt.sourceInformation(var1, "CC(remember):EmptyState.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Integer var12 = var10002;
            Object var11 = null;
            String var10 = "Send us a message";
            int var9 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$EmptyStateKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var14 = var13;
            var4.updateRememberedValue(var14);
            var10003 = var14;
         } else {
            var10003 = var7;
         }

         Function0 var3 = (Function0)var10003;
         var1.endReplaceGroup();
         IntercomPrimaryButtonKt.IntercomPrimaryButton(var10000, (Modifier)var10001, var10002, var3, var1, 3078, 2);
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
