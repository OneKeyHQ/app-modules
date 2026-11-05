package io.intercom.android.sdk.ui.theme;

import androidx.compose.material3.ColorScheme;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aE\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\f\u001a\b\u0010\r\u001a\u00020\u0001H\u0000\u001a\b\u0010\u000e\u001a\u00020\u0001H\u0000\u001a\f\u0010\u000f\u001a\u00020\u0010*\u00020\u0001H\u0000\"\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\" \u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001b¨\u0006\u001c",
   d2 = {
         "getIntercomColors",
         "Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "action",
         "Landroidx/compose/ui/graphics/Color;",
         "onAction",
         "actionContrastWhite",
         "onActionContrastWhite",
         "header",
         "onHeader",
         "isLight",
         "",
         "getIntercomColors-nl4AeYM",
         "(JJJJJJZ)Lio/intercom/android/sdk/ui/theme/IntercomColors;",
         "intercomLightColors",
         "intercomDarkColors",
         "toMaterialColors",
         "Landroidx/compose/material3/ColorScheme;",
         "LocalIntercomColors",
         "Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "getLocalIntercomColors",
         "()Landroidx/compose/runtime/ProvidableCompositionLocal;",
         "currentThemeMode",
         "Landroidx/compose/runtime/MutableState;",
         "Lio/intercom/android/sdk/ui/theme/ThemeMode;",
         "getCurrentThemeMode",
         "()Landroidx/compose/runtime/MutableState;",
         "setCurrentThemeMode",
         "(Landroidx/compose/runtime/MutableState;)V",
         "intercom-sdk-ui_release"
   }
)
public final class IntercomColorsKt {
   @NotNull
   private static final ProvidableCompositionLocal LocalIntercomColors = CompositionLocalKt.staticCompositionLocalOf(
      ((kotlin.jvm.functions.Function0) () -> IntercomColorsKt.LocalIntercomColors$lambda$0())
   );
   @NotNull
   private static MutableState currentThemeMode = SnapshotStateKt.mutableStateOf$default(ThemeMode.LIGHT, null, 2, null);

   @NotNull
   public static final IntercomColors recovered$cb0ce17da3a10a76(long var0, long var2, long var4, long var6, long var8, long var10, boolean var12) {
      return var12
         ? IntercomColors.recovered$2c5c5937d8c458df(
            intercomLightColors(),
            var0,
            var2,
            var4,
            var6,
            var8,
            var10,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            false,
            -64,
            15,
            null
         )
         : IntercomColors.recovered$2c5c5937d8c458df(
            intercomDarkColors(),
            var0,
            var2,
            var4,
            var6,
            var8,
            var10,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            false,
            -64,
            15,
            null
         );
   }

