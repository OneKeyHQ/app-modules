package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Part;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B?\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003JG\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00052\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u000fR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow$PartWrapper;",
         "",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "isLastPart",
         "",
         "isAdminOrAltParticipant",
         "failedAttributeIdentifiers",
         "",
         "",
         "loadingAttributeIdentifiers",
         "<init>",
         "(Lio/intercom/android/sdk/models/Part;ZZLjava/util/List;Ljava/util/List;)V",
         "getPart",
         "()Lio/intercom/android/sdk/models/Part;",
         "()Z",
         "getFailedAttributeIdentifiers",
         "()Ljava/util/List;",
         "getLoadingAttributeIdentifiers",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
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
public final class ContentRow$BubbleMessageRow$PartWrapper {
   @NotNull
   private final Part part;
   private final boolean isLastPart;
   private final boolean isAdminOrAltParticipant;
   @NotNull
   private final List failedAttributeIdentifiers;
   @NotNull
   private final List loadingAttributeIdentifiers;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$BubbleMessageRow$PartWrapper(@NotNull Part var1, boolean var2, boolean var3, @NotNull List var4, @NotNull List var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var4, "failedAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var5, "loadingAttributeIdentifiers");
      this.part = var1;
      this.isLastPart = var2;
      this.isAdminOrAltParticipant = var3;
      this.failedAttributeIdentifiers = var4;
      this.loadingAttributeIdentifiers = var5;
   }

   // $VF: synthetic method
   public ContentRow$BubbleMessageRow$PartWrapper(Part var1, boolean var2, boolean var3, List var4, List var5, int var6, DefaultConstructorMarker var7) {
      this(var1, var2, var3, (((var6 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4), (((var6 & 16) != 0) ? (List) (CollectionsKt.emptyList()) : var5));
   }

   @NotNull
   public final Part getPart() {
      return this.part;
   }

   public final boolean isLastPart() {
      return this.isLastPart;
   }

   public final boolean isAdminOrAltParticipant() {
      return this.isAdminOrAltParticipant;
   }

   @NotNull
   public final List getFailedAttributeIdentifiers() {
      return this.failedAttributeIdentifiers;
   }

   @NotNull
   public final List getLoadingAttributeIdentifiers() {
      return this.loadingAttributeIdentifiers;
   }

   @NotNull
   public final Part component1() {
      return this.part;
   }

   public final boolean component2() {
      return this.isLastPart;
   }

   public final boolean component3() {
      return this.isAdminOrAltParticipant;
   }

   @NotNull
   public final List component4() {
      return this.failedAttributeIdentifiers;
   }

   @NotNull
   public final List component5() {
      return this.loadingAttributeIdentifiers;
   }

   @NotNull
   public final ContentRow$BubbleMessageRow$PartWrapper copy(@NotNull Part var1, boolean var2, boolean var3, @NotNull List var4, @NotNull List var5) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var4, "failedAttributeIdentifiers");
      Intrinsics.checkNotNullParameter(var5, "loadingAttributeIdentifiers");
      return new ContentRow$BubbleMessageRow$PartWrapper(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static ContentRow$BubbleMessageRow$PartWrapper copy$default(
      ContentRow$BubbleMessageRow$PartWrapper var0, Part var1, boolean var2, boolean var3, List var4, List var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.part;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.isLastPart;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.isAdminOrAltParticipant;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.failedAttributeIdentifiers;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.loadingAttributeIdentifiers;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "PartWrapper(part="
         + this.part
         + ", isLastPart="
         + this.isLastPart
         + ", isAdminOrAltParticipant="
         + this.isAdminOrAltParticipant
         + ", failedAttributeIdentifiers="
         + this.failedAttributeIdentifiers
         + ", loadingAttributeIdentifiers="
         + this.loadingAttributeIdentifiers
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.part.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isLastPart);
      var1 = var1 * 31 + Boolean.hashCode(this.isAdminOrAltParticipant);
      var1 = var1 * 31 + this.failedAttributeIdentifiers.hashCode();
      return var1 * 31 + this.loadingAttributeIdentifiers.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$BubbleMessageRow$PartWrapper)) {
         return false;
      } else {
         ContentRow$BubbleMessageRow$PartWrapper var2 = (ContentRow$BubbleMessageRow$PartWrapper)var1;
         if (!Intrinsics.areEqual(this.part, var2.part)) {
            return false;
         } else if (this.isLastPart != var2.isLastPart) {
            return false;
         } else if (this.isAdminOrAltParticipant != var2.isAdminOrAltParticipant) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.failedAttributeIdentifiers, var2.failedAttributeIdentifiers)
               ? false
               : Intrinsics.areEqual(this.loadingAttributeIdentifiers, var2.loadingAttributeIdentifiers);
         }
      }
   }
}
