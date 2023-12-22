
import util.ConnectionManager;

import java.sql.SQLException;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        getTablesByName("pg_catalog");
    }
    private static void getTablesFromPublicSchema()throws SQLException{
        try (var open = ConnectionManager.get()) {
            var metaData = open.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()){
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                while (schemas.next()){
                    var schema = schemas.getString(1);
                    var tables = metaData.getTables(catalog,schema,"%",new String[]{"TABLE"});
                    if(schema.equals("public")){
                        while (tables.next()){
                            var table = tables.getString("TABLE_NAME");
                            var columns = metaData.getColumns(catalog,schema,table,"%");
                        }
                    }
                }
            }
        }
    }
    static void getTablesByName(String name)throws SQLException{
        try (var open = ConnectionManager.get()) {
            var metaData = open.getMetaData();
            var catalogs = metaData.getCatalogs();
            while (catalogs.next()){
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                while (schemas.next()){
                    var schema = schemas.getString("TABLE_SCHEM");
                    var tables = metaData.getTables(catalog,schema,"%",null);
                    if(schema.equals(name)){
                        while (tables.next()){
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }
    private static void checkMetaData()throws SQLException{
        try (var open = ConnectionManager.get()) {
            var metaDate = open.getMetaData();
            //Получение названия каталога
            var catalogs = metaDate.getCatalogs();
            while (catalogs.next()){
                System.out.println(catalogs.getString(1));
            }
            //Получение схем
            var schemas = metaDate.getSchemas();
            while (schemas.next()){
                System.out.println(schemas.getString("TABLE_SCHEM"));
                var tables = metaDate.getTables(null,null,"%s",null);
                while (tables.next()){
                    System.out.println(tables.getString("TABLE_NAME"));
                }
            }
        }
    }
}
