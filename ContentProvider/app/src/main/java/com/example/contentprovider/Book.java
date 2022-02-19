package com.example.contentprovider;
import android.os.Parcel;
import android.os.Parcelable;


public class Book implements Parcelable{
    public int bookId;
    public String bookName;
    public Book(){

    }
    public Book(int bookId,String bookName){
        this.bookId=bookId;
        this.bookName=bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }
    public static final Creator<Book>CREATOR=new Creator<Book>(){

        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };
    private Book(Parcel in){
        bookId=in.readInt();
        bookName=in.readString();
    }
    public String getBookName(){
        return  this.bookName;
    }
    public int getBooId(){
        return this.bookId;
    }
}
