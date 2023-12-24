package dao;

import dto.TicketFilter;
import entity.Flight;
import entity.Ticket;
import exceptions.DaoException;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketDao {
    private static final TicketDao INSTANCE = new TicketDao();
    //Sql queries that used in methods
    private static String DELETE_SQL = "DELETE FROM ticket WHERE id=?;";
    private static final String SAVE_SQL = "INSERT INTO ticket(passenger_no,passenger_name,flight_id,seat_no,cost) values (?,?,?,?,?);";
    private static final String UPDATE_SQL = "" +
            "UPDATE ticket " +
            "SET passenger_no=?,passenger_name=?,flight_id=?,seat_no=?,cost=?;" +
            "WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT ticket.id, " +
            "passenger_no, " +
            "passenger_name, " +
            "flight_id, " +
            "seat_no, " +
            "cost, " +
            "f.status, " +
            "f.aircraft_id, " +
            "f.arrival_airport_code, " +
            "f.arrival_date, " +
            "f.departure_airport_code, " +
            "f.departure_date, " +
            "f.flight_no " +
            " FROM ticket " +
            " JOIN flight f " +
            " ON ticket.flight_id = f.id ";
    private static final String  FIND_BY_ID_SQL = FIND_ALL_SQL+" WHERE ticket.id = ?;";

    private TicketDao(){
    }
    public List<Ticket>findAll(TicketFilter filter){
        List<Object>parameters = new ArrayList<>();
        List<String>whereSql = new ArrayList<>();
        if (filter.seatNo()!=null){
            whereSql.add(" seat_no LIKE ? ");
            parameters.add("%"+filter.seatNo()+"%");
        }
        if (filter.passengerName()!=null){
            whereSql.add(" passenger_name = ? ");
            parameters.add(filter.passengerName());
        }
        var where = whereSql.stream().collect(Collectors.joining(" AND "," WHERE ", " LIMIT ? OFFSET ? "));

        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var sql = FIND_ALL_SQL+where;
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(sql)) {
                for(int i=0;i<parameters.size();i++){
                    preparedStatement.setObject(i+1,parameters.get(i));
                }
                var resultSet = preparedStatement.executeQuery();
                System.out.println(preparedStatement);
                List<Ticket>tickets = new ArrayList<>();
                while (resultSet.next()){
                    tickets.add(buildTicket(resultSet));
                }
                return tickets;
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
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
        var flight = new Flight(
                resultSet.getLong("flight_id"),
                resultSet.getInt("aircraft_id"),
                resultSet.getString("arrival_airport_code"),
                resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                resultSet.getString("departure_airport_code"),
                resultSet.getTimestamp("departure_date").toLocalDateTime(),
                resultSet.getString("flight_no"),
                resultSet.getString("status")
                );
        return new Ticket(resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flight,
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
                ticket = buildTicket(resultSet);
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
            preparedStatement.setLong(3,ticket.getFlightId().id());
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
            preparedStatement.setLong(3,ticket.getFlightId().id());
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
