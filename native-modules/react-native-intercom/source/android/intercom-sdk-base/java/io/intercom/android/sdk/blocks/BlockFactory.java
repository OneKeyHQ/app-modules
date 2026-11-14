package io.intercom.android.sdk.blocks;

import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.logic.TextSplittingStrategy;
import java.util.ArrayList;
import java.util.List;

public class BlockFactory {
   private final TextSplittingStrategy textSplittingStrategy;

   public BlockFactory(TextSplittingStrategy var1) {
      this.textSplittingStrategy = var1;
   }

   public List getBlocksForText(String var1) {
      List var2 = this.textSplittingStrategy.apply(var1);
      int var3 = var2.size();
      List var4 = new ArrayList(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         var4.add(new Block$Builder().withType(BlockType.PARAGRAPH.name().toLowerCase()).withText((String)var2.get(var5)));
      }

      return var4;
   }
}
