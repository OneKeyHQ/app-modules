package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import io.intercom.android.sdk.R.layout;

public final class IntercomCarouselActionButtonBinding implements ViewBinding {
   @NonNull
   private final TextView rootView;

   private IntercomCarouselActionButtonBinding(@NonNull TextView var1) {
      this.rootView = var1;
   }

   @NonNull
   public TextView getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomCarouselActionButtonBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomCarouselActionButtonBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_carousel_action_button, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomCarouselActionButtonBinding bind(@NonNull View var0) {
      if (var0 == null) {
         throw new NullPointerException("rootView");
      } else {
         return new IntercomCarouselActionButtonBinding((TextView)var0);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
