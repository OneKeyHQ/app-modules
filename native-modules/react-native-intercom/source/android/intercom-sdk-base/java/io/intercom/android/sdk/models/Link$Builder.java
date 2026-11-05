package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Link$Builder {
   @Nullable
   String id;
   @Nullable
   Card$Builder card;
   @Nullable
   List blocks;
   @Nullable
   ReactionReply$Builder reactions_reply;
   long created_at;
   long updated_at;

   public Link build() {
      String var1 = this.id == null ? UUID.randomUUID().toString() : this.id;
      List var2 = new ArrayList(CollectionUtils.capacityFor(this.blocks));
      if (this.blocks != null) {
         for (java.lang.Object recoveredEntryElement_0_27 : this.blocks) {
            Block$Builder var4 = (Block$Builder) recoveredEntryElement_0_27;
            if (var4 != null) {
               var2.add(var4.build());
            }
         }
      }

      ReactionReply var5 = this.reactions_reply == null ? ReactionReply.NULL : this.reactions_reply.build();
      Card var6 = this.card == null ? new Card$Builder().build() : this.card.build();
      return new AutoValue_Link(var1, var6, var2, var5, this.created_at, this.updated_at);
   }
}
