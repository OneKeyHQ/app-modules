package io.intercom.android.sdk.helpcenter.api;

import java.util.Map;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J0\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0014\b\u0003\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\tJ4\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\f\u001a\u00020\b2\u0014\b\u0003\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\rJF\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00040\u00032\b\b\u0001\u0010\u0010\u001a\u00020\b2\n\b\u0003\u0010\u0011\u001a\u0004\u0018\u00010\b2\u0014\b\u0003\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\u0012J^\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00032\b\b\u0001\u0010\u0015\u001a\u00020\b2\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0018\u001a\u00020\b2\b\b\u0003\u0010\u0019\u001a\u00020\u001a2\n\b\u0003\u0010\u0011\u001a\u0004\u0018\u00010\b2\u0014\b\u0003\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\u001bJ4\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00032\b\b\u0001\u0010\u0015\u001a\u00020\b2\u0014\b\u0003\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\r¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "",
         "fetchCollections",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "",
         "Lio/intercom/android/sdk/helpcenter/collections/HelpCenterCollection;",
         "options",
         "",
         "",
         "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchCollectionDetails",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent;",
         "id",
         "(Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "searchForArticles",
         "Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse;",
         "searchPhrase",
         "articleSource",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "reactToArticle",
         "",
         "articleId",
         "reactionIndex",
         "",
         "articleContentId",
         "allowAutoResponses",
         "",
         "(Ljava/lang/String;ILjava/lang/String;ZLjava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchArticle",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleResponse;",
         "intercom-sdk-base_release"
   }
)
public interface HelpCenterApi {
   @POST("help_center/collections")
   @Nullable
   Object fetchCollections(@Body @NotNull Map var1, @NotNull Continuation var2);

   @POST("help_center/collections/{id}")
   @Nullable
   Object fetchCollectionDetails(@Path("id") @NotNull String var1, @Body @NotNull Map var2, @NotNull Continuation var3);

   @POST("help_center/search")
   @Nullable
   Object searchForArticles(
      @Query("phrase") @NotNull String var1, @Query("article_source") @Nullable String var2, @Body @NotNull Map var3, @NotNull Continuation var4
   );

   @POST("articles/{articleId}/react")
   @Nullable
   Object reactToArticle(
      @Path("articleId") @NotNull String var1,
      @Query("reaction_index") int var2,
      @Query("article_content_id") @NotNull String var3,
      @Query("allow_auto_responses") boolean var4,
      @Query("article_source") @Nullable String var5,
      @Body @NotNull Map var6,
      @NotNull Continuation var7
   );

   @POST("articles/{articleId}")
   @Nullable
   Object fetchArticle(@Path("articleId") @NotNull String var1, @Body @NotNull Map var2, @NotNull Continuation var3);
}
