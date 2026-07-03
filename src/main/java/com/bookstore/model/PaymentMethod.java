package com.bookstore.model;

public enum PaymentMethod {
    CASH_ON_DELIVERY("Thanh toán khi nhận hàng"),
    ONLINE_BANKING("Thanh toán Online qua Ngân hàng"),
    MOMO("Ví MoMo"),
    VNPAY("Ví VNPay");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
