package io.intercom.android.sdk.m5.upload.data;

import android.os.Parcelable;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bp\u0018\u00002\u00020\u0001:\u0002\u0004\u0005J\b\u0010\u0002\u001a\u00020\u0003H&\u0082\u0001\u0002\u0006\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData;",
         "Landroid/os/Parcelable;",
         "getDataSource",
         "",
         "Media",
         "Gif",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "intercom-sdk-base_release"
   }
)
public interface MediaData extends Parcelable {
   @NotNull
   Object getDataSource();
}
