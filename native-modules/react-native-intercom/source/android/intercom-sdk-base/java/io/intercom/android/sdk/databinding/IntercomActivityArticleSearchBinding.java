package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomActivityArticleSearchBinding implements ViewBinding {
   @NonNull
   private final ConstraintLayout rootView;
   @NonNull
   public final ComposeView articleSearchComposeView;
   @NonNull
   public final ConstraintLayout intercomSearchScreenRoot;

   private IntercomActivityArticleSearchBinding(@NonNull ConstraintLayout var1, @NonNull ComposeView var2, @NonNull ConstraintLayout var3) {
      this.rootView = var1;
      this.articleSearchComposeView = var2;
      this.intercomSearchScreenRoot = var3;
   }

   @NonNull
   public ConstraintLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomActivityArticleSearchBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomActivityArticleSearchBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_activity_article_search, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomActivityArticleSearchBinding bind(@NonNull View var0) {
      int var1 = id.article_search_compose_view;
      ComposeView var2 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
      if (var2 == null) {
         String var4 = var0.getResources().getResourceName(var1);
         throw new NullPointerException("Missing required view with ID: ".concat(var4));
      } else {
         ConstraintLayout var3 = (ConstraintLayout)var0;
         return new IntercomActivityArticleSearchBinding((ConstraintLayout)var0, var2, var3);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
