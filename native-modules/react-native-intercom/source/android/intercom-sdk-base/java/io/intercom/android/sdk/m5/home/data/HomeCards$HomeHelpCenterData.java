package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nR\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeHelpCenterData;",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "cardTitle",
         "",
         "type",
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "suggestedArticles",
         "",
         "Lio/intercom/android/sdk/m5/home/data/SuggestedArticle;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/HomeCardType;Ljava/util/List;)V",
         "getSuggestedArticles",
         "()Ljava/util/List;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeCards$HomeHelpCenterData extends HomeCards {
   @SerializedName("suggested_articles")
   @NotNull
   private final List suggestedArticles;
   public static final int $stable;

   static { $stable = 8; }

   public HomeCards$HomeHelpCenterData(@NotNull String var1, @NotNull HomeCardType var2, @NotNull List var3) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "type");
      Intrinsics.checkNotNullParameter(var3, "suggestedArticles");
      super(var1, var2);
      this.suggestedArticles = var3;
   }

   @NotNull
   public final List getSuggestedArticles() {
      return this.suggestedArticles;
   }
}
