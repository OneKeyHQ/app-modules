package io.intercom.android.sdk.blocks.lib.models;

import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JD\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u0007¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;",
         "name",
         "",
         "url",
         "contentType",
         "humanFileSize",
         "size",
         "",
         "id",
         "intercom-sdk-base_release"
   }
)
public final class BlockAttachment$Companion {
   private BlockAttachment$Companion() {
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, long var5, long var7) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "url");
      Intrinsics.checkNotNullParameter(var3, "contentType");
      Intrinsics.checkNotNullParameter(var4, "humanFileSize");
      return new BlockAttachment(var1, var2, var3, var4, var5, var7);
   }

   // $VF: synthetic method
   public static BlockAttachment create$default(
      BlockAttachment$Companion var0, String var1, String var2, String var3, String var4, long var5, long var7, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = "";
      }

      if ((var9 & 2) != 0) {
         var2 = "";
      }

      if ((var9 & 4) != 0) {
         var3 = "";
      }

      if ((var9 & 8) != 0) {
         var4 = "";
      }

      if ((var9 & 16) != 0) {
         var5 = 0L;
      }

      if ((var9 & 32) != 0) {
         var7 = 0L;
      }

      return var0.create(var1, var2, var3, var4, var5, var7);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, long var5) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "url");
      Intrinsics.checkNotNullParameter(var3, "contentType");
      Intrinsics.checkNotNullParameter(var4, "humanFileSize");
      return create$default(this, var1, var2, var3, var4, var5, 0L, 32, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "url");
      Intrinsics.checkNotNullParameter(var3, "contentType");
      Intrinsics.checkNotNullParameter(var4, "humanFileSize");
      return create$default(this, var1, var2, var3, var4, 0L, 0L, 48, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "url");
      Intrinsics.checkNotNullParameter(var3, "contentType");
      return create$default(this, var1, var2, var3, null, 0L, 0L, 56, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "url");
      return create$default(this, var1, var2, null, null, 0L, 0L, 60, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "name");
      return create$default(this, var1, null, null, null, 0L, 0L, 62, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final BlockAttachment create() {
      return create$default(this, null, null, null, null, 0L, 0L, 63, null);
   }

   // $VF: synthetic method
   public BlockAttachment$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
