package io.intercom.android.sdk.activities;

import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.conversation.ReactionListener;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.metrics.MetricTracker$ReactionLocation;
import io.intercom.android.sdk.models.Reaction;

public class ConversationReactionListener implements ReactionListener {
   private final String conversationId;
   private final String partId;
   private final MetricTracker$ReactionLocation location;
   private final Api api;
   private final MetricTracker metricTracker;

   public ConversationReactionListener(MetricTracker$ReactionLocation var1, String var2, String var3, Api var4, MetricTracker var5) {
      this.conversationId = var3;
      this.partId = var2;
      this.location = var1;
      this.api = var4;
      this.metricTracker = var5;
   }

   @Override
   public void onReactionSelected(Reaction var1) {
      this.api.reactToConversation(this.conversationId, var1.getIndex());
      this.metricTracker.sentReaction(this.conversationId, this.partId, var1.getIndex(), this.location);
   }
}
