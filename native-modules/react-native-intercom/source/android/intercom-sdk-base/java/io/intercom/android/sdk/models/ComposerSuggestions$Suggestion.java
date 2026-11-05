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
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/models/ComposerSuggestions$Suggestion;",
         "",
         "text",
         "",
         "uuid",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getText",
         "()Ljava/lang/String;",
         "getUuid",
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
public final class ComposerSuggestions$Suggestion {
   @SerializedName("text")
   @NotNull
   private final String text;
   @SerializedName("uuid")
   @NotNull
   private final String uuid;
   public static int $stable;

   static {}

   public ComposerSuggestions$Suggestion(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var2, "uuid");
      this.text = var1;
      this.uuid = var2;
   }

   // $VF: synthetic method
   public ComposerSuggestions$Suggestion(String var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) ("") : var1), (((var3 & 2) != 0) ? (String) ("") : var2));
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   public final String getUuid() {
      return this.uuid;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   @NotNull
   public final String component2() {
      return this.uuid;
   }

   @NotNull
   public final ComposerSuggestions$Suggestion copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var2, "uuid");
      return new ComposerSuggestions$Suggestion(var1, var2);
   }

   // $VF: synthetic method
   public static ComposerSuggestions$Suggestion copy$default(ComposerSuggestions$Suggestion var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.uuid;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Suggestion(text=" + this.text + ", uuid=" + this.uuid + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      return var1 * 31 + this.uuid.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ComposerSuggestions$Suggestion)) {
         return false;
      }

      ComposerSuggestions$Suggestion var2 = (ComposerSuggestions$Suggestion)var1;
      return !Intrinsics.areEqual(this.text, var2.text) ? false : Intrinsics.areEqual(this.uuid, var2.uuid);
   }

   public ComposerSuggestions$Suggestion() {
      this(null, null, 3, null);
   }
}
