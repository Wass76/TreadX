package com.TreadX.dealers.enums;

public enum LeadStatus {
    NEW,           // Initial state when lead is created
    CONTACTED,     // Initial contact has been made
    QUALIFIED,     // Lead has been qualified as a potential contact
    CONVERTED,     // Lead has been converted to a contact
    CLOSED         // Lead was not qualified or converted
}
