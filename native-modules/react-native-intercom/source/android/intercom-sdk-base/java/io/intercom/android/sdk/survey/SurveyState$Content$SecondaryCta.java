package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0006HÆ\u0003J'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyState$Content$SecondaryCta;",
         "",
         "buttonText",
         "",
         "destination",
         "isExternalUrl",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Z)V",
         "getButtonText",
         "()Ljava/lang/String;",
         "getDestination",
         "()Z",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyState$Content$SecondaryCta {
   @NotNull
   private final String buttonText;
   @NotNull
   private final String destination;
   private final boolean isExternalUrl;
   public static int $stable;

   static {}

   public SurveyState$Content$SecondaryCta(@NotNull String var1, @NotNull String var2, boolean var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "buttonText");
      Intrinsics.checkNotNullParameter(var2, "destination");
      this.buttonText = var1;
      this.destination = var2;
      this.isExternalUrl = var3;
   }

   @NotNull
   public final String getButtonText() {
      return this.buttonText;
   }

   @NotNull
   public final String getDestination() {
      return this.destination;
   }

   public final boolean isExternalUrl() {
      return this.isExternalUrl;
   }

   @NotNull
   public final String component1() {
      return this.buttonText;
   }

   @NotNull
   public final String component2() {
      return this.destination;
   }

   public final boolean component3() {
      return this.isExternalUrl;
   }

   @NotNull
   public final SurveyState$Content$SecondaryCta copy(@NotNull String var1, @NotNull String var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "buttonText");
      Intrinsics.checkNotNullParameter(var2, "destination");
      return new SurveyState$Content$SecondaryCta(var1, var2, var3);
   }

   // $VF: synthetic method
   public static SurveyState$Content$SecondaryCta copy$default(
      SurveyState$Content$SecondaryCta var0, String var1, String var2, boolean var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.buttonText;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.destination;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.isExternalUrl;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "SecondaryCta(buttonText=" + this.buttonText + ", destination=" + this.destination + ", isExternalUrl=" + this.isExternalUrl + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.buttonText.hashCode();
      var1 = var1 * 31 + this.destination.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isExternalUrl);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyState$Content$SecondaryCta)) {
         return false;
      } else {
         SurveyState$Content$SecondaryCta var2 = (SurveyState$Content$SecondaryCta)var1;
         if (!Intrinsics.areEqual(this.buttonText, var2.buttonText)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.destination, var2.destination) ? false : this.isExternalUrl == var2.isExternalUrl;
         }
      }
   }
}
