package cn.jiguang.an;

import android.content.Context;
import cn.jiguang.am.RecoverTypeaf2a9ae5;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.io.File;
import java.io.RandomAccessFile;

public class RecoverTypeaf2a9ea6 {
   public static boolean recoverMethod_a_e5090bb8(Context var0, String var1, File var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UploadLogUtils", "url is : " + var1 + "\n fileName is : " + var2.getName());
      RandomAccessFile var3 = null;

      try {
         String var4 = "Content-Disposition: form-data";
         byte[] var5 = new byte[(int)var2.length()];
         var3 = new RandomAccessFile(var2, "r");
         var3.read(var5);
         HttpRequest var6 = new HttpRequest(var1);
         var6.setRequestProperty("Connection", "Keep-Alive");
         var6.setRequestProperty("Content-Type", "multipart/form-data");
         var6.setUseCaches(false);
         var6.setHaveRspData(true);
         var6.setDoInPut(true);
         var6.setDoOutPut(true);
         var6.setBody(var5);
         HttpResponse var7 = HttpUtils.httpResponse(var0, var6, false, true);
         int var8 = var7.getResponseCode();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UploadLogUtils", "response code is " + var8 + ", response message is " + var7.toString());
         if (var8 == 200) {
            return true;
         }

         String var9 = var7.getResponseBody();
         RecoverTypeaf2a9ae5.recoverMethod_a_93bf8bd5(var0, false, "", var9);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UploadLogUtils", "error response is : " + var9);
      } catch (Throwable var13) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UploadLogUtils", "error message is : " + var13.getMessage());
      } finally {
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var3);
      }

      return false;
   }
}
