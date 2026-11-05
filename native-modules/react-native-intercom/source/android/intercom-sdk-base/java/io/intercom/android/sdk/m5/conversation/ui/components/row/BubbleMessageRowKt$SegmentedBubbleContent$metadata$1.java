package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadataKt;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBubbleMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$SegmentedBubbleContent$metadata$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1611:1\n1563#2:1612\n1634#2,3:1613\n*S KotlinDebug\n*F\n+ 1 BubbleMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BubbleMessageRowKt$SegmentedBubbleContent$metadata$1\n*L\n294#1:1612\n294#1:1613,3\n*E\n"
)
final class BubbleMessageRowKt$SegmentedBubbleContent$metadata$1 implements Function2 {
   // $VF: synthetic field
   final Part $conversationPart;

   BubbleMessageRowKt$SegmentedBubbleContent$metadata$1(Part var1) {
      this.$conversationPart = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C*292@14690L230:BubbleMessageRow.kt#hr3bi5");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               75783638, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedBubbleContent.<anonymous> (BubbleMessageRow.kt:291)"
            );
         }

         io.intercom.android.sdk.models.Metadata var10000 = this.$conversationPart.getMetadata();
         if (var10000 != null) {
            io.intercom.android.sdk.models.Metadata var3 = var10000;
            int var4 = 0;
            Iterable var5 = var3.getAvatars();
            int var6 = 0;
            Iterable var7 = var5;
            ArrayList var8 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var5, 10));
            int var9 = 0;

            for (Object var11 : var7) {
               Avatar var12 = (Avatar)var11;
               Collection var13 = var8;
               int var14 = 0;
               var13.add(new AvatarWrapper(var12, false, 2, null));
            }

            MessageMetadataKt.MessageMetadata((List)var8, var3.getTitle(), null, var3.getSuffix(), var3.getTimestamp(), var1, 0, 4);
         }

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
