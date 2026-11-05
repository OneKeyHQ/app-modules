package io.intercom.android.sdk.carousel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface PermissionManager$PermissionStatus {
   int GRANTED = 0;
   int DENIED_TEMPORARILY = 1;
   int NEVER_ASKED_OR_DENIED_PERMANENTLY = 2;
}
