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
                        if (callback != null) {
                            callback.execute();
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

    public void add(Callback callback){
        synchronized(thread) {
            callbacks.add(callback);
            //thread.notifyAll();
        }
    }

    public Handler() {
        thread.start();
    }
}
