package views.hotelveiw;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import entities.Category;
import entities.Hotel;
import entities.components.PaymentMethod;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import services.IHotelService;
import ui.dataproviders.ICategoryDataProvider;
import ui.customcompanents.PaymentMethodField;
import ui.customcompanents.TopCenterComposite;
import ui.dataproviders.IHotelDataProvider;
import ui.rootui.NavigationUI;
import utils.GetBeenFromSpringContext;
import utils.entitydescription.IVaadinUtil;
import utils.entitydescription.vo.EntityFieldDescription;
import utils.entitydescription.IEntityUtil;
import utils.entitydescription.vo.SingleValueBeen;
import utils.entitydescription.vo.TakeFieldValueAs;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static entities.components.PaymentMethod.MAX_GUARANTY_DEPOSIT_VALUE;
import static entities.components.PaymentMethod.MIN_GUARANTY_DEPOSIT_VALUE;
import static utils.entitydescription.vo.EntityFieldDescription.SELECT_FIELD_PLACEHOLDER;
import static utils.entitydescription.vo.EntityFieldDescription.VALUE_FIELD_PLACEHOLDER;

@UIScope
@SpringComponent
public class HotelBulkUpdate {
    private static final Logger LOGGER = Logger.getLogger(HotelBulkUpdate.class.getName());

    @Autowired
    private IEntityUtil entityUtil;
    @Autowired
    private IVaadinUtil vaadinUtil;
    @Autowired
    private IHotelDataProvider hotelDataProvider;
    @Autowired
    private ICategoryDataProvider categoryDataProvider;

    private IHotelService hotelService;

    private HashMap<String, TakeFieldValueAs<?,?>> hotelFieldsBindingRules = new HashMap<>();

    private final ComboBox<Map.Entry<String, EntityFieldDescription>> entityFieldsComboBox = new ComboBox<>();

    private HasValue<?> fieldValue = new TextField();              

    private Button updateFieldBtn;
    private Button cancelBtn;

    private VerticalLayout popupContent;
    private Component buttons;
    @Getter
    private PopupView popup;

    public HotelBulkUpdate() {

        // get beans from Application context: hotelService
        this.hotelService = GetBeenFromSpringContext.getBeen(IHotelService.class);
    }

