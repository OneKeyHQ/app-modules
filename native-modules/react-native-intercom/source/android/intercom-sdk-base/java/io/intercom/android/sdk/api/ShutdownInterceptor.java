package io.intercom.android.sdk.api;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Interceptor.Chain;
import org.json.JSONException;
import org.json.JSONObject;

public class ShutdownInterceptor implements Interceptor {
   private static final String ERROR = "error";
   private static final String TYPE = "type";
   private static final String MESSAGE = "message";
   private static final String MESSENGER_SHUTDOWN_RESPONSE = "messenger_shutdown_response";
   private static final String SHUTDOWN_PERIOD = "shutdown_period";
   private final ShutdownState shutdownState;
   private final Twig twig = LumberMill.getLogger();

   public ShutdownInterceptor(ShutdownState var1) {
      this.shutdownState = var1;
   }

   @Override
   public Response intercept(Chain var1) throws IOException {
      if (!this.shutdownState.canSendNetworkRequests()) {
         this.twig.e(this.shutdownState.getShutdownReason());
         throw new IOException(this.shutdownState.getShutdownReason());
      }

      Response var2 = var1.proceed(var1.request());
      if (!var2.isSuccessful()) {
         ResponseBody var3 = var2.body();
         String var4 = var3.string();
         var2 = var2.newBuilder().body(ResponseBody.create(var3.contentType(), var4)).build();
         var3.close();

         try {
            JSONObject var5 = new JSONObject(var4);
            JSONObject var6 = var5.getJSONObject("error");
            if (var6.getString("type").equals("messenger_shutdown_response")) {
               long var7 = var6.getLong("shutdown_period");
               String var9 = var6.getString("message");
               this.shutdownState.updateShutdownState(TimeUnit.SECONDS.toMillis(var7), var9);
            }
         } catch (JSONException var10) {
            this.twig.internal("Failed to deserialise error response: `" + var4 + "` message: `" + var2.message() + "`");
         }
      }

      return var2;
   }
}
