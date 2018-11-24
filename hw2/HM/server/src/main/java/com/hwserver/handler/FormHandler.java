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

package com.hwserver.handler;

import com.hwserver.db.DB;
import com.hwserver.entity.User;
import com.j256.ormlite.dao.Dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spark.Request;
import spark.Response;
import spark.Route;

import static com.j256.ormlite.dao.DaoManager.createDao;

public class FormHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Map<String, Object> result = new HashMap<>();

        addCustomHeaders(request, result);

        addQueryParams(request, result);

        if (ServletFileUpload.isMultipartContent(request.raw())) {
            ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory(8, new File("./Data")));
            List<FileItem> fileItems = fileUpload.parseRequest(request.raw());
        }
        return result;
    }
    private void addCustomHeaders(Request request, Map<String, Object> result) {
        String[] normalHeaders = new String[]{"Accept-Encoding", "Connection", "Content-Length", "Content-Type", "Host", "User-Agent"};
        Set<String> headers = request.headers();
        for (String s : normalHeaders) {
            headers.remove(s);
        }
        if (!headers.isEmpty()) {
            Map<String, String> customHeaderMap = new HashMap<>();
            for (String header : headers) {
                customHeaderMap.put(header, request.headers(header));
            }
            result.put("_customHeader", customHeaderMap);
        }
    }


    private void addQueryParams(Request request, Map<String, Object> result) {
        Map<String, String[]> queryParamsMap = request.queryMap().toMap();
        int i = 0;
        String []info = new String[2];
        for(String[] s:queryParamsMap.values()){
            info[i++]=s[0];
        }
        List<User> users = new ArrayList<User>();
        User user = null;
        try {
            users = getDao().queryForEq("username",info[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(users.size()>0){
            for(User u:users){
                if(u.psd.equals(info[0])) {
                    user = u;
                    result.put("user", user);
                    return ;
                }
            }

            result.put("user", user);
            System.out.println("not");
        }
    }

    public Dao<User, Long> getDao() throws SQLException {
        return createDao(DB.getConnectionSource(), User.class);
    }


}