    @PostConstruct
    void init() {
        // use this method to determine view if Spring will create been
        // if we created bean with our own hands & @PostConstruct don't work

        // del after debug
        System.out.println("start -> HotelBulkUpdate.init() ");

        // set view Configuration
        configureComponents();
        configureEntityDescription();
        buildLayout();

        // del after debug
        System.out.println("STOP -> HotelBulkUpdate.init() ");
    }
    private void configureComponents() {
        // update button
        String caption = "Update";
        String description = "Save changed value in data base";
        updateFieldBtn = vaadinUtil.getStandardFriendlyButton(caption, description);
        updateFieldBtn.setEnabled(false);       // NOT possible

        updateFieldBtn.addClickListener(click -> {
            Map.Entry<String, EntityFieldDescription> fieldDescriptionEntry =
                    entityFieldsComboBox.getValue();
            if (fieldDescriptionEntry == null) {
                return;
            }
            Set<Hotel> selectedHotels = getHotelView().getHotelGrid().getSelectedItems();

            // delete after debug
//            System.out.println();
//            System.out.println("-------- ClickListener event. Start Updating hotels: ------------");
//            selectedHotels.forEach(System.out::println);

            // validate field value
            Binder binder = hotelFieldsBindingRules.get(fieldDescriptionEntry.getKey())
                                                   .getBinder();
            boolean isErrors = binder.validate()
                                     .hasErrors();
            if (isErrors) {
                // is validation errors - nothing to do !!!
                Notification.show("Invalid field value", Notification.Type.WARNING_MESSAGE);

                return;
            }
            // try updating valid field value!!!
            int count = this.bulkUpdate(selectedHotels, fieldDescriptionEntry);

            if (count == 0) {
                // no any updated
                Notification.show("Everything remained unchanged.\n\rClose form and try again.\n\rMaybe someone has already changed the selected hotels",
                        Notification.Type.ERROR_MESSAGE);
                // deselect All hotels in HotelView Grid
                ((NavigationUI) UI.getCurrent()).getHotelView().getHotelGrid().deselectAll();
            } else {
                // somethings updated
                this.popup.setPopupVisible(false);

                if (count == selectedHotels.size()) {
                    //all were updated
                    Notification.show(String.format("All hotels updated: [%d] of [%d]",
                            count, selectedHotels.size()), Notification.Type.WARNING_MESSAGE);
                } else {
                    // not all were updated
                    Notification.show(String.format("Were updated [%d] of [%d] hotels.\n\rMaybe someone has already changed the selected hotels",
                            count, selectedHotels.size()), Notification.Type.ERROR_MESSAGE);
                    // deselect All hotels in HotelView Grid
                    ((NavigationUI) UI.getCurrent()).getHotelView().getHotelGrid().deselectAll();
                }
            }
            // refresh updated Items   !!!
            hotelDataProvider.getDataProvider().refreshAll();
        });

        // button "Cancel"
        caption = "Cancel";
        description = "Close form without saving changes in data base";
        cancelBtn = vaadinUtil.getStandardPrimaryButton(caption, description);
        cancelBtn.addClickListener(click -> {
            this.popup.setPopupVisible(false);

            // clear wrong field value
            String entityFieldName = entityFieldsComboBox.getValue().getKey();
            boolean isError = hotelFieldsBindingRules.get(entityFieldName)
                                                     .getBinder().validate().hasErrors();
            if (isError) {
                this.fieldValue.setValue(null);
            }
        });

        // set entity field
        entityFieldsComboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
        entityFieldsComboBox.setEmptySelectionAllowed(false);
        entityFieldsComboBox.setPlaceholder(SELECT_FIELD_PLACEHOLDER);

        Map<String, EntityFieldDescription> hotelFields = entityUtil.getEntityDescription(Hotel.class);
        Set<Map.Entry<String, EntityFieldDescription>> hotelFieldsSet = hotelFields.entrySet();
        entityFieldsComboBox.setItems(hotelFieldsSet);
        entityFieldsComboBox.setItemCaptionGenerator(mapEntry -> mapEntry.getValue().getFieldCaption());
        entityFieldsComboBox.addValueChangeListener(event -> {
            popupContent.removeComponent((Component)fieldValue);
            popupContent.removeComponent(buttons);

            // determine for value field capturing AbstractField or SingleSelect
            String entityFieldName = event.getValue().getKey();
            fieldValue = hotelFieldsBindingRules.get(entityFieldName).getFieldValueTaker();

            // force Binder StatusChange event for updateControlBtn     !!!!!!!!!
            hotelFieldsBindingRules.get(entityFieldName).getBinder().validate().isOk();

//            // delete after debug
//            System.out.println();
//            System.out.println("*************************************");
//            System.out.println("was click valueChangeListener, fieldValue: " + fieldValue);

            popupContent.addComponent((Component)fieldValue);
            popupContent.addComponent(buttons);
            popupContent.setComponentAlignment(buttons, Alignment.TOP_CENTER);
        });

        // set entity field value - for default picture
        ((TextField) fieldValue).addStyleName(ValoTheme.TEXTFIELD_SMALL);
        ((TextField) fieldValue).setPlaceholder(VALUE_FIELD_PLACEHOLDER);
    }

    private void buildLayout() {
        // Content for the PopupView
        popupContent = new VerticalLayout();
        popupContent.setSpacing(true);
        popupContent.setMargin(true);
        popupContent.setSizeFull();         // set 100% x 100%

        Label label = new Label("Bulk update");
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        label.addStyleName(ValoTheme.LABEL_COLORED);
        popupContent.addComponent(label);
        popupContent.addComponent(entityFieldsComboBox);

        // field value catch
        popupContent.addComponent((Component) fieldValue);

        // buttons
        Component[] buttonComponents = {updateFieldBtn, cancelBtn};
        buttons = new TopCenterComposite(buttonComponents);
        popupContent.addComponent(buttons);
        popupContent.setComponentAlignment(buttons, Alignment.TOP_CENTER);

        // The popup component itself
        popup = new PopupView(null, popupContent);  // small=null -> invisible
        popup.setHideOnMouseOut(false);
    }

