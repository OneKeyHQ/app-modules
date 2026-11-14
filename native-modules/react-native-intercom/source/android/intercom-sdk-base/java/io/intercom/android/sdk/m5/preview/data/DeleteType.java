package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcelable;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.parcelize.Parcelize;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0003\u0004\u0005\u0006B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0003\u0007\b\t¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType;",
         "Landroid/os/Parcelable;",
         "<init>",
         "()V",
         "None",
         "Delete",
         "Remove",
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType$Delete;",
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType$None;",
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType$Remove;",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public abstract class DeleteType implements Parcelable {
   public static int $stable;

   static {}

   private DeleteType() {
   }

   // $VF: synthetic method
   public DeleteType(DefaultConstructorMarker var1) {
      this();
   }
}
