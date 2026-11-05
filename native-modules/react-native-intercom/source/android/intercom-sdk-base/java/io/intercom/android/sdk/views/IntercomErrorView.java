package io.intercom.android.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.utilities.FontUtils;

public class IntercomErrorView extends RelativeLayout {
   private TextView titleView;
   private TextView subtitle;
   private Button actionButton;

   public IntercomErrorView(Context var1) {
      this(var1, null);
   }

   public IntercomErrorView(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      this.titleView = (TextView)this.findViewById(id.empty_text_title);
      FontUtils.setRobotoMediumTypeface(this.titleView);
      this.subtitle = (TextView)this.findViewById(id.empty_text_subtitle);
      this.actionButton = (Button)this.findViewById(id.action_button);
   }

   public void setTitle(@StringRes int var1) {
      this.titleView.setText(var1);
   }

   public void setSubtitle(CharSequence var1) {
      this.subtitle.setText(var1);
   }

   public void setSubtitle(@StringRes int var1) {
      this.subtitle.setText(var1);
   }

   public void setActionButtonVisibility(int var1) {
      this.actionButton.setVisibility(var1);
   }

   public void setActionButtonText(@StringRes int var1) {
      this.actionButton.setText(var1);
   }

   public void setActionButtonClickListener(@Nullable OnClickListener var1) {
      this.actionButton.setOnClickListener(var1);
   }

   public void setActionButtonTextColor(@ColorInt int var1) {
      this.actionButton.setTextColor(var1);
   }
}
