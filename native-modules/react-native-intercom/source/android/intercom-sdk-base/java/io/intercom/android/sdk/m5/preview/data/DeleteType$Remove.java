package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u0007¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType$Remove;",
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType;",
         "<init>",
         "()V",
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
@StabilityInferred(parameters = 1)
public final class DeleteType$Remove extends DeleteType {
   @NotNull
   public static final DeleteType$Remove INSTANCE = new DeleteType$Remove();
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new DeleteType$Remove$Creator();

   private DeleteType$Remove() {
      super(null);
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(1);
   }

   @Override
   public final int describeContents() {
      return 0;
   }
}
