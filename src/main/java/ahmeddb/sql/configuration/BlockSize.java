package ahmeddb.sql.configuration;

/**
 * A wrapper class to make sure that it's always set block size only once.
 */
public final class BlockSize {
    
    private final int size;
    
    private static BlockSize INSTANCE;
    
    private BlockSize(int size){
        this.size = size;
    }
    
    public static BlockSize getInstance(int size){
        if (INSTANCE == null){
            synchronized (BlockSize.class){
                if (INSTANCE == null) INSTANCE = new BlockSize(size);
            }
        }
        return INSTANCE;
    }

    public int getSize() {
        return size;
    }
}
