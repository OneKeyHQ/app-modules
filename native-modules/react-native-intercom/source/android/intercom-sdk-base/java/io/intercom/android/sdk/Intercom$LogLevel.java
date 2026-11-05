package io.intercom.android.sdk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0002\b\u0002\b\u0086\u0002\u0018\u0000 \u00022\u00020\u0001:\u0001\u0002B\u0000¨\u0006\u0003",
   d2 = {"Lio/intercom/android/sdk/Intercom$LogLevel;", "", "Companion", "intercom-sdk-base_release"}
)
public @interface Intercom$LogLevel {
   @NotNull
   Intercom$LogLevel$Companion Companion = Intercom$LogLevel$Companion.$$INSTANCE;
   int VERBOSE = 2;
   int DEBUG = 3;
   int INFO = 4;
   int WARN = 5;
   int ERROR = 6;
   int ASSERT = 7;
   int DISABLED = 8;
}
