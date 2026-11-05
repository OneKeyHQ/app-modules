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
final class ComposableSingletons$FooterNoticeKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$FooterNoticeKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$FooterNoticeKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C161@4728L418:FooterNotice.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1192394372, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$FooterNoticeKt.lambda-2.<anonymous> (FooterNotice.kt:161)"
            );
         }

         AvatarWrapper[] var3 = new AvatarWrapper[3];
         Avatar var10007 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         var3[0] = new AvatarWrapper(var10007, false, 2, null);
         var10007 = Avatar.create("", "LD");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         var3[1] = new AvatarWrapper(var10007, false, 2, null);
         var10007 = Avatar.create("", "LY");
         Intrinsics.checkNotNullExpressionValue(var10007, "create(...)");
         var3[2] = new AvatarWrapper(var10007, false, 2, null);
         FooterNoticeKt.ExpandedFooterNotice(
            null,
            "The team will reply in 1314 minutes",
            "You’ll get replies here and in your email: handover_notice990",
            CollectionsKt.listOf(var3),
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
