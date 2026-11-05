package io.intercom.android.sdk.persistence;

import java.io.File;

public interface JsonStorage$LoadFailureHandler {
   JsonStorage$LoadFailureHandler NONE = new JsonStorage$LoadFailureHandler$1();

   void onLoadFailed(File var1, Exception var2);
}
