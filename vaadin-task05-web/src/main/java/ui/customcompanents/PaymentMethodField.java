package ui.customcompanents;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.components.PaymentMethod;
import entities.enums.PaymentType;
import lombok.Getter;
import utils.GetBeenFromSpringContext;
import utils.entitydescription.IVaadinUtil;

import java.util.EnumSet;

import static entities.components.PaymentMethod.MAX_GUARANTY_DEPOSIT_VALUE;
import static entities.components.PaymentMethod.MIN_GUARANTY_DEPOSIT_VALUE;

public class PaymentMethodField extends CustomField<PaymentMethod> {
    // A single-select radio button group
    @Getter
    private final RadioButtonGroup<PaymentType> paymentTypeRadioButtonGroup =
            new RadioButtonGroup<>();
    @Getter
    private TextField guarantyDeposit;
    private final Label cashLabel = new Label("Payment will be made directly in the hotel.");

    private String caption;

    private PaymentMethod value;

    private boolean isDoSetValue = false;

    public PaymentMethodField(String caption) {
        super();
        // delete after debug
        System.out.println("start -> PaymentMethodField constructor");

        this.caption = caption;
        configureComponents();

        // delete after debug
        System.out.println("STOP -> PaymentMethodField constructor");
    }

    @Override
    protected Component initContent() {
        // delete after debug
        System.out.println("start -> PaymentMethodField.initContent");

        Component component = buildLayout();

        // delete after debug
        System.out.println("STOP -> PaymentMethodField.initContent");

        return component;
    }

    private void configureComponents() {
        // get beans with names:   vaadinUtil
        IVaadinUtil vaadinUtil = GetBeenFromSpringContext.getBeen(IVaadinUtil.class);

        // paymentTypeRadioButtonGroup
        paymentTypeRadioButtonGroup.setItems(EnumSet.allOf(PaymentType.class));
        paymentTypeRadioButtonGroup.setItemCaptionGenerator(PaymentType::getPaymentTypeName);
        paymentTypeRadioButtonGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        paymentTypeRadioButtonGroup.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        paymentTypeRadioButtonGroup.setRequiredIndicatorVisible(true);          // required field
        paymentTypeRadioButtonGroup.addValueChangeListener(event -> {
            // save value before change as valueOld
            PaymentMethod valueOld = new PaymentMethod(value);
            // set default value (Cash) if value is null
            value = value == null ? new PaymentMethod() : value;

            if (PaymentType.CASH.equals(event.getValue())) {
                // for payment by Cash
                this.cashLabel.setVisible(true);
                this.guarantyDeposit.setVisible(false);
                // change value
                value.setPaymentType(event.getValue());
                value.setGuarantyDepositValue(0);

            } else {
                // for NOT Cash payment
                this.cashLabel.setVisible(false);
                this.guarantyDeposit.setVisible(true);
                // change value
                String guarantyDepositValueString = guarantyDeposit.getValue();
                value.setPaymentType(event.getValue());
                value.setGuarantyDepositValue(getGuarantyDepositValue(guarantyDepositValueString));
            }

            // fire value change event - need for Binder in future & update Notification  !!!
            if ( ! isDoSetValue) {
                this.fireEvent(this.createValueChange(valueOld, true));
            }
        });

        // guarantyDeposit
        String description = String.format("Percentage of prepayment. Integer, in [%d;%d]",
                MIN_GUARANTY_DEPOSIT_VALUE, MAX_GUARANTY_DEPOSIT_VALUE);
        guarantyDeposit = vaadinUtil.getStandardTextField(null, description, true);
        guarantyDeposit.setPlaceholder("Guaranty Deposit");
        guarantyDeposit.setValueChangeMode(ValueChangeMode.LAZY);
        guarantyDeposit.addValueChangeListener(event -> {
            // save value before change as valueOld
            PaymentMethod valueOld = new PaymentMethod(value);
            // set default value (Cash) if value is null
            value = value == null ? new PaymentMethod() : value;
            // change value
            value.setGuarantyDepositValue(getGuarantyDepositValue(event.getValue()));
            // set modified value to guarantyDeposit        !!!!!
            guarantyDeposit.setValue(value.getGuarantyDepositValue().toString());
            // fire value change event - need for Binder in future & update Notification  !!!
            if ( ! isDoSetValue) {
                this.fireEvent(this.createValueChange(valueOld, true));
            }
        });
    }

    private Component buildLayout() {
        VerticalLayout rootVertical = new VerticalLayout();
        rootVertical.setSizeUndefined();
        rootVertical.setMargin(false);
        if (caption != null) {
            super.setCaption(caption);
        }
        // set field components
        rootVertical.addComponents(paymentTypeRadioButtonGroup, guarantyDeposit, cashLabel);

        // set visible - cashLabel or guarantyDeposit
        if (this.value != null && PaymentType.CASH.equals(this.value.getPaymentType())) {
            // for payment by Cash
            this.cashLabel.setVisible(true);
            this.guarantyDeposit.setVisible(false);
        } else {
            // for NOT Cash payment
            this.cashLabel.setVisible(false);
            this.guarantyDeposit.setVisible(true);
        }

        return rootVertical;
    }

    @Override
    public PaymentMethod getValue() {

        return value;
    }

    @Override
    protected void doSetValue(PaymentMethod paymentMethod) {
        // delete after debug
        System.out.println("start -> PaymentMethodField.doSetValue");

        PaymentMethod valueOld = this.value;
        this.value = new PaymentMethod(paymentMethod);
        updateValues(valueOld);

        // delete after debug
        System.out.println("STOP -> PaymentMethodField.doSetValue");
    }

    private void updateValues(PaymentMethod valueOld) {
        if (getValue() != null) {
            isDoSetValue = true;
            paymentTypeRadioButtonGroup.setValue(value.getPaymentType());
            guarantyDeposit.setValue(value.getGuarantyDepositValue().toString());

//            // fire value change event - need for Binder in future  !!!
//            // clean comments if you wont notification as form start
//            this.fireEvent(this.createValueChange(valueOld, true));

            isDoSetValue = false;
        }

    }
    private Integer getGuarantyDepositValue(String guarantyDepositValueString) {
        Integer guarantyDepositValue = MIN_GUARANTY_DEPOSIT_VALUE;
        try {
            guarantyDepositValue = Integer.valueOf(guarantyDepositValueString);
        } catch (NumberFormatException exp) {
            guarantyDepositValue = MIN_GUARANTY_DEPOSIT_VALUE;
        }
        // guarantyDepositValue mast be integer (0 <= integer <= 100)
        // guarantyDepositValue mast be integer (MIN_GUARANTY_DEPOSIT_VALUE <= integer <= MAX_GUARANTY_DEPOSIT_VALUE)
        guarantyDepositValue = guarantyDepositValue == null
                ? MIN_GUARANTY_DEPOSIT_VALUE : guarantyDepositValue;
        guarantyDepositValue = guarantyDepositValue < MIN_GUARANTY_DEPOSIT_VALUE
                ? MIN_GUARANTY_DEPOSIT_VALUE : guarantyDepositValue;
        guarantyDepositValue = guarantyDepositValue > MAX_GUARANTY_DEPOSIT_VALUE
                ? MAX_GUARANTY_DEPOSIT_VALUE : guarantyDepositValue;

        return guarantyDepositValue;
    }
}
