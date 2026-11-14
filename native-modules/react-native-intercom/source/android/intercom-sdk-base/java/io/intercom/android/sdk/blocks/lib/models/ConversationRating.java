package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class ConversationRating implements Parcelable {
   private int ratingIndex;
   private String remark;
   private boolean blockRating;
   private final List options;
   public static final Creator CREATOR = new ConversationRating$1();

   ConversationRating(int var1, String var2, List var3) {
      this(var1, var2, false, var3);
   }

   ConversationRating(int var1, String var2, boolean var3, List var4) {
      this.ratingIndex = var1;
      this.remark = var2;
      this.blockRating = var3;
      this.options = var4;
   }

   public static ConversationRating fromBlock(Block var0) {
      return var0 == null
         ? new ConversationRating(-1, "", new ArrayList())
         : new ConversationRating(var0.getRatingIndex(), var0.getRemark(), var0.isBlockRating(), var0.getOptions());
   }

   public Integer getRatingIndex() {
      return this.ratingIndex;
   }

   public void setRatingIndex(int var1) {
      this.ratingIndex = var1;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setRemark(String var1) {
      this.remark = var1;
   }

   public boolean isBlockRating() {
      return this.blockRating;
   }

   public void setBlockRating(boolean var1) {
      this.blockRating = var1;
   }

   public List getOptions() {
      return this.options;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         ConversationRating var2 = (ConversationRating)var1;
         if (this.ratingIndex != var2.ratingIndex) {
            return false;
         } else if (this.blockRating != var2.blockRating) {
            return false;
         } else if (this.remark != null ? this.remark.equals(var2.remark) : var2.remark == null) {
            return this.options != null ? this.options.equals(var2.options) : var2.options == null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.ratingIndex;
      var1 = 31 * var1 + (this.blockRating ? 1 : 0);
      var1 = 31 * var1 + (this.remark != null ? this.remark.hashCode() : 0);
      return 31 * var1 + (this.options != null ? this.options.hashCode() : 0);
   }

   protected ConversationRating(Parcel var1) {
      this.ratingIndex = var1.readInt();
      this.remark = var1.readString();
      this.blockRating = var1.readByte() != 0;
      this.options = new ArrayList();
      var1.readList(this.options, ConversationRatingOption.class.getClassLoader());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeInt(this.ratingIndex);
      var1.writeString(this.remark);
      var1.writeByte((byte)(this.blockRating ? 1 : 0));
      var1.writeList(this.options);
   }
}
