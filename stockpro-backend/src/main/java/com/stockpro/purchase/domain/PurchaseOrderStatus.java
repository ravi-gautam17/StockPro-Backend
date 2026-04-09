package com.stockpro.purchase.domain;

public enum PurchaseOrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    /***
     * Manager/ Admin declined - can be edited and resubmitted (→ {@link #PENDING_APPROVAL}).
     */
    REJECTED,
    APPROVED,
    PARTIALLY_RECEIVED,
    FULLY_RECEIVED,
    CANCELLED
}
