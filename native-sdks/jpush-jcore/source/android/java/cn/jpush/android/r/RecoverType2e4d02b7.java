package cn.jpush.android.r;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5ae9;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.p.RecoverType2e4cfb34;
import cn.jpush.android.t.RecoverType2e4d0a38;
import cn.jpush.android.w.RecoverType2e4d157b;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import org.json.JSONObject;

public class RecoverType2e4d02b7 {
   private static ArrayList recoverMethod_a_212b8e01(ArrayList var0, Context var1, String var2, String var3) {
      return recoverMethod_a_ec1e7f67(var0, var1, var2, var3, true);
   }

   private static ArrayList recoverMethod_a_ec1e7f67(ArrayList var0, Context var1, String var2, String var3, boolean var4) {
      boolean var5 = true;
      ArrayList var6 = new ArrayList();
      Logger.recoverMethod_v_e925fa09("InAppTplHelper", "action:loadH5TemplateImageResources - urlPrefix:" + var2);

      try {
         if (RecoverType9b4c5ae5.recoverMethod_c_aad3b203(var2) && var1 != null && var0.size() > 0 && !TextUtils.isEmpty(var3)) {
            for (Object recoveryElement_39 : var0) {
               String var8 = (String)recoveryElement_39;
               String var9 = var8;
               if (!TextUtils.isEmpty(var9) && !var9.startsWith("http://") && !var9.startsWith("https://")) {
                  var9 = var2 + var8;
               }

               if (RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var9, false)) {
                  byte[] var10 = RecoverType2e4d157b.recoverMethod_a_87852596(var1, var9, "h5_tpl", 3, 1048576);
                  if (var10 != null) {
                     try {
                        String var11 = var8;
                        Logger.recoverMethod_d_e925fa09("InAppTplHelper", "download image resource success, path: " + var8);
                        if (var11.startsWith("http://") || var11.startsWith("https://")) {
                           var11 = RecoverType9b4c5ae7.recoverMethod_a_e7a3948b(var11, false);
                        }

                        String var12 = RecoverType9b4c5ae7.recoverMethod_a_a5a07872(var1, "h5_noti_imgs", 0, var4) + var11;
                        boolean var13 = RecoverType9b4c5ae7.recoverMethod_a_44c5a30a(var12, var10);
                        if (var13) {
                           var6.add(var12);
                           Logger.recoverMethod_d_e925fa09("InAppTplHelper", "download image resource cache to local success, path: " + var12);
                        }

                        Logger.recoverMethod_v_e925fa09("InAppTplHelper", "Succeed to load image - " + var12 + ", cache local status: " + var13);
                     } catch (Throwable var14) {
                        Logger.recoverMethod_ww_1765c98c("InAppTplHelper", "Write storage error,  create img file fail.", var14);
                        var5 = false;
                     }
                  } else {
                     Logger.recoverMethod_w_e925fa09("InAppTplHelper", "download image resource failed, path: " + var9);
                     var5 = false;
                  }
               } else {
                  Logger.recoverMethod_w_e925fa09("InAppTplHelper", "image url " + var9 + " is not image url");
                  var5 = false;
               }
            }

            Logger.recoverMethod_i_e925fa09("InAppTplHelper", "template image resource download completed. local image list: " + var6.toString());
         }
      } catch (Throwable var15) {
         var5 = false;
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "load h5 template image resource failed, " + var15.getMessage());
      }

      return var5 ? var6 : null;
   }

   private static LinkedHashMap recoverMethod_a_af8e030f(Context var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 == null) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "readTemplateConfig context is null");
         return var1;
      }

      Object var2 = null;

      try {
         File var3 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var0, "tpl.cache");
         if (null != var3 && var3.exists()) {
            String var4 = RecoverType9b4c5ae7.recoverMethod_c_af215df4(var3);
            if (!TextUtils.isEmpty(var4)) {
               JSONObject var5 = new JSONObject(var4);
               Iterator var6 = var5.keys();

               while (var6.hasNext()) {
                  String var7 = (String)var6.next();
                  JSONObject var8 = var5.getJSONObject(var7);
                  RecoverType2e4d0a38 var9 = RecoverType2e4d0a38.recoverMethod_a_ef875d04(var8);
                  if (var9 != null) {
                     var1.put(var9.recoverField_a, var9);
                  }
               }
            }
         }
      } catch (Throwable var13) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "readTemplateConfig failed, " + var13.getMessage());
      } finally {
         RecoverType9b4c5af2.recoverMethod_a_d17885d6((Closeable)var2);
      }

      return var1;
   }

   private static void recoverMethod_a_f6dacb59(Context var0, LinkedHashMap var1) {
      try {
         File var2 = RecoverType9b4c5ae7.recoverMethod_a_d4d80730(var0, "tpl.cache");
         if (null == var2) {
            Logger.recoverMethod_w_e925fa09("InAppTplHelper", "saveTemplateConfig file is null");
            return;
         }

         JSONObject var3 = new JSONObject();
         if (var1.size() > 20) {
            var1 = recoverMethod_a_7ebddd33(var1);
         }

         int var4 = 0;

         for (Object recoveryElement_138 : var1.entrySet()) {
            Entry var6 = (Entry)recoveryElement_138;
            RecoverType2e4d0a38 var7 = (RecoverType2e4d0a38)var6.getValue();
            if (var4 < 20) {
               String var8 = (String)var6.getKey();
               var3.put(var8, var7.recoverMethod_a_a7dbfe3());
            } else {
               String var11 = var7.recoverField_b;
               File var9 = new File(var11);
               if (var9.exists() && var9.isFile()) {
                  var9.delete();
                  Logger.recoverMethod_d_e925fa09(
                     "InAppTplHelper", "exceed max template cache count, delete old template file: " + var11 + ", maxCacheCount: " + 20
                  );
               }
            }

            var4++;
         }

         Logger.recoverMethod_d_e925fa09(
            "InAppTplHelper", "[saveTemplateConfig] template config info: " + var3.toString() + ", templateSize: " + var1.values().size()
         );
         RecoverType9b4c5ae7.recoverMethod_a_e925fa0d(var2.getAbsolutePath(), var3.toString());
      } catch (Throwable var10) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "saveTemplateConfig failed, " + var10.getMessage());
      }
   }

   public static LinkedHashMap recoverMethod_a_7ebddd33(LinkedHashMap var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      try {
         Set var2 = var0.entrySet();
         ArrayList var3 = new ArrayList(var2);
         Collections.sort(var3, new Comparator() {
            public int recoverMethod_a_1aaf6588(Entry var1, Entry var2x) {
               if (((RecoverType2e4d0a38)var2x.getValue()).recoverField_c > ((RecoverType2e4d0a38)var1.getValue()).recoverField_c) {
                  return 1;
               } else {
                  return ((RecoverType2e4d0a38)var2x.getValue()).recoverField_c < ((RecoverType2e4d0a38)var1.getValue()).recoverField_c ? -1 : 0;
               }
            }

            // $VF: synthetic method
            @Override
            public int compare(Object var1, Object var2) {
               return this.recoverMethod_a_1aaf6588((Entry)var1, (Entry)var2);
            }
         });

         for (Object recoveryElement_188 : var3) {
            Entry var5 = (Entry)recoveryElement_188;
            var1.put((String)var5.getKey(), (RecoverType2e4d0a38)var5.getValue());
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "sortHashMap failed, " + var6.getMessage());
      }

      return var1;
   }

   public static RecoverType2e4cce2a recoverMethod_a_f0b11163(Context var0, RecoverType2e4cce2a var1) {
      try {
         String var2 = "";
         String var3 = var1.recoverField_bi;
         if (TextUtils.isEmpty(var3)) {
            var3 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var1.recoverField_bj);
         }

         String var4 = var1.recoverField_d;
         LinkedHashMap var5 = recoverMethod_a_af8e030f(var0);
         RecoverType2e4d0a38 var6 = (RecoverType2e4d0a38)var5.get(var3);
         if (null != var6) {
            var2 = var6.recoverField_b;
            if (RecoverType9b4c5ae7.recoverMethod_a_aad3b203(var2)) {
               var1.recoverField_be = "file://" + var2;
               var6.recoverField_c = System.currentTimeMillis();
               boolean var7 = new File(var2).setLastModified(var6.recoverField_c);
               Logger.recoverMethod_d_e925fa09(
                  "InAppTplHelper",
                  "use cache template, templateId: "
                     + var3
                     + ", webPagePath: "
                     + var2
                     + ", totalTemplateCount: "
                     + var5.values().size()
                     + ", update template file time: "
                     + var7
               );
            } else {
               var2 = "";
               var5.remove(var3);
               Logger.recoverMethod_d_e925fa09("InAppTplHelper", "local config has template path, but the template file is not exist, will download again.");
            }

            recoverMethod_a_f6dacb59(var0, var5);
         }

         String var19 = "";
         String var8 = var1.recoverField_bj;
         if (TextUtils.isEmpty(var2)) {
            boolean var9 = false;
            Logger.recoverMethod_d_e925fa09("InAppTplHelper", "no local template cache, start download template, url: " + var8);
            if (!TextUtils.isEmpty(var8)) {
               String var10 = RecoverType2e4d157b.recoverMethod_a_3c5a5db8(var0, var8, "inapp_tpl", 5, 5000L);
               if (!TextUtils.isEmpty(var10)) {
                  var9 = true;
                  var19 = var10;
               } else {
                  var9 = false;
               }
            } else {
               Logger.recoverMethod_w_e925fa09("InAppTplHelper", "template url is Empty! Give up to download!");
            }

            if (!var9) {
               Logger.recoverMethod_w_e925fa09("InAppTplHelper", "NOTE: failed to download html page. Give up this.");
               return null;
            }

            String var21 = RecoverType9b4c5ae7.recoverMethod_a_39e223ba(var0, "h5_tpls", 20);
            var2 = var21 + var3 + ".html";
            Logger.recoverMethod_d_e925fa09("InAppTplHelper", "download template success, will save to local of path: " + var2);
            if (RecoverType9b4c5ae7.recoverMethod_b_e925fa0d(var2, var19)) {
               var1.recoverField_be = "file://" + var2;
               Logger.recoverMethod_d_e925fa09("InAppTplHelper", "cache h5 template to local success, templateId: " + var3 + ", path: " + var1.recoverField_be);
               var6 = RecoverType2e4d0a38.recoverMethod_a_574ffbd2(var3, var2, System.currentTimeMillis());
               var5.put(var3, var6);
               recoverMethod_a_f6dacb59(var0, var5);
            } else {
               var1.recoverField_be = var1.recoverField_bj;
            }
         }

         if (var1.recoverField_bn.size() > 0) {
            Logger.recoverMethod_d_e925fa09(
               "InAppTplHelper", "template has image resource, start download image to local, images: " + var1.recoverField_bn.toString()
            );
            String var20 = var8.substring(0, var8.lastIndexOf("/") + 1);
            ArrayList var22 = recoverMethod_a_212b8e01(var1.recoverField_bn, var0, var20, var4);
            if (var22 == null) {
               Logger.recoverMethod_w_e925fa09("InAppTplHelper", "Load notify in-app message image resources failed, give up this in app!");
               return null;
            }

            Logger.recoverMethod_d_e925fa09("InAppTplHelper", "Load notify in-app message image succeed, webView will open cache!");
            String var11 = var1.recoverField_bl;

            try {
               JSONObject var12 = new JSONObject(var11);

               for (int var13 = 0; var13 < var22.size(); var13++) {
                  String var14 = "in_pic_path" + (var13 + 1);
                  var12.put(var14, var22.get(var13));
               }

               var1.recoverField_bl = var12.toString();
               Logger.recoverMethod_d_e925fa09("InAppTplHelper", "http image resources replace local path, new templateData: " + var1.recoverField_bl);
            } catch (Throwable var15) {
               var1.recoverField_bl = var11;
               Logger.recoverMethod_w_e925fa09("InAppTplHelper", "replace template picture resource error, " + var15.getMessage());
            }
         } else {
            Logger.recoverMethod_d_e925fa09("InAppTplHelper", "template no image resource.");
         }
      } catch (Throwable var16) {
      }

      return var1;
   }

   private static String recoverMethod_a_22670df9(Context var0, String var1, String var2) {
      try {
         byte[] var3 = RecoverType2e4d157b.recoverMethod_a_69351af0(var0, var1, "inapp_image", 3, 3145728, true);
         if (var3 != null) {
            try {
               String var4 = var1;
               Logger.recoverMethod_d_e925fa09("InAppTplHelper", "download image resource success, path: " + var4);
               if (var4.startsWith("http://") || var4.startsWith("https://")) {
                  var4 = RecoverType9b4c5ae7.recoverMethod_a_e7a3948b(var4, true);
               }

               String var5 = RecoverType9b4c5ae7.recoverMethod_a_a5a07872(var0, "j_in_app_" + var2, 0, true) + var4;
               boolean var6 = RecoverType9b4c5ae7.recoverMethod_a_44c5a30a(var5, var3);
               if (var6) {
                  Logger.recoverMethod_d_e925fa09("InAppTplHelper", "download image resource cache to local success, path: " + var5);
                  Logger.recoverMethod_d_e925fa09(
                     "InAppTplHelper", "download image resource cache to local success, Uri.fromFile: " + Uri.fromFile(new File(var4))
                  );
                  RecoverType9b4c5ae9.recoverMethod_a_e925fa09(var5, var1);
                  return var5;
               }

               Logger.recoverMethod_v_e925fa09("InAppTplHelper", "Succeed to load image - " + var5 + ", cache local status: " + var6);
            } catch (Throwable var7) {
               Logger.recoverMethod_ww_1765c98c("InAppTplHelper", "Write storage error,  create img file fail.", var7);
            }
         } else {
            Logger.recoverMethod_w_e925fa09("InAppTplHelper", "download image resource failed, path: " + var1);
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "load h5 template image resource failed, " + var8.getMessage());
      }

      return null;
   }

   public static RecoverType2e4cce2a recoverMethod_b_f0b11163(Context var0, RecoverType2e4cce2a var1) {
      try {
         JSONObject var2 = new JSONObject(var1.recoverField_i);
         boolean var3 = false;
         if (var2.has("image")) {
            String var4 = var2.optString("image");
            if (!TextUtils.isEmpty(var4)) {
               Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage path: " + var4);
               if (var4.startsWith("http")) {
                  if (RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var4, true)) {
                     String var5 = recoverMethod_a_22670df9(var0, var4, var1.recoverField_d);
                     Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage downImage: " + var5);
                     if (TextUtils.isEmpty(var5)) {
                        recoverMethod_b_9d23c587(var0, var4, var1.recoverField_d);
                        return null;
                     }

                     var3 = true;
                     var2.put("image", var5);
                  } else if (10 != var1.recoverField_bt) {
                     Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage path  is not image so delete cache message");
                     RecoverType8b455831.recoverMethod_a_510e8a7c(var0, var1.recoverField_d, 3008L, 4);
                     RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_aad3b203(var1.recoverField_d);
                     return null;
                  }
               }
            }
         }

         if (10 == var1.recoverField_bt && var2.has("banner")) {
            JSONObject var8 = var2.getJSONObject("banner");
            if (var8.has("small_image")) {
               String var9 = var8.optString("small_image");
               if (!TextUtils.isEmpty(var9)) {
                  Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage small path: " + var9);
                  if (var9.startsWith("http")) {
                     if (!RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var9, true)) {
                        RecoverType8b455831.recoverMethod_a_510e8a7c(var0, var1.recoverField_d, 3008L, 4);
                        Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage small  path  is not image so delete cache message");
                        RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_aad3b203(var1.recoverField_d);
                        return null;
                     }

                     String var6 = recoverMethod_a_22670df9(var0, var9, var1.recoverField_d);
                     Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage  small downImage: " + var6);
                     if (TextUtils.isEmpty(var6)) {
                        recoverMethod_b_9d23c587(var0, var9, var1.recoverField_d);
                        return null;
                     }

                     var8.put("small_image", var6);
                     var3 = true;
                  }
               }
            }
         }

         if (var3) {
            var1.recoverField_i = var2.toString();
         }
      } catch (Throwable var7) {
         Logger.recoverMethod_w_e925fa09("InAppTplHelper", "load  image resource failed, " + var7.getMessage());
      }

      return var1;
   }

   private static void recoverMethod_b_9d23c587(Context var0, String var1, String var2) {
      Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage   downImage is null ");
      String var3 = RecoverType2e4d157b.recoverMethod_a_e96ea081(var1);
      Logger.recoverMethod_d_e925fa09("InAppTplHelper", "handleImage   downImage is null,errorCode= " + var3);
      if (!TextUtils.isEmpty(var3)) {
         Logger.recoverMethod_d_e925fa09("InAppTplHelper", "downImage fail by network,so save it,wait network change good to show ");
         RecoverType2e4d157b.recoverMethod_b_aad3b1ff(var1);
      } else {
         Logger.recoverMethod_d_e925fa09("InAppTplHelper", "downImage fail by other reason，so delete message");
         RecoverType8b455831.recoverMethod_a_510e8a7c(var0, var2, 3008L, 4);
         RecoverType2e4cfb34.recoverMethod_a_cd35b0fc().recoverMethod_c_aad3b203(var2);
      }
   }
}
