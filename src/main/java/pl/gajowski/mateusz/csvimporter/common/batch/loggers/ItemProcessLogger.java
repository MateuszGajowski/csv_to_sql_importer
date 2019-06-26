package pl.gajowski.mateusz.csvimporter.common.batch.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class ItemProcessLogger<I, O> implements ItemProcessListener<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcessLogger.class);

    @Override
    public void beforeProcess(I item) {
        LOGGER.info("Before process {}", item);
    }

    @Override
    public void afterProcess(I item, O result) {
        LOGGER.info("After process {}",  result);
    }

    @Override
    public void onProcessError(I item, Exception e) {
        LOGGER.info("On process error {}",  item, e);
    }
}
