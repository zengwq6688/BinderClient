package com.aidl.binderclientapp;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public abstract class Stub extends Binder implements IBookManager {
//    private static final String DESCRIPTOR = "com.aidl.binderserverapp.service.IBookManager";
    private static final String DESCRIPTOR = "com.aidl.binderclientapp.IBookManager";


    public static IBookManager asInterface(IBinder binder) {

        if (binder == null)
            return null;
        //本地调用
        IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
        if (iin != null && iin instanceof IBookManager)
            return (IBookManager) iin;
        //非本地  所以这个IBookManager 不是远程的那个，只是代理实现功能的类而已
        return new Proxy(binder);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {

            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;

            case TRANSAVTION_getBooks:
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBooks();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;

            case TRANSAVTION_addBook:
                data.enforceInterface(DESCRIPTOR);
                Book arg0 = null;
                if (data.readInt() != 0) {
                    arg0 = Book.CREATOR.createFromParcel(data);
                }
                this.addBook(arg0);
                reply.writeNoException();
                return true;

        }
        return super.onTransact(code, data, reply, flags);
    }

    public static final int TRANSAVTION_getBooks = IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSAVTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;




    static class Proxy implements IBookManager {

        //其实是stub 对象 即也是binder对象
        private IBinder remote;

        public Proxy(IBinder remote) {

            this.remote = remote;
        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public List<Book> getBooks() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel replay = Parcel.obtain();
            List<Book> result;

            try {
                data.writeInterfaceToken(DESCRIPTOR);
                remote.transact(Stub.TRANSAVTION_getBooks, data, replay, 0);
                replay.readException();
                result = replay.createTypedArrayList(Book.CREATOR);
            } finally {
                replay.recycle();
                data.recycle();
            }
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.e("addBook", "has person add book");
            Parcel data = Parcel.obtain();
            Parcel replay = Parcel.obtain();

            try {
                data.writeInterfaceToken(DESCRIPTOR);
                if (book != null) {
                    data.writeInt(1);
                    book.writeToParcel(data, 0);
                } else {
                    data.writeInt(0);
                }
                remote.transact(Stub.TRANSAVTION_addBook, data, replay, 0);
                replay.readException();
            } finally {
                replay.recycle();
                data.recycle();
            }
        }

        @Override
        public IBinder asBinder() {
            return remote;
        }
    }
}
