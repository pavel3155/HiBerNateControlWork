package org.example;

import org.example.connBD.Connect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB extends Connect {
    /*
     * конструктор класса 'DB', вызывает родительский класс,
     * скоторый оздает подключение к серверу СУБД
     */
    public DB(String log, String pass) {
        super(log, pass);
    }
    /*
     * метод удаляет БД,если она существует
     */
    public void DropDB(String nameDB) {
        try (Statement stmt = con.createStatement();) {
            String sql = "drop database if exists " + nameDB;
            stmt.executeUpdate(sql);
            System.out.println("База данных 'bookstore' удалена...'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод создает БД 'bookstore'
     */
    public void CreatDB(String nameDB) {
        try (Statement stmt = con.createStatement();) {
            String sql = "create database " + nameDB;
            stmt.executeUpdate(sql);
            System.out.println("База данных 'bookstore' создана...'");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод удаляет из БД 'bookstore таблицы 'author', 'books' если они существуют
     */
    public void DropTbls(){
        try (Statement stmt = con.createStatement();) {
            String tblAuthor = "drop table if exists author cascade";
            String tblBook = "drop table if exists books cascade";
            stmt.executeUpdate(tblAuthor);
            stmt.executeUpdate(tblBook);
            System.out.println("таблицы  'author', 'books' удалены...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод создает в БД 'bookstore таблицы 'author', 'books'
     */
    public void CreatTbls(){
        try (Statement stmt = con.createStatement();) {
            String tblAuthor = "create table author (id serial primary key, author varchar (50))";
            String tblBooks = "create table books (id serial primary key, id_author integer, title varchar(100), price real," +
                             "constraint fk_author foreign key (id_author) references author(id) on delete set null)";
            stmt.executeUpdate(tblAuthor);
            stmt.executeUpdate(tblBooks);
            System.out.println("таблицы  author, books созданы...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод добавляет записи в таблицу 'author'
     */
    public void LoadAuthor(String[][] books) {
        String insert="insert into author (author) values (?)";
        try  {
            PreparedStatement pSt=con.prepareStatement(insert);
            for (String[] author : books){
                pSt.setString(1,author[0]);
                pSt.addBatch();
            }
            pSt.executeBatch();
            pSt.close();
            System.out.println("таблица author заполнена...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод добавляет записи в таблицу 'books'
     */
    public void LoadBooks(String[][] books) {
        String insert="insert into books (id_author, title, price) values (?,?,?)";
        try  {
            PreparedStatement pSt=con.prepareStatement(insert);
            int id_author=0;
            for (String[] author : books) {
                id_author++;
                for (int book=1; book<author.length;book++){
                    pSt.setInt(1,id_author);
                    pSt.setString(2,author[book]);
                    pSt.setDouble(3,10000* Math.random());
                    pSt.addBatch();
                }
                System.out.println();
            }
            pSt.executeBatch();
            pSt.close();
            System.out.println("таблица books заполнена...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
     * метод создает соединение таблиц 'author' и 'books',
     * выводит результат запроса на экран
     */
    public void OutBooks(){
        String query = "select a.author,b.title,b.price from books as b join author as a on b.id_author=a.id";
        try {
            Statement stmt = con.createStatement();
            ResultSet RS  =stmt.executeQuery(query);
            while(RS.next()) {
                System.out.println(RS.getString("author")+":  "+RS.getString("title")+":  "+RS.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
