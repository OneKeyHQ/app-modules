package io.intercom.android.sdk.m5.conversation.ui.components.row;

import io.intercom.android.sdk.blocks.lib.BlockType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class BubbleMessageRowKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[BlockType.values().length];

      try {
         var0[BlockType.SUBHEADING.ordinal()] = 1;
      } catch (NoSuchFieldError var8) {
      }

      try {
         var0[BlockType.HEADING.ordinal()] = 2;
      } catch (NoSuchFieldError var7) {
      }

      try {
         var0[BlockType.CODE.ordinal()] = 3;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[BlockType.PARAGRAPH.ordinal()] = 4;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[BlockType.LINK.ordinal()] = 5;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[BlockType.UNORDEREDLIST.ordinal()] = 6;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[BlockType.ORDEREDLIST.ordinal()] = 7;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