   @NotNull
   public static final IntercomColors intercomLightColors() {
      return new IntercomColors(
         BaseColors.INSTANCE.recovered$981014ce154a4183(),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         BaseColors.INSTANCE.recovered$235b30c090a399c5(),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         BaseColors.INSTANCE.recovered$235b30c090a399c5(),
         new IntercomBaseColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d954bcd5d8909f9c(),
            BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
            null
         ),
         new IntercomTextColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
            BaseColors$NewColorScheme.INSTANCE.recovered$06e29a58f12321a6(),
            BaseColors$NewColorScheme.INSTANCE.recovered$a2be0cb3db137503(),
            BaseColors$NewColorScheme.INSTANCE.recovered$15696b5161a8104b(),
            null
         ),
         new IntercomIconColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
            BaseColors$NewColorScheme.INSTANCE.recovered$06e29a58f12321a6(),
            BaseColors$NewColorScheme.INSTANCE.recovered$96ce41797c07eb51(),
            BaseColors$NewColorScheme.INSTANCE.recovered$15696b5161a8104b(),
            null
         ),
         new IntercomContainerColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$d954bcd5d8909f9c(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b9fb366ba9f93319(),
            BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b6abe7bc2f1ccc46(),
            BaseColors$NewColorScheme.INSTANCE.recovered$3d5b43a2b99829d2(),
            BaseColors$NewColorScheme.INSTANCE.recovered$676f06067eb86246(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
            null
         ),
         new IntercomBorderColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$d954bcd5d8909f9c(), BaseColors$NewColorScheme.INSTANCE.recovered$b9fb366ba9f93319(), null
         ),
         new IntercomSyntaxColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$3f3cf5148d6e8a33(),
            BaseColors$NewColorScheme.INSTANCE.recovered$bdb3d82e7c42e035(),
            BaseColors$NewColorScheme.INSTANCE.recovered$3d5b43a2b99829d2(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$370bdeb409677862(),
            BaseColors$NewColorScheme.INSTANCE.recovered$6624d1014db3e185(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            null
         ),
         new IntercomAlphaColors(
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.3F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.4F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.7F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.8F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 1.0F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.3F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.4F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.7F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.8F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 1.0F, 0.0F, 0.0F, 0.0F, 14, null),
            null
         ),
         BaseColors.INSTANCE.recovered$55d22732f9c6a8d6(),
         Color.recovered$41aa0ee03c2282fb(BaseColors.INSTANCE.recovered$235b30c090a399c5(), 0.04F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors.INSTANCE.recovered$55d22732f9c6a8d6(),
         ColorKt.Color(4293256677L),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         ColorKt.Color(4294440951L),
         BaseColors.INSTANCE.recovered$ee70f87a298e6e47(),
         BaseColors$NewColorScheme.INSTANCE.recovered$d954bcd5d8909f9c(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors.INSTANCE.recovered$bd9fa0c98d0725c7(),
         Color.recovered$41aa0ee03c2282fb(BaseColors.INSTANCE.recovered$235b30c090a399c5(), 0.02F, 0.0F, 0.0F, 0.0F, 14, null),
         ColorKt.Color(4292993505L),
         BaseColors.INSTANCE.recovered$55d22732f9c6a8d6(),
         Color.recovered$41aa0ee03c2282fb(BaseColors.INSTANCE.recovered$235b30c090a399c5(), 0.05F, 0.0F, 0.0F, 0.0F, 14, null),
         ColorKt.Color(4294375158L),
         BaseColors.INSTANCE.recovered$176187de5f63aca7(),
         BaseColors.INSTANCE.recovered$ff90a8751967f2f7(),
         BaseColors.INSTANCE.recovered$1f9589635d4ef302(),
         BaseColors.INSTANCE.recovered$176187de5f63aca7(),
         ColorKt.Color(4279176975L),
         true,
         null
      );
   }

   @NotNull
   public static final IntercomColors intercomDarkColors() {
      return new IntercomColors(
         BaseColors.INSTANCE.recovered$981014ce154a4183(),
         BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
         BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
         BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
         BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
         BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
         new IntercomBaseColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            null
         ),
         new IntercomTextColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$e578ff31bb4ec2ef(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$bf8a61ea9fe0101f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$3e21cd8e8d7e2aff(),
            BaseColors$NewColorScheme.INSTANCE.recovered$2410732bc56df824(),
            null
         ),
         new IntercomIconColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b1f7c2f84a449a7a(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            BaseColors$NewColorScheme.INSTANCE.recovered$bf8a61ea9fe0101f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$5bfc1eb9aaa2a9ed(),
            BaseColors$NewColorScheme.INSTANCE.recovered$2410732bc56df824(),
            null
         ),
         new IntercomContainerColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$ed21b06ea5aa0ed8(),
            BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$bf8a61ea9fe0101f(),
            BaseColors$NewColorScheme.INSTANCE.recovered$53fd3b7932070421(),
            BaseColors$NewColorScheme.INSTANCE.recovered$5bfc1eb9aaa2a9ed(),
            BaseColors$NewColorScheme.INSTANCE.recovered$2c1409cff2896180(),
            BaseColors$NewColorScheme.INSTANCE.recovered$d01d53a4bbdb5a28(),
            null
         ),
         new IntercomBorderColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(), BaseColors$NewColorScheme.INSTANCE.recovered$ed21b06ea5aa0ed8(), null
         ),
         new IntercomSyntaxColors(
            BaseColors$NewColorScheme.INSTANCE.recovered$899d114f6c493bba(),
            BaseColors$NewColorScheme.INSTANCE.recovered$53fd3b7932070421(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b267ae861396f725(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            BaseColors$NewColorScheme.INSTANCE.recovered$dcda1a71b0d5abdb(),
            BaseColors$NewColorScheme.INSTANCE.recovered$b131e18d88a59111(),
            BaseColors$NewColorScheme.INSTANCE.recovered$57b782d856857d08(),
            null
         ),
         new IntercomAlphaColors(
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.3F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.4F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.7F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.8F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(), 1.0F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.2F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.3F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.4F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.7F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.8F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null),
            Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 1.0F, 0.0F, 0.0F, 0.0F, 14, null),
            null
         ),
         BaseColors.INSTANCE.recovered$146d1d8b5c75e8db(),
         BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(),
         BaseColors.INSTANCE.recovered$146d1d8b5c75e8db(),
         ColorKt.Color(4293256677L),
         ColorKt.Color(4281216558L),
         BaseColors.INSTANCE.recovered$146d1d8b5c75e8db(),
         BaseColors.INSTANCE.recovered$ee70f87a298e6e47(),
         BaseColors$NewColorScheme.INSTANCE.recovered$d954bcd5d8909f9c(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors$NewColorScheme.INSTANCE.recovered$95989221dad41c4d(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$6b1eb42900f329b6(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors.INSTANCE.recovered$bd9fa0c98d0725c7(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(), 0.02F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(),
         BaseColors.INSTANCE.recovered$146d1d8b5c75e8db(),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(), 0.9F, 0.0F, 0.0F, 0.0F, 14, null),
         Color.recovered$41aa0ee03c2282fb(BaseColors$NewColorScheme.INSTANCE.recovered$e8e9f050dff0009f(), 0.7F, 0.0F, 0.0F, 0.0F, 14, null),
         BaseColors.INSTANCE.recovered$176187de5f63aca7(),
         BaseColors.INSTANCE.recovered$ff90a8751967f2f7(),
         BaseColors.INSTANCE.recovered$1f9589635d4ef302(),
         BaseColors.INSTANCE.recovered$176187de5f63aca7(),
         ColorKt.Color(4279176975L),
         false,
         null
      );
   }

   @NotNull
   public static final ColorScheme toMaterialColors(@NotNull IntercomColors var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      ColorScheme var10000;
      if (var0.isLight()) {
         long var1 = var0.recovered$202acdf3dbb5c020();
         long var3 = var0.recovered$8a5d9ad2993bccce();
         long var5 = var0.getBase().recovered$00296b21a1edce05();
         long var7 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var9 = var0.getBase().recovered$00296b21a1edce05();
         long var11 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var13 = var0.getBase().recovered$00296b21a1edce05();
         long var15 = var0.getBase().recovered$00296b21a1edce05();
         long var17 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var19 = var0.recovered$2bcfe565407cdfb8();
         var10000 = ColorSchemeKt.recovered$49393497c2d36693(
            var1,
            var3,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            var15,
            var17,
            var5,
            var7,
            var9,
            var11,
            var13,
            0L,
            0L,
            var19,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            -5234692,
            65535,
            null
         );
      } else {
         long var21 = var0.recovered$202acdf3dbb5c020();
         long var22 = var0.recovered$8a5d9ad2993bccce();
         long var23 = var0.getBase().recovered$00296b21a1edce05();
         long var24 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var25 = var0.getBase().recovered$00296b21a1edce05();
         long var26 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var27 = var0.getBase().recovered$00296b21a1edce05();
         long var28 = var0.getBase().recovered$00296b21a1edce05();
         long var29 = var0.getText().recovered$e01a1d3b7b68e21a();
         long var30 = var0.recovered$2bcfe565407cdfb8();
         var10000 = ColorSchemeKt.recovered$8118e0f4fa70e463(
            var21,
            var22,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            var28,
            var29,
            var23,
            var24,
            var25,
            var26,
            var27,
            0L,
            0L,
            var30,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            -5234692,
            65535,
            null
         );
      }

      return var10000;
   }

   @NotNull
   public static final ProvidableCompositionLocal getLocalIntercomColors() {
      return LocalIntercomColors;
   }

   @NotNull
   public static final MutableState getCurrentThemeMode() {
      return currentThemeMode;
   }

   public static final void setCurrentThemeMode(@NotNull MutableState var0) {
      Intrinsics.checkNotNullParameter(var0, "<set-?>");
      currentThemeMode = var0;
   }

   private static final IntercomColors LocalIntercomColors$lambda$0() {
      return intercomLightColors();
   }
}
