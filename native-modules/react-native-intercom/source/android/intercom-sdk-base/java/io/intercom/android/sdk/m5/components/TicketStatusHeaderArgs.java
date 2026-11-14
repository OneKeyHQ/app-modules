package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.text.font.FontWeight;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/TicketStatusHeaderArgs;",
         "",
         "title",
         "",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "<init>",
         "(Ljava/lang/String;Landroidx/compose/ui/text/font/FontWeight;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getFontWeight",
         "()Landroidx/compose/ui/text/font/FontWeight;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class TicketStatusHeaderArgs {
   @NotNull
   private final String title;
   @NotNull
   private final FontWeight fontWeight;
   public static int $stable;

   static {}

   public TicketStatusHeaderArgs(@NotNull String var1, @NotNull FontWeight var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "fontWeight");
      this.title = var1;
      this.fontWeight = var2;
   }

   // $VF: synthetic method
   public TicketStatusHeaderArgs(String var1, FontWeight var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (FontWeight) (FontWeight.Companion.getNormal()) : var2));
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final FontWeight getFontWeight() {
      return this.fontWeight;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final FontWeight component2() {
      return this.fontWeight;
   }

   @NotNull
   public final TicketStatusHeaderArgs copy(@NotNull String var1, @NotNull FontWeight var2) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "fontWeight");
      return new TicketStatusHeaderArgs(var1, var2);
   }

   // $VF: synthetic method
   public static TicketStatusHeaderArgs copy$default(TicketStatusHeaderArgs var0, String var1, FontWeight var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.fontWeight;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketStatusHeaderArgs(title=" + this.title + ", fontWeight=" + this.fontWeight + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      return var1 * 31 + this.fontWeight.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketStatusHeaderArgs)) {
         return false;
      }

      TicketStatusHeaderArgs var2 = (TicketStatusHeaderArgs)var1;
      return !Intrinsics.areEqual(this.title, var2.title) ? false : Intrinsics.areEqual(this.fontWeight, var2.fontWeight);
   }
}
