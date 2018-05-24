package ui.dataproviders.impl;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.Notification;
import entities.Hotel;
import org.springframework.stereotype.Component;
import services.IHotelService;
import ui.datafilters.HotelFilter;
import ui.dataproviders.IHotelDataProvider;
import utils.GetBeenFromSpringContext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component                   // spring component - singleton by default
public class HotelDataProvider implements IHotelDataProvider {
    private static final Logger LOGGER = Logger.getLogger(HotelDataProvider.class.getName());

    private IHotelService hotelService;

    private volatile DataProvider<Hotel, HotelFilter> dataProvider = null;

    public HotelDataProvider() {
        // del after debug
        System.out.println("start -> HotelDataProvider.CONSTRUCTOR ");

        // del after debug
        System.out.println("getBeen(IHotelService.class): " + hotelService);
        System.out.println("STOP -> CategoryDataProvider.CONSTRUCTOR ");
    }

    @Override
    public DataProvider<Hotel, HotelFilter> getDataProvider() {
        DataProvider<Hotel, HotelFilter> instance = this.dataProvider;
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

    private DataProvider<Hotel, HotelFilter> createDataProvider() {
        DataProvider<Hotel, HotelFilter> dataProvider = null;
        hotelService = hotelService == null
                // take hotelService been
                ? GetBeenFromSpringContext.getBeen(IHotelService.class)
                : hotelService;

        if (hotelService != null) {
            dataProvider = new CallbackDataProvider<Hotel, HotelFilter>(
                    (CallbackDataProvider.FetchCallback<Hotel, HotelFilter>) query -> {
                        // getFilter returns Optional<HotelFilter>
                        HotelFilter hotelFilter = query.getFilter().orElse(null);

                        return getAllHotelsByFilter(hotelFilter).stream();
                    },
                    (CallbackDataProvider.CountCallback<Hotel, HotelFilter>) query -> {
                        // getFilter returns Optional<HotelFilter>
                        HotelFilter HotelFilter = query.getFilter().orElse(null);

                        return getAllHotelsByFilter(HotelFilter).size();
                    }, Hotel::getId);
        }

        return dataProvider;
    }

    private List<Hotel> getAllHotelsByFilter(HotelFilter hotelFilter) {
        List<Hotel> hotelList = new ArrayList<>();
        try {
            hotelList = hotelService.getAllByFilter(hotelFilter == null
                            ? null
                            : hotelFilter.getNameFilter(),
                    hotelFilter == null
                            ? null
                            : hotelFilter.getAddressFilter());
        } catch (Exception exp) {
            LOGGER.log(Level.WARNING,
                    String.format("Can't take from DB all hotels by filters: name contains [%s] & address contains [%s]",
                            hotelFilter == null ? "" : hotelFilter.getNameFilter(),
                            hotelFilter == null ? "" : hotelFilter.getAddressFilter()), exp);
            String ERROR_NOTIFICATION = "Can't connect to data base. Try again or refer to administrator";
            Notification.show(ERROR_NOTIFICATION, Notification.Type.ERROR_MESSAGE);
        }

        return hotelList;
    }
}
