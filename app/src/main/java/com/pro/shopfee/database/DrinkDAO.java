package com.pro.shopfee.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pro.shopfee.model.Drink;

import java.util.List;

@Dao
public interface DrinkDAO {

    @Insert
    void insertDrink(Drink drink);

    @Query("SELECT * FROM drink")
    List<Drink> getListDrinkCart();

    @Query("SELECT * FROM drink WHERE id=:id")
    List<Drink> checkDrinkInCart(long id);

    @Delete
    void deleteDrink(Drink drink);

    @Update
    void updateDrink(Drink drink);

    @Query("DELETE from drink")
    void deleteAllDrink();
}
