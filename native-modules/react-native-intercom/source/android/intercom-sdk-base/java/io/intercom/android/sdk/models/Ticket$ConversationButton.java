package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u0015B\u0019\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0005H×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$ConversationButton;",
         "",
         "icon",
         "Lio/intercom/android/sdk/models/Ticket$ConversationButton$IconType;",
         "text",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/Ticket$ConversationButton$IconType;Ljava/lang/String;)V",
         "getIcon",
         "()Lio/intercom/android/sdk/models/Ticket$ConversationButton$IconType;",
         "getText",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "IconType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Ticket$ConversationButton {
   @SerializedName("icon")
   @Nullable
   private final Ticket$ConversationButton$IconType icon;
   @SerializedName("text")
   @NotNull
   private final String text;
   public static int $stable;

   static {}

   public Ticket$ConversationButton(@Nullable Ticket$ConversationButton$IconType var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "text");
      this.icon = var1;
      this.text = var2;
   }

   @Nullable
   public final Ticket$ConversationButton$IconType getIcon() {
      return this.icon;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final Ticket$ConversationButton$IconType component1() {
      return this.icon;
   }

   @NotNull
   public final String component2() {
      return this.text;
   }

   @NotNull
   public final Ticket$ConversationButton copy(@Nullable Ticket$ConversationButton$IconType var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var2, "text");
      return new Ticket$ConversationButton(var1, var2);
   }

   // $VF: synthetic method
   public static Ticket$ConversationButton copy$default(
      Ticket$ConversationButton var0, Ticket$ConversationButton$IconType var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.icon;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.text;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationButton(icon=" + this.icon + ", text=" + this.text + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.icon == null ? 0 : this.icon.hashCode();
      return var1 * 31 + this.text.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Ticket$ConversationButton)) {
         return false;
      }

      Ticket$ConversationButton var2 = (Ticket$ConversationButton)var1;
      return this.icon != var2.icon ? false : Intrinsics.areEqual(this.text, var2.text);
   }
}
