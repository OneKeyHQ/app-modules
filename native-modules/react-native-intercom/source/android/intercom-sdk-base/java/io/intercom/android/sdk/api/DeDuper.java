package io.intercom.android.sdk.api;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeDuper {
   private static final String EMPTY_JSON = "{}";
   private static final String CUSTOM_ATTRIBUTES = "custom_attributes";
   private final Map cachedAttributes = new HashMap();
   private final Provider appConfigProvider;
   private final DeDuperStore store;
   private final Gson gson = new Gson();
   private long earliestUpdateAt = 0L;
   private boolean hydrated = false;

   public DeDuper(Provider var1, DeDuperStore var2) {
      this.appConfigProvider = var1;
      this.store = var2;
   }

   public synchronized void update(Map var1) {
      this.ensureHydrated();
      if (this.hasExpiredCache()) {
         this.cachedAttributes.clear();
         this.setEarliestUpdateAt(TimeProvider.SYSTEM.currentTimeMillis());
      }

      if (this.earliestUpdateAt == 0L) {
         this.setEarliestUpdateAt(TimeProvider.SYSTEM.currentTimeMillis());
      }

      Map var2 = getCustomAttributes(this.cachedAttributes);
      Map var3 = getCustomAttributes(var1);
      this.cachedAttributes.putAll(var1);
      if (var2 != null && var3 != null) {
         this.cachedAttributes.put("custom_attributes", mergeMaps(var2, var3));
      }

      this.persistCachedAttributes();
   }

   public synchronized boolean shouldUpdateUser(Map var1) {
      this.ensureHydrated();
      return !var1.isEmpty() && !containsOnlyEmptyCustomAttributes(var1) ? this.hasNewAttributeValues(var1) || this.isEmpty() || this.hasExpiredCache() : false;
   }

   private static boolean containsOnlyEmptyCustomAttributes(Map var0) {
      return var0.size() == 1 && Collections.EMPTY_MAP.equals(var0.get("custom_attributes"));
   }

   public synchronized void reset() {
      this.ensureHydrated();
      this.cachedAttributes.clear();
      this.earliestUpdateAt = 0L;
      this.persistCachedAttributes();
   }

   public synchronized void readPersistedCachedAttributes() {
      this.hydrated = true;
      String var1 = this.store.getCachedAttributes();
      long var2 = this.store.getEarliestUpdateAt();
      if (!"{}".equals(var1) || var2 != 0L) {
         Map var4;
         try {
            var4 = (Map<String, Object>)this.gson.fromJson(var1, Map.class);
         } catch (JsonSyntaxException var6) {
            return;
         }

         if (var4 == null) {
            return;
         }

         this.cachedAttributes.putAll(var4);
         this.earliestUpdateAt = var2;
      }
   }

   private void ensureHydrated() {
      if (!this.hydrated) {
         this.readPersistedCachedAttributes();
      }
   }

   private void persistCachedAttributes() {
      Map var1 = new HashMap(this.cachedAttributes);
      this.store.save(this.gson.toJson(var1), this.earliestUpdateAt);
   }

   private boolean isEmpty() {
      return this.cachedAttributes.isEmpty();
   }

   private boolean hasExpiredCache() {
      if (this.earliestUpdateAt == 0L) {
         return false;
      }

      long var1 = ((AppConfig)this.appConfigProvider.get()).getUserUpdateCacheMaxAgeMs();
      return TimeProvider.SYSTEM.currentTimeMillis() - this.earliestUpdateAt > var1;
   }

   private boolean hasNewAttributeValues(Map var1) {
      Map var2 = getCustomAttributes(this.cachedAttributes);
      Map var3 = getCustomAttributes(var1);
      if (var3 != null) {
         if (var2 == null) {
            return true;
         }

         for (java.lang.Object recoveredEntryElement_0_120 : var3.entrySet()) {
            Entry var5 = (Entry) recoveredEntryElement_0_120;
            Object var6 = var5.getValue();
            if (var6 != null && !var6.equals(var2.get(var5.getKey()))) {
               return true;
            }
         }
      }

      for (java.lang.Object recoveredEntryElement_1_128 : var1.entrySet()) {
         Entry var8 = (Entry) recoveredEntryElement_1_128;
         if (!"custom_attributes".equals(var8.getKey())) {
            Object var9 = var8.getValue();
            if (var9 != null && !var9.equals(this.cachedAttributes.get(var8.getKey()))) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   private static Map getCustomAttributes(Map var0) {
      Object var1 = var0.get("custom_attributes");
      return var1 instanceof Map ? (Map)var1 : null;
   }

   private static Map mergeMaps(Map var0, Map var1) {
      Map var2 = new HashMap(var0);
      var2.putAll(var1);
      return var2;
   }

   @VisibleForTesting
   Map getMap() {
      return this.cachedAttributes;
   }

   @VisibleForTesting
   void setEarliestUpdateAt(long var1) {
      this.earliestUpdateAt = var1;
   }

   @VisibleForTesting
   long getEarliestUpdateAt() {
      return this.earliestUpdateAt;
   }
}
