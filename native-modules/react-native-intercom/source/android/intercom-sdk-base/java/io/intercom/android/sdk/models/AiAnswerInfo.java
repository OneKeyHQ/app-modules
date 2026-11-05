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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\b\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000bH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/models/AiAnswerInfo;",
         "Landroid/os/Parcelable;",
         "text",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getText",
         "()Ljava/lang/String;",
         "isEmpty",
         "",
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
public final class AiAnswerInfo implements Parcelable {
   @NotNull
   private final String text;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new AiAnswerInfo$Creator();

   public AiAnswerInfo(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      this.text = var1;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final boolean isEmpty() {
      return this.text.length() == 0;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.text);
   }

   @Override
   public final int describeContents() {
      return 0;
   }
}
