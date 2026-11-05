package io.intercom.android.sdk.blocks;

import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.ParagraphBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.ParagraphView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BlockUtils;

class Paragraph implements ParagraphBlock {
   private final StyleType style;
   private final Provider appConfigProvider;

   Paragraph(StyleType var1, Provider var2) {
      this.style = var1;
      this.appConfigProvider = var2;
   }

   @Override
   public View addParagraph(Spanned var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4) {
      TextView var5 = new ParagraphView(var4.getContext(), this.style, var2, (AppConfig)this.appConfigProvider.get(), var3);
      if (StyleType.PREVIEW == this.style) {
         var5.setText(var1.toString());
      } else {
         var5.setText(var1);
      }

      BlockUtils.setLayoutMarginsAndGravity(var5, var2.getGravity(), var3.isLastObject());
      AccessibilityUtils.INSTANCE.removeClickAbilityAnnouncement(var5);
      return var5;
   }
}
