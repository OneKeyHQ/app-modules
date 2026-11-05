package io.intercom.android.sdk.errorreporting;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\b\u0081\b\u0018\u00002\u00020\u0001B7\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u0011J\t\u0010\u0019\u001a\u00020\tHÆ\u0003JH\u0010\u001a\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001¢\u0006\u0002\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\t2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u0007H×\u0001J\t\u0010\u001f\u001a\u00020\u0003H×\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/errorreporting/StackFrame;",
         "",
         "module",
         "",
         "function",
         "filename",
         "lineno",
         "",
         "inApp",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Z)V",
         "getModule",
         "()Ljava/lang/String;",
         "getFunction",
         "getFilename",
         "getLineno",
         "()Ljava/lang/Integer;",
         "Ljava/lang/Integer;",
         "getInApp",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Z)Lio/intercom/android/sdk/errorreporting/StackFrame;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class StackFrame {
   @Nullable
   private final String module;
   @Nullable
   private final String function;
   @Nullable
   private final String filename;
   @Nullable
   private final Integer lineno;
   @SerializedName("in_app")
   private final boolean inApp;
   public static int $stable;

   static {}

   public StackFrame(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable Integer var4, boolean var5) {
      this.module = var1;
      this.function = var2;
      this.filename = var3;
      this.lineno = var4;
      this.inApp = var5;
   }

   @Nullable
   public final String getModule() {
      return this.module;
   }

   @Nullable
   public final String getFunction() {
      return this.function;
   }

   @Nullable
   public final String getFilename() {
      return this.filename;
   }

   @Nullable
   public final Integer getLineno() {
      return this.lineno;
   }

   public final boolean getInApp() {
      return this.inApp;
   }

   @Nullable
   public final String component1() {
      return this.module;
   }

   @Nullable
   public final String component2() {
      return this.function;
   }

   @Nullable
   public final String component3() {
      return this.filename;
   }

   @Nullable
   public final Integer component4() {
      return this.lineno;
   }

   public final boolean component5() {
      return this.inApp;
   }

   @NotNull
   public final StackFrame copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable Integer var4, boolean var5) {
      return new StackFrame(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static StackFrame copy$default(StackFrame var0, String var1, String var2, String var3, Integer var4, boolean var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.module;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.function;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.filename;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.lineno;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.inApp;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "StackFrame(module="
         + this.module
         + ", function="
         + this.function
         + ", filename="
         + this.filename
         + ", lineno="
         + this.lineno
         + ", inApp="
         + this.inApp
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.module == null ? 0 : this.module.hashCode();
      var1 = var1 * 31 + (this.function == null ? 0 : this.function.hashCode());
      var1 = var1 * 31 + (this.filename == null ? 0 : this.filename.hashCode());
      var1 = var1 * 31 + (this.lineno == null ? 0 : this.lineno.hashCode());
      return var1 * 31 + Boolean.hashCode(this.inApp);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StackFrame)) {
         return false;
      } else {
         StackFrame var2 = (StackFrame)var1;
         if (!Intrinsics.areEqual(this.module, var2.module)) {
            return false;
         } else if (!Intrinsics.areEqual(this.function, var2.function)) {
            return false;
         } else if (!Intrinsics.areEqual(this.filename, var2.filename)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.lineno, var2.lineno) ? false : this.inApp == var2.inApp;
         }
      }
   }
}
