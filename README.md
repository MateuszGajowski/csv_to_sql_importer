# csv_to_sql_importer

Simple Spring Boot project which imports .csv files to database.

### Useful commands
gradlew bootRun - to start application
gradlew test - run all tests

### Description
This application is mainly composed on top of Spring Batch. Files are read from 'input' directory and after processing are saved into database.

File processing are done in multiple threads (each for one file). Each file are processed in chunks.
### Some of used frameworks and libraries
- Spring Boot
- Spring Batch (4.2.0.M1 with build in metrics)
- Lombok
- H2 Database
- Micrometer (with Prometheus)

### Configuration
There is simple configuration for this batch (see `FileBatchImporterProperties` class).
Available properties to configure:
- chunk
- gridSize
- filesLocation
- delimeter
- threadPoolConfig
  - maxPoolSize
  - corePoolSize
  - queueCapacity
  
### Useful endpoints
All endpoints from /actuator all enabled.
Prometheus endpoint is also configured if metrics are needed from batch processing.

