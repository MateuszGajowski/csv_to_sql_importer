package pl.gajowski.mateusz.csvimporter.common.batch.configuration;

import lombok.Data;

@Data
public class FileBatchImporterProperties {
    private int chunk = 128;
    private int gridSize = 10;
    private String filesLocation = "input/*.csv";
    private String delimeter = ",";
    private ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();

    @Data
    public static class ThreadPoolConfig {
        private int maxPoolSize = 10;
        private int corePoolSize = 10;
        private int queueCapacity = 10;
    }
}
