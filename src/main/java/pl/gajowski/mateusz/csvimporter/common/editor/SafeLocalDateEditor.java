package pl.gajowski.mateusz.csvimporter.common.editor;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

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

    public static Map<Class<?>, PropertyEditor> create(String dateFormat) {
        return Collections.singletonMap(LocalDate.class, new SafeLocalDateEditor(dateFormat));
    }
}
