package io.intercom.android.sdk.activities;

import androidx.activity.ComponentActivity;
import io.intercom.android.sdk.views.IntercomToolbar$Listener;

class InAppToolbarListener implements IntercomToolbar$Listener {
   private final ComponentActivity activity;

   InAppToolbarListener(ComponentActivity var1) {
      this.activity = var1;
   }

   @Override
   public void onCloseClicked() {
      this.activity.getOnBackPressedDispatcher().onBackPressed();
   }

   @Override
   public void onToolbarClicked() {
   }

   @Override
   public void onInboxClicked() {
   }
}
