package cn.jpush.android.d;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae6;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.x.RecoverType2e4d193b;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4cce2a implements Serializable {
   private static final long serialVersionUID = 8407025843324043625L;
   public int recoverField_a = -1;
   public String recoverField_b;
   public String recoverField_c;
   public String recoverField_d;
   public int recoverField_e;
   public int recoverField_f;
   public String recoverField_g;
   public String recoverField_h;
   public String recoverField_i;
   public int recoverField_j;
   public boolean recoverField_k;
   public int recoverField_l;
   public int recoverField_m = 0;
   public String recoverField_n;
   public String recoverField_o;
   public String recoverField_p;
   public String recoverField_q;
   public int recoverField_r;
   public boolean recoverField_s;
   public int recoverField_t;
   public String recoverField_u;
   public String recoverField_v;
   public JSONArray recoverField_w;
   public String recoverField_x;
   public String recoverField_y;
   public int recoverField_z;
   public String recoverField_A;
   public String recoverField_B;
   public String recoverField_C;
   public int recoverField_D;
   public int recoverField_E;
   public String recoverField_F;
   public String recoverField_G;
   public int recoverField_H = -1;
   public String recoverField_I = "";
   public String recoverField_J;
   public String recoverField_K;
   public String recoverField_L;
   public String recoverField_M;
   public String recoverField_N;
   public String recoverField_O;
   public int recoverField_P;
   public String recoverField_Q;
   public String recoverField_R;
   public int recoverField_S = 0;
   public int recoverField_T;
   public String recoverField_U;
   public String recoverField_V;
   public int recoverField_W;
   public boolean recoverField_X = false;
   public boolean recoverField_Y = false;
   public String recoverField_Z;
   public String recoverField_aa;
   public String recoverField_ab;
   public int recoverField_ac;
   public int recoverField_ad = 0;
   public int recoverField_ae;
   public ArrayList recoverField_af = new ArrayList();
   public String recoverField_ag = "";
   public String recoverField_ah = "";
   public byte recoverField_ai = 0;
   public Integer recoverField_aj;
   public String recoverField_ak;
   public long recoverField_al;
   public String recoverField_am;
   public boolean recoverField_an;
   public boolean recoverField_ao;
   public long recoverField_ap;
   public double recoverField_aq = 200.0;
   public double recoverField_ar = 200.0;
   public String recoverField_as;
   public String recoverField_at;
   public String recoverField_au;
   public String recoverField_av;
   public String recoverField_aw;
   public String recoverField_ax;
   public int recoverField_ay;
   public int recoverField_az;
   public String recoverField_aA = "";
   public String recoverField_aB = "";
   public String recoverField_aC = "";
   public String recoverField_aD = "";
   public String recoverField_aE = "";
   public int recoverField_aF = 1;
   public int recoverField_aG = 2;
   public int recoverField_aH = 0;
   public int recoverField_aI = 0;
   public int recoverField_aJ = 0;
   public int recoverField_aK = 0;
   public int recoverField_aL = 1;
   public int recoverField_aM = 0;
   public String recoverField_aN = "";
   public int recoverField_aO = 0;
   public long recoverField_aP = 0L;
   public int recoverField_aQ = 0;
   public int recoverField_aR = 0;
   public int recoverField_aS = 0;
   public int recoverField_aT = 1;
   public int recoverField_aU = 1;
   public String recoverField_aV = "#FFFFFFFF";
   public String recoverField_aW = "#FF000000";
   public String recoverField_aX = "#FF000000";
   public int recoverField_aY = 14;
   public int recoverField_aZ = 14;
   public int recoverField_ba = 9;
   public int recoverField_bb = 5;
   public float recoverField_bc = 1.0F;
   public float recoverField_bd = 1.0F;
   public String recoverField_be = "";
   public int recoverField_bf = 2;
   public int recoverField_bg = 2;
   public String recoverField_bh;
   public String recoverField_bi = "";
   public String recoverField_bj = "";
   public float recoverField_bk = 0.6F;
   public String recoverField_bl = "";
   public String recoverField_bm = "";
   public ArrayList recoverField_bn = new ArrayList();
   public int recoverField_bo = 0;
   public int recoverField_bp = 180;
   public int recoverField_bq = 0;
   public int recoverField_br = 0;
   public String recoverField_bs = "";
   public int recoverField_bt;
   public int recoverField_bu;
   public boolean recoverField_bv;
   public boolean recoverField_bw;
   public String recoverField_bx;
   public String recoverField_by;
   public String recoverField_bz = "";
   public String recoverField_bA;
   public String recoverField_bB = "";
   public ArrayList recoverField_bC = new ArrayList();
   public int recoverField_bD;
   public int recoverField_bE;
   public int recoverField_bF;
   public int recoverField_bG = 0;
   public int recoverField_bH = 0;
   public float recoverField_bI = -1.0F;
   public String recoverField_bJ;
   public int recoverField_bK = 1;
   public int recoverField_bL = 1;
   public int recoverField_bM;
   public int recoverField_bN;
   public long recoverField_bO;
   public long recoverField_bP;
   public long recoverField_bQ;
   public long recoverField_bR;
   public String recoverField_bS;
   public String recoverField_bT;
   public String recoverField_bU;
   public JSONObject recoverField_bV;
   public JSONObject recoverField_bW;
   public JSONArray recoverField_bX;
   private static HashMap recoverField_bY = new HashMap();
   private static Map recoverField_bZ = new HashMap();
   private static HashMap recoverField_ca = new HashMap();

   public void recoverMethod_a_aad3b1ff(String var1) {
      this.recoverField_bU = var1;
   }

   public static RecoverType2e4cce2a recoverMethod_a_fbe526a1(RecoverType2e4cce2a var0) {
      try {
         JSONObject var1 = new JSONObject(var0.recoverField_i);
         if (null == var1) {
            return null;
         }

         int var2 = var1.getInt("type");
         if (!recoverMethod_a_134632(var2)) {
            Logger.recoverMethod_ke_e925fa09("PushEntity", "parseInApp not support type=" + var2);
            return null;
         }

         if (var1.has("target")) {
            JSONArray var3 = var1.optJSONArray("target");
            if (var3.length() > 0) {
               var0.recoverField_bS = var3.optString(0);
            }
         }

         var0.recoverField_bO = System.currentTimeMillis();
         var0.recoverField_bQ = var1.getInt("delay");
         var0.recoverField_bP = Long.parseLong(var1.getString("expired"));
         var0.recoverField_bR = var1.getInt("interval");
         var0.recoverField_bu = recoverMethod_b_134621(var2);
         var0.recoverField_bt = var2;
         var0.recoverField_q = var1.optString("extras");
         var0.recoverField_w = var1.optJSONArray("target_event");
         if (var1.has("banner")) {
            JSONObject var7 = var1.getJSONObject("banner");
            if (10 == var2) {
               var0.recoverField_bV = var7.getJSONObject("title");
               var0.recoverField_bW = var7.getJSONObject("content");
               var0.recoverField_aD = var0.recoverField_bV.optString("text");
               var0.recoverField_aE = var0.recoverField_bW.optString("text");
            }

            if (11 == var2) {
               String var4 = var1.getString("image");
               if (TextUtils.isEmpty(var4)) {
                  Logger.recoverMethod_ke_e925fa09("PushEntity", "parseInApp image value is null");
                  return null;
               }

               var0.recoverField_bT = var4;
            }

            if (var7 != null && var7.has("duration")) {
               var0.recoverField_bb = var7.getInt("duration") / 1000;
            }

            if (var7 != null && var7.has("location")) {
               var0.recoverField_aH = var7.getInt("location");
            }
         }

         if (var1.has("full")) {
            String var8 = var1.getString("image");
            if (TextUtils.isEmpty(var8)) {
               Logger.recoverMethod_ke_e925fa09("PushEntity", "parseInApp image value is null");
               return null;
            }

            var0.recoverField_bT = var8;
            JSONObject var11 = var1.getJSONObject("full");
            if (var11 != null && var11.has("duration")) {
               var0.recoverField_bb = var11.getInt("duration") / 1000;
            }
         }

         if (var1.has("interstitial")) {
            if (21 == var2) {
               String var9 = var1.getString("image");
               if (TextUtils.isEmpty(var9)) {
                  Logger.recoverMethod_ke_e925fa09("PushEntity", "parseInApp image value is null");
                  return null;
               }

               var0.recoverField_bT = var9;
            }

            JSONObject var10 = var1.optJSONObject("interstitial");
            JSONObject var12 = var10.optJSONObject("title");
            if (var12 != null) {
               var0.recoverField_aD = var12.optString("text");
            }

            JSONObject var5 = var10.optJSONObject("content");
            if (var5 != null) {
               var0.recoverField_aE = var5.optString("text");
            }
         }

         return var0;
      } catch (Throwable var6) {
         Logger.recoverMethod_ke_e925fa09("PushEntity", "parseInApp Throwable=" + var6);
         return null;
      }
   }

   private static boolean recoverMethod_a_134632(int var0) {
      return 10 == var0 || 11 == var0 || 20 == var0 || 21 == var0 || 31 == var0 || 30 == var0;
   }

   private static int recoverMethod_b_134621(int var0) {
      String var1 = String.valueOf(var0);
      if (var1.startsWith("1")) {
         return 2;
      } else if (var1.startsWith("2")) {
         return 3;
      } else {
         return var1.startsWith("3") ? 4 : -1;
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      try {
         JSONObject var2 = new JSONObject(this.recoverField_i);
         JSONObject var3 = var2.optJSONObject("m_content");
         int var4 = var3.optInt("ad_t", 0);
         this.recoverField_r = var4;
         if (var4 != 0) {
            return;
         }

         this.recoverField_s = var3.optInt("full_screen", 0) >= 1;
         this.recoverField_t = var3.optInt("n_flag", 1);
         this.recoverField_v = var3.optString("n_title", "");
         this.recoverField_u = var3.optString("n_bg_color", "");
         this.recoverField_x = var3.optString("n_app_name", "");
         this.recoverField_y = var3.optString("n_content", "");
         this.recoverField_z = var3.optInt("n_style", 0);
         this.recoverField_A = var3.optString("n_big_text", "");
         this.recoverField_B = var3.optString("n_big_pic_path", "");
         this.recoverField_C = var3.optString("n_inbox", "");
         this.recoverField_G = var3.optString("n_extras", "");
         this.recoverField_D = RecoverType2e4cce2a.Memberce51f068.recoverMethod_a_be7fc15c(var3);
         this.recoverField_E = RecoverType2e4cce2a.Memberce51f068.recoverMethod_c_be7fc15c(var3);
         this.recoverField_F = var3.optString("n_category", "");
         this.recoverField_H = var3.optInt("n_alert_type", -1);
         this.recoverField_I = var3.optString("n_channel_id");
         this.recoverField_J = var3.optString("n_display_foreground");
         this.recoverField_L = var3.optString("n_small_icon", "");
         this.recoverField_M = var3.optString("n_large_icon", "");
         this.recoverField_K = var3.optString("n_source", "");
         this.recoverField_N = var3.optString("n_small_icon_uri", "");
         this.recoverField_aB = var3.optString("n_intent");
         JSONObject var5 = var3.optJSONObject("n_intent");
         if (var5 != null) {
            this.recoverField_O = var5.optString("n_url", "");
            this.recoverField_P = var5.optInt("n_fail_handle_type", 0);
            this.recoverField_Q = var5.optString("n_fail_handle_url", "");
            this.recoverField_R = var5.optString("n_package_name", "");
            this.recoverField_T = var5.optInt("n_builder_id", 0);
            this.recoverField_U = var5.optString("wx_app_id", "");
            this.recoverField_V = var5.optString("mini_originid", "");
            this.recoverField_W = var5.optInt("mini_type", 0);
         }

         if (TextUtils.isEmpty(this.recoverField_v)) {
            Logger.recoverMethod_dd_e925fa09("PushEntity", "Not found notificaiton title for developer mode. Use the application name.");
            this.recoverField_v = RecoverType9b4c5ae5.recoverMethod_b_e501eb0d(var1);
         }

         JSONObject var6 = var3.optJSONObject("ad_content");
         boolean var7 = var3.isNull("ad_content");
         if (!var7 && var6 != null) {
            this.recoverMethod_a_be7fc169(var6);
         }

         if (var3.has("n_badge_add_num")) {
            this.recoverField_aj = var3.optInt("n_badge_add_num");
         } else {
            this.recoverField_aj = null;
         }

         this.recoverField_au = var3.optString("n_sound");
         this.recoverField_av = var3.optString("n_show_begin_time");
         this.recoverField_aw = var3.optString("n_show_end_time");
         this.recoverField_w = var3.optJSONArray("n_target_event");
         this.recoverField_bX = var2.optJSONArray("custom_buttons");
      } catch (Throwable var8) {
         Logger.recoverMethod_ww_e925fa09("PushEntity", "parse notification failed:" + var8.getMessage());
      }
   }

   public static RecoverType2e4cce2a recoverMethod_a_f0b11163(Context var0, RecoverType2e4cce2a var1) {
      try {
         JSONObject var2 = new JSONObject(var1.recoverField_i);
         JSONObject var3 = var2.optJSONObject("inapp");
         if (null == var3) {
            return null;
         }

         var1.recoverMethod_a_f0554df3(var0);
         var1.recoverField_aD = var1.recoverField_v;
         var1.recoverField_aE = var1.recoverField_y;
         var1.recoverField_aR = var3.optInt("inapp_delay_display_time", 10) * 1000;
         var1.recoverField_bj = var3.optString("inapp_h5_tpl_url", "");
         var1.recoverField_bu = 1;
         var1.recoverField_bv = var3.optInt("inapp_display_direct", 0) == 1;
         var1.recoverField_aH = var3.optInt("inapp_show_pos", 0);
         var1.recoverField_bb = var3.optInt("inapp_show_threshold_time", 5);
         String var4 = var1.recoverField_M;
         if (!TextUtils.isEmpty(var4)) {
            var1.recoverField_bn.add(var4);
         }

         var1.recoverField_bO = System.currentTimeMillis();
         var1.recoverField_bP = RecoverType9b4c5ae6.recoverMethod_b_aad3b1f3(var3.optString("inapp_end_time", ""));
         var1.recoverField_ay = var3.optInt("inapp_max_cache_numb", 10);
         var1.recoverField_aQ = var3.optInt("inapp_msg_to_user", 1);
         var1.recoverField_bt = 40;
         JSONObject var5 = new JSONObject();
         var5.put("in_title", var1.recoverField_aD);
         var5.put("in_body_content", var1.recoverField_aE);
         var5.put("in_pic_path1", var4);
         var1.recoverField_bl = var5.toString();
         return var1;
      } catch (Throwable var6) {
         return null;
      }
   }

   private void recoverMethod_a_be7fc169(JSONObject var1) {
      this.recoverField_Y = true;
      this.recoverField_Z = var1.optString("e_url", "").trim();
      this.recoverField_aa = var1.optString("e_title", "").trim();
      if (!TextUtils.isEmpty(this.recoverField_Z) && !RecoverType9b4c5ae5.recoverMethod_c_aad3b203(this.recoverField_Z)) {
         this.recoverField_Z = "https://" + this.recoverField_Z;
         Logger.recoverMethod_i_e925fa09("PushEntity", "Add http to non-prefix url: " + this.recoverField_Z);
      }

      this.recoverField_ad = var1.optInt("e_rich_type", 0);
      this.recoverField_ac = var1.optInt("e_jump_mode", 0);
      this.recoverField_ae = var1.optInt("e_show", 0);
      if (3 == this.recoverField_ad || 2 == this.recoverField_ad || 1 == this.recoverField_ad) {
         this.recoverField_af = RecoverType9b4c5af2.recoverMethod_a_48eba5e9(var1.optJSONArray("e_eres"));
      }

      this.recoverField_ag = var1.optString("from_num", "");
      this.recoverField_ah = var1.optString("to_num", "");
   }

   public static RecoverType2e4cce2a recoverMethod_a_39f271f4(Context var0, String var1, String var2) {
      RecoverType2e4cce2a var3 = new RecoverType2e4cce2a();

      try {
         JSONObject var4 = new JSONObject(var1);
         var3.recoverField_d = var4.optString("_jmsgid_");
         if (var3.recoverField_d.isEmpty()) {
            var3.recoverField_d = var4.optString("msg_id");
         }

         var3.recoverField_g = recoverMethod_a_7841f161(var3.recoverField_d, var4);
         var3.recoverField_ai = (byte)var4.optInt("rom_type");
         int var9 = var4.optInt("show_type", -1);
         JSONObject var10 = var4.optJSONObject("m_content");
         if (var10 != null) {
            var3.recoverField_y = var10.optString("n_content");
            var3.recoverField_v = var10.optString("n_title");
            var3.recoverField_u = var10.optString("n_bg_color");
            var3.recoverField_G = var10.optString("n_extras");
            var3.recoverField_I = var10.optString("n_channel_id");
            var3.recoverField_J = var10.optString("n_display_foreground");
            var3.recoverField_H = var10.optInt("n_alert_type");
            var3.recoverField_D = RecoverType2e4cce2a.Memberce51f068.recoverMethod_a_be7fc15c(var10);
            var3.recoverField_E = RecoverType2e4cce2a.Memberce51f068.recoverMethod_c_be7fc15c(var10);
            var3.recoverField_J = var10.optString("n_display_foreground");
            var3.recoverField_au = var10.optString("n_sound");
            var3.recoverField_t = var10.optInt("n_flag", 1);
            var3.recoverField_w = var10.optJSONArray("n_target_event");
            var3.recoverField_bX = var10.optJSONArray("custom_buttons");
            JSONObject var7 = var10.optJSONObject("rich_content");
            if (var7 != null) {
               var3.recoverField_Y = true;
               var3.recoverMethod_a_be7fc169(var7);
               var3.recoverField_j = 3;
            } else {
               var3.recoverField_j = 4;
               var3.recoverField_ad = -1;
            }
         } else {
            var3.recoverField_y = var4.optString("n_content");
            var3.recoverField_v = var4.optString("n_title");
            var3.recoverField_u = var4.optString("n_bg_color");
            var3.recoverField_w = var4.optJSONArray("n_target_event");
            var3.recoverField_G = var4.optString("n_extras");
            var3.recoverField_I = var4.optString("n_channel_id");
            var3.recoverField_J = var4.optString("n_display_foreground");
            var3.recoverField_H = var4.optInt("n_alert_type");
            var3.recoverField_D = RecoverType2e4cce2a.Memberce51f068.recoverMethod_a_be7fc15c(var4);
            var3.recoverField_E = RecoverType2e4cce2a.Memberce51f068.recoverMethod_c_be7fc15c(var4);
            var3.recoverField_J = var4.optString("n_display_foreground");
            var3.recoverField_ai = (byte)var4.optInt("rom_type");
            var3.recoverField_h = var4.optString("override_msg_id");
            var3.recoverField_au = var4.optString("n_sound");
            var3.recoverField_bX = var4.optJSONArray("custom_buttons");
         }

         if (var9 != -1) {
            var3.recoverField_j = var9;
         }

         var3.recoverField_r = 0;
         var3.recoverField_s = true;
         var3.recoverField_b = var0.getPackageName();
         return var3;
      } catch (Throwable var8) {
         Logger.recoverMethod_e_e925fa09("PushEntity", "parseContent error:" + var8);
         String var5 = "NO MSGID";
         byte var6 = 0;
         if (!TextUtils.isEmpty(var3.recoverField_d)) {
            var5 = var3.recoverField_d;
            var6 = var3.recoverField_ai;
         }

         RecoverType8b455831.recoverMethod_a_2ef42797(var5, var2, var6, 996L, var0);
         return null;
      }
   }

   public RecoverType2e4cce27 recoverMethod_a_cd303c69() {
      return new RecoverType2e4cce27(this);
   }

   public static RecoverType2e4cce2a recoverMethod_a_e5f4ac2a(String var0, String var1, String var2, long var3) {
      RecoverType2e4cce2a var5 = null;

      try {
         JSONObject var6 = new JSONObject(var0);
         var5 = new RecoverType2e4cce2a();
         String var7 = var6.optString("msg_id", "");
         if (TextUtils.isEmpty(var7)) {
            var7 = var6.optString("ad_id", "");
         }

         if (TextUtils.isEmpty(var7)) {
            var7 = var6.optString("_jmsgid_", "");
         }

         if (TextUtils.isEmpty(var7)) {
            var7 = var3 + "";
         }

         Logger.recoverMethod_d_e925fa09("PushEntity", "preParseOriginalMsgMessage msgId = " + var7);
         var5.recoverField_d = var7;
         var5.recoverField_b = var1;
         var5.recoverField_c = var2;
         var5.recoverField_g = recoverMethod_a_7841f161(var5.recoverField_d, var6);
         String var8 = var6.optString("sequence_id", "");
         var5.recoverField_aA = var8;
         recoverMethod_b_e925fa09(var7, var8);
         boolean var9 = var6.optInt("n_only", 0) == 1;
         int var10 = 0;
         if (var9) {
            var10 = var6.optInt("n_builder_id", 0);
         }

         var5.recoverField_k = var9;
         var5.recoverField_l = var10;
         var5.recoverField_i = var0;
         var5.recoverField_j = var6.optInt("show_type", 3);
         var5.recoverField_ai = (byte)var6.optInt("rom_type");
         var5.recoverField_m = var6.optInt("notificaion_type", 0);
         var5.recoverField_h = var6.optString("override_msg_id", "");
         var5.recoverField_n = var6.optString("message", "");
         var5.recoverField_o = var6.optString("content_type", "");
         var5.recoverField_p = var6.optString("title", "");
         var5.recoverField_q = var6.optString("extras", "");
      } catch (Throwable var11) {
         Logger.recoverMethod_w_e925fa09("PushEntity", "processMessage failed:" + var11.getMessage());
      }

      return var5;
   }

   public NotificationMessage recoverMethod_b_4c6584f8() {
      NotificationMessage var1 = new NotificationMessage();
      var1.appkey = this.recoverField_c;
      var1.msgId = this.recoverField_d;
      var1.notificationAlertType = this.recoverField_H;
      var1.notificationBigPicPath = this.recoverField_B;
      var1.notificationBigText = this.recoverField_A;
      var1.notificationBuilderId = this.recoverField_l;
      var1.notificationCategory = this.recoverField_F;
      var1.notificationContent = this.recoverField_y;
      var1.notificationExtras = this.recoverField_G;
      var1.notificationId = this.recoverField_e;
      var1.notificationInbox = this.recoverField_C;
      var1.notificationLargeIcon = this.recoverField_M;
      var1.notificationPriority = this.recoverField_D;
      var1.notificationImportance = this.recoverField_E;
      var1.notificationSmallIcon = this.recoverField_L;
      var1.notificationStyle = this.recoverField_z;
      var1.notificationTitle = this.recoverField_v;
      var1.notificationTargetEvent = this.recoverField_w;
      var1.appId = this.recoverField_b;
      var1.notificationType = this.recoverField_m;
      var1.developerArg0 = this.recoverField_at;
      var1.notificationChannelId = this.recoverField_I;
      var1.isRichPush = this.recoverField_Y;
      var1.richType = this.recoverField_ad;
      var1._webPagePath = this.recoverField_ab;
      var1.showResourceList = this.recoverField_af;
      var1.platform = this.recoverField_ai;

      try {
         var1.deeplink = this.recoverField_O;
         var1.failedAction = this.recoverField_P;
         var1.failedLink = this.recoverField_Q;
         var1.targetPkgName = this.recoverField_R;
         var1.displayForeground = this.recoverField_J;
         var1.sspWxAppId = this.recoverField_U;
         var1.sspWmOriginId = this.recoverField_V;
         var1.sspWmType = this.recoverField_W;
         var1.isWmDeepLink = this.recoverField_X;
         var1.inAppMsgTitle = this.recoverField_aD;
         var1.inAppMsgContentBody = this.recoverField_aE;
         var1.inAppMsgType = this.recoverField_aF;
         var1.inAppMsgShowType = this.recoverField_aG;
         var1.inAppMsgShowPos = this.recoverField_aH;
         var1.inAppShowTarget = this.recoverField_bS;
         var1.inAppExtras = this.recoverField_q;
         var1.inAppClickAction = this.recoverField_bU;
         if (JPushConstants.SDK_VERSION_CODE >= 408) {
            var1.notificationNormalSmallIcon = this.recoverField_N;
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("PushEntity", "pushEntry toNotificationMessage error" + var4.getMessage());
      }

      try {
         if (JPushConstants.SDK_VERSION_CODE > 590) {
            var1.customButtons = this.recoverField_bX;
         }
      } catch (Throwable var3) {
      }

      return var1;
   }

   @Override
   public String toString() {
      return "PushEntity{appId='"
         + this.recoverField_b
         + '\''
         + ", senderId='"
         + this.recoverField_c
         + '\''
         + ", messageId='"
         + this.recoverField_d
         + '\''
         + ", jData='"
         + this.recoverField_g
         + '\''
         + ", notificationId="
         + this.recoverField_e
         + ", messageType="
         + this.recoverField_f
         + ", overrideMessageId='"
         + this.recoverField_h
         + '\''
         + ", originalMessage='"
         + this.recoverField_i
         + '\''
         + ", messageVersion="
         + this.recoverField_j
         + ", notificationOnly="
         + this.recoverField_k
         + ", notificationBuilderId="
         + this.recoverField_l
         + ", notificationType="
         + this.recoverField_m
         + ", message='"
         + this.recoverField_n
         + '\''
         + ", contentType='"
         + this.recoverField_o
         + '\''
         + ", title='"
         + this.recoverField_p
         + '\''
         + ", extras='"
         + this.recoverField_q
         + '\''
         + ", type="
         + this.recoverField_r
         + ", isFullScreen="
         + this.recoverField_s
         + ", notificationRemoveMode="
         + this.recoverField_t
         + ", notificationTitle='"
         + this.recoverField_v
         + '\''
         + ", notificationBgColor='"
         + this.recoverField_u
         + '\''
         + ", notificationAppName='"
         + this.recoverField_x
         + '\''
         + ", notificationContent='"
         + this.recoverField_y
         + '\''
         + ", notificationStyle="
         + this.recoverField_z
         + ", notificationBigText='"
         + this.recoverField_A
         + '\''
         + ", notificationBigPicPath='"
         + this.recoverField_B
         + '\''
         + ", notificationInbox='"
         + this.recoverField_C
         + '\''
         + ", notificationPriority="
         + this.recoverField_D
         + ", notificationImportance="
         + this.recoverField_E
         + ", notificationCategory='"
         + this.recoverField_F
         + '\''
         + ", notificationExtra='"
         + this.recoverField_G
         + '\''
         + ", notificationAlertType="
         + this.recoverField_H
         + ", notificationChannelId='"
         + this.recoverField_I
         + '\''
         + ", displayForeground='"
         + this.recoverField_J
         + '\''
         + ", source='"
         + this.recoverField_K
         + '\''
         + ", smallIcon='"
         + this.recoverField_L
         + '\''
         + ", largeIcon='"
         + this.recoverField_M
         + '\''
         + ", normalSmallIcon='"
         + this.recoverField_N
         + '\''
         + ", deeplink='"
         + this.recoverField_O
         + '\''
         + ", failedAction="
         + this.recoverField_P
         + ", failedLink='"
         + this.recoverField_Q
         + '\''
         + ", targetPkgName='"
         + this.recoverField_R
         + '\''
         + ", deeplinkBuilderId="
         + this.recoverField_T
         + ", isRichPush="
         + this.recoverField_Y
         + ", showUrl='"
         + this.recoverField_Z
         + '\''
         + ", showTitle='"
         + this.recoverField_aa
         + '\''
         + ", _webPagePath='"
         + this.recoverField_ab
         + '\''
         + ", jumpMode="
         + this.recoverField_ac
         + ", richType="
         + this.recoverField_ad
         + ", showMode="
         + this.recoverField_ae
         + ", showResourceList="
         + this.recoverField_af
         + ", fromNum='"
         + this.recoverField_ag
         + '\''
         + ", toNum='"
         + this.recoverField_ah
         + '\''
         + ", platform="
         + this.recoverField_ai
         + ", badgeAddNum="
         + this.recoverField_aj
         + ", geofenceid='"
         + this.recoverField_ak
         + '\''
         + ", radius="
         + this.recoverField_al
         + ", status='"
         + this.recoverField_am
         + '\''
         + ", repeat="
         + this.recoverField_an
         + ", hasShow="
         + this.recoverField_ao
         + ", expiration="
         + this.recoverField_ap
         + ", longitude="
         + this.recoverField_aq
         + ", latitude="
         + this.recoverField_ar
         + ", lastGeoStatus='"
         + this.recoverField_as
         + '\''
         + ", developerArg0='"
         + this.recoverField_at
         + '\''
         + ", sound='"
         + this.recoverField_au
         + '\''
         + ", showBeginTime='"
         + this.recoverField_av
         + '\''
         + ", showEndTime='"
         + this.recoverField_aw
         + '\''
         + ", inAppMsgContent='"
         + this.recoverField_aC
         + '\''
         + ", inAppIntent='"
         + this.recoverField_aB
         + ", inAppWebPageUrl = '"
         + this.recoverField_be
         + '\''
         + ", inAppProtocolVer = '"
         + this.recoverField_bf
         + '\''
         + ", inAppSSPGap = '"
         + this.recoverField_bp
         + '\''
         + ", inAppCheckIntent = '"
         + this.recoverField_bg
         + '\''
         + ", inAppTplContent = '"
         + this.recoverField_bh
         + '\''
         + ", inAppTplId = '"
         + this.recoverField_bi
         + '\''
         + ", inAppTplUrl = '"
         + this.recoverField_bj
         + '\''
         + ", inAppMarginY  = '"
         + this.recoverField_bk
         + '\''
         + ", inAppTplData = '"
         + this.recoverField_bl
         + '\''
         + ", inAppIntentActions = '"
         + this.recoverField_bm
         + '\''
         + ", inAppType= '"
         + this.recoverField_bt
         + '\''
         + ", notifyInAppEndTime= '"
         + this.recoverField_ax
         + '\''
         + ", notifyInAppEndTime= '"
         + this.recoverField_ay
         + '\''
         + ", notifyInAppMsgType= '"
         + this.recoverField_az
         + '\''
         + ", inAppPriority= '"
         + this.recoverField_bu
         + '\''
         + ", msgTime= '"
         + this.recoverField_bO
         + '\''
         + ", inAppIsDisplayDirect= '"
         + this.recoverField_bv
         + '\''
         + ", inAppFixed= '"
         + this.recoverField_bw
         + '\''
         + ", inAppBlackConf= '"
         + this.recoverField_bx
         + '\''
         + ", inAppAnimatorConf= '"
         + this.recoverField_by
         + '\''
         + ", inAppAdPosition= '"
         + this.recoverField_bz
         + '\''
         + ", inAppAdPosId= '"
         + this.recoverField_bB
         + '\''
         + ", inAppFragment= '"
         + this.recoverField_bA
         + '\''
         + ", inAppShowCoordinate= '"
         + this.recoverField_bC
         + '\''
         + ", inAppCoordinateType= '"
         + this.recoverField_bD
         + '\''
         + ", inAppWinWidth= '"
         + this.recoverField_bE
         + '\''
         + ", inAppWinHeight= '"
         + this.recoverField_bF
         + '\''
         + ", sequenceId= '"
         + this.recoverField_aA
         + '\''
         + ", swipeDismiss= '"
         + this.recoverField_bK
         + '\''
         + ", customButtons= '"
         + this.recoverField_bX
         + '\''
         + '}';
   }

   public static RecoverType2e4cce2a recoverMethod_b_365d807c(String var0) {
      try {
         JSONObject var1 = new JSONObject(var0);
         RecoverType2e4cce2a var2 = new RecoverType2e4cce2a();
         var2.recoverField_a = var1.optInt("msgType", -1);
         var2.recoverField_b = var1.optString("appId");
         var2.recoverField_c = var1.optString("senderId");
         var2.recoverField_d = var1.optString("messageId");
         var2.recoverField_g = recoverMethod_a_7841f161(var2.recoverField_d, var1);
         var2.recoverField_e = var1.optInt("notificationId");
         var2.recoverField_f = var1.optInt("messageType");
         var2.recoverField_h = var1.optString("overrideMessageId");
         var2.recoverField_i = var1.optString("originalMessage");
         var2.recoverField_j = var1.optInt("messageVersion");
         var2.recoverField_k = var1.optBoolean("notificationOnly");
         var2.recoverField_l = var1.optInt("notificationBuilderId");
         var2.recoverField_m = var1.optInt("notificationType");
         var2.recoverField_n = var1.optString("message");
         var2.recoverField_o = var1.optString("contentType");
         var2.recoverField_p = var1.optString("title");
         var2.recoverField_q = var1.optString("extras");
         var2.recoverField_r = var1.optInt("type");
         var2.recoverField_s = var1.optBoolean("isFullScreen");
         var2.recoverField_t = var1.optInt("notificationRemoveMode");
         var2.recoverField_v = var1.optString("notificationTitle");
         var2.recoverField_u = var1.optString("notificationBgColor");
         var2.recoverField_w = var1.optJSONArray("notificationTargetEvent");
         var2.recoverField_x = var1.optString("notificationAppName");
         var2.recoverField_y = var1.optString("notificationContent");
         var2.recoverField_z = var1.optInt("notificationStyle");
         var2.recoverField_A = var1.optString("notificationBigText");
         var2.recoverField_B = var1.optString("notificationBigPicPath");
         var2.recoverField_C = var1.optString("notificationInbox");
         var2.recoverField_D = var1.optInt("notificationPriority");
         var2.recoverField_E = var1.optInt("notificationImportance");
         var2.recoverField_F = var1.optString("notificationCategory");
         var2.recoverField_G = var1.optString("notificationExtra");
         var2.recoverField_H = var1.optInt("notificationAlertType");
         var2.recoverField_I = var1.optString("notificationChannelId");
         var2.recoverField_J = var1.optString("displayForeground");
         var2.recoverField_K = var1.optString("source");
         var2.recoverField_L = var1.optString("smallIcon");
         var2.recoverField_M = var1.optString("largeIcon");
         var2.recoverField_N = var1.optString("normalSmallIcon");
         var2.recoverField_O = var1.optString("deeplink");
         var2.recoverField_P = var1.optInt("failedAction");
         var2.recoverField_Q = var1.optString("failedLink");
         var2.recoverField_R = var1.optString("targetPkgName");
         var2.recoverField_T = var1.optInt("deeplinkBuilderId");
         var2.recoverField_Y = var1.optBoolean("isRichPush");
         var2.recoverField_Z = var1.optString("showUrl");
         var2.recoverField_aa = var1.optString("showTitle");
         var2.recoverField_ab = var1.optString("_webPagePath");
         var2.recoverField_ac = var1.optInt("jumpMode");
         var2.recoverField_ad = var1.optInt("richType");
         var2.recoverField_ae = var1.optInt("showMode");

         try {
            String var3 = var1.optString("showResourceList");
            if (!TextUtils.isEmpty(var3)) {
               String[] var4 = var3.split(",");
               var2.recoverField_af = new ArrayList(Arrays.asList(var4));
            }
         } catch (Throwable var7) {
            Logger.recoverMethod_e_e925fa09("PushEntity", "showResourceList e:" + var7);
         }

         var2.recoverField_ag = var1.optString("fromNum");
         var2.recoverField_ah = var1.optString("toNum");
         var2.recoverField_ai = (byte)var1.optInt("platform");
         if (var1.has("badgeAddNum")) {
            var2.recoverField_aj = var1.optInt("badgeAddNum");
         }

         var2.recoverField_ak = var1.optString("geofenceid");
         var2.recoverField_al = var1.optLong("radius");
         var2.recoverField_am = var1.optString("status");
         var2.recoverField_an = var1.optBoolean("repeat");
         var2.recoverField_ao = var1.optBoolean("hasShow");
         var2.recoverField_ap = var1.optLong("expiration");
         var2.recoverField_aq = var1.optDouble("longitude");
         var2.recoverField_ar = var1.optDouble("latitude");
         var2.recoverField_as = var1.optString("lastGeoStatus");
         var2.recoverField_at = var1.optString("developerArg0");
         var2.recoverField_au = var1.optString("sound");
         var2.recoverField_av = var1.optString("showBeginTime");
         var2.recoverField_aw = var1.optString("showEndTime");
         var2.recoverField_X = var1.optBoolean("isWmDeepLink");
         var2.recoverField_U = var1.optString("wxAppId");
         var2.recoverField_V = var1.optString("miniOriginid");
         var2.recoverField_W = var1.optInt("miniType");
         var2.recoverField_aC = var1.optString("inAppMsgContent");
         var2.recoverField_aB = var1.optString("inAppIntent");
         var2.recoverField_aD = var1.optString("inAppMsgTitle");
         var2.recoverField_aE = var1.optString("inAppMsgContentBody");
         var2.recoverField_bS = var1.optString("target");
         var2.recoverField_bU = var1.optString("clickAction");
         var2.recoverField_q = var1.optString("extras");
         var2.recoverField_aF = var1.optInt("inAppMsgType");
         var2.recoverField_aG = var1.optInt("inAppMsgShowType");
         var2.recoverField_aH = var1.optInt("inAppMsgShowPos");
         var2.recoverField_aI = var1.optInt("inAppMsgShowAniAction");
         var2.recoverField_bq = var1.optInt("inSupportNotifyAni");
         var2.recoverField_br = var1.optInt("inSupportVibration");
         var2.recoverField_bs = var1.optString("inAppIcon");
         var2.recoverField_aJ = var1.optInt("inAppMsgDismissAniAction");
         var2.recoverField_aK = var1.optInt("inAppMsgShowBackground");
         var2.recoverField_aL = var1.optInt("inAppMsgAllowIntercept");
         var2.recoverField_aM = var1.optInt("inAppMsgFilterMsg");
         var2.recoverField_aN = var1.optString("inAppMsgPicPath");
         var2.recoverField_aO = var1.optInt("inAppMsgCountLmt");
         var2.recoverField_aP = var1.optInt("inAppMsgGap");
         var2.recoverField_aQ = var1.optInt("inAppMsgToUser");
         var2.recoverField_aR = var1.optInt("inAppMsgDelayDisplayTime");
         var2.recoverField_aS = var1.optInt("inAppMsgPageDelayDisplayTime");
         var2.recoverField_aT = var1.optInt("inAppMsgReportPageName");
         var2.recoverField_aU = var1.optInt("inAppUseSysAlert");
         var2.recoverField_aV = var1.optString("inAppMsgBackgroundColor");
         var2.recoverField_aW = var1.optString("inAppMsgTitleColor");
         var2.recoverField_aX = var1.optString("inAppMsgContentColor");
         var2.recoverField_aY = var1.optInt("inAppMsgTitleSize");
         var2.recoverField_aZ = var1.optInt("inAppMsgContentSize");
         var2.recoverField_ba = var1.optInt("inAppMsgCircularSize");
         var2.recoverField_bb = var1.optInt("inAppMsgShowTime");
         var2.recoverField_bc = (float)var1.optDouble("inAppMsgShowElapseTime");
         var2.recoverField_bd = (float)var1.optDouble("inAppMsgDismissElapseTime");
         var2.recoverField_be = var1.optString("inAppWebPageUrl");
         var2.recoverField_bf = var1.optInt("inAppProtocolVer");
         var2.recoverField_bp = var1.optInt("inAppSSPGap");
         var2.recoverField_bg = var1.optInt("inAppCheckIntent");
         var2.recoverField_bh = var1.optString("inAppTplContent");
         var2.recoverField_bi = var1.optString("inAppTplId");
         var2.recoverField_bj = var1.optString("inAppTplUrl");
         var2.recoverField_bk = (float)var1.optDouble("inAppMarginY");
         var2.recoverField_bl = var1.optString("inAppTplData");
         var2.recoverField_bm = var1.optString("inAppIntentActions");
         var2.recoverField_bt = var1.optInt("inAppType");
         var2.recoverField_bP = var1.optLong("expired");
         var2.recoverField_ay = var1.optInt("notifyInAppCacheNumb");
         var2.recoverField_az = var1.optInt("notifyInAppMsgType");
         var2.recoverField_bu = var1.optInt("inAppPriority");
         var2.recoverField_bv = var1.optBoolean("inAppIsDisplayDirect");
         var2.recoverField_bx = var1.optString("inAppBlackConf");
         var2.recoverField_by = var1.optString("inAppAnimatorConf");
         var2.recoverField_bz = var1.optString("inAppAdPosition");
         var2.recoverField_bA = var1.optString("inAppFragment");
         var2.recoverField_bB = var1.optString("inAppAdPosId");
         var2.recoverField_bD = var1.optInt("inAppCoordinateType");
         var2.recoverField_bE = var1.optInt("inAppWinWidth");
         var2.recoverField_bF = var1.optInt("inAppWinHeight");
         JSONArray var9 = var1.optJSONArray("inAppShowCoordinate");
         var2.recoverField_bC = new ArrayList();
         if (null != var9) {
            for (int var10 = 0; var10 < var9.length(); var10++) {
               float var5 = (float)var9.getDouble(var10);
               var2.recoverField_bC.add(var5);
            }
         }

         var2.recoverField_bG = var1.optInt("inAppPosMsgCountLmt");
         var2.recoverField_bH = var1.optInt("inAppPosMsgGap");
         var2.recoverField_bI = (float)var1.optDouble("inAppSizeRatio");
         var2.recoverField_aA = var1.optString("sequenceId");
         var2.recoverField_bw = var1.optBoolean("inAppFixed");

         try {
            String var11 = var1.optString("inAppShowResList");
            if (!TextUtils.isEmpty(var11)) {
               String[] var12 = var11.split(",");
               var2.recoverField_bn = new ArrayList(Arrays.asList(var12));
            }
         } catch (Throwable var6) {
            Logger.recoverMethod_e_e925fa09("PushEntity", "inAppShowResList e:" + var6);
         }

         var2.recoverField_bJ = var1.optString("unionADExtras");
         var2.recoverField_bK = var1.optInt("swipeDismiss");
         var2.recoverField_bL = var1.optInt("adSspSvrVer");
         var2.recoverField_bM = var1.optInt("inAppMsgStyle");
         var2.recoverField_bN = var1.optInt("inAppMsgStyleGap");
         var2.recoverField_bX = var1.optJSONArray("customButtons");
         var2.recoverField_bS = var1.optString("target");
         var2.recoverField_bO = var1.optLong("msgTime");
         var2.recoverField_bQ = var1.optInt("delay");
         var2.recoverField_bR = var1.optInt("interval");
         return var2;
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("PushEntity", "parseJSONString e:" + var8);
         return null;
      }
   }

   public String recoverMethod_c_73cf11cb() {
      try {
         JSONObject var1 = new JSONObject();
         var1.put("msgType", this.recoverField_a);
         var1.put("appId", this.recoverField_b);
         var1.put("senderId", this.recoverField_c);
         var1.put("messageId", this.recoverField_d);
         var1.put("_j_data_", this.recoverField_g);
         var1.put("notificationId", this.recoverField_e);
         var1.put("messageType", this.recoverField_f);
         var1.put("overrideMessageId", this.recoverField_h);
         var1.put("messageVersion", this.recoverField_j);
         var1.put("notificationOnly", this.recoverField_k);
         var1.put("notificationBuilderId", this.recoverField_l);
         var1.put("notificationType", this.recoverField_m);
         var1.put("message", this.recoverField_n);
         var1.put("originalMessage", this.recoverField_i);
         var1.put("contentType", this.recoverField_o);
         var1.put("title", this.recoverField_p);
         var1.put("extras", this.recoverField_q);
         var1.put("type", this.recoverField_r);
         var1.put("isFullScreen", this.recoverField_s);
         var1.put("notificationRemoveMode", this.recoverField_t);
         var1.put("notificationTitle", this.recoverField_v);
         var1.put("notificationBgColor", this.recoverField_u);
         var1.put("notificationTargetEvent", this.recoverField_w);
         var1.put("notificationAppName", this.recoverField_x);
         var1.put("notificationContent", this.recoverField_y);
         var1.put("notificationStyle", this.recoverField_z);
         var1.put("notificationBigText", this.recoverField_A);
         var1.put("notificationBigPicPath", this.recoverField_B);
         var1.put("notificationInbox", this.recoverField_C);
         var1.put("notificationPriority", this.recoverField_D);
         var1.put("notificationImportance", this.recoverField_E);
         var1.put("notificationCategory", this.recoverField_F);
         var1.put("notificationExtra", this.recoverField_G);
         var1.put("notificationAlertType", this.recoverField_H);
         var1.put("notificationChannelId", this.recoverField_I);
         var1.put("displayForeground", this.recoverField_J);
         var1.put("source", this.recoverField_K);
         var1.put("smallIcon", this.recoverField_L);
         var1.put("largeIcon", this.recoverField_M);
         var1.put("normalSmallIcon", this.recoverField_N);
         var1.put("deeplink", this.recoverField_O);
         var1.put("failedAction", this.recoverField_P);
         var1.put("failedLink", this.recoverField_Q);
         var1.put("targetPkgName", this.recoverField_R);
         var1.put("deeplinkBuilderId", this.recoverField_T);
         var1.put("isRichPush", this.recoverField_Y);
         var1.put("showUrl", this.recoverField_Z);
         var1.put("showTitle", this.recoverField_aa);
         var1.put("_webPagePath", this.recoverField_ab);
         var1.put("jumpMode", this.recoverField_ac);
         var1.put("richType", this.recoverField_ad);
         var1.put("showMode", this.recoverField_ae);
         var1.put("showResourceList", RecoverType9b4c5af2.recoverMethod_a_cd13baf0(this.recoverField_af, ","));
         var1.put("fromNum", this.recoverField_ag);
         var1.put("toNum", this.recoverField_ah);
         var1.put("platform", this.recoverField_ai);
         var1.put("badgeAddNum", this.recoverField_aj);
         var1.put("geofenceid", this.recoverField_ak);
         var1.put("radius", this.recoverField_al);
         var1.put("status", this.recoverField_am);
         var1.put("repeat", this.recoverField_an);
         var1.put("hasShow", this.recoverField_ao);
         var1.put("expiration", this.recoverField_ap);
         var1.put("longitude", this.recoverField_aq);
         var1.put("latitude", this.recoverField_ar);
         var1.put("lastGeoStatus", this.recoverField_as);
         var1.put("developerArg0", this.recoverField_at);
         var1.put("sound", this.recoverField_au);
         var1.put("showBeginTime", this.recoverField_av);
         var1.put("showEndTime", this.recoverField_aw);
         var1.put("isWmDeepLink", this.recoverField_X);
         var1.put("wxAppId", this.recoverField_U);
         var1.put("miniOriginid", this.recoverField_V);
         var1.put("miniType", this.recoverField_W);
         var1.put("inAppMsgContent", this.recoverField_aC);
         var1.put("inAppIntent", this.recoverField_aB);
         var1.put("inAppMsgTitle", this.recoverField_aD);
         var1.put("inAppMsgContentBody", this.recoverField_aE);
         var1.put("target", this.recoverField_bS);
         var1.put("clickAction", this.recoverField_bU);
         var1.put("extras", this.recoverField_q);
         var1.put("inAppMsgType", this.recoverField_aF);
         var1.put("inAppMsgShowType", this.recoverField_aG);
         var1.put("inAppMsgShowPos", this.recoverField_aH);
         var1.put("inAppMsgShowAniAction", this.recoverField_aI);
         var1.put("inSupportNotifyAni", this.recoverField_bq);
         var1.put("inSupportVibration", this.recoverField_br);
         var1.put("inAppIcon", this.recoverField_bs);
         var1.put("inAppMsgDismissAniAction", this.recoverField_aJ);
         var1.put("inAppMsgShowBackground", this.recoverField_aK);
         var1.put("inAppMsgAllowIntercept", this.recoverField_aL);
         var1.put("inAppMsgFilterMsg", this.recoverField_aM);
         var1.put("inAppMsgPicPath", this.recoverField_aN);
         var1.put("inAppMsgCountLmt", this.recoverField_aO);
         var1.put("inAppMsgGap", this.recoverField_aP);
         var1.put("inAppMsgToUser", this.recoverField_aQ);
         var1.put("inAppMsgDelayDisplayTime", this.recoverField_aR);
         var1.put("inAppMsgPageDelayDisplayTime", this.recoverField_aS);
         var1.put("inAppMsgReportPageName", this.recoverField_aT);
         var1.put("inAppUseSysAlert", this.recoverField_aU);
         var1.put("inAppMsgBackgroundColor", this.recoverField_aV);
         var1.put("inAppMsgTitleColor", this.recoverField_aW);
         var1.put("inAppMsgContentColor", this.recoverField_aX);
         var1.put("inAppMsgTitleSize", this.recoverField_aY);
         var1.put("inAppMsgContentSize", this.recoverField_aZ);
         var1.put("inAppMsgCircularSize", this.recoverField_ba);
         var1.put("inAppMsgShowTime", this.recoverField_bb);
         var1.put("inAppMsgShowElapseTime", this.recoverField_bc);
         var1.put("inAppMsgDismissElapseTime", this.recoverField_bd);
         var1.put("inAppWebPageUrl", this.recoverField_be);
         var1.put("inAppProtocolVer", this.recoverField_bf);
         var1.put("inAppSSPGap", this.recoverField_bp);
         var1.put("inAppCheckIntent", this.recoverField_bg);
         var1.put("inAppTplContent", this.recoverField_bh);
         var1.put("inAppTplId", this.recoverField_bi);
         var1.put("inAppTplUrl", this.recoverField_bj);
         var1.put("inAppMarginY", this.recoverField_bk);
         var1.put("inAppTplData", this.recoverField_bl);
         var1.put("inAppIntentActions", this.recoverField_bm);
         var1.put("inAppShowResList", RecoverType9b4c5af2.recoverMethod_a_cd13baf0(this.recoverField_bn, ","));
         var1.put("inAppType", this.recoverField_bt);
         var1.put("expired", this.recoverField_bP);
         var1.put("notifyInAppCacheNumb", this.recoverField_ay);
         var1.put("notifyInAppMsgType", this.recoverField_az);
         var1.put("inAppPriority", this.recoverField_bu);
         var1.put("inAppIsDisplayDirect", this.recoverField_bv);
         var1.put("inAppFixed", this.recoverField_bw);
         var1.put("inAppBlackConf", this.recoverField_bx);
         var1.put("inAppAnimatorConf", this.recoverField_by);
         var1.put("inAppAdPosition", this.recoverField_bz);
         var1.put("inAppFragment", this.recoverField_bA);
         var1.put("inAppAdPosId", this.recoverField_bB);
         var1.put("inAppCoordinateType", this.recoverField_bD);
         var1.put("inAppWinWidth", this.recoverField_bE);
         var1.put("inAppWinHeight", this.recoverField_bF);
         JSONArray var2 = new JSONArray();

         for (Object recoveryElement_1238 : this.recoverField_bC) {
            float var4 = (float)recoveryElement_1238;
            var2.put(var4);
         }

         var1.put("inAppShowCoordinate", var2);
         var1.put("inAppPosMsgCountLmt", this.recoverField_bG);
         var1.put("inAppPosMsgGap", this.recoverField_bH);
         var1.put("inAppSizeRatio", this.recoverField_bI);
         var1.put("sequenceId", this.recoverField_aA);
         var1.put("unionADExtras", this.recoverField_bJ);
         var1.put("swipeDismiss", this.recoverField_bK);
         var1.put("adSspSvrVer", this.recoverField_bL);
         var1.put("inAppMsgStyle", this.recoverField_bM);
         var1.put("inAppMsgStyleGap", this.recoverField_bN);
         var1.put("customButtons", this.recoverField_bX);
         var1.put("target", this.recoverField_bS);
         var1.put("msgTime", this.recoverField_bO);
         var1.put("delay", this.recoverField_bQ);
         var1.put("interval", this.recoverField_bR);
         var1.put("inAppPriority", this.recoverField_bu);
         return var1.toString();
      } catch (Throwable var5) {
         return "";
      }
   }

   public static String recoverMethod_a_7841f161(String var0, JSONObject var1) {
      if (!TextUtils.isEmpty(var0) && var1 != null) {
         try {
            String var2 = var1.optString("_j_data_", "");
            Logger.recoverMethod_d_e925fa09("PushEntity", "parse jdata:" + var2);
            recoverMethod_a_e925fa09(var0, var2);
            return var2;
         } catch (Throwable var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      if (recoverField_bY != null && !TextUtils.isEmpty(var0) && !TextUtils.isEmpty(var1)) {
         recoverField_bY.put(var0, var1);
      }
   }

   public static String recoverMethod_c_e96ea081(String var0) {
      return recoverField_bY != null ? (String)recoverField_bY.get(var0) : "";
   }

   public static void recoverMethod_b_e925fa09(String var0, String var1) {
      if (!TextUtils.isEmpty(var0) && !TextUtils.isEmpty(var1)) {
         recoverField_bZ.put(var0, var1);
      }
   }

   public static String recoverMethod_d_e96ea081(String var0) {
      if (!TextUtils.isEmpty(var0)) {
         try {
            String var1 = var0;
            LineNumberReader var2 = new LineNumberReader(new StringReader(var1));
            String var3 = var2.readLine();
            String var4 = var2.readLine();
            if (var3 == null) {
               Logger.recoverMethod_e_e925fa09("PushEntity", "NO appId");
               return null;
            }

            if (var4 == null) {
               Logger.recoverMethod_e_e925fa09("PushEntity", "NO senderId");
               return null;
            }

            int var5 = var3.length() + var4.length() + 2;
            if (var1.length() > var5 + 1) {
               var1 = var1.substring(var5);
               JSONObject var6 = new JSONObject(var1);
               String var7 = var6.optString("_j_data_", "");
               Logger.recoverMethod_d_e925fa09("PushEntity", "get jdata from push msg:" + var7);
               return var7;
            }

            Logger.recoverMethod_d_e925fa09("PushEntity", "No msgContent");
            return null;
         } catch (Throwable var8) {
            Logger.recoverMethod_d_e925fa09("PushEntity", "get jdata from push msg error:" + var8.getMessage());
         }
      }

      return null;
   }

   public static class Memberce51f067 implements Serializable {
      private static final long serialVersionUID = 1L;
      public String recoverField_a;
      public byte recoverField_b;
      public String recoverField_c;
      public int recoverField_d;
      public String recoverField_e;
      public String recoverField_f;

      public static RecoverType2e4cce2a.Memberce51f067 recoverMethod_a_14ff071f(String var0) {
         if (TextUtils.isEmpty(var0)) {
            return null;
         }

         try {
            return recoverMethod_a_fd74075(new JSONObject(var0));
         } catch (Throwable var2) {
            return null;
         }
      }

      public static RecoverType2e4cce2a.Memberce51f067 recoverMethod_a_fd74075(JSONObject var0) {
         if (var0 == null) {
            return null;
         }

         RecoverType2e4cce2a.Memberce51f067 var1 = new RecoverType2e4cce2a.Memberce51f067();
         var1.recoverField_a = var0.optString("msg_id", "");
         var1.recoverField_b = (byte)var0.optInt("rom_type", 0);
         var1.recoverField_c = var0.optString("name", "");
         var1.recoverField_d = var0.optInt("action_type", 0);
         var1.recoverField_e = var0.optString("action", "");
         var1.recoverField_f = var0.optString("data", "");
         return var1;
      }

      public JSONObject recoverMethod_a_a7dbfe3() {
         JSONObject var1 = new JSONObject();

         try {
            var1.put("msg_id", this.recoverField_a);
            var1.put("rom_type", this.recoverField_b);
            var1.put("name", this.recoverField_c);
            var1.put("action_type", this.recoverField_d);
            var1.put("action", this.recoverField_e);
            var1.put("data", this.recoverField_f);
         } catch (Exception var3) {
            Logger.recoverMethod_e_e925fa09("PushEntity", "CustomButton toJSON error: " + var3.getMessage());
         }

         return var1;
      }

      @Override
      public String toString() {
         return "CustomButton{msgId='"
            + this.recoverField_a
            + '\''
            + ", platform="
            + this.recoverField_b
            + ", name='"
            + this.recoverField_c
            + '\''
            + ", actionType="
            + this.recoverField_d
            + ", action='"
            + this.recoverField_e
            + '\''
            + ", data='"
            + this.recoverField_f
            + '\''
            + '}';
      }
   }

   private static class Memberce51f068 {
      private static final Map recoverField_a = new HashMap();

      public static int recoverMethod_a_be7fc15c(JSONObject var0) {
         int var1 = var0.optInt("n_priority", 0);
         int var2 = recoverMethod_c_be7fc15c(var0);
         if (-1 == var2) {
            return var1;
         }

         int var3 = RecoverType2e4d193b.recoverMethod_b_134621(var2);
         return var1 > var3 ? var3 : var1;
      }

      private static int recoverMethod_c_be7fc15c(JSONObject var0) {
         JSONObject var1 = var0.optJSONObject("n_importance");
         if (null != var1) {
            String var2 = Build.MANUFACTURER;
            if (null == var2) {
               return -1;
            }

            var2 = var2.toLowerCase();
            String var3 = (String)recoverField_a.get(var2);
            if (null != var3) {
               int var4 = var1.optInt(var3, -1);
               Logger.recoverMethod_d_e925fa09("PushEntity", "im_v:" + var4);
               return var4;
            }
         }

         return -1;
      }

      static {
         recoverField_a.put("google", "fc");
         recoverField_a.put("huawei", "hw");
         recoverField_a.put("honor", "hn");
         recoverField_a.put("meizu", "mz");
         recoverField_a.put("vivo", "vi");
         recoverField_a.put("oppo", "op");
         recoverField_a.put("realme", "op");
         recoverField_a.put("oneplus", "op");
         recoverField_a.put("xiaomi", "xm");
         recoverField_a.put("blackshark", "xm");
      }
   }
}
