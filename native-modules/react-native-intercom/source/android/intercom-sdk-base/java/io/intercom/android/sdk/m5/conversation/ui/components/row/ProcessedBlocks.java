package io.intercom.android.sdk.m5.conversation.ui.components.row;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J)\u0010\r\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0012HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\t¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/ProcessedBlocks;",
         "",
         "cleaned",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "extracted",
         "<init>",
         "(Ljava/util/List;Ljava/util/List;)V",
         "getCleaned",
         "()Ljava/util/List;",
         "getExtracted",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
final class ProcessedBlocks {
   @NotNull
   private final List cleaned;
   @NotNull
   private final List extracted;

   public ProcessedBlocks(@NotNull List var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "cleaned");
      Intrinsics.checkNotNullParameter(var2, "extracted");
      this.cleaned = var1;
      this.extracted = var2;
   }

   @NotNull
   public final List getCleaned() {
      return this.cleaned;
   }

   @NotNull
   public final List getExtracted() {
      return this.extracted;
   }

   @NotNull
   public final List component1() {
      return this.cleaned;
   }

   @NotNull
   public final List component2() {
      return this.extracted;
   }

   @NotNull
   public final ProcessedBlocks copy(@NotNull List var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "cleaned");
      Intrinsics.checkNotNullParameter(var2, "extracted");
      return new ProcessedBlocks(var1, var2);
   }

   // $VF: synthetic method
   public static ProcessedBlocks copy$default(ProcessedBlocks var0, List var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.cleaned;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.extracted;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ProcessedBlocks(cleaned=" + this.cleaned + ", extracted=" + this.extracted + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.cleaned.hashCode();
      return var1 * 31 + this.extracted.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ProcessedBlocks)) {
         return false;
      }

      ProcessedBlocks var2 = (ProcessedBlocks)var1;
      return !Intrinsics.areEqual(this.cleaned, var2.cleaned) ? false : Intrinsics.areEqual(this.extracted, var2.extracted);
   }
}
