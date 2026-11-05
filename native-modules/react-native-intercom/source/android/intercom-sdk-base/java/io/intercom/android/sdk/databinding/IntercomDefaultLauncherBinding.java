package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomDefaultLauncherBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final TextView launcherBadgeCount;
   @NonNull
   public final ImageButton launcherIcon;
   @NonNull
   public final FrameLayout launcherRoot;

   private IntercomDefaultLauncherBinding(@NonNull FrameLayout var1, @NonNull TextView var2, @NonNull ImageButton var3, @NonNull FrameLayout var4) {
      this.rootView = var1;
      this.launcherBadgeCount = var2;
      this.launcherIcon = var3;
      this.launcherRoot = var4;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomDefaultLauncherBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomDefaultLauncherBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_default_launcher, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomDefaultLauncherBinding bind(@NonNull View var0) {
      int var1 = id.launcher_badge_count;
      TextView var2 = (TextView)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.launcher_icon;
         ImageButton var3 = (ImageButton)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            FrameLayout var4 = (FrameLayout)var0;
            return new IntercomDefaultLauncherBinding((FrameLayout)var0, var2, var3, var4);
         }
      }

      String var5 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var5));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
