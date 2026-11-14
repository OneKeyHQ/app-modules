package io.intercom.android.sdk.m5.conversation.ui.components.row;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001BG\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003JI\u0010\u0013\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000b¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/CategorizedBlocks;",
         "",
         "images",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "videos",
         "attachments",
         "remaining",
         "<init>",
         "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V",
         "getImages",
         "()Ljava/util/List;",
         "getVideos",
         "getAttachments",
         "getRemaining",
         "component1",
         "component2",
         "component3",
         "component4",
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
final class CategorizedBlocks {
   @NotNull
   private final List images;
   @NotNull
   private final List videos;
   @NotNull
   private final List attachments;
   @NotNull
   private final List remaining;

   public CategorizedBlocks(@NotNull List var1, @NotNull List var2, @NotNull List var3, @NotNull List var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "images");
      Intrinsics.checkNotNullParameter(var2, "videos");
      Intrinsics.checkNotNullParameter(var3, "attachments");
      Intrinsics.checkNotNullParameter(var4, "remaining");
      this.images = var1;
      this.videos = var2;
      this.attachments = var3;
      this.remaining = var4;
   }

   // $VF: synthetic method
   public CategorizedBlocks(List var1, List var2, List var3, List var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), (((var5 & 2) != 0) ? (List) (CollectionsKt.emptyList()) : var2), (((var5 & 4) != 0) ? (List) (CollectionsKt.emptyList()) : var3), (((var5 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4));
   }

   @NotNull
   public final List getImages() {
      return this.images;
   }

   @NotNull
   public final List getVideos() {
      return this.videos;
   }

   @NotNull
   public final List getAttachments() {
      return this.attachments;
   }

   @NotNull
   public final List getRemaining() {
      return this.remaining;
   }

   @NotNull
   public final List component1() {
      return this.images;
   }

   @NotNull
   public final List component2() {
      return this.videos;
   }

   @NotNull
   public final List component3() {
      return this.attachments;
   }

   @NotNull
   public final List component4() {
      return this.remaining;
   }

   @NotNull
   public final CategorizedBlocks copy(@NotNull List var1, @NotNull List var2, @NotNull List var3, @NotNull List var4) {
      Intrinsics.checkNotNullParameter(var1, "images");
      Intrinsics.checkNotNullParameter(var2, "videos");
      Intrinsics.checkNotNullParameter(var3, "attachments");
      Intrinsics.checkNotNullParameter(var4, "remaining");
      return new CategorizedBlocks(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static CategorizedBlocks copy$default(CategorizedBlocks var0, List var1, List var2, List var3, List var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.images;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.videos;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.attachments;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.remaining;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "CategorizedBlocks(images="
         + this.images
         + ", videos="
         + this.videos
         + ", attachments="
         + this.attachments
         + ", remaining="
         + this.remaining
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.images.hashCode();
      var1 = var1 * 31 + this.videos.hashCode();
      var1 = var1 * 31 + this.attachments.hashCode();
      return var1 * 31 + this.remaining.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CategorizedBlocks)) {
         return false;
      } else {
         CategorizedBlocks var2 = (CategorizedBlocks)var1;
         if (!Intrinsics.areEqual(this.images, var2.images)) {
            return false;
         } else if (!Intrinsics.areEqual(this.videos, var2.videos)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.attachments, var2.attachments) ? false : Intrinsics.areEqual(this.remaining, var2.remaining);
         }
      }
   }

   public CategorizedBlocks() {
      this(null, null, null, null, 15, null);
   }
}
