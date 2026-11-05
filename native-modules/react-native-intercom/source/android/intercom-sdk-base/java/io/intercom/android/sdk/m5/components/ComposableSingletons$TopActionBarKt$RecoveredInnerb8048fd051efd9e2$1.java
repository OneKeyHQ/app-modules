package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,255:1\n1047#2,6:256\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-3$1\n*L\n187#1:256,6\n*E\n"
)
final class ComposableSingletons$TopActionBarKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C186@7166L2,177@6762L446:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -266401790, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-3.<anonymous> (TopActionBar.kt:177)"
            );
         }

         AvatarWrapper[] var4 = new AvatarWrapper[3];
         Avatar var10004 = Avatar.create("", "HC");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         var4[0] = new AvatarWrapper(var10004, false, 2, null);
         var10004 = Avatar.create("", "AA");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         var4[1] = new AvatarWrapper(var10004, false, 2, null);
         var10004 = Avatar.create("", "SS");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         var4[2] = new AvatarWrapper(var10004, false, 2, null);
         List var3 = CollectionsKt.listOf(var4);
         int var18 = drawable.intercom_clock;
         Object var10000 = null;
         String var10001 = "Team";
         String var10002 = "Back in 3 hours";
         Integer var10003 = var18;
         List var22 = var3;
         var1.startReplaceGroup(1429562722);
         ComposerKt.sourceInformation(var1, "CC(remember):TopActionBar.kt#9igjgp");
         Composer var6 = var1;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var10005;
         if (var9 == Composer.Companion.getEmpty()) {
            List var16 = var3;
            Integer var15 = var10003;
            String var14 = "Back in 3 hours";
            String var13 = "Team";
            Object var12 = null;
            int var11 = 0;
            Function0 var17 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TopActionBarKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var10000 = var12;
            var10001 = var13;
            var10002 = var14;
            var10003 = var15;
            var22 = var16;
            Object var19 = var17;
            var6.updateRememberedValue(var19);
            var10005 = var19;
         } else {
            var10005 = var9;
         }

         Function0 var5 = (Function0)var10005;
         var1.endReplaceGroup();
         TopActionBarKt.recovered$e7704aa1c1b2200b(
            (Modifier)var10000, var10001, var10002, var10003, var22, var5, null, true, 0L, 0L, 0L, null, null, var1, 12779952, 0, 8001
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
