package cn.jpush.android.cache;

public class Key {
   String recoverField_a;
   String recoverField_b;
   Object recoverField_c;
   boolean recoverField_d;
   boolean recoverField_e;

   public Key file(String var1) {
      this.recoverField_a = var1;
      return this;
   }

   public Key name(String var1) {
      this.recoverField_b = var1;
      return this;
   }

   private Key recoverMethod_a_9de3f42d() {
      this.recoverField_d = true;
      return this;
   }

   private Key recoverMethod_b_9de3f42d() {
      this.recoverField_e = true;
      return this;
   }

   private Key(String var1, String var2, Object var3) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      if (var3 == null) {
         throw new IllegalArgumentException("default value can not be null");
      }

      this.recoverField_c = var3;
   }

   public Key set(Object var1) {
      this.recoverField_c = var1;
      return this;
   }

   public static Key ThirdPush_ClearFlag() {
      return new Key("cn.jpush.config", "third_push_clear_flag", "").recoverMethod_a_9de3f42d();
   }

   public static Key FCM_ClearFlag() {
      return new Key("cn.jpush.config", "fcm_clear_flag", "").recoverMethod_a_9de3f42d();
   }

   public static Key ThirdPush_RegID(byte var0) {
      return new Key("cn.jpush.config", "pluginPlatformRegid_v2" + var0, "").recoverMethod_a_9de3f42d();
   }

   public static Key ThirdPush_RegUpload(byte var0) {
      return new Key("cn.jpush.config", "pluginPlatformRegidupload" + var0, false).recoverMethod_a_9de3f42d();
   }

   public static Key PushVerCode() {
      return new Key("cn.jpush.config", "versionCode", 0).recoverMethod_a_9de3f42d();
   }

   public static Key BadgeCurNum() {
      return new Key("cn.jpush.config", "badgeCurNum", 0).recoverMethod_a_9de3f42d();
   }

   public static Key NotiSchedule() {
      return new Key("cn.jpush.config", "NotiSchedule", "").recoverMethod_a_9de3f42d();
   }

   public static Key NotiCancel() {
      return new Key("cn.jpush.config", "NotiCancel", "").recoverMethod_a_9de3f42d();
   }

   public static Key NotiShow() {
      return new Key("cn.jpush.config", "NotiShow", "").recoverMethod_a_9de3f42d();
   }

   public static Key BlackPagesLastReqTime() {
      return new Key("cn.jpush.config", "BLastReqTime", 0L).recoverMethod_a_9de3f42d();
   }

   public static Key BlackPagesInternalTime() {
      return new Key("cn.jpush.config", "BInternalTime", 21600L).recoverMethod_a_9de3f42d();
   }

   public static Key UPSRegister() {
      return new Key("cn.jpush.config", "upsRegister", true);
   }

   public static Key IsNotificationEnabledLastTime() {
      return new Key("cn.jpush.config", "isNotificationEnabledLastTime", -1).recoverMethod_a_9de3f42d();
   }

   public static Key SvrAniConfigSign() {
      return new Key("cn.jpush.config", "SvrAniConfigSign", "").recoverMethod_a_9de3f42d();
   }

   public static Key LocalAniConfigSign() {
      return new Key("cn.jpush.config", "LocalAniConfigSign", "").recoverMethod_a_9de3f42d();
   }

   public static Key PushStatusSyncTime() {
      return new Key("cn.jpush.config", "PushStatusSyncTime", 0L).recoverMethod_a_9de3f42d();
   }

   public static Key LocalAniConfigVersion() {
      return new Key("cn.jpush.config", "LocalAniConfigVersion", 0).recoverMethod_a_9de3f42d();
   }

   public static Key LastAniConfigReqTime() {
      return new Key("cn.jpush.config", "LastAniConfigReqTime", 0L).recoverMethod_a_9de3f42d();
   }

   public static Key LastAniConfigPath() {
      return new Key("cn.jpush.config", "LastAniConfigPath", "").recoverMethod_a_9de3f42d();
   }

   public static Key LastReportApiStatisticTime() {
      return new Key("cn.jpush.config", "LastReportApiStatisticTime", 0L).recoverMethod_a_9de3f42d();
   }

   public static Key AllowRunningProcess() {
      return new Key("cn.jpush.config", "AllowRunningProcess", false).recoverMethod_a_9de3f42d();
   }

   public static Key IgnoreLocalAllowRPConfig() {
      return new Key("cn.jpush.config", "IgnoreLocalAllowRPConfig", false).recoverMethod_a_9de3f42d();
   }

   public static Key isEnableUA() {
      return new Key("cn.jpush.config", "IsEnableUA", true).recoverMethod_a_9de3f42d();
   }

   public static Key registerType() {
      return new Key("cn.jpush.config", "r_type", 0).recoverMethod_a_9de3f42d();
   }

   public static Key JAppKey(String var0) {
      return new Key("cn.jpush.config", var0 + "j_key", "").recoverMethod_b_9de3f42d();
   }

   public static Key MAppKey(String var0) {
      return new Key("cn.jpush.config", var0 + "m_key", "").recoverMethod_b_9de3f42d();
   }

   public static Key MAppId(String var0) {
      return new Key("cn.jpush.config", var0 + "m_id", "").recoverMethod_b_9de3f42d();
   }

   public static Key MAppSecret(String var0) {
      return new Key("cn.jpush.config", var0 + "m_scrt", "").recoverMethod_b_9de3f42d();
   }
}
