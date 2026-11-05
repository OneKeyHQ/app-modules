package io.intercom.android.sdk.lightcompressor.video;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0017\b\u0080\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010\u0019\u001a\u00020\tHÆ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0007HÆ\u0003J?\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0007HÆ\u0001J\u0013\u0010\u001c\u001a\u00020\u00052\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001f\u001a\u00020\u0007HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/video/Result;",
         "",
         "index",
         "",
         "success",
         "",
         "failureMessage",
         "",
         "size",
         "",
         "path",
         "<init>",
         "(IZLjava/lang/String;JLjava/lang/String;)V",
         "getIndex",
         "()I",
         "getSuccess",
         "()Z",
         "getFailureMessage",
         "()Ljava/lang/String;",
         "getSize",
         "()J",
         "getPath",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class Result {
   private final int index;
   private final boolean success;
   @Nullable
   private final String failureMessage;
   private final long size;
   @Nullable
   private final String path;

   public Result(int var1, boolean var2, @Nullable String var3, long var4, @Nullable String var6) {
      this.index = var1;
      this.success = var2;
      this.failureMessage = var3;
      this.size = var4;
      this.path = var6;
   }

   // $VF: synthetic method
   public Result(int var1, boolean var2, String var3, long var4, String var6, int var7, DefaultConstructorMarker var8) {
      this(var1, var2, var3, (((var7 & 8) != 0) ? (long) (0L) : var4), (((var7 & 16) != 0) ? (String) (null) : var6));
   }

   public final int getIndex() {
      return this.index;
   }

   public final boolean getSuccess() {
      return this.success;
   }

   @Nullable
   public final String getFailureMessage() {
      return this.failureMessage;
   }

   public final long getSize() {
      return this.size;
   }

   @Nullable
   public final String getPath() {
      return this.path;
   }

   public final int component1() {
      return this.index;
   }

   public final boolean component2() {
      return this.success;
   }

   @Nullable
   public final String component3() {
      return this.failureMessage;
   }

   public final long component4() {
      return this.size;
   }

   @Nullable
   public final String component5() {
      return this.path;
   }

   @NotNull
   public final Result copy(int var1, boolean var2, @Nullable String var3, long var4, @Nullable String var6) {
      return new Result(var1, var2, var3, var4, var6);
   }

   // $VF: synthetic method
   public static Result copy$default(Result var0, int var1, boolean var2, String var3, long var4, String var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.index;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.success;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.failureMessage;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.size;
      }

      if ((var7 & 16) != 0) {
         var6 = var0.path;
      }

      return var0.copy(var1, var2, var3, var4, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "Result(index="
         + this.index
         + ", success="
         + this.success
         + ", failureMessage="
         + this.failureMessage
         + ", size="
         + this.size
         + ", path="
         + this.path
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.index);
      var1 = var1 * 31 + Boolean.hashCode(this.success);
      var1 = var1 * 31 + (this.failureMessage == null ? 0 : this.failureMessage.hashCode());
      var1 = var1 * 31 + Long.hashCode(this.size);
      return var1 * 31 + (this.path == null ? 0 : this.path.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Result)) {
         return false;
      } else {
         Result var2 = (Result)var1;
         if (this.index != var2.index) {
            return false;
         } else if (this.success != var2.success) {
            return false;
         } else if (!Intrinsics.areEqual(this.failureMessage, var2.failureMessage)) {
            return false;
         } else {
            return this.size != var2.size ? false : Intrinsics.areEqual(this.path, var2.path);
         }
      }
   }
}
