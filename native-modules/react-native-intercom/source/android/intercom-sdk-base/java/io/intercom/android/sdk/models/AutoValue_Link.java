package io.intercom.android.sdk.models;

import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.List;
import org.jspecify.annotations.Nullable;

final class AutoValue_Link extends Link {
   private final String id;
   private final Card card;
   private final List blocks;
   private final ReactionReply reactionReply;
   private final long createdAt;
   private final long updatedAt;

   AutoValue_Link(String var1, Card var2, List var3, ReactionReply var4, long var5, long var7) {
      if (var1 == null) {
         throw new NullPointerException("Null id");
      }

      this.id = var1;
      if (var2 == null) {
         throw new NullPointerException("Null card");
      }

      this.card = var2;
      if (var3 == null) {
         throw new NullPointerException("Null blocks");
      }

      this.blocks = var3;
      if (var4 == null) {
         throw new NullPointerException("Null reactionReply");
      }

      this.reactionReply = var4;
      this.createdAt = var5;
      this.updatedAt = var7;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public Card getCard() {
      return this.card;
   }

   @Override
   public List getBlocks() {
      return this.blocks;
   }

   @Override
   public ReactionReply getReactionReply() {
      return this.reactionReply;
   }

   @Override
   public long getCreatedAt() {
      return this.createdAt;
   }

   @Override
   public long getUpdatedAt() {
      return this.updatedAt;
   }

   @Override
   public String toString() {
      return "Link{id="
         + this.id
         + ", card="
         + this.card
         + ", blocks="
         + this.blocks
         + ", reactionReply="
         + this.reactionReply
         + ", createdAt="
         + this.createdAt
         + ", updatedAt="
         + this.updatedAt
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Link)) {
         return false;
      }

      Link var2 = (Link)var1;
      return this.id.equals(var2.getId())
         && this.card.equals(var2.getCard())
         && this.blocks.equals(var2.getBlocks())
         && this.reactionReply.equals(var2.getReactionReply())
         && this.createdAt == var2.getCreatedAt()
         && this.updatedAt == var2.getUpdatedAt();
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.id.hashCode();
      var1 *= 1000003;
      var1 ^= this.card.hashCode();
      var1 *= 1000003;
      var1 ^= this.blocks.hashCode();
      var1 *= 1000003;
      var1 ^= this.reactionReply.hashCode();
      var1 *= 1000003;
      var1 ^= (int)(this.createdAt >>> 32 ^ this.createdAt);
      var1 *= 1000003;
      return var1 ^ (int)(this.updatedAt >>> 32 ^ this.updatedAt);
   }
}
