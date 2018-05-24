package entities.components;

import annotations.NoBulkUpdate;
import entities.enums.PaymentType;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.AccessType.PROPERTY;

@Data
@Embeddable
public class PaymentMethod {
    @NoBulkUpdate
    public final static String NULL_PAYMENT_METHOD_REPRESENTATION = "No payment method";
    @NoBulkUpdate
    public static final Integer MIN_GUARANTY_DEPOSIT_VALUE = 0;
    @NoBulkUpdate
    public static final Integer MAX_GUARANTY_DEPOSIT_VALUE = 100;

    // 	PAYMENT_TYPE	varchar(30)	NOT	NULL
    @Enumerated(EnumType.STRING)        //safe as varchar
    @Column(name = "PAYMENT_TYPE", nullable = false, length = 30)
    @Access(PROPERTY)
    private PaymentType paymentType;

    // 	GUARANTY_DEPOSIT_VALUE	int(11)
    @Column(name = "GUARANTY_DEPOSIT_VALUE")
    @Access(PROPERTY)
    private Integer guarantyDepositValue = 0;

    public PaymentMethod() {
        // by default - cash type
        this(PaymentType.CASH, 0);
    }

    public PaymentMethod(PaymentMethod paymentMethod) {
        this(paymentMethod == null ? null : paymentMethod.getPaymentType(),
                paymentMethod == null ? null : paymentMethod.getGuarantyDepositValue());
    }

    public PaymentMethod(PaymentType paymentType, Integer guarantyDepositValue) {
        if (paymentType == null || PaymentType.CASH.equals(paymentType)) {
            // by default - cash type
            this.paymentType = PaymentType.CASH;
            this.guarantyDepositValue = 0;
        } else {
            this.paymentType = paymentType;
            this.guarantyDepositValue = guarantyDepositValue == null
                    ? 0
                    : guarantyDepositValue;
        }
    }


    @Override
    public String toString() {
        return
//                "PaymentMethod{" +
                "{" +
//                "paymentType=" + paymentType.getPaymentTypeName() +
                        "Type=" + (paymentType == null ? "null" : paymentType.getPaymentTypeName()) +
//                ", guarantyDepositValue=" + guarantyDepositValue +
                        ", Guaranty Deposit=" + guarantyDepositValue +
                        "%}";
    }
}
