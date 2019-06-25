package pl.gajowski.mateusz.csvimporter.common.batch.writter;

import org.springframework.batch.support.annotation.Classifier;
import pl.gajowski.mateusz.csvimporter.common.entity.TableNameAwareEntity;

public class TableNameClassifier {

    @Classifier
    public String classify(TableNameAwareEntity tableNameAwareEntity) {
        return tableNameAwareEntity.getTableName();
    }
}
