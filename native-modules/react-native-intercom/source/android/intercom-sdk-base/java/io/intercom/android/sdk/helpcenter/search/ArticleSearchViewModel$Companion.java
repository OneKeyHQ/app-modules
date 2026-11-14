package io.intercom.android.sdk.helpcenter.search;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000+\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\b\u0003*\u0001\r\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u001d\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¢\u0006\u0002\u0010\u000e¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "isFromSearchBrowse",
         "",
         "factory",
         "io/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$Companion$factory$1",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Z)Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class ArticleSearchViewModel$Companion {
   private ArticleSearchViewModel$Companion() {
   }

   @NotNull
   public final ArticleSearchViewModel create(@NotNull ViewModelStoreOwner var1, @NotNull HelpCenterApi var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "helpCenterApi");
      return (ArticleSearchViewModel)new ViewModelProvider(var1, this.factory(var2, var3)).get(ArticleSearchViewModel.class);
   }

   private final ArticleSearchViewModel$Companion$factory$1 factory(HelpCenterApi var1, boolean var2) {
      return new ArticleSearchViewModel$Companion$factory$1(var1, var2);
   }

   // $VF: synthetic method
   public ArticleSearchViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
