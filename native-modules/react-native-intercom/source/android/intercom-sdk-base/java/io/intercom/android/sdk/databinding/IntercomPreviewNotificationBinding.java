package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomPreviewNotificationBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final ComposeView chatSnippetComposeView;
   @NonNull
   public final TextView inAppNotificationMessageSummary;
   @NonNull
   public final FrameLayout notificationRoot;
   @NonNull
   public final FrameLayout parentCard;
   @NonNull
   public final ImageView previewAvatar;
   @NonNull
   public final ComposeView previewAvatarComposeView;
   @NonNull
   public final TextView replyFromTextview;
   @NonNull
   public final ComposeView ticketHeaderComposeView;

   private IntercomPreviewNotificationBinding(
      @NonNull FrameLayout var1,
      @NonNull ComposeView var2,
      @NonNull TextView var3,
      @NonNull FrameLayout var4,
      @NonNull FrameLayout var5,
      @NonNull ImageView var6,
      @NonNull ComposeView var7,
      @NonNull TextView var8,
      @NonNull ComposeView var9
   ) {
      this.rootView = var1;
      this.chatSnippetComposeView = var2;
      this.inAppNotificationMessageSummary = var3;
      this.notificationRoot = var4;
      this.parentCard = var5;
      this.previewAvatar = var6;
      this.previewAvatarComposeView = var7;
      this.replyFromTextview = var8;
      this.ticketHeaderComposeView = var9;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomPreviewNotificationBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomPreviewNotificationBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_preview_notification, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomPreviewNotificationBinding bind(@NonNull View var0) {
      int var1 = id.chat_snippet_compose_view;
      ComposeView var2 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.in_app_notification_message_summary;
         TextView var3 = (TextView)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            FrameLayout var4 = (FrameLayout)var0;
            var1 = id.parent_card;
            FrameLayout var5 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
            if (var5 != null) {
               var1 = id.preview_avatar;
               ImageView var6 = (ImageView)ViewBindings.findChildViewById(var0, var1);
               if (var6 != null) {
                  var1 = id.preview_avatar_compose_view;
                  ComposeView var7 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
                  if (var7 != null) {
                     var1 = id.reply_from_textview;
                     TextView var8 = (TextView)ViewBindings.findChildViewById(var0, var1);
                     if (var8 != null) {
                        var1 = id.ticket_header_compose_view;
                        ComposeView var9 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
                        if (var9 != null) {
                           return new IntercomPreviewNotificationBinding((FrameLayout)var0, var2, var3, var4, var5, var6, var7, var8, var9);
                        }
                     }
                  }
               }
            }
         }
      }

      String var10 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var10));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
