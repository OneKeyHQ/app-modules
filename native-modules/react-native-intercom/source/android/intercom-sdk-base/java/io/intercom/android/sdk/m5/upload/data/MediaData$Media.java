package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u0000 \u00142\u00020\u0001:\u0004\u0011\u0012\u0013\u0014J\b\u0010\u0010\u001a\u00020\rH\u0016R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0012\u0010\n\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\u0005R\u0012\u0010\f\u001a\u00020\rX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000f\u0082\u0001\u0003\u0015\u0016\u0017¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData;",
         "mimeType",
         "",
         "getMimeType",
         "()Ljava/lang/String;",
         "size",
         "",
         "getSize",
         "()J",
         "fileName",
         "getFileName",
         "uri",
         "Landroid/net/Uri;",
         "getUri",
         "()Landroid/net/Uri;",
         "getDataSource",
         "Image",
         "Video",
         "Other",
         "Companion",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Image;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Other;",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Video;",
         "intercom-sdk-base_release"
   }
)
public interface MediaData$Media extends MediaData {
   @NotNull
   MediaData$Media$Companion Companion = MediaData$Media$Companion.$$INSTANCE;

   @NotNull
   String getMimeType();

   long getSize();

   @NotNull
   String getFileName();

   @NotNull
   Uri getUri();

   @NotNull
   Uri getDataSource();
}
