package cn.jiguang.at;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.al.RecoverTypeaf2a9729;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.ar.RecoverTypeaf2aadb0;
import cn.jiguang.av.RecoverTypeaf2abcae;
import cn.jiguang.ax.RecoverTypeaf2ac42f;
import cn.jiguang.ax.RecoverTypeaf2ac430;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bg.RecoverTypeaf2af8c1;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import cn.jiguang.m.RecoverType5a6aed9;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONObject;

public class RecoverTypeaf2ab531 {
   public static int recoverField_a = 290;
   public static int recoverField_b = 30;
   public static int recoverField_c = 2;
   public static long recoverField_d = 10000L;
   private static AtomicReference recoverField_f = new AtomicReference(recoverField_d);
   private static AtomicReference recoverField_g = new AtomicReference(recoverField_c);
   public static boolean recoverField_e = false;
   private volatile AtomicReference recoverField_h = new AtomicReference();
   private int recoverField_i;
   private int recoverField_j;
   private boolean recoverField_k = false;
   private int recoverField_l = 0;
   private int recoverField_m = 0;
   private long recoverField_n;
   private boolean recoverField_o;
   private boolean recoverField_p;
   private Context recoverField_q;
   private boolean recoverField_r = false;
   private final AtomicBoolean recoverField_s = new AtomicBoolean(false);
   private RecoverTypeaf2ac42f recoverField_t = new RecoverTypeaf2ac42f() {
      @Override
      public void recoverMethod_a_c050048(Message var1) {
         if (var1 != null) {
            Bundle var2 = null;
            switch (var1.what) {
               case 1004:
                  var2 = new Bundle();
                  var2.putBoolean("force", true);
                  RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a16", var2);
                  break;
               case 1005:
                  var2 = new Bundle();
                  var2.putBoolean("force", false);
                  RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a16", var2);
                  break;
               case 1006:
               case 1007:
                  RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a2", null);
                  break;
               case 1011:
                  RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a14", null);
                  break;
               case 1022:
                  RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a17", null);
                  break;
               case 2000:
                  RecoverTypeaf2ab534.recoverMethod_a_c69656f2().recoverMethod_a_f0554df3(RecoverTypeaf2ab531.this.recoverField_q);
                  break;
               case 2001:
                  boolean var3 = RecoverTypeaf2ab531.this.recoverMethod_o_9b79();
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "check socket connected=" + !var3);
                  if (var3) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "socket disconnect,need to restart");
                     RecoverTypeaf2ab531.this.recoverMethod_a_d19840b7(RecoverTypeaf2ab531.this.recoverField_q, "tcp_a25", null);
                  }
            }
         }
      }
   };
   private boolean recoverField_u = false;
   private int recoverField_v = 0;
   private int recoverField_w = 0;

   private RecoverTypeaf2ab531() {
      this.recoverField_p = true;
      this.recoverField_q = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
   }

   public static RecoverTypeaf2ab531 recoverMethod_a_c6965695() {
      return RecoverTypeaf2ab531.Member8f5231af.recoverField_a;
   }

   public synchronized void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_r) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JCoreTCPManager", "hb:" + this.recoverMethod_f_9b68() + ",google:" + true + ",google pure : " + false + ",internal:" + false
         );
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "init tcp manager...");
         this.recoverField_q = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var1.getApplicationContext());
         RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_a_f0554df3(this.recoverField_q);
         RecoverTypeaf2ab530.recoverMethod_a_c6965676().recoverMethod_a_1a552341(var1, true);
         Boolean var2 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(
            RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), RecoverType5a690d1.recoverMethod_af_48794abf()
         );
         this.recoverMethod_a_1385ff(var2);
         this.recoverField_r = true;
      }
   }

   public void recoverMethod_a_d19840b7(Context var1, String var2, Bundle var3) {
      this.recoverMethod_a_f0554df3(var1);
      RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(new RecoverTypeaf2ab531.Member8f5231ae(var1, var2, var3));
   }

   public void recoverMethod_b_9b75() {
      RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1022);
      this.recoverField_n = System.currentTimeMillis();
      this.recoverField_m = 0;
      this.recoverField_s.set(false);
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreTCPManager", "update rtc state");
   }

   private void recoverMethod_a_13462e(int var1) {
      this.recoverField_i = var1;
      if (var1 == 1012) {
         RecoverTypeaf2aadab.recoverMethod_a_f0554df3(this.recoverField_q);
      }

      this.recoverMethod_l_9b75();
      Bundle var2 = new Bundle();
      var2.putBoolean("state", false);
      RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_5dc9c75("isTcpLoggedIn", var2);
   }

   public RecoverTypeaf2aadb0 recoverMethod_c_c6956df6() {
      return (RecoverTypeaf2aadb0)this.recoverField_h.get();
   }

   public boolean recoverMethod_d_9b79() {
      return this.recoverField_o;
   }

   private void recoverMethod_j_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - onLoggedIn");
      if (!this.recoverField_o) {
         this.recoverField_o = true;
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_q, 1, 0, "success");
      }

      this.recoverMethod_b_9b75();
      this.recoverField_l = 0;
      this.recoverField_m = 0;
      RecoverType5a681cd.recoverMethod_b_f0554df3(this.recoverField_q);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Force to send 12s heartbeat after login");
      RecoverType5a681cd.recoverMethod_a_304fbba3(this.recoverField_q, true, 5000L);
      RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_q, "on_logged", null);
      Bundle var1 = new Bundle();
      var1.putBoolean("login", true);
      RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_q, "periodtask", var1);
      if (this.recoverMethod_k_9b79()) {
         RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(2000, 2000L, this.recoverField_t);
         RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_f0554df3(this.recoverField_q);
         RecoverTypeaf2ab530.recoverMethod_a_c6965676().recoverMethod_a_1a552341(this.recoverField_q, true);
         Bundle var2 = new Bundle();
         var2.putBoolean("state", true);
         RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_5dc9c75("isTcpLoggedIn", var2);
      }
   }

   private boolean recoverMethod_k_9b79() {
      boolean var1 = RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_a_f0554df7(this.recoverField_q);
      if (!var1 && !RecoverTypeaf2ab534.recoverMethod_a_c69656f2().recoverMethod_b_f0554df7(this.recoverField_q)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "not keep tcp");
         this.recoverField_p = false;
         this.recoverMethod_l_9b75();
         return false;
      } else {
         return true;
      }
   }

   private void recoverMethod_b_13462e(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - onDisconnected");
      if (this.recoverField_o) {
         this.recoverField_o = false;
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_q, -1, -1, "push connect break");
      }

      if (this.recoverMethod_c_c6956df6() == null
         && (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_q, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "push already stopped!!!");
      } else {
         this.recoverField_m = 0;
         this.recoverMethod_c_13462e(var1);
         this.recoverMethod_m_9b75();
         this.recoverField_l++;
      }
   }

   private synchronized void recoverMethod_c_13462e(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "closeTcp instanceId:" + var1);
      if (var1 > 0) {
         RecoverTypeaf2aadb0 var2 = this.recoverMethod_c_c6956df6();
         if (var2 != null && var2.recoverMethod_f_9b68() == var1) {
            this.recoverMethod_l_9b75();
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "JCoreTCPManager", "closeTcp instanceId not match, current:" + (var2 != null ? var2.recoverMethod_f_9b68() : "null")
            );
         }
      } else {
         this.recoverMethod_l_9b75();
      }
   }

   private synchronized void recoverMethod_l_9b75() {
      RecoverTypeaf2aadb0 var1 = this.recoverMethod_c_c6956df6();
      if (var1 != null) {
         var1.recoverMethod_c_9b75();
         this.recoverField_h.set(null);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "tcp has stopeed");
      }
   }

   private void recoverMethod_m_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - retryConnect - disconnectedTimes:" + this.recoverField_l);
      if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "[retryConnect] network is not connect");
      } else if (this.recoverField_j > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "[retryConnect] registerErrCode >0,registerErrCode:" + this.recoverField_j);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "[retryConnect] mDisconnectedTimes:" + this.recoverField_l);
         if (this.recoverField_l == 0) {
            this.recoverMethod_n_9b75();
         } else {
            int var1 = -1;

            try {
               var1 = RecoverTypeaf2b07c1.recoverMethod_c_f0554de6(this.recoverField_q.getApplicationContext());
            } catch (Throwable var4) {
            }

            int var2 = (int)(Math.pow(2.0, this.recoverField_l) * 3.0 * 1000.0);
            int var3 = this.recoverMethod_f_9b68();
            if (var2 > var3 * 1000 / 2) {
               var2 = var3 * 1000 / 2;
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "JCoreTCPManager",
               "[retryConnect] mDisconnectedTimes:" + this.recoverField_l + ",chargedLever:" + var1 + ",heartbeatInterval:" + var3 + ",delayTime:" + var2
            );
            if (RecoverTypeaf2aadab.recoverMethod_d_f0554df7(this.recoverField_q.getApplicationContext()) && this.recoverField_k) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "tcp ssl retry connect times use up, use no ssl retry again.");
               RecoverTypeaf2aadab.recoverMethod_a_1385ff(false);
               this.recoverField_l = 0;
            }

            if (var1 == 1 ? this.recoverField_l < 30 : this.recoverField_l < 5) {
               if (!RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_a_134632(1011)) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "send MSG_RESTART_CONN");
                  RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(1011, var2, this.recoverField_t);
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Already has MSG_RESTART_CONN");
               }
            }
         }
      }
   }

   private synchronized void recoverMethod_n_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - restartNetworkingClient, pid:" + Process.myPid());
      if (!this.recoverField_p) {
         RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09("JCoreTCPManager", "need not keep tcp,next start app will re login");
      } else if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09("JCoreTCPManager", "No network connection. Give up to start connection thread.");
      } else if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_q, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCoreTCPManager", "[restartNetworkingClient] tcp has close by active");
      } else if (!RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_f0554df7(this.recoverField_q)) {
         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCoreTCPManager", "[restartNetworkingClient] sdk limit");
      } else if (!(Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_q, RecoverType5a690d1.recoverMethod_Q_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCoreTCPManager", "[restartNetworkingClient] tcp has close by ups.unregister");
      } else if (this.recoverField_j == 1005 || this.recoverField_j == 1006 || this.recoverField_j == 1008 || this.recoverField_j == 1009) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "[restartNetworkingClient] registerErrCode >0,registerErrCode:" + this.recoverField_j);
         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_q, 2002, this.recoverField_j, "");
      } else if (this.recoverField_i == 102) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("JCoreTCPManager", "login failed:102,give up start connection thread.reset from next app start");
      } else if (!this.recoverMethod_o_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "NetworkingClient is running");
      } else {
         RecoverTypeaf2aadb0 var1 = (RecoverTypeaf2aadb0)this.recoverField_h.get();
         if (var1 != null && var1.recoverMethod_d_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "socket is  connecting");
         } else {
            try {
               if (var1 != null) {
                  var1.recoverMethod_c_9b75();
                  this.recoverField_h.set(null);
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "start to new NetworkingClient");
               RecoverTypeaf2aadb0 var2 = new RecoverTypeaf2aadb0(this.recoverField_q.getApplicationContext());
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "start to connect =" + var2);
               var2.recoverMethod_b_9b75();
               this.recoverField_h.set(var2);
               RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(2001, 90000L, this.recoverField_t);
            } catch (Throwable var3) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "start throwable =" + var3);
            }
         }
      }
   }

   private boolean recoverMethod_o_9b79() {
      RecoverTypeaf2aadb0 var1 = (RecoverTypeaf2aadb0)this.recoverField_h.get();
      return var1 == null || var1.recoverMethod_e_c697b2f8() == null || !var1.recoverMethod_e_c697b2f8().recoverMethod_b_9b79();
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "handleStop...");
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "tcp already stoped");
      } else if (!RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_134632(0)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Action: handleStopPush - can't stop tcp");
      } else {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_af_48794abf().recoverMethod_a_4e9722a3(true));
         this.recoverMethod_a_1385ff(true);
         this.recoverMethod_l_9b75();
      }
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "handleUnRegister...");
      if (!(Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_Q_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "tcp already stoped");
      } else if (!RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_134632(0)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Action: handleUnRegister - can't stop tcp");
      } else {
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_Q_48794abf().recoverMethod_a_4e9722a3(false));
         this.recoverMethod_l_9b75();
      }
   }

   private void recoverMethod_d_f0554df3(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "handleResume...");
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_af_48794abf().recoverMethod_a_4e9722a3(false));
      this.recoverMethod_a_1385ff(false);
      if (this.recoverField_o) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "[handleResume] is loggedin");
      } else {
         this.recoverMethod_n_9b75();
      }
   }

   private boolean recoverMethod_p_9b79() {
      return this.recoverField_v > 0
         ? System.currentTimeMillis() - this.recoverField_n < (this.recoverField_v - 4) * 1000L
         : System.currentTimeMillis() - this.recoverField_n < 18000L;
   }

   private void recoverMethod_c_79e5e33f(Bundle var1) {
      if (this.recoverField_s.get()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "isBeating, skip this time");
      } else {
         boolean var2 = false;
         if (var1 != null) {
            var2 = var1.getBoolean("force", false);
         }

         if (!var2 && this.recoverMethod_p_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "No need to rtc, Because it have succeed recently");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Send heart beat");
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1005);
            if (this.recoverField_o) {
               this.recoverMethod_r_9b75();
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "socket is closed or push isn't login");
            }
         }
      }
   }

   private void recoverMethod_q_9b75() {
      boolean var1 = this.recoverField_s.get();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "check isBeating status=" + var1);
      if (var1) {
         this.recoverField_s.set(false);
         this.recoverField_m++;
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - onHeartbeatTimeout - timeoutTimes:" + this.recoverField_m);
         RecoverTypeaf2a9ae9.recoverMethod_b_e925fa09("JCoreTCPManager", "============================================================");
         if (this.recoverMethod_c_c6956df6() != null && !this.recoverField_o) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Is connecting now. Give up to retry.");
         } else if (this.recoverField_o && this.recoverField_m <= 1) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Already logged in. Give up to retry.");
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(1005, 5000L, this.recoverField_t);
         } else {
            this.recoverMethod_l_9b75();
            this.recoverMethod_m_9b75();
         }
      }
   }

   private void recoverMethod_r_9b75() {
      this.recoverField_s.set(true);
      RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1022);
      Long var1 = RecoverTypeaf2aadab.recoverMethod_c_f0554de7(this.recoverField_q);
      long var2 = RecoverType5a68d10.recoverMethod_f_f0554de7(this.recoverField_q);
      byte var4 = 1;
      int var5 = RecoverTypeaf2aa628.recoverField_a;
      int var6 = recoverField_e ? 1 : 0;
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("JCoreTCPManager", "heartbeat initPushSdk:" + var6);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "JCoreTCPManager", "heartbeat - juid:" + var2 + ", flag:" + var4 + " ,userType:" + var5 + " ,initPushSdk:" + var6
      );
      byte[] var7 = RecoverTypeaf2abcae.recoverMethod_a_d6eec457(var1, JConstants.tcpSessionId, var2, var4, var5, JConstants.DEFAULT_HEARTBEAT_INTERVAL, var6);
      byte[] var8 = RecoverTypeaf2abcae.recoverMethod_a_d9b22e23(this.recoverField_q, var7);
      if (var8 != null) {
         this.recoverMethod_c_c6956df6().recoverMethod_e_c697b2f8().recoverMethod_a_25e06ef(var8);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreTCPManager", "send hb failed:sendData is null");
      }

      RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(1022, 10000L, this.recoverField_t);
   }

   public void recoverMethod_a_79e5e33f(Bundle var1) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_q, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "[rtc] tcp has close by active");
      } else {
         boolean var2 = true;
         long var3 = 0L;
         if (var1 != null) {
            var2 = var1.getBoolean("force", true);
            var3 = var1.getLong("delay_time", 0L);
            if (var1.containsKey("heartbeat_time_out") || var1.containsKey("heartbeat_time_out_count")) {
               recoverField_f.set(var1.getLong("heartbeat_time_out", recoverField_d));
               recoverField_g.set(var1.getInt("heartbeat_time_out_count", recoverField_c));
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", " update heartbeatTimeOutCount=" + recoverField_g.get());
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", " update mHeartbeatTimeOut=" + recoverField_f.get());
            }
         }

         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", " current use heartbeatTimeOutCount=" + recoverField_g.get());
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", " current use  mHeartbeatTimeOut=" + recoverField_f.get());
         if (this.recoverMethod_o_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "[rtc] rtc restartNetworkingClient");
            this.recoverMethod_n_9b75();
         } else {
            if (var3 <= 0L) {
               this.recoverMethod_c_79e5e33f(var1);
            } else {
               if (var2) {
                  RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1005);
                  RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1004);
               }

               RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(var2 ? 1004 : 1005, var3, this.recoverField_t);
            }

            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreTCPManager", "send rtc force=" + var2 + " delay=" + var3);
         }
      }
   }

   private void recoverMethod_s_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "Action - onHeartbeatSucceed");
      RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_q, 19, 0, "ack success");
   }

   public void recoverMethod_b_79e5e33f(Bundle var1) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_q, RecoverType5a690d1.recoverMethod_af_48794abf())) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreTCPManager", "[netWorkChanged] tcp has close by active");
      } else {
         RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1006);
         RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(1007);
         boolean var2 = var1.getBoolean("connected", false);
         if (var2) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Handle connected state.");
            if (this.recoverMethod_o_9b79()) {
               this.recoverMethod_n_9b75();
            } else {
               RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(1006, 3000L, this.recoverField_t);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "Handle disconnected state.");
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(1007, 3000L, this.recoverField_t);
         }
      }
   }

   public void recoverMethod_a_be7fc169(JSONObject var1) {
      try {
         if (var1 != null) {
            this.recoverField_u = var1.optBoolean("use_ssp");
            if (this.recoverField_u) {
               int var2 = var1.optInt("hb_interval");
               if (var2 <= 0) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreTCPManager", "hb_interval is less than 0, will use 15s");
                  this.recoverField_v = 15;
               } else if (var2 >= JConstants.DEFAULT_HEARTBEAT_INTERVAL / 2) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                     "JCoreTCPManager",
                     "hb_interval is more than " + JConstants.DEFAULT_HEARTBEAT_INTERVAL + ", will use " + JConstants.DEFAULT_HEARTBEAT_INTERVAL + "s"
                  );
                  this.recoverField_v = JConstants.DEFAULT_HEARTBEAT_INTERVAL;
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreTCPManager", "set ssp heartbeat interval:" + var2);
                  this.recoverField_v = var2;
               }

               if (this.recoverField_v > 0) {
                  this.recoverMethod_i_9b75();
               }
            }
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreTCPManager", "[TcpManager] handleCtrlHeartBeatCmd error:" + var3);
      }
   }

   public void recoverMethod_e_9b75() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "updateHeartBeatInterval  -- current HeartBeatInterval = " + this.recoverField_w);
      if (this.recoverField_v > 0) {
         if (this.recoverField_w == 0) {
            this.recoverField_w = this.recoverField_v * 2;
         } else if (this.recoverField_w * 2 < JConstants.DEFAULT_HEARTBEAT_INTERVAL / 2) {
            this.recoverField_w *= 2;
         } else {
            this.recoverField_w = JConstants.DEFAULT_HEARTBEAT_INTERVAL;
         }
      }
   }

   public int recoverMethod_f_9b68() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "getHeartBeatInterval  -- current HeartBeatInterval = " + this.recoverField_w);
      if (this.recoverField_v > 0) {
         return this.recoverField_w == 0 ? this.recoverField_v : this.recoverField_w;
      } else {
         return JConstants.DEFAULT_HEARTBEAT_INTERVAL;
      }
   }

   public int recoverMethod_g_9b68() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "getNextHbIntervalForLog  -- current HeartBeatInterval = " + this.recoverField_w);
      if (this.recoverField_v > 0) {
         if (this.recoverField_w == 0) {
            return this.recoverField_v * 2 < JConstants.DEFAULT_HEARTBEAT_INTERVAL / 2 ? this.recoverField_v * 2 : JConstants.DEFAULT_HEARTBEAT_INTERVAL;
         } else {
            return this.recoverField_w * 2 < JConstants.DEFAULT_HEARTBEAT_INTERVAL / 2 ? this.recoverField_w * 2 : JConstants.DEFAULT_HEARTBEAT_INTERVAL;
         }
      } else {
         return JConstants.DEFAULT_HEARTBEAT_INTERVAL;
      }
   }

   public int recoverMethod_h_9b68() {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "getAlarmDelay  -- current HeartBeatInterval = " + this.recoverField_w);
      if (this.recoverField_v > 0) {
         return this.recoverField_w == 0 ? this.recoverField_v + 5 : this.recoverField_w + 5;
      } else {
         return JConstants.DEFAULT_ALARM_DELAY;
      }
   }

   public void recoverMethod_i_9b75() {
      this.recoverField_w = 0;
      RecoverTypeaf2ab530.recoverMethod_a_c6965676().recoverMethod_b_9b75();
   }

   private void recoverMethod_a_1385ff(boolean var1) {
      RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), 2011, var1 ? 0 : 1, "");
   }

   class Member8f5231ae extends RecoverTypeaf2b0401 {
      private Context recoverField_b;
      private String recoverField_c;
      private Bundle recoverField_d;

      public Member8f5231ae(Context var2, String var3, Bundle var4) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_h = "TcpManager";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            if (this.recoverField_c.equals("tcp_a1")) {
               RecoverTypeaf2af8c1.recoverMethod_a_9b75();
               RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_b, "tcp_a1", null);
            }

            if (this.recoverField_c.equals("tcp_a1") || this.recoverField_c.equals("tcp_a28")) {
               boolean var18 = RecoverTypeaf2ab531.recoverField_e;
               this.recoverMethod_b_9b75();
               if (RecoverTypeaf2ab531.this.recoverMethod_o_9b79()) {
                  RecoverTypeaf2ab531.this.recoverMethod_n_9b75();
               } else if (var18 != RecoverTypeaf2ab531.recoverField_e && RecoverTypeaf2ab531.this.recoverField_o) {
                  RecoverTypeaf2ab531.this.recoverMethod_r_9b75();
               }
            } else if (!this.recoverField_c.equals("tcp_a3") && !this.recoverField_c.equals("tcp_a4") && !this.recoverField_c.equals("tcp_a20")) {
               if (this.recoverField_c.equals("tcp_a5")) {
                  if (this.recoverField_d != null) {
                     byte[] var17 = this.recoverField_d.getByteArray("body");
                     int var19 = this.recoverField_d.getInt("cmd", -1);
                     int var20 = this.recoverField_d.getInt("ver", -1);
                     long var21 = this.recoverField_d.getLong("rid", -1L);
                     String var22 = this.recoverField_d.getString("sdk_type");
                     long var23 = this.recoverField_d.getLong("timeout");
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                        "JCoreTCPManager", "send quest,cmd:" + var19 + ",ver:" + var20 + ",rid:" + var21 + ",body size:" + var17.length
                     );
                     if (var19 < 0 || var20 < 0 || var21 < 0L || TextUtils.isEmpty(var22)) {
                        return;
                     }

                     RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_9e326362(this.recoverField_b, var21, var19, var20, var17, var22, var23);
                  }
               } else if (this.recoverField_c.equals("tcp_a19")) {
                  int var12 = 0;
                  if (this.recoverField_d != null) {
                     RecoverTypeaf2ab531.this.recoverField_k = this.recoverField_d.getBoolean("connect_state", false);
                     var12 = this.recoverField_d.getInt("connect_instance_id", var12);
                  }

                  RecoverTypeaf2ab531.this.recoverMethod_b_13462e(var12);
               } else if (this.recoverField_c.equals("tcp_a11")) {
                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09(
                     "JCoreTCPManager", "resgiter success:" + RecoverType5a68d10.recoverMethod_b_e501eb0d(this.recoverField_b)
                  );
                  RecoverTypeaf2b07c1.recoverMethod_f_f0554df3(this.recoverField_b);
                  RecoverTypeaf2ab52c.recoverMethod_a_c69655fa()
                     .recoverMethod_a_4abe089d(this.recoverField_b, 0, 0, RecoverType5a68d10.recoverMethod_b_e501eb0d(this.recoverField_b));
                  RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_b, "on_register", null);
               } else if (this.recoverField_c.equals("tcp_a10")) {
                  RecoverTypeaf2ab531.this.recoverMethod_j_9b75();
               } else if (this.recoverField_c.equals("tcp_a9")) {
                  RecoverTypeaf2ab531.this.recoverMethod_d_f0554df3(this.recoverField_b);
               } else if (this.recoverField_c.equals("tcp_a8")) {
                  RecoverTypeaf2ab531.this.recoverMethod_b_f0554df3(this.recoverField_b);
               } else if (this.recoverField_c.equals("tcp_a23")) {
                  RecoverTypeaf2ab531.this.recoverMethod_l_9b75();
               } else if (this.recoverField_c.equals("tcp_a2")) {
                  RecoverTypeaf2ab530.recoverMethod_a_c6965676().recoverMethod_a_1a552341(this.recoverField_b, false);
                  RecoverTypeaf2ab531.this.recoverMethod_a_79e5e33f(this.recoverField_d);
               } else if (this.recoverField_c.equals("tcp_a13")) {
                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "resgiter failed");
                  if (this.recoverField_d != null) {
                     int var13 = this.recoverField_d.getInt("resCode", 0);
                     RecoverTypeaf2ab531.this.recoverField_j = var13;
                     RecoverTypeaf2aadab.recoverMethod_a_1a54e370(this.recoverField_b, var13);
                  }
               } else if (this.recoverField_c.equals("tcp_a12")) {
                  RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreTCPManager", "login failed");
                  if (this.recoverField_d != null) {
                     int var14 = this.recoverField_d.getInt("resCode", 0);
                     RecoverTypeaf2ab531.this.recoverMethod_a_13462e(var14);
                  }
               } else if (this.recoverField_c.equals("tcp_a14")) {
                  RecoverTypeaf2ab531.this.recoverMethod_n_9b75();
               } else if (this.recoverField_c.equals("tcp_a15")) {
                  RecoverTypeaf2ab531.this.recoverMethod_b_79e5e33f(this.recoverField_d);
               } else if (this.recoverField_c.equals("tcp_a16")) {
                  RecoverTypeaf2ab531.this.recoverMethod_c_79e5e33f(this.recoverField_d);
               } else if (this.recoverField_c.equals("tcp_a17")) {
                  RecoverTypeaf2ab531.this.recoverMethod_q_9b75();
               } else if (this.recoverField_c.equals("tcp_a26")) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "check heartbeat status");
                  RecoverTypeaf2ab531.this.recoverMethod_q_9b75();
               } else if (this.recoverField_c.equals("tcp_a18")) {
                  RecoverTypeaf2ab531.this.recoverMethod_s_9b75();
               } else if (this.recoverField_c.equals("tcp_a6")) {
                  if (this.recoverField_d != null) {
                     long var15 = this.recoverField_d.getLong("rid", -1L);
                     if (var15 > 0L) {
                        RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_b_1a54e731(this.recoverField_b, var15);
                     }
                  }
               } else if (this.recoverField_c.equals("tcp_a7")) {
                  if (this.recoverField_d != null) {
                     long var16 = this.recoverField_d.getLong("rid", -1L);
                     if (var16 > 0L) {
                        RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_1a54e731(this.recoverField_b, var16);
                     }
                  }
               } else if (this.recoverField_c.equals("tcp_a21")) {
                  RecoverTypeaf2ab531.this.recoverMethod_k_9b79();
               } else if (this.recoverField_c.equals("tcp_a22")) {
                  RecoverTypeaf2ab531.this.recoverMethod_b_9b75();
               } else if (this.recoverField_c.equals("tcp_a23")) {
                  RecoverTypeaf2ab531.this.recoverMethod_c_f0554df3(this.recoverField_b);
               } else if (this.recoverField_c.equals("tcp_a24") || this.recoverField_c.equals("tcp_a25")) {
                  this.recoverMethod_b_9b75();
                  RecoverTypeaf2ab531.this.recoverMethod_l_9b75();
                  RecoverTypeaf2ab531.this.recoverField_j = 0;
                  RecoverTypeaf2ab531.this.recoverField_i = 0;
                  RecoverTypeaf2ab531.this.recoverMethod_n_9b75();
               }
            } else if (!RecoverTypeaf2ab531.this.recoverMethod_o_9b79()) {
               byte[] var1 = this.recoverField_d.getByteArray("body");
               int var2 = this.recoverField_d.getInt("cmd", -1);
               int var3 = this.recoverField_d.getInt("ver", -1);
               long var4 = this.recoverField_d.getLong("rid", -1L);
               String var6 = this.recoverField_d.getString("sdk_type");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JCoreTCPManager", "send data,cmd:" + var2 + ",ver:" + var3 + ",rid:" + var4 + ",body size:" + var1.length
               );
               if (var2 < 0 || var3 < 0 || var4 < 0L) {
                  return;
               }

               if (this.recoverField_c.equals("tcp_a3")) {
                  RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_57af07a2(this.recoverField_b, var4, var2, var3, var1, var6);
               } else {
                  long var7 = 0L;
                  if (this.recoverField_c.equals("tcp_a20")) {
                     var7 = this.recoverField_d.getLong("uid", 0L);
                     if (var7 == 0L) {
                        RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreTCPManager", "share response uid is 0");
                        return;
                     }
                  }

                  byte[] var9 = RecoverTypeaf2abcae.recoverMethod_a_7700bcb1(this.recoverField_b, var2, var3, var4, var1, var7);
                  int var10 = RecoverTypeaf2ab531.recoverMethod_a_c6965695()
                     .recoverMethod_c_c6956df6()
                     .recoverMethod_e_c697b2f8()
                     .recoverMethod_a_25e06ef(var9);
                  if (var10 != 0) {
                     RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(this.recoverField_b, 1500, "code:" + var10);
                  }
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "send data failed:tcp breaked,will restart");
               RecoverTypeaf2ab531.this.recoverMethod_n_9b75();
            }
         } catch (Throwable var11) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreTCPManager", "tcp action failed:" + var11.getMessage());
         }
      }

      private void recoverMethod_b_9b75() {
         boolean var1 = RecoverType5a68d10.recoverMethod_a_aad3b203("JPUSH");
         if (var1) {
            Integer var2 = RecoverType5a68d10.recoverMethod_n_7238765a(this.recoverField_b);
            if (null == var2 || var2 < 590) {
               RecoverTypeaf2ab531.recoverField_e = true;
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "isJPushCallUp: JPush version < 590, set isPushInit true");
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "isJPushCallUp: No JDispatchActionSdkType branch");
         }

         if (null != this.recoverField_d) {
            String var3 = this.recoverField_d.getString("jg_init_sdk_type", "");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreTCPManager", "isJPushCallUp: sdkType:" + var3);
            if ("JPUSH".equals(var3)) {
               RecoverTypeaf2ab531.recoverField_e = true;
            }
         }
      }
   }

   private static final class Member8f5231af {
      private static final RecoverTypeaf2ab531 recoverField_a = new RecoverTypeaf2ab531();
   }
}
