package io.intercom.android.sdk.m5.home.ui.header;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$HomeHeaderKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$HomeHeaderKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C208@8187L6,184@6985L1640:HomeHeader.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1111020598, var2, -1, "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderKt.lambda-1.<anonymous> (HomeHeader.kt:184)"
            );
         }

         AvatarWrapper[] var4 = new AvatarWrapper[3];
         Avatar var10005 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var4[0] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "RS");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var4[1] = new AvatarWrapper(var10005, false, 2, null);
         var10005 = Avatar.create("", "VR");
         Intrinsics.checkNotNullExpressionValue(var10005, "create(...)");
         var4[2] = new AvatarWrapper(var10005, false, 2, null);
         List var3 = CollectionsKt.listOf(var4);
         HomeUiState$Content$ContentHeader$ColoredText var8 = new HomeUiState$Content$ContentHeader$ColoredText(
            "I am greeting.", 0.5F, null, null, "#FFFFFF", "#000000", 12, null
         );
         HomeUiState$Content$ContentHeader$ColoredText var5 = new HomeUiState$Content$ContentHeader$ColoredText(
            "I am intro.", 1.0F, null, null, "#FFFFFF", "#000000", 12, null
         );
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid var6 = new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), false, null
         );
         HomeUiState$Content$ContentHeader$CloseButtonColor var7 = new HomeUiState$Content$ContentHeader$CloseButtonColor("#000000", "#FFFFFF", "#FFFFFF", 0.5F);
         HomeHeaderKt.HomeContentHeader(
            null, new HomeUiState$Content$ContentHeader(true, "", null, var8, var5, var6, null, true, var3, var7, 64, null), var1, 0, 1
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
