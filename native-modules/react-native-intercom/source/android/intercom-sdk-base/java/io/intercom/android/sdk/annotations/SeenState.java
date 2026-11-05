package io.intercom.android.sdk.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface SeenState {
   String SEEN = "seen";
   String UNSEEN = "unseen";
   String HIDE = "hide";
}
