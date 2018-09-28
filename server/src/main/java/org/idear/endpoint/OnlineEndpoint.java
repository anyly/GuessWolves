package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import org.idear.util.StringUtil;

import javax.websocket.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by idear on 2018/9/21.
 */
public abstract class OnlineEndpoint {

    protected static CopyOnWriteArraySet<Session> clientSessions = new CopyOnWriteArraySet<>();

    protected Session session;

    final protected JSONObject messageHandler(Session session, String action, JSONObject data) {
        //Method method = null;
        String methodName = "on"+ StringUtil.toUpperCaseFirstOne(action);
        try {
            Class thisClass = this.getClass();
            Method[] methods = thisClass.getMethods();
            for (Method method : methods) {
                if (methodName.equals(method.getName())) {// 方法名一致
                    Class returnType = method.getReturnType();
                    if (returnType != null && (
                            returnType.isAssignableFrom(Array.class) ||
                            returnType.isAssignableFrom(Set.class) ||
                            returnType.isAssignableFrom(List.class))) {
                        throw new ClassCastException(methodName+"() return type is not allow Array/Set/List");
                    }
                    /*
                    Class[] parameterTypes = method.getParameterTypes();
                    Set set = new HashSet();
                    ArrayList parameter = new ArrayList(parameterTypes.length);
                    for (Class parameterType : parameterTypes) {
                        if (!set.contains(session) &&
                                (session.getClass() == parameterType
                                || parameterType.isAssignableFrom(session.getClass()))) {
                            parameter.add(session);
                            set.add(session);
                        } else if (!set.contains(action) &&
                                (action.getClass() == parameterType
                                || parameterType.isAssignableFrom(action.getClass()))) {
                            parameter.add(action);
                            set.add(action);
                        } else if (!set.contains(data) &&
                                (data.getClass() == parameterType
                                || parameterType.isAssignableFrom(data.getClass()))) {
                            parameter.add(data);
                            set.add(data);
                        } else {
                            parameter.add(null);
                        }
                    }
                    */

                    // 反射不支持不定数组
                    //Object returnObject = method.invoke(this, session, data);
                    Object returnObject = method.invoke(this, data);
                    if (returnObject == null) {
                        return null;
                    } else if (returnObject instanceof JSONObject) {
                        return (JSONObject) returnObject;
                    } else {
                        String jsonString = JSONObject.toJSONString(returnObject);
                        return (JSONObject) JSONObject.parse(jsonString);
                    }
                }

            }

        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("找不到"+methodName+"()方法！");
        return null;
    }

    protected JSONObject httpResponse(Session session, String action, JSONObject data) {
        return null;
    }

    protected void wsReceive(Session session, String action, JSONObject data) {

    }

    public void onOpen(Session session, EndpointConfig config) {
        clientSessions.add(session);
        this.session = session;
        System.out.println("建立连接"+session.getId());
//        session.addMessageHandler(new MessageHandler.Whole<String>() {
//            @Override
//            public void onMessage(String message) {
//
//            }
//        });
    }

    public void onMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String action = jsonObject.getString("action");
        //String datatype = jsonObject.getString("datatype");
        JSONObject data = jsonObject.getJSONObject("data");
        String httpPrefix = "http_";
        if (action.startsWith(httpPrefix)) {
            // http请求
            String newAction = action.substring(httpPrefix.length());
            JSONObject response = messageHandler(session, newAction, data);
            if (response == null) {
                response = httpResponse(session, newAction, data);
            }
            emit(session, action, response);
        } else {
            // admit请求
            messageHandler(session, action, data);
            wsReceive(session, action, jsonObject);
        }
    }

    public void onClose(CloseReason closeReason) {
        clientSessions.remove(session);
        System.out.println("断开连接"+session.getId()+" : "+closeReason.getReasonPhrase());
        session = null;
    }

    public void onError(Session session, Throwable error){
        System.out.println(session.getId()+": 发生错误");
        error.printStackTrace();
    }

    /**
     * 当前端传输
     * @param action
     * @param data
     */
    final public void emit(String action, JSONObject data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);
        try {
            session.getBasicRemote().sendText(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 指定某一端传输
     * @param session
     * @param action
     * @param data
     */
    final public void emit(Session session, String action, JSONObject data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);
        try {
            session.getBasicRemote().sendText(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 广播所有在线连接
     * @param action
     * @param data
     */
    final public void emitAll(String action, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);

        LinkedList<Session> list = new LinkedList(clientSessions);
        for (Session session: list) {
            try {
                session.getBasicRemote().sendText(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //this.session.getAsyncRemote().sendText(message);
        }
    }
}
