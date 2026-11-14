package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
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
   "SMAP\nFooterNotice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$FooterNoticeKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,217:1\n1047#2,6:218\n*S KotlinDebug\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$FooterNoticeKt$lambda-4$1\n*L\n197#1:218,6\n*E\n"
)
final class ComposableSingletons$FooterNoticeKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$FooterNoticeKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$FooterNoticeKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C196@5799L2,191@5574L238:FooterNotice.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -759832452, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$FooterNoticeKt.lambda-4.<anonymous> (FooterNotice.kt:191)"
            );
         }

         String var10000 = "The team will reply in 1314 minutes";
         Avatar var10003 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10003, "create(...)");
         List var10001 = CollectionsKt.listOf(new AvatarWrapper(var10003, false, 2, null));
         var1.startReplaceGroup(460333816);
         ComposerKt.sourceInformation(var1, "CC(remember):FooterNotice.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            List var11 = var10001;
            String var10 = "The team will reply in 1314 minutes";
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$FooterNoticeKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var13 = var12;
            var4.updateRememberedValue(var13);
            var10002 = var13;
         } else {
            var10002 = var7;
         }

         Function0 var3 = (Function0)var10002;
         var1.endReplaceGroup();
         FooterNoticeKt.access$FooterNoticePillWithoutAnimation(var10000, var10001, var3, var1, 390);
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
