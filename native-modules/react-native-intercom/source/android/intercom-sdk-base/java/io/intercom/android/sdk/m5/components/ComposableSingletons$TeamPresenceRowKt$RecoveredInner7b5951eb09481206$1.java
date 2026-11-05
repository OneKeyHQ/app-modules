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
final class ComposableSingletons$TeamPresenceRowKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TeamPresenceRowKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TeamPresenceRowKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C45@1602L359:TeamPresenceRow.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -919412179,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$TeamPresenceRowKt.lambda-1.<anonymous> (TeamPresenceRow.kt:45)"
            );
         }

         AvatarWrapper[] var3 = new AvatarWrapper[4];
         Avatar var10005 = Avatar.create("", "VR");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[0] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[1] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "PR");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[2] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "LD");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var3[3] = new AvatarWrapper(var10005, false, 2, null);
         TeamPresenceRowKt.TeamPresenceRow(null, CollectionsKt.listOf(var3), var1, 0, 1);
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
