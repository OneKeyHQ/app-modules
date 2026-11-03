package cn.jiguang.bk;

import android.os.Build.VERSION;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.text.TextUtils;
import cn.jiguang.ac.RecoverTypeaf2a755a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;

public class RecoverTypeaf2b07c3 {
   private static int recoverMethod_a_134621(int var0) {
      return var0 != Integer.MAX_VALUE && var0 != Integer.MIN_VALUE && var0 >= -140 && var0 <= 0 ? var0 : 0;
   }

   private static long recoverMethod_a_256a6b9(long var0, boolean var2) {
      if (var0 != -1L && var0 != 2147483647L && var0 != Long.MAX_VALUE && var0 != -2147483648L && var0 != Long.MIN_VALUE && var0 >= 0L) {
         if (var2) {
            if (var0 >= 68719476735L) {
               return 0L;
            }
         } else if (var0 >= 268435455L) {
            return 0L;
         }

         return var0;
      } else {
         return 0L;
      }
   }

   private static int recoverMethod_b_134621(int var0) {
      return var0 != -1 && var0 != Integer.MAX_VALUE && var0 != Integer.MIN_VALUE && var0 >= 0 && var0 < 65535 ? var0 : 0;
   }

   private static String recoverMethod_a_ed5678a7(CharSequence var0) {
      return var0 != null && !TextUtils.isEmpty(var0.toString()) ? var0.toString().trim() : "";
   }

   private static int recoverMethod_c_134621(int var0) {
      return var0 != Integer.MAX_VALUE && var0 != -1 && var0 >= 1 && var0 <= 999 ? var0 : 0;
   }

   private static int recoverMethod_d_134621(int var0) {
      return var0 != Integer.MAX_VALUE && var0 != -1 && var0 >= 0 && var0 <= 999 ? var0 : 0;
   }

   private static String recoverMethod_a_ec2edeab(int var0, int var1) {
      if (var0 <= 0 || var1 < 0) {
         return "";
      } else if (var0 == 460) {
         return var1 == 11 ? "46011" : String.format("%03d%02d", var0, var1);
      } else {
         return String.format("%03d", var0) + var1;
      }
   }

   private static void recoverMethod_a_8ef12f66(RecoverTypeaf2a755a var0, String var1, String var2) {
      try {
         if (!TextUtils.isEmpty(var1)) {
            var0.recoverField_d = Integer.parseInt(var1);
         } else {
            var0.recoverField_d = 0;
         }

         if (!TextUtils.isEmpty(var2)) {
            var0.recoverField_e = Integer.parseInt(var2);
         } else {
            var0.recoverField_e = 0;
         }
      } catch (NumberFormatException var4) {
         var0.recoverField_d = 0;
         var0.recoverField_e = 0;
      }
   }

