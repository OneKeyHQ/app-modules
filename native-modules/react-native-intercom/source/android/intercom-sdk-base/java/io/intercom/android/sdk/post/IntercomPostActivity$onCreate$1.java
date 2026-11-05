package io.intercom.android.sdk.post;

import androidx.activity.OnBackPressedCallback;
import kotlin.Metadata;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004",
   d2 = {
         "io/intercom/android/sdk/post/IntercomPostActivity$onCreate$1",
         "Landroidx/activity/OnBackPressedCallback;",
         "handleOnBackPressed",
         "",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPostActivity$onCreate$1 extends OnBackPressedCallback {
   // $VF: synthetic field
   final IntercomPostActivity this$0;

   IntercomPostActivity$onCreate$1(IntercomPostActivity var1) {
      super(true);
      this.this$0 = var1;
   }

   @Override
   public void handleOnBackPressed() {
      if (IntercomPostActivity.access$isPreview(this.this$0)) {
         IntercomPostActivity.access$getInjector(this.this$0)
            .getMetricTracker()
            .closedInAppFromFull(IntercomPostActivity.access$getConversationId(this.this$0), IntercomPostActivity.access$getPart(this.this$0).getId());
      }

      this.this$0.finish();
   }
}
