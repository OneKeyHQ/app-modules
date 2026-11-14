package io.intercom.android.sdk.helpcenter.articles;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000I\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\b\u0003*\u0001\u0019\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JT\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00160\u00152\b\b\u0002\u0010\u0017\u001a\u00020\u0012JS\u0010\u0018\u001a\u00020\u00192\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00160\u00152\b\b\u0002\u0010\u0017\u001a\u00020\u0012H\u0002¢\u0006\u0002\u0010\u001aR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "HAPPY_SERVER_INDEX",
         "",
         "NEUTRAL_SERVER_INDEX",
         "SAD_SERVER_INDEX",
         "create",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "baseUrl",
         "",
         "metricPlace",
         "isFromSearchBrowse",
         "",
         "shouldHideReactions",
         "scrollTo",
         "Lkotlin/Function1;",
         "",
         "isSystemInDarkTheme",
         "factory",
         "io/intercom/android/sdk/helpcenter/articles/ArticleViewModel$Companion$factory$1",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Ljava/lang/String;Ljava/lang/String;ZZLkotlin/jvm/functions/Function1;Z)Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class ArticleViewModel$Companion {
   private ArticleViewModel$Companion() {
   }

   @NotNull
   public final ArticleViewModel create(
      @NotNull ViewModelStoreOwner var1,
      @NotNull HelpCenterApi var2,
      @NotNull String var3,
      @NotNull String var4,
      boolean var5,
      boolean var6,
      @NotNull Function1 var7,
      boolean var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "helpCenterApi");
      Intrinsics.checkNotNullParameter(var3, "baseUrl");
      Intrinsics.checkNotNullParameter(var4, "metricPlace");
      Intrinsics.checkNotNullParameter(var7, "scrollTo");
      return (ArticleViewModel)new ViewModelProvider(var1, this.factory(var2, var3, var4, var5, var6, var7, var8)).get(ArticleViewModel.class);
   }

   // $VF: synthetic method
   public static ArticleViewModel create$default(
      ArticleViewModel$Companion var0,
      ViewModelStoreOwner var1,
      HelpCenterApi var2,
      String var3,
      String var4,
      boolean var5,
      boolean var6,
      Function1 var7,
      boolean var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 128) != 0) {
         var8 = false;
      }

      return var0.create(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private final ArticleViewModel$Companion$factory$1 factory(
      HelpCenterApi var1, String var2, String var3, boolean var4, boolean var5, Function1 var6, boolean var7
   ) {
      return new ArticleViewModel$Companion$factory$1(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   static ArticleViewModel$Companion$factory$1 factory$default(
      ArticleViewModel$Companion var0,
      HelpCenterApi var1,
      String var2,
      String var3,
      boolean var4,
      boolean var5,
      Function1 var6,
      boolean var7,
      int var8,
      Object var9
   ) {
      if ((var8 & 64) != 0) {
         var7 = false;
      }

      return var0.factory(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public ArticleViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
