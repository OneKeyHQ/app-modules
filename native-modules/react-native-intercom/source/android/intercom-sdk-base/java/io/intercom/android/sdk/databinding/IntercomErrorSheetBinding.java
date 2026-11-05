package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.views.IntercomErrorView;

public final class IntercomErrorSheetBinding implements ViewBinding {
   @NonNull
   private final IntercomErrorView rootView;
   @NonNull
   public final Button actionButton;
   @NonNull
   public final TextView emptyTextSubtitle;
   @NonNull
   public final TextView emptyTextTitle;
   @NonNull
   public final IntercomErrorView errorLayoutSheet;

   private IntercomErrorSheetBinding(
      @NonNull IntercomErrorView var1, @NonNull Button var2, @NonNull TextView var3, @NonNull TextView var4, @NonNull IntercomErrorView var5
   ) {
      this.rootView = var1;
      this.actionButton = var2;
      this.emptyTextSubtitle = var3;
      this.emptyTextTitle = var4;
      this.errorLayoutSheet = var5;
   }

   @NonNull
   public IntercomErrorView getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomErrorSheetBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomErrorSheetBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_error_sheet, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomErrorSheetBinding bind(@NonNull View var0) {
      int var1 = id.action_button;
      Button var2 = (Button)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.empty_text_subtitle;
         TextView var3 = (TextView)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.empty_text_title;
            TextView var4 = (TextView)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               IntercomErrorView var5 = (IntercomErrorView)var0;
               return new IntercomErrorSheetBinding((IntercomErrorView)var0, var2, var3, var4, var5);
            }
         }
      }

      String var6 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var6));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
