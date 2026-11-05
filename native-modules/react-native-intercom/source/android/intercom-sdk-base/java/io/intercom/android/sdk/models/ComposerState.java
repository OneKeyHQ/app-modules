package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000e\b\u0081\b\u0018\u0000 \u001e2\u00020\u0001:\u0002\u001d\u001eB)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0006\u0010\u0012\u001a\u00020\u0005J\u0006\u0010\u0013\u001a\u00020\u0005J\t\u0010\u0014\u001a\u00020\u0003HÂ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\bHÆ\u0003J1\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00052\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\bH×\u0001J\t\u0010\u001c\u001a\u00020\u0003H×\u0001R\u0010\u0010\u0002\u001a\u00020\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/models/ComposerState;",
         "",
         "visibility",
         "",
         "isWorkflowActive",
         "",
         "placeholder",
         "version",
         "",
         "<init>",
         "(Ljava/lang/String;ZLjava/lang/String;I)V",
         "()Z",
         "getPlaceholder",
         "()Ljava/lang/String;",
         "getVersion",
         "()I",
         "getComposerVisibility",
         "Lio/intercom/android/sdk/models/ComposerState$ComposerVisibility;",
         "isVisible",
         "isDisabled",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "ComposerVisibility",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ComposerState {
   @NotNull
   public static final ComposerState$Companion Companion = new ComposerState$Companion(null);
   @SerializedName("visibility")
   @NotNull
   private final String visibility;
   @SerializedName("workflow_active")
   private final boolean isWorkflowActive;
   @SerializedName("placeholder")
   @NotNull
   private final String placeholder;
   @SerializedName("version")
   private final int version;
   public static int $stable;

   static {}
   @NotNull
   private static final ComposerState NULL = new ComposerState("visible_and_enabled", false, "", 0);

   public ComposerState(@NotNull String var1, boolean var2, @NotNull String var3, int var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "visibility");
      Intrinsics.checkNotNullParameter(var3, "placeholder");
      this.visibility = var1;
      this.isWorkflowActive = var2;
      this.placeholder = var3;
      this.version = var4;
   }

   // $VF: synthetic method
   public ComposerState(String var1, boolean var2, String var3, int var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (int) (0) : var4));
   }

   public final boolean isWorkflowActive() {
      return this.isWorkflowActive;
   }

   @NotNull
   public final String getPlaceholder() {
      return this.placeholder;
   }

   public final int getVersion() {
      return this.version;
   }

   private final ComposerState$ComposerVisibility getComposerVisibility() {
      String var1 = this.visibility;
      switch (var1.hashCode()) {
         case -1217487446:
            if (var1.equals("hidden")) {
               return ComposerState$ComposerVisibility$Hidden.INSTANCE;
            }
            break;
         case -888711535:
            if (var1.equals("visible_and_disabled")) {
               return ComposerState$ComposerVisibility$VisibleAndDisabled.INSTANCE;
            }
            break;
         case 1262491788:
            if (var1.equals("visible_and_enabled")) {
               return ComposerState$ComposerVisibility$VisibleAndEnabled.INSTANCE;
            }
      }

      return ComposerState$ComposerVisibility$VisibleAndEnabled.INSTANCE;
   }

   public final boolean isVisible() {
      return !Intrinsics.areEqual(this.getComposerVisibility(), ComposerState$ComposerVisibility$Hidden.INSTANCE);
   }

   public final boolean isDisabled() {
      return Intrinsics.areEqual(this.getComposerVisibility(), ComposerState$ComposerVisibility$VisibleAndDisabled.INSTANCE);
   }

   private final String component1() {
      return this.visibility;
   }

   public final boolean component2() {
      return this.isWorkflowActive;
   }

   @NotNull
   public final String component3() {
      return this.placeholder;
   }

   public final int component4() {
      return this.version;
   }

   @NotNull
   public final ComposerState copy(@NotNull String var1, boolean var2, @NotNull String var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "visibility");
      Intrinsics.checkNotNullParameter(var3, "placeholder");
      return new ComposerState(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ComposerState copy$default(ComposerState var0, String var1, boolean var2, String var3, int var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.visibility;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.isWorkflowActive;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.placeholder;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.version;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ComposerState(visibility="
         + this.visibility
         + ", isWorkflowActive="
         + this.isWorkflowActive
         + ", placeholder="
         + this.placeholder
         + ", version="
         + this.version
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.visibility.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isWorkflowActive);
      var1 = var1 * 31 + this.placeholder.hashCode();
      return var1 * 31 + Integer.hashCode(this.version);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ComposerState)) {
         return false;
      } else {
         ComposerState var2 = (ComposerState)var1;
         if (!Intrinsics.areEqual(this.visibility, var2.visibility)) {
            return false;
         } else if (this.isWorkflowActive != var2.isWorkflowActive) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.placeholder, var2.placeholder) ? false : this.version == var2.version;
         }
      }
   }

   // $VF: synthetic method
   public static final ComposerState access$getNULL$cp() {
      return NULL;
   }
}
