package io.intercom.android.sdk.metrics.ops;

public class OpsMetricObject {
   private final String type;
   private final String name;
   private final long value;
   private final String id;

   public OpsMetricObject(String var1, String var2, long var3, String var5) {
      this.type = var1;
      this.name = var2;
      this.value = var3;
      this.id = var5;
   }

   public String getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         OpsMetricObject var2 = (OpsMetricObject)var1;
         if (this.value != var2.value) {
            return false;
         } else if (!this.type.equals(var2.type)) {
            return false;
         } else {
            return !this.name.equals(var2.name) ? false : this.id.equals(var2.id);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = 31 * var1 + this.name.hashCode();
      var1 = 31 * var1 + (int)(this.value ^ this.value >>> 32);
      return 31 * var1 + this.id.hashCode();
   }

   @Override
   public String toString() {
      return "OpsMetricObject{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", value=" + this.value + ", id='" + this.id + '\'' + '}';
   }
}
