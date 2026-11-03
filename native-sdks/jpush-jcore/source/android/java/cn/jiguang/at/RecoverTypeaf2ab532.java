package cn.jiguang.at;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.ar.RecoverTypeaf2aadaf;
import cn.jiguang.ar.RecoverTypeaf2aadb3;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.aw.RecoverTypeaf2ac06f;
import cn.jiguang.aw.RecoverTypeaf2ac070;
import cn.jiguang.ax.RecoverTypeaf2ac42f;
import cn.jiguang.ax.RecoverTypeaf2ac430;
import cn.jiguang.ay.RecoverTypeaf2ac7f1;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.io.Closeable;
import java.io.File;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2ab532 {
   private final AtomicBoolean recoverField_a = new AtomicBoolean(false);
   private final Map recoverField_b = new ConcurrentHashMap();
   private final Map recoverField_c = new ConcurrentHashMap();
   private static int recoverField_d = 0;
   private Map recoverField_e = new HashMap();
   private Map recoverField_f = new HashMap();

   public static RecoverTypeaf2ab532 recoverMethod_a_c69656b4() {
      return RecoverTypeaf2ab532.Member8f52356f.recoverField_a;
   }

   private RecoverTypeaf2ab532() {
   }

   private RecoverTypeaf2ab532.Member8f523571 recoverMethod_a_37b4a9a6(RecoverTypeaf2aadaf var1, int var2) {
      RecoverTypeaf2ab532.Member8f523571 var3 = null;
      if (var1 != null) {
         ConcurrentHashMap var4 = (ConcurrentHashMap)this.recoverField_c.get(var1);
         if (var4 != null) {
            var3 = (RecoverTypeaf2ab532.Member8f523571)var4.get(var2);
            var4.remove(var2);
         }
      }

      return var3;
   }

   public void recoverMethod_a_21e9879d(Context var1, RecoverTypeaf2ab532.Member8f523571 var2) {
      try {
         boolean var3 = var2.recoverField_h == null;
         if (var3) {
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_a_f0554df3(var1);
            var2.recoverField_h = this.recoverMethod_b_eafbb042(var1, var2.recoverField_e);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "tcp report begin=" + var2);
         }

         if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
            this.recoverMethod_a_1b47df06(var1, var2, -2);
            return;
         }

         if (var2.recoverField_h == null || var2.recoverField_h.isEmpty()) {
            this.recoverMethod_a_1b47df06(var1, var2, -1);
            return;
         }

         RecoverTypeaf2aadaf var4 = null;

         for (Object recoveryElement_95 : this.recoverField_b.keySet()) {
            RecoverTypeaf2aadaf var6 = (RecoverTypeaf2aadaf)recoveryElement_95;
            if (var2.recoverField_h.contains(var6)) {
               var4 = var6;
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "use exist ipPort=" + var4);
               break;
            }
         }

         if (var4 == null) {
            try {
               Iterator var9 = var2.recoverField_h.iterator();
               if (!var9.hasNext()) {
                  this.recoverMethod_a_1b47df06(var1, var2, -1);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "use next no has ipPort ");
                  return;
               }

               var4 = (RecoverTypeaf2aadaf)var9.next();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "use next ipPort=" + var4);
            } catch (Throwable var7) {
               this.recoverMethod_a_1b47df06(var1, var2, -1);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "use next no has ipPort err:" + var7.getMessage());
               return;
            }
         }

         var2.recoverField_g = var4;
         var2.recoverField_h.remove(var4);
         if (var3) {
            RecoverTypeaf2ac430.recoverMethod_a_c6982776()
               .recoverMethod_b_1a5239c(50000 + var2.recoverField_f, 15000L, new RecoverTypeaf2ab532.Member8f523570(var1, var2));
         }

         ConcurrentHashMap var10 = (ConcurrentHashMap)this.recoverField_c.get(var4);
         if (var10 == null) {
            var10 = new ConcurrentHashMap();
            this.recoverField_c.put(var4, var10);
         }

         var2.recoverField_i = false;
         var10.put(var2.recoverField_f, var2);
         this.recoverMethod_a_fec26df1(var1, var4, var2);
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "tcp upload e:" + var8);
      }
   }

   private boolean recoverMethod_a_fb05cc70(Context var1, Set var2) {
      return RecoverTypeaf2aadab.recoverMethod_d_f0554df7(var1) && (var2 == null || !var2.contains("sdk_jid") || var2.size() != 1);
   }

   private synchronized void recoverMethod_a_fec26df1(Context var1, RecoverTypeaf2aadaf var2, RecoverTypeaf2ab532.Member8f523571 var3) {
      Pair var4 = (Pair)this.recoverField_b.get(var2);
      RecoverTypeaf2ac06e var5 = var4 != null ? (RecoverTypeaf2ac06e)var4.second : null;
      boolean var6 = this.recoverMethod_a_fb05cc70(var1, var3.recoverField_e);
      if (var5 == null) {
         if (var6) {
            var5 = new RecoverTypeaf2ac06f(30720, 19);
         } else {
            var5 = new RecoverTypeaf2ac070(30720, 19);
         }

         int var7 = recoverMethod_b_9b68();
         this.recoverField_b.put(var2, new Pair(var7, var5));
         RecoverTypeaf2ab532.Member8f523570 var8 = new RecoverTypeaf2ab532.Member8f523570(var1, 1, var2);
         if (!var6 && RecoverTypeaf2aadab.recoverMethod_d_f0554df7(var1)) {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", var8);
         } else {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("ONCE_TASK", var8);
         }
      } else if (var5.recoverMethod_b_9b79()) {
         this.recoverMethod_a_6fb73d21(var1, (Integer)var4.first, var5, var2, var3);
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "closeSocket sockets:" + this.recoverField_b);

      for (Object recoveryElement_173 : this.recoverField_b.entrySet()) {
         Entry var3 = (Entry)recoveryElement_173;
         RecoverTypeaf2aadaf var4 = (RecoverTypeaf2aadaf)var3.getKey();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "closeSocket IpPort:" + var4);
         this.recoverMethod_a_ae476c4(var1, var4, -3);
      }
   }

   private void recoverMethod_a_6fb73d21(Context var1, int var2, RecoverTypeaf2ac06e var3, RecoverTypeaf2aadaf var4, RecoverTypeaf2ab532.Member8f523571 var5) {
      byte[] var6 = this.recoverMethod_a_3799a043(var1, var5.recoverField_c, var5.recoverField_a, var5.recoverField_f);
      if (var6 != null && var6.length != 0) {
         RecoverTypeaf2ac430.recoverMethod_a_c6982776()
            .recoverMethod_b_1a5239c(50000 + var5.recoverField_f, 15000L, new RecoverTypeaf2ab532.Member8f523570(var1, var5));
         var5.recoverField_i = true;
         int var7 = var3.recoverMethod_a_25e06ef(var6);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "TcpReporter",
            "send tcp data, len="
               + var6.length
               + ", data="
               + var5
               + ", use ssl: "
               + (var3 instanceof RecoverTypeaf2ac06f)
               + ", send "
               + (var7 == 0 ? "succeed" : "failed")
         );
         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd()
            .recoverMethod_b_3b99bddb(var3.recoverField_h + ":" + var3.recoverField_i, var5.recoverField_e.toString(), var6.length);
         RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(60000 + var2, 31000L, new RecoverTypeaf2ab532.Member8f523570(var1, var4));
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "package data failed, give up, data=" + var5);
         this.recoverMethod_a_1b47df06(var1, var5, -1);
      }
   }

   private void recoverMethod_a_ae476c4(Context var1, RecoverTypeaf2aadaf var2, int var3) {
      Pair var4 = (Pair)this.recoverField_b.remove(var2);
      int var5 = 0;
      if (var4 != null) {
         var5 = (Integer)var4.first;
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6((Closeable)var4.second);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "closeSocket final");
      }

      ConcurrentHashMap var6 = (ConcurrentHashMap)this.recoverField_c.remove(var2);
      if (var3 == 0 && !RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
         var3 = -2;
      }

      if (var6 != null && var6.size() > 0) {
         if (var3 == 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "socket(" + var5 + ") at " + var2 + " is disconnected, go on send waiting request");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
               "TcpReporter", "socket(" + var5 + ") at " + var2 + " is disconnected, finish waiting request, code=" + var3
            );
         }

         for (Object recoveryElement_230 : var6.values()) {
            RecoverTypeaf2ab532.Member8f523571 var8 = (RecoverTypeaf2ab532.Member8f523571)recoveryElement_230;
            if (var8 != null) {
               RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(50000 + var8.recoverField_f);
               if (var3 == 0) {
                  this.recoverMethod_a_21e9879d(var1, var8);
               } else {
                  RecoverTypeaf2ab532.Member8f523570 var9 = new RecoverTypeaf2ab532.Member8f523570(var1, 3, var8);
                  var9.recoverField_f = var3;
                  RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(var9);
               }
            }
         }
      } else if (var5 > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "socket(" + var5 + ") at " + var2 + " is disconnected, no task left");
      }
   }

   private void recoverMethod_a_1b47df06(Context var1, RecoverTypeaf2ab532.Member8f523571 var2, int var3) {
      if (var2.recoverField_g != null) {
         ConcurrentHashMap var4 = (ConcurrentHashMap)this.recoverField_c.get(var2.recoverField_g);
         if (var4 != null) {
            var4.remove(var2.recoverField_f);
         }
      }

      RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e(50000 + var2.recoverField_f);
      RecoverTypeaf2ab532.Member8f523570 var5 = new RecoverTypeaf2ab532.Member8f523570(var1, 3, var2);
      var5.recoverField_f = var3;
      RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(var5);
   }

   private void recoverMethod_a_20ff9034(Context var1, ByteBuffer var2, RecoverTypeaf2aadaf var3) {
      try {
         if (var2 == null || var2.array() == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "onReceive buffer is null");
            return;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "Received bytes - len:" + var2.array().length + ", ipPort: " + var3);
         if (var2.array().length != 19) {
            this.recoverMethod_b_ae476c4(var1, var3, 1);
            this.recoverMethod_a_ae476c4(var1, var3, -1);
            RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_c_3b99bddb(var3.toString(), "unknown_receive", var2.array().length);
            return;
         }

         byte var4 = var2.get(2);
         if (var4 != 1 && var4 != 0) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "wrong version " + var4);
            this.recoverMethod_b_ae476c4(var1, var3, 2);
            this.recoverMethod_a_ae476c4(var1, var3, -1);
            RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_c_3b99bddb(var3.toString(), "unknown_receive", var2.array().length);
         } else {
            short var5 = var2.getShort(15);
            short var6 = var2.getShort(17);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "onResult seqId=" + var5 + " code=" + var6);
            RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_13462e('썐' + var5);
            RecoverTypeaf2ab532.Member8f523571 var7 = this.recoverMethod_a_37b4a9a6(var3, var5);
            if (var7 != null) {
               if (var6 == 0) {
                  RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_c_3b99bddb(var3.toString(), var7.recoverField_e.toString(), var2.array().length);
                  RecoverTypeaf2ab532.Member8f523570 var8 = new RecoverTypeaf2ab532.Member8f523570(var1, 3, var7);
                  var8.recoverField_f = var6;
                  RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(var8);
               } else if (var6 == 401) {
                  this.recoverMethod_a_ae476c4(var1, var3, var6);
               } else {
                  this.recoverMethod_a_21e9879d(var1, var7);
               }
            }
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "tcp reporter onReceive err:" + var9);
      }
   }

   private void recoverMethod_a_89c001f(Context var1, RecoverTypeaf2aadaf var2) {
      try {
         if (var2 == null) {
            return;
         }

         Pair var3 = (Pair)this.recoverField_b.get(var2);
         if (var3 == null) {
            return;
         }

         RecoverTypeaf2ac06e var4 = (RecoverTypeaf2ac06e)var3.second;
         if (var4 == null || var4.recoverMethod_b_9b79()) {
            return;
         }

         String var5 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_d_782597e1(var2.recoverField_a, var2.recoverField_b, var4.recoverField_g);
         int var6 = var4.recoverMethod_a_afa300d7(var2.recoverField_a, var2.recoverField_b);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "start tcp report socket(" + var3.first + "):" + var2 + ", code: " + var6);
         if (var6 != 0) {
            this.recoverMethod_a_ae476c4(var1, var2, 0);
            RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_g_afa300e4(var5, var6);
         } else {
            RecoverTypeaf2ab532.Member8f523570 var7 = new RecoverTypeaf2ab532.Member8f523570(var1, 2, var2);
            RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(var7);
            RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_g_afa300e4(var5, 0);
            RecoverTypeaf2ab532.Member8f523570 var8 = new RecoverTypeaf2ab532.Member8f523570(var1, var2);

            while (!this.recoverField_a.get()) {
               try {
                  ByteBuffer var9 = var4.recoverMethod_a_d68a3c36();
                  this.recoverMethod_a_20ff9034(var1, var9, var2);
                  RecoverTypeaf2ac430.recoverMethod_a_c6982776().recoverMethod_b_1a5239c(60000 + (Integer)var3.first, 31000L, var8);
               } catch (RecoverTypeaf2ab52f var10) {
                  if (var10.recoverField_a != -997) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "recv failed with error:" + var10);
                  }
                  break;
               }
            }
         }

         this.recoverMethod_a_ae476c4(var1, var2, 0);
         if (this.recoverField_a.get()) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "Break receiving by wantStop");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TcpReporter", "disconnected");
         }
      } catch (Throwable var11) {
         RecoverTypeaf2a9ae9.recoverMethod_b_1765c98c("TcpReporter", "socket exception", var11);
      }
   }

   private void recoverMethod_b_89c001f(Context var1, RecoverTypeaf2aadaf var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "socket at " + var2 + " is connected, deal with waiting request");
      ConcurrentHashMap var3 = (ConcurrentHashMap)this.recoverField_c.get(var2);
      Pair var4 = (Pair)this.recoverField_b.get(var2);
      if (var4 != null) {
         if (var3 != null) {
            RecoverTypeaf2ac06e var5 = (RecoverTypeaf2ac06e)var4.second;
            if (var5 != null && var5.recoverMethod_b_9b79()) {
               for (Object recoveryElement_367 : var3.values()) {
                  RecoverTypeaf2ab532.Member8f523571 var7 = (RecoverTypeaf2ab532.Member8f523571)recoveryElement_367;
                  if (var7 != null && !var7.recoverField_i) {
                     this.recoverMethod_a_6fb73d21(var1, (Integer)var4.first, var5, var2, var7);
                  }
               }
            }
         }

         RecoverTypeaf2ac430.recoverMethod_a_c6982776()
            .recoverMethod_b_1a5239c(60000 + (Integer)var4.first, 31000L, new RecoverTypeaf2ab532.Member8f523570(var1, var2));
      }
   }

   public RecoverTypeaf2ab532.Member8f523571 recoverMethod_a_31c0f640(
      Context var1, JSONObject var2, byte[] var3, int var4, File var5, Set var6, ReportCallBack var7, String var8
   ) {
      RecoverTypeaf2ab532.Member8f523571 var9 = new RecoverTypeaf2ab532.Member8f523571();
      var9.recoverField_b = var2;
      var9.recoverField_a = var4;
      var9.recoverField_c = var3;
      var9.recoverField_d = var5;
      var9.recoverField_e = var6;
      var9.recoverField_f = recoverMethod_b_f0554de6(var1);
      var9.recoverField_j = var7;
      var9.recoverField_k = var8;
      return var9;
   }

   private byte[] recoverMethod_a_3799a043(Context var1, byte[] var2, int var3, int var4) {
      if (var2 != null && var2.length != 0 && var2.length <= 30683) {
         RecoverTypeaf2ac7f1 var5 = new RecoverTypeaf2ac7f1(var2.length + 37);
         var5.recoverMethod_b_13462e(0);
         var5.recoverMethod_a_13462e(1);
         long var6 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_9b72();
         var5.recoverMethod_a_1349ef(var6);
         long var8 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_B_48794abf());
         var5.recoverMethod_b_1349ef(var8);
         byte[] var10 = recoverMethod_a_40fc2425(var1, var8, var2);
         var5.recoverMethod_a_25e06fc(var10);
         var5.recoverMethod_a_1349ef(var3);
         var5.recoverMethod_b_13462e(var4);
         var5.recoverMethod_a_25e06fc(var2);
         var5.recoverMethod_b_255f295(var5.recoverMethod_a_9b68(), 0);
         return var5.recoverMethod_b_12d408();
      } else {
         return null;
      }
   }

   private static byte[] recoverMethod_a_40fc2425(Context var0, long var1, byte[] var3) {
      String var4 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_E_48794abf());
      if (TextUtils.isEmpty(var4)) {
         var4 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_F_48794abf());
      }

      String var5 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var4);
      String var6 = RecoverTypeaf2b07d5.recoverMethod_b_79834524(var3);
      byte[] var7 = RecoverTypeaf2b07d5.recoverMethod_c_afa28ebe(var1 + var5 + var6);
      if (var7 == null) {
         return new byte[16];
      }

      if (var7.length == 16) {
         return var7;
      }

      byte[] var8 = new byte[16];
      System.arraycopy(var3, 0, var8, 0, Math.min(var7.length, 16));
      return var8;
   }

   public static synchronized int recoverMethod_b_f0554de6(Context var0) {
      short var1 = 10000;
      Integer var2 = (Integer)RecoverType5a690d2.recoverMethod_b_413ed21(var0, RecoverType5a690d1.recoverMethod_ap_48794abf());
      if (var2 == null) {
         var2 = Math.abs(new SecureRandom().nextInt(var1));
      }

      var2 = (var2 + 1) % var1;
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ap_48794abf().recoverMethod_a_4e9722a3(var2));
      return var2;
   }

   private static synchronized int recoverMethod_b_9b68() {
      recoverField_d = (recoverField_d + 1) % 10000;
      return recoverField_d;
   }

   private void recoverMethod_b_ae476c4(Context var1, RecoverTypeaf2aadaf var2, int var3) {
      try {
         if (var2 == null) {
            return;
         }

         String var4;
         if (var2.recoverField_c != null) {
            var4 = var2.recoverField_c.getHostAddress();
         } else {
            var4 = var2.recoverField_a;
         }

         RecoverTypeaf2aadb3 var5 = new RecoverTypeaf2aadb3(var1);
         var5.recoverMethod_a_5fe03918(3, var4, var2.recoverField_b, RecoverType5a68d11.recoverMethod_b_f0554de7(var1), 0L, var3);
      } catch (Throwable var6) {
      }

      this.recoverField_e.put(var2, System.currentTimeMillis());
   }

   private LinkedHashSet recoverMethod_b_eafbb042(Context var1, Set var2) {
      LinkedHashSet var3 = new LinkedHashSet();
      String var4 = RecoverTypeaf2aa9e8.recoverMethod_d_73cf11cb();
      RecoverTypeaf2aadaf var5 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(var4);
      if (var5 != null && var5.recoverMethod_a_9b79()) {
         var3.add(var5);
      } else {
         Set var6 = this.recoverMethod_c_5367b5b(var1, var2);
         if (var6 != null) {
            for (Object recoveryElement_485 : var6) {
               RecoverTypeaf2aadaf var8 = (RecoverTypeaf2aadaf)recoveryElement_485;
               if (var8 != null && var8.recoverMethod_a_9b79()) {
                  var3.add(var8);
               }
            }
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "tcp report find urls=" + var3);

      try {
         Set var11 = this.recoverField_e.entrySet();
         Iterator var12 = var11.iterator();

         while (var12.hasNext()) {
            Entry var13 = (Entry)var12.next();
            if (System.currentTimeMillis() - (Long)var13.getValue() > 600000L) {
               var12.remove();
            }
         }

         for (Object recoveryElement_506 : this.recoverField_e.keySet()) {
            RecoverTypeaf2aadaf var9 = (RecoverTypeaf2aadaf)recoveryElement_506;
            var3.remove(var9);
         }
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("TcpReporter", "remove black tcp urls err", var10);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "tcp report find urls and remove black List Tcp Urls =" + var3);
      return var3;
   }

   private void recoverMethod_a_be7fc169(JSONObject var1) {
      if (var1 != null && var1.length() != 0) {
         try {
            HashMap var2 = new HashMap();
            Iterator var3 = var1.keys();

            while (var3.hasNext()) {
               String var4 = (String)var3.next();
               JSONArray var5 = var1.optJSONArray(var4);
               LinkedHashSet var6 = new LinkedHashSet();
               if (var5 != null) {
                  for (int var7 = 0; var7 < var5.length(); var7++) {
                     RecoverTypeaf2aadaf var8 = RecoverTypeaf2aadaf.recoverMethod_a_800e3061(var5.getString(var7));
                     if (var8 != null && var8.recoverMethod_a_9b79()) {
                        var6.add(var8);
                     }
                  }
               }

               var2.put(var4, var6);
            }

            if (!var2.isEmpty()) {
               this.recoverField_f = var2;
            }
         } catch (JSONException var9) {
         }
      }
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      if (this.recoverField_f == null || this.recoverField_f.isEmpty()) {
         String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_ay_48794abf());
         if (!TextUtils.isEmpty(var2)) {
            try {
               JSONObject var3 = new JSONObject(var2);
               this.recoverMethod_a_be7fc169(var3);
            } catch (JSONException var4) {
            }
         }
      }
   }

   private Set recoverMethod_c_5367b5b(Context var1, Set var2) {
      this.recoverMethod_c_f0554df3(var1);
      boolean var3 = this.recoverMethod_a_fb05cc70(var1, var2);
      String var4 = var3 ? "ssl" : "normal";
      if (var2 != null && !var2.isEmpty()) {
         Set var5 = null;

         for (Object recoveryElement_567 : var2) {
            String var7 = (String)recoveryElement_567;
            String var8 = this.recoverMethod_a_e7a3948b(var7, var3);
            Set var9 = (Set)this.recoverField_f.get(var8);
            if (var9 == null || var9.isEmpty()) {
               return (Set)this.recoverField_f.get(var4);
            }

            if (var5 == null) {
               var5 = var9;
            } else {
               var5.retainAll(var9);
            }

            if (var5.isEmpty()) {
               return (Set)this.recoverField_f.get(var4);
            }
         }

         return var5;
      } else {
         return (Set)this.recoverField_f.get(var4);
      }
   }

   private String recoverMethod_a_e7a3948b(String var1, boolean var2) {
      if (TextUtils.isEmpty(var1)) {
         return var2 ? "ssl" : "normal";
      }

      String var3;
      switch (var1) {
         case "android_awake2":
         case "android_awake":
         case "android_awake_target2":
         case "android_awake_target":
         case "awake":
         case "aa3":
         case "aat3":
            var3 = "awake";
            break;
         case "active_launch":
         case "active_terminate":
         case "active_user":
            var3 = "active_user";
            break;
         case "identify_account":
         case "detach_account":
         case "account":
            var3 = "account";
            break;
         case "android_notification_state":
            var3 = "android_notification_state";
            break;
         case "normal":
            var3 = var2 ? "ssl" : "normal";
            break;
         default:
            if (!this.recoverField_f.containsKey(var1)) {
               var3 = var2 ? "ssl" : "normal";
            } else {
               var3 = var1;
            }
      }

      return var3;
   }

   private static final class Member8f52356f {
      private static final RecoverTypeaf2ab532 recoverField_a = new RecoverTypeaf2ab532();
   }

   static class Member8f523570 extends RecoverTypeaf2ac42f implements Runnable {
      private Context recoverField_a;
      private int recoverField_d;
      private final Object recoverField_e;
      private int recoverField_f;

      Member8f523570(Context var1, Object var2) {
         this.recoverField_a = var1;
         this.recoverField_e = var2;
      }

      Member8f523570(Context var1, int var2, Object var3) {
         this.recoverField_a = var1;
         this.recoverField_d = var2;
         this.recoverField_e = var3;
      }

      @Override
      public void recoverMethod_a_c050048(Message var1) {
         if (var1.what >= 60000) {
            if (this.recoverField_e instanceof RecoverTypeaf2aadaf) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "time to idle=" + this.recoverField_e);
               RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_ae476c4(this.recoverField_a, (RecoverTypeaf2aadaf)this.recoverField_e, 0);
            }
         } else if (var1.what >= 50000 && this.recoverField_e instanceof RecoverTypeaf2ab532.Member8f523571) {
            RecoverTypeaf2ab532.Member8f523571 var2 = (RecoverTypeaf2ab532.Member8f523571)this.recoverField_e;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "onTimeout=" + var2);
            RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_37b4a9a6(var2.recoverField_g, var2.recoverField_f);
            RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_21e9879d(this.recoverField_a, var2);
         }
      }

      @Override
      public void run() {
         try {
            switch (this.recoverField_d) {
               case 1:
                  if (this.recoverField_e instanceof RecoverTypeaf2aadaf) {
                     RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_a_89c001f(this.recoverField_a, (RecoverTypeaf2aadaf)this.recoverField_e);
                  }
                  break;
               case 2:
                  if (this.recoverField_e instanceof RecoverTypeaf2aadaf) {
                     RecoverTypeaf2ab532.recoverMethod_a_c69656b4().recoverMethod_b_89c001f(this.recoverField_a, (RecoverTypeaf2aadaf)this.recoverField_e);
                  }
                  break;
               case 3:
                  if (this.recoverField_e instanceof RecoverTypeaf2ab532.Member8f523571) {
                     RecoverTypeaf2ab532.Member8f523571 var1 = (RecoverTypeaf2ab532.Member8f523571)this.recoverField_e;
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TcpReporter", "onResult, data=" + var1 + " code=" + this.recoverField_f);
                     RecoverTypeaf2aa26b.recoverMethod_a_77286e9d(
                        this.recoverField_a,
                        this.recoverField_f,
                        var1.recoverField_b,
                        var1.recoverField_c,
                        var1.recoverField_a,
                        var1.recoverField_d,
                        var1.recoverField_e,
                        var1.recoverField_j,
                        var1.recoverField_k
                     );
                  }
            }
         } catch (Throwable var2) {
         }
      }
   }

   public static class Member8f523571 {
      int recoverField_a;
      JSONObject recoverField_b;
      byte[] recoverField_c;
      File recoverField_d;
      Set recoverField_e;
      int recoverField_f = -1;
      RecoverTypeaf2aadaf recoverField_g;
      LinkedHashSet recoverField_h;
      boolean recoverField_i;
      transient ReportCallBack recoverField_j;
      transient String recoverField_k;

      @Override
      public String toString() {
         return "TcpReportData{types="
            + this.recoverField_e
            + ", seqId="
            + this.recoverField_f
            + ", ipPort="
            + this.recoverField_g
            + ", restUrls="
            + this.recoverField_h
            + '}';
      }
   }
}
