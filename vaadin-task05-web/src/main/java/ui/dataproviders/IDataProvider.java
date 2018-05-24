package ui.dataproviders;

import com.vaadin.data.provider.DataProvider;

public interface IDataProvider<T,F> {

    DataProvider<T,F> getDataProvider();
}
