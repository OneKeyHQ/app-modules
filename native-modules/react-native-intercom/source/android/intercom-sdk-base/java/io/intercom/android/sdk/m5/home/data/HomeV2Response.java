package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\n\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u000e\u001a\u00020\u000fH×\u0001J\t\u0010\u0010\u001a\u00020\u0011H×\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeV2Response;",
         "",
         "cards",
         "",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "<init>",
         "(Ljava/util/List;)V",
         "getCards",
         "()Ljava/util/List;",
         "component1",
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
@StabilityInferred(parameters = 0)
public final class HomeV2Response {
   @SerializedName("cards")
   @NotNull
   private final List cards;
   public static final int $stable;

   static { $stable = 8; }

   public HomeV2Response(@NotNull List var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "cards");
      this.cards = var1;
   }

   @NotNull
   public final List getCards() {
      return this.cards;
   }

   @NotNull
   public final List component1() {
      return this.cards;
   }

   @NotNull
   public final HomeV2Response copy(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "cards");
      return new HomeV2Response(var1);
   }

   // $VF: synthetic method
   public static HomeV2Response copy$default(HomeV2Response var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.cards;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "HomeV2Response(cards=" + this.cards + ')';
   }

   @Override
   public int hashCode() {
      return this.cards.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeV2Response)) {
         return false;
      }

      HomeV2Response var2 = (HomeV2Response)var1;
      return Intrinsics.areEqual(this.cards, var2.cards);
   }
}
