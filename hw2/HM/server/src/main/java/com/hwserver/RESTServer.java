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

package com.hwserver;


import com.google.gson.Gson;
import com.hwserver.db.DB;
import com.hwserver.handler.BlogHandler;
import com.hwserver.handler.FormHandler;
import com.hwserver.transformer.GsonTransformer;

import java.sql.SQLException;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

public class RESTServer {
    static final String TYPE = "application/json; charset=UTF-8";

    public static void main(String[] args) throws SQLException {
        DB.init();

        Spark.init();
        Spark.post("/blog", BlogHandler.POST, GsonTransformer.getDefault());
        Spark.post("/form", new FormHandler(), GsonTransformer.getDefault());

        Spark.exception(RuntimeException.class, new ExceptionHandler() {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                Gson gson = new Gson();
                response.body(gson.toJson(Resp.create(400, exception.getMessage())));
            }
        });
        Spark.exception(Exception.class, new ExceptionHandler() {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                Gson gson = new Gson();
                response.body(gson.toJson(Resp.create(500, exception.getMessage())));
            }
        });
    }
}
