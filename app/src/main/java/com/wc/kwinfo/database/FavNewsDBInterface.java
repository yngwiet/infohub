package com.wc.kwinfo.database;

/**
 * Created by wang on 2015/11/22.
 */
public interface FavNewsDBInterface {
    void getFavNewsFromDB();
    void addFavNewsToDB(int newsPos);
    void removeFavNewsFromDB();
}
