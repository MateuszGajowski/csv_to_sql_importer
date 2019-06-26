package pl.gajowski.mateusz.csvimporter.bond.batch.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import pl.gajowski.mateusz.csvimporter.common.batch.configuration.FileBatchImporterProperties;
import pl.gajowski.mateusz.csvimporter.bond.model.csv.Bond;
import pl.gajowski.mateusz.csvimporter.common.batch.mapper.FileNameAwareBeanWrapperFieldSetMapper;
import pl.gajowski.mateusz.csvimporter.common.editor.SafeLocalDateEditor;

import java.net.MalformedURLException;

@Configuration
public class BondReaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BondReaderConfiguration.class);

    @Bean
    @StepScope
    public FlatFileItemReader<Bond> bondReader(@Value("#{stepExecutionContext['fileName']}") String file,
                                               FileBatchImporterProperties fileBatchImporterProperties) {
        final UrlResource urlResource;
        try {
            urlResource = new UrlResource(file);
        } catch (MalformedURLException e) {
            LOGGER.error("Error processing file URL", e);
            throw new IllegalStateException("Incorrect file path format=" + file);
        }

        return new FlatFileItemReaderBuilder<Bond>()
                .resource(urlResource)
                .delimited()
                .delimiter(fileBatchImporterProperties.getDelimeter())
                .names(new String[]{"error", "errorText", "isin", "mvUnitNotional",
                        "scenarioDate", "scenarioSpecId", "tradehubMessageId", "type", "zeroSimulation"})
                .linesToSkip(1)
                .fieldSetMapper(new FileNameAwareBeanWrapperFieldSetMapper<Bond>(urlResource, Bond.class) {{
                    setCustomEditors(SafeLocalDateEditor.create("yyyy-MM-dd"));
                }})
                .name("bond")
                .build();
    }
}
