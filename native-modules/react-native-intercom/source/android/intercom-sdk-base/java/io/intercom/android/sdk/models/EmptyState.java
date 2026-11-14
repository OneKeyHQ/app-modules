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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u0000 \u00192\u00020\u0001:\u0002\u0018\u0019B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0006HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/models/EmptyState;",
         "",
         "title",
         "",
         "text",
         "action",
         "Lio/intercom/android/sdk/models/EmptyState$Action;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/EmptyState$Action;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getText",
         "getAction",
         "()Lio/intercom/android/sdk/models/EmptyState$Action;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Action",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class EmptyState {
   @NotNull
   public static final EmptyState$Companion Companion = new EmptyState$Companion(null);
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("text")
   @NotNull
   private final String text;
   @SerializedName("action")
   @NotNull
   private final EmptyState$Action action;
   public static int $stable;

   static {}
   @NotNull
   private static final EmptyState NULL = new EmptyState(null, null, null, 7, null);

   public EmptyState(@NotNull String var1, @NotNull String var2, @NotNull EmptyState$Action var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "text");
      Intrinsics.checkNotNullParameter(var3, "action");
      this.title = var1;
      this.text = var2;
      this.action = var3;
   }

   // $VF: synthetic method
   public EmptyState(String var1, String var2, EmptyState$Action var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) ("") : var1), (((var4 & 2) != 0) ? (String) ("") : var2), (((var4 & 4) != 0) ? (EmptyState$Action) (new EmptyState$Action(null, null, null, 7, null)) : var3));
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   public final EmptyState$Action getAction() {
      return this.action;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final String component2() {
      return this.text;
   }

   @NotNull
   public final EmptyState$Action component3() {
      return this.action;
   }

   @NotNull
   public final EmptyState copy(@NotNull String var1, @NotNull String var2, @NotNull EmptyState$Action var3) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "text");
      Intrinsics.checkNotNullParameter(var3, "action");
      return new EmptyState(var1, var2, var3);
   }

   // $VF: synthetic method
   public static EmptyState copy$default(EmptyState var0, String var1, String var2, EmptyState$Action var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.text;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.action;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "EmptyState(title=" + this.title + ", text=" + this.text + ", action=" + this.action + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + this.text.hashCode();
      return var1 * 31 + this.action.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof EmptyState)) {
         return false;
      } else {
         EmptyState var2 = (EmptyState)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.text, var2.text) ? false : Intrinsics.areEqual(this.action, var2.action);
         }
      }
   }

   public EmptyState() {
      this(null, null, null, 7, null);
   }

   // $VF: synthetic method
   public static final EmptyState access$getNULL$cp() {
      return NULL;
   }
}
