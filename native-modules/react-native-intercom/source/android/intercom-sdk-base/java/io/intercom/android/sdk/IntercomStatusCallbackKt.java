package io.intercom.android.sdk;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u0004",
   d2 = {
         "noStatusCallback",
         "Lio/intercom/android/sdk/IntercomStatusCallback;",
         "getNoStatusCallback",
         "()Lio/intercom/android/sdk/IntercomStatusCallback;",
         "intercom-sdk-base_release"
   }
)
public final class IntercomStatusCallbackKt {
   @NotNull
   private static final IntercomStatusCallback noStatusCallback = new IntercomStatusCallbackKt$noStatusCallback$1();

   @NotNull
   public static final IntercomStatusCallback getNoStatusCallback() {
      return noStatusCallback;
   }
}
