package org.duracloud.common.util;

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
     * The total result count
     * @return
     */
    public long getResultCount();

    /**
     * The max number of results per page; 
     * ie the page size.
     * @return
     */
    public int getMaxResultsPerPage();

    public void setMaxResultsPerPage(int maxResults); 

    /**
     * Sets the starting index for the page.
     * @param index The absolute item index.
     * @throws IndexOutOfBoundsException
     */
    public void setFirstResultIndex(long index) throws IndexOutOfBoundsException;
    
    
    public long getFirstResultIndex();
    
    /**
     * Returns the results for the current "page"
     * @return
     */
    public List<E> getResultList();
}
