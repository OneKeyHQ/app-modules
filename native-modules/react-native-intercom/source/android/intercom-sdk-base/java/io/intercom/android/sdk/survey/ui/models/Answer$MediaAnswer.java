package io.intercom.android.sdk.survey.ui.models;

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
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001:\u0003\u0014\u0015\u0016B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\b\u0010\t\u001a\u00020\nH\u0016J\u000f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H×\u0003J\t\u0010\u0011\u001a\u00020\nH×\u0001J\t\u0010\u0012\u001a\u00020\u0013H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "mediaItems",
         "",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "<init>",
         "(Ljava/util/List;)V",
         "getMediaItems",
         "()Ljava/util/List;",
         "getLength",
         "",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "MediaItem",
         "FileUploadStatus",
         "FileUploadError",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Answer$MediaAnswer extends Answer {
   @NotNull
   private final List mediaItems;
   public static final int $stable;

   static { $stable = 8; }

   public Answer$MediaAnswer(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "mediaItems");
      super(null);
      this.mediaItems = var1;
   }

   @NotNull
   public final List getMediaItems() {
      return this.mediaItems;
   }

   @Override
   public int getLength() {
      return this.mediaItems.size();
   }

   @NotNull
   public final List component1() {
      return this.mediaItems;
   }

   @NotNull
   public final Answer$MediaAnswer copy(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "mediaItems");
      return new Answer$MediaAnswer(var1);
   }

   // $VF: synthetic method
   public static Answer$MediaAnswer copy$default(Answer$MediaAnswer var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.mediaItems;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "MediaAnswer(mediaItems=" + this.mediaItems + ')';
   }

   @Override
   public int hashCode() {
      return this.mediaItems.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$MediaAnswer)) {
         return false;
      }

      Answer$MediaAnswer var2 = (Answer$MediaAnswer)var1;
      return Intrinsics.areEqual(this.mediaItems, var2.mediaItems);
   }
}
