package io.intercom.android.sdk.activities;

import io.intercom.android.sdk.conversation.ReactionListener;
import io.intercom.android.sdk.views.ContentAwareScrollView;
import io.intercom.android.sdk.views.ContentAwareScrollView$Listener;

class IntercomNoteActivity$2 implements ContentAwareScrollView$Listener {
   // $VF: synthetic field
   final ContentAwareScrollView val$noteView;
   // $VF: synthetic field
   final ReactionListener val$reactionListener;
   // $VF: synthetic field
   final IntercomNoteActivity this$0;

   IntercomNoteActivity$2(IntercomNoteActivity var1, ContentAwareScrollView var2, ReactionListener var3) {
      this.this$0 = var1;
      this.val$noteView = var2;
      this.val$reactionListener = var3;
   }

   @Override
   public void onBottomReached() {
      this.val$noteView.setListener(null);
      this.this$0.composerLayout.setVisibility(0);
      this.this$0.reactionComposer.setUpReactions(this.this$0.part.getReactionReply(), true, this.val$reactionListener);
   }

   @Override
   public void onScrollChanged(int var1) {
   }
}
