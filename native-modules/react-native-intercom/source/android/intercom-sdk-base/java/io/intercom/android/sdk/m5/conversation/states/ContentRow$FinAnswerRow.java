package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0007HÆ\u0003J)\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÇ\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u0011H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$FinAnswerRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "groupingPosition",
         "Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "senderMetadata",
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Part;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;)V",
         "getPart",
         "()Lio/intercom/android/sdk/models/Part;",
         "getGroupingPosition",
         "()Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "getSenderMetadata",
         "()Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
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
public final class ContentRow$FinAnswerRow implements ContentRow {
   @NotNull
   private final Part part;
   @NotNull
   private final GroupingPosition groupingPosition;
   @Nullable
   private final SenderMetadata senderMetadata;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$FinAnswerRow(@NotNull Part var1, @NotNull GroupingPosition var2, @Nullable SenderMetadata var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "groupingPosition");
      this.part = var1;
      this.groupingPosition = var2;
      this.senderMetadata = var3;
   }

   // $VF: synthetic method
   public ContentRow$FinAnswerRow(Part var1, GroupingPosition var2, SenderMetadata var3, int var4, DefaultConstructorMarker var5) {
      this(var1, var2, (((var4 & 4) != 0) ? (SenderMetadata) (null) : var3));
   }

   @NotNull
   public final Part getPart() {
      return this.part;
   }

   @NotNull
   public final GroupingPosition getGroupingPosition() {
      return this.groupingPosition;
   }

   @Nullable
   public final SenderMetadata getSenderMetadata() {
      return this.senderMetadata;
   }

   @NotNull
   @Override
   public String getKey() {
      return "fin_answer_row_" + this.part.getId();
   }

   @NotNull
   public final Part component1() {
      return this.part;
   }

   @NotNull
   public final GroupingPosition component2() {
      return this.groupingPosition;
   }

   @Nullable
   public final SenderMetadata component3() {
      return this.senderMetadata;
   }

   @NotNull
   public final ContentRow$FinAnswerRow copy(@NotNull Part var1, @NotNull GroupingPosition var2, @Nullable SenderMetadata var3) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "groupingPosition");
      return new ContentRow$FinAnswerRow(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ContentRow$FinAnswerRow copy$default(
      ContentRow$FinAnswerRow var0, Part var1, GroupingPosition var2, SenderMetadata var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.part;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.groupingPosition;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.senderMetadata;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "FinAnswerRow(part=" + this.part + ", groupingPosition=" + this.groupingPosition + ", senderMetadata=" + this.senderMetadata + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.part.hashCode();
      var1 = var1 * 31 + this.groupingPosition.hashCode();
      return var1 * 31 + (this.senderMetadata == null ? 0 : this.senderMetadata.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$FinAnswerRow)) {
         return false;
      } else {
         ContentRow$FinAnswerRow var2 = (ContentRow$FinAnswerRow)var1;
         if (!Intrinsics.areEqual(this.part, var2.part)) {
            return false;
         } else {
            return this.groupingPosition != var2.groupingPosition ? false : Intrinsics.areEqual(this.senderMetadata, var2.senderMetadata);
         }
      }
   }
}
