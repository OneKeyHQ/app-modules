package io.intercom.android.sdk.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.compose.ui.platform.ComposeView;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.notification.InAppNotificationCardKt;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.AvatarUtils;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;

class ChatSnippet extends ChatNotification {
   private TextView contentBody;

   public ChatSnippet(Context var1, Conversation var2, int var3, int var4, InAppNotification$Listener var5, Provider var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected ViewGroup inflateChatRootView(ViewGroup var1, LayoutInflater var2) {
      ViewGroup var3 = (ViewGroup)var2.inflate(layout.intercom_preview_chat_snippet_overlay, var1, false);
      this.contentBody = (TextView)var3.findViewById(id.chathead_text_body);
      return var3;
   }

   @Override
   protected View getContentContainer() {
      return this.overlayRoot.findViewById(id.chathead_text_body);
   }

   @Override
   public void update(Conversation var1, Runnable var2) {
      this.conversation = var1;
      View var3 = this.overlayRoot.findViewById(id.chathead_text_body);
      View var4 = this.overlayRoot.findViewById(id.chathead_text_container);
      var4.setPivotX(0.0F);
      this.performReplyPulse(var4, var3, var2);
   }

   @Override
   protected void updateViewDataDuringReplyPulse(int var1) {
      Part var2 = this.conversation.lastPart();
      ImageView var3 = (ImageView)this.overlayRoot.findViewById(id.chathead_avatar);
      AvatarUtils.loadAvatarIntoView(PartExtensionsKt.resolvedAvatar(var2), var3, (AppConfig)this.appConfigProvider.get());
      TextView var4 = (TextView)this.overlayRoot.findViewById(id.chathead_text_header);
      var4.setText(this.getHeaderText());
      this.setBodyText(var2);
      ComposeView var5 = (ComposeView)this.overlayRoot.findViewById(id.ticket_header_compose_view);
      if (!ComposeCompatibilityUtilKt.hideForLegacyActivity(var5)) {
         InAppNotificationCardKt.addTicketHeaderToCompose(var5, this.conversation);
         if (this.conversation.getTicket() != null) {
            var4.setVisibility(8);
            var5.setVisibility(0);
         } else {
            var4.setVisibility(0);
            var5.setVisibility(8);
         }
      } else {
         var4.setVisibility(0);
      }

      ComposeView var6 = (ComposeView)this.overlayRoot.findViewById(id.chathead_avatar_compose_view);
      if (!ComposeCompatibilityUtilKt.hideForLegacyActivity(var6)) {
         ComposeCompatibilityUtilKt.addAvatarIconToCompose(var6, var2, (AppConfig)this.appConfigProvider.get());
         var3.setVisibility(8);
         var6.setVisibility(0);
      } else {
         var3.setVisibility(0);
      }
   }

   @Override
   void populateViewsWithData() {
      ComposeView var1 = (ComposeView)this.overlayRoot.findViewById(id.chat_snippet_compose_view);
      if (ComposeCompatibilityUtilKt.hideForLegacyActivity(var1)) {
         this.setupLegacyNotification();
      } else {
         this.setupComposeNotification(var1);
      }
   }

   private void setupComposeNotification(ComposeView var1) {
      InAppNotificationCardKt.addInappNotificationCardToView(var1, this.conversation);
      var1.setVisibility(0);
      FrameLayout var2 = (FrameLayout)this.overlayRoot.findViewById(id.parent_card);
      if (var2 != null) {
         var2.setVisibility(8);
      }
   }

   private void setupLegacyNotification() {
      FrameLayout var1 = (FrameLayout)this.overlayRoot.findViewById(id.parent_card);
      if (var1 != null) {
         var1.setVisibility(0);
      }

      TextView var2 = (TextView)this.overlayRoot.findViewById(id.chathead_text_header);
      if (var2 != null) {
         var2.setText(this.getHeaderText());
      }

      Part var3 = this.conversation.lastPart();
      this.setBodyText(var3);
      ImageView var4 = (ImageView)this.overlayRoot.findViewById(id.chathead_avatar);
      if (var4 != null) {
         AvatarUtils.loadAvatarIntoView(PartExtensionsKt.resolvedAvatar(var3), var4, (AppConfig)this.appConfigProvider.get());
      }
   }

   private void setBodyText(Part var1) {
      if (var1.getSummary().isEmpty()) {
         this.contentBody.setText(this.localisedContext.getString(string.intercom_image_attached));
      } else if (var1.getEventData().getCustomStatePrefix() != null && !var1.getEventData().getCustomStatePrefix().isEmpty()) {
         this.contentBody.setText(var1.getEventData().getCustomStatePrefix());
      } else {
         this.contentBody.setText(var1.getSummary());
      }
   }
}
