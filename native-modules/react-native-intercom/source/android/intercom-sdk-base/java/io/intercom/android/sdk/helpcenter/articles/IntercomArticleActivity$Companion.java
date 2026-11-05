package io.intercom.android.sdk.helpcenter.articles;

import android.annotation.SuppressLint;
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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0007J\u0010\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\nH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$Companion;",
         "",
         "<init>",
         "()V",
         "ARTICLE_ID",
         "",
         "METRIC_PLACE",
         "IS_SEARCH_BROWSE",
         "SHOULD_HIDE_REACTIONS",
         "buildIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "articleActivityArguments",
         "Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$ArticleActivityArguments;",
         "getArguments",
         "intent",
         "intercom-sdk-base_release"
   }
)
public final class IntercomArticleActivity$Companion {
   private IntercomArticleActivity$Companion() {
   }

   @JvmStatic
   @NotNull
   public final Intent buildIntent(@NotNull Context var1, @NotNull IntercomArticleActivity$ArticleActivityArguments var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "articleActivityArguments");
      Intent var3 = new Intent(var1, IntercomArticleActivity.class);
      Intent var4 = var3;
      int var5 = 0;
      var4.setFlags(268435456);
      var4.putExtra("ARTICLE_ID", var2.getArticleId());
      var4.putExtra("METRIC_PLACE", var2.getMetricPlace());
      var4.putExtra("IS_FROM_SEARCH_BROWSE", var2.isFromSearchBrowse());
      var4.putExtra("SHOULD_HIDE_REACTIONS", var2.getShouldHideReactions());
      return var3;
   }

   @SuppressLint("WrongConstant")
   @NotNull
   public final IntercomArticleActivity$ArticleActivityArguments getArguments(@NotNull Intent var1) {
      Intrinsics.checkNotNullParameter(var1, "intent");
      IntercomArticleActivity$ArticleActivityArguments var10000;
      String var10002 = var1.getStringExtra("ARTICLE_ID");
      if (var10002 == null) {
         var10002 = "";
      }

      String var10003 = var1.getStringExtra("METRIC_PLACE");
      if (var10003 == null) {
         var10003 = "";
      }

      var10000 = new IntercomArticleActivity$ArticleActivityArguments(
         var10002, var10003, var1.getBooleanExtra("IS_FROM_SEARCH_BROWSE", false), var1.getBooleanExtra("SHOULD_HIDE_REACTIONS", false)
      );
      return var10000;
   }

   // $VF: synthetic method
   public IntercomArticleActivity$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
