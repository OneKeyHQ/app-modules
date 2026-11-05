package io.intercom.android.sdk.survey.block;

import io.intercom.android.sdk.blocks.lib.BlockType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class BlockRenderData$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[BlockType.values().length];

      try {
         var0[BlockType.HEADING.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[BlockType.SUBHEADING.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[BlockType.PARAGRAPH.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
