package io.intercom.android.sdk.lightcompressor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Build.VERSION;
import android.provider.MediaStore.Downloads;
import android.provider.MediaStore.Video.Media;
import androidx.annotation.RequiresApi;
import io.intercom.android.sdk.lightcompressor.compressor.Compressor;
import io.intercom.android.sdk.lightcompressor.config.AppSpecificStorageConfiguration;
import io.intercom.android.sdk.lightcompressor.config.Configuration;
import io.intercom.android.sdk.lightcompressor.config.SaveLocation;
import io.intercom.android.sdk.lightcompressor.config.SharedStorageConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin._Assertions;
import kotlin.coroutines.Continuation;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JP\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0007J\b\u0010\u0019\u001a\u00020\tH\u0007JJ\u0010\u001a\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0002JJ\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\"2\u0006\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0082@¢\u0006\u0002\u0010$JQ\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010'\u001a\u0004\u0018\u00010\"2\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010(\u001a\u00020\"2\b\u0010)\u001a\u0004\u0018\u00010\u0010H\u0002¢\u0006\u0002\u0010*J(\u0010+\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010,\u001a\u00020\"2\u0006\u0010-\u001a\u00020\"2\u0006\u0010.\u001a\u00020&H\u0003J\u0018\u0010/\u001a\u00020\"2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u00100\u001a\u00020\u000eH\u0002J\u001f\u00101\u001a\u00020\"2\u0006\u00102\u001a\u00020\"2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0002¢\u0006\u0002\u00103R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u00064",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/VideoCompressor;",
         "",
         "<init>",
         "()V",
         "coroutineScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "job",
         "Lkotlinx/coroutines/Job;",
         "start",
         "",
         "context",
         "Landroid/content/Context;",
         "uris",
         "",
         "Landroid/net/Uri;",
         "isStreamable",
         "",
         "sharedStorageConfiguration",
         "Lio/intercom/android/sdk/lightcompressor/config/SharedStorageConfiguration;",
         "appSpecificStorageConfiguration",
         "Lio/intercom/android/sdk/lightcompressor/config/AppSpecificStorageConfiguration;",
         "configureWith",
         "Lio/intercom/android/sdk/lightcompressor/config/Configuration;",
         "listener",
         "Lio/intercom/android/sdk/lightcompressor/CompressionListener;",
         "cancel",
         "doVideoCompression",
         "configuration",
         "startCompression",
         "Lio/intercom/android/sdk/lightcompressor/video/Result;",
         "index",
         "",
         "srcUri",
         "destPath",
         "",
         "streamableFile",
         "(ILandroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/lightcompressor/config/Configuration;Lio/intercom/android/sdk/lightcompressor/CompressionListener;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "saveVideoFile",
         "Ljava/io/File;",
         "filePath",
         "videoName",
         "shouldSave",
         "(Landroid/content/Context;Ljava/lang/String;Lio/intercom/android/sdk/lightcompressor/config/SharedStorageConfiguration;Lio/intercom/android/sdk/lightcompressor/config/AppSpecificStorageConfiguration;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/Boolean;)Ljava/io/File;",
         "saveVideoInExternal",
         "videoFileName",
         "saveLocation",
         "videoFile",
         "getMediaPath",
         "uri",
         "validatedFileName",
         "name",
         "(Ljava/lang/String;Ljava/lang/Boolean;)Ljava/lang/String;",
         "intercom-sdk-lightcompressor_release"
   }
)
@SourceDebugExtension(
   "SMAP\nVideoCompressor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VideoCompressor.kt\nio/intercom/android/sdk/lightcompressor/VideoCompressor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,433:1\n1#2:434\n*E\n"
)
public final class VideoCompressor {
   @NotNull
   public static final VideoCompressor INSTANCE = new VideoCompressor();
   @NotNull
   private static final CoroutineScope coroutineScope = CoroutineScopeKt.MainScope();
   @Nullable
   private static Job job;

   private VideoCompressor() {
   }

   @JvmStatic
   @JvmOverloads
   public static final void start(
      @NotNull Context var0,
      @NotNull List var1,
      boolean var2,
      @Nullable SharedStorageConfiguration var3,
      @Nullable AppSpecificStorageConfiguration var4,
      @NotNull Configuration var5,
      @NotNull CompressionListener var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var5, "configureWith");
      Intrinsics.checkNotNullParameter(var6, "listener");
      boolean var7 = var3 == null || var4 == null;
      if (_Assertions.ENABLED && !var7) {
         String var10 = "Assertion failed";
         throw new AssertionError(var10);
      }

