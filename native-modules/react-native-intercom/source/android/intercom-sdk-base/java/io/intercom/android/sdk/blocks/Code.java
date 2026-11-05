package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.blocks.lib.interfaces.CodeBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

class Code implements CodeBlock {
   @Override
   public View addCode(Spanned var1, BlockMetadata var2, ViewGroup var3) {
      TextView var4 = this.getCodeBlockView(var1, var3.getContext());
      BlockUtils.setLayoutMarginsAndGravity(var4, 8388611, var2.isLastObject());
      return var4;
   }

   private TextView getCodeBlockView(Spanned var1, Context var2) {
      TextView var3 = new TextView(var2);
      var3.setTextColor(ContextCompat.getColor(var2, color.intercom_white));
      var3.setTextSize(14.0F);
      var3.setText(var1);
      var3.setPadding(ScreenUtils.dpToPx(14.0F, var2), ScreenUtils.dpToPx(12.0F, var2), ScreenUtils.dpToPx(14.0F, var2), ScreenUtils.dpToPx(14.0F, var2));
      var3.setBackgroundResource(color.intercom_slate_grey_two);
      var3.setTypeface(Typeface.MONOSPACE);
      BlockUtils.createLayoutParams(var3, -2, -2);
      BlockUtils.setDefaultMarginBottom(var3);
      return var3;
   }
}
