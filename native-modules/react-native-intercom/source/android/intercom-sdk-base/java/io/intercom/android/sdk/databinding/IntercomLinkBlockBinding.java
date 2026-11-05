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

public final class IntercomLinkBlockBinding implements ViewBinding {
   @NonNull
   private final LinearLayout rootView;
   @NonNull
   public final TextView author;
   @NonNull
   public final ImageView avatar;
   @NonNull
   public final TextView description;
   @NonNull
   public final TextView title;

   private IntercomLinkBlockBinding(@NonNull LinearLayout var1, @NonNull TextView var2, @NonNull ImageView var3, @NonNull TextView var4, @NonNull TextView var5) {
      this.rootView = var1;
      this.author = var2;
      this.avatar = var3;
      this.description = var4;
      this.title = var5;
   }

   @NonNull
   public LinearLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomLinkBlockBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomLinkBlockBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_link_block, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomLinkBlockBinding bind(@NonNull View var0) {
      int var1 = id.author;
      TextView var2 = (TextView)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.avatar;
         ImageView var3 = (ImageView)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.description;
            TextView var4 = (TextView)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.title;
               TextView var5 = (TextView)ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  return new IntercomLinkBlockBinding((LinearLayout)var0, var2, var3, var4, var5);
               }
            }
         }
      }

      String var6 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var6));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
