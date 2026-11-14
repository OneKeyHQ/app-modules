package io.intercom.android.sdk.ui.theme;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\bW\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B§\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\u0006\u0010\u0010\u001a\u00020\u0003\u0012\u0006\u0010\u0011\u001a\u00020\u0003\u0012\u0006\u0010\u0012\u001a\u00020\u0003\u0012\u0006\u0010\u0013\u001a\u00020\u0003\u0012\u0006\u0010\u0014\u001a\u00020\u0003\u0012\u0006\u0010\u0015\u001a\u00020\u0003\u0012\u0006\u0010\u0016\u001a\u00020\u0003¢\u0006\u0004\b\u0017\u0010\u0018J\u0010\u0010/\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b0\u0010\u001aJ\u0010\u00101\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b2\u0010\u001aJ\u0010\u00103\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b4\u0010\u001aJ\u0010\u00105\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b6\u0010\u001aJ\u0010\u00107\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b8\u0010\u001aJ\u0010\u00109\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b:\u0010\u001aJ\u0010\u0010;\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b<\u0010\u001aJ\u0010\u0010=\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b>\u0010\u001aJ\u0010\u0010?\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b@\u0010\u001aJ\u0010\u0010A\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bB\u0010\u001aJ\u0010\u0010C\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bD\u0010\u001aJ\u0010\u0010E\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bF\u0010\u001aJ\u0010\u0010G\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bH\u0010\u001aJ\u0010\u0010I\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bJ\u0010\u001aJ\u0010\u0010K\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bL\u0010\u001aJ\u0010\u0010M\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bN\u0010\u001aJ\u0010\u0010O\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bP\u0010\u001aJ\u0010\u0010Q\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bR\u0010\u001aJ\u0010\u0010S\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bT\u0010\u001aJ\u0010\u0010U\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\bV\u0010\u001aJØ\u0001\u0010W\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u00032\b\b\u0002\u0010\u0013\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u0003HÇ\u0001¢\u0006\u0004\bX\u0010YJ\u0013\u0010Z\u001a\u00020[2\b\u0010\\\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010]\u001a\u00020^H×\u0001J\t\u0010_\u001a\u00020`H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u0019\u0010\u001aR\u0013\u0010\u0004\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u001c\u0010\u001aR\u0013\u0010\u0005\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u001d\u0010\u001aR\u0013\u0010\u0006\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u001e\u0010\u001aR\u0013\u0010\u0007\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u001f\u0010\u001aR\u0013\u0010\b\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b \u0010\u001aR\u0013\u0010\t\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b!\u0010\u001aR\u0013\u0010\n\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\"\u0010\u001aR\u0013\u0010\u000b\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b#\u0010\u001aR\u0013\u0010\f\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b$\u0010\u001aR\u0013\u0010\r\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b%\u0010\u001aR\u0013\u0010\u000e\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b&\u0010\u001aR\u0013\u0010\u000f\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b'\u0010\u001aR\u0013\u0010\u0010\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b(\u0010\u001aR\u0013\u0010\u0011\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b)\u0010\u001aR\u0013\u0010\u0012\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b*\u0010\u001aR\u0013\u0010\u0013\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b+\u0010\u001aR\u0013\u0010\u0014\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b,\u0010\u001aR\u0013\u0010\u0015\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b-\u0010\u001aR\u0013\u0010\u0016\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b.\u0010\u001a¨\u0006a",
   d2 = {
         "Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;",
         "",
         "black10",
         "Landroidx/compose/ui/graphics/Color;",
         "black20",
         "black30",
         "black40",
         "black50",
         "black60",
         "black70",
         "black80",
         "black90",
         "black100",
         "white10",
         "white20",
         "white30",
         "white40",
         "white50",
         "white60",
         "white70",
         "white80",
         "white90",
         "white100",
         "<init>",
         "(JJJJJJJJJJJJJJJJJJJJLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getBlack10-0d7_KjU",
         "()J",
         "J",
         "getBlack20-0d7_KjU",
         "getBlack30-0d7_KjU",
         "getBlack40-0d7_KjU",
         "getBlack50-0d7_KjU",
         "getBlack60-0d7_KjU",
         "getBlack70-0d7_KjU",
         "getBlack80-0d7_KjU",
         "getBlack90-0d7_KjU",
         "getBlack100-0d7_KjU",
         "getWhite10-0d7_KjU",
         "getWhite20-0d7_KjU",
         "getWhite30-0d7_KjU",
         "getWhite40-0d7_KjU",
         "getWhite50-0d7_KjU",
         "getWhite60-0d7_KjU",
         "getWhite70-0d7_KjU",
         "getWhite80-0d7_KjU",
         "getWhite90-0d7_KjU",
         "getWhite100-0d7_KjU",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "component3-0d7_KjU",
         "component4",
         "component4-0d7_KjU",
         "component5",
         "component5-0d7_KjU",
         "component6",
         "component6-0d7_KjU",
         "component7",
         "component7-0d7_KjU",
         "component8",
         "component8-0d7_KjU",
         "component9",
         "component9-0d7_KjU",
         "component10",
         "component10-0d7_KjU",
         "component11",
         "component11-0d7_KjU",
         "component12",
         "component12-0d7_KjU",
         "component13",
         "component13-0d7_KjU",
         "component14",
         "component14-0d7_KjU",
         "component15",
         "component15-0d7_KjU",
         "component16",
         "component16-0d7_KjU",
         "component17",
         "component17-0d7_KjU",
         "component18",
         "component18-0d7_KjU",
         "component19",
         "component19-0d7_KjU",
         "component20",
         "component20-0d7_KjU",
         "copy",
         "copy-Cmkg8xs",
         "(JJJJJJJJJJJJJJJJJJJJ)Lio/intercom/android/sdk/ui/theme/IntercomAlphaColors;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomAlphaColors {
   private final long black10;
   private final long black20;
   private final long black30;
   private final long black40;
   private final long black50;
   private final long black60;
   private final long black70;
   private final long black80;
   private final long black90;
   private final long black100;
   private final long white10;
   private final long white20;
   private final long white30;
   private final long white40;
   private final long white50;
   private final long white60;
   private final long white70;
   private final long white80;
   private final long white90;
   private final long white100;
   public static int $stable;

   static {}

   private IntercomAlphaColors(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      long var15,
      long var17,
      long var19,
      long var21,
      long var23,
      long var25,
      long var27,
      long var29,
      long var31,
      long var33,
      long var35,
      long var37,
      long var39
   ) {
      this.black10 = var1;
      this.black20 = var3;
      this.black30 = var5;
      this.black40 = var7;
      this.black50 = var9;
      this.black60 = var11;
      this.black70 = var13;
      this.black80 = var15;
      this.black90 = var17;
      this.black100 = var19;
      this.white10 = var21;
      this.white20 = var23;
      this.white30 = var25;
      this.white40 = var27;
      this.white50 = var29;
      this.white60 = var31;
      this.white70 = var33;
      this.white80 = var35;
      this.white90 = var37;
      this.white100 = var39;
   }

   public final long recovered$146d1d8b5c75e8db() {
      return this.black10;
   }

   public final long recovered$693d1052fe772bbc() {
      return this.black20;
   }

   public final long recovered$361285b32c1d8b5d() {
      return this.black30;
   }

   public final long recovered$93e80fd1c1e8080f() {
      return this.black40;
   }

   public final long recovered$fcca768c8ee6e65f() {
      return this.black50;
   }

   public final long recovered$4337bd3b060c9e6f() {
      return this.black60;
   }

   public final long recovered$bd9fa0c98d0725c7() {
      return this.black70;
   }

   public final long recovered$cf2b48289945f947() {
      return this.black80;
   }

   public final long recovered$ee70f87a298e6e47() {
      return this.black90;
   }

   public final long recovered$b71800394e05ff63() {
      return this.black100;
   }

   public final long recovered$834191bcf7769287() {
      return this.white10;
   }

   public final long recovered$78202eaf3ebf3336() {
      return this.white20;
   }

   public final long recovered$4384ff88a3ddbfeb() {
      return this.white30;
   }

   public final long recovered$bc8d3de7da5e153b() {
      return this.white40;
   }

   public final long recovered$7e17ebebe7e73b05() {
      return this.white50;
   }

   public final long recovered$9215c60d8c9ae827() {
      return this.white60;
   }

   public final long recovered$13c81ffff1207b3e() {
      return this.white70;
   }

   public final long recovered$37ce13d1e4f23d94() {
      return this.white80;
   }

   public final long recovered$e49c7c38a5e035d9() {
      return this.white90;
   }

   public final long recovered$12e27dd6c0ca521e() {
      return this.white100;
   }

   public final long recovered$2b43d556f0094889() {
      return this.black10;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.black20;
   }

   public final long recovered$46bfddf50387e286() {
      return this.black30;
   }

   public final long recovered$0e8bb1255e180941() {
      return this.black40;
   }

   public final long recovered$6420fbad4c51c3dc() {
      return this.black50;
   }

   public final long recovered$d686ca7219936040() {
      return this.black60;
   }

   public final long recovered$4ec02e8e998a8498() {
      return this.black70;
   }

   public final long recovered$8cbd43f56305574f() {
      return this.black80;
   }

   public final long recovered$0bbdf301dbe42e08() {
      return this.black90;
   }

   public final long recovered$b875af3d9c333cf7() {
      return this.black100;
   }

   public final long recovered$28255bfaadfbecfd() {
      return this.white10;
   }

   public final long recovered$2f1e4e1393ef3988() {
      return this.white20;
   }

   public final long recovered$06bb5a9c3bbed089() {
      return this.white30;
   }

   public final long recovered$52323945801cdb3c() {
      return this.white40;
   }

   public final long recovered$d2373b040a344642() {
      return this.white50;
   }

   public final long recovered$8bdcafd355d283ce() {
      return this.white60;
   }

   public final long recovered$db93f6a9656f0f1a() {
      return this.white70;
   }

   public final long recovered$53c7297a64e7619a() {
      return this.white80;
   }

   public final long recovered$f8e07295264d499c() {
      return this.white90;
   }

   public final long recovered$b37baf675859f3ad() {
      return this.white100;
   }

   @NotNull
   public final IntercomAlphaColors recovered$4975e9c16434778e(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      long var15,
      long var17,
      long var19,
      long var21,
      long var23,
      long var25,
      long var27,
      long var29,
      long var31,
      long var33,
      long var35,
      long var37,
      long var39
   ) {
      return new IntercomAlphaColors(
         var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23, var25, var27, var29, var31, var33, var35, var37, var39, null
      );
   }

   // $VF: synthetic method
   public static IntercomAlphaColors recovered$4c9c36a9c44e02c6(
      IntercomAlphaColors var0,
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      long var15,
      long var17,
      long var19,
      long var21,
      long var23,
      long var25,
      long var27,
      long var29,
      long var31,
      long var33,
      long var35,
      long var37,
      long var39,
      int var41,
      Object var42
   ) {
      if ((var41 & 1) != 0) {
         var1 = var0.black10;
      }

      if ((var41 & 2) != 0) {
         var3 = var0.black20;
      }

      if ((var41 & 4) != 0) {
         var5 = var0.black30;
      }

      if ((var41 & 8) != 0) {
         var7 = var0.black40;
      }

      if ((var41 & 16) != 0) {
         var9 = var0.black50;
      }

      if ((var41 & 32) != 0) {
         var11 = var0.black60;
      }

      if ((var41 & 64) != 0) {
         var13 = var0.black70;
      }

      if ((var41 & 128) != 0) {
         var15 = var0.black80;
      }

      if ((var41 & 256) != 0) {
         var17 = var0.black90;
      }

      if ((var41 & 512) != 0) {
         var19 = var0.black100;
      }

      if ((var41 & 1024) != 0) {
         var21 = var0.white10;
      }

      if ((var41 & 2048) != 0) {
         var23 = var0.white20;
      }

      if ((var41 & 4096) != 0) {
         var25 = var0.white30;
      }

      if ((var41 & 8192) != 0) {
         var27 = var0.white40;
      }

      if ((var41 & 16384) != 0) {
         var29 = var0.white50;
      }

      if ((var41 & 32768) != 0) {
         var31 = var0.white60;
      }

      if ((var41 & 65536) != 0) {
         var33 = var0.white70;
      }

      if ((var41 & 131072) != 0) {
         var35 = var0.white80;
      }

      if ((var41 & 262144) != 0) {
         var37 = var0.white90;
      }

      if ((var41 & 524288) != 0) {
         var39 = var0.white100;
      }

      return var0.recovered$4975e9c16434778e(
         var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23, var25, var27, var29, var31, var33, var35, var37, var39
      );
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("IntercomAlphaColors(black10=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black10))
         .append(", black20=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black20))
         .append(", black30=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black30))
         .append(", black40=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black40))
         .append(", black50=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black50))
         .append(", black60=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black60))
         .append(", black70=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black70))
         .append(", black80=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black80))
         .append(", black90=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black90))
         .append(", black100=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.black100))
         .append(", white10=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white10))
         .append(", white20=");
      var1.append((Object)Color.recovered$cfef63bb1d111f9f(this.white20))
         .append(", white30=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white30))
         .append(", white40=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white40))
         .append(", white50=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white50))
         .append(", white60=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white60))
         .append(", white70=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white70))
         .append(", white80=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white80))
         .append(", white90=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white90))
         .append(", white100=")
         .append((Object)Color.recovered$cfef63bb1d111f9f(this.white100))
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.black10);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black20);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black30);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black40);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black50);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black60);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black70);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black80);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black90);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.black100);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white10);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white20);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white30);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white40);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white50);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white60);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white70);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white80);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.white90);
      return var1 * 31 + Color.recovered$87e060624084b26b(this.white100);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomAlphaColors)) {
         return false;
      } else {
         IntercomAlphaColors var2 = (IntercomAlphaColors)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.black10, var2.black10)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black20, var2.black20)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black30, var2.black30)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black40, var2.black40)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black50, var2.black50)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black60, var2.black60)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black70, var2.black70)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black80, var2.black80)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black90, var2.black90)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.black100, var2.black100)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white10, var2.white10)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white20, var2.white20)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white30, var2.white30)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white40, var2.white40)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white50, var2.white50)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white60, var2.white60)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white70, var2.white70)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.white80, var2.white80)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.white90, var2.white90) ? false : Color.recovered$9eab66ea8c46e11c(this.white100, var2.white100);
         }
      }
   }

   // $VF: synthetic method
   public IntercomAlphaColors(
      long var1,
      long var3,
      long var5,
      long var7,
      long var9,
      long var11,
      long var13,
      long var15,
      long var17,
      long var19,
      long var21,
      long var23,
      long var25,
      long var27,
      long var29,
      long var31,
      long var33,
      long var35,
      long var37,
      long var39,
      DefaultConstructorMarker var41
   ) {
      this(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23, var25, var27, var29, var31, var33, var35, var37, var39);
   }
}
