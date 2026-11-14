package io.intercom.android.sdk.m5.conversation.ui.components.row;

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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0006HÆ\u0003J#\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0011\u001a\u00020\u0012H×\u0001J\t\u0010\u0013\u001a\u00020\u0014H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/RevealedBlocks;",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "isCaughtUp",
         "",
         "<init>",
         "(Ljava/util/List;Z)V",
         "getBlocks",
         "()Ljava/util/List;",
         "()Z",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class RevealedBlocks {
   @NotNull
   private final List blocks;
   private final boolean isCaughtUp;
   public static final int $stable;

   static { $stable = 8; }

   public RevealedBlocks(@NotNull List var1, boolean var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "blocks");
      this.blocks = var1;
      this.isCaughtUp = var2;
   }

   @NotNull
   public final List getBlocks() {
      return this.blocks;
   }

   public final boolean isCaughtUp() {
      return this.isCaughtUp;
   }

   @NotNull
   public final List component1() {
      return this.blocks;
   }

   public final boolean component2() {
      return this.isCaughtUp;
   }

   @NotNull
   public final RevealedBlocks copy(@NotNull List var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "blocks");
      return new RevealedBlocks(var1, var2);
   }

   // $VF: synthetic method
   public static RevealedBlocks copy$default(RevealedBlocks var0, List var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.blocks;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.isCaughtUp;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "RevealedBlocks(blocks=" + this.blocks + ", isCaughtUp=" + this.isCaughtUp + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.blocks.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isCaughtUp);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof RevealedBlocks)) {
         return false;
      }

      RevealedBlocks var2 = (RevealedBlocks)var1;
      return !Intrinsics.areEqual(this.blocks, var2.blocks) ? false : this.isCaughtUp == var2.isCaughtUp;
   }
}
