package cn.jiguang.bk;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.Arrays;

public class RecoverTypeaf2b07d3 {
   private byte[] recoverField_a = new byte[]{
      -42,
      -112,
      -23,
      -2,
      -52,
      -31,
      61,
      -73,
      22,
      -74,
      20,
      -62,
      40,
      -5,
      44,
      5,
      43,
      103,
      -102,
      118,
      42,
      -66,
      4,
      -61,
      -86,
      68,
      19,
      38,
      73,
      -122,
      6,
      -103,
      -100,
      66,
      80,
      -12,
      -111,
      -17,
      -104,
      122,
      51,
      84,
      11,
      67,
      -19,
      -49,
      -84,
      98,
      -28,
      -77,
      28,
      -87,
      -55,
      8,
      -24,
      -107,
      -128,
      -33,
      -108,
      -6,
      117,
      -113,
      63,
      -90,
      71,
      7,
      -89,
      -4,
      -13,
      115,
      23,
      -70,
      -125,
      89,
      60,
      25,
      -26,
      -123,
      79,
      -88,
      104,
      107,
      -127,
      -78,
      113,
      100,
      -38,
      -117,
      -8,
      -21,
      15,
      75,
      112,
      86,
      -99,
      53,
      30,
      36,
      14,
      94,
      99,
      88,
      -47,
      -94,
      37,
      34,
      124,
      59,
      1,
      33,
      120,
      -121,
      -44,
      0,
      70,
      87,
      -97,
      -45,
      39,
      82,
      76,
      54,
      2,
      -25,
      -96,
      -60,
      -56,
      -98,
      -22,
      -65,
      -118,
      -46,
      64,
      -57,
      56,
      -75,
      -93,
      -9,
      -14,
      -50,
      -7,
      97,
      21,
      -95,
      -32,
      -82,
      93,
      -92,
      -101,
      52,
      26,
      85,
      -83,
      -109,
      50,
      48,
      -11,
      -116,
      -79,
      -29,
      29,
      -10,
      -30,
      46,
      -126,
      102,
      -54,
      96,
      -64,
      41,
      35,
      -85,
      13,
      83,
      78,
      111,
      -43,
      -37,
      55,
      69,
      -34,
      -3,
      -114,
      47,
      3,
      -1,
      106,
      114,
      109,
      108,
      91,
      81,
      -115,
      27,
      -81,
      -110,
      -69,
      -35,
      -68,
      127,
      17,
      -39,
      92,
      65,
      31,
      16,
      90,
      -40,
      10,
      -63,
      49,
      -120,
      -91,
      -51,
      123,
      -67,
      45,
      116,
      -48,
      18,
      -72,
      -27,
      -76,
      -80,
      -119,
      105,
      -105,
      74,
      12,
      -106,
      119,
      126,
      101,
      -71,
      -15,
      9,
      -59,
      110,
      -58,
      -124,
      24,
      -16,
      125,
      -20,
      58,
      -36,
      77,
      32,
      121,
      -18,
      95,
      62,
      -41,
      -53,
      57,
      72
   };
   private int[] recoverField_b = new int[]{
      462357,
      472066609,
      943670861,
      1415275113,
      1886879365,
      -1936483679,
      -1464879427,
      -993275175,
      -521670923,
      -66909679,
      404694573,
      876298825,
      1347903077,
      1819507329,
      -2003855715,
      -1532251463,
      -1060647211,
      -589042959,
      -117504499,
      337322537,
      808926789,
      1280531041,
      1752135293,
      -2071227751,
      -1599623499,
      -1128019247,
      -656414995,
      -184876535,
      269950501,
      741554753,
      1213159005,
      1684763257
   };

   private int recoverMethod_a_255f288(int var1, int var2) {
      return var1 << var2 | var1 >>> 32 - var2;
   }

   private int recoverMethod_a_134621(int var1) {
      return (this.recoverField_a[var1 >>> 24 & 0xFF] & 0xFF) << 24
         | (this.recoverField_a[var1 >>> 16 & 0xFF] & 0xFF) << 16
         | (this.recoverField_a[var1 >>> 8 & 0xFF] & 0xFF) << 8
         | this.recoverField_a[var1 & 0xFF] & 0xFF;
   }

