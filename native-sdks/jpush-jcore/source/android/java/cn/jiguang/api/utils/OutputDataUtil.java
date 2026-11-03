package cn.jiguang.api.utils;

import cn.jiguang.bi.RecoverTypeaf2b0042;
import java.math.BigInteger;

public class OutputDataUtil {
   private static final String TAG = "OutputDataUtil";
   private byte[] array;
   private int pos;
   private int saved_pos;
   private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);

   public OutputDataUtil(int var1) {
      this.array = new byte[var1];
      this.pos = 0;
      this.saved_pos = -1;
   }

   public OutputDataUtil() {
      this(32);
   }

   public int current() {
      return this.pos;
   }

   private void check(long var1, int var3) {
      long var4 = 1L;
      var4 <<= var3;
      if (var1 < 0L || var1 > var4) {
         RecoverTypeaf2b0042.recoverMethod_c_e925fa09("OutputDataUtil", var1 + " out of range for " + var3 + " bit value max:" + var4);
      }
   }

   private void need(int var1) {
      if (this.array.length - this.pos < var1) {
         int var2 = this.array.length * 2;
         if (var2 < this.pos + var1) {
            var2 = this.pos + var1;
         }

         byte[] var3 = new byte[var2];
         System.arraycopy(this.array, 0, var3, 0, this.pos);
         this.array = var3;
      }
   }

   public void jump(int var1) {
      if (var1 > this.pos) {
         throw new IllegalArgumentException("cannot jump past end of data");
      }

      this.pos = var1;
   }

   public void save() {
      this.saved_pos = this.pos;
   }

   public void restore() {
      if (this.saved_pos < 0) {
         throw new IllegalStateException("no previous state");
      }

      this.pos = this.saved_pos;
      this.saved_pos = -1;
   }

   public void writeU8(int var1) {
      this.check(var1, 8);
      this.need(1);
      this.array[this.pos++] = (byte)(var1 & 0xFF);
   }

   public void writeU8At(int var1, int var2) {
      this.check(var1, 8);
      if (var2 > this.pos - 1) {
         throw new IllegalArgumentException("cannot write past end of data");
      }

      this.array[var2] = (byte)(var1 & 0xFF);
   }

   public void writeU16(int var1) {
      this.check(var1, 16);
      this.need(2);
      this.array[this.pos++] = (byte)(var1 >>> 8 & 0xFF);
      this.array[this.pos++] = (byte)(var1 & 0xFF);
   }

   public void writeU16At(int var1, int var2) {
      this.check(var1, 16);
      if (var2 > this.pos - 2) {
         throw new IllegalArgumentException("cannot write past end of data");
      }

      this.array[var2++] = (byte)(var1 >>> 8 & 0xFF);
      this.array[var2++] = (byte)(var1 & 0xFF);
   }

   public void writeU32(long var1) {
      this.check(var1, 32);
      this.need(4);
      this.array[this.pos++] = (byte)(var1 >>> 24 & 255L);
      this.array[this.pos++] = (byte)(var1 >>> 16 & 255L);
      this.array[this.pos++] = (byte)(var1 >>> 8 & 255L);
      this.array[this.pos++] = (byte)(var1 & 255L);
   }

   public void writeU32At(long var1, int var3) {
      this.check(var1, 32);
      if (var3 > this.pos - 4) {
         throw new IllegalArgumentException("cannot write past end of data");
      }

      this.array[var3++] = (byte)(var1 >>> 24 & 255L);
      this.array[var3++] = (byte)(var1 >>> 16 & 255L);
      this.array[var3++] = (byte)(var1 >>> 8 & 255L);
      this.array[var3++] = (byte)(var1 & 255L);
   }

   public static int encodeZigZag32(int var0) {
      return var0 << 1 ^ var0 >> 31;
   }

   public static long encodeZigZag64(long var0) {
      return var0 << 1 ^ var0 >> 63;
   }

   public void writeU64(long var1) {
      long var3 = var1;
      this.need(8);
      this.array[this.pos++] = (byte)(var3 >>> 56 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 48 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 40 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 32 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 24 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 16 & 255L);
      this.array[this.pos++] = (byte)(var3 >>> 8 & 255L);
      this.array[this.pos++] = (byte)(var3 & 255L);
   }

   public void writeU64At(long var1, int var3) {
      this.array[var3++] = (byte)(var1 >>> 56 & 255L);
      this.array[var3++] = (byte)(var1 >>> 48 & 255L);
      this.array[var3++] = (byte)(var1 >>> 40 & 255L);
      this.array[var3++] = (byte)(var1 >>> 32 & 255L);
      this.array[var3++] = (byte)(var1 >>> 24 & 255L);
      this.array[var3++] = (byte)(var1 >>> 16 & 255L);
      this.array[var3++] = (byte)(var1 >>> 8 & 255L);
      this.array[var3++] = (byte)(var1 & 255L);
   }

   public void writeRawLittleEndian16(int var1) {
      this.array[this.pos++] = (byte)(var1 & 0xFF);
      this.array[this.pos++] = (byte)(var1 >> 8 & 0xFF);
   }

   public void writeRawLittleEndian32(int var1) {
      this.array[this.pos++] = (byte)(var1 & 0xFF);
      this.array[this.pos++] = (byte)(var1 >> 8 & 0xFF);
      this.array[this.pos++] = (byte)(var1 >> 16 & 0xFF);
      this.array[this.pos++] = (byte)(var1 >> 24 & 0xFF);
   }

   public void writeRawLittleEndian64(long var1) {
      this.array[this.pos++] = (byte)((int)var1 & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 8) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 16) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 24) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 32) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 40) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 48) & 0xFF);
      this.array[this.pos++] = (byte)((int)(var1 >> 56) & 0xFF);
   }

   public void writeByteArray(byte[] var1, int var2, int var3) {
      this.need(var3);
      System.arraycopy(var1, var2, this.array, this.pos, var3);
      this.pos += var3;
   }

   public void writeByteArrayincludeLength(byte[] var1) {
      this.writeU16(var1.length);
      this.writeByteArray(var1, 0, var1.length);
   }

   public void writeByteArray(byte[] var1) {
      this.writeByteArray(var1, 0, var1.length);
   }

   public void writeCountedString(byte[] var1) {
      if (var1.length > 255) {
         throw new IllegalArgumentException("Invalid counted string");
      }

      this.need(1 + var1.length);
      this.array[this.pos++] = (byte)(var1.length & 0xFF);
      this.writeByteArray(var1, 0, var1.length);
   }

   public byte[] toByteArray() {
      byte[] var1 = new byte[this.pos];
      System.arraycopy(this.array, 0, var1, 0, this.pos);
      return var1;
   }
}
