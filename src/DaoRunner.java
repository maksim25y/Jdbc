import dao.TicketDao;
import entity.Ticket;

import java.math.BigDecimal;

public class DaoRunner {
    public static void main(String[] args) {
        var ticketDao = TicketDao.getInstance();
        var rows = ticketDao.findAll();
        for(Ticket ticket:rows){
            System.out.println(ticket);
        }
    }
    public void updateTest(){
        var ticketDao = TicketDao.getInstance();
        var byId = ticketDao.findById(5L);
        System.out.println(byId);
    }
    public static void deleteTest(){
        var ticketDao = TicketDao.getInstance();
        var deleted = ticketDao.delete(2L);
        System.out.println(deleted);
    }
    public static void savedTest(){
        var ticketDao = TicketDao.getInstance();
        var ticket = new Ticket();
        ticket.setPassengerNo("123134");
        ticket.setPassengerName("Maksim");
        ticket.setFlightId(3L);
        ticket.setSeatNo("B3");
        ticket.setCost(BigDecimal.TEN);
        var savedTicket = TicketDao.save(ticket);
        System.out.println(savedTicket);
    }
}
