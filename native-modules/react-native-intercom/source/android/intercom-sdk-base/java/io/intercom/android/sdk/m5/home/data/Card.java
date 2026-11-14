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
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0005¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003JG\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001f\u001a\u00020 H×\u0001J\t\u0010!\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0016\u0010\u000b\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/Card;",
         "",
         "action",
         "Lio/intercom/android/sdk/m5/home/data/Action;",
         "cardTitle",
         "",
         "spaceItems",
         "",
         "Lio/intercom/android/sdk/m5/home/data/SpaceItem;",
         "suggestedArticles",
         "Lio/intercom/android/sdk/m5/home/data/SuggestedArticle;",
         "type",
         "<init>",
         "(Lio/intercom/android/sdk/m5/home/data/Action;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V",
         "getAction",
         "()Lio/intercom/android/sdk/m5/home/data/Action;",
         "getCardTitle",
         "()Ljava/lang/String;",
         "getSpaceItems",
         "()Ljava/util/List;",
         "getSuggestedArticles",
         "getType",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
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
@StabilityInferred(parameters = 0)
public final class Card {
   @SerializedName("action")
   @NotNull
   private final Action action;
   @SerializedName("card_title")
   @NotNull
   private final String cardTitle;
   @SerializedName("space_items")
   @NotNull
   private final List spaceItems;
   @SerializedName("suggested_articles")
   @NotNull
   private final List suggestedArticles;
   @SerializedName("type")
   @NotNull
   private final String type;
   public static final int $stable;

   static { $stable = 8; }

   public Card(@NotNull Action var1, @NotNull String var2, @NotNull List var3, @NotNull List var4, @NotNull String var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "action");
      Intrinsics.checkNotNullParameter(var2, "cardTitle");
      Intrinsics.checkNotNullParameter(var3, "spaceItems");
      Intrinsics.checkNotNullParameter(var4, "suggestedArticles");
      Intrinsics.checkNotNullParameter(var5, "type");
      this.action = var1;
      this.cardTitle = var2;
      this.spaceItems = var3;
      this.suggestedArticles = var4;
      this.type = var5;
   }

   @NotNull
   public final Action getAction() {
      return this.action;
   }

   @NotNull
   public final String getCardTitle() {
      return this.cardTitle;
   }

   @NotNull
   public final List getSpaceItems() {
      return this.spaceItems;
   }

   @NotNull
   public final List getSuggestedArticles() {
      return this.suggestedArticles;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   @NotNull
   public final Action component1() {
      return this.action;
   }

   @NotNull
   public final String component2() {
      return this.cardTitle;
   }

   @NotNull
   public final List component3() {
      return this.spaceItems;
   }

   @NotNull
   public final List component4() {
      return this.suggestedArticles;
   }

   @NotNull
   public final String component5() {
      return this.type;
   }

   @NotNull
   public final Card copy(@NotNull Action var1, @NotNull String var2, @NotNull List var3, @NotNull List var4, @NotNull String var5) {
      Intrinsics.checkNotNullParameter(var1, "action");
      Intrinsics.checkNotNullParameter(var2, "cardTitle");
      Intrinsics.checkNotNullParameter(var3, "spaceItems");
      Intrinsics.checkNotNullParameter(var4, "suggestedArticles");
      Intrinsics.checkNotNullParameter(var5, "type");
      return new Card(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static Card copy$default(Card var0, Action var1, String var2, List var3, List var4, String var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.action;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.cardTitle;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.spaceItems;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.suggestedArticles;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.type;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Card(action="
         + this.action
         + ", cardTitle="
         + this.cardTitle
         + ", spaceItems="
         + this.spaceItems
         + ", suggestedArticles="
         + this.suggestedArticles
         + ", type="
         + this.type
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = var1 * 31 + this.cardTitle.hashCode();
      var1 = var1 * 31 + this.spaceItems.hashCode();
      var1 = var1 * 31 + this.suggestedArticles.hashCode();
      return var1 * 31 + this.type.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Card)) {
         return false;
      } else {
         Card var2 = (Card)var1;
         if (!Intrinsics.areEqual(this.action, var2.action)) {
            return false;
         } else if (!Intrinsics.areEqual(this.cardTitle, var2.cardTitle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.spaceItems, var2.spaceItems)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.suggestedArticles, var2.suggestedArticles) ? false : Intrinsics.areEqual(this.type, var2.type);
         }
      }
   }
}
