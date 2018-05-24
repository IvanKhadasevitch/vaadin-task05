package views.hotelveiw;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import entities.Category;
import entities.Hotel;
import entities.components.PaymentMethod;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import services.ICategoryService;
import services.IHotelService;
import ui.dataproviders.IHotelDataProvider;
import ui.rootui.MainViewDisplay;
import ui.rootui.NavigationUI;
import ui.customcompanents.FilterWithClearBtn;
import ui.customcompanents.TopCenterComposite;
import ui.datafilters.HotelFilter;
import utils.GetBeenFromSpringContext;
import utils.entitydescription.IVaadinUtil;
import utils.entitydescription.vo.EntityFieldDescription;
import utils.entitydescription.IEntityUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@UIScope
@SpringView(name = HotelView.VIEW_NAME)
public class HotelView extends VerticalLayout implements View {
    private static final Logger LOGGER = Logger.getLogger(HotelView.class.getName());

    public static final String VIEW_NAME = "hotel";
    private static final String TITLE_NAME = "hotels";

    @Autowired
    private MainViewDisplay mainViewDisplay;
    @Autowired
    private IEntityUtil entityUtil;
    @Autowired
    private IVaadinUtil vaadinComponentUtil;
    @Autowired
    private IHotelDataProvider hotelDataProvider;
    @Autowired
    private HotelEditForm hotelEditForm;
    @Autowired
    private HotelBulkUpdate hotelBulkUpdate;

    private final HotelFilter hotelFilter = new HotelFilter(null, null);

    private IHotelService hotelService;
    private ICategoryService categoryService;

    private FilterWithClearBtn filterByName;
    private FilterWithClearBtn filterByAddress;
    @Getter
    private Button addHotelBtn;
    private Button deleteHotelBtn;
    private Button editHotelBtn;
    private Button bulkUpdateBtn;

    @Getter
    private final Grid<Hotel> hotelGrid = new Grid<>();

    public HotelView() {
        super();

        // del after debug
        System.out.println("start -> HotelView.CONSTRUCTOR ");

        // take beans ICategoryService & IHotelService
        this.hotelService = GetBeenFromSpringContext.getBeen(IHotelService.class);
        this.categoryService = GetBeenFromSpringContext.getBeen(ICategoryService.class);

        // del after debug
        System.out.println("getBeen(IHotelService.class): " + hotelService);
        System.out.println("STOP -> HotelView.CONSTRUCTOR ");
    }

    @PostConstruct
    void init() {
        // use this method to determine view if Spring will create been
        // if we created bean with our own hands & @PostConstruct don't work

        // del after debug
        System.out.println("start -> HotelView.init() ");

        // set view Configuration
        configureHotelEntity();
        configureComponents();
        buildLayout();

        // del after debug
        System.out.println("STOP -> HotelView.init() ");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()

        // del after debug
        System.out.println("start -> HotelView.enter() ");

        // set page URI in browser history
        NavigationUI.startPage.pushState(VIEW_NAME);

        // set Page Title
        Page.getCurrent().setTitle(NavigationUI.MAIN_TITLE + " " + TITLE_NAME);

        mainViewDisplay.showView(this);

        // del after debug
        System.out.println("STOP -> HotelView.enter()");
    }

