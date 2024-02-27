package ahmeddb.sql.logmanagement;

import ahmeddb.sql.configuration.DataSourceConfigProvider;
import ahmeddb.sql.filemanagement.BlockId;
import ahmeddb.sql.filemanagement.FileManager;
import ahmeddb.sql.filemanagement.Page;

import java.util.Iterator;

public class LogRecordList implements Iterable<LogRecord>{

    private static final FileManager fileManager = FileManager.getInstance();

    private static final int BLOCK_SIZE = DataSourceConfigProvider.getDataSourceConfig().getBlockSize();

    private static final String LOG_FILE_NAME = DataSourceConfigProvider.getDataSourceConfig().getLogFileName();

    /**
     * The index of a block in a file
     */
    private long currentBlockIndex;

    private final Page logPage;

    /**
     * The index at which we start reading record bytes
     */
    private int recordIndex;

    public LogRecordList(){
        //we start counting down from last block in the file, as it the most recent one.
        currentBlockIndex = fileManager.getBlocks(LOG_FILE_NAME) - 1;

        //start reading the last block to load into memory page
        logPage = new Page(new byte[BLOCK_SIZE]);
        fileManager.read(new BlockId(LOG_FILE_NAME,currentBlockIndex),logPage);

        //get initial first position
        recordIndex = logPage.getInt(0);
    }


    @Override
    public Iterator<LogRecord> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return currentBlockIndex >= 0;
            }

            @Override
            public LogRecord next() {
                byte[] recordBytes = logPage.getBytes(recordIndex);
                recordIndex = logPage.position();
                if (!logPage.hasRemaining()) moveToPreviousBlock();
                return new LogRecord(recordBytes);
            }
        };
    }


    /**
     * We move to blocks in reverse as we need to start from most recent to least recent.
     */
    private void moveToPreviousBlock() {
        currentBlockIndex--;
        if (currentBlockIndex >= 0){
            //read the new block
            fileManager.read(new BlockId(LOG_FILE_NAME, currentBlockIndex),logPage);
            logPage.position(0);
            //get the index of the first record in that new block
            recordIndex = logPage.getInt(0);
        }

    }


}
