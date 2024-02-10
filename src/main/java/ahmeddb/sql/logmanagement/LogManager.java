package ahmeddb.sql.logmanagement;

import ahmeddb.sql.configuration.DataSourceConfigProvider;
import ahmeddb.sql.filemanagement.FileManager;
import ahmeddb.sql.filemanagement.Page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * The log manager is the database engine component responsible for writing log records to the log file.
 * It doesn't understand the contents of the log records that responsibility belongs to the recovery manager.
 * Instead, the log manager treats the log as just an ever-increasing sequence of log records.
 * Each block in the log file may contain more than 1 log record as the Log records can have varying sizes.
 * Each log page will have its size for a specific record bytes only, NO record will have equal amount of bytes to
 * another record, it depends on the record data.
 * The algorithm of dealing with log file:
 * <pre>
 * - Permanently allocate one memory page to hold the contents of the last block of the log file.
 * - When a new log record is submitted:
 *      a) If there is no room in the page, then: Write the page to disk and clear its contents.
 *      b) Append the new log record to the page.
 * - When the database system requests that a particular log record be written to disk:
 *      a) Determine if that log record is in the page.
 *      b) If so, then write the page to disk.
 * </pre>
 */
public class LogManager {

    /**
     * A singleton instance of the log manager object, each database instance has only one log manager that deal with its log files.
     */
    private static LogManager INSTANCE;

    /**
     * File manager instance to do operations in db files.
     */
    private static final FileManager fileManager = FileManager.getInstance();


    /**
     * A logical block size inside the file (any db file has many blocks that all have the same sizes).
     */
    private static final int blockSize = DataSourceConfigProvider.getDataSourceConfig().getBlockSize();

    /**
     * The name of the log file that will be responsible for storing db logging data.
     */
    private static final String logFileName = DataSourceConfigProvider.getDataSourceConfig().getLogFileName();

    /**
     * The name of the database log directory
     */
    private static final String logDirectoryName = DataSourceConfigProvider.getDataSourceConfig().getLogDirectoryName();

    /**
     * Each block in the log file may contain more than 1 log record as the Log records can have varying sizes.
     * The page contain the contents of the last log block in the file.
     */
    private final Page logPage;

    private LogManager(){
        createLogDirectory();
        logPage = new Page(new byte[blockSize]);
        logPage.setInt(0,blockSize);
    }

    /**
     * Create a database log directory if it's not exist.
     */
    private void createLogDirectory(){
        Path directoryPath = Path.of(logDirectoryName);
        try {
            if (Files.notExists(directoryPath)) Files.createDirectories(directoryPath);
        }
        catch (IOException ioException){
            throw new RuntimeException("Cannot create database log directory for the name: '" + logDirectoryName + "'", ioException.getCause());
        }
    }

    //make a thread-safe singleton object, so the instance is the same across application lifecycle.
    public static LogManager getINSTANCE() {
        if (INSTANCE == null){
            synchronized (LogManager.class){
                if (INSTANCE == null){
                    INSTANCE = new LogManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * The method append adds a record to the log and returns an integer. As far as the
     * log manager is concerned, a log record is an arbitrarily sized byte array;
     * it saves the array in the log file but has no idea what its contents denote.
     * The only constraint is that the array must fit inside a page. The return value
     * from append identifies the new log record; this identifier is called its log sequence number (or LSN).
     * Appending a record to the log does not guarantee that the record will get written
     * to disk; instead, the log manager chooses when to write log records to disk.
     * client can force a specific log record to disk by calling the method flush. The argument to
     * flush is the LSN of a log record; the method ensures that this log record (and all previous log records) is written to disk.
     *
     * @param record the record that will be appended to the end of the log file.
     * @return log sequence number, that identifies that a new record has been added, and the returned number belongs to it.
     */
    public int append(LogRecord record) throws Exception {
        throw new Exception("Not Implemented yet");
    }
}
