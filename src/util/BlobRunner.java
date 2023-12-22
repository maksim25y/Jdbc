package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

public class BlobRunner {
    public static void main(String[] args) throws SQLException, IOException {
        //blob - binary large object (Бинарный большой объект)
        //saveImg();
//        saveImgInPostgres();
        try {
            getImage();
        } finally {
            ConnectionManager.closePool();
        }
    }
    //Чтение потока байтов из БД
    private static void getImage() throws SQLException, IOException {
        var sql = "SELECT image FROM aircraft WHERE id=?";
        try (var connection = ConnectionManager.get()) {
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,1);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                var resultImg = resultSet.getBytes("image");
                Files.write(Path.of("resourses","boing777copy.jpg"),resultImg, StandardOpenOption.CREATE);
            }
        }
    }
    //В СУБД Postgresql(где нет поддержки blob,clob)
    private static void saveImgInPostgres()throws SQLException, IOException {
        var sql = "UPDATE aircraft SET image=? WHERE id=1;";
        try (var connection = ConnectionManager.get()) {
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1,Files.readAllBytes(Path.of("resourses","boing777.jpg")));
            preparedStatement.executeUpdate();
        }
    }
    //В СУБД по типу Oracle(где есть поддержка blob,clob)
    private static void saveImg()throws SQLException, IOException {
        var sql = "UPDATE aircraft SET image=? WHERE id=1";
        try (var connection = ConnectionManager.get()) {
                var preparedStatement = connection.prepareStatement(sql);
                connection.setAutoCommit(false);
                var blob = connection.createBlob();
                blob.setBytes(1, Files.readAllBytes(Path.of("resourses","boing777.jpg")));
                preparedStatement.setBlob(1,blob);
                preparedStatement.executeUpdate();
                connection.commit();
        }
    }
}
