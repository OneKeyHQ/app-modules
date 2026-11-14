package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Header$Expanded$Body;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$TeammateSheetContentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$TeammateSheetContentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$TeammateSheetContentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C44@1624L1264:TeammateSheetContent.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1445246988,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$TeammateSheetContentKt.lambda-1.<anonymous> (TeammateSheetContent.kt:44)"
            );
         }

         Header$Expanded$Style var3 = Header$Expanded$Style.H1;
         List var10005 = CollectionsKt.listOf(new Header$Expanded$Body(var3, "Body 1", null));
         AvatarType var10006 = AvatarType.FACEPILE;
         AvatarWrapper[] var4 = new AvatarWrapper[3];
         Avatar var10011 = new Avatar$Builder().withInitials("A").build();
         Intrinsics.checkNotNullExpressionValue(var10011, "build(...)");
         var4[0] = new AvatarWrapper(var10011, false);
         var10011 = new Avatar$Builder().withInitials("B").build();
         Intrinsics.checkNotNullExpressionValue(var10011, "build(...)");
         var4[1] = new AvatarWrapper(var10011, false);
         var10011 = new Avatar$Builder().withInitials("C").build();
         Intrinsics.checkNotNullExpressionValue(var10011, "build(...)");
         var4[2] = new AvatarWrapper(var10011, false);
         TeammateSheetContentKt.TeammateSheetContent(
            null,
            null,
            new ExpandedTeamPresenceState("Title", var10005, var10006, CollectionsKt.listOf(var4), CollectionsKt.emptyList(), CollectionsKt.emptyList(), false),
            var1,
            0,
            3
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
