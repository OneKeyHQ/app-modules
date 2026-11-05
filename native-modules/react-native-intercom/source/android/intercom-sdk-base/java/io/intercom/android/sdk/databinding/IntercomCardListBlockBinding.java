package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomCardListBlockBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final LinearLayout cardLinks;

   private IntercomCardListBlockBinding(@NonNull FrameLayout var1, @NonNull LinearLayout var2) {
      this.rootView = var1;
      this.cardLinks = var2;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomCardListBlockBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomCardListBlockBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_card_list_block, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomCardListBlockBinding bind(@NonNull View var0) {
      int var1 = id.card_links;
      LinearLayout var2 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 == null) {
         String var3 = var0.getResources().getResourceName(var1);
         throw new NullPointerException("Missing required view with ID: ".concat(var3));
      } else {
         return new IntercomCardListBlockBinding((FrameLayout)var0, var2);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
