package io.intercom.android.sdk.utilities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface AttributeValidatorUtils$AttributeResponseCode {
   int OK = 0;
   int INVALID_STRING = 1;
   int INVALID_EMAIL = 2;
   int INVALID_NUMBER = 3;
   int INVALID_NUMBER_NEEDS_PREFIX = 4;
   int INVALID_NUMBER_HAS_TOO_MANY_DIGITS = 5;
   int INVALID_COUNTRY_CODE = 6;
   int INVALID_NUMBER_NOT_ENOUGH_DIGITS = 7;
}
