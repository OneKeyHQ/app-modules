package io.intercom.android.sdk.helpcenter.articles;

import android.webkit.WebView;
import io.intercom.android.sdk.articles.ArticleWebViewListener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H\u0016J\b\u0010\u0005\u001a\u00020\u0003H\u0016J\b\u0010\u0006\u001a\u00020\u0003H\u0016J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n",
   d2 = {
         "io/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1",
         "Lio/intercom/android/sdk/articles/ArticleWebViewListener;",
         "onArticleStartedLoading",
         "",
         "onArticleFinishedLoading",
         "onArticleLoadingError",
         "articleNotFound",
         "scrollArticleViewTo",
         "y",
         "",
         "intercom-sdk-base_release"
   }
)
public final class IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1 implements ArticleWebViewListener {
   // $VF: synthetic field
   final IntercomArticleActivity this$0;
   // $VF: synthetic field
   final WebView $this_apply;

   IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1(IntercomArticleActivity var1, WebView var2) {
      this.this$0 = var1;
      this.$this_apply = var2;
   }

   @Override
   public void onArticleStartedLoading() {
      IntercomArticleActivity.access$getViewModel(this.this$0).onArticleStartedLoading();
   }

   @Override
   public void onArticleFinishedLoading() {
      IntercomArticleActivity.access$getViewModel(this.this$0).onArticleFinishedLoading();
      this.$this_apply
         .evaluateJavascript("window.alexandriaArticleContentId", io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1.recoveredExpressionFactory_93(((io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1) this).this$0));
   }

   @Override
   public void onArticleLoadingError() {
      IntercomArticleActivity.access$getViewModel(this.this$0).onArticleLoadingError();
   }

   @Override
   public void articleNotFound() {
      IntercomArticleActivity.access$getViewModel(this.this$0).articleNotFound();
   }

   @Override
   public void scrollArticleViewTo(int var1) {
      IntercomArticleActivity.access$getViewModel(this.this$0).scrollArticleViewTo(var1);
   }

   private static final void onArticleFinishedLoading$lambda$0(IntercomArticleActivity var0, String var1) {
      ArticleViewModel var10000 = IntercomArticleActivity.access$getViewModel(var0);
      Intrinsics.checkNotNull(var1);
      var10000.articleContentIdFetched(StringsKt.removeSurrounding(var1, "\""));
   }

   private static android.webkit.ValueCallback recoveredExpressionFactory_93(io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_93_0) {
      return ((android.webkit.ValueCallback) (recoveredExpressionArgument_93_0) -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1.onArticleFinishedLoading$lambda$0(recoveredExpressionCapture_93_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_93_0)));
   }
}
