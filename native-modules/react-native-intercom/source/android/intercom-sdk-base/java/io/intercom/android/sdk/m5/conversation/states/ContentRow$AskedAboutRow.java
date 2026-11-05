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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$AskedAboutRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Part;)V",
         "getPart",
         "()Lio/intercom/android/sdk/models/Part;",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
         "component1",
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
public final class ContentRow$AskedAboutRow implements ContentRow {
   @NotNull
   private final Part part;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$AskedAboutRow(@NotNull Part var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "part");
      this.part = var1;
   }

   @NotNull
   public final Part getPart() {
      return this.part;
   }

   @NotNull
   @Override
   public String getKey() {
      return "asked_about_row_" + this.part.getId();
   }

   @NotNull
   public final Part component1() {
      return this.part;
   }

   @NotNull
   public final ContentRow$AskedAboutRow copy(@NotNull Part var1) {
      Intrinsics.checkNotNullParameter(var1, "part");
      return new ContentRow$AskedAboutRow(var1);
   }

   // $VF: synthetic method
   public static ContentRow$AskedAboutRow copy$default(ContentRow$AskedAboutRow var0, Part var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.part;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "AskedAboutRow(part=" + this.part + ')';
   }

   @Override
   public int hashCode() {
      return this.part.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$AskedAboutRow)) {
         return false;
      }

      ContentRow$AskedAboutRow var2 = (ContentRow$AskedAboutRow)var1;
      return Intrinsics.areEqual(this.part, var2.part);
   }
}
