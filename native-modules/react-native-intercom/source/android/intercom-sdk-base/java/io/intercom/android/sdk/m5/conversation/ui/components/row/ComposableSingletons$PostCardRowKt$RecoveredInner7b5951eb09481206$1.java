package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$PostCardRowKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$PostCardRowKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PostCardRowKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C182@7254L695:PostCardRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1591302201,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$PostCardRowKt.lambda-1.<anonymous> (PostCardRow.kt:182)"
            );
         }

         Part$Builder var10001 = new Part$Builder().withParticipantIsAdmin(false);
         Block$Builder[] var3 = new Block$Builder[]{
            BubbleMessageRowKt.getParagraphBlock(), BubbleMessageRowKt.getLongParagraphBlock(), BubbleMessageRowKt.getLongParagraphBlock()
         };
         Part var7 = var10001.withBlocks(CollectionsKt.listOf(var3)).withStyle(io.intercom.android.sdk.models.MessageStyle.POST).build();
         Part var4 = var7;
         Object var6 = null;
         int var5 = 0;
         var4.setParticipant(Participant.create("", "Paul", "user", "", Avatar.create("", "PK"), false));
         Intrinsics.checkNotNullExpressionValue(var7, "apply(...)");
         PostCardRowKt.PostCardRow((Modifier)var6, var7, "SDK Test App", var1, 384, 1);
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
