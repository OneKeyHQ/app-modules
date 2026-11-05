package io.intercom.android.nexus;

import androidx.annotation.NonNull;

public interface NexusListener {
   void notifyEvent(@NonNull NexusEvent var1);

   void onConnect();

   void onConnectFailed();

   void onShutdown();
}
