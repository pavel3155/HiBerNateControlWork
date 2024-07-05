package org.example;

import Entitys.Author;
import Entitys.Book;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DB db = createDB();//создаем и наполняем БД 'bookstore'
        int idAuthor=db.objADD(new Author("Б.Л. Пастернак"));//добавляем запись в таблицу 'author'
        Author author = db.objFind(Author.class,idAuthor);
        int idBook = db.objADD(new Book("Я человек...",1240.45,author));//добавляем запись(книгу) в таблицу 'books'
        db.myQueryAllEntries(Book.class,"Book");//выводит на экран все запси(книги) таблицы 'books'
        db.myQueryUpdate("title",
                "Доктор Живаго",
                "author",
                db.objFind(Author.class,4),
                Book.class);//меняем название книги автора с id=4
        db.myQueryUpdate("price",
                1212.5,
                "author",
                db.objFind(Author.class,4),
                Book.class);//меняем стоимость книги автора с id=4
        db.myQueryAllEntries(Book.class,"Book");//выводит на экран все запси(книги) таблицы 'books'
        db.myQueryAllEntries(Author.class,"Author");//выводит на экран все запси(книги) таблицы 'author'
        db.myQueryUpdate("author",
                "А.С. Пушкин",
                "id",
                4,
                Author.class);//меняем автора с id=4
        db.myQueryAllEntries(Author.class,"Author");//выводит на экран все запси(книги) таблицы 'author'
        db.myQueryUpdate("title",
                "Капитанская дочка",
                "author",
                db.objFind(Author.class,4),
                Book.class);//меняем название книги автора с id=4
        db.myQueryAllEntries(Book.class,"Book");//выводит на экран все запси(книги) таблицы 'books'
        db.myQueryUpdate("price",
                2200,
                "author",
                db.objFind(Author.class,2),
                "title",
                "Война и мир",
                Book.class);//устанавливаем новое значение 'price'=2200 для книги автора "Л.Н. Толстой", название "Война и мир"
        db.myQueryAllEntries(Book.class,"Book");//выводит на экран все запси(книги) таблицы 'books'
    }

    private static DB createDB(){
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
        return dbLibrary;
    }
}