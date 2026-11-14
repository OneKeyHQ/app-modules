package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.blocks.lib.models.Block;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u00052\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0013H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\n¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/MediaItem;",
         "",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "isVideo",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Z)V",
         "getBlock",
         "()Lio/intercom/android/sdk/blocks/lib/models/Block;",
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
public final class MediaItem {
   @NotNull
   private final Block block;
   private final boolean isVideo;
   public static final int $stable;

   static { $stable = 8; }

   public MediaItem(@NotNull Block var1, boolean var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "block");
      this.block = var1;
      this.isVideo = var2;
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   public final boolean isVideo() {
      return this.isVideo;
   }

   @NotNull
   public final Block component1() {
      return this.block;
   }

   public final boolean component2() {
      return this.isVideo;
   }

   @NotNull
   public final MediaItem copy(@NotNull Block var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "block");
      return new MediaItem(var1, var2);
   }

   // $VF: synthetic method
   public static MediaItem copy$default(MediaItem var0, Block var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.block;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.isVideo;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "MediaItem(block=" + this.block + ", isVideo=" + this.isVideo + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.block.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isVideo);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof MediaItem)) {
         return false;
      }

      MediaItem var2 = (MediaItem)var1;
      return !Intrinsics.areEqual(this.block, var2.block) ? false : this.isVideo == var2.isVideo;
   }
}
