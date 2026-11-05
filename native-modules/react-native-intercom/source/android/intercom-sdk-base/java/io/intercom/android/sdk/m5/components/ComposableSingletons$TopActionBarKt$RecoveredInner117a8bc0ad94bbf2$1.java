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
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TopActionBarKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C213@7801L192:TopActionBar.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               247317339, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-5.<anonymous> (TopActionBar.kt:213)"
            );
         }

         Avatar var10002 = Avatar.create("", "HC");
         Intrinsics.checkNotNullExpressionValue(var10002, "create(...)");
         List var3 = CollectionsKt.listOf(new AvatarWrapper(var10002, false, 2, null));
         TopActionBarKt.recovered$e7704aa1c1b2200b(null, "Hannah", null, null, var3, null, null, true, 0L, 0L, 0L, null, null, var1, 12582960, 0, 8045);
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
