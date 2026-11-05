package io.intercom.android.sdk.utilities.commons;

public interface TimeProvider {
   TimeProvider SYSTEM = System::currentTimeMillis;

   long currentTimeMillis();
}
