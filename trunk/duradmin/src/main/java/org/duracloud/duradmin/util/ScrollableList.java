package org.duracloud.duradmin.util;

import java.util.List;

import org.duracloud.common.util.Scrollable;



public abstract class ScrollableList<E> implements Scrollable<E>, UpdatableList<E>{
    private long resultCount;
    
    private long firstResultIndex = -1;
    
    private int maxResultsPerPage;
    
    private List<E> resultList;

    private boolean markedForUpdate;
    
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
            markedForUpdate = true;
        }

    }

    public void setMaxResultsPerPage(int maxResultsPerPage) {
        if(this.maxResultsPerPage != maxResultsPerPage){
            markedForUpdate = true;
        }
        this.maxResultsPerPage = maxResultsPerPage;
    }

    public long getFirstResultIndex() {
        update();
        return this.firstResultIndex;
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
    
    public Object getFilterParameters(){
        return null;
    }
    
    protected abstract void updateList() throws DataRetrievalException;
    
    public void update(long resultCount, List<E> resultList) {
       this.resultCount = resultCount;
       this.resultList = resultList;
       markedForUpdate = false;
    }
}
