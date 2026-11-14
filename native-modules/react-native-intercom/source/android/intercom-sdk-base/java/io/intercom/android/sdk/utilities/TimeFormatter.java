package io.intercom.android.sdk.utilities;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.LastParticipatingAdmin;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
   private final Context context;
   private final TimeProvider timeProvider;
   private SimpleDateFormat absoluteDateFormatter;
   private SimpleDateFormat absoluteTimeFormatter;

   public TimeFormatter(Context var1, TimeProvider var2) {
      this.context = var1;
      this.timeProvider = var2;
   }

   public CharSequence getAdminActiveStatus(LastParticipatingAdmin var1, Provider var2) {
      if (this.shouldShowActiveOrAwayState(((AppConfig)var2.get()).getLocale())) {
         return var1.isActive() ? this.context.getString(string.intercom_active_state) : this.context.getString(string.intercom_away_state);
      } else {
         return var1.getLastActiveAt() <= 0L ? "" : this.getAdminActiveStatus(this.getDateFromTimeStamp(var1.getLastActiveAt()));
      }
   }

   private CharSequence getAdminActiveStatus(Date var1) {
      long var2 = this.getDifferenceInMinutes(var1);
      if (var2 > TimeUnit.DAYS.toMinutes(6L)) {
         return this.context.getText(string.intercom_active_week_ago);
      }

      Phrase var4;
      if (var2 >= TimeUnit.HOURS.toMinutes(23L) + 31L) {
         var4 = Phrase.from(this.context, string.intercom_active_day_ago).put("days", Long.toString(1L + (var2 / 60L - 13L) / 24L));
      } else if (var2 >= 53L) {
         var4 = Phrase.from(this.context, string.intercom_active_hour_ago).put("hours", Long.toString(1L + (var2 - 31L) / 60L));
      } else if (var2 >= 38L) {
         var4 = Phrase.from(this.context, string.intercom_active_minute_ago).put("minutes", Long.toString(45L));
      } else if (var2 >= 16L) {
         var4 = Phrase.from(this.context, string.intercom_active_minute_ago).put("minutes", Long.toString(30L));
      } else {
         var4 = Phrase.from(this.context, string.intercom_active_15m_ago).put("minutes", Long.toString(15L));
      }

      return var4.format();
   }

   @VisibleForTesting
   boolean shouldShowActiveOrAwayState(String var1) {
      return var1.equals("en");
   }

   private long getDifferenceInMinutes(Date var1) {
      return TimeUnit.MILLISECONDS.toMinutes(this.timeProvider.currentTimeMillis() - var1.getTime());
   }

   private Date getDateFromTimeStamp(long var1) {
      return new Date(var1 * 1000L);
   }

   public static String formatTimeInMillisAsDate(long var0, String var2) {
      Date var3 = new Date(var0);
      return new SimpleDateFormat(var2, Locale.getDefault()).format(var3);
   }

   public static String formatTimeForTickets(long var0, Context var2) {
      Date var3 = new Date(var0 * 1000L);
      long var4 = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - var3.getTime());
      long var6 = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - var3.getTime());
      long var8 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - var3.getTime());
      long var10 = var8 / 7L;
      Phrase var12;
      if (var10 > 0L) {
         var12 = Phrase.from(var2, string.intercom_time_week_ago).put("delta", Long.toString(var10));
      } else if (var8 > 0L) {
         var12 = Phrase.from(var2, string.intercom_time_day_ago).put("delta", Long.toString(var8));
      } else if (var6 > 0L) {
         var12 = Phrase.from(var2, string.intercom_time_hour_ago).put("delta", Long.toString(var6));
      } else {
         if (var4 < 1L) {
            return var2.getString(string.intercom_time_just_now);
         }

         var12 = Phrase.from(var2, string.intercom_time_minute_ago).put("delta", Long.toString(var4));
      }

      return var12.format().toString();
   }

   public static String formatToUtcTime(int var0, int var1) {
      Calendar var2 = Calendar.getInstance();
      var2.set(11, var0);
      var2.set(12, var1);
      Date var3 = var2.getTime();
      SimpleDateFormat var4 = new SimpleDateFormat("HH:mm", Locale.getDefault());
      var4.setTimeZone(TimeZone.getTimeZone("UTC"));
      return var4.format(var3);
   }

   public static String formatFromUtcTime(int var0, int var1) {
      Calendar var2 = Calendar.getInstance();
      var2.setTimeZone(TimeZone.getTimeZone("UTC"));
      var2.set(11, var0);
      var2.set(12, var1);
      Date var3 = var2.getTime();
      SimpleDateFormat var4 = new SimpleDateFormat("HH:mm", Locale.getDefault());
      var4.setTimeZone(TimeZone.getDefault());
      return var4.format(var3);
   }

   public static int getHour(long var0) {
      Date var2 = new Date(var0);
      return var2.getHours();
   }

   public static int getMinute(long var0) {
      Date var2 = new Date(var0);
      return var2.getMinutes();
   }
}
