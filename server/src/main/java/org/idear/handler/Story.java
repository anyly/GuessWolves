package org.idear.handler;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * 故事, 分为多章节
 */
public class Story {
    private LinkedList<Chapter> chapters = new LinkedList<>();
    private LinkedList<String> keys = new LinkedList<>();
    private HashSet<String> pass = new HashSet<>();

    public Story addChapter(String key, Chapter chapter) {
        keys.add(key);
        chapters.add(chapter);
        return this;
    }

    public Story addChapter(String key) {
        return addChapter(key, null);
    }

    /**
     * 开始表演
     * @return
     */
    public String action() {
        Chapter chapter = null;
        String key = null;
        while ((chapter = chapters.getFirst()) != null) {
            key = keys.getFirst();
            if (chapter.perform()) {
                System.out.println("行动["+key+"]  >[下一步]");
                poll();
            } else {
                System.out.println("行动["+key+"]  =[暂停]");
                return keys.getFirst();
            }
        }
        return null;
    }

    /**
     * 下一章节开始
     * @return
     */
    public String next() {
        poll();
        return action();
    }

    /**
     * 如果当前是指定阶段则进入下一阶段
     * @param key
     * @return
     */
    public String focus(String key) {
        String key1 = keys.getFirst();
        if (key1.equals(key)) {
            poll();
            return action();
        }
        return null;
    }

    /**
     * 当前阶段
     * @return
     */
    public String chapter() {
        return keys.getFirst();
    }

    private void poll() {
        chapters.poll();
        String key = keys.poll();
        if (key != null) {
            pass.add(key);
        }
    }
}
