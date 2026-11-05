package io.intercom.android.sdk.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.compose.ui.platform.ComposeView;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.UploadingImageCache;
import io.intercom.android.sdk.blocks.ViewHolderGenerator;
import io.intercom.android.sdk.blocks.lib.Blocks;
import io.intercom.android.sdk.blocks.lib.BlocksViewHolder;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.notification.ChatFullNotificationKt;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.utilities.AvatarUtils;

class ChatFull extends ChatNotification {
   private final MetricTracker metricTracker;

   ChatFull(Context var1, Conversation var2, int var3, int var4, InAppNotification$Listener var5, MetricTracker var6, Provider var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.metricTracker = var6;
   }

   @Override
   protected ViewGroup inflateChatRootView(ViewGroup var1, LayoutInflater var2) {
      ViewGroup var3 = (ViewGroup)var2.inflate(layout.intercom_preview_chat_full_overlay, var1, false);
      this.metricTracker.viewedInApp(this.conversation.getId(), this.conversation.lastPart().getId(), 1);
      return var3;
   }

   @Override
   protected View getContentContainer() {
      ComposeView var1 = (ComposeView)this.overlayRoot.findViewById(id.chat_full_compose_view);
      return var1 != null && var1.getVisibility() == 0 ? var1 : ((ViewGroup)this.overlayRoot.findViewById(id.chathead_text_container)).getChildAt(0);
   }

   @Override
   public void update(Conversation var1, Runnable var2) {
      this.conversation = var1;
      var2.run();
   }

   @Override
   void populateViewsWithData() {
      ComposeView var1 = (ComposeView)this.overlayRoot.findViewById(id.chat_full_compose_view);
      if (ComposeCompatibilityUtilKt.hideForLegacyActivity(var1)) {
         this.setupLegacyNotification();
      } else {
         this.setupComposeNotification(var1);
      }
   }

   private void setupComposeNotification(ComposeView var1) {
      ChatFullNotificationKt.addChatFullNotificationToView(var1, this.conversation, (AppConfig)this.appConfigProvider.get());
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

      Blocks var2 = new Blocks(this.localisedContext, LumberMill.getBlocksTwig());
      Injector var3 = Injector.get();
      Api var4 = var3.getApi();
      ViewHolderGenerator var5 = new ViewHolderGenerator(
         new UploadingImageCache(),
         var4,
         this.appConfigProvider,
         this.conversation.getId(),
         new ChatFull$ChatFullImageClickListener(null),
         new ChatFull$ChatFullButtonClickListener(null),
         var3.getGson(),
         this.metricTracker
      );
      BlocksViewHolder var6 = var5.getChatFullHolder();
      View var7 = var2.createBlocks(this.conversation.lastPart().getBlocks(), var6);
      ViewGroup var8 = (ViewGroup)this.overlayRoot.findViewById(id.chathead_text_container);
      var8.addView(var7);
      var8.getViewTreeObserver().addOnPreDrawListener(new ChatFull$1(this, var8));
      TextView var9 = (TextView)this.overlayRoot.findViewById(id.chathead_text_header);
      if (var9 != null) {
         var9.setText(this.getHeaderText());
      }

      ImageView var10 = (ImageView)this.overlayRoot.findViewById(id.chathead_avatar);
      if (var10 != null) {
         AvatarUtils.loadAvatarIntoView(this.conversation.lastPart().getParticipant().getAvatar(), var10, (AppConfig)this.appConfigProvider.get());
      }

      var4.markConversationAsRead(this.conversation.getId());
   }

   @Override
   protected void updateViewDataDuringReplyPulse(int var1) {
   }
}
