package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.m5.components.IntercomPrimaryButton;

public final class IntercomConversationRatingBlockBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;
   @NonNull
   public final TextView intercomRatingExpiredTextView;
   @NonNull
   public final LinearLayout intercomRatingOptionsLayout;
   @NonNull
   public final IntercomPrimaryButton intercomRatingTellUsMoreButton;
   @NonNull
   public final ImageView intercomYouRatedImageView;
   @NonNull
   public final LinearLayout intercomYouRatedLayout;
   @NonNull
   public final TextView rateYourConversationTextView;

   private IntercomConversationRatingBlockBinding(
      @NonNull LinearLayout var1,
      @NonNull TextView var2,
      @NonNull LinearLayout var3,
      @NonNull IntercomPrimaryButton var4,
      @NonNull ImageView var5,
      @NonNull LinearLayout var6,
      @NonNull TextView var7
   ) {
      this.rootView = var1;
      this.intercomRatingExpiredTextView = var2;
      this.intercomRatingOptionsLayout = var3;
      this.intercomRatingTellUsMoreButton = var4;
      this.intercomYouRatedImageView = var5;
      this.intercomYouRatedLayout = var6;
      this.rateYourConversationTextView = var7;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomConversationRatingBlockBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomConversationRatingBlockBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_conversation_rating_block, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomConversationRatingBlockBinding bind(@NonNull View var0) {
      int var1 = id.intercom_rating_expired_text_view;
      TextView var2 = (TextView)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.intercom_rating_options_layout;
         LinearLayout var3 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.intercom_rating_tell_us_more_button;
            IntercomPrimaryButton var4 = (IntercomPrimaryButton)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.intercom_you_rated_image_view;
               ImageView var5 = (ImageView)ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  var1 = id.intercom_you_rated_layout;
                  LinearLayout var6 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
                  if (var6 != null) {
                     var1 = id.rate_your_conversation_text_view;
                     TextView var7 = (TextView)ViewBindings.findChildViewById(var0, var1);
                     if (var7 != null) {
                        return new IntercomConversationRatingBlockBinding((LinearLayout)var0, var2, var3, var4, var5, var6, var7);
                     }
                  }
               }
            }
         }
      }

      String var8 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var8));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
