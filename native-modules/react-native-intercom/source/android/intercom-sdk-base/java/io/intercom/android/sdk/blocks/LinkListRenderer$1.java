package io.intercom.android.sdk.blocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import io.intercom.android.sdk.blocks.lib.models.Link;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.Collections;

class LinkListRenderer$1 implements OnClickListener {
   // $VF: synthetic field
   final Link val$link;
   // $VF: synthetic field
   final Context val$context;
   // $VF: synthetic field
   final LinkListRenderer this$0;

   LinkListRenderer$1(LinkListRenderer var1, Link var2, Context var3) {
      this.this$0 = var1;
      this.val$link = var2;
      this.val$context = var3;
   }

   @Override
   public void onClick(View var1) {
      if (("educate.article".equals(this.val$link.getLinkType()) || "educate.suggestion".equals(this.val$link.getLinkType()))
         && this.val$context instanceof Activity) {
         Intent var2 = IntercomArticleActivity.buildIntent(
            this.val$context, new IntercomArticleActivity$ArticleActivityArguments(this.val$link.getArticleId(), "conversation")
         );
         this.val$context.startActivity(var2);
      } else if ("educate.help_center".equals(this.val$link.getLinkType()) && this.val$context instanceof Activity) {
         IntercomRootActivityLauncher.INSTANCE.startHelpCenterCollections(this.val$context, Collections.emptyList(), "conversation");
      } else {
         LinkOpener.handleUrl(this.val$link.getUrl(), this.val$context, LinkListRenderer.access$000(this.this$0));
      }
   }
}
