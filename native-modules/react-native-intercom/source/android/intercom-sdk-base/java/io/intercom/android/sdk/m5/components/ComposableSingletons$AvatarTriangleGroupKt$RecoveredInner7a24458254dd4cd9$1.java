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
final class ComposableSingletons$AvatarTriangleGroupKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$AvatarTriangleGroupKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$AvatarTriangleGroupKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C104@3776L205:AvatarTriangleGroup.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -549018737,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$AvatarTriangleGroupKt.lambda-2.<anonymous> (AvatarTriangleGroup.kt:104)"
            );
         }

         AvatarWrapper[] var3 = new AvatarWrapper[2];
         Avatar var10004 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         var3[0] = new AvatarWrapper(var10004, false, 2, null);
         var10004 = Avatar.create("", "RS");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         var3[1] = new AvatarWrapper(var10004, false, 2, null);
         AvatarTriangleGroupKt.recovered$617ccf65fce42f20(CollectionsKt.listOf(var3), null, null, 0.0F, var1, 0, 14);
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
