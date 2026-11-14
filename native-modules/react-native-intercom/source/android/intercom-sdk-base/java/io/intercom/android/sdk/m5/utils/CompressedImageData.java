package io.intercom.android.sdk.m5.utils;

import android.net.Uri;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001b\u001a\u00020\u001cHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/m5/utils/CompressedImageData;",
         "",
         "uri",
         "Landroid/net/Uri;",
         "width",
         "",
         "height",
         "size",
         "",
         "<init>",
         "(Landroid/net/Uri;IIJ)V",
         "getUri",
         "()Landroid/net/Uri;",
         "getWidth",
         "()I",
         "getHeight",
         "getSize",
         "()J",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
final class CompressedImageData {
   @NotNull
   private final Uri uri;
   private final int width;
   private final int height;
   private final long size;

   public CompressedImageData(@NotNull Uri var1, int var2, int var3, long var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "uri");
      this.uri = var1;
      this.width = var2;
      this.height = var3;
      this.size = var4;
   }

   @NotNull
   public final Uri getUri() {
      return this.uri;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final long getSize() {
      return this.size;
   }

   @NotNull
   public final Uri component1() {
      return this.uri;
   }

   public final int component2() {
      return this.width;
   }

   public final int component3() {
      return this.height;
   }

   public final long component4() {
      return this.size;
   }

   @NotNull
   public final CompressedImageData copy(@NotNull Uri var1, int var2, int var3, long var4) {
      Intrinsics.checkNotNullParameter(var1, "uri");
      return new CompressedImageData(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static CompressedImageData copy$default(CompressedImageData var0, Uri var1, int var2, int var3, long var4, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.uri;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.width;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.height;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.size;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "CompressedImageData(uri=" + this.uri + ", width=" + this.width + ", height=" + this.height + ", size=" + this.size + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.uri.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.width);
      var1 = var1 * 31 + Integer.hashCode(this.height);
      return var1 * 31 + Long.hashCode(this.size);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CompressedImageData)) {
         return false;
      } else {
         CompressedImageData var2 = (CompressedImageData)var1;
         if (!Intrinsics.areEqual(this.uri, var2.uri)) {
            return false;
         } else if (this.width != var2.width) {
            return false;
         } else {
            return this.height != var2.height ? false : this.size == var2.size;
         }
      }
   }
}