   private int recoverMethod_b_134621(int var1) {
      return var1
         ^ this.recoverMethod_a_255f288(var1, 2)
         ^ this.recoverMethod_a_255f288(var1, 10)
         ^ this.recoverMethod_a_255f288(var1, 18)
         ^ this.recoverMethod_a_255f288(var1, 24);
   }

   private int recoverMethod_c_134621(int var1) {
      return var1 ^ this.recoverMethod_a_255f288(var1, 13) ^ this.recoverMethod_a_255f288(var1, 23);
   }

   private int[] recoverMethod_a_4962d968(byte[] var1) {
      int[] var2 = new int[4];

      for (int var3 = 0; var3 < 4; var3++) {
         var2[var3] = (var1[4 * var3] & 255) << 24 | (var1[1 + 4 * var3] & 255) << 16 | (var1[2 + 4 * var3] & 255) << 8 | var1[3 + 4 * var3] & 255;
      }

      return var2;
   }

   public void recoverMethod_a_5a2d1711(byte[] var1, byte[] var2, int[] var3) {
      int[] var4 = this.recoverMethod_a_4962d968(var1);

      for (byte var5 = 0; var5 < 32; var5 += 4) {
         var4[0] ^= this.recoverMethod_b_134621(this.recoverMethod_a_134621(var4[1] ^ var4[2] ^ var4[3] ^ var3[var5]));
         var4[1] ^= this.recoverMethod_b_134621(this.recoverMethod_a_134621(var4[2] ^ var4[3] ^ var4[0] ^ var3[var5 + 1]));
         var4[2] ^= this.recoverMethod_b_134621(this.recoverMethod_a_134621(var4[3] ^ var4[0] ^ var4[1] ^ var3[var5 + 2]));
         var4[3] ^= this.recoverMethod_b_134621(this.recoverMethod_a_134621(var4[0] ^ var4[1] ^ var4[2] ^ var3[var5 + 3]));
      }

      for (byte var6 = 0; var6 < 16; var6 += 4) {
         var2[var6] = (byte)(var4[3 - var6 / 4] >>> 24 & 0xFF);
         var2[var6 + 1] = (byte)(var4[3 - var6 / 4] >>> 16 & 0xFF);
         var2[var6 + 2] = (byte)(var4[3 - var6 / 4] >>> 8 & 0xFF);
         var2[var6 + 3] = (byte)(var4[3 - var6 / 4] & 0xFF);
      }
   }

   public int[] recoverMethod_a_e30de08c(byte[] var1, boolean var2) {
      int[] var3 = this.recoverMethod_a_4962d968(var1);
      var3[0] ^= -1548633402;
      var3[1] ^= 1453994832;
      var3[2] ^= 1736282519;
      var3[3] ^= -1301273892;
      int[] var4 = new int[32];

      for (byte var5 = 0; var5 < 32; var5 += 4) {
         var4[var5] = var3[0] ^= this.recoverMethod_c_134621(this.recoverMethod_a_134621(var3[1] ^ var3[2] ^ var3[3] ^ this.recoverField_b[var5]));
         var4[var5 + 1] = var3[1] ^= this.recoverMethod_c_134621(this.recoverMethod_a_134621(var3[2] ^ var3[3] ^ var3[0] ^ this.recoverField_b[var5 + 1]));
         var4[var5 + 2] = var3[2] ^= this.recoverMethod_c_134621(this.recoverMethod_a_134621(var3[3] ^ var3[0] ^ var3[1] ^ this.recoverField_b[var5 + 2]));
         var4[var5 + 3] = var3[3] ^= this.recoverMethod_c_134621(this.recoverMethod_a_134621(var3[0] ^ var3[1] ^ var3[2] ^ this.recoverField_b[var5 + 3]));
      }

      if (var2) {
         for (int var6 = 0; var6 < 16; var6++) {
            int var7 = var4[var6];
            var4[var6] = var4[31 - var6];
            var4[31 - var6] = var7;
         }
      }

      return var4;
   }

