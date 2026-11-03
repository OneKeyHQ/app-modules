package cn.jpush.android.v;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.helper.Logger;

public class RecoverType2e4d11ba extends RecoverType2e4d11bd {
   private static final String[] recoverField_a = new String[]{
      "_id", "ln_id", "ln_count", "ln_remove", "ln_type", "ln_extra", "ln_trigger_time", "ln_add_time"
   };
   private static volatile RecoverType2e4d11ba recoverField_b;
   private static final Object recoverField_c = new Object();

   private RecoverType2e4d11ba(Context var1) {
      super(var1, "jpush_local_notification.db", null, 1);
   }

   public static RecoverType2e4d11ba recoverMethod_a_4af8d378(Context var0) {
      if (recoverField_b == null) {
         synchronized (recoverField_c) {
            if (recoverField_b == null) {
               recoverField_b = new RecoverType2e4d11ba(var0.getApplicationContext());
            }
         }
      }

      return recoverField_b;
   }

   @Override
   public void onCreate(SQLiteDatabase var1) {
      try {
         var1.execSQL(
            "CREATE TABLE t_localnotification (_id INTEGER PRIMARY KEY AUTOINCREMENT ,ln_id long not null,ln_count integer not null,ln_remove integer not null,ln_type integer not null,ln_extra text ,ln_trigger_time long ,ln_add_time long );"
         );
      } catch (Exception var3) {
         Logger.recoverMethod_v_e925fa09("LocalNotificationDb", "表已经存在");
      }
   }

   @Override
   public void onUpgrade(SQLiteDatabase var1, int var2, int var3) {
      var1.execSQL("DROP TABLE IF EXISTS t_localnotification");
      this.onCreate(var1);
   }

   public static RecoverType2e4d11bb recoverMethod_a_cfbf928(Cursor var0) {
      if (null != var0 && var0.getCount() != 0) {
         try {
            RecoverType2e4d11bb var1 = new RecoverType2e4d11bb();
            var1.recoverMethod_a_1349ef(var0.getLong(1));
            var1.recoverMethod_a_13462e(var0.getInt(2));
            var1.recoverMethod_b_13462e(var0.getInt(3));
            var1.recoverMethod_c_13462e(var0.getInt(4));
            var1.recoverMethod_a_aad3b1ff(RecoverType9b4c5ae7.recoverMethod_c_e96ea081(var0.getString(5)));
            var1.recoverMethod_c_1349ef(var0.getLong(6));
            var1.recoverMethod_b_1349ef(var0.getLong(7));
            Logger.recoverMethod_i_e925fa09("LocalNotificationDb", var1.toString());
            return var1;
         } catch (Exception var2) {
            var2.getStackTrace();
            Logger.recoverMethod_w_e925fa09("LocalNotificationDb", "initLocalNotificationDBData exception:" + var2.getMessage());
            return null;
         }
      } else {
         Logger.recoverMethod_d_e925fa09("LocalNotificationDb", "cursor is null");
         return null;
      }
   }

   public long recoverMethod_a_e0d72012(long var1, int var3, int var4, int var5, String var6, long var7, long var9) {
      long var11 = 0L;
      if (this.recoverMethod_a_138603(true)) {
         try {
            ContentValues var13 = new ContentValues();
            var13.put("ln_id", var1);
            var13.put("ln_count", var3);
            var13.put("ln_remove", var4);
            var13.put("ln_type", var5);
            var13.put("ln_extra", RecoverType9b4c5ae7.recoverMethod_c_e96ea081(var6));
            var13.put("ln_trigger_time", var7);
            var13.put("ln_add_time", var9);
            var11 = this.getWritableDatabase().insert("t_localnotification", null, var13);
         } catch (Exception var17) {
         } finally {
            this.recoverMethod_b_1385ff(true);
         }
      }

      return var11;
   }

