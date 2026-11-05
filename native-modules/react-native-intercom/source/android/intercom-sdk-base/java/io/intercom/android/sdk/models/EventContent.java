package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u001d\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\b\u0010\u000f\u001a\u00020\u0010H\u0007J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0010H×\u0001J\t\u0010\u0016\u001a\u00020\u0003H×\u0001J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0010H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/models/EventContent;",
         "Landroid/os/Parcelable;",
         "text",
         "",
         "weight",
         "Lio/intercom/android/sdk/models/Weight;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/models/Weight;)V",
         "getText",
         "()Ljava/lang/String;",
         "getWeight",
         "()Lio/intercom/android/sdk/models/Weight;",
         "component1",
         "component2",
         "copy",
         "describeContents",
         "",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class EventContent implements Parcelable {
   @NotNull
   public static final EventContent$Companion Companion = new EventContent$Companion(null);
   @SerializedName("text")
   @NotNull
   private final String text;
   @SerializedName("weight")
   @Nullable
   private final Weight weight;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new EventContent$Creator();
   @JvmField
   @NotNull
   public static final EventContent EMPTY = new EventContent(null, null, 3, null);

   public EventContent(@NotNull String var1, @Nullable Weight var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      this.text = var1;
      this.weight = var2;
   }

   // $VF: synthetic method
   public EventContent(String var1, Weight var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) ("") : var1), (((var3 & 2) != 0) ? (Weight) (null) : var2));
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final Weight getWeight() {
      return this.weight;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.text);
      Weight var3 = this.weight;
      if (var3 == null) {
         var1.writeInt(0);
      } else {
         var1.writeInt(1);
         var1.writeString(var3.name());
      }
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   @Nullable
   public final Weight component2() {
      return this.weight;
   }

   @NotNull
   public final EventContent copy(@NotNull String var1, @Nullable Weight var2) {
      Intrinsics.checkNotNullParameter(var1, "text");
      return new EventContent(var1, var2);
   }

   // $VF: synthetic method
   public static EventContent copy$default(EventContent var0, String var1, Weight var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.weight;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "EventContent(text=" + this.text + ", weight=" + this.weight + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      return var1 * 31 + (this.weight == null ? 0 : this.weight.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof EventContent)) {
         return false;
      }

      EventContent var2 = (EventContent)var1;
      return !Intrinsics.areEqual(this.text, var2.text) ? false : this.weight == var2.weight;
   }

   public EventContent() {
      this(null, null, 3, null);
   }
}
