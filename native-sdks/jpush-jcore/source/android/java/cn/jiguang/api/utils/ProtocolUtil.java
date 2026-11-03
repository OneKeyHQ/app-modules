package cn.jiguang.api.utils;

import cn.jiguang.api.JResponse;
import cn.jiguang.bi.RecoverTypeaf2b0042;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ProtocolUtil {
   private static final String TAG = "ProtocolUtil";
   private static final String ENCODING_UTF_8 = "UTF-8";

   public static byte[] getBytesConsumed(ByteBuffer var0) {
      try {
         byte[] var1 = new byte[var0.remaining()];
         var0.get(var1);
         return var1;
      } catch (NegativeArraySizeException var2) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("ProtocolUtil", "[getBytesConsumed] - ByteBuffer error.");
         return null;
      } catch (Exception var3) {
         return null;
      }
   }

   public static byte[] getBytes(ByteBuffer var0) {
      try {
         byte[] var1 = new byte[var0.remaining()];
         ByteBuffer var2 = var0.asReadOnlyBuffer();
         ((Buffer)var2).flip();
         var0.get(var1);
         return var1;
      } catch (NegativeArraySizeException var3) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("ProtocolUtil", "[getBytes] - ByteBuffer error.");
         return null;
      } catch (Exception var4) {
         return null;
      }
   }

   public static byte[] tlv2ToByteArray(String var0) {
      if (null != var0 && !"".equals(var0)) {
         byte[] var1 = null;

         try {
            var1 = var0.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var4) {
         }

         if (var1 == null) {
            return new byte[]{0, 0};
         }

         short var2 = (short)var1.length;
         byte[] var3 = new byte[var2 + 2];
         System.arraycopy(short2ByteArray(var2), 0, var3, 0, 2);
         System.arraycopy(var1, 0, var3, 2, var2);
         return var3;
      } else {
         return new byte[]{0, 0};
      }
   }

   public static byte[] fixedStringToBytes(String var0, int var1) {
      if (null != var0 && !"".equals(var0)) {
         byte[] var2 = null;

         try {
            var2 = var0.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var5) {
         }

         if (var2 == null) {
            return new byte[]{0, 0, 0, 0};
         }

         byte[] var3 = getDefaultByte(var1);
         int var4 = var2.length > var1 ? var1 : var2.length;
         System.arraycopy(var2, 0, var3, 0, var4);
         return var3;
      } else {
         return new byte[]{0, 0, 0, 0};
      }
   }

   public static void fillIntData(byte[] var0, int var1, int var2) {
      System.arraycopy(int2ByteArray(var1), 0, var0, var2, 4);
   }

   public static void fillStringData(byte[] var0, String var1, int var2) {
      byte[] var3 = var1.getBytes();
      System.arraycopy(var3, 0, var0, var2, var3.length);
   }

   public static byte[] getDefaultByte(int var0) {
      byte[] var1 = new byte[var0];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[0] = 0;
      }

      return var1;
   }

   public static String getTlv2(ByteBuffer var0, JResponse var1) {
      short var2 = ByteBufferUtils.getShort(var0, var1);
      if (var2 < 0) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("ProtocolUtil", "[getTlv2] - ByteBuffer error.");
         return null;
      }

      byte[] var3 = new byte[var2];
      ByteBufferUtils.get(var0, var3, var1);

      try {
         return new String(var3, "UTF-8");
      } catch (Throwable var5) {
         return null;
      }
   }

   public static String getTlv2(ByteBuffer var0) {
      try {
         short var1 = var0.getShort();
         byte[] var2 = new byte[var1];
         var0.get(var2);
         return new String(var2, "UTF-8");
      } catch (UnsupportedEncodingException var3) {
      } catch (Exception var4) {
      }

      return null;
   }

   public static String getString(ByteBuffer var0, int var1) {
      byte[] var2 = new byte[var1];
      var0.get(var2);

      try {
         return new String(var2, "UTF-8");
      } catch (UnsupportedEncodingException var4) {
         return null;
      }
   }

   public static byte[] short2ByteArray(short var0) {
      return new byte[]{(byte)(var0 >>> 8), (byte)var0};
   }

   public static byte[] int2ByteArray(int var0) {
      return new byte[]{(byte)(var0 >>> 24), (byte)(var0 >>> 16), (byte)(var0 >>> 8), (byte)var0};
   }

   public static byte[] long2ByteArray(long var0) {
      return new byte[]{
         (byte)(var0 >>> 56),
         (byte)(var0 >>> 48),
         (byte)(var0 >>> 40),
         (byte)(var0 >>> 32),
         (byte)(var0 >>> 24),
         (byte)(var0 >>> 16),
         (byte)(var0 >>> 8),
         (byte)var0
      };
   }
}
