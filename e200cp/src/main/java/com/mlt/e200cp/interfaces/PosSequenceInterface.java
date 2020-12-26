package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.EmvTransactionDetails;
import com.mlt.e200cp.models.PosDetails;
import com.mlt.e200cp.models.repository.response.ISOPaymentResponse;

public interface PosSequenceInterface {

     void onPortOpened();
     void onCardInserted();
     void onCardTapped();
     void onCardSwipped();
     void onChipFallBack();
     void selectApp(CharSequence[] args,int Timeout);
     void onSwipeFallBack();
     void onStartPinEntry();
     void onPromptCheckPhone();
     void onStartProcessing(PosDetails posDetails,EmvTransactionDetails emvTransactionDetails);
     void onTransactionSuccessful(ISOPaymentResponse response);
     void onTrasactionError(String error);
     void onReverseTxn(EmvTransactionDetails transactionDetails, PosDetails posDetails);
     void onTrasactionSvcError(String error, ISOPaymentResponse response);
     void onTransactionEnded(String errorMessage,ISOPaymentResponse response);
     void removeCardDisplayed();
}
