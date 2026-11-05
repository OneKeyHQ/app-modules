package io.intercom.android.sdk.tickets.create.reducers;

import io.intercom.android.sdk.blocks.lib.models.FormField;
import io.intercom.android.sdk.blocks.lib.models.ListOption;
import io.intercom.android.sdk.blocks.lib.models.Options;
import io.intercom.android.sdk.blocks.lib.models.TicketTypeV2;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel;
import io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer;
import io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer;
import io.intercom.android.sdk.tickets.create.data.TicketAttributeRequest;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a(\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000¨\u0006\t",
   d2 = {
         "reduceAttributeList",
         "",
         "Lio/intercom/android/sdk/tickets/create/data/TicketAttributeRequest;",
         "updatedQuestionId",
         "",
         "currentTicketType",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "currentUiState",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConditionalAttributesReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConditionalAttributesReducer.kt\nio/intercom/android/sdk/tickets/create/reducers/ConditionalAttributesReducerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,59:1\n1#2:60\n1869#3,2:61\n*S KotlinDebug\n*F\n+ 1 ConditionalAttributesReducer.kt\nio/intercom/android/sdk/tickets/create/reducers/ConditionalAttributesReducerKt\n*L\n26#1:61,2\n*E\n"
)
public final class ConditionalAttributesReducerKt {
   @NotNull
   public static final List reduceAttributeList(@NotNull String var0, @Nullable TicketTypeV2 var1, @NotNull io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var2) {
      Intrinsics.checkNotNullParameter(var0, "updatedQuestionId");
      Intrinsics.checkNotNullParameter(var2, "currentUiState");
      if (var1 != null) {
         TicketTypeV2 var3 = var1;
         int var4 = 0;
         Iterable var5 = var3.getFormFields();
         Iterator var6 = var5.iterator();

         Object var10000;
         while (true) {
            if (!var6.hasNext()) {
               var10000 = null;
               break;
            }

            Object var7 = var6.next();
            FormField var8 = (FormField)var7;
            int var9 = 0;
            if (Intrinsics.areEqual(var8.getId(), var0)) {
               var10000 = var7;
               break;
            }
         }

         FormField var10 = (FormField)var10000;
         if (var10 == null) {
            return CollectionsKt.emptyList();
         }

         if (var10.isControllingAttribute()) {
            List var30 = CollectionsKt.createListBuilder();
            List var31 = var30;
            int var32 = 0;
            Iterable var33 = var3.getFormFields();
            int var34 = 0;

            for (Object var12 : var33) {
               FormField var13 = (FormField)var12;
               int var14 = 0;
               String var15 = var13.getId();
               QuestionState var16 = null;
               Object var17 = null;
               if (var2 instanceof io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) {
                  Iterable var18 = ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)var2).getQuestions();
                  Iterator var19 = var18.iterator();

                  Object var36;
                  while (true) {
                     if (var19.hasNext()) {
                        Object var20 = var19.next();
                        QuestionState var21 = (QuestionState)var20;
                        int var22 = 0;
                        if (!Intrinsics.areEqual(var21.getQuestionModel().getId(), var15)) {
                           continue;
                        }

                        var36 = var20;
                        break;
                     }

                     var36 = null;
                     break;
                  }

                  var16 = (QuestionState)var36;
                  var17 = var16 != null ? var16.getAnswer() : null;
               }

               if (var17 != null) {
                  Object var35 = var17;
                  String var37;
                  if (var35 instanceof io.intercom.android.sdk.survey.ui.models.Answer$DateTimeAnswer) {
                     var37 = null;
                  } else if (var35 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer) {
                     var37 = null;
                  } else if (var35 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MultipleAnswer) {
                     var37 = null;
                  } else if (Intrinsics.areEqual(var35, io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$InitialNoAnswer.INSTANCE)) {
                     var37 = null;
                  } else if (Intrinsics.areEqual(var35, io.intercom.android.sdk.survey.ui.models.Answer$NoAnswer$ResetNoAnswer.INSTANCE)) {
                     var37 = null;
                  } else {
                     if (!(var35 instanceof io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)) {
                        throw new NoWhenBranchMatchedException();
                     }

                     label97:
                     if ((var16 != null ? var16.getQuestionModel() : null) instanceof io.intercom.android.sdk.survey.model.SurveyData$Step$Question$DropDownQuestionModel) {
                        Options var38 = var13.getOptions();
                        if (var38 != null) {
                           List var39 = var38.getListOptions();
                           if (var39 != null) {
                              Iterable var23 = var39;
                              Iterator var24 = var23.iterator();

                              Object var40;
                              while (true) {
                                 if (!var24.hasNext()) {
                                    var40 = null;
                                    break;
                                 }

                                 Object var25 = var24.next();
                                 ListOption var26 = (ListOption)var25;
                                 int var27 = 0;
                                 if (Intrinsics.areEqual(var26.getLabel(), ((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var17).getAnswer())) {
                                    var40 = var25;
                                    break;
                                 }
                              }

                              ListOption var41 = (ListOption)var40;
                              if ((ListOption)var40 != null) {
                                 var37 = var41.getId();
                                 break label97;
                              }
                           }
                        }

                        var37 = null;
                     } else {
                        var37 = ((io.intercom.android.sdk.survey.ui.models.Answer$SingleAnswer)var17).getAnswer();
                     }
                  }

                  Object var28 = var37;
                  if (var28 != null) {
                     var31.add(new TicketAttributeRequest(var15, var28));
                  }
               }
            }

            return CollectionsKt.build(var30);
         }
      }

      return CollectionsKt.emptyList();
   }
}
