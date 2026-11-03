package cn.jiguang.t;

import android.content.Context;
import android.net.Uri;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6c921 {
   private Context recoverField_a;
   private Uri recoverField_b;

   public RecoverType5a6c921(Context var1) {
      this.recoverField_a = var1;
      String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("k97muQEI3n3mlJTZdD9q3lC6tvCVkeY7wPOrf0wd0l4zQtrGyn5uq0enBKOySISr");
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NubiaOpenIDHelper", "url: " + var2);
      this.recoverField_b = Uri.parse(var2);
   }

   public String recoverMethod_a_73cf11cb() {
      try {
         return RecoverType5a6c920.recoverMethod_a_5e86c76c(this.recoverField_a, this.recoverField_b);
      } catch (Exception var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NubiaOpenIDHelper", "get Ids-oa error: " + var2.getMessage());
         return "";
      }
   }
}
