package io.intercom.android.sdk.metrics;

import com.google.gson.annotations.SerializedName;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricObject {
   private static final String KEY_ANDROID_INSTALLER_PACKAGE_NAME = "android_installer_package_name";
   private static final String KEY_ANDROID_IS_DEBUG_BUILD = "android_is_debug_build";
   static final String KEY_SDK_VERSION = "sdk_version";
   static final String KEY_APP_VERSION = "app_version";
   static final String KEY_APP_MIN_SDK_VERSION = "app_min_sdk_version";
   static final String KEY_APP_NAME = "app_name";
   static final String KEY_USER_ID = "user_id";
   static final String KEY_ACTION = "action";
   static final String KEY_OBJECT = "object";
   static final String KEY_PLACE = "place";
   static final String KEY_CONTEXT = "context";
   static final String KEY_OWNER = "owner";
   static final String KEY_CONVERSATION_SHOWN = "conversation_shown";
   private final Map metadata = new HashMap();
   private final String id;
   @SerializedName("created_at")
   private final long createdAt;
   private final String name;

   MetricObject(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      TimeProvider var9,
      String var10,
      String var11,
      String var12,
      String var13
   ) {
      this.name = var1;
      this.createdAt = TimeUnit.MILLISECONDS.toSeconds(var9.currentTimeMillis());
      this.id = var3;
      this.metadata.put("action", var5);
      this.metadata.put("object", var6);
      this.metadata.put("place", var7);
      this.metadata.put("context", var8);
      this.metadata.put("owner", var2);
      this.metadata.put("app_min_sdk_version", var12);
      this.metadata.put("app_name", var13);
      if (!var4.isEmpty()) {
         this.metadata.put("user_id", var4);
      }

      this.metadata.put("sdk_version", var11);
      this.metadata.put("app_version", var10);
   }

   MetricObject addMetaData(String var1, Object var2) {
      this.metadata.put(var1, var2);
      return this;
   }

   MetricObject addIsDebugBuild(boolean var1) {
      this.addMetaData("android_is_debug_build", var1);
      return this;
   }

   MetricObject addInstallerPackageName(String var1) {
      this.addMetaData("android_installer_package_name", var1);
      return this;
   }

   public String getId() {
      return this.id;
   }

   public Map getMetadata() {
      return this.metadata;
   }

   public String getName() {
      return this.name;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         MetricObject var2 = (MetricObject)var1;
         if (this.createdAt != var2.createdAt) {
            return false;
         } else if (!this.metadata.equals(var2.metadata)) {
            return false;
         } else {
            return this.id != null ? this.id.equals(var2.id) : var2.id == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.metadata.hashCode();
      var1 = 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
      return 31 * var1 + (int)(this.createdAt ^ this.createdAt >>> 32);
   }

   @Override
   public String toString() {
      return "MetricObject{metadata=" + this.metadata + ", id='" + this.id + '\'' + ", createdAt=" + this.createdAt + '}';
   }
}
