package cn.jpush.android.webview.bridge;

import android.text.TextUtils;
import android.webkit.WebView;
import cn.jpush.android.helper.Logger;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType8afd8fa2 {
   private HashMap recoverField_a;
   private String recoverField_b;
   private String recoverField_c;

   public RecoverType8afd8fa2(String var1, Class var2) {
      try {
         if (TextUtils.isEmpty(var1)) {
            throw new Exception("injected name can not be null");
         }

         this.recoverField_b = var1;
         this.recoverField_a = new HashMap();
         Method[] var3 = var2.getDeclaredMethods();
         StringBuilder var4 = new StringBuilder("javascript:(function(b){console.log(\"");
         var4.append(this.recoverField_b);
         var4.append(
            " initialization begin\");var a={queue:[],callback:function(){var d=Array.prototype.slice.call(arguments,0);var c=d.shift();var e=d.shift();this.queue[c].apply(this,d);if(!e){delete this.queue[c]}}};"
         );

         for (Method var8 : var3) {
            String var9;
            if (var8.getModifiers() == 9 && (var9 = this.recoverMethod_a_b11dffe3(var8)) != null) {
               this.recoverField_a.put(var9, var8);
               var4.append(String.format(Locale.ENGLISH, "a.%s=", var8.getName()));
            }
         }

         var4.append("function(){var f=Array.prototype.slice.call(arguments,0);if(f.length<1){throw\"");
         var4.append(this.recoverField_b);
         var4.append(
            " call error, message:miss method name\"}var e=[];for(var h=1;h<f.length;h++){var c=f[h];var j=typeof c;e[e.length]=j;if(j==\"function\"){var d=a.queue.length;a.queue[d]=c;f[h]=d}}var g=JSON.parse(prompt(JSON.stringify({method:f.shift(),types:e,args:f})));if(g.code!=200){throw\""
         );
         var4.append(this.recoverField_b);
         var4.append(
            " call error, code:\"+g.code+\", message:\"+g.result}return g.result};Object.getOwnPropertyNames(a).forEach(function(d){var c=a[d];if(typeof c===\"function\"&&d!==\"callback\"){a[d]=function(){return c.apply(a,[d].concat(Array.prototype.slice.call(arguments,0)))}}});b."
         );
         var4.append(this.recoverField_b);
         var4.append("=a;console.log(\"");
         var4.append(this.recoverField_b);
         var4.append(" initialization end\")})(window);");
         this.recoverField_c = var4.toString();
         Logger.recoverMethod_d_e925fa09("JsCallJava", "----------" + var4.toString());
      } catch (Exception var10) {
         Logger.recoverMethod_e_e925fa09("JsCallJava", "init js error:" + var10.getMessage());
      }
   }

   private String recoverMethod_a_b11dffe3(Method var1) {
      String var2 = var1.getName();
      Class[] var3 = var1.getParameterTypes();
      int var4 = var3.length;
      if (var4 >= 1 && var3[0] == WebView.class) {
         for (int var5 = 1; var5 < var4; var5++) {
            Class var6 = var3[var5];
            if (var6 == String.class) {
               var2 = var2 + "_S";
            } else if (var6 == int.class || var6 == long.class || var6 == float.class || var6 == double.class) {
               var2 = var2 + "_N";
            } else if (var6 == boolean.class) {
               var2 = var2 + "_B";
            } else if (var6 == JSONObject.class) {
               var2 = var2 + "_O";
            } else {
               var2 = var2 + "_P";
            }
         }

         return var2;
      } else {
         Logger.recoverMethod_w_e925fa09("JsCallJava", "method(" + var2 + ") must use webview to be first parameter, will be pass");
         return null;
      }
   }

   public String recoverMethod_a_73cf11cb() {
      return this.recoverField_c;
   }

   public String recoverMethod_a_f25613fe(WebView var1, String var2) {
      if (!TextUtils.isEmpty(var2)) {
         try {
            JSONObject var3 = new JSONObject(var2);
            String var4 = var3.getString("method");
            JSONArray var5 = var3.getJSONArray("types");
            JSONArray var6 = var3.getJSONArray("args");
            String var7 = var4;
            int var8 = var5.length();
            Object[] var9 = new Object[var8 + 1];
            int var10 = 0;
            var9[0] = var1;

            for (int var12 = 0; var12 < var8; var12++) {
               String var11 = var5.optString(var12);
               if ("string".equals(var11)) {
                  var7 = var7 + "_S";
                  var9[var12 + 1] = var6.isNull(var12) ? null : var6.getString(var12);
               } else if ("number".equals(var11)) {
                  var7 = var7 + "_N";
                  var10 = var10 * 10 + var12 + 1;
               } else if ("boolean".equals(var11)) {
                  var7 = var7 + "_B";
                  var9[var12 + 1] = var6.getBoolean(var12);
               } else if ("object".equals(var11)) {
                  var7 = var7 + "_O";
                  var9[var12 + 1] = var6.isNull(var12) ? null : var6.getJSONObject(var12);
               } else {
                  var7 = var7 + "_P";
               }
            }

            Method var17 = (Method)this.recoverField_a.get(var7);
            if (var17 == null) {
               return this.recoverMethod_a_5f9b6ce0(var2, 500, "not found method(" + var7 + ") with valid parameters");
            }

            if (var10 > 0) {
               Class[] var13 = var17.getParameterTypes();

               while (var10 > 0) {
                  int var14 = var10 - var10 / 10 * 10;
                  Class var15 = var13[var14];
                  if (var15 == int.class) {
                     var9[var14] = var6.getInt(var14 - 1);
                  } else if (var15 == long.class) {
                     var9[var14] = Long.parseLong(var6.getString(var14 - 1));
                  } else {
                     var9[var14] = var6.getDouble(var14 - 1);
                  }

                  var10 /= 10;
               }
            }

            return this.recoverMethod_a_5f9b6ce0(var2, 200, var17.invoke(null, var9));
         } catch (Exception var16) {
            return var16.getCause() != null
               ? this.recoverMethod_a_5f9b6ce0(var2, 500, "method execute error:" + var16.getCause().getMessage())
               : this.recoverMethod_a_5f9b6ce0(var2, 500, "method execute error:" + var16.getMessage());
         }
      } else {
         return this.recoverMethod_a_5f9b6ce0(var2, 500, "call data empty");
      }
   }

   private String recoverMethod_a_5f9b6ce0(String var1, int var2, Object var3) {
      String var4 = "";
      if (var3 == null) {
         var4 = "null";
      } else if (var3 instanceof String) {
         var3 = ((String)var3).replace("\"", "\\\"");
         var4 = "\"" + var3 + "\"";
      } else if (var3 instanceof Integer
         || var3 instanceof Long
         || var3 instanceof Boolean
         || var3 instanceof Float
         || var3 instanceof Double
         || var3 instanceof JSONObject) {
         var4 = String.valueOf(var3);
      }

      String var5 = String.format(Locale.ENGLISH, "{\"code\": %d, \"result\": %s}", var2, var4);
      Logger.recoverMethod_d_e925fa09("JsCallJava", this.recoverField_b + " call json: " + var1 + " result:" + var5);
      return var5;
   }
}