   public byte[] recoverMethod_a_eb729d53(byte[] var1, byte[] var2, byte[] var3) {
      if (var1 != null && var1.length != 0) {
         if (var2 != null && var2.length == 16) {
            if (var3 != null && var3.length != 16) {
               var3 = null;
            }

            var1 = recoverMethod_b_4962d961(var1);
            int[] var4 = this.recoverMethod_a_e30de08c(var2, false);
            byte[] var5 = new byte[16];
            byte[] var6 = var3;
            byte[] var7 = new byte[var1.length];

            for (byte var8 = 0; var8 + 16 <= var1.length; var8 += 16) {
               System.arraycopy(var1, var8, var5, 0, 16);
               if (var6 != null) {
                  recoverMethod_a_e30eacc3(var5, var6);
               } else {
                  var6 = new byte[16];
               }

               this.recoverMethod_a_5a2d1711(var5, var6, var4);
               System.arraycopy(var6, 0, var7, var8, var6.length);
            }

            return var7;
         } else {
            throw new IllegalArgumentException("key's length should be 16");
         }
      } else {
         return var1;
      }
   }

   public byte[] recoverMethod_b_eb729d53(byte[] var1, byte[] var2, byte[] var3) {
      if (var1 != null && var1.length != 0) {
         if (var2 != null && var2.length == 16) {
            if (var3 != null && var3.length != 16) {
               var3 = null;
            }

            int[] var4 = this.recoverMethod_a_e30de08c(var2, true);
            byte[] var5 = new byte[16];
            byte[] var6 = var3;
            byte[] var7 = new byte[16];
            byte[] var8 = new byte[var1.length];

            for (byte var9 = 0; var9 + 16 <= var1.length; var9 += 16) {
               System.arraycopy(var1, var9, var5, 0, 16);
               this.recoverMethod_a_5a2d1711(var5, var7, var4);
               if (var6 != null) {
                  recoverMethod_a_e30eacc3(var7, var6);
               } else {
                  var6 = new byte[16];
               }

               System.arraycopy(var1, var9, var6, 0, 16);
               System.arraycopy(var7, 0, var8, var9, var7.length);
            }

            return recoverMethod_c_4962d961(var8);
         } else {
            throw new IllegalArgumentException("key's length should be 16");
         }
      } else {
         return var1;
      }
   }

   private static void recoverMethod_a_e30eacc3(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         var0[var2] ^= var1[var2];
      }
   }

   private static byte[] recoverMethod_b_4962d961(byte[] var0) {
      int var1 = 16 - var0.length % 16;
      byte[] var2 = Arrays.copyOf(var0, var0.length + var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var0.length + var3] = (byte)var1;
      }

      return var2;
   }

   private static byte[] recoverMethod_c_4962d961(byte[] var0) {
      byte var1 = var0[var0.length - 1];
      byte[] var2 = new byte[var0.length - var1];
      System.arraycopy(var0, 0, var2, 0, var2.length);
      return var2;
   }

   public byte[] recoverMethod_a_19174fd7(byte[] var1, String var2) throws java.io.UnsupportedEncodingException {
      byte[] var3 = recoverMethod_a_afa28ebe(var2);
      String var4 = var2.substring(0, 16);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SM4", "seed=" + var2);
      return this.recoverMethod_a_eb729d53(var1, var3, var4.getBytes("utf-8"));
   }

   public byte[] recoverMethod_b_19174fd7(byte[] var1, String var2) throws java.io.UnsupportedEncodingException {
      byte[] var3 = recoverMethod_a_afa28ebe(var2);
      String var4 = var2.substring(0, 16);
      return this.recoverMethod_b_eb729d53(var1, var3, var4.getBytes("utf-8"));
   }

   public static byte[] recoverMethod_a_afa28ebe(String var0) {
      int var1 = var0.length() / 2;
      byte[] var2 = new byte[var1];
      char[] var3 = var0.toLowerCase().toCharArray();

      for (int var4 = 0; var4 < var1; var4++) {
         int var5 = var4 * 2;
         var2[var4] = (byte)(recoverMethod_a_132f9b(var3[var5]) << 4 | recoverMethod_a_132f9b(var3[var5 + 1]));
      }

      return var2;
   }

   private static int recoverMethod_a_132f9b(char var0) {
      return (byte)"0123456789abcdef".indexOf(var0);
   }
}
