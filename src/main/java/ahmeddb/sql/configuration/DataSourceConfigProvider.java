package ahmeddb.sql.configuration;

/**
 * The class provide the database configuration overall application, the configuration implementation
 * can be changed to another implementation by calling the setter method.
 */
public final class DataSourceConfigProvider extends DataSourceConfig{
    private DataSourceConfigProvider(){}
    private static DataSourceConfig dataSourceConfig;

    /**
     * Setting db configuration, if this method not invoked, a default implementation will be provided.
     * @apiNote The setter method will not have any effect if configuration is not provided from tne beginning.
     * @param newDataSourceConfig Passing a configuration implementation to this method will be used.
     */
    public synchronized static void setDataSourceConfig(DataSourceConfig newDataSourceConfig){
        if (dataSourceConfig == null) dataSourceConfig = newDataSourceConfig;
    }

    /**
     * This method used internally to all internal db classes to provide necessary configuration,
     * it's not intended to use by any user.
     * @return DataSourceConfig implementation
     */
    public synchronized static DataSourceConfig getDataSourceConfig(){
        if (dataSourceConfig == null) dataSourceConfig = AhmedDbConfig.getInstance();
        return dataSourceConfig;
    }
}
