package ahmeddb.sql.configuration;

/**
 * Default database configuration class.
 */
public final class AhmedDbConfig extends DataSourceConfig {

    private static AhmedDbConfig instance;
    private AhmedDbConfig(){}

    //make a thread-safe singleton class, so configuration is the same across all application
    public static AhmedDbConfig getInstance(){
        if (instance == null){
            synchronized (AhmedDbConfig.class) {
                if (instance == null) instance = new AhmedDbConfig();
            }
        }
        return instance;
    }

}
