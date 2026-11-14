package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u001e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B]\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\r\u0010\u000eJ\u0006\u0010\u001c\u001a\u00020\u0007J\u0006\u0010\u001d\u001a\u00020\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\t\u0010!\u001a\u00020\u0007HÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0011\u0010#\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\nHÆ\u0003J\u0010\u0010$\u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u0019J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003HÆ\u0003Jl\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003HÇ\u0001¢\u0006\u0002\u0010'J\b\u0010(\u001a\u00020)H\u0007J\u0013\u0010*\u001a\u00020\u00072\b\u0010+\u001a\u0004\u0018\u00010,H×\u0003J\t\u0010-\u001a\u00020)H×\u0001J\t\u0010.\u001a\u00020\u0003H×\u0001J\u0018\u0010/\u001a\u0002002\u0006\u00101\u001a\u0002022\u0006\u00103\u001a\u00020)H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0018\u0010\b\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u001e\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001a\u0010\u000b\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019R\u0018\u0010\f\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0010¨\u00064",
   d2 = {
         "Lio/intercom/android/sdk/models/Attribute;",
         "Landroid/os/Parcelable;",
         "identifier",
         "",
         "name",
         "type",
         "submitted",
         "",
         "customBotControlId",
         "options",
         "",
         "multiline",
         "value",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/util/List;Ljava/lang/Boolean;Ljava/lang/String;)V",
         "getIdentifier",
         "()Ljava/lang/String;",
         "getName",
         "getType",
         "getSubmitted",
         "()Z",
         "getCustomBotControlId",
         "getOptions",
         "()Ljava/util/List;",
         "getMultiline",
         "()Ljava/lang/Boolean;",
         "Ljava/lang/Boolean;",
         "getValue",
         "hasValue",
         "getRenderType",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/util/List;Ljava/lang/Boolean;Ljava/lang/String;)Lio/intercom/android/sdk/models/Attribute;",
         "describeContents",
         "",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
public final class Attribute implements Parcelable {
   @SerializedName("identifier")
   @NotNull
   private final String identifier;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("type")
   @NotNull
   private final String type;
   @SerializedName("submitted")
   private final boolean submitted;
   @SerializedName("custom_bot_control_id")
   @Nullable
   private final String customBotControlId;
   @SerializedName("options")
   @Nullable
   private final List options;
   @SerializedName("multiline")
   @Nullable
   private final Boolean multiline;
   @SerializedName("value")
   @Nullable
   private final String value;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new Attribute$Creator();

