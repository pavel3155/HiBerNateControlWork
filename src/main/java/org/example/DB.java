package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.connBD.Connect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DB extends Connect {
    /*
     * конструктор класса 'DB', вызывает родительский класс,
     * скоторый создает подключение к серверу СУБД
     */
    public DB(String log, String pass) {
        super(log, pass);
    }
    /*
     * метод удаляет БД,если она существует
     */
    public void DropDB(String nameDB) {
        try (Statement stmt = con.createStatement();) {
            String backend = "select pg_terminate_backend(pid) from pg_stat_activity where datname='"+nameDB+"'";//завершаем сеанс подключения к БД
            String sql = "drop database if exists " + nameDB;
            stmt.executeQuery(backend);
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

    /*
     * метод добавляет объект сущность в соответствующую таблицу БД,
     * возвращает id(PK) добавленного в таблицу объекта
     */
    public  <T> Integer objADD(T objClass){
        SessionFactory SF=new Configuration().configure().buildSessionFactory();
        Session session= SF.openSession();
        Transaction transaction = session.beginTransaction();
        Integer id=(Integer) session.save(objClass);
        transaction.commit();
        session.close();
        SF.close();
        return id;
    }

    /*
     * метод возвращает объект сущность по id(PK)соответствующейтаблицы БД,
     */
    public <T> T objFind(Class<T> cls, int pKey){
        SessionFactory SF=new Configuration().configure().buildSessionFactory();
        Session session= SF.openSession();
        T obj=session.find(cls,pKey);
        session.close();
        SF.close();
        return obj;
    }

    /* метод обновляет одно поле таблицы связанной с калассом (сущностью)'cls', при выполнении одного условия;
     * метод принимает в качестве входных параметров поле/значение 'setFieled'/'setValue', которое требуется обновить/присвоить,
     * поле/значение 'whFieled'/'whValue' условия для обновления записи таблицы,
     * класс(сущность) 'cls'  связанный с таблицей в БД.
     */

    public <S,W,T> void myQueryUpdate(String setFieled,
                                              S setValue,
                                              String whFieled,
                                              W whValue,
                                              Class<T> cls )
    {
        SessionFactory SF = new Configuration().configure().buildSessionFactory();
        try (Session session = SF.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<T> criteriaUpdate = builder.createCriteriaUpdate(cls);
            Root<T> root = criteriaUpdate.from(cls);
            criteriaUpdate.set(setFieled,setValue);
            criteriaUpdate.where(builder.equal (root.get(whFieled), whValue));
            Transaction transaction = session.beginTransaction();
            int count = session.createQuery(criteriaUpdate).executeUpdate();
            System.out.println("количество обновленных записей: " + count);
            transaction.commit();
        }
        SF.close();
    }

    /*
     * метод обновляет одно поле таблицы связанной с калассом (сущностью)'cls',при выполнении двух условий;
     * метод принимает в качестве входных параметров поле/значение 'setFieled'/'setValue', которое требуется обновить/присвоить,
     * поле/значение 'whFieled1'/'whValue1' первого условия и 'whFieled2'/'whValue2' второго условия для обновления записи таблицы,
     * класс(сущность) 'cls'  связанный с таблицей в БД.
     */
    public <S,W1,W2,T> void myQueryUpdate(String setFieled,
                                                  S setValue,
                                                  String whFieled1,
                                                  W1 whValue1,
                                                  String whFieled2,
                                                  W2 whValue2,
                                                  Class<T> cls )
    {
        SessionFactory SF = new Configuration().configure().buildSessionFactory();
        try (Session session = SF.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<T> criteriaUpdate = builder.createCriteriaUpdate(cls);
            Root<T> root = criteriaUpdate.from(cls);
            Predicate wV1=builder.equal(root.get(whFieled1),whValue1);
            Predicate wV2=builder.equal(root.get(whFieled2),whValue2);
            criteriaUpdate.set(setFieled,setValue);
            criteriaUpdate.where(builder.and(wV1,wV2));
            Transaction transaction = session.beginTransaction();
            int count = session.createQuery(criteriaUpdate).executeUpdate();
            System.out.println("количество обновленных записей: " + count);
            transaction.commit();
        }
        SF.close();
    }

    /*
     * метод принимает в качестве входных параметротв
     * класс(сущность) 'cls'  связанный с таблицей в БД,
     * имя класса(сущность) и выводит все записи таблицы БД связанной с классом 'cls'
     */
    public <T> void myQueryAllEntries(Class<T> cls, String entName){
        SessionFactory SF=new Configuration().configure().buildSessionFactory();
        try (Session session= SF.openSession()){
            String hql = "from "+entName;
            Query<T> query =  session.createQuery(hql, cls);
            List<T> list = query.list();
            SF.close();
            for (T l : list){
                System.out.println(l);
            }
        }
    }

}
