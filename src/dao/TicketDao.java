package dao;

import entity.Ticket;
import exceptions.DaoException;
import util.ConnectionManager;

import java.sql.SQLException;
import java.sql.Statement;

public class TicketDao {
    private static final TicketDao INSTANCE = new TicketDao();
    private static String DELETE_SQL = "DELETE FROM ticket WHERE id=?;";
    private static final String SAVE_SQL = "INSERT INTO ticket(passenger_no,passenger_name,flight_id,seat_no,cost) values (?,?,?,?,?);";
    private TicketDao(){
    }
    public static TicketDao getInstance(){
        return INSTANCE;
    }
    //This method insert value into table
    public static Ticket save(Ticket ticket){
        try (var connection = ConnectionManager.get();var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1,ticket.getPassengerNo());
            preparedStatement.setString(2,ticket.getPassengerName());
            preparedStatement.setLong(3,ticket.getFlightId());
            preparedStatement.setString(4,ticket.getSeatNo());
            preparedStatement.setBigDecimal(5,ticket.getCost());
            preparedStatement.executeUpdate();
            var generatedKey = preparedStatement.getGeneratedKeys();
            if(generatedKey.next()){
                ticket.setId(generatedKey.getLong("id"));
            }
            return ticket;
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
    }
    //This method deletes row from table by ID
    public static boolean delete(Long id){
        try(var connection = ConnectionManager.get();var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1,id);
            return preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
