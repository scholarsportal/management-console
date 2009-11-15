package org.duracloud.duradmin.util;

import java.util.List;

/**
 * A simple interface for lists supporting scrollable behavior
 * result sets. 
 *
 * @author Danny Bernstein
 * @version $Id$
 */
public interface Scrollable<E> {
    

    /**
     * The max number of results per page; 
     * ie the page size.
     * @return
     */
    public int getMaxResultsPerPage();

    public void setMaxResultsPerPage(int maxResults); 

    public boolean isPreviousAvailable();

    public void first();

    public void previous();
    /**
     * Returns the results for the current "page"
     * @return
     */
    public List<E> getResultList();
}
