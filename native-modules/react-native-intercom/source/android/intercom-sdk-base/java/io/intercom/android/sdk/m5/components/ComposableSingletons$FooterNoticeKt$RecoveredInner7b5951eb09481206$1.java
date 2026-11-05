package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$FooterNoticeKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$FooterNoticeKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$FooterNoticeKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C147@4327L288:FooterNotice.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -316510596, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$FooterNoticeKt.lambda-1.<anonymous> (FooterNotice.kt:147)"
            );
         }

         Avatar var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         FooterNoticeKt.ExpandedFooterNotice(
            null,
            "The team will reply in 1314 minutes",
            "You’ll get replies here and in your email: handover_notice990",
            CollectionsKt.listOf(new AvatarWrapper(var10005, false, 2, null)),
            var1,
            432,
            1
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
