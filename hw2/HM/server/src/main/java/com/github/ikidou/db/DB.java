
/*
 * Copyright 2016 ikidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ikidou.db;

import com.github.ikidou.entity.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.SqliteDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class
DB {
    public static ConnectionSource getConnectionSource() throws SQLException {
        return new JdbcConnectionSource("jdbc:sqlite:Data/user.db", new SqliteDatabaseType());
    }
    public static void init() {
        File dbFile = new File("Data/user.db");
        ConnectionSource source = null;
        try {
            source = DB.getConnectionSource();
            if (!dbFile.exists()) {
                File parentFile = dbFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdir();
                }
                TableUtils.createTable(source, User.class);
                List<User> UserList = generateDefaultUserList();
                Dao<User, Long> UserDao = DaoManager.createDao(source, User.class);
                DatabaseConnection databaseConnection = UserDao.startThreadConnection();
                boolean autoCommit = databaseConnection.isAutoCommit();
                databaseConnection.setAutoCommit(false);
                for (User User : UserList) {
                    UserDao.create(User);
                }
                UserDao.endThreadConnection(databaseConnection);
                UserDao.commit(databaseConnection);
                databaseConnection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<User> generateDefaultUserList() {
        List<User> UserList = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            User User = new User();
            User.username = "xiaofang"+i;
            User.psd = "1234" + (i + 1);
            User.email = "1149160879.qq" + (i + 1);
            UserList.add(User);
        }
        return UserList;
    }
}
