package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-6$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,255:1\n1047#2,6:256\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-6$1\n*L\n228#1:256,6\n*E\n"
)
final class ComposableSingletons$TopActionBarKt$RecoveredInner0c8347b84394e1a9$1 implements Function2 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInner0c8347b84394e1a9$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInner0c8347b84394e1a9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C227@8153L2,227@8108L48:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1856141887, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-6.<anonymous> (TopActionBar.kt:227)"
            );
         }

         Object var10000 = null;
         String var10001 = "Hannah";
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var1.startReplaceGroup(1657282437);
         ComposerKt.sourceInformation(var1, "CC(remember):TopActionBar.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var14 = null;
            Object var13 = null;
            Object var12 = null;
            String var11 = "Hannah";
            Object var10 = null;
            int var9 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TopActionBarKt$RecoveredInner0c8347b84394e1a9$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            var10004 = var14;
            Object var16 = var15;
            var4.updateRememberedValue(var16);
            var10005 = var16;
         } else {
            var10005 = var7;
         }

         Function0 var3 = (Function0)var10005;
         var1.endReplaceGroup();
         TopActionBarKt.recovered$e7704aa1c1b2200b(
            (Modifier)var10000, var10001, (String)var10002, (Integer)var10003, (List)var10004, var3, null, false, 0L, 0L, 0L, null, null, var1, 196656, 0, 8157
         );
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
