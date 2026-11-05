package io.intercom.android.sdk.models;

import java.util.List;

public class ReactionReply$Builder {
   Integer reaction_index;
   List reaction_set;

   public ReactionReply build() {
      return new ReactionReply(this);
   }
}
