package org.duracloud.duradmin.web.util;


public class Page {
    public Page(long number, long firstResultIndex, boolean current) {
        super();
        this.number = number;
        this.firstResultIndex = firstResultIndex;
        this.current = current;
    }

    private long number;
    private long firstResultIndex;
    private boolean current;
    
    public long getNumber() {
        return number;
    }
    
    public long getFirstResultIndex() {
        return firstResultIndex;
    }
    
    public boolean isCurrent(){
        return this.current;
    }
    
}
