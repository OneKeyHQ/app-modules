package io.intercom.android.sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;

public final class IntercomPreviewChatFullOverlayBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final FrameLayout chatAvatarContainer;
   @NonNull
   public final FrameLayout chatFullBody;
   @NonNull
   public final ComposeView chatFullComposeView;
   @NonNull
   public final View chatOverlayOverflowFade;
   @NonNull
   public final ImageView chatheadAvatar;
   @NonNull
   public final ComposeView chatheadAvatarComposeView;
   @NonNull
   public final FrameLayout chatheadRoot;
   @NonNull
   public final LinearLayout chatheadTextContainer;
   @NonNull
   public final TextView chatheadTextHeader;
   @NonNull
   public final FrameLayout parentCard;

   private IntercomPreviewChatFullOverlayBinding(
      @NonNull FrameLayout var1,
      @NonNull FrameLayout var2,
      @NonNull FrameLayout var3,
      @NonNull ComposeView var4,
      @NonNull View var5,
      @NonNull ImageView var6,
      @NonNull ComposeView var7,
      @NonNull FrameLayout var8,
      @NonNull LinearLayout var9,
      @NonNull TextView var10,
      @NonNull FrameLayout var11
   ) {
      this.rootView = var1;
      this.chatAvatarContainer = var2;
      this.chatFullBody = var3;
      this.chatFullComposeView = var4;
      this.chatOverlayOverflowFade = var5;
      this.chatheadAvatar = var6;
      this.chatheadAvatarComposeView = var7;
      this.chatheadRoot = var8;
      this.chatheadTextContainer = var9;
      this.chatheadTextHeader = var10;
      this.parentCard = var11;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomPreviewChatFullOverlayBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomPreviewChatFullOverlayBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_preview_chat_full_overlay, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomPreviewChatFullOverlayBinding bind(@NonNull View var0) {
      int var1 = id.chat_avatar_container;
      FrameLayout var2 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.chat_full_body;
         FrameLayout var3 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.chat_full_compose_view;
            ComposeView var4 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.chat_overlay_overflow_fade;
               View var5 = ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  var1 = id.chathead_avatar;
                  ImageView var6 = (ImageView)ViewBindings.findChildViewById(var0, var1);
                  if (var6 != null) {
                     var1 = id.chathead_avatar_compose_view;
                     ComposeView var7 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
                     if (var7 != null) {
                        FrameLayout var8 = (FrameLayout)var0;
                        var1 = id.chathead_text_container;
                        LinearLayout var9 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
                        if (var9 != null) {
                           var1 = id.chathead_text_header;
                           TextView var10 = (TextView)ViewBindings.findChildViewById(var0, var1);
                           if (var10 != null) {
                              var1 = id.parent_card;
                              FrameLayout var11 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
                              if (var11 != null) {
                                 return new IntercomPreviewChatFullOverlayBinding(
                                    (FrameLayout)var0, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11
                                 );
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      String var12 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var12));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