    // base functionality !!!
    private int bulkUpdate(Set<Hotel> selectedHotels,
                           Map.Entry<String, EntityFieldDescription> fieldDescriptionEntry) {
        //delete after debug
        System.out.println("start -> HotelBulkUpdate.bulkUpdate");

        String fieldName = fieldDescriptionEntry.getKey();
        EntityFieldDescription entityFieldDescription = fieldDescriptionEntry.getValue();

        //delete after debug
        System.out.println("fieldName: " + fieldName);

        // validate field value
        Binder binder = hotelFieldsBindingRules.get(fieldDescriptionEntry.getKey())
                                               .getBinder();

        // delete after debug
        System.out.println("binder: " + binder);

        boolean isOk = binder.validate().isOk();           //  !!!!!------!!!!!-----!!!!!
        int count = 0;
        if ( isOk ) {
            // validation passed - can update
            // take validated data field from binder to SingleValueBeen
            SingleValueBeen singleValueBeen = new SingleValueBeen(null);
            binder.writeBeanIfValid(singleValueBeen);

            //delete after debug
            System.out.println("singleValueBeen: " + singleValueBeen);

            Method setMethod = entityFieldDescription.getMethodSet();
            boolean isUpdated = false;
            Hotel updatedHotel = null;
            for (Hotel hotel : selectedHotels) {
                try {
                    setMethod.invoke(hotel,
                            entityFieldDescription.getFieldClass().cast(singleValueBeen.getValue()));

                    // delete after debug
                    System.out.println("hotel after invoke: " + hotel);

                    updatedHotel = hotelService.bulkUpdate(hotel);
                    isUpdated = updatedHotel != null;

                    // delete after debug
                    System.out.println("isUpdated: " + isUpdated);

                    if (isUpdated) {
                        count++;
                    }
                } catch (Exception exp) {
                    LOGGER.log(Level.WARNING, "Can't update hotel: " + hotel, exp);
                }
            }
        }

        //delete after debug
        System.out.println("STOP -> HotelBulkUpdate.bulkUpdate");

        return count;
    }

