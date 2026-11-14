package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\r\u0010\u0012\u001a\u00020\u0003¢\u0006\u0004\b\u0013\u0010\u000bJ\u0010\u0010\u0014\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0015\u0010\u000bJ\u0010\u0010\u0016\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0017\u0010\u000bJ\u0010\u0010\u0018\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0019\u0010\u000bJ\u0010\u0010\u001a\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u001b\u0010\u000bJ\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\b\u001dJD\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001¢\u0006\u0004\b\u001f\u0010 J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010$\u001a\u00020%H×\u0001J\t\u0010&\u001a\u00020'H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\r\u0010\u000bR\u0013\u0010\u0005\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\u000e\u0010\u000bR\u0013\u0010\u0006\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\u000f\u0010\u000bR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyUiColors;",
         "",
         "background",
         "Landroidx/compose/ui/graphics/Color;",
         "onBackground",
         "button",
         "onButton",
         "dropDownSelectedColor",
         "<init>",
         "(JJJJLandroidx/compose/ui/graphics/Color;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBackground-0d7_KjU",
         "()J",
         "J",
         "getOnBackground-0d7_KjU",
         "getButton-0d7_KjU",
         "getOnButton-0d7_KjU",
         "getDropDownSelectedColor-QN2ZGVo",
         "()Landroidx/compose/ui/graphics/Color;",
         "getButtonBorder",
         "getButtonBorder-0d7_KjU",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "component3-0d7_KjU",
         "component4",
         "component4-0d7_KjU",
         "component5",
         "component5-QN2ZGVo",
         "copy",
         "copy-qa9m3tE",
         "(JJJJLandroidx/compose/ui/graphics/Color;)Lio/intercom/android/sdk/survey/SurveyUiColors;",
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
@StabilityInferred(parameters = 1)
public final class SurveyUiColors {
   private final long background;
   private final long onBackground;
   private final long button;
   private final long onButton;
   @Nullable
   private final Color dropDownSelectedColor;
   public static int $stable;

   static {}

   private SurveyUiColors(long var1, long var3, long var5, long var7, Color var9) {
      this.background = var1;
      this.onBackground = var3;
      this.button = var5;
      this.onButton = var7;
      this.dropDownSelectedColor = var9;
   }

   // $VF: synthetic method
   public SurveyUiColors(long var1, long var3, long var5, long var7, Color var9, int var10, DefaultConstructorMarker var11) {
      if ((var10 & 16) != 0) {
         var9 = null;
      }

      this(var1, var3, var5, var7, var9, null);
   }

   public final long recovered$a7e7bdd99c6c8d3d() {
      return this.background;
   }

   public final long recovered$f126774a6443d8d2() {
      return this.onBackground;
   }

   public final long recovered$923dd289d74a1df8() {
      return this.button;
   }

   public final long recovered$9bdc86596139ab10() {
      return this.onButton;
   }

   @Nullable
   public final Color recovered$ec244e2186b74a5a() {
      return this.dropDownSelectedColor;
   }

   public final long recovered$0a79dc0323a17592() {
      return ColorExtensionsKt.recovered$41e561cae61044c5(this.button)
         ? ColorExtensionsKt.recovered$de7fe4417d7923f0(this.button)
         : ColorExtensionsKt.recovered$da172df9fccf1b21(this.button);
   }

   public final long recovered$2b43d556f0094889() {
      return this.background;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.onBackground;
   }

   public final long recovered$46bfddf50387e286() {
      return this.button;
   }

   public final long recovered$0e8bb1255e180941() {
      return this.onButton;
   }

   @Nullable
   public final Color recovered$24816913fd54cc67() {
      return this.dropDownSelectedColor;
   }

   @NotNull
   public final SurveyUiColors recovered$b55ca9069ccbc240(long var1, long var3, long var5, long var7, @Nullable Color var9) {
      return new SurveyUiColors(var1, var3, var5, var7, var9, null);
   }

   // $VF: synthetic method
   public static SurveyUiColors recovered$4e1a9ef58cd7c1b4(SurveyUiColors var0, long var1, long var3, long var5, long var7, Color var9, int var10, Object var11) {
      if ((var10 & 1) != 0) {
         var1 = var0.background;
      }

      if ((var10 & 2) != 0) {
         var3 = var0.onBackground;
      }

      if ((var10 & 4) != 0) {
         var5 = var0.button;
      }

      if ((var10 & 8) != 0) {
         var7 = var0.onButton;
      }

      if ((var10 & 16) != 0) {
         var9 = var0.dropDownSelectedColor;
      }

      return var0.recovered$b55ca9069ccbc240(var1, var3, var5, var7, var9);
   }

   @NotNull
   @Override
   public String toString() {
      return "SurveyUiColors(background="
         + Color.recovered$cfef63bb1d111f9f(this.background)
         + ", onBackground="
         + Color.recovered$cfef63bb1d111f9f(this.onBackground)
         + ", button="
         + Color.recovered$cfef63bb1d111f9f(this.button)
         + ", onButton="
         + Color.recovered$cfef63bb1d111f9f(this.onButton)
         + ", dropDownSelectedColor="
         + this.dropDownSelectedColor
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.background);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.onBackground);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.button);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.onButton);
      return var1 * 31 + (this.dropDownSelectedColor == null ? 0 : Color.recovered$87e060624084b26b(this.dropDownSelectedColor.recovered$45f9de7b85e0e0b5()));
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyUiColors)) {
         return false;
      } else {
         SurveyUiColors var2 = (SurveyUiColors)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.background, var2.background)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.onBackground, var2.onBackground)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.button, var2.button)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.onButton, var2.onButton)
               ? false
               : Intrinsics.areEqual(this.dropDownSelectedColor, var2.dropDownSelectedColor);
         }
      }
   }

   // $VF: synthetic method
   public SurveyUiColors(long var1, long var3, long var5, long var7, Color var9, DefaultConstructorMarker var10) {
      this(var1, var3, var5, var7, var9);
   }
}
