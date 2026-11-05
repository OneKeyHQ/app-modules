package io.intercom.android.sdk.m5.home.reducers;

import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.HeaderBackdropType;
import io.intercom.android.sdk.models.HeaderBackgroundModel;
import io.intercom.android.sdk.models.HeaderConfig;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.ColorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00004\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0000\u001a\"\u0010\u0004\u001a\u00020\u00052\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000\u001a'\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\tH\u0000¢\u0006\u0004\b\u0011\u0010\u0012¨\u0006\u0013",
   d2 = {
         "computeErrorHeader",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;",
         "configModules",
         "Lio/intercom/android/sdk/models/ConfigModules;",
         "computeContentHeader",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;",
         "teamPresence",
         "Lio/intercom/android/sdk/models/TeamPresence;",
         "isHeaderImageLoaded",
         "",
         "getHeaderBackdropStyle",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "headerBackgroundModel",
         "Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "fallbackBackgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "isDark",
         "getHeaderBackdropStyle-bw27NRU",
         "(Lio/intercom/android/sdk/models/HeaderBackgroundModel;JZ)Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeHeaderStateReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderStateReducer.kt\nio/intercom/android/sdk/m5/home/reducers/HomeHeaderStateReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,247:1\n1563#2:248\n1634#2,3:249\n1563#2:253\n1634#2,3:254\n1563#2:257\n1634#2,3:258\n1563#2:261\n1634#2,3:262\n1#3:252\n*S KotlinDebug\n*F\n+ 1 HomeHeaderStateReducer.kt\nio/intercom/android/sdk/m5/home/reducers/HomeHeaderStateReducerKt\n*L\n143#1:248\n143#1:249,3\n220#1:253\n220#1:254,3\n221#1:257\n221#1:258,3\n224#1:261\n224#1:262,3\n*E\n"
)
public final class HomeHeaderStateReducerKt {
   @NotNull
   public static final io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader computeErrorHeader(@Nullable ConfigModules var0) {
      io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader var10000;
      if (var0 != null) {
         int var2 = 0;
         String var3 = var0.getCustomization().getHeader().getForegroundColor();
         String var4 = var0.getCustomization().getHeader().getBackgroundColor();
         var10000 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader(var4, var3);
      } else {
         var10000 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader("#000000", "#FFFFFF");
      }

      return var10000;
   }

