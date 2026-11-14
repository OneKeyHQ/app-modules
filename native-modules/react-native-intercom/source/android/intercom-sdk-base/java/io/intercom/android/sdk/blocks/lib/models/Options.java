package io.intercom.android.sdk.blocks.lib.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B+\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\bHÆ\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0018H×\u0001J\t\u0010\u0019\u001a\u00020\bH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/Options;",
         "",
         "multiline",
         "",
         "listOptions",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/ListOption;",
         "accept",
         "",
         "<init>",
         "(ZLjava/util/List;Ljava/lang/String;)V",
         "getMultiline",
         "()Z",
         "getListOptions",
         "()Ljava/util/List;",
         "getAccept",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Options {
   @SerializedName("multiline")
   private final boolean multiline;
   @SerializedName("list_options")
   @NotNull
   private final List listOptions;
   @SerializedName("accept")
   @NotNull
   private final String accept;
   public static final int $stable;

   static { $stable = 8; }

   public Options(boolean var1, @NotNull List var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var2, "listOptions");
      Intrinsics.checkNotNullParameter(var3, "accept");
      this.multiline = var1;
      this.listOptions = var2;
      this.accept = var3;
   }

   // $VF: synthetic method
   public Options(boolean var1, List var2, String var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (boolean) (false) : var1), (((var4 & 2) != 0) ? (List) (CollectionsKt.emptyList()) : var2), (((var4 & 4) != 0) ? (String) ("") : var3));
   }

   public final boolean getMultiline() {
      return this.multiline;
   }

   @NotNull
   public final List getListOptions() {
      return this.listOptions;
   }

   @NotNull
   public final String getAccept() {
      return this.accept;
   }

   public final boolean component1() {
      return this.multiline;
   }

   @NotNull
   public final List component2() {
      return this.listOptions;
   }

   @NotNull
   public final String component3() {
      return this.accept;
   }

   @NotNull
   public final Options copy(boolean var1, @NotNull List var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var2, "listOptions");
      Intrinsics.checkNotNullParameter(var3, "accept");
      return new Options(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Options copy$default(Options var0, boolean var1, List var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.multiline;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.listOptions;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.accept;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Options(multiline=" + this.multiline + ", listOptions=" + this.listOptions + ", accept=" + this.accept + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.multiline);
      var1 = var1 * 31 + this.listOptions.hashCode();
      return var1 * 31 + this.accept.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Options)) {
         return false;
      } else {
         Options var2 = (Options)var1;
         if (this.multiline != var2.multiline) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.listOptions, var2.listOptions) ? false : Intrinsics.areEqual(this.accept, var2.accept);
         }
      }
   }

   public Options() {
      this(false, null, null, 7, null);
   }
}
