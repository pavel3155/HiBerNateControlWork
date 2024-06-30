package org.example.connBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    String HOST_NAME ="jdbc:postgresql://localhost:5432/";
    String LOGIN;
    String PASS;
    public Connection con;
    /*
     * конструктор класса 'Connect',
     * создает подключение к серверу СУБД
     */
    public Connect(String log, String pass){
        try {
            Class.forName("org.postgresql.Driver");
            this.LOGIN=log;
            this.PASS=pass;
            String strConn=this.HOST_NAME;
            this.con = DriverManager.getConnection(strConn,LOGIN,PASS);
            System.out.println("соединение с сервером установлено...");
        } catch (ClassNotFoundException e ) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("драйвер подключен...");
    }
    /*
     * метод создает подключение к БД
     */
    public void connToDB(String nameBD, String encoding){
        try {
            String strConn=this.HOST_NAME+nameBD+encoding;
            this.con = DriverManager.getConnection(strConn,LOGIN,PASS);
            System.out.println("соединение с БД установлено...");
        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
    }
    /*
     * метод закрывает соединение
     */
    public void connClose() {
        try{
            con.close();
            System.out.println("соединение закрыто...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
