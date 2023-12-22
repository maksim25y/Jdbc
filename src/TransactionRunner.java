import util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {
    public static void main(String[] args) throws SQLException {
        Long deleteId = 2L;
        var deleteFlightSql = "DELETE FROM flight WHERE id = "+deleteId;
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id ="+deleteId;
        Connection connection = null;
        Statement statement = null;
        try {
            connection=ConnectionManager.get();
            connection.setAutoCommit(false);
            //создание батча
            statement = connection.createStatement();
            //Добавление запросов в батч
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);
            //Возвращает массив, полученный после выполнения запроса
            statement.executeBatch();
            connection.commit();
        }catch (Exception e){
            if(connection!=null){
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection!=null){
                connection.close();
            }
            if(statement!=null){
                statement.close();
            }
        }
    }
}
