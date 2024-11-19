package com.pro.shopfee.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pro.shopfee.model.Drink;

@Database(entities = {Drink.class}, version = 1)
public abstract class DrinkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "drink.db";

    // Biến static kiểu DrinkDatabase để lưu trữ instance duy nhất của lớp (Singleton pattern).
    private static DrinkDatabase instance;

    public static synchronized DrinkDatabase getInstance(Context context) {
        if (instance == null) {
            // Nếu instance chưa tồn tại, nó sẽ được tạo bằng Room.databaseBuilder().
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DrinkDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries() // Cho phép thực hiện các truy vấn trên luồng chính
                    .build();
        }
        return instance;
    }

    public abstract DrinkDAO drinkDAO();
}
