package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Avatar;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J-\u0010\u0016\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\bH×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$EventRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "labelSegments",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/EventLabelSegment;",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "partId",
         "",
         "<init>",
         "(Ljava/util/List;Lio/intercom/android/sdk/models/Avatar;Ljava/lang/String;)V",
         "getLabelSegments",
         "()Ljava/util/List;",
         "getAvatar",
         "()Lio/intercom/android/sdk/models/Avatar;",
         "getPartId",
         "()Ljava/lang/String;",
         "key",
         "getKey",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ContentRow$EventRow implements ContentRow {
   @NotNull
   private final List labelSegments;
   @NotNull
   private final Avatar avatar;
   @NotNull
   private final String partId;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$EventRow(@NotNull List var1, @NotNull Avatar var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "labelSegments");
      Intrinsics.checkNotNullParameter(var2, "avatar");
      Intrinsics.checkNotNullParameter(var3, "partId");
      this.labelSegments = var1;
      this.avatar = var2;
      this.partId = var3;
   }

   @NotNull
   public final List getLabelSegments() {
      return this.labelSegments;
   }

   @NotNull
   public final Avatar getAvatar() {
      return this.avatar;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   @NotNull
   @Override
   public String getKey() {
      return "event_row_" + this.partId;
   }

   @NotNull
   public final List component1() {
      return this.labelSegments;
   }

   @NotNull
   public final Avatar component2() {
      return this.avatar;
   }

   @NotNull
   public final String component3() {
      return this.partId;
   }

   @NotNull
   public final ContentRow$EventRow copy(@NotNull List var1, @NotNull Avatar var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "labelSegments");
      Intrinsics.checkNotNullParameter(var2, "avatar");
      Intrinsics.checkNotNullParameter(var3, "partId");
      return new ContentRow$EventRow(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ContentRow$EventRow copy$default(ContentRow$EventRow var0, List var1, Avatar var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.labelSegments;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.avatar;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.partId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "EventRow(labelSegments=" + this.labelSegments + ", avatar=" + this.avatar + ", partId=" + this.partId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.labelSegments.hashCode();
      var1 = var1 * 31 + this.avatar.hashCode();
      return var1 * 31 + this.partId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$EventRow)) {
         return false;
      } else {
         ContentRow$EventRow var2 = (ContentRow$EventRow)var1;
         if (!Intrinsics.areEqual(this.labelSegments, var2.labelSegments)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.avatar, var2.avatar) ? false : Intrinsics.areEqual(this.partId, var2.partId);
         }
      }
   }
}
