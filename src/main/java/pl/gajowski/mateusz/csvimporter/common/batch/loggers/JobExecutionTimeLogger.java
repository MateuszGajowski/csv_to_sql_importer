package pl.gajowski.mateusz.csvimporter.common.batch.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.util.Date;

public class JobExecutionTimeLogger extends JobExecutionListenerSupport {
    public static final Logger LOGGER = LoggerFactory.getLogger(JobExecutionTimeLogger.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        final Date start = jobExecution.getCreateTime();
        final Date end = jobExecution.getEndTime();
        long diff = end.getTime() - start.getTime();

        LOGGER.info("Execution of job with id={} took={}ms",
                jobExecution.getJobId(),
                diff);
    }
}
