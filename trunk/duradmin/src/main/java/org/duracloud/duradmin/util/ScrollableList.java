
package org.duracloud.duradmin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class ScrollableList<E>
        implements Scrollable<E> {

    private int maxResultsPerPage = 10;

    private List<E> resultList;

    private boolean markedForUpdate = true;

    private E currentMarker = null;

    /**
     * The markers represent the last item in each previous "page" of results.
     * The last element in the list refers to the marker for the current page.
     */
    private Queue<E> markers = new LinkedList<E>();

    public int getMaxResultsPerPage() {
        return this.maxResultsPerPage;
    }

    public void markForUpdate(){
        this.markedForUpdate = true;
    }
    
    public void setMaxResultsPerPage(int maxResults) {
        if (this.maxResultsPerPage != maxResults) {
            markedForUpdate = true;
            this.maxResultsPerPage = maxResults;
        }
    }

    private E getLastResultInCurrentList() {
        List<E> results = this.resultList;
        if (results.size() > 0) {
            return results.get(results.size() - 1);
        } else {
            return null;
        }

    }

    public void next() {
        if (!isNextAvailable()) {
            return;
        }
        //put curent marker in the previous queue.
        E previousMarker = this.currentMarker;
        if (previousMarker != null) {
            this.markers.add(previousMarker);
        }

        //set the currentMarker
        this.currentMarker = getLastResultInCurrentList();
        //flag for update.
        markedForUpdate = true;

        try {

            update();
        } catch (DataRetrievalException ex) {
            //rollback state
            if (previousMarker != null) {
                this.markers.remove();
            }
            this.currentMarker = previousMarker;
            throw new RuntimeException(ex);
        }

    }

    public void first() {
        this.markers.clear();
        this.currentMarker = null;
        this.markedForUpdate = true;
    }

    public void previous() {
        if (isPreviousAvailable()) {
            //pop the "current" marker off the queue
            this.markers.remove();
            if (this.markers.size() > 0) {
                this.currentMarker = this.markers.remove();
            } else {
                this.currentMarker = null;
            }

            this.markedForUpdate = true;
        }
    }

    public boolean isPreviousAvailable() {
        return this.markers.size() > 0;
    }

    public boolean isNextAvailable() {
        try {
            update();
            return getResultList().size() >= this.maxResultsPerPage;
        } catch (DataRetrievalException e) {
            throw new RuntimeException(e);
        }
    }

    protected E getCurrentMarker() {
        return this.currentMarker;
    }

    public List<E> getResultList() {
        try {
            update();
            return this.resultList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final void update() throws DataRetrievalException {
        if (markedForUpdate) {
            this.resultList = getData();
            this.currentMarker = getLastResultInCurrentList();
            markedForUpdate = false;
        }
    }

    protected abstract List<E> getData() throws DataRetrievalException;

}
