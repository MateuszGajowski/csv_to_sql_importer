package pl.gajowski.mateusz.csvimporter.common.batch.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import pl.gajowski.mateusz.csvimporter.common.editor.SafeLocalDateEditor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class BeanWrapperFieldSetMapperCustom<T> extends BeanWrapperFieldSetMapper<T> {

    private final String dateFormat;

    @Override
    protected void initBinder(DataBinder binder) {
        SafeLocalDateEditor.register(binder, dateFormat == null ? "yyyy-MM-dd" : dateFormat);
    }
}