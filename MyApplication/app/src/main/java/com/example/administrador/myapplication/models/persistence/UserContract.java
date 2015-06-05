package com.example.administrador.myapplication.models.persistence;

import android.database.Cursor;

import com.example.administrador.myapplication.models.entities.User;

/**
 * Created by robson on 31/05/15.
 */
public class UserContract {

    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";

    public static final String[] COLUNS = {ID, NAME, PASSWORD};

    public static String createTable() {
        final StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE ");
        sql.append(TABLE);
        sql.append(" ( ");
        sql.append(ID + " INTEGER PRIMARY KEY, ");
        sql.append(NAME + " TEXT, ");
        sql.append(PASSWORD + " TEXT ");
        sql.append(" ); ");
        return sql.toString();
    }

    public static String insertUser() {
        final StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO ");
        sql.append(TABLE);
        sql.append(" ( ");
        sql.append(ID + ", ");
        sql.append(NAME + ", ");
        sql.append(PASSWORD);
        sql.append(" ) ");
        sql.append(" VALUES( ");
        sql.append(1 + ", ");
        sql.append("'admin', 'admin'");
        sql.append(" ) ");
        return sql.toString();
    }

    public static User bind(Cursor cursor) {
        if (!cursor.isBeforeFirst() || cursor.moveToNext()) {
            User user = new User();
            user.setId((cursor.getInt(cursor.getColumnIndex(ID))));
            user.setUser(cursor.getString(cursor.getColumnIndex(NAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
            return user;
        }
        return null;
    }

}
