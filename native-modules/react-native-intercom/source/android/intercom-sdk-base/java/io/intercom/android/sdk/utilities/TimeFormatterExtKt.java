package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.format.DateFormat;
import io.intercom.android.sdk.R.string;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000,\n\u0000\n\u0002\u0010\u000e\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u0014\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0007\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\b\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\t\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\u0016\u0010\n\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0000\u001a\u0016\u0010\r\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0000\u001a\u0018\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a\u0010\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a\u0010\u0010\u0014\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a\u0010\u0010\u0015\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u0012H\u0002¨\u0006\u0016",
   d2 = {
         "formattedDateFromLong",
         "",
         "",
         "context",
         "Landroid/content/Context;",
         "formattedDateForDayDivider",
         "toISOFormat",
         "toISOFormatApi26",
         "toISOFormatPreApi26",
         "toHourOfDay",
         "toHourOfDayApi26",
         "zoneId",
         "Ljava/time/ZoneId;",
         "toHourOfDayPreApi26",
         "timeZone",
         "Ljava/util/TimeZone;",
         "getFormattedTime",
         "date",
         "Ljava/util/Date;",
         "getDifferenceInMinutes",
         "getDifferenceInHours",
         "getDifferenceInDays",
         "intercom-sdk-base_release"
   }
)
public final class TimeFormatterExtKt {
   @NotNull
   public static final String formattedDateFromLong(long var0, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var2, "context");
      if (var0 <= 0L) {
         return "";
      }

      Date var3 = new Date(var0 * 1000);
      return getFormattedTime(var2, var3);
   }

   @NotNull
   public static final String formattedDateForDayDivider(long var0, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var2, "context");
      if (var0 <= 0L) {
         return "";
      }

      Date var3 = new Date(var0 * 1000);
      Locale var4 = UtilsKt.getLocaleCompat(var2);
      SimpleDateFormat var5 = new SimpleDateFormat(DateFormat.getBestDateTimePattern(var4, "MMMM d"), var4);
      String var10000 = var5.format(var3);
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   @NotNull
   public static final String toISOFormat(long var0) {
      return VERSION.SDK_INT >= 26 ? toISOFormatApi26(var0) : toISOFormatPreApi26(var0);
   }

   @NotNull
   public static final String toISOFormatApi26(long var0) {
      Instant var2 = Instant.ofEpochMilli(var0 * 1000);
      String var10000 = DateTimeFormatter.ISO_INSTANT.format(var2);
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   @NotNull
   public static final String toISOFormatPreApi26(long var0) {
      SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
      var2.setTimeZone(TimeZone.getTimeZone("UTC"));
      String var10000 = var2.format(var0 * 1000);
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   @NotNull
   public static final String toHourOfDay(long var0) {
      return VERSION.SDK_INT >= 26 ? toHourOfDayApi26$default(var0, null, 1, null) : toHourOfDayPreApi26$default(var0, null, 1, null);
   }

   @NotNull
   public static final String toHourOfDayApi26(long var0, @NotNull ZoneId var2) {
      Intrinsics.checkNotNullParameter(var2, "zoneId");
      Instant var3 = Instant.ofEpochMilli(var0 * 1000);
      ZonedDateTime var4 = ZonedDateTime.ofInstant(var3, var2);
      DateTimeFormatter var5 = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
      String var6 = var5.format(var4);
      String var10000;
      if (var4.getHour() == 0) {
         Intrinsics.checkNotNull(var6);
         var10000 = StringsKt.replace$default(var6, "12:00", "00:00", false, 4, null);
      } else {
         String var7 = var6;
         Intrinsics.checkNotNull(var7);
         var10000 = var7;
      }

      return var10000;
   }

   // $VF: synthetic method
   public static String toHourOfDayApi26$default(long var0, ZoneId var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var2 = ZoneId.systemDefault();
      }

      return toHourOfDayApi26(var0, var2);
   }

   @NotNull
   public static final String toHourOfDayPreApi26(long var0, @NotNull TimeZone var2) {
      Intrinsics.checkNotNullParameter(var2, "timeZone");
      SimpleDateFormat var3 = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
      var3.setTimeZone(var2);
      String var4 = var3.format(var0 * 1000);
      Calendar var5 = Calendar.getInstance(var2);
      var5.setTimeInMillis(var0 * 1000);
      String var10000;
      if (var5.get(11) == 0) {
         Intrinsics.checkNotNull(var4);
         var10000 = StringsKt.replace$default(var4, "12:00", "00:00", false, 4, null);
      } else {
         String var6 = var4;
         Intrinsics.checkNotNull(var6);
         var10000 = var6;
      }

      return var10000;
   }

   // $VF: synthetic method
   public static String toHourOfDayPreApi26$default(long var0, TimeZone var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var2 = TimeZone.getDefault();
      }

      return toHourOfDayPreApi26(var0, var2);
   }

   private static final String getFormattedTime(Context var0, Date var1) {
      long var2 = getDifferenceInMinutes(var1);
      long var4 = getDifferenceInHours(var1);
      long var6 = getDifferenceInDays(var1);
      long var8 = var6 / 7;
      return var8 > 0L
         ? Phrase.from(var0, string.intercom_time_week_ago).put("delta", String.valueOf(var8)).format().toString()
         : (
            var6 > 0L
               ? Phrase.from(var0, string.intercom_time_day_ago).put("delta", String.valueOf(var6)).format().toString()
               : (
                  var4 > 0L
                     ? Phrase.from(var0, string.intercom_time_hour_ago).put("delta", String.valueOf(var4)).format().toString()
                     : (
                        var2 >= 1L
                           ? Phrase.from(var0, string.intercom_time_minute_ago).put("delta", String.valueOf(var2)).format().toString()
                           : var0.getText(string.intercom_time_just_now).toString()
                     )
               )
         );
   }

   private static final long getDifferenceInMinutes(Date var0) {
      return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - var0.getTime());
   }

   private static final long getDifferenceInHours(Date var0) {
      return TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - var0.getTime());
   }

   private static final long getDifferenceInDays(Date var0) {
      return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - var0.getTime());
   }
}
