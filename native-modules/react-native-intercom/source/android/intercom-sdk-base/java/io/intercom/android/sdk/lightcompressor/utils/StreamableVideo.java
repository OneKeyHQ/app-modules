package io.intercom.android.sdk.lightcompressor.utils;

import android.util.Log;
import io.intercom.android.sdk.lightcompressor.data.AtomsKt;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\u000bJ\u0018\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0002J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0002J\u0018\u0010\u0015\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J \u0010\u0015\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/utils/StreamableVideo;",
         "",
         "<init>",
         "()V",
         "tag",
         "",
         "ATOM_PREAMBLE_SIZE",
         "",
         "start",
         "",
         "in",
         "Ljava/io/File;",
         "out",
         "convert",
         "infile",
         "Ljava/nio/channels/FileChannel;",
         "outfile",
         "safeClose",
         "",
         "closeable",
         "Ljava/io/Closeable;",
         "readAndFill",
         "buffer",
         "Ljava/nio/ByteBuffer;",
         "position",
         "",
         "intercom-sdk-lightcompressor_release"
   }
)
@SourceDebugExtension(
   "SMAP\nStreamableVideo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamableVideo.kt\nio/intercom/android/sdk/lightcompressor/utils/StreamableVideo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,220:1\n1#2:221\n*E\n"
)
public final class StreamableVideo {
   @NotNull
   public static final StreamableVideo INSTANCE = new StreamableVideo();
   @NotNull
   private static final String tag = "StreamableVideo";
   private static final int ATOM_PREAMBLE_SIZE = 8;

   private StreamableVideo() {
   }

   public final boolean start(@Nullable File var1, @NotNull File var2) throws IOException {
      Intrinsics.checkNotNullParameter(var2, "out");
      boolean var3 = false;
      FileInputStream var4 = null;
      FileOutputStream var5 = null;

      boolean var13;
      try {
         var4 = new FileInputStream(var1);
         FileChannel var6 = var4.getChannel();
         var5 = new FileOutputStream(var2);
         FileChannel var7 = var5.getChannel();
         Intrinsics.checkNotNull(var6);
         Intrinsics.checkNotNull(var7);
         boolean var8 = this.convert(var6, var7);
         boolean var9 = var8;
         int var10 = 0;
         var3 = var9;
         var13 = var8;
      } finally {
         this.safeClose(var4);
         this.safeClose(var5);
         if (!var3) {
            var2.delete();
         }
      }

      return var13;
   }

