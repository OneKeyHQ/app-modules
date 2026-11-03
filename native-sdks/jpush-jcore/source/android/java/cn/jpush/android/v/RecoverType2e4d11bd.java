package cn.jpush.android.v;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build.VERSION;

public abstract class RecoverType2e4d11bd extends SQLiteOpenHelper {
   private volatile int recoverField_a = 0;
   private volatile int recoverField_b = 0;
   private volatile SQLiteDatabase recoverField_c;
   private volatile SQLiteDatabase recoverField_d;
   private final Object recoverField_e = new Object();
   private final Object recoverField_f = new Object();
   private final Context recoverField_g;
   private final String recoverField_h;
   private final int recoverField_i;
   private final CursorFactory recoverField_j;

   public RecoverType2e4d11bd(Context var1, String var2, CursorFactory var3, int var4) {
      super(var1, var2, var3, var4);
      this.recoverField_g = var1;
      this.recoverField_h = var2;
      this.recoverField_i = var4;
      this.recoverField_j = var3;
   }

   public boolean recoverMethod_a_138603(boolean var1) {
      try {
         if (var1) {
            synchronized (this.recoverField_e) {
               this.getWritableDatabase();
               this.recoverField_b++;
            }

            return true;
         } else {
            synchronized (this.recoverField_f) {
               this.getReadableDatabase();
               this.recoverField_a++;
            }

            return true;
         }
      } catch (Exception var7) {
         return false;
      }
   }

   @Override
   public SQLiteDatabase getReadableDatabase() {
      if (this.recoverField_c == null || !this.recoverField_c.isOpen()) {
         synchronized (this.recoverField_f) {
            if (this.recoverField_c == null || !this.recoverField_c.isOpen()) {
               try {
                  this.getWritableDatabase();
               } catch (SQLiteException var4) {
               }

               String var2 = this.recoverField_g.getDatabasePath(this.recoverField_h).getPath();
               this.recoverField_c = SQLiteDatabase.openDatabase(var2, this.recoverField_j, 1);
               if (this.recoverField_c.getVersion() != this.recoverField_i) {
                  throw new SQLiteException(
                     "Can't upgrade read-only database from version " + this.recoverField_c.getVersion() + " to " + this.recoverField_i + ": " + var2
                  );
               }

               this.recoverField_a = 0;
               this.onOpen(this.recoverField_c);
            }
         }
      }

      return this.recoverField_c;
   }

   @Override
   public SQLiteDatabase getWritableDatabase() {
      if (this.recoverField_d == null || !this.recoverField_d.isOpen()) {
         synchronized (this.recoverField_e) {
            if (this.recoverField_d == null || !this.recoverField_d.isOpen()) {
               this.recoverField_b = 0;
               this.recoverField_d = super.getWritableDatabase();
               if (VERSION.SDK_INT >= 11) {
                  this.recoverField_d.enableWriteAheadLogging();
               }
            }
         }
      }

      return this.recoverField_d;
   }

   @Deprecated
   @Override
   public void close() {
   }

   public void recoverMethod_b_1385ff(boolean var1) {
      if (var1) {
         synchronized (this.recoverField_e) {
            boolean var3 = this.recoverField_d == null || !this.recoverField_d.isOpen() || --this.recoverField_b <= 0;
            if (var3) {
               this.recoverField_b = 0;
               if (this.recoverField_d != null) {
                  this.recoverField_d.close();
               }

               this.recoverField_d = null;
            }
         }
      } else {
         synchronized (this.recoverField_f) {
            boolean var9 = this.recoverField_c == null || !this.recoverField_c.isOpen() || --this.recoverField_a <= 0;
            if (var9) {
               this.recoverField_a = 0;
               if (this.recoverField_c != null) {
                  this.recoverField_c.close();
               }

               this.recoverField_c = null;
            }
         }
      }
   }
}
