package io.intercom.android.sdk.blocks.lib.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001BU\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\b¢\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\bHÆ\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\nHÆ\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010 \u001a\u00020\bHÆ\u0003J_\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\f\u001a\u00020\bHÇ\u0001J\u0013\u0010\"\u001a\u00020\b2\b\u0010#\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010$\u001a\u00020%H×\u0001J\t\u0010&\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0018\u0010\t\u001a\u0004\u0018\u00010\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0018\u0010\u000b\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0016\u0010\f\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0015¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/FormField;",
         "",
         "id",
         "",
         "name",
         "value",
         "type",
         "required",
         "",
         "options",
         "Lio/intercom/android/sdk/blocks/lib/models/Options;",
         "identifier",
         "isControllingAttribute",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/blocks/lib/models/Options;Ljava/lang/String;Z)V",
         "getId",
         "()Ljava/lang/String;",
         "getName",
         "getValue",
         "getType",
         "getRequired",
         "()Z",
         "getOptions",
         "()Lio/intercom/android/sdk/blocks/lib/models/Options;",
         "getIdentifier",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class FormField {
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("value")
   @Nullable
   private final String value;
   @SerializedName("type")
   @NotNull
   private final String type;
   @SerializedName("required")
   private final boolean required;
   @SerializedName("options")
   @Nullable
   private final Options options;
   @SerializedName("identifier")
   @Nullable
   private final String identifier;
   @SerializedName("is_controlling_attribute")
   private final boolean isControllingAttribute;
   public static final int $stable;

   static { $stable = 8; }

   public FormField(
      @NotNull String var1,
      @NotNull String var2,
      @Nullable String var3,
      @NotNull String var4,
      boolean var5,
      @Nullable Options var6,
      @Nullable String var7,
      boolean var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var4, "type");
      this.id = var1;
      this.name = var2;
      this.value = var3;
      this.type = var4;
      this.required = var5;
      this.options = var6;
      this.identifier = var7;
      this.isControllingAttribute = var8;
   }

   // $VF: synthetic method
   public FormField(
      String var1, String var2, String var3, String var4, boolean var5, Options var6, String var7, boolean var8, int var9, DefaultConstructorMarker var10
   ) {
      this(var1, var2, (((var9 & 4) != 0) ? (String) (null) : var3), var4, var5, (((var9 & 32) != 0) ? (Options) (null) : var6), (((var9 & 64) != 0) ? (String) (null) : var7), (((var9 & 128) != 0) ? (boolean) (false) : var8));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @Nullable
   public final String getValue() {
      return this.value;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   public final boolean getRequired() {
      return this.required;
   }

   @Nullable
   public final Options getOptions() {
      return this.options;
   }

   @Nullable
   public final String getIdentifier() {
      return this.identifier;
   }

   public final boolean isControllingAttribute() {
      return this.isControllingAttribute;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @Nullable
   public final String component3() {
      return this.value;
   }

   @NotNull
   public final String component4() {
      return this.type;
   }

   public final boolean component5() {
      return this.required;
   }

   @Nullable
   public final Options component6() {
      return this.options;
   }

   @Nullable
   public final String component7() {
      return this.identifier;
   }

   public final boolean component8() {
      return this.isControllingAttribute;
   }

   @NotNull
   public final FormField copy(
      @NotNull String var1,
      @NotNull String var2,
      @Nullable String var3,
      @NotNull String var4,
      boolean var5,
      @Nullable Options var6,
      @Nullable String var7,
      boolean var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var4, "type");
      return new FormField(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static FormField copy$default(
      FormField var0, String var1, String var2, String var3, String var4, boolean var5, Options var6, String var7, boolean var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.name;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.value;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.type;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.required;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.options;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.identifier;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.isControllingAttribute;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "FormField(id="
         + this.id
         + ", name="
         + this.name
         + ", value="
         + this.value
         + ", type="
         + this.type
         + ", required="
         + this.required
         + ", options="
         + this.options
         + ", identifier="
         + this.identifier
         + ", isControllingAttribute="
         + this.isControllingAttribute
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.name.hashCode();
      var1 = var1 * 31 + (this.value == null ? 0 : this.value.hashCode());
      var1 = var1 * 31 + this.type.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.required);
      var1 = var1 * 31 + (this.options == null ? 0 : this.options.hashCode());
      var1 = var1 * 31 + (this.identifier == null ? 0 : this.identifier.hashCode());
      return var1 * 31 + Boolean.hashCode(this.isControllingAttribute);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FormField)) {
         return false;
      } else {
         FormField var2 = (FormField)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.value, var2.value)) {
            return false;
         } else if (!Intrinsics.areEqual(this.type, var2.type)) {
            return false;
         } else if (this.required != var2.required) {
            return false;
         } else if (!Intrinsics.areEqual(this.options, var2.options)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.identifier, var2.identifier) ? false : this.isControllingAttribute == var2.isControllingAttribute;
         }
      }
   }
}
