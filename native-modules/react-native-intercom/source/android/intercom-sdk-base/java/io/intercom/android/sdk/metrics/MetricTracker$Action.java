package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$Action {
   String OPENED = "opened";
   String CLOSED = "closed";
   String TYPED = "typed";
   String SENT = "sent";
   String RECEIVED = "received";
   String VIEWED = "viewed";
   String DISMISSED = "dismissed";
   String SEARCHED = "searched";
   String CLICKED = "clicked";
   String STARTED = "started";
   String LOADED = "loaded";
   String FAILED = "failed";
   String COMPLETED = "completed";
   String REQUESTED = "requested";
   String SUBMITTED = "submitted";
}
