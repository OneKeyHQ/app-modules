package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000b¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$NoteCardRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "companyName",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/Part;Ljava/lang/String;)V",
         "getPart",
         "()Lio/intercom/android/sdk/models/Part;",
         "getCompanyName",
         "()Ljava/lang/String;",
         "key",
         "getKey",
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
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ContentRow$NoteCardRow implements ContentRow {
   @NotNull
   private final Part part;
   @NotNull
   private final String companyName;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$NoteCardRow(@NotNull Part var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "companyName");
      this.part = var1;
      this.companyName = var2;
   }

   @NotNull
   public final Part getPart() {
      return this.part;
   }

   @NotNull
   public final String getCompanyName() {
      return this.companyName;
   }

   @NotNull
   @Override
   public String getKey() {
      return "note_card_row_" + this.part.getId();
   }

   @NotNull
   public final Part component1() {
      return this.part;
   }

   @NotNull
   public final String component2() {
      return this.companyName;
   }

   @NotNull
   public final ContentRow$NoteCardRow copy(@NotNull Part var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "companyName");
      return new ContentRow$NoteCardRow(var1, var2);
   }

   // $VF: synthetic method
   public static ContentRow$NoteCardRow copy$default(ContentRow$NoteCardRow var0, Part var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.part;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.companyName;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "NoteCardRow(part=" + this.part + ", companyName=" + this.companyName + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.part.hashCode();
      return var1 * 31 + this.companyName.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$NoteCardRow)) {
         return false;
      }

      ContentRow$NoteCardRow var2 = (ContentRow$NoteCardRow)var1;
      return !Intrinsics.areEqual(this.part, var2.part) ? false : Intrinsics.areEqual(this.companyName, var2.companyName);
   }
}
