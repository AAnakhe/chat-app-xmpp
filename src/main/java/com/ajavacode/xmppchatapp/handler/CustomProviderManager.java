package com.ajavacode.xmppchatapp.handler;

import org.jivesoftware.smack.provider.ProviderManager;

public class CustomProviderManager {

    static {
        ProviderManager.addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());
        ProviderManager.addExtensionProvider(ReadReceiptRequest.ELEMENT, ReadReceiptRequest.NAMESPACE, new ReadReceiptRequest.Provider());
    }

    public static void initialize() {
    }
}
