package views.hotelveiw;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.Category;
import entities.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import services.IHotelService;
import ui.dataproviders.ICategoryDataProvider;
import ui.dataproviders.IHotelDataProvider;
import ui.customcompanents.PaymentMethodField;
import ui.rootui.NavigationUI;
import utils.GetBeenFromSpringContext;
import utils.entitydescription.IVaadinUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static entities.Hotel.MAX_RATING;
import static entities.Hotel.MIN_RATING;
import static entities.components.PaymentMethod.MAX_GUARANTY_DEPOSIT_VALUE;
import static entities.components.PaymentMethod.MIN_GUARANTY_DEPOSIT_VALUE;

@UIScope
@SpringComponent
public class HotelEditForm extends FormLayout {
    private static final Logger LOGGER = Logger.getLogger(HotelEditForm.class.getName());

    @Autowired
    private IVaadinUtil vaadinUtil;
    @Autowired
    private IHotelDataProvider hotelDataProvider;
    @Autowired
    private ICategoryDataProvider categoryDataProvider;

    private IHotelService hotelService;

    private Hotel hotel = new Hotel();
    private Binder<Hotel> hotelBinder = new Binder<>(Hotel.class);

    private TextField nameTextField;
    private TextField addressTextField;
    private TextField ratingTextField;
    private PaymentMethodField paymentMethodField;
    private DateField operatesFromDateField;
    private NativeSelect<Category> categoryNativeSelect = new NativeSelect<>("Category:");
    private TextField urlTextField;
    private TextArea descriptionTextArea;

    private Button saveHotelBtn;
    private Button closeFormBtn;

    public HotelEditForm() {

        // take beans:  hotelService, vaadinUtil, hotelDataProvider, categoryDataProvider
        this.hotelService = GetBeenFromSpringContext.getBeen(IHotelService.class);
    }

    @PostConstruct
    void init() {
        // use this method to determine view if Spring will create been
        // if we created bean with our own hands & @PostConstruct don't work

        // del after debug
        System.out.println("start -> HotelEditForm.init() ");

        // set view Configuration
        configureComponents();
        buildLayout();

        // del after debug
        System.out.println("STOP -> HotelEditForm.init() ");
    }
    private void configureComponents() {
        // add ToolTip to the forms fields & configure Components
        String caption = "Name:";
        String description = "Hotels name";
        nameTextField = vaadinUtil.getStandardTextField(caption, description, true);
        nameTextField.setId("nameTextFieldHotelEditForm_id");           // for testing

        caption = "Address:";
        description = "Hotels address";
        addressTextField = vaadinUtil.getStandardTextField(caption, description, true);
        addressTextField.setId("addressTextFieldHotelEditForm_id");           // for testing

        caption = "Rating:";
        description = String.format("Hotels rating from %d to %d stars", MIN_RATING, MAX_RATING);
        ratingTextField = vaadinUtil.getStandardTextField(caption, description, true);
        ratingTextField.setId("ratingTextFieldHotelEditForm_id");           // for testing

        caption = "Payment method:";
        description = "Method of payment for accommodation in the hotel.";
        paymentMethodField = new PaymentMethodField(caption);
        paymentMethodField.setId("paymentMethodFieldHotelEditForm_id");
        paymentMethodField.getPaymentTypeRadioButtonGroup().setId("paymentMethodFieldRadioButtonGroupHotelEditForm_id");
        paymentMethodField.getGuarantyDeposit().setId("paymentMethodFieldGuarantyDepositHotelEditForm_id");
        paymentMethodField.setDescription(description);
        paymentMethodField.setRequiredIndicatorVisible(true);
        paymentMethodField.addValueChangeListener(event -> {
            Notification.show(String.format("Payment Method changed.\n\rold value : %s\n\rnew value: %s",
                    event.getOldValue(), event.getValue()), Notification.Type.HUMANIZED_MESSAGE);
        });

        caption = "Operates from:";
        description = "Date of the beginning of the operating\n\rof the hotel must be in the past";
        operatesFromDateField = vaadinUtil.getStandardDateField(caption, description, true);

        // set DataProvider - it will set & refresh Category Items
        categoryNativeSelect.setDataProvider(categoryDataProvider.getDataProvider());
        categoryNativeSelect.setId("categoryNativeSelectHotelEditForm_id");         // for testing
        categoryNativeSelect.setItemCaptionGenerator(Category::getName);
        categoryNativeSelect.setDescription("Hotel category");
        categoryNativeSelect.addStyleName(ValoTheme.COMBOBOX_SMALL);
        categoryNativeSelect.setEmptySelectionAllowed(true);

        caption = "URL:";
        description = "Info about hotel on the booking.com";
        urlTextField = vaadinUtil.getStandardTextField(caption, description, true);
        urlTextField.setId("urlTextFieldHotelEditForm_id");         // for testing

        caption = "Description:";
        description = "Hotel description";
        // not required
        descriptionTextArea = vaadinUtil.getStandardTextArea(caption, description, false);
        descriptionTextArea.setId("descriptionTextAreaHotelEditForm_id");         // for testing

        // buttons
        caption = "Save";
        description = "Save data in data base";
        saveHotelBtn = vaadinUtil.getStandardFriendlyButton(caption, description);
        saveHotelBtn.setId("saveHotelBtnHotelEditForm_id");         // for testing
        saveHotelBtn.addClickListener(e -> saveHotel());

        caption = "Close";
        description = "Close without saving changes.";
        closeFormBtn = vaadinUtil.getStandardPrimaryButton(caption, description);
        closeFormBtn.addClickListener(e -> closeHotelEditForm());

        // let work saveHotelBtn only when form valid
        vaadinUtil.setFormSaveBtnControl(hotelBinder, saveHotelBtn);

        // connect entity fields with form fields by Binder
        hotelBinder.forField(nameTextField)
                   // Shorthand for requiring the field to be non-empty
                   .asRequired("Every hotel must have a name")
                   .bind(Hotel::getName, Hotel::setName);

        hotelBinder.forField(addressTextField)
                   .asRequired("Every hotel must have a address")
                   .bind(Hotel::getAddress, Hotel::setAddress);

        hotelBinder.forField(ratingTextField)
                   .asRequired("Every hotel must have a rating")
                   .withConverter(new StringToIntegerConverter("Enter an integer, please"))
                   .withValidator(rating -> rating >= MIN_RATING && rating <= MAX_RATING,
                           String.format("Rating must be in [%d;%d]", MIN_RATING, MAX_RATING))
                   .bind(Hotel::getRating, Hotel::setRating);

        hotelBinder.forField(paymentMethodField)
                   .asRequired("Every hotel must have a payment method")
                   .withValidator(p -> p != null && (p.getPaymentType() != null),
                           "Every hotel has a way of payment for accommodation")
                   .withValidator(p -> p.getGuarantyDepositValue() >= MIN_GUARANTY_DEPOSIT_VALUE &&
                                   p.getGuarantyDepositValue() <= MAX_GUARANTY_DEPOSIT_VALUE,
                           String.format("Guaranty deposit value must be integer in [%d;%d]",
                                   MIN_GUARANTY_DEPOSIT_VALUE, MAX_GUARANTY_DEPOSIT_VALUE))
                   .bind(Hotel::getPaymentMethod, Hotel::setPaymentMethod);

        hotelBinder.forField(operatesFromDateField)
                   .asRequired("Every hotel must operates from a certain date")
                   .withValidator(new DateRangeValidator("Date must be in the past",
                           null, LocalDate.now().minusDays(1)))
                   .withConverter(LocalDate::toEpochDay, LocalDate::ofEpochDay,
                           "Don't look like a date")
                   .bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);

