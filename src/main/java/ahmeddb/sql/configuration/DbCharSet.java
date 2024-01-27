package ahmeddb.sql.configuration;

import java.nio.charset.Charset;

/**
 * A wrapper class to make sure that it's always set charset only once.
 */
public final class DbCharSet {

    private final Charset charset;

    private static DbCharSet INSTANCE;

    private DbCharSet(Charset charset){
        this.charset = charset;
    }

    public static DbCharSet getInstance(Charset charset){
        if (INSTANCE == null){
            synchronized (DbCharSet.class){
                if (INSTANCE == null) INSTANCE = new DbCharSet(charset);
            }
        }
        return INSTANCE;
    }

    public Charset getCharset() {
        return charset;
    }
}
