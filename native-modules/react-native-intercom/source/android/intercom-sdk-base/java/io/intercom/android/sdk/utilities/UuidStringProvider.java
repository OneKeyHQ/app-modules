package io.intercom.android.sdk.utilities;

public interface UuidStringProvider {
   UuidStringProvider SYSTEM = new UuidStringProvider$1();

   String newUuidString();
}
