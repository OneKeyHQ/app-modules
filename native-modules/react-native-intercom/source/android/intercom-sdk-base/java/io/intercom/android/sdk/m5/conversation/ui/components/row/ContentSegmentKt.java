package io.intercom.android.sdk.m5.conversation.ui.components.row;

import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0003\u001a\u00020\u0004*\u00020\u0005H\u0000\u001a\u0018\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007*\b\u0012\u0004\u0012\u00020\u00050\u0007H\u0000\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t",
   d2 = {
         "STANDALONE_BLOCK_TYPES",
         "",
         "Lio/intercom/android/sdk/blocks/lib/BlockType;",
         "rendersStandalone",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "segmentStandaloneBlocks",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/ContentSegment;",
         "intercom-sdk-base_release"
   }
)
public final class ContentSegmentKt {
   @NotNull
   private static final Set STANDALONE_BLOCK_TYPES = SetsKt.setOf(BlockType.CODE);

   public static final boolean rendersStandalone(@NotNull Block var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return STANDALONE_BLOCK_TYPES.contains(var0.getType()) || var0.getType() == BlockType.TABLE && ConfigurableIntercomThemeKt.getFinTableBlocksEnabled();
   }

   @NotNull
   public static final List segmentStandaloneBlocks(@NotNull List var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      List var1 = new ArrayList();
      List var2 = new ArrayList();

      for (java.lang.Object recoveredEntryElement_0_48 : var0) {
         Block var4 = (Block) recoveredEntryElement_0_48;
         if (rendersStandalone(var4)) {
            segmentStandaloneBlocks$flushRun(var2, var1);
            var1.add(new ContentSegment$Standalone(var4));
         } else {
            var2.add(var4);
         }
      }

      segmentStandaloneBlocks$flushRun(var2, var1);
      return var1;
   }

   private static final void segmentStandaloneBlocks$flushRun(List var0, List var1) {
      if (!var0.isEmpty()) {
         var1.add(new ContentSegment$TextRun(CollectionsKt.toList(var0)));
         var0.clear();
      }
   }
}