   @NotNull
   public static final io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader computeContentHeader(@Nullable ConfigModules var0, @NotNull TeamPresence var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "teamPresence");
      io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader var35;
      if (var0 != null) {
         int var5 = 0;
         Pair var10000;
         switch (var0.getHome().getHeader().getBackground().getType()) {
            case SOLID:
            case COLORS:
               var10000 = new Pair(
                  new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                     var0.getHome().getHeader().getContent().getIntro().getText(),
                     var0.getHome().getHeader().getContent().getIntro().getOpacity(),
                     var0.getHome().getHeader().getContent().getTextColorType(),
                     var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                     var0.getHome().getHeader().getContent().getIntro().getColorDark(),
                     var0.getHome().getHeader().getContent().getIntro().getColor()
                  ),
                  new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                     var0.getHome().getHeader().getContent().getGreeting().getText(),
                     var0.getHome().getHeader().getContent().getGreeting().getOpacity(),
                     var0.getHome().getHeader().getContent().getTextColorType(),
                     var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                     var0.getHome().getHeader().getContent().getGreeting().getColorDark(),
                     var0.getHome().getHeader().getContent().getGreeting().getColor()
                  )
               );
               break;
            case IMAGE:
               var10000 = var2
                  ? new Pair(
                     new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                        var0.getHome().getHeader().getContent().getIntro().getText(),
                        var0.getHome().getHeader().getContent().getIntro().getOpacity(),
                        var0.getHome().getHeader().getContent().getTextColorType(),
                        var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                        var0.getHome().getHeader().getContent().getIntro().getColorDark(),
                        var0.getHome().getHeader().getContent().getIntro().getColor()
                     ),
                     new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                        var0.getHome().getHeader().getContent().getGreeting().getText(),
                        var0.getHome().getHeader().getContent().getGreeting().getOpacity(),
                        var0.getHome().getHeader().getContent().getTextColorType(),
                        var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                        var0.getHome().getHeader().getContent().getGreeting().getColorDark(),
                        var0.getHome().getHeader().getContent().getGreeting().getColor()
                     )
                  )
                  : new Pair(
                     new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                        var0.getHome().getHeader().getContent().getIntro().getText(),
                        1.0F,
                        var0.getHome().getHeader().getContent().getTextColorType(),
                        var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                        var0.getHome().getHeader().getContent().getIntro().getColorDark(),
                        var0.getHome().getHeader().getContent().getIntro().getColor()
                     ),
                     new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText(
                        var0.getHome().getHeader().getContent().getGreeting().getText(),
                        1.0F,
                        var0.getHome().getHeader().getContent().getTextColorType(),
                        var0.getHome().getHeader().getContent().getTextColorTypeDark(),
                        var0.getHome().getHeader().getContent().getGreeting().getColorDark(),
                        var0.getHome().getHeader().getContent().getGreeting().getColor()
                     )
                  );
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         Pair var6 = var10000;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var7 = (io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText)var6.component1();
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var8 = (io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText)var6.component2();
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor var33;
         switch (var0.getHome().getHeader().getBackground().getType()) {
            case SOLID:
            case COLORS:
               var33 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor(
                  var0.getHome().getHeader().getContent().getCloseButton().getBackgroundColor(),
                  var0.getHome().getHeader().getContent().getCloseButton().getForegroundColor(),
                  var7.getTextColorDark(),
                  var0.getHome().getHeader().getContent().getCloseButton().getBackgroundOpacity()
               );
               break;
            case IMAGE:
               var33 = var2
                  ? new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor(
                     var0.getHome().getHeader().getContent().getCloseButton().getBackgroundColor(),
                     var0.getHome().getHeader().getContent().getCloseButton().getForegroundColor(),
                     var7.getTextColorDark(),
                     var0.getHome().getHeader().getContent().getCloseButton().getBackgroundOpacity()
                  )
                  : new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor("#000000", var0.getCustomization().getHeader().getForegroundColor(), var7.getTextColorDark(), 0.5F);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor var9;
         HeaderConfig var10;
         boolean var34;
         label55: {
            var9 = var33;
            var10 = var0.getHome().getHeader();
            int var11 = 0;
            if (StringsKt.isBlank(var10.getContent().getLogoUrl())) {
               CharSequence var12 = var10.getContent().getLogoDarkUrl();
               if (var12 == null || StringsKt.isBlank(var12)) {
                  var34 = false;
                  break label55;
               }
            }

            var34 = true;
         }

         boolean var13 = var34;
         String var32 = var10.getContent().getLogoUrl();
         String var14 = var10.getContent().getLogoDarkUrl();
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var15 = recovered$e53cbbe306c5c2cc(
            var10.getBackground(), ColorExtensionsKt.toComposeColor$default(var0.getCustomization().getHeader().getBackgroundColor(), 0.0F, 1, null), false
         );
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var16 = recovered$e53cbbe306c5c2cc(
            var10.getBackground(), ColorExtensionsKt.toComposeColor$default(var0.getCustomization().getHeader().getBackgroundColor(), 0.0F, 1, null), true
         );
         boolean var17 = var10.getContent().getShowAvatars();
         Iterable var18 = CollectionsKt.take(var1.getBuiltActiveAdmins(), 3);
         int var19 = 0;
         Iterable var20 = var18;
         ArrayList var21 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var18, 10));
         int var22 = 0;

         for (Object var24 : var20) {
            Participant var25 = (Participant)var24;
            Collection var26 = var21;
            int var27 = 0;
            Avatar var10002 = var25.getAvatar();
            Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
            Boolean var10003 = var25.isBot();
            Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
            var26.add(new AvatarWrapper(var10002, var10003));
         }

         List var28 = (List)var21;
         var35 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader(var13, var32, var14, var8, var7, var15, var16, var17, var28, var9);
      } else {
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var3 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText("", 1.0F, null, null, "#FFFFFF", "#FFFFFF", 12, null);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var4 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText("", 1.0F, null, null, "#FFFFFF", "#FFFFFF", 12, null);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid var29 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(Color.Companion.recovered$235b30c090a399c5(), false, null);
         List var30 = CollectionsKt.emptyList();
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor var31 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor("#000000", "#FFFFFF", "#FFFFFF", 0.5F);
         var35 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader(false, null, null, var4, var3, var29, null, false, var30, var31, 68, null);
      }

      return var35;
   }

   @NotNull
   public static final io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle recovered$e53cbbe306c5c2cc(@NotNull HeaderBackgroundModel var0, long var1, boolean var3) {
      Intrinsics.checkNotNullParameter(var0, "headerBackgroundModel");
      HeaderBackdropType var10000;
      if (var3) {
         var10000 = var0.getTypeDark();
         if (var10000 == null) {
            var10000 = var0.getType();
         }
      } else {
         var10000 = var0.getType();
      }

      HeaderBackdropType var4 = var10000;
      boolean var45;
      if (var3) {
         Boolean var44 = var0.getFadeToDark();
         var45 = var44 != null ? var44 : var0.getFade();
      } else {
         var45 = var0.getFade();
      }

      boolean var5 = var45;
      io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var47;
      switch (var4) {
         case SOLID:
            long var53;
            if (var3) {
               String var52 = var0.getColorDark();
               if (var52 != null) {
                  String var30 = var52;
                  int var33 = 0;
                  var53 = ColorKt.Color(ColorUtils.parseColor(var30));
               } else {
                  switch (var0.getType()) {
                     case SOLID:
                     case COLORS:
                        var53 = ColorKt.Color(ColorUtils.parseColor(var0.getColor()));
                        break;
                     case IMAGE:
                        var53 = var1;
                        break;
                     default:
                        throw new NoWhenBranchMatchedException();
                  }
               }
            } else {
               var53 = ColorKt.Color(ColorUtils.parseColor(var0.getColor()));
            }

            long var20 = var53;
            var47 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(var20, var5, null);
            break;
         case COLORS:
            List var49;
            if (var3) {
               List var50 = var0.getGradientDark();
               if (var50 != null) {
                  Iterable var22 = var50;
                  int var25 = 0;
                  Iterable var27 = var22;
                  ArrayList var29 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var22, 10));
                  int var32 = 0;

                  for (Object var37 : var27) {
                     String var39 = (String)var37;
                     Collection var43 = var29;
                     int var41 = 0;
                     var43.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var39))));
                  }

                  var49 = (List)var29;
               } else {
                  List var51 = var0.getGradient();
                  if (var51 == null) {
                     Color[] var24 = new Color[]{Color.recovered$f46ef195ad4a2363(var1), Color.recovered$f46ef195ad4a2363(var1)};
                     var49 = CollectionsKt.listOf(var24);
                  } else {
                     Iterable var23 = var51;
                     int var26 = 0;
                     Iterable var28 = var23;
                     ArrayList var31 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var23, 10));
                     int var34 = 0;

                     for (Object var38 : var28) {
                        String var40 = (String)var38;
                        Collection var42 = var31;
                        int var17 = 0;
                        var42.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var40))));
                     }

                     var49 = (List)var31;
                  }
               }
            } else {
               List var48 = var0.getGradient();
               if (var48 == null) {
                  Color[] var21 = new Color[]{Color.recovered$f46ef195ad4a2363(var1), Color.recovered$f46ef195ad4a2363(var1)};
                  var49 = CollectionsKt.listOf(var21);
               } else {
                  Iterable var8 = var48;
                  int var9 = 0;
                  Iterable var10 = var8;
                  ArrayList var11 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var8, 10));
                  int var12 = 0;

                  for (Object var14 : var10) {
                     String var15 = (String)var14;
                     Collection var18 = var11;
                     int var16 = 0;
                     var18.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var15))));
                  }

                  var49 = (List)var11;
               }
            }

            List var19 = var49;
            var47 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var19, var5);
            break;
         case IMAGE:
            String var46;
            if (var3) {
               var46 = var0.getImageDarkUrl();
               if (var46 == null) {
                  var46 = var0.getImageUrl();
               }
            } else {
               var46 = var0.getImageUrl();
            }

            String var6 = var46;
            var47 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image(var1, var6, var5, null);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var47;
   }
}
