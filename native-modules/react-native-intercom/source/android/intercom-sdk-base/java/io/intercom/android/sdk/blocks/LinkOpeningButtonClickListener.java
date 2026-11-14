package io.intercom.android.sdk.blocks;

import android.view.View;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.utilities.LinkOpener;

public class LinkOpeningButtonClickListener implements ButtonClickListener {
   private final Api api;

   public LinkOpeningButtonClickListener(Api var1) {
      this.api = var1;
   }

   @Override
   public void onButtonClicked(View var1, String var2) {
      LinkOpener.handleUrl(var2, var1.getContext(), this.api);
   }

   @Override
   public boolean shouldHandleClicks() {
      return true;
   }
}
