package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Attribute;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0007HÆ\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u000e¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "",
         "attribute",
         "Lio/intercom/android/sdk/models/Attribute;",
         "partId",
         "",
         "isFormDisabled",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/Attribute;Ljava/lang/String;Z)V",
         "getAttribute",
         "()Lio/intercom/android/sdk/models/Attribute;",
         "getPartId",
         "()Ljava/lang/String;",
         "()Z",
         "component1",
         "component2",
         "component3",
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
public final class AttributeData {
   @NotNull
   private final Attribute attribute;
   @NotNull
   private final String partId;
   private final boolean isFormDisabled;
   public static final int $stable;

   static { $stable = 8; }

   public AttributeData(@NotNull Attribute var1, @NotNull String var2, boolean var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "attribute");
      Intrinsics.checkNotNullParameter(var2, "partId");
      this.attribute = var1;
      this.partId = var2;
      this.isFormDisabled = var3;
   }

   @NotNull
   public final Attribute getAttribute() {
      return this.attribute;
   }

   @NotNull
   public final String getPartId() {
      return this.partId;
   }

   public final boolean isFormDisabled() {
      return this.isFormDisabled;
   }

   @NotNull
   public final Attribute component1() {
      return this.attribute;
   }

   @NotNull
   public final String component2() {
      return this.partId;
   }

   public final boolean component3() {
      return this.isFormDisabled;
   }

   @NotNull
   public final AttributeData copy(@NotNull Attribute var1, @NotNull String var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "attribute");
      Intrinsics.checkNotNullParameter(var2, "partId");
      return new AttributeData(var1, var2, var3);
   }

   // $VF: synthetic method
   public static AttributeData copy$default(AttributeData var0, Attribute var1, String var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.attribute;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.partId;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.isFormDisabled;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "AttributeData(attribute=" + this.attribute + ", partId=" + this.partId + ", isFormDisabled=" + this.isFormDisabled + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.attribute.hashCode();
      var1 = var1 * 31 + this.partId.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isFormDisabled);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AttributeData)) {
         return false;
      } else {
         AttributeData var2 = (AttributeData)var1;
         if (!Intrinsics.areEqual(this.attribute, var2.attribute)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.partId, var2.partId) ? false : this.isFormDisabled == var2.isFormDisabled;
         }
      }
   }
}
