package org.duracloud.duradmin.util;

import java.util.List;

import org.duracloud.common.util.Scrollable;



public abstract class ScrollableList<E> implements Scrollable<E>{
    private long resultCount;
    
    private long firstResultIndex = -1;
    
    private int maxResultsPerPage = 10;
    
    private List<E> resultList;

    private boolean markedForUpdate = true;
    
    public int getMaxResultsPerPage() {
        return this.maxResultsPerPage;
    }

    public long getResultCount() {
        update();
        return this.resultCount;
    }

    public List<E> getResultList() {
        update();
        return this.resultList;
    }

    public void setFirstResultIndex(long index) throws IndexOutOfBoundsException {
        if(index >= resultCount){
            throw new IndexOutOfBoundsException("index (" + index + ") must be less than "+ resultCount);
        }

        if(this.firstResultIndex != index){
            this.firstResultIndex = index;
            markedForUpdate = true;
        }

    }

    public void setMaxResultsPerPage(int maxResultsPerPage) {
        if(this.maxResultsPerPage != maxResultsPerPage){
            this.firstResultIndex = 0;
            this.maxResultsPerPage = maxResultsPerPage;
            markedForUpdate = true;
        }
    }

    public long getFirstResultIndex() {
        return this.firstResultIndex;
    }
    
    public long getFirstDisplayIndex(){
        return getFirstResultIndex()+1;
    }
    
    public long getLastDisplayIndex(){
        update();
        return getFirstResultIndex()+getResultList().size();
    }
    
    private void update(){
        if(markedForUpdate){
            try {
                updateList();
            } catch (DataRetrievalException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public long getLastPageStartingIndex(){
       long lastPageSize = (this.resultCount % this.maxResultsPerPage);
       return this.resultCount - (lastPageSize > 0 ? lastPageSize : this.maxResultsPerPage);
    }
    
    public Object getFilterParameters(){
        return null;
    }
    
    protected abstract void updateList() throws DataRetrievalException;
    
    protected void update(long resultCount, List<E> resultList) {
       this.resultCount = resultCount;
       if(this.firstResultIndex < 0){
           this.firstResultIndex = 0;
       }
       
       int lastIndex = Math.min(this.maxResultsPerPage, resultList.size());
       this.resultList = resultList.subList(0, lastIndex);
       markedForUpdate = false;
    }
}
