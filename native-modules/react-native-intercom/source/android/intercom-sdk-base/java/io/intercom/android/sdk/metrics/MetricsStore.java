package io.intercom.android.sdk.metrics;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.metrics.ops.OpsMetricObject;
import io.intercom.android.sdk.persistence.JsonStorage;
import io.intercom.android.sdk.persistence.JsonStorage$LoadFailureHandler;
import io.intercom.android.sdk.utilities.IoUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MetricsStore {
   private static final String METRICS_DIR = "intercomMetrics";
   private static final String OPS_METRICS_DIR = "intercomOpsMetrics";
   private static final int STORED_METRIC_LIMIT = 200;
   private static final int STORED_OP_METRIC_LIMIT = 100;
   private final Provider apiProvider;
   private final String installerPackageName;
   private final boolean isDebugBuild;
   private final Provider appConfigProvider;
   private final Executor executor;
   private final JsonStorage storage;
   private final File metricsDir;
   private final File opsMetricsDir;
   private final Twig twig;
   private final JsonStorage$LoadFailureHandler deleteOnFailureHandler = new MetricsStore$7(this);

   public MetricsStore(Context var1, Provider var2, Provider var3) {
      this(
         var2,
         var3,
         Executors.newSingleThreadExecutor(),
         new JsonStorage(new Gson()),
         new File(var1.getCacheDir(), "intercomMetrics"),
         new File(var1.getCacheDir(), "intercomOpsMetrics"),
         AppTypeDetector.isDebugBuild(var1),
         AppTypeDetector.getInstallerPackageName(var1),
         LumberMill.getLogger()
      );
   }

   @VisibleForTesting
   MetricsStore(Provider var1, Provider var2, Executor var3, JsonStorage var4, File var5, File var6, boolean var7, String var8, Twig var9) {
      this.apiProvider = var1;
      this.appConfigProvider = var2;
      this.executor = var3;
      this.storage = var4;
      this.metricsDir = var5;
      this.opsMetricsDir = var6;
      this.isDebugBuild = var7;
      this.installerPackageName = var8;
      this.twig = var9;
   }

   public void track(MetricObject var1) {
      if (!this.isDisabled()) {
         var1.addInstallerPackageName(this.installerPackageName).addIsDebugBuild(this.isDebugBuild);
         this.executor.execute(new MetricsStore$1(this, var1));
      }
   }

   public void track(OpsMetricObject var1) {
      if (!this.isDisabled()) {
         this.executor.execute(new MetricsStore$2(this, var1));
      }
   }

   private boolean isDisabled() {
      return !((AppConfig)this.appConfigProvider.get()).isMetricsEnabled();
   }

   private boolean hasReachedMetricStorageLimit() {
      return this.storage.getDirectoryFileCount(this.metricsDir) > 200;
   }

   private boolean hasReachedOpsMetricStorageLimit() {
      return this.storage.getDirectoryFileCount(this.opsMetricsDir) > 100;
   }

   private File getFile(MetricObject var1) {
      return new File(this.metricsDir, var1.getId() + ".json");
   }

   private File getFile(OpsMetricObject var1) {
      return new File(this.opsMetricsDir, var1.getId() + ".json");
   }

   public void loadAndSendMetrics() {
      this.executor.execute(new MetricsStore$3(this));
   }

   private void uploadMetrics(List var1, List var2) {
      ((Api)this.apiProvider.get()).sendMetrics(var1, var2, new MetricsStore$4(this, var1, var2));
   }

   @VisibleForTesting
   List loadMetrics() {
      List var1 = new ArrayList();
      this.storage.loadFilesInDirectory(this.metricsDir, MetricObject.class, new MetricsStore$5(this, var1), this.deleteOnFailureHandler);
      return var1;
   }

   @VisibleForTesting
   List loadOpsMetrics() {
      List var1 = new ArrayList();
      this.storage.loadFilesInDirectory(this.opsMetricsDir, OpsMetricObject.class, new MetricsStore$6(this, var1), this.deleteOnFailureHandler);
      return var1;
   }

   private static void copyNonNullItems(List var0, List var1) {
      for (Object var3 : var0) {
         if (var3 != null) {
            var1.add(var3);
         }
      }
   }

   @VisibleForTesting
   void deleteFilesFor(List var1, List var2) {
      for (java.lang.Object recoveredEntryElement_0_129 : var1) {
         MetricObject var4 = (MetricObject) recoveredEntryElement_0_129;
         IoUtils.safelyDelete(this.getFile(var4));
      }

      for (java.lang.Object recoveredEntryElement_0_133 : var2) {
         OpsMetricObject var6 = (OpsMetricObject) recoveredEntryElement_0_133;
         IoUtils.safelyDelete(this.getFile(var6));
      }
   }

   // $VF: synthetic method
   static boolean access$000(MetricsStore var0) {
      return var0.hasReachedMetricStorageLimit();
   }

   // $VF: synthetic method
   static File access$100(MetricsStore var0, MetricObject var1) {
      return var0.getFile(var1);
   }

   // $VF: synthetic method
   static JsonStorage access$200(MetricsStore var0) {
      return var0.storage;
   }

   // $VF: synthetic method
   static boolean access$300(MetricsStore var0) {
      return var0.hasReachedOpsMetricStorageLimit();
   }

   // $VF: synthetic method
   static File access$400(MetricsStore var0, OpsMetricObject var1) {
      return var0.getFile(var1);
   }

   // $VF: synthetic method
   static void access$500(MetricsStore var0, List var1, List var2) {
      var0.uploadMetrics(var1, var2);
   }

   // $VF: synthetic method
   static Executor access$600(MetricsStore var0) {
      return var0.executor;
   }

   // $VF: synthetic method
   static void access$700(List var0, List var1) {
      copyNonNullItems(var0, var1);
   }

   // $VF: synthetic method
   static Twig access$800(MetricsStore var0) {
      return var0.twig;
   }
}
