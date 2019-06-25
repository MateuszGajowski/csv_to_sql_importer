package pl.gajowski.mateusz.csvimporter.common.editor;

import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

import javax.xml.bind.DataBindingException;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SafeLocalDateEditor extends PropertyEditorSupport {

    private final DateTimeFormatter formatter;

    public SafeLocalDateEditor(String dateFormat) {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.isEmpty(text)) {
            setValue(LocalDate.parse(text, formatter));
        } else {
            setValue(null);
        }
    }

    @Override
    public String getAsText() throws IllegalArgumentException {
        Object date = getValue();
        if (date != null) {
            return formatter.format((LocalDate) date);
        } else {
            return "";
        }
    }

    public static void register(DataBinder binder, String dateFormat) {
        binder.registerCustomEditor(LocalDate.class, new SafeLocalDateEditor(dateFormat));
    }

}
