package io.intercom.android.sdk.activities;

import androidx.activity.OnBackPressedCallback;

class IntercomNoteActivity$1 extends OnBackPressedCallback {
   // $VF: synthetic field
   final IntercomNoteActivity this$0;

   IntercomNoteActivity$1(IntercomNoteActivity var1, boolean var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Override
   public void handleOnBackPressed() {
      IntercomNoteActivity.access$000(this.this$0).closeWindow(this.this$0);
      this.this$0.metricTracker.closedInAppFromFull(this.this$0.conversationId, this.this$0.part.getId());
   }
}
