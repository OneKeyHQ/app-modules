package io.intercom.android.sdk.helpcenter.search;

import android.content.Context;
import android.content.Intent;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$Companion;",
         "",
         "<init>",
         "()V",
         "IS_FROM_SEARCH_BROWSE",
         "",
         "buildIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "isFromSearchBrowse",
         "",
         "getArguments",
         "Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$ArticleSearchArgs;",
         "intent",
         "intercom-sdk-base_release"
   }
)
public final class IntercomArticleSearchActivity$Companion {
   private IntercomArticleSearchActivity$Companion() {
   }

   @JvmStatic
   @NotNull
   public final Intent buildIntent(@NotNull Context var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intent var3 = new Intent(var1, IntercomArticleSearchActivity.class);
      var3.setFlags(268435456);
      var3.putExtra("IS_SEARCH_BROWSE", var2);
      return var3;
   }

   @NotNull
   public final IntercomArticleSearchActivity$ArticleSearchArgs getArguments(@NotNull Intent var1) {
      Intrinsics.checkNotNullParameter(var1, "intent");
      return new IntercomArticleSearchActivity$ArticleSearchArgs(var1.getBooleanExtra("IS_SEARCH_BROWSE", false));
   }

   // $VF: synthetic method
   public IntercomArticleSearchActivity$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
