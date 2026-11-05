package io.intercom.android.sdk.m5.navigation.transitions;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.android.parcel.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0010\u001a\u00020\u0011H\u0016J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0005HÇ\u0001J\b\u0010\u0017\u001a\u00020\u0018H\u0007J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH×\u0003J\t\u0010\u001d\u001a\u00020\u0018H×\u0001J\u0018\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0018H\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "Landroid/os/Parcelable;",
         "enter",
         "Lio/intercom/android/sdk/m5/navigation/transitions/EnterTransitionStyle;",
         "exit",
         "Lio/intercom/android/sdk/m5/navigation/transitions/ExitTransitionStyle;",
         "popEnter",
         "popExit",
         "<init>",
         "(Lio/intercom/android/sdk/m5/navigation/transitions/EnterTransitionStyle;Lio/intercom/android/sdk/m5/navigation/transitions/ExitTransitionStyle;Lio/intercom/android/sdk/m5/navigation/transitions/EnterTransitionStyle;Lio/intercom/android/sdk/m5/navigation/transitions/ExitTransitionStyle;)V",
         "getEnter",
         "()Lio/intercom/android/sdk/m5/navigation/transitions/EnterTransitionStyle;",
         "getExit",
         "()Lio/intercom/android/sdk/m5/navigation/transitions/ExitTransitionStyle;",
         "getPopEnter",
         "getPopExit",
         "toString",
         "",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "describeContents",
         "",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 1)
public final class TransitionArgs implements Parcelable {
   @NotNull
   private final EnterTransitionStyle enter;
   @NotNull
   private final ExitTransitionStyle exit;
   @NotNull
   private final EnterTransitionStyle popEnter;
   @NotNull
   private final ExitTransitionStyle popExit;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new TransitionArgs$Creator();

   public TransitionArgs(
      @NotNull EnterTransitionStyle var1, @NotNull ExitTransitionStyle var2, @NotNull EnterTransitionStyle var3, @NotNull ExitTransitionStyle var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "enter");
      Intrinsics.checkNotNullParameter(var2, "exit");
      Intrinsics.checkNotNullParameter(var3, "popEnter");
      Intrinsics.checkNotNullParameter(var4, "popExit");
      this.enter = var1;
      this.exit = var2;
      this.popEnter = var3;
      this.popExit = var4;
   }

   // $VF: synthetic method
   public TransitionArgs(
      EnterTransitionStyle var1, ExitTransitionStyle var2, EnterTransitionStyle var3, ExitTransitionStyle var4, int var5, DefaultConstructorMarker var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = EnterTransitionStyle.NULL;
      }

      if ((var5 & 2) != 0) {
         var2 = ExitTransitionStyle.NULL;
      }

      if ((var5 & 4) != 0) {
         var3 = var1;
      }

      if ((var5 & 8) != 0) {
         var4 = var2;
      }

      this(var1, var2, var3, var4);
   }

   @NotNull
   public final EnterTransitionStyle getEnter() {
      return this.enter;
   }

   @NotNull
   public final ExitTransitionStyle getExit() {
      return this.exit;
   }

   @NotNull
   public final EnterTransitionStyle getPopEnter() {
      return this.popEnter;
   }

   @NotNull
   public final ExitTransitionStyle getPopExit() {
      return this.popExit;
   }

   @NotNull
   @Override
   public String toString() {
      String var10000 = Uri.encode(new Gson().toJson(this));
      Intrinsics.checkNotNullExpressionValue(var10000, "encode(...)");
      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.enter.name());
      var1.writeString(this.exit.name());
      var1.writeString(this.popEnter.name());
      var1.writeString(this.popExit.name());
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final EnterTransitionStyle component1() {
      return this.enter;
   }

   @NotNull
   public final ExitTransitionStyle component2() {
      return this.exit;
   }

   @NotNull
   public final EnterTransitionStyle component3() {
      return this.popEnter;
   }

   @NotNull
   public final ExitTransitionStyle component4() {
      return this.popExit;
   }

   @NotNull
   public final TransitionArgs copy(
      @NotNull EnterTransitionStyle var1, @NotNull ExitTransitionStyle var2, @NotNull EnterTransitionStyle var3, @NotNull ExitTransitionStyle var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "enter");
      Intrinsics.checkNotNullParameter(var2, "exit");
      Intrinsics.checkNotNullParameter(var3, "popEnter");
      Intrinsics.checkNotNullParameter(var4, "popExit");
      return new TransitionArgs(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static TransitionArgs copy$default(
      TransitionArgs var0, EnterTransitionStyle var1, ExitTransitionStyle var2, EnterTransitionStyle var3, ExitTransitionStyle var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.enter;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.exit;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.popEnter;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.popExit;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @Override
   public int hashCode() {
      int var1 = this.enter.hashCode();
      var1 = var1 * 31 + this.exit.hashCode();
      var1 = var1 * 31 + this.popEnter.hashCode();
      return var1 * 31 + this.popExit.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TransitionArgs)) {
         return false;
      } else {
         TransitionArgs var2 = (TransitionArgs)var1;
         if (this.enter != var2.enter) {
            return false;
         } else if (this.exit != var2.exit) {
            return false;
         } else {
            return this.popEnter != var2.popEnter ? false : this.popExit == var2.popExit;
         }
      }
   }

   public TransitionArgs() {
      this(null, null, null, null, 15, null);
   }
}
