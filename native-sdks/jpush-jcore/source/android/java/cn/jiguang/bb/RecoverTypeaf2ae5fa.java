package cn.jiguang.bb;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bc.RecoverTypeaf2ae9b9;
import cn.jiguang.be.RecoverTypeaf2af13b;
import cn.jiguang.be.RecoverTypeaf2af13d;
import cn.jiguang.be.RecoverTypeaf2af13e;
import cn.jiguang.be.RecoverTypeaf2af13f;
import cn.jiguang.be.RecoverTypeaf2af140;
import cn.jiguang.be.RecoverTypeaf2af141;
import cn.jiguang.be.RecoverTypeaf2af142;
import cn.jiguang.be.RecoverTypeaf2af144;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2ae5fa {
   private ConcurrentHashMap recoverField_a;
   private AtomicBoolean recoverField_b = new AtomicBoolean(false);
   private Context recoverField_c;
   private boolean recoverField_d;
   private String recoverField_e = "netmt.catch";

   private RecoverTypeaf2ae5fa() {
   }

   public static RecoverTypeaf2ae5fa recoverMethod_a_c69c3eec() {
      return RecoverTypeaf2ae5fa.Member90095437.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_d) {
         this.recoverField_c = var1.getApplicationContext();
         this.recoverField_b.set((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_aG_48794abf()));

         try {
            String var2 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(this.recoverField_c).replace(this.recoverField_c.getPackageName(), "");
            if (!TextUtils.isEmpty(var2)) {
               if (var2.startsWith(":")) {
                  var2 = var2.substring(1);
               }

               if (!this.recoverField_e.contains(var2)) {
                  this.recoverField_e = "netmt." + var2;
               }
            }
         } catch (Throwable var3) {
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "init DATAPATH :" + this.recoverField_e);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "init state :" + this.recoverField_b.get());
         if (this.recoverField_b.get()) {
            this.recoverField_a = new ConcurrentHashMap();
         } else {
            RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff(this.recoverField_e);
         }

         this.recoverField_d = true;
      }
   }

   public void recoverMethod_a_1a552341(Context var1, boolean var2) {
      this.recoverMethod_a_f0554df3(var1);
      synchronized (RecoverTypeaf2ae5fa.class) {
         if (this.recoverField_b.get() != var2) {
            this.recoverField_b.set(var2);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_c, RecoverType5a690d1.recoverMethod_aG_48794abf().recoverMethod_a_4e9722a3(var2));
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "change state :" + this.recoverField_b.get());
            if (this.recoverField_b.get()) {
               this.recoverField_a = new ConcurrentHashMap();
            } else {
               this.recoverField_a = null;
            }
         }
      }
   }

   public String recoverMethod_a_bd4fc8df(Context var1, HttpRequest var2, boolean var3) {
      String var4 = "";

      try {
         if (this.recoverMethod_b_f0554df7(var1)) {
            RecoverTypeaf2af144 var5 = new RecoverTypeaf2af144(this.recoverField_c);
            var5.recoverMethod_a_ad38039f(var2, var3);
            var4 = var5.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var4, var5);
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "httpStart wrong:" + var6.getMessage());
      }

      return var4;
   }

   public void recoverMethod_a_982b89e3(Context var1, String var2, HttpResponse var3) {
      try {
         if (this.recoverMethod_b_f0554df7(var1) && !TextUtils.isEmpty(var2)) {
            RecoverTypeaf2ae9b9 var4 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var2);
            if (var4 != null && var4 instanceof RecoverTypeaf2af144) {
               ((RecoverTypeaf2af144)var4).recoverMethod_a_d6d5a26b(var3);
               this.recoverMethod_a_b2b3f693(var4);
            }
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "httpEnd wrong:" + var5.getMessage());
      }
   }

   public String recoverMethod_a_e96ea081(String var1) {
      String var2 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af13b var3 = new RecoverTypeaf2af13b(this.recoverField_c);
            var3.recoverMethod_a_aad3b1ff(var1);
            var2 = var3.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var2, var3);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "dnsStart wrong:" + var4.getMessage());
         }
      }

      return var2;
   }

   public void recoverMethod_a_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af13b) {
                  ((RecoverTypeaf2af13b)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "dnsEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_b_85e4583c(String var1, int var2) {
      String var3 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af13d var4 = new RecoverTypeaf2af13d(this.recoverField_c);
            var4.recoverMethod_a_afa300e4(var1, var2);
            var3 = var4.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var3, var4);
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "sisStart wrong:" + var5.getMessage());
         }
      }

      return var3;
   }

   public void recoverMethod_c_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af13d) {
                  ((RecoverTypeaf2af13d)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "sisEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_a_782597e1(String var1, int var2, int var3) {
      String var4 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af13e var5 = new RecoverTypeaf2af13e(this.recoverField_c);
            var5.recoverMethod_a_db6739ae(var1, var2, var3 + "");
            var4 = var5.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var4, var5);
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpConnStart wrong:" + var6.getMessage());
         }
      }

      return var4;
   }

   public void recoverMethod_d_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af13e) {
                  ((RecoverTypeaf2af13e)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpConnEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_b_782597e1(String var1, int var2, int var3) {
      String var4 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af141 var5 = new RecoverTypeaf2af141(this.recoverField_c);
            var5.recoverMethod_a_db6739ae(var1, var2, var3 + "");
            var4 = var5.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var4, var5);
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpRegisterStart wrong:" + var6.getMessage());
         }
      }

      return var4;
   }

   public void recoverMethod_e_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af141) {
                  ((RecoverTypeaf2af141)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpRegisterEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_c_782597e1(String var1, int var2, int var3) {
      String var4 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af140 var5 = new RecoverTypeaf2af140(this.recoverField_c);
            var5.recoverMethod_a_db6739ae(var1, var2, var3 + "");
            var4 = var5.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var4, var5);
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpLoginStart wrong:" + var6.getMessage());
         }
      }

      return var4;
   }

   public void recoverMethod_f_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af140) {
                  ((RecoverTypeaf2af140)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpLoginEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_d_782597e1(String var1, int var2, int var3) {
      String var4 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af142 var5 = new RecoverTypeaf2af142(this.recoverField_c);
            var5.recoverMethod_a_db6739ae(var1, var2, var3 + "");
            var4 = var5.recoverMethod_c_73cf11cb();
            this.recoverField_a.put(var4, var5);
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpReportStart wrong:" + var6.getMessage());
         }
      }

      return var4;
   }

   public void recoverMethod_g_afa300e4(String var1, int var2) {
      if (this.recoverField_b.get()) {
         try {
            if (!TextUtils.isEmpty(var1)) {
               RecoverTypeaf2ae9b9 var3 = (RecoverTypeaf2ae9b9)this.recoverField_a.remove(var1);
               if (var3 != null && var3 instanceof RecoverTypeaf2af142) {
                  ((RecoverTypeaf2af142)var3).recoverMethod_a_13462e(var2);
                  this.recoverMethod_a_b2b3f693(var3);
               }
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpReportEnd wrong:" + var4.getMessage());
         }
      }
   }

   public String recoverMethod_a_ce0c4cdc(String var1, int var2, int var3, int var4) {
      String var5 = "";
      if (this.recoverField_b.get()) {
         try {
            RecoverTypeaf2af13f var6 = new RecoverTypeaf2af13f(this.recoverField_c);
            var6.recoverMethod_a_91806f15(var1, var2, var3 + "", var4);
            this.recoverMethod_a_b2b3f693(var6);
         } catch (Throwable var7) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "tcpDisconnect wrong:" + var7.getMessage());
         }
      }

      return var5;
   }

   private boolean recoverMethod_b_f0554df7(Context var1) {
      this.recoverMethod_a_f0554df3(var1);
      return this.recoverField_b.get();
   }

   private void recoverMethod_a_b2b3f693(final RecoverTypeaf2ae9b9 var1) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               synchronized (RecoverTypeaf2ae5fa.class) {
                  JSONObject var2 = var1.recoverMethod_b_a7dbfe3();
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "save moniter data:" + var2);
                  JSONObject var3 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d(RecoverTypeaf2ae5fa.this.recoverField_e);
                  JSONArray var4 = null;
                  if (var3 != null && var3.has("data")) {
                     var4 = var3.optJSONArray("data");
                  } else {
                     var3 = new JSONObject();
                     var3.put("type", "sdk_moniter");
                     var4 = new JSONArray();
                  }

                  var4.put(var2);
                  var3.put("data", var4);
                  RecoverTypeaf2b07c9.recoverMethod_a_27ad0523(RecoverTypeaf2ae5fa.this.recoverField_e, var3);
               }
            } catch (Throwable var7) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetMoniterManager", "save data error:" + var7.getMessage());
            }
         }
      });
   }

   public void recoverMethod_b_9b75() {
      if (this.recoverField_b.get()) {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  synchronized (RecoverTypeaf2ae5fa.class) {
                     JSONObject var2 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d(RecoverTypeaf2ae5fa.this.recoverField_e);
                     if (var2 != null && var2.has("data")) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NetMoniterManager", "start report moniter data");
                        RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(RecoverTypeaf2ae5fa.this.recoverField_c, var2, "sdk_moniter");
                        RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff(RecoverTypeaf2ae5fa.this.recoverField_e);
                     }
                  }
               } catch (Throwable var5) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NetMoniterManager", "save data error:" + var5.getMessage());
               }
            }
         });
      }
   }

   private static final class Member90095437 {
      private static final RecoverTypeaf2ae5fa recoverField_a = new RecoverTypeaf2ae5fa();
   }
}
