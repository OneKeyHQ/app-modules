package io.intercom.android.sdk.helpcenter.component;

import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\u001a2\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0000¨\u0006\u000b",
   d2 = {
         "computeViewState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "currentState",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "metricPlace",
         "",
         "isFromSearchBrowse",
         "",
         "intercom-sdk-base_release"
   }
)
public final class TeammateHelpKt {
   @NotNull
   public static final ArticleViewState$TeamPresenceState computeViewState(
      @Nullable ArticleMetadata var0, @NotNull ArticleViewState$TeamPresenceState var1, @NotNull AppConfig var2, @NotNull String var3, boolean var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "currentState");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      Intrinsics.checkNotNullParameter(var3, "metricPlace");
      String var5 = var2.isAccessToTeammateEnabled() ? "style_human" : "style_bot";
      int var6 = var2.getPrimaryColor();
      return ArticleViewState$TeamPresenceState.copy$default(var1, var0, null, null, 0, 0, var6, var3, var5, var4, null, 542, null);
   }
}