        hotelBinder.forField(categoryNativeSelect)
                   .asRequired("Every hotel must have a category")
                   .withValidator(p -> {
                      return getHotelView().isCategoryNameInList(p.getName());
                   }, "Define category, please")
                   .bind(Hotel::getCategory, Hotel::setCategory);

        hotelBinder.forField(urlTextField)
                   .asRequired("Every hotel must have a link to booking.com")
                   .bind(Hotel::getUrl, Hotel::setUrl);

        hotelBinder.forField(descriptionTextArea).bind(Hotel::getDescription, Hotel::setDescription);
    }

    private void buildLayout() {
        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);     // hide form at start

        HorizontalLayout buttons = new HorizontalLayout(saveHotelBtn, closeFormBtn);
        buttons.setSpacing(true);

        this.addComponents(nameTextField, addressTextField, ratingTextField, paymentMethodField,
                operatesFromDateField, categoryNativeSelect, urlTextField,
                descriptionTextArea, buttons);
    }

    private void saveHotel() {
        // This will make all current validation errors visible
        BinderValidationStatus<Hotel> status = hotelBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size(), Notification.Type.WARNING_MESSAGE);
        }

        // save validated hotel with not empty fields (field "descriptionTextArea" - can be empty)
        if (status.isOk()) {
            // take validated data fields from binder to persisted categoryNativeSelect
            hotelBinder.writeBeanIfValid(this.hotel);               // !!!

            // try save in DB new or update persisted hotel
            boolean isSaved = false;
            Hotel savedHotel = null;
            try {
                savedHotel = hotelService.save(this.hotel);
                isSaved = savedHotel != null;
            } catch (Exception exp) {
                LOGGER.log(Level.WARNING, "Can't save hotel: " + this.hotel, exp);
            }

            if (isSaved) {
                this.setVisible(false);
                Notification.show("Saved hotel with name: " + hotel.getName(),
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show(String.format("Can't save hotel with name [%s].\n\rMaybe someone has already changed it.\n\rClose form & try again.",
                        hotel.getName()), Notification.Type.ERROR_MESSAGE);
            }
            // update All changed hotel Items in: HotelView(in Grid)
            hotelDataProvider.getDataProvider().refreshAll();
            // switch OFF selection on updated hotel
            getHotelView().getHotelGrid().deselectAll();
        }
        getHotelView().getAddHotelBtn().setEnabled(true);
    }

    public void setHotel(Hotel hotel) {
        this.setVisible(true);

        // save persisted hotel in HotelEditForm class
        this.hotel = hotel;

        // connect entity fields with form fields
        hotelBinder.readBean(hotel);

        // set active categoryNativeSelect in categoryNativeSelect items list
        categoryNativeSelect.setValue(hotel.getCategory() != null
                ? hotel.getCategory()
                : new Category(null));
    }

    private void closeHotelEditForm() {
        this.setVisible(false);
        HotelView hotelView = getHotelView();
        hotelView.getAddHotelBtn().setEnabled(true);
        hotelView.getHotelGrid().deselectAll();
    }

    private HotelView getHotelView() {
        return ((NavigationUI) UI.getCurrent()).getHotelView();
    }
}
