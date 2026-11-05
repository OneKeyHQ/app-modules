package io.intercom.android.sdk.articles;

public interface ArticleWebViewListener {
   void onArticleStartedLoading();

   void onArticleFinishedLoading();

   void onArticleLoadingError();

   void articleNotFound();

   void scrollArticleViewTo(int var1);
}
