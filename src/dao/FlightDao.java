package dao;

import entity.Flight;
import exceptions.DaoException;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight>{
    private static final FlightDao INSTANCE = new FlightDao();
    private static final String FINT_BY_ID = "" +
            "SELECT id," +
            "aircraft_id," +
            "arrival_airport_code," +
            "arrival_date," +
            "departure_airport_code," +
            "departure_date," +
            "flight_no," +
            "status" +
            " FROM flight WHERE id =  ?";
    public FlightDao() {
    }
    public static FlightDao getInstance(){
        return INSTANCE;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Flight save(Flight ticket) {
        return null;
    }

    @Override
    public Flight update(Flight ticket) {
        return null;
    }
    public Optional<Flight> findById(Long id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FINT_BY_ID)) {
            preparedStatement.setLong(1,id);
            var resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            while (resultSet.next()){
                flight = new Flight(resultSet.getLong("id"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("flight_no"),
                        resultSet.getString("status")
                );
            }
            return Optional.ofNullable(flight);
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id,connection);
        }catch (SQLException throwables){
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<Flight> findAll() {
        return null;
    }
}
