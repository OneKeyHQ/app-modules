package io.intercom.android.sdk.blocks;

import android.view.View;

public interface ButtonClickListener {
   void onButtonClicked(View var1, String var2);

   boolean shouldHandleClicks();
}
