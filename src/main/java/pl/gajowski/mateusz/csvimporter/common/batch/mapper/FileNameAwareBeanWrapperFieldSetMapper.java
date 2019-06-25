package pl.gajowski.mateusz.csvimporter.common.batch.mapper;

import lombok.SneakyThrows;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import pl.gajowski.mateusz.csvimporter.common.batch.model.FileNameAwareItem;

import java.net.URLDecoder;
import java.util.Objects;

public class FileNameAwareBeanWrapperFieldSetMapper<T> extends BeanWrapperFieldSetMapperCustom<T> {
    private String fileName;

    @Override
    protected void initBinder(DataBinder binder) {
        super.initBinder(binder);
    }

    public FileNameAwareBeanWrapperFieldSetMapper(UrlResource urlResource, Class targetType) {
        this(null, urlResource, targetType);
    }

    @SuppressWarnings("unchecked")
    public FileNameAwareBeanWrapperFieldSetMapper(String localDateFormat, UrlResource urlResource, Class targetType) {
        super(localDateFormat);
        setTargetType(targetType);
        resolveFileName(urlResource);
    }

    @Override
    public T mapFieldSet(FieldSet fs) throws BindException {
        final T t = super.mapFieldSet(fs);

        if (t instanceof FileNameAwareItem) {
            ((FileNameAwareItem) t).setFileName(fileName);
        }

        return t;
    }

    @SneakyThrows
    private void resolveFileName(UrlResource urlResource) {
        this.fileName = URLDecoder.decode(Objects.requireNonNull(urlResource.getFilename()), "UTF-8");
    }
}