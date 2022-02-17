package com.example.wang;

import  com.example.wang.Book;

interface IBookManager {
     List<Book> getBookList();
     void addBook(in Book book);

}