   public Attribute(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      boolean var4,
      @Nullable String var5,
      @Nullable List var6,
      @Nullable Boolean var7,
      @Nullable String var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "identifier");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "type");
      this.identifier = var1;
      this.name = var2;
      this.type = var3;
      this.submitted = var4;
      this.customBotControlId = var5;
      this.options = var6;
      this.multiline = var7;
      this.value = var8;
   }

   // $VF: synthetic method
   public Attribute(
      String var1, String var2, String var3, boolean var4, String var5, List var6, Boolean var7, String var8, int var9, DefaultConstructorMarker var10
   ) {
      this(var1, var2, var3, var4, (((var9 & 16) != 0) ? (String) (null) : var5), (((var9 & 32) != 0) ? (List) (null) : var6), (((var9 & 64) != 0) ? (Boolean) (null) : var7), (((var9 & 128) != 0) ? (String) (null) : var8));
   }

   @NotNull
   public final String getIdentifier() {
      return this.identifier;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   public final boolean getSubmitted() {
      return this.submitted;
   }

   @Nullable
   public final String getCustomBotControlId() {
      return this.customBotControlId;
   }

   @Nullable
   public final List getOptions() {
      return this.options;
   }

   @Nullable
   public final Boolean getMultiline() {
      return this.multiline;
   }

   @Nullable
   public final String getValue() {
      return this.value;
   }

   public final boolean hasValue() {
      CharSequence var1 = this.value;
      return var1 != null && var1.length() != 0;
   }

   @NotNull
   public final String getRenderType() {
      String var1 = this.type;
      switch (var1.hashCode()) {
         case -891985903:
            if (var1.equals("string")) {
               Collection var2 = this.options;
               if (var2 != null && !var2.isEmpty()) {
                  return "list";
               }

               String var3 = this.identifier;
               switch (var3.hashCode()) {
                  case 75849770:
                     if (var3.equals("company.website")) {
                        return this.identifier;
                     }
                     break;
                  case 96619420:
                     if (var3.equals("email")) {
                        return this.identifier;
                     }
                     break;
                  case 106642798:
                     if (var3.equals("phone")) {
                        return this.identifier;
                     }
               }

               return "text";
            }
            break;
         case 3076014:
            if (var1.equals("date")) {
               return "date";
            }
            break;
         case 64711720:
            if (var1.equals("boolean")) {
               return "boolean";
            }
            break;
         case 97526364:
            if (var1.equals("float")) {
               return "float";
            }
            break;
         case 1958052158:
            if (var1.equals("integer")) {
               return "number";
            }
      }

      return "unknown";
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.identifier);
      var1.writeString(this.name);
      var1.writeString(this.type);
      var1.writeInt(this.submitted ? 1 : 0);
      var1.writeString(this.customBotControlId);
      var1.writeStringList(this.options);
      Boolean var3 = this.multiline;
      if (var3 == null) {
         var1.writeInt(0);
      } else {
         var1.writeInt(1);
         var1.writeInt(var3 ? 1 : 0);
      }

      var1.writeString(this.value);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.identifier;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @NotNull
   public final String component3() {
      return this.type;
   }

   public final boolean component4() {
      return this.submitted;
   }

   @Nullable
   public final String component5() {
      return this.customBotControlId;
   }

   @Nullable
   public final List component6() {
      return this.options;
   }

   @Nullable
   public final Boolean component7() {
      return this.multiline;
   }

   @Nullable
   public final String component8() {
      return this.value;
   }

   @NotNull
   public final Attribute copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      boolean var4,
      @Nullable String var5,
      @Nullable List var6,
      @Nullable Boolean var7,
      @Nullable String var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "identifier");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "type");
      return new Attribute(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static Attribute copy$default(
      Attribute var0, String var1, String var2, String var3, boolean var4, String var5, List var6, Boolean var7, String var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.identifier;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.name;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.type;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.submitted;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.customBotControlId;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.options;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.multiline;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.value;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "Attribute(identifier="
         + this.identifier
         + ", name="
         + this.name
         + ", type="
         + this.type
         + ", submitted="
         + this.submitted
         + ", customBotControlId="
         + this.customBotControlId
         + ", options="
         + this.options
         + ", multiline="
         + this.multiline
         + ", value="
         + this.value
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.identifier.hashCode();
      var1 = var1 * 31 + this.name.hashCode();
      var1 = var1 * 31 + this.type.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.submitted);
      var1 = var1 * 31 + (this.customBotControlId == null ? 0 : this.customBotControlId.hashCode());
      var1 = var1 * 31 + (this.options == null ? 0 : this.options.hashCode());
      var1 = var1 * 31 + (this.multiline == null ? 0 : this.multiline.hashCode());
      return var1 * 31 + (this.value == null ? 0 : this.value.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Attribute)) {
         return false;
      } else {
         Attribute var2 = (Attribute)var1;
         if (!Intrinsics.areEqual(this.identifier, var2.identifier)) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.type, var2.type)) {
            return false;
         } else if (this.submitted != var2.submitted) {
            return false;
         } else if (!Intrinsics.areEqual(this.customBotControlId, var2.customBotControlId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.options, var2.options)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.multiline, var2.multiline) ? false : Intrinsics.areEqual(this.value, var2.value);
         }
      }
   }
}
