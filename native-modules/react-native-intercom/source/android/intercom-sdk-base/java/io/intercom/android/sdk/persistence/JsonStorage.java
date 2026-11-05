package io.intercom.android.sdk.persistence;

import androidx.annotation.WorkerThread;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.IoUtils;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {
   private static final Twig TWIG = LumberMill.getLogger();
   private final Gson gson;

   public JsonStorage(Gson var1) {
      this.gson = var1;
   }

   @WorkerThread
   public void loadThenDelete(File var1, Class var2, JsonStorage$LoadHandler var3) {
      FileReader var4 = null;

      try {
         var4 = new FileReader(var1);
         var3.onLoad(this.gson.fromJson(var4, var2));
      } catch (Exception var9) {
      } finally {
         IoUtils.safelyDelete(var1);
         IoUtils.closeQuietly(var4);
      }
   }

   @WorkerThread
   public void loadFilesInDirectory(File var1, Class var2, JsonStorage$LoadHandler var3) {
      this.loadFilesInDirectory(var1, var2, var3, JsonStorage$LoadFailureHandler.NONE);
   }

   @WorkerThread
   public void loadFilesInDirectory(File var1, Class var2, JsonStorage$LoadHandler var3, JsonStorage$LoadFailureHandler var4) {
      FileReader var5 = null;
      File[] var6 = var1.listFiles();
      if (var6 != null) {
         List var7 = new ArrayList(var6.length);

         for (File var11 : var6) {
            try {
               var5 = new FileReader(var11);
               var7.add(this.gson.fromJson(var5, var2));
            } catch (Exception var16) {
               var4.onLoadFailed(var11, var16);
            } finally {
               IoUtils.closeQuietly(var5);
            }
         }

         var3.onLoad(var7);
      }
   }

   @WorkerThread
   public int getDirectoryFileCount(File var1) {
      String[] var2 = var1.list();
      return var2 == null ? 0 : var2.length;
   }

   @WorkerThread
   public void saveToFileAsJson(Object var1, File var2) {
      Writer var3 = null;

      try {
         if (var2.exists() && !var2.delete()) {
            throw new RuntimeException("Couldn't delete existing file at " + var2.getAbsolutePath());
         }

         File var4 = var2.getParentFile();
         if (var4 != null && !var4.exists() && !var4.mkdirs()) {
            throw new RuntimeException("Couldn't create missing parent dir at " + var4.getAbsolutePath());
         }

         var3 = new FileWriter(var2);
         this.gson.toJson(var1, var3);
      } catch (Exception var8) {
         TWIG.internal("Couldn't save file to disk: " + var8);
      } finally {
         IoUtils.closeQuietly(var3);
      }
   }
}
