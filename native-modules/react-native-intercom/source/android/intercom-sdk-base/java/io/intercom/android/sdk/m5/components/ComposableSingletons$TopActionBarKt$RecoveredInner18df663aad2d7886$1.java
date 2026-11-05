package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
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
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,255:1\n1047#2,6:256\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-4$1\n*L\n204#1:256,6\n*E\n"
)
final class ComposableSingletons$TopActionBarKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C203@7636L2,196@7328L350:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               646731538, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-4.<anonymous> (TopActionBar.kt:196)"
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
         Object var10000 = null;
         String var10001 = "Team";
         Object var10002 = null;
         Object var10003 = null;
         List var21 = var3;
         var1.startReplaceGroup(-1357837725);
         ComposerKt.sourceInformation(var1, "CC(remember):TopActionBar.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10005;
         if (var8 == Composer.Companion.getEmpty()) {
            List var15 = var3;
            Object var14 = null;
            Object var13 = null;
            String var12 = "Team";
            Object var11 = null;
            int var10 = 0;
            Function0 var16 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TopActionBarKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var11;
            var10001 = var12;
            var10002 = var13;
            var10003 = var14;
            var21 = var15;
            Object var18 = var16;
            var5.updateRememberedValue(var18);
            var10005 = var18;
         } else {
            var10005 = var8;
         }

         Function0 var17 = (Function0)var10005;
         var1.endReplaceGroup();
         TopActionBarKt.recovered$e7704aa1c1b2200b(
            (Modifier)var10000, var10001, (String)var10002, (Integer)var10003, var21, var17, null, true, 0L, 0L, 0L, null, null, var1, 12779568, 0, 8013
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
