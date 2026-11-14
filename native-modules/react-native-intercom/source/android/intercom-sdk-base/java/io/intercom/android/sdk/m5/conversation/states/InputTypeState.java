package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0018\b\u0081\b\u0018\u0000 \"2\u00020\u0001:\u0001\"B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003J\t\u0010\u001c\u001a\u00020\u000bHÆ\u0003JK\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\b\b\u0002\u0010\n\u001a\u00020\u000bHÇ\u0001J\u0013\u0010\u001e\u001a\u00020\u00032\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010 \u001a\u00020\u000bH×\u0001J\t\u0010!\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;",
         "",
         "mediaInputEnabled",
         "",
         "gifInputEnabled",
         "cameraInputEnabled",
         "fileInputEnabled",
         "trustedFileExtensions",
         "",
         "",
         "availableMediaItems",
         "",
         "<init>",
         "(ZZZZLjava/util/Set;I)V",
         "getMediaInputEnabled",
         "()Z",
         "getGifInputEnabled",
         "getCameraInputEnabled",
         "getFileInputEnabled",
         "getTrustedFileExtensions",
         "()Ljava/util/Set;",
         "getAvailableMediaItems",
         "()I",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class InputTypeState {
   @NotNull
   public static final InputTypeState$Companion Companion = new InputTypeState$Companion(null);
   private final boolean mediaInputEnabled;
   private final boolean gifInputEnabled;
   private final boolean cameraInputEnabled;
   private final boolean fileInputEnabled;
   @NotNull
   private final Set trustedFileExtensions;
   private final int availableMediaItems;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final InputTypeState DEFAULT = new InputTypeState(true, true, true, true, MediaPickerButtonKt.getDefaultTrustedFileExtensions(), 10);

   public InputTypeState(boolean var1, boolean var2, boolean var3, boolean var4, @NotNull Set var5, int var6) {
      super();
      Intrinsics.checkNotNullParameter(var5, "trustedFileExtensions");
      this.mediaInputEnabled = var1;
      this.gifInputEnabled = var2;
      this.cameraInputEnabled = var3;
      this.fileInputEnabled = var4;
      this.trustedFileExtensions = var5;
      this.availableMediaItems = var6;
   }

   public final boolean getMediaInputEnabled() {
      return this.mediaInputEnabled;
   }

   public final boolean getGifInputEnabled() {
      return this.gifInputEnabled;
   }

   public final boolean getCameraInputEnabled() {
      return this.cameraInputEnabled;
   }

   public final boolean getFileInputEnabled() {
      return this.fileInputEnabled;
   }

   @NotNull
   public final Set getTrustedFileExtensions() {
      return this.trustedFileExtensions;
   }

   public final int getAvailableMediaItems() {
      return this.availableMediaItems;
   }

   public final boolean component1() {
      return this.mediaInputEnabled;
   }

   public final boolean component2() {
      return this.gifInputEnabled;
   }

   public final boolean component3() {
      return this.cameraInputEnabled;
   }

   public final boolean component4() {
      return this.fileInputEnabled;
   }

   @NotNull
   public final Set component5() {
      return this.trustedFileExtensions;
   }

   public final int component6() {
      return this.availableMediaItems;
   }

   @NotNull
   public final InputTypeState copy(boolean var1, boolean var2, boolean var3, boolean var4, @NotNull Set var5, int var6) {
      Intrinsics.checkNotNullParameter(var5, "trustedFileExtensions");
      return new InputTypeState(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static InputTypeState copy$default(
      InputTypeState var0, boolean var1, boolean var2, boolean var3, boolean var4, Set var5, int var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.mediaInputEnabled;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.gifInputEnabled;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.cameraInputEnabled;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.fileInputEnabled;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.trustedFileExtensions;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.availableMediaItems;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "InputTypeState(mediaInputEnabled="
         + this.mediaInputEnabled
         + ", gifInputEnabled="
         + this.gifInputEnabled
         + ", cameraInputEnabled="
         + this.cameraInputEnabled
         + ", fileInputEnabled="
         + this.fileInputEnabled
         + ", trustedFileExtensions="
         + this.trustedFileExtensions
         + ", availableMediaItems="
         + this.availableMediaItems
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.mediaInputEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.gifInputEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.cameraInputEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.fileInputEnabled);
      var1 = var1 * 31 + this.trustedFileExtensions.hashCode();
      return var1 * 31 + Integer.hashCode(this.availableMediaItems);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof InputTypeState)) {
         return false;
      } else {
         InputTypeState var2 = (InputTypeState)var1;
         if (this.mediaInputEnabled != var2.mediaInputEnabled) {
            return false;
         } else if (this.gifInputEnabled != var2.gifInputEnabled) {
            return false;
         } else if (this.cameraInputEnabled != var2.cameraInputEnabled) {
            return false;
         } else if (this.fileInputEnabled != var2.fileInputEnabled) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.trustedFileExtensions, var2.trustedFileExtensions) ? false : this.availableMediaItems == var2.availableMediaItems;
         }
      }
   }

   // $VF: synthetic method
   public static final InputTypeState access$getDEFAULT$cp() {
      return DEFAULT;
   }
}
