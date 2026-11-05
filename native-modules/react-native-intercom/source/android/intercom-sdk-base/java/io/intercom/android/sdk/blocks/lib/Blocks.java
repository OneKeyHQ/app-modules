package io.intercom.android.sdk.blocks.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata$Builder;
import io.intercom.android.sdk.models.carousel.Appearance;
import java.util.List;

public class Blocks {
   private final LayoutInflater inflater;
   private final Twig twig;
   private final Appearance appearance;

   public Blocks(Context var1, Twig var2) {
      this.inflater = LayoutInflater.from(var1);
      this.twig = var2;
      this.appearance = Appearance.NULL;
   }

   public Blocks(Context var1, Twig var2, Appearance var3) {
      this.inflater = LayoutInflater.from(var1);
      this.twig = var2;
      this.appearance = var3;
   }

   public LinearLayout createBlocks(List var1, BlocksViewHolder var2) {
      LinearLayout var3 = (LinearLayout)this.inflater.inflate(var2.getLayout(), null);
      if (var1 != null) {
         for (int var4 = 0; var4 < var1.size(); var4++) {
            Block var5 = (Block)var1.get(var4);
            boolean var6 = var4 == 0;
            boolean var7 = var4 == var1.size() - 1;
            BlockMetadata var8 = new BlockMetadata$Builder().withAppearance(this.appearance.toBuilder()).isFirstObject(var6).isLastObject(var7).build();

            try {
               View var9 = var5.getType().getView(var2, var5, var3, var8);
               var3.addView(var9);
            } catch (BlockTypeNotImplementedException var10) {
               this.twig.e(var10, "Error creating view for block with type %s ", var5.getType());
            }
         }
      }

      return var3;
   }
}
