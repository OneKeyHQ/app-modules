package cn.jpush.android.asus;

import android.content.Context;
import android.os.Bundle;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.c.RecoverType2e4cca67;

public class RecoverTypefa94ae12 extends JThirdPlatFormInterface {
   @Override
   public void init(Context var1) {
      RecoverTypefa94ae11.recoverMethod_b_f0554df3(var1);
   }

   @Override
   public String getToken(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_g_e501eb0d(var1);
   }

   @Override
   public boolean isSupport(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_a_f0554df7(var1);
   }

   @Override
   public byte getRomType(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_d_f0554ddf(var1);
   }

   @Override
   public void register(Context var1, Bundle var2) {
      RecoverTypefa94ae11.recoverMethod_c_f0554df3(var1);
   }

   @Override
   public String getRomName() {
      return RecoverTypefa94ae11.recoverField_a;
   }

   @Override
   public String getAppkey(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_e_e501eb0d(var1);
   }

   @Override
   public String getAppId(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_f_e501eb0d(var1);
   }

   @Override
   public boolean isNeedClearToken(Context var1) {
      return RecoverTypefa94ae11.recoverMethod_h_f0554df7(var1);
   }

   @Override
   public void clearNotification(Context var1, int var2) {
   }

   @Override
   public void clearAllNotification(Context var1) {
   }

   @Override
   public void stopPush(Context var1) {
      RecoverType2e4cca67.recoverMethod_c_f0554df3(var1);
   }

   @Override
   public void resumePush(Context var1) {
      RecoverType2e4cca67.recoverMethod_b_f0554df3(var1);
   }
}