    private void configureEntityDescription() {
        // configure Entity Hotel
        // configure "name" field
        String description = "Name of the hotel";           // Required field
        TextField nameTextField = vaadinUtil.getStandardTextField(null,
                description, true);

        Binder<SingleValueBeen<String>> binderName = new Binder<>();
        binderName.forField(nameTextField)
                      .asRequired("Every hotel must have name")
                      .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<String,String> nameTakeFieldValueAs = new TakeFieldValueAs<>(String.class, binderName
                , String.class, nameTextField);
        // save "name" configuration
        hotelFieldsBindingRules.put("name", nameTakeFieldValueAs);

        // configure "address" field
        description = "Address of the hotel";           // Required field
        TextField addressTextField = vaadinUtil.getStandardTextField(null,
                description, true);

        Binder<SingleValueBeen<String>> binderAddress = new Binder<>();
        binderAddress.forField(addressTextField)
                  .asRequired("Every hotel must have address")
                  .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<String,String> addressTakeFieldValueAs = new TakeFieldValueAs<>(String.class, binderAddress,
                String.class, addressTextField);
        // save "address" configuration
        hotelFieldsBindingRules.put("address", addressTakeFieldValueAs);

        // configure "rating" field
        description = String.format("Rating of the hotel must be in [%d;%d]",
                Hotel.MIN_RATING, Hotel.MAX_RATING);           // Required field
        TextField ratingTextField = vaadinUtil.getStandardTextField(null,
                description, true);

        Binder<SingleValueBeen<Integer>> binderRating = new Binder<>();
        binderRating.forField(ratingTextField)
                .asRequired("Every hotel must have a rating")
                .withConverter(new StringToIntegerConverter("Enter an integer, please"))
                .withValidator(rating -> rating >= Hotel.MIN_RATING && rating <= Hotel.MAX_RATING,
                        description)
                .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<String,Integer>  ratingTakeFieldValueAs =
                new TakeFieldValueAs<>(Integer.class, binderRating, String.class, ratingTextField);
        // save "rating" configuration
        hotelFieldsBindingRules.put("rating", ratingTakeFieldValueAs);

        // configure "operatesFrom" field
        description = "Opening day of the hotel";           // Required field
        DateField operatesFromDateField = vaadinUtil.getStandardDateField(null,
                description, true);
        // set default value as yesterday
        operatesFromDateField.setValue(LocalDate.now().minusDays(1));
        operatesFromDateField.setPlaceholder(VALUE_FIELD_PLACEHOLDER);

        Binder<SingleValueBeen<Long>> binderOperatesFrom = new Binder<>();
        binderOperatesFrom.forField(operatesFromDateField)
                          .asRequired("Every hotel must operates from a certain date")
                          .withValidator(new DateRangeValidator("Date must be in the past",
                                  null, LocalDate.now().minusDays(1)))
                          .withConverter(LocalDate::toEpochDay, LocalDate::ofEpochDay,
                                  "Don't look like a date")
                          .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<LocalDate,Long> operatesFromTakeFieldValueAs =
                new TakeFieldValueAs<>(Long.class, binderOperatesFrom, LocalDate.class,
                        operatesFromDateField);
        // save "operatesFrom" configuration
        hotelFieldsBindingRules.put("operatesFrom", operatesFromTakeFieldValueAs);

        // configure "category" field
        description = "Chose definite hotel category from the list";       // Required field
        NativeSelect<Category> categoryNativeSelect = new NativeSelect<>();
        // DataProvider set Category items & look for their updating        !!!
        categoryNativeSelect.setDataProvider(categoryDataProvider.getDataProvider());
        categoryNativeSelect.setDescription(description);
        categoryNativeSelect.setEmptySelectionAllowed(true);           // YES emptySelect !!!
        categoryNativeSelect.setRequiredIndicatorVisible(true);         // Required field
        categoryNativeSelect.setItemCaptionGenerator(Category::getName);

        Binder<SingleValueBeen<Category>> binderCategory = new Binder<>();

        binderCategory.forField(categoryNativeSelect)
                .asRequired("Every hotel must have a category")
                .withValidator(category ->
                                getHotelView().isCategoryNameInList(category == null
                                        ? ""
                                        : category.getName()),
                        "Define category, please.\n\rMaybe someone has already deleted this.\n\rPress Cancel to refresh list")
                .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<Category,Category> categoryTakeFieldValueAs =
                new TakeFieldValueAs<>(Category.class, binderCategory,
                Category.class, categoryNativeSelect);
        // save "category" configuration
        hotelFieldsBindingRules.put("category", categoryTakeFieldValueAs);

        // configure "paymentMethod" field
        description = "Payment method for hotel accommodation"; // Required field, Required indicator OFF
        PaymentMethodField paymentMethodField = new PaymentMethodField(null);
        paymentMethodField.setDescription(description);
        paymentMethodField.addValueChangeListener(event -> {
            Notification.show(String.format("Payment Method changed.\n\rold  value: %s\n\rnew value: %s",
                    event.getOldValue(), event.getValue()), Notification.Type.HUMANIZED_MESSAGE);
        });

        Binder<SingleValueBeen<PaymentMethod>> binderPaymentMethod = new Binder<>();
        binderPaymentMethod.forField(paymentMethodField)
                           .withValidator(p -> p != null && (p.getPaymentType() != null),
                                   "Every hotel has a way of payment for accommodation")
                           .withValidator(p -> p.getGuarantyDepositValue() >= MIN_GUARANTY_DEPOSIT_VALUE &&
                                           p.getGuarantyDepositValue() <= MAX_GUARANTY_DEPOSIT_VALUE,
                                   String.format("Guaranty deposit value must be integer in [%d;%d]",
                                           MIN_GUARANTY_DEPOSIT_VALUE, MAX_GUARANTY_DEPOSIT_VALUE))
                           .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<PaymentMethod,PaymentMethod> paymentMethodTakeFieldValueAs =
                new TakeFieldValueAs<>(PaymentMethod.class, binderPaymentMethod,
                        PaymentMethod.class, paymentMethodField);
        // save "paymentMethod" configuration
        hotelFieldsBindingRules.put("paymentMethod", paymentMethodTakeFieldValueAs);

        // configure "url" field
        description = "Link to the booking.com";       // Required field
        TextField urlTextField = vaadinUtil.getStandardTextField(null,
                description, true);
        Binder<SingleValueBeen<String>> binderURL = new Binder<>();
        binderURL.readBean(new SingleValueBeen<>(null));    // new been with [null] value by default
        binderURL.forField(urlTextField)
                 .asRequired("Every hotel must have a link to booking.com")
                 .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<String,String> urlTakeFieldValueAs =
                new TakeFieldValueAs<>(String.class, binderURL, String.class, urlTextField);
        // save "url" configuration
        hotelFieldsBindingRules.put("url", urlTakeFieldValueAs);

        // configure "description" field
        description = "Description of the hotel";       // NOT Required field
        TextArea descriptionTextArea = vaadinUtil.getStandardTextArea(null,
                description, false);

        Binder<SingleValueBeen<String>> binderDescription = new Binder<>();
        binderDescription.forField(descriptionTextArea)
                         .bind(SingleValueBeen::getValue, SingleValueBeen::setValue);

        TakeFieldValueAs<String,String> descriptionTakeFieldValueAs =
                new TakeFieldValueAs<>(String.class, binderDescription, String.class,
                        descriptionTextArea);
        // save "description" configuration
        hotelFieldsBindingRules.put("description", descriptionTakeFieldValueAs);

        // put identical configurations: placeholder, saveBtn
        hotelFieldsBindingRules.forEach((key, value) -> {
            if (value.getFieldValueTaker() instanceof TextField ||
                    value.getFieldValueTaker() instanceof TextArea) {
                // TextField or TextArea
                AbstractTextField textField = (AbstractTextField) value.getFieldValueTaker();
                textField.setPlaceholder(VALUE_FIELD_PLACEHOLDER);
            }
            // let work saveBtn only when form valid
            vaadinUtil.setBulkUpdateBtnControl(value.getBinder(), updateFieldBtn);
        });
    }

    private HotelView getHotelView() {
        return ((NavigationUI) UI.getCurrent()).getHotelView();
    }
}
