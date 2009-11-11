package org.duracloud.duradmin.util;

import java.util.List;


public interface UpdatableList<E> {
    public void update(long resultCount, List<E> resultList);
    public long getFirstResultIndex();
    public Object getFilterParameters();
}
