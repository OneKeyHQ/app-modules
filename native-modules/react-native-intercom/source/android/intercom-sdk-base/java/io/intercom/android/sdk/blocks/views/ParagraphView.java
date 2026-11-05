package io.intercom.android.sdk.blocks.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.blocks.StyleType;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.TrackingLinkMovementMethod;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

@SuppressLint("ViewConstructor")
public class ParagraphView extends AppCompatTextView {
   private static final int POST_BOTTOM_MARGIN_DP = 24;
   private static final int POST_LINE_SPACING_DP = 4;
   private static final int CAROUSEL_LINE_SPACING_DP = 8;
   private static final int DEFAULT_LINE_SPACING_DP = 2;
   private final Context context;
   private final StyleType style;
   private final BlockAlignment alignment;
   private final AppConfig appConfig;
   private final BlockMetadata blockMetadata;

   public ParagraphView(Context var1, StyleType var2, BlockAlignment var3, AppConfig var4, BlockMetadata var5) {
      super(var1);
      this.context = var1;
      this.style = var2;
      this.alignment = var3;
      this.appConfig = var4;
      this.blockMetadata = var5;
      this.applyParagraphStyle();
   }

   public ParagraphView(Context var1, StyleType var2, BlockAlignment var3, AppConfig var4) {
      this(var1, var2, var3, var4, BlockMetadata.NULL);
   }

   private void applyParagraphStyle() {
      BlockUtils.createLayoutParams(this, -2, -2);
      BlockUtils.setDefaultMarginBottom(this);
      this.setLineSpacing(ScreenUtils.dpToPx(2.0F, this.context), 1.0F);
      this.setTextSize(16.0F);
      this.setTextIsSelectable(false);
      this.setEllipsize(TruncateAt.END);
      this.setScrollContainer(false);
      this.setFocusable(false);
      this.setClickable(false);
      this.setLongClickable(false);
      this.setGravity(this.alignment.getGravity());
      this.setMovementMethod(new TrackingLinkMovementMethod());
      MarginLayoutParams var1 = (MarginLayoutParams)this.getLayoutParams();
      int var2 = this.appConfig.getPrimaryColor();
      switch (this.style) {
         case ADMIN:
            this.setTextColor(ContextCompat.getColor(this.context, color.intercom_grey_800));
            this.setLinkTextColor(ColorUtils.primaryOrBlackAccessibility(this.context, this.appConfig));
            break;
         case ARTICLE:
            this.setTextColor(ContextCompat.getColor(this.context, color.intercom_grey_700));
            this.setLinkTextColor(ColorUtils.primaryOrDarkColor(this.context, this.appConfig));
            break;
         case NOTE:
         case CONTAINER_CARD:
            this.setTextColor(ContextCompat.getColor(this.context, color.intercom_grey_700));
            this.setLinkTextColor(ColorUtils.primaryOrBlackAccessibility(this.context, this.appConfig));
            break;
         case POST:
            var1.bottomMargin = ScreenUtils.dpToPx(24.0F, this.context);
            this.setLineSpacing(ScreenUtils.dpToPx(4.0F, this.context), 1.0F);
            this.setTextColor(ContextCompat.getColor(this.context, color.intercom_white));
            this.setLinkTextColor(ColorUtils.lightenColor(var2));
            break;
         case CHAT_FULL:
            this.setMovementMethod(null);
            this.setTextSize(14.0F);
            this.setTextColor(ContextCompat.getColor(this.context, color.intercom_grey_600));
            this.setLinkTextColor(ColorUtils.primaryOrDarkColor(this.context, this.appConfig));
            break;
         case CAROUSEL:
            this.setTextSize(20.0F);
            this.setLineSpacing(ScreenUtils.dpToPx(8.0F, this.context), 1.0F);
            String var3 = this.blockMetadata.getAppearance().getTextColor();
            if (!var3.isEmpty()) {
               int var4 = Color.parseColor(var3);
               this.setTextColor(var4);
               this.setLinkTextColor(var4);
            }
            break;
         case USER:
         default:
            this.setIncludeFontPadding(false);
            ColorUtils.setTextColorWhiteOrDark(this, this.appConfig.isPrimaryColorRenderDarkText());
      }
   }
}
