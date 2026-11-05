package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$Text;
import io.intercom.android.sdk.ui.common.StringProvider;
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
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0007HÆ\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\f0\tHÆ\u0003J\t\u0010 \u001a\u00020\u000eHÆ\u0003JQ\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t2\b\b\u0002\u0010\r\u001a\u00020\u000eHÇ\u0001J\u0013\u0010\"\u001a\u00020\u00072\b\u0010#\u001a\u0004\u0018\u00010$H×\u0003J\t\u0010%\u001a\u00020&H×\u0001J\t\u0010'\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0015R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$TextInput;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "initialMessage",
         "",
         "hintText",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "isDisabled",
         "",
         "buttons",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBarButton;",
         "mediaUploadItems",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "inputType",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/ui/common/StringProvider;ZLjava/util/List;Ljava/util/List;Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;)V",
         "getInitialMessage",
         "()Ljava/lang/String;",
         "getHintText",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "()Z",
         "getButtons",
         "()Ljava/util/List;",
         "getMediaUploadItems",
         "getInputType",
         "()Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ComposerState$TextInput implements ComposerState {
   @NotNull
   private final String initialMessage;
   @NotNull
   private final StringProvider hintText;
   private final boolean isDisabled;
   @NotNull
   private final List buttons;
   @NotNull
   private final List mediaUploadItems;
   @NotNull
   private final ComposerInputType inputType;
   public static final int $stable;

   static { $stable = 8; }

   public ComposerState$TextInput(
      @NotNull String var1, @NotNull StringProvider var2, boolean var3, @NotNull List var4, @NotNull List var5, @NotNull ComposerInputType var6
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      Intrinsics.checkNotNullParameter(var2, "hintText");
      Intrinsics.checkNotNullParameter(var4, "buttons");
      Intrinsics.checkNotNullParameter(var5, "mediaUploadItems");
      Intrinsics.checkNotNullParameter(var6, "inputType");
      this.initialMessage = var1;
      this.hintText = var2;
      this.isDisabled = var3;
      this.buttons = var4;
      this.mediaUploadItems = var5;
      this.inputType = var6;
   }

   // $VF: synthetic method
   public ComposerState$TextInput(
      String var1, StringProvider var2, boolean var3, List var4, List var5, ComposerInputType var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, (((var7 & 4) != 0) ? (boolean) (false) : var3), (((var7 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4), (((var7 & 16) != 0) ? (List) (CollectionsKt.emptyList()) : var5), (((var7 & 32) != 0) ? (ComposerInputType) (ComposerInputType$Text.INSTANCE) : var6));
   }

   @NotNull
   public final String getInitialMessage() {
      return this.initialMessage;
   }

   @NotNull
   public final StringProvider getHintText() {
      return this.hintText;
   }

   public final boolean isDisabled() {
      return this.isDisabled;
   }

   @NotNull
   public final List getButtons() {
      return this.buttons;
   }

   @NotNull
   public final List getMediaUploadItems() {
      return this.mediaUploadItems;
   }

   @NotNull
   public final ComposerInputType getInputType() {
      return this.inputType;
   }

   @NotNull
   public final String component1() {
      return this.initialMessage;
   }

   @NotNull
   public final StringProvider component2() {
      return this.hintText;
   }

   public final boolean component3() {
      return this.isDisabled;
   }

   @NotNull
   public final List component4() {
      return this.buttons;
   }

   @NotNull
   public final List component5() {
      return this.mediaUploadItems;
   }

   @NotNull
   public final ComposerInputType component6() {
      return this.inputType;
   }

   @NotNull
   public final ComposerState$TextInput copy(
      @NotNull String var1, @NotNull StringProvider var2, boolean var3, @NotNull List var4, @NotNull List var5, @NotNull ComposerInputType var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "initialMessage");
      Intrinsics.checkNotNullParameter(var2, "hintText");
      Intrinsics.checkNotNullParameter(var4, "buttons");
      Intrinsics.checkNotNullParameter(var5, "mediaUploadItems");
      Intrinsics.checkNotNullParameter(var6, "inputType");
      return new ComposerState$TextInput(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static ComposerState$TextInput copy$default(
      ComposerState$TextInput var0, String var1, StringProvider var2, boolean var3, List var4, List var5, ComposerInputType var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.initialMessage;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.hintText;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.isDisabled;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.buttons;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.mediaUploadItems;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.inputType;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "TextInput(initialMessage="
         + this.initialMessage
         + ", hintText="
         + this.hintText
         + ", isDisabled="
         + this.isDisabled
         + ", buttons="
         + this.buttons
         + ", mediaUploadItems="
         + this.mediaUploadItems
         + ", inputType="
         + this.inputType
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.initialMessage.hashCode();
      var1 = var1 * 31 + this.hintText.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isDisabled);
      var1 = var1 * 31 + this.buttons.hashCode();
      var1 = var1 * 31 + this.mediaUploadItems.hashCode();
      return var1 * 31 + this.inputType.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ComposerState$TextInput)) {
         return false;
      } else {
         ComposerState$TextInput var2 = (ComposerState$TextInput)var1;
         if (!Intrinsics.areEqual(this.initialMessage, var2.initialMessage)) {
            return false;
         } else if (!Intrinsics.areEqual(this.hintText, var2.hintText)) {
            return false;
         } else if (this.isDisabled != var2.isDisabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.buttons, var2.buttons)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.mediaUploadItems, var2.mediaUploadItems) ? false : Intrinsics.areEqual(this.inputType, var2.inputType);
         }
      }
   }
}
