package utils.entitydescription;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public interface IVaadinUtil {
    TextField getStandardTextField(String caption, String description, Boolean required);
    DateField getStandardDateField(String caption, String description, Boolean required);
    TextArea getStandardTextArea(String caption, String description, Boolean required);

    Button getStandardPrimaryButton(String caption, String description);
    Button getStandardFriendlyButton(String caption, String description);
    Button getStandardDangerButton(String caption, String description);

    <T> void setBulkUpdateBtnControl(Binder<T> binder, Button controlButton);
    <T> void setFormSaveBtnControl(Binder<T> binder, Button controlButton);

}
