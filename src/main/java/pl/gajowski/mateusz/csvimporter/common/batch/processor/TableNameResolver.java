package pl.gajowski.mateusz.csvimporter.common.batch.processor;

public interface TableNameResolver {
    String resolve(String fileName);
}
