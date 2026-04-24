package it.hackerinside.etk.core.Models;

import java.util.List;

public enum KeyUsageProfile {

    DIGITAL_SIGNATURE(
        List.of(
        		KeyUsageBit.DIGITAL_SIGNATURE, 
        		KeyUsageBit.NON_REPUDIATION
        )
    ),

    ENCRYPTION(
        List.of(
            KeyUsageBit.KEY_AGREEMENT,
            KeyUsageBit.KEY_ENCIPHERMENT,
            KeyUsageBit.DATA_ENCIPHERMENT,
            KeyUsageBit.ENCIPHER_ONLY
        )
    );

    private final List<KeyUsageBit> requiredUsages;

    KeyUsageProfile(List<KeyUsageBit> requiredUsages) {
        this.requiredUsages = requiredUsages;
    }

    public List<KeyUsageBit> requiredUsages() {
        return requiredUsages;
    }
}