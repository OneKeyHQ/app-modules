package cn.jpush.android.w;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;

public class RecoverType2e4d157c {
   private String recoverField_a;
   private String recoverField_b;
   private int recoverField_c;
   private int recoverField_d;
   private Map recoverField_e;
   private Map recoverField_f;
   private Object recoverField_g;
   private boolean recoverField_h;
   private boolean recoverField_i = true;
   private boolean recoverField_j = false;
   private boolean recoverField_k = false;
   private X509TrustManager recoverField_l;
   private HostnameVerifier recoverField_m;

   public RecoverType2e4d157c(String var1) {
      this.recoverField_a = var1;
      this.recoverField_c = -1;
      this.recoverField_d = -1;
      this.recoverField_f = new HashMap();
   }

   public String recoverMethod_a_73cf11cb() {
      return this.recoverField_a;
   }

   public int recoverMethod_b_9b68() {
      return this.recoverField_c;
   }

   public int recoverMethod_c_9b68() {
      return this.recoverField_d;
   }

   public void recoverMethod_a_5d527811(Object var1) {
      this.recoverField_g = var1;
   }

   public boolean recoverMethod_d_9b79() {
      return this.recoverField_h;
   }

   public boolean recoverMethod_e_9b79() {
      return this.recoverField_k;
   }

   public X509TrustManager recoverMethod_f_ebfc5567() {
      return this.recoverField_l;
   }

   public HostnameVerifier recoverMethod_g_1a6e2dd9() {
      return this.recoverField_m;
   }

   public byte[] recoverMethod_h_12d408() {
      if (this.recoverField_g != null) {
         if (this.recoverField_g instanceof String) {
            if (!TextUtils.isEmpty((CharSequence)this.recoverField_g)) {
               return ((String)this.recoverField_g).getBytes();
            }
         } else if (this.recoverField_g instanceof byte[]) {
            return (byte[])this.recoverField_g;
         }
      }

      String var1 = RecoverType2e4d157e.recoverMethod_a_abb17b30(this.recoverField_e);
      return !TextUtils.isEmpty(var1) ? var1.getBytes() : null;
   }

   public void recoverMethod_a_e925fa09(String var1, String var2) {
      this.recoverField_f.put(var1, var2);
   }

   public Map recoverMethod_i_604efcea() {
      return this.recoverField_f;
   }

   public boolean recoverMethod_j_9b79() {
      return this.recoverField_i;
   }

   public boolean recoverMethod_k_9b79() {
      return this.recoverField_j;
   }

   public String recoverMethod_l_73cf11cb() {
      return this.recoverField_b;
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      this.recoverField_b = var1;
   }
}
