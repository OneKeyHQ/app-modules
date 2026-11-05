package io.intercom.android.sdk.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.EditText;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.models.ReactionReply;

class IntercomNoteActivity$3 extends AnimatorListenerAdapter {
   // $VF: synthetic field
   final IntercomNoteActivity this$0;

   IntercomNoteActivity$3(IntercomNoteActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onAnimationEnd(Animator var1) {
      this.this$0.animateContent();
      if (IntercomNoteActivity.access$100(this.this$0) && ReactionReply.isNull(this.this$0.part.getReactionReply())) {
         this.this$0.composerLayout.setVisibility(0);
         EditText var2 = (EditText)this.this$0.findViewById(id.composer_input_view);
         View var3 = this.this$0.findViewById(id.note_touch_target);
         var3.setOnClickListener(this.this$0);
         IntercomNoteActivity.access$200(this.this$0, var2);
         var2.setVisibility(0);
      }
   }
}
