package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000b\u0010\b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0015\u0010\t\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/models/StreamingPart;",
         "",
         "metadata",
         "Lio/intercom/android/sdk/models/Metadata;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Metadata;)V",
         "getMetadata",
         "()Lio/intercom/android/sdk/models/Metadata;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class StreamingPart {
   @SerializedName("metadata")
   @Nullable
   private final Metadata metadata;
   public static final int $stable;

   static { $stable = 8; }

   public StreamingPart(@Nullable Metadata var1) {
      this.metadata = var1;
   }

   // $VF: synthetic method
   public StreamingPart(Metadata var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Metadata) (null) : var1));
   }

   @Nullable
   public final Metadata getMetadata() {
      return this.metadata;
   }

   @Nullable
   public final Metadata component1() {
      return this.metadata;
   }

   @NotNull
   public final StreamingPart copy(@Nullable Metadata var1) {
      return new StreamingPart(var1);
   }

   // $VF: synthetic method
   public static StreamingPart copy$default(StreamingPart var0, Metadata var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.metadata;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "StreamingPart(metadata=" + this.metadata + ')';
   }

   @Override
   public int hashCode() {
      return this.metadata == null ? 0 : this.metadata.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof StreamingPart)) {
         return false;
      }

      StreamingPart var2 = (StreamingPart)var1;
      return Intrinsics.areEqual(this.metadata, var2.metadata);
   }

   public StreamingPart() {
      this(null, 1, null);
   }
}
