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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00072\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/models/HeaderConfig;",
         "",
         "background",
         "Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "content",
         "Lio/intercom/android/sdk/models/HeaderContentModel;",
         "headerExpanded",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/HeaderBackgroundModel;Lio/intercom/android/sdk/models/HeaderContentModel;Z)V",
         "getBackground",
         "()Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "getContent",
         "()Lio/intercom/android/sdk/models/HeaderContentModel;",
         "getHeaderExpanded",
         "()Z",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HeaderConfig {
   @SerializedName("background")
   @NotNull
   private final HeaderBackgroundModel background;
   @SerializedName("content")
   @NotNull
   private final HeaderContentModel content;
   @SerializedName("header_expanded")
   private final boolean headerExpanded;
   public static final int $stable;

   static { $stable = 8; }

   public HeaderConfig(@NotNull HeaderBackgroundModel var1, @NotNull HeaderContentModel var2, boolean var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "background");
      Intrinsics.checkNotNullParameter(var2, "content");
      this.background = var1;
      this.content = var2;
      this.headerExpanded = var3;
   }

   // $VF: synthetic method
   public HeaderConfig(HeaderBackgroundModel var1, HeaderContentModel var2, boolean var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (HeaderBackgroundModel) (new HeaderBackgroundModel(null, null, null, null, null, null, false, null, null, null, false, 2047, null)) : var1), (((var4 & 2) != 0) ? (HeaderContentModel) (new HeaderContentModel(null, null, false, null, null, null, null, null, 255, null)) : var2), (((var4 & 4) != 0) ? (boolean) (true) : var3));
   }

   @NotNull
   public final HeaderBackgroundModel getBackground() {
      return this.background;
   }

   @NotNull
   public final HeaderContentModel getContent() {
      return this.content;
   }

   public final boolean getHeaderExpanded() {
      return this.headerExpanded;
   }

   @NotNull
   public final HeaderBackgroundModel component1() {
      return this.background;
   }

   @NotNull
   public final HeaderContentModel component2() {
      return this.content;
   }

   public final boolean component3() {
      return this.headerExpanded;
   }

   @NotNull
   public final HeaderConfig copy(@NotNull HeaderBackgroundModel var1, @NotNull HeaderContentModel var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "background");
      Intrinsics.checkNotNullParameter(var2, "content");
      return new HeaderConfig(var1, var2, var3);
   }

   // $VF: synthetic method
   public static HeaderConfig copy$default(HeaderConfig var0, HeaderBackgroundModel var1, HeaderContentModel var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.background;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.content;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.headerExpanded;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "HeaderConfig(background=" + this.background + ", content=" + this.content + ", headerExpanded=" + this.headerExpanded + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.background.hashCode();
      var1 = var1 * 31 + this.content.hashCode();
      return var1 * 31 + Boolean.hashCode(this.headerExpanded);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderConfig)) {
         return false;
      } else {
         HeaderConfig var2 = (HeaderConfig)var1;
         if (!Intrinsics.areEqual(this.background, var2.background)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.content, var2.content) ? false : this.headerExpanded == var2.headerExpanded;
         }
      }
   }

   public HeaderConfig() {
      this(null, null, false, 7, null);
   }
}