   private final <E extends Exception> boolean convert(FileChannel var1, FileChannel var2) throws IOException, E {
      ByteBuffer var3 = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
      int var4 = 0;
      long var5 = 0L;
      long var7 = 0L;
      ByteBuffer var9 = null;
      ByteBuffer var10 = null;
      long var11 = 0L;

      do {
         Intrinsics.checkNotNull(var3);
         if (!this.readAndFill(var1, var3)) {
            break;
         }

         var5 = NumbersUtilsKt.uInt32ToLong(var3.getInt());
         var4 = var3.getInt();
         if (var4 == AtomsKt.getFTYP_ATOM()) {
            int var13 = NumbersUtilsKt.uInt32ToInt(var5);
            var10 = ByteBuffer.allocate(var13).order(ByteOrder.BIG_ENDIAN);
            ((Buffer)var3).rewind();
            var10.put(var3);
            if (var1.read(var10) < var13 - 8) {
               break;
            }

            ((Buffer)var10).flip();
            var11 = var1.position();
         } else if (var5 == 1L) {
            ((Buffer)var3).clear();
            if (!this.readAndFill(var1, var3)) {
               break;
            }

            var5 = NumbersUtilsKt.uInt64ToLong(var3.getLong());
            var1.position(var1.position() + var5 - 16);
         } else {
            var1.position(var1.position() + var5 - 8);
         }

         if (var4 != AtomsKt.getFREE_ATOM()
            && var4 != AtomsKt.getJUNK_ATOM()
            && var4 != AtomsKt.getMDAT_ATOM()
            && var4 != AtomsKt.getMOOV_ATOM()
            && var4 != AtomsKt.getPNOT_ATOM()
            && var4 != AtomsKt.getSKIP_ATOM()
            && var4 != AtomsKt.getWIDE_ATOM()
            && var4 != AtomsKt.getPICT_ATOM()
            && var4 != AtomsKt.getUUID_ATOM()
            && var4 != AtomsKt.getFTYP_ATOM()) {
            Log.wtf("StreamableVideo", "encountered non-QT top-level atom (is this a QuickTime file?)");
            break;
         }
      } while (var5 >= 8L);

      if (var4 != AtomsKt.getMOOV_ATOM()) {
         Log.wtf("StreamableVideo", "last atom in file was not a moov atom");
         return false;
      }

      int var23 = NumbersUtilsKt.uInt32ToInt(var5);
      var7 = var1.size() - var23;
      var9 = ByteBuffer.allocate(var23).order(ByteOrder.BIG_ENDIAN);
      if (!this.readAndFill(var1, var9, var7)) {
         throw (E)new Exception("failed to read moov atom");
      }

      if (var9.getInt(12) == AtomsKt.getCMOV_ATOM()) {
         throw (E)new Exception("this utility does not support compressed moov atoms yet");
      }

      while (var9.remaining() >= 8) {
         int var14 = var9.position();
         var4 = var9.getInt(var14 + 4);
         if (var4 == AtomsKt.getSTCO_ATOM() || var4 == AtomsKt.getCO64_ATOM()) {
            var5 = NumbersUtilsKt.uInt32ToLong(var9.getInt(var14));
            if (var5 > var9.remaining()) {
               throw (E)new Exception("bad atom size");
            }

            ((Buffer)var9).position(var14 + 12);
            if (var9.remaining() < 4) {
               throw (E)new Exception("malformed atom");
            }

            int var15 = NumbersUtilsKt.uInt32ToInt(var9.getInt());
            if (var4 == AtomsKt.getSTCO_ATOM()) {
               Log.i("StreamableVideo", "patching stco atom...");
               if (var9.remaining() < var15 * 4) {
                  throw (E)new Exception("bad atom size/element count");
               }

               for (int var24 = 0; var24 < var15; var24++) {
                  int var25 = var9.getInt(var9.position());
                  int var18 = var25 + var23;
                  if (var25 < 0 && var18 >= 0) {
                     throw (E)new Exception(
                        "This is bug in original qt-faststart.c: stco atom should be extended to co64 atom as new offset value overflows uint32, but is not implemented."
                     );
                  }

                  var9.putInt(var18);
               }
            } else if (var4 == AtomsKt.getCO64_ATOM()) {
               Log.wtf("StreamableVideo", "patching co64 atom...");
               if (var9.remaining() < var15 * 8) {
                  throw (E)new Exception("bad atom size/element count");
               }

               for (int var16 = 0; var16 < var15; var16++) {
                  long var17 = var9.getLong(var9.position());
                  var9.putLong(var17 + var23);
               }
            }
         } else {
            ((Buffer)var9).position(var9.position() + 1);
         }
      }

      var1.position(var11);
      if (var10 != null) {
         Log.i("StreamableVideo", "writing ftyp atom...");
         ((Buffer)var10).rewind();
         var2.write(var10);
      }

      Log.i("StreamableVideo", "writing moov atom...");
      ((Buffer)var9).rewind();
      var2.write(var9);
      Log.i("StreamableVideo", "copying rest of file...");
      var1.transferTo(var11, var7 - var11, var2);
      return true;
   }

   private final void safeClose(Closeable var1) {
      if (var1 != null) {
         try {
            var1.close();
         } catch (IOException var3) {
            Log.wtf("StreamableVideo", "Failed to close file: ");
         }
      }
   }

   private final boolean readAndFill(FileChannel var1, ByteBuffer var2) throws IOException {
      ((Buffer)var2).clear();
      int var3 = var1.read(var2);
      ((Buffer)var2).flip();
      return var3 == var2.capacity();
   }

   private final boolean readAndFill(FileChannel var1, ByteBuffer var2, long var3) throws IOException {
      ((Buffer)var2).clear();
      int var5 = var1.read(var2, var3);
      ((Buffer)var2).flip();
      return var5 == var2.capacity();
   }
}
