package entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
        CREDIT_CARD("Credit Card"),
        CASH("Cash");

        private String paymentTypeName;
}
