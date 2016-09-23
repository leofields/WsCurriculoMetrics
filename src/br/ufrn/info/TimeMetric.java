package br.ufrn.info;

import java.util.Hashtable;

/**
 * @author Leonardo René (leonardo@info.ufrn.br)
 * @since 23/09/2016
 */
public class TimeMetric {

    private static TimeMetric instance = null;

    private Hashtable<Operations, Long> table;

    protected TimeMetric(){
        table = new Hashtable<>();
    }

    public static TimeMetric getInstance(){
        if(instance == null) {
            instance = new TimeMetric();
        }
        return instance;
    }

    public synchronized void addTime(Operations op, Long time) {
        if(table.get(op) == null) {
            table.put(op, time);
        } else {
            table.put(op, table.get(op) + time);
        }
    }

    public Long getTime(Operations op) {
        return  table.get(op);
    }
}
