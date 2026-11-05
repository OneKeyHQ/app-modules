package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomMessengerCardBlockBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final ProgressBar loadingView;

   private IntercomMessengerCardBlockBinding(@NonNull FrameLayout var1, @NonNull ProgressBar var2) {
      this.rootView = var1;
      this.loadingView = var2;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomMessengerCardBlockBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomMessengerCardBlockBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_messenger_card_block, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomMessengerCardBlockBinding bind(@NonNull View var0) {
      int var1 = id.loading_view;
      ProgressBar var2 = (ProgressBar)ViewBindings.findChildViewById(var0, var1);
      if (var2 == null) {
         String var3 = var0.getResources().getResourceName(var1);
         throw new NullPointerException("Missing required view with ID: ".concat(var3));
      } else {
         return new IntercomMessengerCardBlockBinding((FrameLayout)var0, var2);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
