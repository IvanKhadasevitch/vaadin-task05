package ui.dataproviders.impl;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.Notification;
import entities.Category;
import org.springframework.stereotype.Component;
import services.ICategoryService;
import ui.dataproviders.ICategoryDataProvider;
import utils.GetBeenFromSpringContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component                   // spring component - singleton by default
public class CategoryDataProvider implements ICategoryDataProvider {
    private static final Logger LOGGER = Logger.getLogger(CategoryDataProvider.class.getName());

    private ICategoryService categoryService;

    private volatile DataProvider<Category, String> dataProvider = null;

    public CategoryDataProvider() {
        // del after debug
        System.out.println("start -> CategoryDataProvider.CONSTRUCTOR ");

        // del after debug
        System.out.println("getBeen(CategoryService.class): " + categoryService);
        System.out.println("STOP -> CategoryDataProvider.CONSTRUCTOR ");
    }

    @Override
    public DataProvider<Category, String> getDataProvider() {
        DataProvider<Category, String> instance = this.dataProvider;
        if (instance == null) {
            synchronized (CategoryDataProvider.class) {
                instance = this.dataProvider;
                if (instance == null) {
                    this.dataProvider = instance = this.createDataProvider();
                }
            }
        }

        return instance;
    }

    private DataProvider<Category, String> createDataProvider() {
        DataProvider<Category, String> dataProvider = null;
        categoryService = categoryService == null
                // take categoryService been
                ? GetBeenFromSpringContext.getBeen(ICategoryService.class)
                : categoryService;

        if (categoryService != null) {
            dataProvider = new CallbackDataProvider<Category, String>(
                    (CallbackDataProvider.FetchCallback<Category, String>) query -> {
                        // getFilter returns Optional<String>
                        String filter = query.getFilter().orElse(null);

                        return getAllCategoriesByFilter(filter).stream();
                    },
                    (CallbackDataProvider.CountCallback<Category, String>) query -> {
                        // getFilter returns Optional<String>
                        String filter = query.getFilter().orElse(null);

                        return getAllCategoriesByFilter(filter).size();
                    }, Category::getId);
        }

        return dataProvider;
    }

    private List<Category> getAllCategoriesByFilter(String nameFilter) {
        List<Category> categoryList = new ArrayList<>();
        try {
            categoryList = categoryService.getAllByFilter(nameFilter);
        } catch (Exception exp) {
            LOGGER.log(Level.WARNING, "Can't take all categories from DB by filter: filterByName.getValue()", exp);
            String ERROR_NOTIFICATION = "Can't connect to data base. Try again or refer to administrator";
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }

        return categoryList;
    }

}
