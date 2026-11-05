package io.intercom.android.sdk.m5.conversation.ui.components;

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
final class ComposableSingletons$MessageMetadataKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$MessageMetadataKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$MessageMetadataKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C99@3400L283:MessageMetadata.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1395125050,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MessageMetadataKt.lambda-1.<anonymous> (MessageMetadata.kt:99)"
            );
         }

         Avatar var10002 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10002, "create(...)");
         MessageMetadataKt.MessageMetadata(CollectionsKt.listOf(new AvatarWrapper(var10002, false, 2, null)), "Lisa", null, null, 1726738186L, var1, 27696, 4);
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