    private void configureComponents() {
        // Hotel DataProvider: DataProvider<Hotel, HotelFilter>
        // Hotel DataProvider for Grid: ConfigurableFilterDataProvider<Hotel, Void, HotelFilter>
        ConfigurableFilterDataProvider<Hotel, Void, HotelFilter> hotelDataProviderWithFilter =
                hotelDataProvider.getDataProvider().withConfigurableFilter();

        // filter fields with clear button
        filterByName = new FilterWithClearBtn("Filter by name...",
                event -> {
                    String nameFilter = event.getValue();
                    if (nameFilter != null && nameFilter.trim().isEmpty()) {
                        // null disables filtering
                        nameFilter = null;
                    }
                    this.hotelFilter.setNameFilter(nameFilter);
                    // set filter to Hotel DataProvider
                    hotelDataProviderWithFilter.setFilter(this.hotelFilter);
                });
        filterByName.getFilterField().setId("filterByNameTextFieldHotelView_id");   // for testing
        filterByAddress = new FilterWithClearBtn("Filter by address...",
                event -> {
                    String addressFilter = event.getValue();
                    if (addressFilter != null && addressFilter.trim().isEmpty()) {
                        // null disables filtering
                        addressFilter = null;
                    }
                    this.hotelFilter.setAddressFilter(addressFilter);
                    // set filter to Hotel DataProvider
                    hotelDataProviderWithFilter.setFilter(this.hotelFilter);
                });

        // add Hotel Button
        String caption = "Add hotel";
        String description = "Open form to enter date and\n\rsave new hotel in data base";
        addHotelBtn = vaadinComponentUtil.getStandardPrimaryButton(caption, description);
        addHotelBtn.setId("addHotelBtnHotelView_id");           // for testing
        addHotelBtn.addClickListener(e -> {
            addHotelBtn.setEnabled(false);
            hotelEditForm.setHotel(new Hotel());
        });

        // delete Hotel Button
        caption = "Delete hotel";
        description = "Delete all selected hotels from data base.";
        deleteHotelBtn = vaadinComponentUtil.getStandardDangerButton(caption, description);
        deleteHotelBtn.setEnabled(false);
        deleteHotelBtn.addClickListener(e -> {
            int deletedHotelsCount = deleteSelectedHotels(hotelGrid.getSelectedItems());

            deleteHotelBtn.setEnabled(false);
            editHotelBtn.setEnabled(false);
            addHotelBtn.setEnabled(true);
            if (deletedHotelsCount > 0) {
                // if any was deleted - update HotelItems in: HotelView (in Grid)
                hotelDataProvider.getDataProvider().refreshAll();
            }
            Notification.show(String.format("Were deleted [%d] hotels.", deletedHotelsCount),
                    Notification.Type.WARNING_MESSAGE);
        });

        // edit Hotel Button (can edit only if one hotel was chosen)
        caption = "Edit hotel";
        description = "Open form to edit selected hotel\n\rand save it in the data base.";
        editHotelBtn = vaadinComponentUtil.getStandardPrimaryButton(caption, description);
        editHotelBtn.setEnabled(false);
        editHotelBtn.addClickListener(e -> {
            addHotelBtn.setEnabled(true);       // switch on addNewHotel possibility
            Hotel editCandidate = hotelGrid.getSelectedItems().iterator().next();
            hotelEditForm.setHotel(editCandidate);
        });

        // bulk update Button
        caption = "Bulk update";
        description = "Opens a form for changing and saving\n\rin the DB the values of all the selected\n\relements of the column to the same value";
        bulkUpdateBtn = vaadinComponentUtil.getStandardPrimaryButton(caption, description);
        bulkUpdateBtn.setEnabled(false);
        bulkUpdateBtn.addClickListener(e -> {
            bulkUpdateBtn.setEnabled(true);
            hotelBulkUpdate.getPopup().setPopupVisible(true);
        });

        // Hotel list (Grid)
        // set DataProvider to the Grid  - naw DataProvider response for refreshing Grid items !!!
        hotelGrid.setDataProvider(hotelDataProviderWithFilter);

        hotelGrid.addColumn(Hotel::getName)
                 .setCaption(getFieldCaption("name"));    // setCaption("Name")
        hotelGrid.setFrozenColumnCount(1);                        // froze "name" column
        hotelGrid.addColumn(Hotel::getAddress)
                 .setCaption(getFieldCaption("address"))  // setCaption("Address")
                 .setHidable(true);
        hotelGrid.addColumn(Hotel::getRating)
                 .setCaption(getFieldCaption("rating"))     // setCaption("Rating")
                 .setHidable(true);

        hotelGrid.addColumn(hotel -> hotel.getPaymentMethod() == null
                ? PaymentMethod.NULL_PAYMENT_METHOD_REPRESENTATION
                : hotel.getPaymentMethod().getPaymentType().getPaymentTypeName()
        )
                 .setCaption("Payment type")
                 .setHidable(true);
        hotelGrid.addColumn(hotel -> hotel.getPaymentMethod() == null
                ? PaymentMethod.NULL_PAYMENT_METHOD_REPRESENTATION
                : hotel.getPaymentMethod().getGuarantyDepositValue().toString() + '%'
        )
                 .setCaption("Guaranty Deposit")
                 .setHidable(true);

        hotelGrid.addColumn(hotel -> LocalDate.ofEpochDay(hotel.getOperatesFrom()))
                 .setCaption(getFieldCaption("operatesFrom"))   // setCaption("Operates from")
                 .setHidable(true);
        ;
        hotelGrid.addColumn(hotel -> {
            String categoryName = hotel.getCategory() != null
                    ? hotel.getCategory().getName()
                    : "";
            return this.existWithName(categoryName);
        })
                 .setCaption(getFieldCaption("category"))   // setCaption("Category")
                 .setHidable(true);

        hotelGrid.addColumn(hotel ->
                        "<a href='" + hotel.getUrl() + "' target='_blank'>more info</a>",
                new HtmlRenderer()
        )
                 .setCaption(getFieldCaption("url"))        // setCaption("URL")
                 .setHidable(true);

        hotelGrid.addColumn(Hotel::getDescription)
                 .setCaption(getFieldCaption("description"))    // setCaption("Description")
                 .setHidable(true);

        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);      // MULTI select possible !!!
        // delete and edit selected Hotel
        hotelGrid.addSelectionListener(e -> {
            // when Hotel is chosen - can delete or edit
            Set<Hotel> selectedHotels = e.getAllSelectedItems();
            if (selectedHotels != null && selectedHotels.size() == 1) {
                // chosen only one hotel - can add & delete & edit, can't bulkUpdate
                addHotelBtn.setEnabled(true);
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(true);
                bulkUpdateBtn.setEnabled(false);
            } else if (selectedHotels != null && selectedHotels.size() > 1) {
                // chosen more then one hotel - can delete & add & bulk update
                hotelEditForm.setVisible(false);
                addHotelBtn.setEnabled(true);
                deleteHotelBtn.setEnabled(true);
                bulkUpdateBtn.setEnabled(true);
                editHotelBtn.setEnabled(false);
            } else {
                // no any hotel chosen - can't delete & edit & bulkUpdate
                deleteHotelBtn.setEnabled(false);
                editHotelBtn.setEnabled(false);
                bulkUpdateBtn.setEnabled(false);
                hotelEditForm.setVisible(false);
            }
        });
    }

    private void buildLayout() {
        // tools bar - filters & buttons
        HorizontalLayout control = new HorizontalLayout(filterByName, filterByAddress,
                addHotelBtn, deleteHotelBtn, editHotelBtn, bulkUpdateBtn);

        control.setMargin(false);
        control.setWidth("100%");
        // divide free space between filterByName (50%) & filterByAddress (50%)
        control.setExpandRatio(filterByName, 1);
        control.setExpandRatio(filterByAddress, 1);

        // bulk update component
        Component[] bulkUpdateComponents = {hotelBulkUpdate.getPopup()};
        Component bulkUpdateComposite = new TopCenterComposite(bulkUpdateComponents);

        // content - HotelList & hotelEditForm
        HorizontalLayout hotelContent = new HorizontalLayout(hotelGrid, hotelEditForm);
        hotelGrid.setSizeFull();            // size 100% x 100%
        hotelGrid.addStyleName(ValoTheme.TABLE_SMALL);
        hotelEditForm.setSizeFull();
        hotelContent.setMargin(false);
        hotelContent.setWidth("100%");

        hotelContent.setHeight(40, Unit.REM);

        hotelContent.setExpandRatio(hotelGrid, 180);
        hotelContent.setExpandRatio(hotelEditForm, 140);

        // Compound view parts and allow resizing
        this.addComponents(control, hotelContent, bulkUpdateComposite);
        this.setComponentAlignment(bulkUpdateComposite, Alignment.TOP_CENTER);

        this.setSpacing(true);
        this.setMargin(false);
        this.setWidth("100%");
    }

    public boolean isCategoryNameInList(String categoryName) {
        boolean result = false;
        try {
            result = categoryService.existWithName(categoryName);
        } catch (Exception exp) {
            LOGGER.log(Level.WARNING, "Can't take from DB category with name: " + categoryName, exp);
        }

        return result;
    }

    private String existWithName(String categoryName) {
        try {
            return categoryService.existWithName(categoryName)
                    ? categoryName
                    : Category.NULL_CATEGORY_REPRESENTATION;
        } catch (Exception exp) {
            LOGGER.log(Level.WARNING, "Can't take from DB category by name: " + categoryName, exp);

            return Category.NULL_CATEGORY_REPRESENTATION;
        }
    }

    private int deleteSelectedHotels(Set<Hotel> selectedHotels) {
        int deleteCount = 0;
        Hotel hotelForeDelete = null;
        try {
            for (Hotel hotel : selectedHotels) {
                hotelForeDelete = hotel;
                hotelService.delete(hotel.getId());
                deleteCount++;
                hotelGrid.deselect(hotel);
            }
        } catch (Exception exp) {
            LOGGER.log(Level.WARNING, "Can't delete from DB hotel: " + hotelForeDelete, exp);
            String ERROR_NOTIFICATION = "Can't connect to data base.\n\rTry again or refer to administrator";
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }

        return deleteCount;
    }

    private void configureHotelEntity() {
        Map<String, EntityFieldDescription> hotelEntityDescription = entityUtil.getEntityDescription(Hotel.class);

        // configure "operatesFrom" field caption
        hotelEntityDescription.get("operatesFrom").setFieldCaption("Operates from");

        // configure "url" field caption
        hotelEntityDescription.get("url").setFieldCaption("URL");

        // configure "paymentMethod" field caption
        hotelEntityDescription.get("paymentMethod").setFieldCaption("Payment Method");
    }

    private String getFieldCaption(String fieldName) {
        Map<String, EntityFieldDescription> hotelEntityDescription = entityUtil.getEntityDescription(Hotel.class);
        EntityFieldDescription entityFieldDescription = hotelEntityDescription.get(fieldName);
        return entityFieldDescription.getFieldCaption();
    }
}
