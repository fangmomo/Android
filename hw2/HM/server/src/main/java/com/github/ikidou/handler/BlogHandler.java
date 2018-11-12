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

package com.github.ikidou.handler;

import com.github.ikidou.Resp;
import com.github.ikidou.db.DB;
import com.github.ikidou.entity.User;
import com.github.ikidou.util.StringUtils;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import static com.j256.ormlite.dao.DaoManager.createDao;

public enum BlogHandler implements Route {
    POST {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            Gson gson = new Gson();
            User user = gson.fromJson(request.body(), User.class);
            System.out.println(user.toString());
            String field = null;
            if (StringUtils.isEmpty(user.psd)) {
                field = "psd";
            } else if (StringUtils.isEmpty(user.username)) {
                field = "username";
            } else if (StringUtils.isEmpty(user.email)) {
                field = "email";
            }
            if (field != null) {
                return Resp.create(400, " `" + field + "` is empty!");
            } else {
               getDao().create(user);
                return Resp.create(200, "OK", user);
            }
        }
    };
    public Dao<User, Long> getDao() throws SQLException {
        return createDao(DB.getConnectionSource(), User.class);
    }
}
