package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bJ\b\u0010\u000e\u001a\u00020\u000fH\u0007J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000fH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/models/Source;",
         "Landroid/os/Parcelable;",
         "id",
         "",
         "type",
         "title",
         "url",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getId",
         "()Ljava/lang/String;",
         "getType",
         "getTitle",
         "getUrl",
         "describeContents",
         "",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class Source implements Parcelable {
   @NotNull
   private final String id;
   @NotNull
   private final String type;
   @NotNull
   private final String title;
   @NotNull
   private final String url;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new Source$Creator();

   public Source(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "url");
      this.id = var1;
      this.type = var2;
      this.title = var3;
      this.url = var4;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.id);
      var1.writeString(this.type);
      var1.writeString(this.title);
      var1.writeString(this.url);
   }

   @Override
   public final int describeContents() {
      return 0;
   }
}
