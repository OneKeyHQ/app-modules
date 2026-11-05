package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.commons.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public class ReactionReply implements Parcelable {
   private final List reactionSet;
   @Nullable
   private Integer reactionIndex;
   public static final ReactionReply NULL = new ReactionReply(new ReactionReply$Builder());
   public static final Creator CREATOR = new ReactionReply$1();

   ReactionReply(ReactionReply$Builder var1) {
      this.reactionIndex = var1.reaction_index;
      this.reactionSet = new ArrayList(CollectionUtils.capacityFor(var1.reaction_set));
      if (var1.reaction_set != null) {
         this.reactionSet.addAll(var1.reaction_set);
      }
   }

   public List getReactionSet() {
      return this.reactionSet;
   }

   public void setReactionIndex(int var1) {
      this.reactionIndex = var1;
   }

   @Nullable
   public Integer getReactionIndex() {
      return this.reactionIndex;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         ReactionReply var2 = (ReactionReply)var1;
         if (!this.reactionSet.equals(var2.reactionSet)) {
            return false;
         } else {
            return this.reactionIndex != null ? this.reactionIndex.equals(var2.reactionIndex) : var2.reactionIndex == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.reactionSet.hashCode();
      return 31 * var1 + (this.reactionIndex != null ? this.reactionIndex.hashCode() : 0);
   }

   public static boolean isNull(ReactionReply var0) {
      return NULL.equals(var0) || var0 == null;
   }

   ReactionReply(Parcel var1) {
      this.reactionIndex = var1.readByte() == 0 ? null : var1.readInt();
      this.reactionSet = new ArrayList();
      var1.readList(this.reactionSet, Reaction.class.getClassLoader());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      if (this.reactionIndex == null) {
         var1.writeByte((byte)0);
      } else {
         var1.writeByte((byte)1);
         var1.writeInt(this.reactionIndex);
      }

      var1.writeList(this.reactionSet);
   }
}
