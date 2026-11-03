package cn.jpush.android.data;

import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class JPushLocalNotification implements Serializable {
   private static final long serialVersionUID = 1472982106750878137L;
   private int recoverField_a = 1;
   private String recoverField_b = "";
   private String recoverField_c = "00";
   private String recoverField_d = "00";
   private long recoverField_e = 0L;
   private String recoverField_f;
   private String recoverField_g;
   private String recoverField_h;
   private long recoverField_i;
   private long recoverField_j = 1L;
   private int recoverField_k = 1;
   private String recoverField_l = "";
   private String recoverField_m = "";
   private int recoverField_n = 0;
   private String recoverField_o = "";

   public String toJSON() {
      JSONObject var1 = new JSONObject();

      try {
         JSONObject var2 = new JSONObject();
         if (!TextUtils.isEmpty(this.recoverField_h)) {
            JSONObject var3 = new JSONObject(this.recoverField_h);
            var2.put("n_extras", var3);
         }

         var2.put("n_priority", this.recoverField_n);
         this.recoverMethod_a_4e510755("n_category", this.recoverField_o, var2);
         this.recoverMethod_a_4e510755("n_content", this.recoverField_f, var2);
         this.recoverMethod_a_4e510755("n_title", this.recoverField_g, var2);
         this.recoverMethod_a_4e510755("n_content", this.recoverField_f, var2);
         var2.put("ad_t", 0);
         var1.put("m_content", var2);
         this.recoverMethod_a_4e510755("msg_id", "" + this.recoverField_j, var1);
         this.recoverMethod_a_4e510755("content_type", this.recoverField_m, var1);
         this.recoverMethod_a_4e510755("override_msg_id", this.recoverField_l, var1);
         var1.put("n_only", this.recoverField_k);
         var1.put("n_builder_id", this.recoverField_i);
         var1.put("show_type", 3);
         var1.put("notificaion_type", 1);
      } catch (JSONException var4) {
      }

      return var1.toString();
   }

   private void recoverMethod_a_4e510755(String var1, String var2, JSONObject var3) throws org.json.JSONException {
      if (!TextUtils.isEmpty(var2)) {
         var3.put(var1, var2);
      }
   }

   public void setNotificationId(long var1) {
      this.recoverField_j = (int)var1;
   }

   public long getNotificationId() {
      return this.recoverField_j;
   }

   public void setBroadcastTime(long var1) {
      this.recoverField_e = var1;
   }

   public void setBroadcastTime(Date var1) {
      this.recoverField_e = var1.getTime();
   }

   public void setBroadcastTime(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var1 >= 0 && var2 >= 1 && var2 <= 12 && var3 >= 1 && var3 <= 31 && var4 >= 0 && var4 <= 23 && var5 >= 0 && var5 <= 59 && var6 >= 0 && var6 <= 59) {
         Calendar var7 = Calendar.getInstance();
         var7.set(var1, var2 - 1, var3, var4, var5, var6);
         Date var8 = var7.getTime();
         long var9 = System.currentTimeMillis();
         if (var8.getTime() < var9) {
            this.recoverField_e = var9;
         } else {
            this.recoverField_e = var8.getTime();
         }
      } else {
         Logger.recoverMethod_ee_e925fa09("JPushLocalNotification", "Set time fail! Please check your args!");
      }
   }

   public long getBroadcastTime() {
      return this.recoverField_e;
   }

   public void setExtras(String var1) {
      this.recoverField_h = var1;
   }

   public String getExtras() {
      return this.recoverField_h;
   }

   public String getTitle() {
      return this.recoverField_g;
   }

   public void setTitle(String var1) {
      this.recoverField_g = var1;
   }

   public String getContent() {
      return this.recoverField_f;
   }

   public void setContent(String var1) {
      this.recoverField_f = var1;
   }

   public long getBuilderId() {
      return this.recoverField_i;
   }

   public void setBuilderId(long var1) {
      this.recoverField_i = var1;
   }

   public int getPriority() {
      return this.recoverField_n;
   }

   public void setPriority(int var1) {
      this.recoverField_n = var1;
   }

   public String getCategory() {
      return this.recoverField_o;
   }

   public void setCategory(String var1) {
      this.recoverField_o = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         JPushLocalNotification var2 = (JPushLocalNotification)var1;
         return this.recoverField_j == var2.recoverField_j;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return (int)(this.recoverField_j ^ this.recoverField_j >>> 32);
   }
}
