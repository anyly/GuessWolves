package org.idear.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 操作日志
 */
public class Log {
    /**
     * 详细描述
     */
    private String description;
    /**
     * 摘要信息
     */
    private String summary;
    /**
     * 视角
     */
    private List<Map<Integer, String>> viewports = new LinkedList<>();


}
