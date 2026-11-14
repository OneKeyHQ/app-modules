package io.intercom.android.sdk.blocks.lib.models;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J0\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u0007H\u0007¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/Author$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/blocks/lib/models/Author;",
         "name",
         "",
         "avatar",
         "firstName",
         "lastName",
         "intercom-sdk-base_release"
   }
)
public final class Author$Companion {
   private Author$Companion() {
   }

   @JvmStatic
   @NotNull
   public final Author create(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "avatar");
      Intrinsics.checkNotNullParameter(var3, "firstName");
      Intrinsics.checkNotNullParameter(var4, "lastName");
      return new Author(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Author create$default(Author$Companion var0, String var1, String var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = "";
      }

      if ((var5 & 2) != 0) {
         var2 = "";
      }

      if ((var5 & 4) != 0) {
         var3 = "";
      }

      if ((var5 & 8) != 0) {
         var4 = "";
      }

      return var0.create(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public Author$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
