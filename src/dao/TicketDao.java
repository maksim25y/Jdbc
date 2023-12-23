package dao;

import entity.Ticket;
import exceptions.DaoException;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao {
    private static final TicketDao INSTANCE = new TicketDao();
    //Sql queries that used in methods
    private static String DELETE_SQL = "DELETE FROM ticket WHERE id=?;";
    private static final String SAVE_SQL = "INSERT INTO ticket(passenger_no,passenger_name,flight_id,seat_no,cost) values (?,?,?,?,?);";
    private static final String UPDATE_SQL = "" +
            "UPDATE ticket " +
            "SET passenger_no=?,passenger_name=?,flight_id=?,seat_no=?,cost=?;" +
            "WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id,passenger_no,passenger_name,flight_id,seat_no,cost FROM ticket";
    private static final String  FIND_BY_ID_SQL = FIND_ALL_SQL+" WHERE id = ?;";

    private TicketDao(){
    }
    //This method returns all rows in table
    public static List<Ticket>findAll() {
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Ticket>tickets = new ArrayList<>();
            while (resultSet.next()){
                tickets.add(buildTicket(resultSet));
            }
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    //This method build element and return it
    private static Ticket buildTicket(ResultSet resultSet)throws SQLException{
        return new Ticket(resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                resultSet.getLong("flight_id"),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }
    //This method find element in table by id
    public static Optional<Ticket> findById(Long id){
        try(var connection = ConnectionManager.get();var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)){
            preparedStatement.setLong(1,id);
            var resultSet = preparedStatement.executeQuery();
            Ticket ticket = null;
            if(resultSet.next()){
                ticket = new Ticket(resultSet.getLong("id"),
                        resultSet.getString("passenger_no"),
                        resultSet.getString("passenger_name"),
                        resultSet.getLong("flight_id"),
                        resultSet.getString("seat_no"),
                        resultSet.getBigDecimal("cost")
                );
            }
            return Optional.ofNullable(ticket);
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
    }
    //This method update some rows in table
    private static Ticket update(Ticket ticket){
        try (var connection = ConnectionManager.get();var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1,ticket.getPassengerNo());
            preparedStatement.setString(2,ticket.getPassengerName());
            preparedStatement.setLong(3,ticket.getFlightId());
            preparedStatement.setString(4,ticket.getSeatNo());
            preparedStatement.setBigDecimal(5,ticket.getCost());
            preparedStatement.setLong(6,ticket.getId());
            preparedStatement.executeUpdate();
            return ticket;
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
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
