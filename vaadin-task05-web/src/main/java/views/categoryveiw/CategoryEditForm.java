package views.categoryveiw;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import services.ICategoryService;
import ui.dataproviders.ICategoryDataProvider;
import ui.dataproviders.IHotelDataProvider;
import ui.rootui.NavigationUI;
import utils.GetBeenFromSpringContext;
import utils.entitydescription.IVaadinUtil;
import views.hotelveiw.HotelView;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

@UIScope
@SpringComponent
public class CategoryEditForm extends FormLayout {
    private static final Logger LOGGER = Logger.getLogger(CategoryEditForm.class.getName());

    @Autowired
    private IVaadinUtil vaadinUtil;
    @Autowired
    private ICategoryDataProvider categoryDataProvider;
    @Autowired
    private IHotelDataProvider hotelDataProvider;

    private ICategoryService categoryService;

    private Category category;
    private final Binder<Category> categoryBinder = new Binder<>(Category.class);

    private TextField nameTextField;

    private Button saveCategoryBtn;
    private Button closeFormBtn;

    public CategoryEditForm(){

        // get beans with names: categoryService,
        this.categoryService = GetBeenFromSpringContext.getBeen(ICategoryService.class);

    }

    @PostConstruct
    void init() {
        // use this method to determine view if Spring will create been
        // if we created bean with our own hands & @PostConstruct don't work

        // del after debug
        System.out.println("start -> CategoryEditForm.init() ");

        // set view Configuration
        configureComponents();
        buildLayout();

        // del after debug
        System.out.println("STOP -> CategoryEditForm.init() ");
    }

    private void configureComponents() {
        String caption = "Category name:";
        String description = "Hotels category name";                // Required field
        nameTextField = vaadinUtil.getStandardTextField(caption, description, true);
        nameTextField.setId("nameTextFieldCategoryEditForm_id");    // id for testing

        // buttons
        caption = "Save";
        description = "Save data in data base";;
        saveCategoryBtn = vaadinUtil.getStandardFriendlyButton(caption, description);
        saveCategoryBtn.setId("saveCategoryBtnCategoryEditForm_id");    // id for testing
        saveCategoryBtn.addClickListener(e -> saveCategory());

        caption = "Close";
        description = "Close without saving changes.";
        closeFormBtn = vaadinUtil.getStandardPrimaryButton(caption, description);
        closeFormBtn.addClickListener(e -> closeCategoryEditForm());

        // let work saveCategoryBtn only when form valid
        vaadinUtil.setFormSaveBtnControl(categoryBinder, saveCategoryBtn);

        // connect entity fields with form fields
        categoryBinder.forField(nameTextField)
                      .asRequired("Every category must have name")
                      .withValidator(categoryName ->
                                      ! getHotelView().isCategoryNameInList(categoryName),
                              "Such category name already in use.")
                      .bind(Category::getName, Category::setName);
    }

    private void buildLayout() {
        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);

        // form tools - buttons
        HorizontalLayout buttons = new HorizontalLayout(saveCategoryBtn, closeFormBtn);
        buttons.setSpacing(true);

        // collect form components - form fields & buttons
        this.addComponents(nameTextField, buttons);
    }

    private void saveCategory() {
        // This will make all current validation errors visible
        BinderValidationStatus<Category> status = categoryBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size(), Notification.Type.WARNING_MESSAGE);
        }

        // save validated Category with not empty fields
        if ( status.isOk() ) {
            // take validated data fields from binder to persisted category
            categoryBinder.writeBeanIfValid(this.category);

            // try save in DB new or update persisted category
            boolean isSaved = false;
            Category savedCategory = null;
            try {
                savedCategory = categoryService.save(this.category);
                isSaved = savedCategory != null;

            } catch (Exception exp) {
                LOGGER.log(Level.WARNING, "Can't save category: " + this.category, exp);
            }

            if (isSaved) {
                this.setVisible(false);
                Notification.show("Saved category with name: " + this.category.getName(),
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show( String.format("Can't save category with name [%s].\n\rMaybe someone has already changed it.\n\rClose form & try again ",
                        this.category.getName()), Notification.Type.ERROR_MESSAGE);
            }
            // update All or changed category Items in:
            // CategoryView(in Grid), HotelEditForm (in NativeSelect),
            // HotelBulkUpdate (in NativeSelect)
            categoryDataProvider.getDataProvider().refreshAll();
            // switch OFF selection on updated category
            getCategoryView().getCategoryGrid().deselectAll();

            // refresh Hotels in HotelViewGrid
            hotelDataProvider.getDataProvider().refreshAll();
        }

        getCategoryView().getAddCategoryBtn().setEnabled(true);
    }

    public void setCategory(Category category) {
        this.setVisible(true);

        // save persisted category in CategoryEditForm class
        this.category = category;

        // connect entity fields with form fields
        categoryBinder.readBean(category);
    }

    private void closeCategoryEditForm() {
        this.setVisible(false);
        CategoryView categoryView = getCategoryView();
        categoryView.getAddCategoryBtn().setEnabled(true);
        categoryView.getCategoryGrid().deselectAll();
    }

    private CategoryView getCategoryView() {
        return ((NavigationUI) UI.getCurrent()).getCategoryView();
    }

    private HotelView getHotelView() {
        return ((NavigationUI) UI.getCurrent()).getHotelView();
    }
}
