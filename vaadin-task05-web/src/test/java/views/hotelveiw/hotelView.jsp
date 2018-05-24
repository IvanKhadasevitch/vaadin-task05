
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

</head>
<body>
<%-- base Menu --%>
<div tabindex="0" class="v-menubar v-widget borderless v-menubar-borderless" role="menubar">
    <span tabindex="-1" class="v-menubar-menuitem v-menubar-menuitem-unchecked" role="menuitem">
        <span class="v-menubar-menuitem-caption"><span class="v-icon Vaadin-Icons"></span>
            Hotel
        </span>
    </span>
    <span tabindex="-1" class="v-menubar-menuitem v-menubar-menuitem-checked" role="menuitem">
        <span class="v-menubar-menuitem-caption"><span class="v-icon Vaadin-Icons"></span>
            Category
        </span>
    </span>
</div>

<%-- PaymenMethod by Cash--%>
<div class="v-customcomponent v-widget v-required v-customcomponent-required v-has-width" id="paymentMethodFieldHotelView_id" aria-labelledby="gwt-uid-33" aria-required="true" style="width: 100%;">
    <div class="v-verticallayout v-layout v-vertical v-widget">
        <div class="v-slot">
            <div class="v-label v-widget v-label-undef-w">Old: {Type=Cash, Guaranty Deposit=0%}</div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot">
            <div class="v-label v-widget v-label-undef-w">New: {Type=Cash, Guaranty Deposit=0%}</div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot v-slot-horizontal v-slot-small">
            <div class="v-widget v-has-caption v-caption-on-right">
                <div tabindex="0" class="v-select-optiongroup v-widget horizontal v-select-optiongroup-horizontal small v-select-optiongroup-small v-required v-select-optiongroup-required" aria-required="true" id="paymentMethodFieldRadioButtonGroupHotelView_id" aria-labelledby="gwt-uid-25" role="radiogroup" style="outline-style: none;">
                    <span class="v-radiobutton v-select-option">
                        <input type="radio" name="gwt-uid-22" value="on" id="gwt-uid-43" tabindex="0">
                        <label for="gwt-uid-43">Credit Card</label>
                    </span>
                    <span class="v-radiobutton v-select-option v-select-option-selected">
                        <input type="radio" name="gwt-uid-22" value="on" id="gwt-uid-44" tabindex="0" checked="">
                        <label for="gwt-uid-44">Cash</label>
                    </span>
                </div>
                <div class="v-caption v-caption-horizontal v-caption-small" id="gwt-uid-25" for="gwt-uid-26">
                    <span class="v-required-field-indicator" aria-hidden="true">*</span>
                </div>
            </div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot">
            <div class="v-label v-widget v-label-undef-w">Payment will be made directly in the hotel.</div>
        </div>
    </div>
</div>

<%-- PaymenMethod by Credit Card--%>
<div class="v-customcomponent v-widget v-required v-customcomponent-required v-has-width" id="paymentMethodFieldHotelView_id" aria-labelledby="gwt-uid-33" aria-required="true" style="width: 100%;">
    <div class="v-verticallayout v-layout v-vertical v-widget">
        <div class="v-slot">
            <div class="v-label v-widget v-label-undef-w">Old: {Type=Cash, Guaranty Deposit=0%}</div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot">
            <div class="v-label v-widget v-label-undef-w">New: {Type=Credit Card, Guaranty Deposit=0%}</div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot v-slot-horizontal v-slot-small">
            <div class="v-widget v-has-caption v-caption-on-right">
                <div tabindex="0" class="v-select-optiongroup v-widget v-required v-select-optiongroup-required horizontal v-select-optiongroup-horizontal small v-select-optiongroup-small" aria-required="true" id="paymentMethodFieldRadioButtonGroupHotelView_id" aria-labelledby="gwt-uid-25" role="radiogroup" style="outline-style: none;">
                    <span class="v-radiobutton v-select-option v-select-option-selected">
                        <input type="radio" name="gwt-uid-22" value="on" id="gwt-uid-43" tabindex="0" checked="">
                        <label for="gwt-uid-43">Credit Card</label>
                    </span>
                    <span class="v-radiobutton v-select-option">
                        <input type="radio" name="gwt-uid-22" value="on" id="gwt-uid-44" tabindex="0">
                        <label for="gwt-uid-44">Cash</label>
                    </span>
                </div>
                <div class="v-caption v-caption-horizontal v-caption-small" id="gwt-uid-25" for="paymentMethodFieldRadioButtonGroupHotelView_id">
                    <span class="v-required-field-indicator" aria-hidden="true">*</span>
                </div>
            </div>
        </div>
        <div class="v-spacing"></div>
        <div class="v-slot v-slot-small">
            <div class="v-widget v-has-caption v-caption-on-right">
                <input type="text" class="v-textfield v-widget small v-textfield-small v-required v-textfield-required" aria-required="true" id="paymentMethodFieldGuarantyDepositHotelView_id" aria-labelledby="gwt-uid-45" placeholder="Guaranty Deposit" tabindex="0">
                <div class="v-caption v-caption-small" id="gwt-uid-45" for="gwt-uid-46">
                    <span class="v-required-field-indicator" aria-hidden="true">*</span>
                </div>
            </div>
        </div>
    </div>
</div>

<label for="gwt-uid-54">Credit Card</label>

<%-- HotelEditForm CategoryNativeSelect --%>
<div tabindex="0" class="v-select v-widget small v-select-small v-required v-select-required" id="categoryNativeSelectHotelEditForm_id" aria-labelledby="gwt-uid-37" aria-required="true" style="outline-style: none;">
    <select class="v-select-select" size="1" tabindex="0">
        <option value=""></option>
        <option value="2">Hotel</option>
        <option value="3">Hostel</option>
        <option value="4">GuestHouse</option>
        <option value="5">Apartments</option>
        <option value="6">14</option>
        <option value="7">ha-ha-ha</option>
        <option value="8">SeleniumCategory_112</option>
        <option value="9">SeleniumCategory_1</option>
        <option value="10">SeleniumCategory_2</option>
        <option value="11">SeleniumCategory_3</option>
    </select>
</div>

</body>
</html>
