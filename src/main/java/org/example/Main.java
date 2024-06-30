package org.example;

import Entitys.Author;
import Entitys.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        createDB();//создаем и наполняем БД 'bookstore'
        int idAuthor=objADD(new Author("Б.Л. Пастернак"));//добавляем запись в таблицу 'author'
        Author author = objFind(Author.class,idAuthor);
        int idBook = objADD(new Book("Я человек...",1240.45,author));//добавляем запись(книгу) в таблицу 'books'
        myQueryToUsersScrollLast();//выводит на экран все запси(книги) таблицы 'books'
        myQueryUpdate(objFind(Author.class,4),"Доктор Живаго"); //изменем название книги
        myQueryToUsersScrollLast();//выводит на экран все запси(книги) таблицы 'books'
    }

    private static void createDB(){
        String[][] books={{"Ф.М. Достоевский","Преступление и наказание","Идиот","Бесы"},
                {"Л.Н. Толстой","Война и мир","Анна Каренина","Кавказский пленник","Лев и собачка"},
                {"Н.В. Гоголь","Ревизор","Тарас Бульба","Вий"}};


        DB dbLibrary=new DB("postgres","123456");//подключаемся к серверу
        dbLibrary.DropDB("bookstore");//удаляем БД если она существует
        dbLibrary.CreatDB("bookstore");//создаем БД
        dbLibrary.connToDB("bookstore","");//подключаемся к БД
        //dbLibrary.DropTbls();//удаляем таблицы если они существуют
        dbLibrary.CreatTbls();//создаем таблицы
        dbLibrary.LoadAuthor(books);//добавляем записи в таблицу
        dbLibrary.LoadBooks(books);//добавляем записи в таблицу
        dbLibrary.OutBooks();//выводим на экран записи таблицы 'books'
        dbLibrary.connClose();//закрываем соединение
    }
    /*
     * метод добавляет объект сущность в соответствующую таблицу БД,
     * возвращает id(PK) добавленного в таблицу объекта
     */
    private static <T> Integer objADD(T objClass){
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
    private static <T> T objFind(Class<T> cls, int pKey){
        SessionFactory SF=new Configuration().configure().buildSessionFactory();
        Session session= SF.openSession();
        T obj=session.find(cls,pKey);
        T obj1=session.find(cls,pKey);
        session.close();
        SF.close();
        return obj;
    }

    /*
     * метод изменяет название книги соответсвующего автора
    */
    private static void myQueryUpdate(Author author, String title) {
        SessionFactory SF = new Configuration().configure().buildSessionFactory();
        try (Session session = SF.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Book> criteriaUpdate = builder.createCriteriaUpdate(Book.class);
            Root<Book> root = criteriaUpdate.from(Book.class);
            criteriaUpdate.set("title", title);
            criteriaUpdate.where(builder.equal (root.get("author"), author));
            Transaction transaction = session.beginTransaction();
            int count = session.createQuery(criteriaUpdate).executeUpdate();
            System.out.println("количество обновленных записей: " + count);
            transaction.commit();
        }
        SF.close();
    }

    /*
     * метод выводит на экран все запси(книги) таблицы 'books'
     */
    private static void myQueryToUsersScrollLast(){
        SessionFactory SF=new Configuration().configure().buildSessionFactory();
        try (Session session= SF.openSession()){
            String hql = "from Book";
            Query<Book> query =  session.createQuery(hql, Book.class);
            List<Book> books = query.list();
            SF.close();
            for (Book b : books){
                System.out.println(b);
            }
        }
    }

}