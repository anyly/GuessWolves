package org.idear.handler;

import java.util.LinkedList;

/**
 * Created by idear on 2018/9/29.
 */
public class Handler {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Callback callback = null;
            Object data = null;
            for (;;) {
                synchronized (this) {
                    try {
                        callback = callbacks.poll();
                        data = datas.poll();
                        if (callback != null) {
                            callback.execute(data);
                        } else {
                            //this.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    LinkedList<Callback> callbacks = new LinkedList<>();
    LinkedList datas = new LinkedList<>();

    public <T> void add(Callback callback, T data){
        synchronized(thread) {
            callbacks.add(callback);
            datas.add(data);
            //thread.notifyAll();
        }
    }

    public void add(Callback callback) {
        add(callback, null);
    }

    public Handler() {
        thread.start();
    }
}
