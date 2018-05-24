package utils.entitydescription.vo;


import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import lombok.Getter;

@Getter
public class TakeFieldValueAs<VALUE, TYPE> {

    private Class<TYPE> entityFieldClass;
    private Binder<SingleValueBeen<TYPE>> binder;
    private Class<VALUE> fieldValueClass;
    private HasValue<VALUE> fieldValueTaker;

    public TakeFieldValueAs(Class<TYPE> entityFieldClass, Binder<SingleValueBeen<TYPE>> binder,
                            Class<VALUE> fieldValueClass, HasValue<VALUE> fieldValueTaker) {
        this.entityFieldClass = entityFieldClass;
        this.binder = binder;
        this.fieldValueClass = fieldValueClass;
        this.fieldValueTaker = fieldValueTaker;
    }
}
