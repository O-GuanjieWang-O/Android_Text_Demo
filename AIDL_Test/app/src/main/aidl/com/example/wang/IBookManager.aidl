package com.example.wang;

import  com.example.wang.Book;
import com.example.wang.IOnNewBookArrivedListener;
interface IBookManager {
     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(IOnNewBookArrivedListener listener);
     void unregisterListener(IOnNewBookArrivedListener listener);
}