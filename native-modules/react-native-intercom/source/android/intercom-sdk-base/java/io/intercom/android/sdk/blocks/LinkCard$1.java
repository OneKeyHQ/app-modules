package io.intercom.android.sdk.blocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import io.intercom.android.sdk.blocks.lib.models.Link;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;

class LinkCard$1 implements OnClickListener {
   // $VF: synthetic field
   final Context val$context;
   // $VF: synthetic field
   final Link val$link;
   // $VF: synthetic field
   final LinkCard this$0;

   LinkCard$1(LinkCard var1, Context var2, Link var3) {
      this.this$0 = var1;
      this.val$context = var2;
      this.val$link = var3;
   }

   @Override
   public void onClick(View var1) {
      if (this.val$context instanceof Activity) {
         Intent var2 = IntercomArticleActivity.buildIntent(
            this.val$context, new IntercomArticleActivity$ArticleActivityArguments(this.val$link.getArticleId(), "conversation")
         );
         this.val$context.startActivity(var2);
      }
   }
}
