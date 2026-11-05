package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/FinStreamingData;",
         "",
         "isFinStreaming",
         "",
         "clientAssignedUUID",
         "",
         "<init>",
         "(ZLjava/lang/String;)V",
         "()Z",
         "getClientAssignedUUID",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class FinStreamingData {
   private final boolean isFinStreaming;
   @NotNull
   private final String clientAssignedUUID;
   public static int $stable;

   static {}

   public FinStreamingData(boolean var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "clientAssignedUUID");
      this.isFinStreaming = var1;
      this.clientAssignedUUID = var2;
   }

   // $VF: synthetic method
   public FinStreamingData(boolean var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (boolean) (false) : var1), (((var3 & 2) != 0) ? (String) ("") : var2));
   }

   public final boolean isFinStreaming() {
      return this.isFinStreaming;
   }

   @NotNull
   public final String getClientAssignedUUID() {
      return this.clientAssignedUUID;
   }

   public final boolean component1() {
      return this.isFinStreaming;
   }

   @NotNull
   public final String component2() {
      return this.clientAssignedUUID;
   }

   @NotNull
   public final FinStreamingData copy(boolean var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var2, "clientAssignedUUID");
      return new FinStreamingData(var1, var2);
   }

   // $VF: synthetic method
   public static FinStreamingData copy$default(FinStreamingData var0, boolean var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.isFinStreaming;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.clientAssignedUUID;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "FinStreamingData(isFinStreaming=" + this.isFinStreaming + ", clientAssignedUUID=" + this.clientAssignedUUID + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.isFinStreaming);
      return var1 * 31 + this.clientAssignedUUID.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof FinStreamingData)) {
         return false;
      }

      FinStreamingData var2 = (FinStreamingData)var1;
      return this.isFinStreaming != var2.isFinStreaming ? false : Intrinsics.areEqual(this.clientAssignedUUID, var2.clientAssignedUUID);
   }

   public FinStreamingData() {
      this(false, null, 3, null);
   }
}