   public static RecoverTypeaf2a755a recoverMethod_a_b71aa1d4(CellInfo var0) {
      if (var0 != null && VERSION.SDK_INT >= 23) {
         RecoverTypeaf2a755a var1 = new RecoverTypeaf2a755a();

         try {
            if (var0 instanceof CellInfoGsm) {
               CellInfoGsm var12 = (CellInfoGsm)var0;
               CellSignalStrengthGsm var17 = var12.getCellSignalStrength();
               CellIdentityGsm var22 = var12.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var17.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var22.getCid(), false);
               var1.recoverField_c = recoverMethod_b_134621(var22.getLac());
               if (VERSION.SDK_INT >= 28) {
                  String var26 = var22.getMccString();
                  String var30 = var22.getMncString();
                  recoverMethod_a_8ef12f66(var1, var26, var30);
               } else {
                  var1.recoverField_d = recoverMethod_c_134621(var22.getMcc());
                  var1.recoverField_e = recoverMethod_d_134621(var22.getMnc());
               }

               var1.recoverField_h = "2G";
               var1.recoverField_i = "gsm";
               if (VERSION.SDK_INT >= 28) {
                  var1.recoverField_g = recoverMethod_a_ed5678a7(var22.getOperatorAlphaLong());
               }

               var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
            } else if (var0 instanceof CellInfoLte) {
               CellInfoLte var11 = (CellInfoLte)var0;
               CellSignalStrengthLte var16 = var11.getCellSignalStrength();
               CellIdentityLte var21 = var11.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var16.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var21.getCi(), false);
               var1.recoverField_c = recoverMethod_b_134621(var21.getTac());
               if (VERSION.SDK_INT >= 28) {
                  String var25 = var21.getMccString();
                  String var29 = var21.getMncString();
                  recoverMethod_a_8ef12f66(var1, var25, var29);
               } else {
                  var1.recoverField_d = recoverMethod_c_134621(var21.getMcc());
                  var1.recoverField_e = recoverMethod_d_134621(var21.getMnc());
               }

               var1.recoverField_h = "4G";
               var1.recoverField_i = "lte";
               if (VERSION.SDK_INT >= 28) {
                  var1.recoverField_g = recoverMethod_a_ed5678a7(var21.getOperatorAlphaLong());
               }

               var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
            } else if (var0 instanceof CellInfoWcdma) {
               CellInfoWcdma var10 = (CellInfoWcdma)var0;
               CellSignalStrengthWcdma var15 = var10.getCellSignalStrength();
               CellIdentityWcdma var20 = var10.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var15.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var20.getCid(), false);
               var1.recoverField_c = recoverMethod_b_134621(var20.getLac());
               if (VERSION.SDK_INT >= 28) {
                  String var24 = var20.getMccString();
                  String var28 = var20.getMncString();
                  recoverMethod_a_8ef12f66(var1, var24, var28);
               } else {
                  var1.recoverField_d = recoverMethod_c_134621(var20.getMcc());
                  var1.recoverField_e = recoverMethod_d_134621(var20.getMnc());
               }

               var1.recoverField_h = "3G";
               var1.recoverField_i = "wcdma";
               if (VERSION.SDK_INT >= 28) {
                  var1.recoverField_g = recoverMethod_a_ed5678a7(var20.getOperatorAlphaLong());
               }

               var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
            } else if (var0 instanceof CellInfoCdma) {
               CellInfoCdma var9 = (CellInfoCdma)var0;
               CellSignalStrengthCdma var14 = var9.getCellSignalStrength();
               CellIdentityCdma var19 = var9.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var14.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var19.getBasestationId(), false);
               var1.recoverField_c = recoverMethod_b_134621(var19.getNetworkId());
               var1.recoverField_d = 0;
               var1.recoverField_e = 0;
               var1.recoverField_h = "3G";
               var1.recoverField_i = "cdma2000";
               if (VERSION.SDK_INT >= 28) {
                  var1.recoverField_g = recoverMethod_a_ed5678a7(var19.getOperatorAlphaLong());
               }

               var1.recoverField_f = "";
            } else if (VERSION.SDK_INT >= 29 && var0 instanceof CellInfoNr) {
               CellInfoNr var8 = (CellInfoNr)var0;
               CellSignalStrength var13 = var8.getCellSignalStrength();
               CellIdentityNr var18 = (CellIdentityNr)var8.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var13.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var18.getNci(), true);
               var1.recoverField_c = recoverMethod_b_134621(var18.getTac());
               String var23 = var18.getMccString();
               String var27 = var18.getMncString();
               recoverMethod_a_8ef12f66(var1, var23, var27);
               var1.recoverField_h = "5G";
               var1.recoverField_i = "nr";
               var1.recoverField_g = recoverMethod_a_ed5678a7(var18.getOperatorAlphaLong());
               var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
            } else if (VERSION.SDK_INT >= 29 && var0 instanceof CellInfoTdscdma) {
               CellInfoTdscdma var2 = (CellInfoTdscdma)var0;
               CellSignalStrengthTdscdma var3 = var2.getCellSignalStrength();
               CellIdentityTdscdma var4 = var2.getCellIdentity();
               var1.recoverField_j = recoverMethod_a_134621(var3.getDbm());
               var1.recoverField_b = recoverMethod_a_256a6b9(var4.getCid(), false);
               var1.recoverField_c = recoverMethod_b_134621(var4.getLac());
               String var5 = var4.getMccString();
               String var6 = var4.getMncString();
               recoverMethod_a_8ef12f66(var1, var5, var6);
               var1.recoverField_h = "3G";
               var1.recoverField_i = "tdscdma";
               var1.recoverField_g = recoverMethod_a_ed5678a7(var4.getOperatorAlphaLong());
               var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
            }

            if (var1.recoverField_f == null) {
               var1.recoverField_f = "";
            }
         } catch (Exception var7) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("CellUtils", "Process cell info failed: " + var7.getMessage());
            return null;
         }

         if (TextUtils.isEmpty(var1.recoverField_f)) {
            var1.recoverField_f = recoverMethod_a_ec2edeab(var1.recoverField_d, var1.recoverField_e);
         }

         if (var1.recoverField_d == 0 && var1.recoverField_e == 0 && var1.recoverField_c == 0 && var1.recoverField_b == 0L) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("CellUtils", "Invalid MCC/MNC, cannot generate operator code: " + var1.toString());
            return null;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("CellUtils", "processSelectedCell: " + var1.toString());
            return var1;
         }
      } else {
         return null;
      }
   }
}
