package io.intercom.android.sdk.tickets.create.reducers;

import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.FormField;
import io.intercom.android.sdk.blocks.lib.models.ListOption;
import io.intercom.android.sdk.blocks.lib.models.Options;
import io.intercom.android.sdk.blocks.lib.models.TicketTypeV2;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0001H\u0000\u001a\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002\u001a\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0002¨\u0006\u000f",
   d2 = {
         "convertToTicketFormUiState",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;",
         "ticketData",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "currentState",
         "getPlaceholderText",
         "",
         "type",
         "",
         "getInputType",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCreateTicketReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketReducer.kt\nio/intercom/android/sdk/tickets/create/reducers/CreateTicketReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,203:1\n1869#2:204\n295#2,2:205\n1563#2:207\n1634#2,3:208\n1870#2:213\n1#3:211\n118#4:212\n*S KotlinDebug\n*F\n+ 1 CreateTicketReducer.kt\nio/intercom/android/sdk/tickets/create/reducers/CreateTicketReducerKt\n*L\n57#1:204\n61#1:205,2\n78#1:207\n78#1:208,3\n57#1:213\n135#1:212\n*E\n"
)
public final class CreateTicketReducerKt {
   @NotNull
   public static final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState convertToTicketFormUiState(
      @NotNull TicketTypeV2 var0, @NotNull UserIdentity var1, @NotNull AppConfig var2, @Nullable io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "ticketData");
      Intrinsics.checkNotNullParameter(var1, "userIdentity");
      Intrinsics.checkNotNullParameter(var2, "config");
      List var4 = new ArrayList();
      List var5 = var3 instanceof io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content ? ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)var3).getQuestions() : CollectionsKt.emptyList();
      String var10000 = var1.getEmail();
      Intrinsics.checkNotNullExpressionValue(var10000, "getEmail(...)");
      if (!StringsKt.isBlank(var10000)) {
         Collection var6 = var4;
         List var7 = CollectionsKt.emptyList();
         int var8 = string.intercom_placeholder_email_input;
         io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var9 = io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL;
         int var10 = string.intercom_get_notified_by_email;
         QuestionState var36 = new QuestionState(
            new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel("-1", var7, true, null, var9, null, false, var8, var10, 8, null),
            SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
         );
         QuestionState var39 = var36;
         int var41 = 0;
         String var10003 = var1.getEmail();
         Intrinsics.checkNotNullExpressionValue(var10003, "getEmail(...)");
         var39.setAnswer(new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer(var10003));
         QuestionState var37 = var36;
         var6.add(var37);
      }

      Iterable var35 = var0.getFormFields();
      int var38 = 0;

      for (Object var42 : var35) {
         FormField var43 = (FormField)var42;
         int var11 = 0;
         int var12 = getPlaceholderText(var43.getType());
         Iterable var13 = var5;
         int var14 = 0;
         Iterator var15 = var13.iterator();

         Object var76;
         while (true) {
            if (!var15.hasNext()) {
               var76 = null;
               break;
            }

            Object element$ivx = var15.next();
            QuestionState var17 = (QuestionState)element$ivx;
            int var18 = 0;
            if (Intrinsics.areEqual(var17.getQuestionModel().getId(), var43.getId())) {
               var76 = element$ivx;
               break;
            }
         }

         QuestionState var19 = (QuestionState)var76;
         if (var19 != null && !Intrinsics.areEqual(var43.getType(), "list")) {
            var4.add(var19);
         } else {
            String var44 = var43.getType();
            switch (var44.hashCode()) {
               case -891985903:
                  if (!var44.equals("string")) {
                     continue;
                  }
                  break;
               case 3322014:
                  if (!var44.equals("list")) {
                     continue;
                  }

                  Collection var46;
                  String var49;
                  List var55;
                  boolean var59;
                  List var79;
                  label125: {
                     var46 = var4;
                     var49 = var43.getId();
                     var55 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName()));
                     var59 = var43.getRequired();
                     Options var77 = var43.getOptions();
                     if (var77 != null) {
                        List var78 = var77.getListOptions();
                        if (var78 != null) {
                           Iterable var20 = var78;
                           int var21 = 0;
                           Iterable var22 = var20;
                           ArrayList var23 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var20, 10));
                           int var24 = 0;

                           for (Object var26 : var22) {
                              ListOption var27 = (ListOption)var26;
                              Collection var28 = var23;
                              int var29 = 0;
                              var28.add(var27.getLabel());
                           }

                           var79 = (List)var23;
                           break label125;
                        }
                     }

                     var79 = CollectionsKt.emptyList();
                  }

                  List var63 = var79;
                  io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel var80 = new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel(var49, var55, var59, var63, null, var12, 16, null);
                  SurveyUiColors var33 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
                  io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionModel var34 = var80;
                  QuestionState var50 = new QuestionState(var34, var33);
                  QuestionState var56 = var50;
                  int var60 = 0;
                  if (var43.getValue() != null) {
                     Options var64 = var43.getOptions();
                     if (var64 != null) {
                        List var30 = var64.getListOptions();
                        if (var30 != null) {
                           Iterable var31 = var30;
                           Iterator var32 = var31.iterator();

                           Object var81;
                           while (true) {
                              if (!var32.hasNext()) {
                                 var81 = null;
                                 break;
                              }

                              Object var67 = var32.next();
                              ListOption var68 = (ListOption)var67;
                              int var69 = 0;
                              if (Intrinsics.areEqual(var68.getId(), var43.getValue())) {
                                 var81 = var67;
                                 break;
                              }
                           }

                           ListOption var70 = (ListOption)var81;
                           if (var70 != null) {
                              ListOption var72 = var70;
                              int var74 = 0;
                              var56.setAnswer(new io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer(var72.getLabel()));
                           }
                        }
                     }
                  }

                  QuestionState var51 = var50;
                  var46.add(var51);
                  continue;
               case 64711720:
                  if (var44.equals("boolean")) {
                     Collection var45 = var4;
                     String var10004 = var43.getId();
                     List var10005 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName()));
                     boolean var10006 = var43.getRequired();
                     String[] var48 = new String[]{"True", "False"};
                     var45.add(
                        new QuestionState(
                           new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$SingleChoiceQuestionModel(var10004, var10005, var10006, CollectionsKt.listOf(var48), false),
                           SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
                        )
                     );
                  }
                  continue;
               case 97434231:
                  if (var44.equals("files")) {
                     var4.add(
                        new QuestionState(
                           new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$UploadFileQuestionModel(
                              var43.getId(),
                              CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName())),
                              Intrinsics.areEqual(var43.getIdentifier(), "single")
                                 ? new ActualString("")
                                 : new StringRes(string.intercom_file_max_limit, CollectionsKt.listOf(TuplesKt.to("limit", "10"))),
                              var43.getRequired(),
                              Intrinsics.areEqual(var43.getIdentifier(), "single") ? 1 : 10,
                              var2.getAttachmentSettings().getTrustedFileExtensions()
                           ),
                           SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
                        )
                     );
                  }
                  continue;
               case 97526364:
                  if (!var44.equals("float")) {
                     continue;
                  }
                  break;
               case 1793702779:
                  if (var44.equals("datetime")) {
                     var4.add(
                        new QuestionState(
                           new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DatePickerQuestionModel(
                              var43.getId(),
                              CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName())),
                              var43.getRequired()
                           ),
                           SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
                        )
                     );
                  }
                  continue;
               case 1958052158:
                  if (!var44.equals("integer")) {
                     continue;
                  }
                  break;
               default:
                  continue;
            }

            Collection var47 = var4;
            QuestionState var82;
            if (Intrinsics.areEqual(var43.getIdentifier(), "multiline")) {
               String var52 = var43.getId();
               List var57 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName()));
               boolean var61 = var43.getRequired();
               io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var65 = getInputType(var43.getType());
               int var73 = 120;
               int var75 = 0;
               float var71 = Dp.recovered$95b3d103663bf060(var73);
               var82 = new QuestionState(
                  new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$LongTextQuestionModel(var52, var57, var61, null, var65, null, var71, 0, var12, 136, null),
                  SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
               );
            } else {
               String var53 = var43.getId();
               List var58 = CollectionsKt.listOf(new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText(var43.getName()));
               boolean var62 = var43.getRequired();
               io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType var66 = getInputType(var43.getType());
               var82 = new QuestionState(
                  new io.intercom.android.sdk.survey.model.SurveyData$Step$Question$ShortTextQuestionModel(var53, var58, var62, null, var66, null, false, var12, null, 328, null),
                  SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null))
               );
            }

            QuestionState var54 = var82;
            var47.add(var54);
         }
      }

      return new io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content(var0.getName(), var4, false, false, 12, null);
   }

   private static final int getPlaceholderText(String var0) {
      String var1 = var0;
      switch (var1.hashCode()) {
         case 3322014:
            if (var1.equals("list")) {
               return string.intercom_please_select;
            }
            break;
         case 96619420:
            if (var1.equals("email")) {
               return string.intercom_placeholder_email_input;
            }
            break;
         case 97526364:
            if (var1.equals("float")) {
               return string.intercom_placeholder_numerical_inputs;
            }
            break;
         case 1958052158:
            if (var1.equals("integer")) {
               return string.intercom_placeholder_numerical_inputs;
            }
      }

      return string.intercom_placeholder_text_inputs;
   }

   private static final io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType getInputType(String var0) {
      String var1 = var0;
      switch (var1.hashCode()) {
         case 96619420:
            if (var1.equals("email")) {
               return io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.EMAIL;
            }
            break;
         case 97526364:
            if (var1.equals("float")) {
               return io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.FLOAT;
            }
            break;
         case 1958052158:
            if (var1.equals("integer")) {
               return io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.INTEGER;
            }
      }

      return io.intercom.android.sdk.survey.model.SurveyData$Step$Question$QuestionValidation$ValidationType.TEXT;
   }
}
