package io.intercom.android.sdk.ui.theme;

import androidx.compose.material3.Typography;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.TextUnitKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0000\u001a\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0001H\u0000\"\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b",
   d2 = {
         "defaultIntercomTypography",
         "Lio/intercom/android/sdk/ui/theme/IntercomTypography;",
         "toMaterialTypography",
         "Landroidx/compose/material3/Typography;",
         "LocalIntercomTypography",
         "Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "getLocalIntercomTypography",
         "()Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "intercom-sdk-ui_release"
   }
)
public final class IntercomTypographyKt {
   @NotNull
   private static final ProvidableCompositionLocal LocalIntercomTypography = CompositionLocalKt.staticCompositionLocalOf(
      ((kotlin.jvm.functions.Function0) () -> IntercomTypographyKt.LocalIntercomTypography$lambda$0())
   );

   @NotNull
   public static final IntercomTypography defaultIntercomTypography() {
      long var0 = TextUnitKt.getSp(32);
      long var2 = TextUnitKt.getSp(48);
      FontWeight var4 = FontWeight.Companion.getBold();
      TextStyle var10002 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(28);
      var2 = TextUnitKt.getSp(32);
      var4 = FontWeight.Companion.getSemiBold();
      TextStyle var10003 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(20);
      var2 = TextUnitKt.getSp(24);
      var4 = FontWeight.Companion.getSemiBold();
      TextStyle var10004 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(16);
      var2 = TextUnitKt.getSp(20);
      var4 = FontWeight.Companion.getNormal();
      TextStyle var10005 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(16);
      var2 = TextUnitKt.getSp(20);
      var4 = FontWeight.Companion.getSemiBold();
      TextStyle var10006 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(14);
      var2 = TextUnitKt.getSp(18);
      var4 = FontWeight.Companion.getNormal();
      TextStyle var10007 = new TextStyle(
         0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
      );
      var0 = TextUnitKt.getSp(12);
      var2 = TextUnitKt.getSp(18);
      var4 = FontWeight.Companion.getNormal();
      return new IntercomTypography(
         var10002,
         var10003,
         var10004,
         var10005,
         var10006,
         var10007,
         new TextStyle(
            0L, var0, var4, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, var2, null, null, null, 0, 0, null, 16646137, null
         )
      );
   }

   @NotNull
   public static final Typography toMaterialTypography(@NotNull IntercomTypography var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return Typography.copy$default(
         new Typography(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 32767, null),
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         var0.getType04(),
         var0.getType04Point5(),
         null,
         null,
         var0.getType05(),
         null,
         23039,
         null
      );
   }

   @NotNull
   public static final ProvidableCompositionLocal getLocalIntercomTypography() {
      return LocalIntercomTypography;
   }

   private static final IntercomTypography LocalIntercomTypography$lambda$0() {
      return defaultIntercomTypography();
   }
}
