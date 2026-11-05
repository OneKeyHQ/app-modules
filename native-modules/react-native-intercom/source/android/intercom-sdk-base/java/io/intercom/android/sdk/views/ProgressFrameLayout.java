package io.intercom.android.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.conversation.UploadProgressListener;
import io.intercom.android.sdk.logger.LumberMill;

public class ProgressFrameLayout extends FrameLayout implements UploadProgressListener {
   final UploadProgressBar uploadProgressBar;
   private final Twig twig = LumberMill.getLogger();

   public ProgressFrameLayout(Context var1) {
      this(var1, null);
   }

   public ProgressFrameLayout(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.uploadProgressBar = new UploadProgressBar(var1, var2);
      this.addView(this.uploadProgressBar);
   }

   @Override
   public void uploadStarted() {
      this.uploadProgressBar.smoothStartAnimation();
   }

   @Override
   public void uploadNotice(byte var1) {
      this.twig.internal("progress", "" + var1);
      this.post(new ProgressFrameLayout$1(this, var1));
   }

   @Override
   public void uploadSmoothEnd() {
      this.post(new ProgressFrameLayout$2(this));
   }

   @Override
   public void uploadStopped() {
      this.uploadProgressBar.hideBar();
   }
}
