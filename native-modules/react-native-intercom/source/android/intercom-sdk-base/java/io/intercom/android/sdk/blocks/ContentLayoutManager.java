package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.blocks.lib.BlocksViewHolder;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata$Builder;
import io.intercom.android.sdk.models.carousel.Appearance$Builder;
import io.intercom.android.sdk.models.carousel.CarouselScreen;
import java.util.List;

public class ContentLayoutManager {
   private final Context context;
   private final BlocksViewHolder blocksViewHolder;

   public ContentLayoutManager(Context var1, BlocksViewHolder var2) {
      this.context = var1;
      this.blocksViewHolder = var2;
   }

   public void renderCarouselScreen(CarouselScreen var1, FrameLayout var2) {
      LayoutInflater var3 = LayoutInflater.from(this.context);
      LinearLayout var4 = (LinearLayout)var3.inflate(this.blocksViewHolder.getLayout(), var2, false);
      LayoutParams var5 = (LayoutParams)var4.getLayoutParams();
      var5.gravity = var1.getGravity();
      var4.setLayoutParams(var5);
      var2.addView(var4);
      this.renderContent(var1.getBlocks(), var1.getTextColor(), var4);
   }

   @VisibleForTesting
   void renderContent(List var1, String var2, LinearLayout var3) {
      for (java.lang.Object recoveredEntryElement_0_39 : var1) {
         Block var5 = (Block) recoveredEntryElement_0_39;
         this.renderBlock(var5, var2, var3);
      }
   }

   @VisibleForTesting
   void renderBlock(Block var1, String var2, LinearLayout var3) {
      Appearance$Builder var4 = new Appearance$Builder().withTextColor(var2);
      BlockMetadata var5 = new BlockMetadata$Builder().withAppearance(var4).build();
      View var6 = var1.getType().getView(this.blocksViewHolder, var1, var3, var5);
      var3.addView(var6);
   }
}
