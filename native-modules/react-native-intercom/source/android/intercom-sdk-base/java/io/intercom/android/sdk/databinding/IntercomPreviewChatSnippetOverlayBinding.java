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

public final class IntercomPreviewChatSnippetOverlayBinding implements ViewBinding {
   @NonNull
   private final FrameLayout rootView;
   @NonNull
   public final FrameLayout chatAvatarContainer;
   @NonNull
   public final FrameLayout chatFullBody;
   @NonNull
   public final ComposeView chatSnippetComposeView;
   @NonNull
   public final ImageView chatheadAvatar;
   @NonNull
   public final ComposeView chatheadAvatarComposeView;
   @NonNull
   public final FrameLayout chatheadRoot;
   @NonNull
   public final TextView chatheadTextBody;
   @NonNull
   public final LinearLayout chatheadTextContainer;
   @NonNull
   public final TextView chatheadTextHeader;
   @NonNull
   public final FrameLayout parentCard;
   @NonNull
   public final ComposeView ticketHeaderComposeView;

   private IntercomPreviewChatSnippetOverlayBinding(
      @NonNull FrameLayout var1,
      @NonNull FrameLayout var2,
      @NonNull FrameLayout var3,
      @NonNull ComposeView var4,
      @NonNull ImageView var5,
      @NonNull ComposeView var6,
      @NonNull FrameLayout var7,
      @NonNull TextView var8,
      @NonNull LinearLayout var9,
      @NonNull TextView var10,
      @NonNull FrameLayout var11,
      @NonNull ComposeView var12
   ) {
      this.rootView = var1;
      this.chatAvatarContainer = var2;
      this.chatFullBody = var3;
      this.chatSnippetComposeView = var4;
      this.chatheadAvatar = var5;
      this.chatheadAvatarComposeView = var6;
      this.chatheadRoot = var7;
      this.chatheadTextBody = var8;
      this.chatheadTextContainer = var9;
      this.chatheadTextHeader = var10;
      this.parentCard = var11;
      this.ticketHeaderComposeView = var12;
   }

   @NonNull
   public FrameLayout getRoot() {
      return this.rootView;
   }

   @NonNull
   public static IntercomPreviewChatSnippetOverlayBinding inflate(@NonNull LayoutInflater var0) {
      return inflate(var0, null, false);
   }

   @NonNull
   public static IntercomPreviewChatSnippetOverlayBinding inflate(@NonNull LayoutInflater var0, @Nullable ViewGroup var1, boolean var2) {
      View var3 = var0.inflate(layout.intercom_preview_chat_snippet_overlay, var1, false);
      if (var2) {
         var1.addView(var3);
      }

      return bind(var3);
   }

   @NonNull
   public static IntercomPreviewChatSnippetOverlayBinding bind(@NonNull View var0) {
      int var1 = id.chat_avatar_container;
      FrameLayout var2 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
      if (var2 != null) {
         var1 = id.chat_full_body;
         FrameLayout var3 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
         if (var3 != null) {
            var1 = id.chat_snippet_compose_view;
            ComposeView var4 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
            if (var4 != null) {
               var1 = id.chathead_avatar;
               ImageView var5 = (ImageView)ViewBindings.findChildViewById(var0, var1);
               if (var5 != null) {
                  var1 = id.chathead_avatar_compose_view;
                  ComposeView var6 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
                  if (var6 != null) {
                     FrameLayout var7 = (FrameLayout)var0;
                     var1 = id.chathead_text_body;
                     TextView var8 = (TextView)ViewBindings.findChildViewById(var0, var1);
                     if (var8 != null) {
                        var1 = id.chathead_text_container;
                        LinearLayout var9 = (LinearLayout)ViewBindings.findChildViewById(var0, var1);
                        if (var9 != null) {
                           var1 = id.chathead_text_header;
                           TextView var10 = (TextView)ViewBindings.findChildViewById(var0, var1);
                           if (var10 != null) {
                              var1 = id.parent_card;
                              FrameLayout var11 = (FrameLayout)ViewBindings.findChildViewById(var0, var1);
                              if (var11 != null) {
                                 var1 = id.ticket_header_compose_view;
                                 ComposeView var12 = (ComposeView)ViewBindings.findChildViewById(var0, var1);
                                 if (var12 != null) {
                                    return new IntercomPreviewChatSnippetOverlayBinding(
                                       (FrameLayout)var0, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12
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
      }

      String var13 = var0.getResources().getResourceName(var1);
      throw new NullPointerException("Missing required view with ID: ".concat(var13));
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
