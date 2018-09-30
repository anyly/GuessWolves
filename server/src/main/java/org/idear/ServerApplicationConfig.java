package org.idear;

import org.idear.handler.GameCenter;
import org.idear.handler.Handler;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by idear on 2018/9/21.
 */
public class ServerApplicationConfig implements javax.websocket.server.ServerApplicationConfig  {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        GameCenter.loadFor();

        Set<ServerEndpointConfig> result = null;
//        for (Class<? extends Endpoint> cls:endpointClasses) {
//            boolean isAbstract = Modifier.isAbstract(cls.getModifiers());
//            if (!isAbstract) {
//                String path = cls.getSimpleName();
//                if(Character.isUpperCase(path.charAt(0))) {
//                    path = (new StringBuilder()).append(Character.toLowerCase(path.charAt(0))).append(path.substring(1)).toString();
//                }
//                ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(cls, "/"+path).build();
//                result.add(serverEndpointConfig);
//            }
//        }
        return result;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // 过滤注视类
        return scanned;
    }
}
