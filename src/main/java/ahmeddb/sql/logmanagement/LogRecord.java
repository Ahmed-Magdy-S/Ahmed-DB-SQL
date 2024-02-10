package ahmeddb.sql.logmanagement;

/**
 * A wrapper class to hold log record data.
 * Each block in the log file may contain more than 1 log record as the Log records can have varying sizes.
 * Each log page will have its size for a specific record bytes only, NO record will have equal amount of bytes to
 * another record, it depends on the record data.
 * @param content Log record content.
 */
public record LogRecord(
        byte[] content
) {
}
