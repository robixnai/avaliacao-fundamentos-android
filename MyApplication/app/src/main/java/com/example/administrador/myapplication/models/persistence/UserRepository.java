package com.example.administrador.myapplication.models.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrador.myapplication.models.entities.User;
import com.example.administrador.myapplication.util.AppUtil;

/**
 * Created by robson on 31/05/15.
 */
public class UserRepository {

    private static class Singleton {
        public static final UserRepository INSTANCE = new UserRepository();
    }

    private UserRepository() {
        super();
    }

    public static UserRepository getInstance() {
        return Singleton.INSTANCE;
    }

    public User getUser(String name, String password) {
        String where = UserContract.NAME + " = ? AND " + UserContract.PASSWORD + " = ? ";
        String[] args = {name, password};

        DatabaseHelper helper = new DatabaseHelper(AppUtil.CONTEXT);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(UserContract.TABLE, UserContract.COLUNS, where, args, null, null, null);
        User user = UserContract.bind(cursor);
        db.close();
        helper.close();
        return user;
    }

}
