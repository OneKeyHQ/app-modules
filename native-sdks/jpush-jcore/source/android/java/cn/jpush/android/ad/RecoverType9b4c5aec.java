package cn.jpush.android.ad;

import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecoverType9b4c5aec {
   public static final Pattern recoverField_a = Pattern.compile(
      "((aero|arpa|asia|a[cdefgilmnoqrstuwxz])|(biz|b[abdefghijmnorstvwyz])|(cat|com|coop|c[acdfghiklmnoruvxyz])|d[ejkmoz]|(edu|e[cegrstu])|f[ijkmor]|(gov|g[abdefghilmnpqrstuwy])|h[kmnrtu]|(info|int|i[delmnoqrst])|(jobs|j[emop])|k[eghimnprwyz]|l[abcikrstuvy]|(mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])|(name|net|n[acefgilopruz])|(org|om)|(pro|p[aefghklmnrstwy])|qa|r[eosuw]|s[abcdeghijklmnortuvyz]|(tel|travel|t[cdfghjklmnoprtvwz])|u[agksyz]|v[aceginu]|w[fs]|(xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-80akhbyknj4f|xn\\-\\-9t4b11yi5a|xn\\-\\-deba0ad|xn\\-\\-g6w251d|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-zckzah)|y[etu]|z[amw])"
   );
   public static final Pattern recoverField_b = Pattern.compile(
      "((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?((?:(?:[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef][a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\-]{0,64}\\.)+(?:(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])|(?:biz|b[abdefghijmnorstvwyz])|(?:cat|com|coop|c[acdfghiklmnoruvxyz])|d[ejkmoz]|(?:edu|e[cegrstu])|f[ijkmor]|(?:gov|g[abdefghilmnpqrstuwy])|h[kmnrtu]|(?:info|int|i[delmnoqrst])|(?:jobs|j[emop])|k[eghimnprwyz]|l[abcikrstuvy]|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])|(?:name|net|n[acefgilopruz])|(?:org|om)|(?:pro|p[aefghklmnrstwy])|qa|r[eosuw]|s[abcdeghijklmnortuvyz]|(?:tel|travel|t[cdfghjklmnoprtvwz])|u[agksyz]|v[aceginu]|w[fs]|(?:xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-80akhbyknj4f|xn\\-\\-9t4b11yi5a|xn\\-\\-deba0ad|xn\\-\\-g6w251d|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-zckzah)|y[etu]|z[amw]))|(?:(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])))(?:\\:\\d{1,5})?)(\\/(?:(?:[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)"
   );
   public static final Pattern recoverField_c = Pattern.compile(
      "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))"
   );
   public static final Pattern recoverField_d = Pattern.compile(
      "(((([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef][a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\-]*)*[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef]\\.)+"
         + recoverField_a
         + ")|"
         + recoverField_c
         + ")"
   );
   public static final Pattern recoverField_e = Pattern.compile(
      "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
   );
   public static final Pattern recoverField_f = Pattern.compile("(\\+[0-9]+[\\- \\.]*)?(\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");

   public static boolean recoverMethod_a_aad3b203(String var0) {
      if (null == var0) {
         return false;
      }

      Pattern var1 = Pattern.compile("^[一-龥0-9a-zA-Z_!@#$&*+=.|]{0,40}$");
      Matcher var2 = var1.matcher(var0);
      return var2.matches();
   }

   public static int recoverMethod_b_aad3b1f2(String var0) {
      if (null != var0 && !TextUtils.isEmpty(var0)) {
         if (var0.getBytes().length > 40) {
            return JPushInterface.Member6357d7ea.TOO_LONG_ALIAS;
         }

         Pattern var1 = Pattern.compile("^[一-龥0-9a-zA-Z_!@#$&*+=.|]+$");
         Matcher var2 = var1.matcher(var0);
         return !var2.matches() ? JPushInterface.Member6357d7ea.INVALID_ALIAS : 0;
      } else {
         return 0;
      }
   }

   public static int recoverMethod_c_aad3b1f2(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return 0;
      }

      Pattern var1 = Pattern.compile("^[+0-9][-0-9]{1,}$");
      Matcher var2 = var1.matcher(var0);
      return !var2.matches() ? JPushInterface.Member6357d7ea.ERROR_CODE_INVALID_MOBILENUMBER : 0;
   }

   public static int recoverMethod_a_abc98a9d(Set var0) {
      if (null != var0 && !var0.isEmpty()) {
         if (var0.size() > 1000) {
            return JPushInterface.Member6357d7ea.TOO_MANY_TAGS;
         }

         for (Object recoveryElement_71 : var0) {
            String var2 = (String)recoveryElement_71;
            if (null == var2) {
               return JPushInterface.Member6357d7ea.INVALID_TAGS;
            }

            if (var2.getBytes().length > 40) {
               return JPushInterface.Member6357d7ea.TOO_LONG_TAGS;
            }

            Pattern var3 = Pattern.compile("^[一-龥0-9a-zA-Z_!@#$&*+=.|]+$");
            Matcher var4 = var3.matcher(var2);
            if (!var4.matches()) {
               return JPushInterface.Member6357d7ea.INVALID_TAGS;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }
}
