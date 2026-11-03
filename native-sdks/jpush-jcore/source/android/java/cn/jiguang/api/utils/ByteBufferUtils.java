package cn.jiguang.api.utils;

import cn.jiguang.api.JResponse;
import cn.jiguang.bi.RecoverTypeaf2b0042;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferUtils {
   private static final String TAG = "ByteBufferUtils";
   public static final int ERROR_CODE = 10000;

   public static int getInt(ByteBuffer var0, JResponse var1) {
      try {
         return var0.getInt();
      } catch (BufferUnderflowException var3) {
         onException(var3.fillInStackTrace(), var1, var0);
      } catch (BufferOverflowException var4) {
         onException(var4.fillInStackTrace(), var1, var0);
      } catch (Exception var5) {
         onException(var5.fillInStackTrace(), var1, var0);
      }

      if (var1 != null) {
         var1.code = 10000;
      }

      return -1;
   }

   public static short getShort(ByteBuffer var0, JResponse var1) {
      try {
         return var0.getShort();
      } catch (BufferUnderflowException var3) {
         onException(var3.fillInStackTrace(), var1, var0);
      } catch (BufferOverflowException var4) {
         onException(var4.fillInStackTrace(), var1, var0);
      } catch (Exception var5) {
         onException(var5.fillInStackTrace(), var1, var0);
      }

      if (var1 != null) {
         var1.code = 10000;
      }

      return -1;
   }

   public static ByteBuffer get(ByteBuffer var0, byte[] var1, JResponse var2) {
      try {
         return var0.get(var1);
      } catch (BufferUnderflowException var4) {
         onException(var4.fillInStackTrace(), var2, var0);
      } catch (BufferOverflowException var5) {
         onException(var5.fillInStackTrace(), var2, var0);
      } catch (Exception var6) {
         onException(var6.fillInStackTrace(), var2, var0);
      }

      if (var2 != null) {
         var2.code = 10000;
      }

      return null;
   }

   public static Byte get(ByteBuffer var0, JResponse var1) {
      try {
         return var0.get();
      } catch (BufferUnderflowException var3) {
         onException(var3.fillInStackTrace(), var1, var0);
      } catch (BufferOverflowException var4) {
         onException(var4.fillInStackTrace(), var1, var0);
      } catch (Exception var5) {
         onException(var5.fillInStackTrace(), var1, var0);
      }

      if (var1 != null) {
         var1.code = 10000;
      }

      return null;
   }

   public static long getLong(ByteBuffer var0, JResponse var1) {
      try {
         return var0.getLong();
      } catch (BufferUnderflowException var3) {
         onException(var3.fillInStackTrace(), var1, var0);
      } catch (BufferOverflowException var4) {
         onException(var4.fillInStackTrace(), var1, var0);
      } catch (Exception var5) {
         onException(var5.fillInStackTrace(), var1, var0);
      }

      if (var1 != null) {
         var1.code = 10000;
      }

      return 0L;
   }

   private static void onException(Throwable var0, JResponse var1, ByteBuffer var2) {
      generalExtraStr(var0, var1, var2);
   }

   private static String generalExtraStr(Throwable var0, JResponse var1, ByteBuffer var2) {
      StringBuilder var3 = new StringBuilder();
      if (var1 != null) {
         var3.append(var1.toString());
         var3.append("|bytebuffer:" + (var2 == null ? "byteBuffer is null" : var2.toString()));
      }

      RecoverTypeaf2b0042.recoverMethod_f_e925fa09("ByteBufferUtils", "byteBuffer info:" + var3.toString());

      try {
         StringWriter var4 = new StringWriter();
         PrintWriter var5 = new PrintWriter(var4);
         var0.printStackTrace(var5);
         String var6 = var4.toString();
         RecoverTypeaf2b0042.recoverMethod_f_e925fa09("ByteBufferUtils", "parse data error stackTrace:" + var6);
      } catch (Exception var7) {
      }

      return var3.toString();
   }
}
