package io.intercom.android.sdk.m5.conversation.data;

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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/AblyMessage;",
         "",
         "eventName",
         "",
         "eventData",
         "Lio/intercom/android/sdk/m5/conversation/data/AblyEventData;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/data/AblyEventData;)V",
         "getEventName",
         "()Ljava/lang/String;",
         "getEventData",
         "()Lio/intercom/android/sdk/m5/conversation/data/AblyEventData;",
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
public final class AblyMessage {
   @SerializedName("eventName")
   @NotNull
   private final String eventName;
   @SerializedName("eventData")
   @NotNull
   private final AblyEventData eventData;
   public static final int $stable;

   static { $stable = 8; }

   public AblyMessage(@NotNull String var1, @NotNull AblyEventData var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "eventName");
      Intrinsics.checkNotNullParameter(var2, "eventData");
      this.eventName = var1;
      this.eventData = var2;
   }

   @NotNull
   public final String getEventName() {
      return this.eventName;
   }

   @NotNull
   public final AblyEventData getEventData() {
      return this.eventData;
   }

   @NotNull
   public final String component1() {
      return this.eventName;
   }

   @NotNull
   public final AblyEventData component2() {
      return this.eventData;
   }

   @NotNull
   public final AblyMessage copy(@NotNull String var1, @NotNull AblyEventData var2) {
      Intrinsics.checkNotNullParameter(var1, "eventName");
      Intrinsics.checkNotNullParameter(var2, "eventData");
      return new AblyMessage(var1, var2);
   }

   // $VF: synthetic method
   public static AblyMessage copy$default(AblyMessage var0, String var1, AblyEventData var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.eventName;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.eventData;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AblyMessage(eventName=" + this.eventName + ", eventData=" + this.eventData + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.eventName.hashCode();
      return var1 * 31 + this.eventData.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AblyMessage)) {
         return false;
      }

      AblyMessage var2 = (AblyMessage)var1;
      return !Intrinsics.areEqual(this.eventName, var2.eventName) ? false : Intrinsics.areEqual(this.eventData, var2.eventData);
   }
}