   public long recoverMethod_b_e0d72012(long var1, int var3, int var4, int var5, String var6, long var7, long var9) {
      long var11 = 0L;
      if (this.recoverMethod_a_138603(true)) {
         try {
            String var13 = "ln_id=" + var1;
            ContentValues var14 = new ContentValues();
            var14.put("ln_id", var1);
            var14.put("ln_count", var3);
            var14.put("ln_remove", var4);
            var14.put("ln_type", var5);
            var14.put("ln_extra", RecoverType9b4c5ae7.recoverMethod_c_e96ea081(var6));
            var14.put("ln_trigger_time", var7);
            var14.put("ln_add_time", var9);
            var11 = this.getWritableDatabase().update("t_localnotification", var14, var13, null);
         } catch (Exception var18) {
         } finally {
            this.recoverMethod_b_1385ff(true);
         }
      }

      return var11;
   }

   public Cursor recoverMethod_a_479cc43c(long var1, long var3) {
      long var5 = var1 + var3;
      String var7 = "ln_count>0 and ln_trigger_time<" + var5 + " and " + "ln_trigger_time" + ">" + var1;

      try {
         return this.getReadableDatabase().query(true, "t_localnotification", recoverField_a, var7, null, null, null, null, null);
      } catch (Exception var9) {
         Logger.recoverMethod_w_e925fa09("LocalNotificationDb", "getRtcDatas exception:" + var9.getMessage());
         return null;
      }
   }

   public Cursor recoverMethod_a_56cbbcdd(int var1, long var2) {
      String var4 = "ln_count=" + var1 + " and " + "ln_trigger_time" + "<" + var2;

      try {
         return this.getReadableDatabase().query(true, "t_localnotification", recoverField_a, var4, null, null, null, null, null);
      } catch (Exception var6) {
         Logger.recoverMethod_w_e925fa09("LocalNotificationDb", "getOutDatas exception:" + var6.getMessage());
         return null;
      }
   }

   public RecoverType2e4d11bb recoverMethod_a_c9571536(long var1, int var3) throws java.lang.Exception {
      if (this.recoverMethod_a_138603(false)) {
         Cursor var4 = null;

         try {
            String var5 = "ln_id=" + var1 + " and " + "ln_type" + "=" + var3;
            var4 = this.getReadableDatabase().query(true, "t_localnotification", recoverField_a, var5, null, null, null, null, null);
            if (var4 != null) {
               var4.moveToFirst();
            }

            return recoverMethod_a_cfbf928(var4);
         } finally {
            if (var4 != null) {
               var4.close();
            }

            this.recoverMethod_b_1385ff(false);
         }
      } else {
         throw new Exception("open database failed");
      }
   }

   public int recoverMethod_a_1349e2(long var1) {
      if (this.recoverMethod_a_138603(true)) {
         try {
            String var3 = "ln_id=" + var1;
            return this.getWritableDatabase().delete("t_localnotification", var3, null);
         } catch (Exception var8) {
         } finally {
            this.recoverMethod_b_1385ff(true);
         }
      }

      return 0;
   }

   public int[] recoverMethod_a_12d40f() {
      String var1 = "1";
      int[] var2 = null;
      boolean var3 = false;
      if (this.recoverMethod_a_138603(false)) {
         Cursor var4 = null;

         try {
            String[] var5 = new String[]{"ln_id"};
            var4 = this.getReadableDatabase().query(true, "t_localnotification", var5, var1, null, null, null, null, null);
            if (var4 != null && var4.getCount() > 0) {
               var2 = new int[var4.getCount()];
               var4.moveToFirst();
               int var6 = 0;

               do {
                  var2[var6] = var4.getInt(0);
                  var6++;
               } while (var4.moveToNext());
            }

            if (this.recoverMethod_a_138603(true)) {
               var3 = true;
               String var12 = "delete from t_localnotification";
               this.getWritableDatabase().execSQL(var12);
            }
         } catch (Exception var10) {
         } finally {
            if (var4 != null) {
               var4.close();
            }

            this.recoverMethod_b_1385ff(false);
            if (var3) {
               this.recoverMethod_b_1385ff(true);
            }
         }
      }

      return var2;
   }
}
