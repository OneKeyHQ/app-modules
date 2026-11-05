package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Weight;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/EventLabelSegment;",
         "",
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
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class EventLabelSegment {
   @NotNull
   private final String text;
   @Nullable
   private final Weight weight;
   public static int $stable;

   static {}

   public EventLabelSegment(@NotNull String var1, @Nullable Weight var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      this.text = var1;
      this.weight = var2;
   }

   // $VF: synthetic method
   public EventLabelSegment(String var1, Weight var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (Weight) (null) : var2));
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final Weight getWeight() {
      return this.weight;
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
   public final EventLabelSegment copy(@NotNull String var1, @Nullable Weight var2) {
      Intrinsics.checkNotNullParameter(var1, "text");
      return new EventLabelSegment(var1, var2);
   }

   // $VF: synthetic method
   public static EventLabelSegment copy$default(EventLabelSegment var0, String var1, Weight var2, int var3, Object var4) {
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
      return "EventLabelSegment(text=" + this.text + ", weight=" + this.weight + ')';
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

      if (!(var1 instanceof EventLabelSegment)) {
         return false;
      }

      EventLabelSegment var2 = (EventLabelSegment)var1;
      return !Intrinsics.areEqual(this.text, var2.text) ? false : this.weight == var2.weight;
   }
}
