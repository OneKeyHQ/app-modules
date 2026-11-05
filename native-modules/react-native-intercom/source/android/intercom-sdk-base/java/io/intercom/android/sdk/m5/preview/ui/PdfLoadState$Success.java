package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\n\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH×\u0003J\t\u0010\u000f\u001a\u00020\u0010H×\u0001J\t\u0010\u0011\u001a\u00020\u0012H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/ui/PdfLoadState$Success;",
         "Lio/intercom/android/sdk/m5/preview/ui/PdfLoadState;",
         "bitmaps",
         "",
         "Landroid/graphics/Bitmap;",
         "<init>",
         "(Ljava/util/List;)V",
         "getBitmaps",
         "()Ljava/util/List;",
         "component1",
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
public final class PdfLoadState$Success extends PdfLoadState {
   @NotNull
   private final List bitmaps;
   public static final int $stable;

   static { $stable = 8; }

   public PdfLoadState$Success(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "bitmaps");
      super(null);
      this.bitmaps = var1;
   }

   @NotNull
   public final List getBitmaps() {
      return this.bitmaps;
   }

   @NotNull
   public final List component1() {
      return this.bitmaps;
   }

   @NotNull
   public final PdfLoadState$Success copy(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "bitmaps");
      return new PdfLoadState$Success(var1);
   }

   // $VF: synthetic method
   public static PdfLoadState$Success copy$default(PdfLoadState$Success var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.bitmaps;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Success(bitmaps=" + this.bitmaps + ')';
   }

   @Override
   public int hashCode() {
      return this.bitmaps.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof PdfLoadState$Success)) {
         return false;
      }

      PdfLoadState$Success var2 = (PdfLoadState$Success)var1;
      return Intrinsics.areEqual(this.bitmaps, var2.bitmaps);
   }
}
