package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0011\u001a\u00020\u0006HÆ\u0003¢\u0006\u0004\b\u0012\u0010\rJ.\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001¢\u0006\u0004\b\u0014\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0013\u0010\u0005\u001a\u00020\u0006¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\r¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/survey/block/SuffixText;",
         "",
         "text",
         "",
         "ttsText",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;JLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getText",
         "()Ljava/lang/String;",
         "getTtsText",
         "getColor-0d7_KjU",
         "()J",
         "J",
         "component1",
         "component2",
         "component3",
         "component3-0d7_KjU",
         "copy",
         "copy-mxwnekA",
         "(Ljava/lang/String;Ljava/lang/String;J)Lio/intercom/android/sdk/survey/block/SuffixText;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SuffixText {
   @NotNull
   public static final SuffixText$Companion Companion = new SuffixText$Companion(null);
   @NotNull
   private final String text;
   @NotNull
   private final String ttsText;
   private final long color;
   public static int $stable;

   static {}
   @NotNull
   private static final SuffixText NO_SUFFIX = new SuffixText("", "", Color.Companion.recovered$235b30c090a399c5(), null);

   private SuffixText(String var1, String var2, long var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var2, "ttsText");
      this.text = var1;
      this.ttsText = var2;
      this.color = var3;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   public final String getTtsText() {
      return this.ttsText;
   }

   public final long recovered$6f15fcf6c761ad06() {
      return this.color;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   @NotNull
   public final String component2() {
      return this.ttsText;
   }

   public final long recovered$46bfddf50387e286() {
      return this.color;
   }

   @NotNull
   public final SuffixText recovered$87fa727b8657f347(@NotNull String var1, @NotNull String var2, long var3) {
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var2, "ttsText");
      return new SuffixText(var1, var2, var3, null);
   }

   // $VF: synthetic method
   public static SuffixText recovered$3ac46c778466f299(SuffixText var0, String var1, String var2, long var3, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.ttsText;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.color;
      }

      return var0.recovered$87fa727b8657f347(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "SuffixText(text=" + this.text + ", ttsText=" + this.ttsText + ", color=" + Color.recovered$cfef63bb1d111f9f(this.color) + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      var1 = var1 * 31 + this.ttsText.hashCode();
      return var1 * 31 + Color.recovered$87e060624084b26b(this.color);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SuffixText)) {
         return false;
      } else {
         SuffixText var2 = (SuffixText)var1;
         if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.ttsText, var2.ttsText) ? false : Color.recovered$9eab66ea8c46e11c(this.color, var2.color);
         }
      }
   }

   // $VF: synthetic method
   public SuffixText(String var1, String var2, long var3, DefaultConstructorMarker var5) {
      this(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final SuffixText access$getNO_SUFFIX$cp() {
      return NO_SUFFIX;
   }
}
