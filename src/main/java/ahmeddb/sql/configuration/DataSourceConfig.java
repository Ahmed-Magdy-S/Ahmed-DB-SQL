package ahmeddb.sql.configuration;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Responsible for setting/getting database configuration, it must be extended to use the configuration data.
 * The configuration data are set only once during the first use and cannot be changed in the future, Except for Database Name.
 */
public abstract class DataSourceConfig {

    private BlockSize blockSize;

    /**
     * The database name for the application.
     * It's the only variable that can be changed in this configuration.
     */
    private String databaseName;
    private DbCharSet dbCharSet;

    /**
     * Once size is set for a first time, it cannot be changer in the future, even if you call this method again.
     * @param size Logical Block Size for a DB file.
     */
    public synchronized void setBlockSize(int size){
        blockSize = BlockSize.getInstance(size);
    };

    /**
     * Take care if the block size didn't set before, a default value
     * will be set, and it won't be changed in the future.
     * @return The singleton instance of the block size
     */
    public synchronized int getBlockSize(){
        if (blockSize == null) blockSize = BlockSize.getInstance(4096);
        return blockSize.getSize();

    }

    public void setDatabaseName(String databaseName){
        this.databaseName = databaseName;
    }

    public String getDatabaseName(){
        return this.databaseName;
    }

    /**
     * Once charset is set for a first time, it cannot be changer in the future, even if you call this method again.
     * @param charset The charset that will be set for all db files.
     */
    public synchronized void setDbCharset(Charset charset){
        dbCharSet = DbCharSet.getInstance(charset);
    }

    /**
     * Take care if the charset didn't set before, a default value
     * will be set, and it won't be changed in the future.
     * @return The singleton instance of the DbCharSet
     */
    public synchronized Charset getDbCharset(){
        if (dbCharSet == null) dbCharSet = DbCharSet.getInstance(StandardCharsets.UTF_8);
        return dbCharSet.getCharset();
    }

}
