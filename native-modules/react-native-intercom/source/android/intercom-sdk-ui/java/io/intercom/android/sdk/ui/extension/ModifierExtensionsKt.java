package io.intercom.android.sdk.ui.extension;

import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a+\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0017\u0010\u0004\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0006¨\u0006\u0007",
   d2 = {
         "ifTrue",
         "Landroidx/compose/ui/Modifier;",
         "condition",
         "",
         "modifier",
         "Lkotlin/Function1;",
         "Lkotlin/ExtensionFunctionType;",
         "intercom-sdk-ui_release"
   }
)
public final class ModifierExtensionsKt {
   @NotNull
   public static final Modifier ifTrue(@NotNull Modifier var0, boolean var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var2, "modifier");
      return var1 ? var0.then((Modifier)var2.invoke(Modifier.Companion)) : var0;
   }
}
