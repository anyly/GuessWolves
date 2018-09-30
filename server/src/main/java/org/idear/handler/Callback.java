package org.idear.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by idear on 2018/9/29.
 */
public interface Callback<T> {
    void execute(T data);
}
