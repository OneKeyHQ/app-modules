package io.intercom.android.sdk.m5.components.avatar;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.utilities.ThemeUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\u0011\u001a\u00020\fH\u0007¢\u0006\u0002\u0010\u0012J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0016\u001a\u00020\u00052\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\fH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\nR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000e¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "isBot",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/Avatar;Z)V",
         "getAvatar",
         "()Lio/intercom/android/sdk/models/Avatar;",
         "()Z",
         "initials",
         "",
         "getInitials",
         "()Ljava/lang/String;",
         "label",
         "getLabel",
         "getImageUrl",
         "(Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AvatarWrapper {
   @NotNull
   public static final AvatarWrapper$Companion Companion = new AvatarWrapper$Companion(null);
   @NotNull
   private final Avatar avatar;
   private final boolean isBot;
   @NotNull
   private final String initials;
   @NotNull
   private final String label;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final AvatarWrapper NULL;

   public AvatarWrapper(@NotNull Avatar var1, boolean var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "avatar");
      this.avatar = var1;
      this.isBot = var2;
      String var10001 = this.avatar.getInitials();
      Intrinsics.checkNotNullExpressionValue(var10001, "getInitials(...)");
      this.initials = var10001;
      var10001 = this.avatar.getLabel();
      Intrinsics.checkNotNullExpressionValue(var10001, "getLabel(...)");
      this.label = var10001;
   }

   // $VF: synthetic method
   public AvatarWrapper(Avatar var1, boolean var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (boolean) (false) : var2));
   }

   @NotNull
   public final Avatar getAvatar() {
      return this.avatar;
   }

   public final boolean isBot() {
      return this.isBot;
   }

   @NotNull
   public final String getInitials() {
      return this.initials;
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   @Composable
   @NotNull
   public final String getImageUrl(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(1575443384);
      ComposerKt.sourceInformation(var1, "C(getImageUrl)85@3236L106:AvatarIcon.kt#ssa3oh");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1575443384, var2, -1, "io.intercom.android.sdk.m5.components.avatar.AvatarWrapper.getImageUrl (AvatarIcon.kt:85)");
      }

      String var10000 = ThemeUtils.INSTANCE
         .selectUrlForThemeCompose$intercom_sdk_base_release(this.avatar.getImageUrl(), this.avatar.getImageDarkUrl(), var1, 384);
      if (var10000 == null) {
         var10000 = "";
      }

      String var3 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   @NotNull
   public final Avatar component1() {
      return this.avatar;
   }

   public final boolean component2() {
      return this.isBot;
   }

   @NotNull
   public final AvatarWrapper copy(@NotNull Avatar var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "avatar");
      return new AvatarWrapper(var1, var2);
   }

   // $VF: synthetic method
   public static AvatarWrapper copy$default(AvatarWrapper var0, Avatar var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.avatar;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.isBot;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AvatarWrapper(avatar=" + this.avatar + ", isBot=" + this.isBot + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatar.hashCode();
      return var1 * 31 + Boolean.hashCode(this.isBot);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AvatarWrapper)) {
         return false;
      }

      AvatarWrapper var2 = (AvatarWrapper)var1;
      return !Intrinsics.areEqual(this.avatar, var2.avatar) ? false : this.isBot == var2.isBot;
   }

   // $VF: synthetic method
   public static final AvatarWrapper access$getNULL$cp() {
      return NULL;
   }

   static {
      Avatar var10002 = Avatar.NULL;
      Intrinsics.checkNotNullExpressionValue(Avatar.NULL, "NULL");
      NULL = new AvatarWrapper(var10002, false, 2, null);
   }
}
