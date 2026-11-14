package io.intercom.android.sdk.api;

import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import java.io.IOException;
import retrofit2.Response;

public class ErrorObject {
   private final Twig twig = LumberMill.getLogger();
   private final Throwable throwable;
   private final int statusCode;
   @Nullable
   private final String errorBody;

   public ErrorObject(Throwable var1, @Nullable Response var2) {
      this.throwable = var1;
      this.errorBody = this.parseErrorBody(var2);
      this.statusCode = this.parseStatusCode(var2);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   @Nullable
   String getErrorBody() {
      return this.errorBody;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   boolean hasErrorBody() {
      return this.errorBody != null;
   }

   private int parseStatusCode(@Nullable Response var1) {
      return var1 != null ? var1.code() : -1;
   }

   private String parseErrorBody(@Nullable Response var1) {
      if (var1 != null && var1.errorBody() != null) {
         try {
            return var1.errorBody().string();
         } catch (IOException var3) {
            this.twig.internal("Couldn't parse error body: " + var3.getMessage());
         }
      }

      return null;
   }
}
