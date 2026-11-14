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
   "SMAP\nFooterNotice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$FooterNoticeKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,217:1\n1047#2,6:218\n*S KotlinDebug\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$FooterNoticeKt$lambda-5$1\n*L\n213#1:218,6\n*E\n"
)
final class ComposableSingletons$FooterNoticeKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$FooterNoticeKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$FooterNoticeKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C212@6276L2,205@5921L368:FooterNotice.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1035678920, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$FooterNoticeKt.lambda-5.<anonymous> (FooterNotice.kt:205)"
            );
         }

         String var10000 = "The team will reply in 1314 minutes";
         AvatarWrapper[] var3 = new AvatarWrapper[3];
         Avatar var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[0] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "LD");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[1] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "LY");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[2] = new AvatarWrapper(var10005, false, 2, null);
         List var10001 = CollectionsKt.listOf(var3);
         var1.startReplaceGroup(1967900889);
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
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$FooterNoticeKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var14 = var12;
            var4.updateRememberedValue(var14);
            var10002 = var14;
         } else {
            var10002 = var7;
         }

         Function0 var13 = (Function0)var10002;
         var1.endReplaceGroup();
         FooterNoticeKt.access$FooterNoticePillWithoutAnimation(var10000, var10001, var13, var1, 390);
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
