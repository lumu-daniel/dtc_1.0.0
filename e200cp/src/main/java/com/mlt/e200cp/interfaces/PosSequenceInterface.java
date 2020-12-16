package com.mlt.e200cp.interfaces;

import com.mlt.e200cp.models.response.ISOPaymentResponse;

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
     void onStartProcessing();
     void onTransactionSuccessful(ISOPaymentResponse response);
     void onTrasactionError(String error);
     void onTrasactionSvcError(String error, ISOPaymentResponse response);
     void onTransactionEnded(ISOPaymentResponse response);
     void removeCardDisplayed();
}
