package io.intercom.android.sdk.lightcompressor.utils;

import kotlin.Metadata;
import kotlin.math.MathKt;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\u001a\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0001H\u0000\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0001H\u0000\u001a\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003H\u0000\u001a\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0003H\u0002\u001a\u0018\u0010\n\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000¨\u0006\r",
   d2 = {
         "uInt32ToLong",
         "",
         "int32",
         "",
         "uInt32ToInt",
         "uInt32",
         "uInt64ToLong",
         "uInt64",
         "roundEven",
         "value",
         "generateWidthHeightValue",
         "",
         "factor",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class NumbersUtilsKt {
   public static final long uInt32ToLong(int var0) {
      return var0;
   }

   public static final <E extends Exception> int uInt32ToInt(long var0) throws E {
      if (var0 <= 2147483647L && var0 >= 0L) {
         return (int)var0;
      } else {
         throw (E)new Exception("uInt32 value is too large");
      }
   }

   public static final <E extends Exception> long uInt64ToLong(long var0) throws E {
      if (var0 < 0L) {
         throw (E)new Exception("uInt64 value is too large");
      } else {
         return var0;
      }
   }

   public static final <E extends Exception> int uInt32ToInt(int var0) throws E {
      if (var0 < 0) {
         throw (E)new Exception("uInt32 value is too large");
      } else {
         return var0;
      }
   }

   private static final int roundEven(int var0) {
      return var0 + 1 & -2;
   }

   public static final int generateWidthHeightValue(double var0, double var2) {
      return roundEven(MathKt.roundToInt(var0 * var2 / 16) * 16);
   }
}