      var7 = var5.getVideoNames().size() == var1.size();
      if (_Assertions.ENABLED && !var7) {
         String var8 = "Assertion failed";
         throw new AssertionError(var8);
      }

      INSTANCE.doVideoCompression(var0, var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static void start$default(
      Context var0,
      List var1,
      boolean var2,
      SharedStorageConfiguration var3,
      AppSpecificStorageConfiguration var4,
      Configuration var5,
      CompressionListener var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 4) != 0) {
         var2 = false;
      }

      if ((var7 & 8) != 0) {
         var3 = null;
      }

      if ((var7 & 16) != 0) {
         var4 = null;
      }

      start(var0, var1, var2, var3, var4, var5, var6);
   }

   @JvmStatic
   public static final void cancel() {
      if (job != null) {
         DefaultImpls.cancel$default(job, (CancellationException)null, 1, null);
      }

      Compressor.INSTANCE.setRunning(false);
   }

   private final void doVideoCompression(
      Context var1,
      List var2,
      boolean var3,
      SharedStorageConfiguration var4,
      AppSpecificStorageConfiguration var5,
      Configuration var6,
      CompressionListener var7
   ) {
      ObjectRef var8 = new ObjectRef();
      int var9 = 0;

      for (int var10 = var2.size(); var9 < var10; var9++) {
         job = BuildersKt.launch$default(
            coroutineScope,
            Dispatchers.getIO(),
            null,
            new VideoCompressor$doVideoCompression$1(var1, var2, var9, var4, var5, var3, var6, var8, var7, null),
            2,
            null
         );
      }
   }

   private final Object startCompression(
      int var1, Context var2, Uri var3, String var4, String var5, Configuration var6, CompressionListener var7, Continuation var8
   ) {
      return BuildersKt.withContext(Dispatchers.getDefault(), new VideoCompressor$startCompression$2(var1, var2, var3, var4, var5, var6, var7, null), var8);
   }

   // $VF: synthetic method
   static Object startCompression$default(
      VideoCompressor var0,
      int var1,
      Context var2,
      Uri var3,
      String var4,
      String var5,
      Configuration var6,
      CompressionListener var7,
      Continuation var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 16) != 0) {
         var5 = null;
      }

      return var0.startCompression(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private final <E extends Throwable> File saveVideoFile(
      Context var1, String var2, SharedStorageConfiguration var3, AppSpecificStorageConfiguration var4, Boolean var5, String var6, Boolean var7
   ) throws E {
      if (var2 == null) {
         return null;
      }

      int var9 = 0;
      File var10 = new File(var2);
      if (var3 != null) {
         String var41 = INSTANCE.validatedFileName(var6, var5);
         SaveLocation var46 = var3.getSaveAt();
         String var47;
         switch (var46 == null ? -1 : VideoCompressor$WhenMappings.$EnumSwitchMapping$0[var46.ordinal()]) {
            case 1:
               var47 = Environment.DIRECTORY_DOWNLOADS;
               break;
            case 2:
               var47 = Environment.DIRECTORY_PICTURES;
               break;
            default:
               var47 = Environment.DIRECTORY_MOVIES;
         }

         String var42 = var47;
         if (VERSION.SDK_INT >= 29) {
            String var43 = var3.getSubFolderName() != null ? var42 + '/' + var3.getSubFolderName() : var42;
            if (Intrinsics.areEqual(var7, true)) {
               VideoCompressor var49 = INSTANCE;
               Intrinsics.checkNotNull(var43);
               var49.saveVideoInExternal(var1, var41, var43, var10);
               new File(var1.getFilesDir(), var41).delete();
               return new File("/storage/emulated/0/" + var43, var41);
            } else {
               return new File(var1.getFilesDir(), var41);
            }
         } else {
            File var13 = Environment.getExternalStoragePublicDirectory(var42);
            String var14 = var3.getSubFolderName() != null ? "" + var13 + '/' + var3.getSubFolderName() : var13.getPath();
            File var15 = new File(var14, var41);
            if (!var15.exists()) {
               try {
                  if (false) throw (IOException)null; // Source-only checked-exception analysis; javac emits no instruction.
                  File var48 = var15.getParentFile();
                  if (var48 != null) {
                     var48.mkdirs();
                  }
               } catch (IOException var36) {
                  var36.printStackTrace();
               }
            }

            if (Intrinsics.areEqual(var7, true)) {
               var var17 = var1.openFileOutput(var41, 0);
               Throwable var16 = null;

               try {
                  FileOutputStream var18 = var17;
                  int var19 = 0;
                  FileInputStream var20 = new FileInputStream(var10);
                  Throwable var21 = null;

                  try {
                     FileInputStream var22 = var20;
                     int var23 = 0;
                     byte[] var24 = new byte[4096];

                     while (true) {
                        int var25 = var22.read(var24);
                        if (var25 <= 0) {
                           Unit var45 = Unit.INSTANCE;
                           break;
                        }

                        var18.write(var24, 0, var25);
                     }
                  } catch (Throwable var37) {
                     var21 = var37;
                     throw var37;
                  } finally {
                     CloseableKt.closeFinally(var20, var21);
                  }

                  Unit var44 = Unit.INSTANCE;
               } catch (Throwable var39) {
                  var16 = var39;
                  throw (E)var39;
               } finally {
                  CloseableKt.closeFinally(var17, var16);
               }
            }

            return var15;
         }
      } else {
         String var11 = INSTANCE.validatedFileName(var6, var5);
         Intrinsics.checkNotNull(var4);
         String var12 = var4.getSubFolderName() != null ? var4.getSubFolderName() + '/' + var11 : var11;
         if (!new File("" + var1.getFilesDir() + '/' + var12).exists()) {
            File var10000 = new File("" + var1.getFilesDir() + '/' + var12).getParentFile();
            if (var10000 != null) {
               var10000.mkdirs();
            }
         }

         return new File(var1.getFilesDir(), var12);
      }
   }

   @RequiresApi(29)
   private final <E extends Throwable> void saveVideoInExternal(Context var1, String var2, String var3, File var4) throws E {
      ContentValues var6 = new ContentValues();
      ContentValues var7 = var6;
      int var8 = 0;
      var7.put("_display_name", var2);
      var7.put("mime_type", "video/mp4");
      var7.put("relative_path", var3);
      var7.put("is_pending", 1);
      ContentValues var5 = var6;
      Uri var51 = Media.getContentUri("external_primary");
      if (Intrinsics.areEqual(var3, Environment.DIRECTORY_DOWNLOADS)) {
         var51 = Downloads.EXTERNAL_CONTENT_URI;
      }

      Uri var52 = var1.getContentResolver().insert(var51, var5);
      if (var52 != null) {
         int var10 = 0;
         var var11 = var1.getContentResolver().openFileDescriptor(var52, "rw");
         Throwable var12 = null;

         try {
            ParcelFileDescriptor var13 = var11;
            int var14 = 0;
            if (var13 != null) {
               int var16 = 0;
               FileOutputStream var17 = new FileOutputStream(var13.getFileDescriptor());
               Throwable var18 = null;

               try {
                  FileOutputStream var19 = var17;
                  int var20 = 0;
                  FileInputStream var21 = new FileInputStream(var4);
                  Throwable var22 = null;

                  try {
                     FileInputStream var23 = var21;
                     int var24 = 0;
                     byte[] var25 = new byte[4096];

                     while (true) {
                        int var26 = var23.read(var25);
                        if (var26 <= 0) {
                           Unit var54 = Unit.INSTANCE;
                           break;
                        }

                        var19.write(var25, 0, var26);
                     }
                  } catch (Throwable var45) {
                     var22 = var45;
                     throw var45;
                  } finally {
                     CloseableKt.closeFinally(var21, var22);
                  }

                  Unit var53 = Unit.INSTANCE;
               } catch (Throwable var47) {
                  var18 = var47;
                  throw var47;
               } finally {
                  CloseableKt.closeFinally(var17, var18);
               }
            } else {
               Object var10000 = null;
            }
         } catch (Throwable var49) {
            var12 = var49;
            throw (E)var49;
         } finally {
            CloseableKt.closeFinally(var11, var12);
         }

         var5.clear();
         var5.put("is_pending", 0);
         var1.getContentResolver().update(var52, var5, null, null);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final <E extends Throwable> String getMediaPath(Context var1, Uri var2) throws E, java.io.FileNotFoundException {
      ContentResolver var3 = var1.getContentResolver();
      String[] var5 = new String[]{"_data"};
      String[] var4 = var5;
      Cursor var51 = null;
      boolean var32 = false /* VF: Semaphore variable */;

      String var52;
      label215: {
         String var25;
         try {
            var32 = true;
            var51 = var3.query(var2, var4, null, null, null);
            if (var51 == null) {
               throw new Exception();
            }

            int var7 = var51.getColumnIndexOrThrow("_data");
            var51.moveToFirst();
            var52 = var51.getString(var7);
            Intrinsics.checkNotNull(var52);
            var52 = var52;
            var32 = false;
            break label215;
         } catch (Exception var49) {
            int var9 = 0;
            String var10 = var1.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
            File var11 = new File(var10);
            InputStream var10000 = var3.openInputStream(var2);
            if (var10000 != null) {
               var var12 = var10000;
               Throwable var13 = null;

               try {
                  InputStream var14 = var12;
                  int var15 = 0;
                  FileOutputStream var16 = new FileOutputStream(var11);
                  Throwable var17 = null;

                  try {
                     FileOutputStream var18 = var16;
                     int var19 = 0;
                     byte[] var20 = new byte[4096];
                     int var21 = 0;

                     while (true) {
                        int var22 = var14.read(var20);
                        int var23 = var22;
                        int var24 = 0;
                        var21 = var23;
                        if (var22 <= 0) {
                           Unit var54 = Unit.INSTANCE;
                           break;
                        }

                        var18.write(var20, 0, var21);
                     }
                  } catch (Throwable var45) {
                     var17 = var45;
                     throw var45;
                  } finally {
                     CloseableKt.closeFinally(var16, var17);
                  }

                  Unit var53 = Unit.INSTANCE;
               } catch (Throwable var47) {
                  var13 = var47;
                  throw (E)var47;
               } finally {
                  CloseableKt.closeFinally(var12, var13);
               }
            }

            String var59 = var11.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(var59, "getAbsolutePath(...)");
            var25 = var59;
            var32 = false;
         } finally {
            if (var32) {
               if (var51 != null) {
                  var51.close();
               }
            }
         }

         if (var51 != null) {
            var51.close();
         }

         return var25;
      }

      var51.close();
      return var52;
   }

   private final String validatedFileName(String var1, Boolean var2) {
      String var3 = var2 != null && var2 ? var1 + "_temp" : var1;
      return !StringsKt.contains$default(var3, "mp4", false, 2, null) ? var3 + ".mp4" : var3;
   }

   @JvmStatic
   @JvmOverloads
   public static final void start(
      @NotNull Context var0,
      @NotNull List var1,
      boolean var2,
      @Nullable SharedStorageConfiguration var3,
      @NotNull Configuration var4,
      @NotNull CompressionListener var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var4, "configureWith");
      Intrinsics.checkNotNullParameter(var5, "listener");
      start$default(var0, var1, var2, var3, null, var4, var5, 16, null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void start(@NotNull Context var0, @NotNull List var1, boolean var2, @NotNull Configuration var3, @NotNull CompressionListener var4) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var3, "configureWith");
      Intrinsics.checkNotNullParameter(var4, "listener");
      start$default(var0, var1, var2, null, null, var3, var4, 24, null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void start(@NotNull Context var0, @NotNull List var1, @NotNull Configuration var2, @NotNull CompressionListener var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var2, "configureWith");
      Intrinsics.checkNotNullParameter(var3, "listener");
      start$default(var0, var1, false, null, null, var2, var3, 28, null);
   }

   // $VF: synthetic method
   public static final String access$getMediaPath(VideoCompressor var0, Context var1, Uri var2) throws java.io.FileNotFoundException {
      return var0.getMediaPath(var1, var2);
   }

   // $VF: synthetic method
   public static final File access$saveVideoFile(
      VideoCompressor var0,
      Context var1,
      String var2,
      SharedStorageConfiguration var3,
      AppSpecificStorageConfiguration var4,
      Boolean var5,
      String var6,
      Boolean var7
   ) {
      return var0.saveVideoFile(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static final Object access$startCompression(
      VideoCompressor var0, int var1, Context var2, Uri var3, String var4, String var5, Configuration var6, CompressionListener var7, Continuation var8
   ) {
      return var0.startCompression(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
