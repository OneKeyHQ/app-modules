package io.intercom.android.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.conversation.UploadProgressListener;
import io.intercom.android.sdk.logger.LumberMill;

public class ProgressLinearLayout extends LinearLayout implements UploadProgressListener {
   UploadProgressBar uploadProgressBar;
   ImageView attachmentIcon;
   private final Twig twig = LumberMill.getLogger();

   public ProgressLinearLayout(Context var1) {
      this(var1, null);
   }

   public ProgressLinearLayout(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   public void setUploadProgressBar(UploadProgressBar var1) {
      this.uploadProgressBar = var1;
   }

   public void setAttachmentIcon(ImageView var1) {
      this.attachmentIcon = var1;
   }

   @Override
   public void uploadStarted() {
      this.uploadProgressBar.smoothStartAnimation();
   }

   @Override
   public void uploadNotice(byte var1) {
      this.twig.internal("progress", "" + var1);
      this.post(new ProgressLinearLayout$1(this, var1));
   }

   @Override
   public void uploadSmoothEnd() {
      this.post(new ProgressLinearLayout$2(this));
   }

   @Override
   public void uploadStopped() {
      this.uploadProgressBar.hideBar();
   }
}
