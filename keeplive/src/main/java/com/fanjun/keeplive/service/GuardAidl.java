//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fanjun.keeplive.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface GuardAidl extends IInterface {
    void wakeUp(String var1, String var2, int var3) throws RemoteException;

    public abstract static class Stub extends Binder implements GuardAidl {
        private static final String DESCRIPTOR = "com.fanjun.keeplive.service.GuardAidl";
        static final int TRANSACTION_wakeUp = 1;

        public Stub() {
            this.attachInterface(this, "com.fanjun.keeplive.service.GuardAidl");
        }

        public static GuardAidl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("com.fanjun.keeplive.service.GuardAidl");
                return (GuardAidl)(iin != null && iin instanceof GuardAidl ? (GuardAidl)iin : new GuardAidl.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = "com.fanjun.keeplive.service.GuardAidl";
            switch(code) {
                case 1:
                    data.enforceInterface(descriptor);
                    String _arg0 = data.readString();
                    String _arg1 = data.readString();
                    int _arg2 = data.readInt();
                    this.wakeUp(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(descriptor);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements GuardAidl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "com.fanjun.keeplive.service.GuardAidl";
            }

            public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("com.fanjun.keeplive.service.GuardAidl");
                    _data.writeString(title);
                    _data.writeString(discription);
                    _data.writeInt(iconRes);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }
        }
    }
}
