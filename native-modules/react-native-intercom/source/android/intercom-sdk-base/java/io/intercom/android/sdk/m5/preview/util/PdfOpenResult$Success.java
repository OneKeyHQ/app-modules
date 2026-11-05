package io.intercom.android.sdk.m5.preview.util;

import android.os.ParcelFileDescriptor;
import androidx.compose.runtime.internal.StabilityInferred;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/util/PdfOpenResult$Success;",
         "Lio/intercom/android/sdk/m5/preview/util/PdfOpenResult;",
         "fileDescriptor",
         "Landroid/os/ParcelFileDescriptor;",
         "tempFile",
         "Ljava/io/File;",
         "<init>",
         "(Landroid/os/ParcelFileDescriptor;Ljava/io/File;)V",
         "getFileDescriptor",
         "()Landroid/os/ParcelFileDescriptor;",
         "getTempFile",
         "()Ljava/io/File;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class PdfOpenResult$Success extends PdfOpenResult {
   @NotNull
   private final ParcelFileDescriptor fileDescriptor;
   @Nullable
   private final File tempFile;
   public static final int $stable;

   static { $stable = 8; }

   public PdfOpenResult$Success(@NotNull ParcelFileDescriptor var1, @Nullable File var2) {
      Intrinsics.checkNotNullParameter(var1, "fileDescriptor");
      super(null);
      this.fileDescriptor = var1;
      this.tempFile = var2;
   }

   @NotNull
   public final ParcelFileDescriptor getFileDescriptor() {
      return this.fileDescriptor;
   }

   @Nullable
   public final File getTempFile() {
      return this.tempFile;
   }

   @NotNull
   public final ParcelFileDescriptor component1() {
      return this.fileDescriptor;
   }

   @Nullable
   public final File component2() {
      return this.tempFile;
   }

   @NotNull
   public final PdfOpenResult$Success copy(@NotNull ParcelFileDescriptor var1, @Nullable File var2) {
      Intrinsics.checkNotNullParameter(var1, "fileDescriptor");
      return new PdfOpenResult$Success(var1, var2);
   }

   // $VF: synthetic method
   public static PdfOpenResult$Success copy$default(PdfOpenResult$Success var0, ParcelFileDescriptor var1, File var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.fileDescriptor;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.tempFile;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Success(fileDescriptor=" + this.fileDescriptor + ", tempFile=" + this.tempFile + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.fileDescriptor.hashCode();
      return var1 * 31 + (this.tempFile == null ? 0 : this.tempFile.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof PdfOpenResult$Success)) {
         return false;
      }

      PdfOpenResult$Success var2 = (PdfOpenResult$Success)var1;
      return !Intrinsics.areEqual(this.fileDescriptor, var2.fileDescriptor) ? false : Intrinsics.areEqual(this.tempFile, var2.tempFile);
   }
}
