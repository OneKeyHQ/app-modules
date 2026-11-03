package cn.jiguang.w;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6d463 {
   private Context recoverField_a;

   public RecoverType5a6d463(Context var1) {
      this.recoverField_a = var1;
   }

   public String recoverMethod_a_dd3b7328(int var1, String var2) {
      String var4 = null;

      try {
         Uri var3;
         switch (var1) {
            case 0:
               String var5 = RecoverType5a68d10.recoverMethod_c_e96ea081(
                  "HDjJBOCwNx0aZi+Z7p83oR6X29bt13JSzReihhERik1zTqeaHqknpahXd0Fww8QTTjy7MHX7/1f2JKNuxQ9omA=="
               );
               var3 = Uri.parse(var5);
               break;
            case 1:
               String var6 = RecoverType5a68d10.recoverMethod_c_e96ea081(
                  "HDjJBOCwNx0aZi+Z7p83oR6X29bt13JSzReihhERik1mRRRjMzvkebo9eb3zqoVhCi2GjcOItUY/+C68T3lxGg=="
               );
               var3 = Uri.parse(var6 + var2);
               break;
            case 2:
               String var7 = RecoverType5a68d10.recoverMethod_c_e96ea081(
                  "HDjJBOCwNx0aZi+Z7p83oR6X29bt13JSzReihhERik1FXRr9EgPkoDQnyVgIu2tARnCHNUAAn6DU1ZkzxwPHKA=="
               );
               var3 = Uri.parse(var7 + var2);
               break;
            default:
               var3 = null;
         }

         Cursor var9 = this.recoverField_a.getContentResolver().query(var3, null, null, null, null);
         if (var9 != null) {
            if (var9.moveToNext()) {
               var4 = var9.getString(var9.getColumnIndex("value"));
            }

            var9.close();
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("VivoDB", "return cursor is null,return");
         }
      } catch (Exception var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("VivoDB", "get id error: " + var8.getMessage());
      }

      return var4;
   }
}
