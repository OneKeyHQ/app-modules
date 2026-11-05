package io.intercom.android.sdk.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface AttributeType {
   String TEXT = "text";
   String EMAIL = "email";
   String WEBSITE = "company.website";
   String NUMBER = "number";
   String FLOAT = "float";
   String PHONE = "phone";
   String BOOLEAN = "boolean";
   String LIST = "list";
   String DATE = "date";
   String UNKNOWN = "unknown";
   String INTEGER = "integer";
}
