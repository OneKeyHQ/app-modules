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
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00052\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0011\u001a\u00020\u0012H×\u0001J\t\u0010\u0013\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "",
         "label",
         "",
         "pinned",
         "",
         "<init>",
         "(Ljava/lang/String;Z)V",
         "getLabel",
         "()Ljava/lang/String;",
         "getPinned",
         "()Z",
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
public final class SenderMetadata {
   @NotNull
   private final String label;
   private final boolean pinned;
   public static int $stable;

   static {}

   public SenderMetadata(@NotNull String var1, boolean var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "label");
      this.label = var1;
      this.pinned = var2;
   }

   // $VF: synthetic method
   public SenderMetadata(String var1, boolean var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (boolean) (false) : var2));
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   public final boolean getPinned() {
      return this.pinned;
   }

   @NotNull
   public final String component1() {
      return this.label;
   }

   public final boolean component2() {
      return this.pinned;
   }

   @NotNull
   public final SenderMetadata copy(@NotNull String var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "label");
      return new SenderMetadata(var1, var2);
   }

   // $VF: synthetic method
   public static SenderMetadata copy$default(SenderMetadata var0, String var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.label;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.pinned;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "SenderMetadata(label=" + this.label + ", pinned=" + this.pinned + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.label.hashCode();
      return var1 * 31 + Boolean.hashCode(this.pinned);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SenderMetadata)) {
         return false;
      }

      SenderMetadata var2 = (SenderMetadata)var1;
      return !Intrinsics.areEqual(this.label, var2.label) ? false : this.pinned == var2.pinned;
   }
}
