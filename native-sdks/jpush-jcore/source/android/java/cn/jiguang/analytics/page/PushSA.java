package cn.jiguang.analytics.page;

import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PushSA {
   private static final String TAG = "PushSA";
   private static final String KEY_SESSION_ID = "session_id";
   public static final String CACHE_PATH = "push_stat_cache.json";
   public static boolean isOnResumeInvoke = false;
   public static boolean isOnPauseInvoke = false;
   private String cur_activity = null;
   private String cur_session_id = null;
   private long interval = 30L;
   private long latestResumeTime = 0L;
   private long latestPauseTime = 0L;
   private boolean firstResume = true;
   private boolean activitySwitch = false;
   private boolean stat_enable = true;
   private long tempTimelong = 0L;
   public static final String REPORT_ACTIVE_LAUNCH = "active_launch";
   public static final String REPORT_ACTIVE_TERMINATE = "active_terminate";
   private JSONObject flow_cache = null;
   private final Object cur_session_file_lock = new Object();

   private PushSA() {
   }

   public void onFragmentResume(Context var1, String var2) {
      if (this.activitySwitch) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "PushSA", "JCoreInterface.onResume() must be called after called JCoreInterface.onPause() in last Activity or Fragment"
         );
      } else {
         this.activitySwitch = true;
         this.cur_activity = var2;
         this.latestResumeTime = System.currentTimeMillis();
         final Context var3 = var1.getApplicationContext();

         try {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", new RecoverTypeaf2b0401("PushSA#onFragmentResume") {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     PushSA.this.sendLogRoutine(var3);
                  } catch (Throwable var2x) {
                  }
               }
            });
         } catch (Throwable var5) {
         }
      }
   }

   public void onFragmentPause(Context var1, String var2) {
      if (!this.activitySwitch) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "PushSA", "JCoreInterface.onPause() must be called after called JCoreInterface.onResume() in this Activity or Fragment"
         );
      } else {
         this.activitySwitch = false;
         if (this.cur_activity != null && this.cur_activity.equals(var2)) {
            this.latestPauseTime = System.currentTimeMillis();
            final Context var3 = var1.getApplicationContext();

            try {
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", new RecoverTypeaf2b0401("PushSA#onFragmentPause") {
                  @Override
                  public void recoverMethod_a_9b75() {
                     try {
                        PushSA.this.saveLogRoutine(var3);
                     } catch (Throwable var2x) {
                     }
                  }
               });
            } catch (Throwable var5) {
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("PushSA", "page name didn't match the last one passed by onResume");
         }
      }
   }

   public long getInterval() {
      return this.interval;
   }

   public void setInterval(long var1) {
      this.interval = var1;
   }

   public boolean isStatEnable() {
      return this.stat_enable;
   }

   public void setStatEnable(boolean var1) {
      this.stat_enable = var1;
   }

   public void onKillProcess(Context var1) {
      try {
         if (this.cur_activity != null && this.activitySwitch) {
            this.latestPauseTime = System.currentTimeMillis();
            final Context var2 = var1.getApplicationContext();

            try {
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", new RecoverTypeaf2b0401("PushSA#onKillProcess") {
                  @Override
                  public void recoverMethod_a_9b75() {
                     try {
                        PushSA.this.saveLogRoutine(var2);
                     } catch (Throwable var2x) {
                     }
                  }
               });
            } catch (Throwable var4) {
            }
         }
      } catch (Exception var5) {
      }
   }

   public static PushSA getInstance() {
      return PushSA.Member54ed5516.recoverField_a;
   }

   public void onResume(Context var1) {
      if (this.invokeCheck(var1, "onResume")) {
         isOnResumeInvoke = true;

         try {
            TabActivity var2 = (TabActivity)var1;
            this.activitySwitch = false;
         } catch (ClassCastException var4) {
         } catch (Exception var5) {
         }

         if (!this.activitySwitch) {
            this.activitySwitch = true;
            this.latestResumeTime = System.currentTimeMillis();
            this.cur_activity = var1.getClass().getName();

            try {
               Context var6 = var1.getApplicationContext();
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", new PushSA.Member54ed5517(true, var6, this));
            } catch (Throwable var3) {
            }
         }
      }
   }

   public void onPause(Context var1) {
      if (this.invokeCheck(var1, "onPause")) {
         isOnPauseInvoke = true;

         try {
            TabActivity var2 = (TabActivity)var1;
            this.activitySwitch = true;
         } catch (ClassCastException var4) {
         } catch (Exception var5) {
         }

         if (this.activitySwitch) {
            this.activitySwitch = false;
            if (this.cur_activity != null && this.cur_activity.equals(var1.getClass().getName())) {
               this.latestPauseTime = System.currentTimeMillis();
               this.tempTimelong = this.latestResumeTime;

               try {
                  Context var6 = var1.getApplicationContext();
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("SCHEDULE_TASK", new PushSA.Member54ed5517(false, var6, this));
               } catch (Throwable var3) {
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PushSA", "the activity pass by onPause didn't match last one passed by onResume");
            }
         }
      }
   }

   private boolean invokeCheck(Context var1, String var2) {
      if (!this.stat_enable) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PushSA", "stat function has been disabled");
         return false;
      } else if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PushSA", "context is null");
         return false;
      } else if (var1 instanceof Application) {
         RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("PushSA", "Context should be an Activity on this method : " + var2);
         return false;
      } else {
         return true;
      }
   }

   private void wrapDate(JSONObject var1) throws JSONException {
      String var2 = RecoverTypeaf2b07c6.recoverMethod_b_73cf11cb();
      String var3 = var2.split("_")[0];
      String var4 = var2.split("_")[1];
      var1.put("date", var3);
      var1.put("time", var4);
   }

   private JSONObject createNewSession(Context var1, long var2) {
      this.cur_session_id = this.generateSessionID(var1, var2);
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var1,
         RecoverType5a690d1.recoverMethod_R_48794abf().recoverMethod_a_4e9722a3(this.latestResumeTime),
         RecoverType5a690d1.recoverMethod_U_48794abf().recoverMethod_a_4e9722a3(this.cur_session_id)
      );
      JSONObject var4 = new JSONObject();

      try {
         this.wrapDate(var4);
         RecoverType5a68d10.recoverMethod_a_1b76323(var1, var4, "active_launch");
         var4.put("session_id", this.cur_session_id);
         return var4;
      } catch (JSONException var6) {
         return null;
      }
   }

   private String generateSessionID(Context var1, long var2) {
      StringBuilder var4 = new StringBuilder();
      String var5 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var1);
      if (!TextUtils.isEmpty(var5)) {
         var4.append(var5);
      }

      var4.append(var2);
      return RecoverTypeaf2b07d5.recoverMethod_d_e96ea081(var4.toString());
   }

   private boolean isNewSession(Context var1) {
      boolean var2 = true;
      if (this.firstResume) {
         this.firstResume = false;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PushSA", "statistics start");
         long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_T_48794abf());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "PushSA",
            "lastPause:" + var3 + ",latestResumeTime:" + this.latestResumeTime + ",interval:" + this.interval * 1000L + ",a:" + (this.latestResumeTime - var3)
         );
         if (var3 > 0L && this.latestResumeTime - var3 <= this.interval * 1000L) {
            var2 = false;
         }
      } else if (this.latestResumeTime - this.latestPauseTime <= this.interval * 1000L) {
         var2 = false;
      }

      return var2;
   }

   private JSONObject getFlowCache(Context var1) {
      if (this.flow_cache == null) {
         this.flow_cache = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("push_stat_cache.json");
      }

      return this.flow_cache;
   }

   private void updateFlowCache(JSONObject var1) {
      this.flow_cache = var1;
   }

   private void sendLogRoutine(Context var1) {
      if (this.isNewSession(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("PushSA", "new statistics session");
         JSONArray var2 = new JSONArray();
         JSONObject var3 = this.createNewSession(var1, this.latestResumeTime);
         if (var3 != null) {
            var2.put(var3);
         }

         JSONObject var4 = null;
         synchronized (this.cur_session_file_lock) {
            var4 = this.getFlowCache(var1);
            if (var4 != null && var4.length() > 0) {
               try {
                  RecoverType5a68d10.recoverMethod_a_1b76323(var1, var4, "active_terminate");
               } catch (Exception var8) {
               }

               this.clearCurrentLogFile(var1);
               this.flow_cache = null;
            }
         }

         if (var4 != null && var4.length() > 0 && RecoverTypeaf2b07c2.recoverMethod_c_f0554df7(var1)) {
            var2.put(var4);
         }

         RecoverType5a68d10.recoverMethod_a_127ac70f(var1, var2);
      } else {
         this.cur_session_id = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var1, RecoverType5a690d1.recoverMethod_U_48794abf());
      }
   }

   private void clearCurrentLogFile(Context var1) {
      RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("push_stat_cache.json");
   }

   private void saveLogRoutine(Context var1) {
      if (var1 != null) {
         synchronized (this.cur_session_file_lock) {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(
               var1,
               RecoverType5a690d1.recoverMethod_T_48794abf().recoverMethod_a_4e9722a3(this.latestPauseTime),
               RecoverType5a690d1.recoverMethod_S_48794abf().recoverMethod_a_4e9722a3(this.latestPauseTime)
            );
            if (RecoverTypeaf2b07c2.recoverMethod_c_f0554df7(var1)) {
               JSONObject var3 = this.getFlowCache(var1);
               if (var3 == null) {
                  var3 = new JSONObject();
               }

               try {
                  this.updateFlowInfo(var3, var1);
               } catch (Exception var6) {
               }

               this.updateFlowCache(var3);
               this.saveCurrentLog(var1, var3);
            }
         }
      }
   }

   private void saveCurrentLog(Context var1, JSONObject var2) {
      RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("push_stat_cache.json", var2);
   }

   private void updateFlowInfo(JSONObject var1, Context var2) throws JSONException {
      long var3 = 10L;
      long var5 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var2, RecoverType5a690d1.recoverMethod_R_48794abf());
      if (var5 <= 0L) {
         long var7 = this.latestPauseTime - this.tempTimelong;
         if (var7 > 0L) {
            var3 = var7 / 1000L;
         }

         RecoverType5a690d2.recoverMethod_a_abcb8d40(var2, RecoverType5a690d1.recoverMethod_R_48794abf().recoverMethod_a_4e9722a3(this.tempTimelong));
      } else {
         var3 = (this.latestPauseTime - var5) / 1000L;
      }

      var1.put("duration", var3);
      var1.put("itime", System.currentTimeMillis() / 1000L);
      var1.put("session_id", this.cur_session_id);
      this.wrapDate(var1);
   }

   private static final class Member54ed5516 {
      private static final PushSA recoverField_a = new PushSA();
   }

   static class Member54ed5517 extends RecoverTypeaf2b0401 {
      boolean recoverField_a;
      Context recoverField_b;
      PushSA recoverField_c;

      public Member54ed5517(boolean var1, Context var2, PushSA var3) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_h = "PushSA";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            if (this.recoverField_a) {
               this.recoverField_c.sendLogRoutine(this.recoverField_b);
            } else {
               this.recoverField_c.saveLogRoutine(this.recoverField_b);
            }
         } catch (Throwable var2) {
         }
      }
   }
}
