package io.intercom.android.sdk.utilities;

import io.intercom.android.sdk.logger.LumberMill;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\b\u0002\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u0004\u0018\u00010\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/utilities/MappedVersion;",
         "",
         "file",
         "Ljava/io/File;",
         "<init>",
         "(Ljava/io/File;)V",
         "unshared",
         "Ljava/util/concurrent/atomic/AtomicInteger;",
         "buffer",
         "Ljava/nio/MappedByteBuffer;",
         "getBuffer",
         "()Ljava/nio/MappedByteBuffer;",
         "buffer$delegate",
         "Lkotlin/Lazy;",
         "get",
         "",
         "incrementAndGet",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNativeCounterFallbackCoordinator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NativeCounterFallbackCoordinator.kt\nio/intercom/android/sdk/utilities/MappedVersion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,113:1\n1#2:114\n*E\n"
)
final class MappedVersion {
   @NotNull
   private static final MappedVersion$Companion Companion = new MappedVersion$Companion(null);
   @NotNull
   private final File file;
   @NotNull
   private final AtomicInteger unshared;
   @NotNull
   private final Lazy buffer$delegate;
   @Deprecated
   public static final int INT_BYTES = 4;

   public MappedVersion(@NotNull File var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "file");
      this.file = var1;
      this.unshared = new AtomicInteger();
      final io.intercom.android.sdk.utilities.MappedVersion recoveredExpressionCapture_1619_0 = this;
      this.buffer$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.utilities.MappedVersion.buffer_delegate$lambda$1(recoveredExpressionCapture_1619_0)));
   }

   private final MappedByteBuffer getBuffer() {
      Lazy var1 = this.buffer$delegate;
      return (MappedByteBuffer)var1.getValue();
   }

   public final int get() {
      MappedByteBuffer var10000 = this.getBuffer();
      return var10000 != null ? var10000.getInt(0) : this.unshared.incrementAndGet();
   }

   public final int incrementAndGet() {
      MappedByteBuffer var10000 = this.getBuffer();
      int var6;
      if (var10000 != null) {
         MappedByteBuffer var1 = var10000;
         int var2 = 0;
         int var3 = var1.getInt(0) + 1;
         int var4 = var3;
         int var5 = 0;
         var1.putInt(0, var4);
         var6 = var3;
      } else {
         var6 = this.unshared.incrementAndGet();
      }

      return var6;
   }

   private static final MappedByteBuffer buffer_delegate$lambda$1(MappedVersion var0) {
      MappedByteBuffer var1;
      try {
         File var10000 = var0.file.getParentFile();
         if (var10000 != null) {
            var10000.mkdirs();
         }

         RandomAccessFile var11 = new RandomAccessFile(var0.file, "rw");
         Throwable var2 = null;

         MappedByteBuffer var12;
         try {
            RandomAccessFile var3 = var11;
            int var4 = 0;
            if (var3.length() < 4L) {
               var3.setLength(4L);
            }

            var12 = var3.getChannel().map(MapMode.READ_WRITE, 0L, 4L);
         } catch (Throwable var8) {
            var2 = var8;
            throw var8;
         } finally {
            CloseableKt.closeFinally(var11, var2);
         }

         var1 = var12;
      } catch (Exception var10) {
         LumberMill.getLogger().w("Could not map the DataStore version counter (" + var10.getMessage() + "); every read will re-check disk.");
         var1 = null;
      }

      return var1;
   }
}
