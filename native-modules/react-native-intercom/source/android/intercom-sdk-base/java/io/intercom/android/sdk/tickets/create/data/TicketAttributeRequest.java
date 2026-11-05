package io.intercom.android.sdk.tickets.create.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0001HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0001HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0011\u001a\u00020\u0012H×\u0001J\t\u0010\u0013\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\u0004\u001a\u00020\u00018\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/data/TicketAttributeRequest;",
         "",
         "descriptorId",
         "",
         "value",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/Object;)V",
         "getDescriptorId",
         "()Ljava/lang/String;",
         "getValue",
         "()Ljava/lang/Object;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketAttributeRequest {
   @SerializedName("descriptor_id")
   @NotNull
   private final String descriptorId;
   @SerializedName("value")
   @NotNull
   private final Object value;
   public static final int $stable;

   static { $stable = 8; }

   public TicketAttributeRequest(@NotNull String var1, @NotNull Object var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "descriptorId");
      Intrinsics.checkNotNullParameter(var2, "value");
      this.descriptorId = var1;
      this.value = var2;
   }

   @NotNull
   public final String getDescriptorId() {
      return this.descriptorId;
   }

   @NotNull
   public final Object getValue() {
      return this.value;
   }

   @NotNull
   public final String component1() {
      return this.descriptorId;
   }

   @NotNull
   public final Object component2() {
      return this.value;
   }

   @NotNull
   public final TicketAttributeRequest copy(@NotNull String var1, @NotNull Object var2) {
      Intrinsics.checkNotNullParameter(var1, "descriptorId");
      Intrinsics.checkNotNullParameter(var2, "value");
      return new TicketAttributeRequest(var1, var2);
   }

   // $VF: synthetic method
   public static TicketAttributeRequest copy$default(TicketAttributeRequest var0, String var1, Object var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.descriptorId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.value;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketAttributeRequest(descriptorId=" + this.descriptorId + ", value=" + this.value + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.descriptorId.hashCode();
      return var1 * 31 + this.value.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketAttributeRequest)) {
         return false;
      }

      TicketAttributeRequest var2 = (TicketAttributeRequest)var1;
      return !Intrinsics.areEqual(this.descriptorId, var2.descriptorId) ? false : Intrinsics.areEqual(this.value, var2.value);
   }
}
