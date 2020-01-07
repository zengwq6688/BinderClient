package com.aidl.binderclientapp;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IBookManager extends IInterface {
    List<Book> getBooks() throws RemoteException;

    void addBook(Book book) throws RemoteException;
}
