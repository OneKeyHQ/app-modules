package io.intercom.android.sdk.lightcompressor;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H'J\"\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH'J\u0018\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\nH'J\u0018\u0010\r\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH'J\u0010\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H'¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/CompressionListener;",
         "",
         "onStart",
         "",
         "index",
         "",
         "onSuccess",
         "size",
         "",
         "path",
         "",
         "onFailure",
         "failureMessage",
         "onProgress",
         "percent",
         "",
         "onCancelled",
         "intercom-sdk-lightcompressor_release"
   }
)
public interface CompressionListener {
   @MainThread
   void onStart(int var1);

   @MainThread
   void onSuccess(int var1, long var2, @Nullable String var4);

   @MainThread
   void onFailure(int var1, @NotNull String var2);

   @WorkerThread
   void onProgress(int var1, float var2);

   @WorkerThread
   void onCancelled(int var1);
}
