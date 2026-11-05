package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import io.intercom.android.sdk.R.layout;

public final class IntercomNotificationPillBinding implements ViewBinding {
   @NonNull
   private final TextView rootView;
   @NonNull
   public final TextView notificationPill;

   private IntercomNotificationPillBinding(@NonNull TextView var1, @NonNull TextView var2) {
      this.rootView = var1;
      this.notificationPill = var2;
   }

   @NonNull
   public TextView getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomNotificationPillBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomNotificationPillBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_notification_pill, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomNotificationPillBinding bind(@NonNull View var0) {
      if (var0 == null) {
         throw new NullPointerException("rootView");
      }

      TextView var1 = (TextView)var0;
      return new IntercomNotificationPillBinding((TextView)var0, var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
