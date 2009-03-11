package org.duraspace.duradav.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.WebdavException;

public interface WebdavHandler {

    void handleGet(HttpServletRequest req,
                   HttpServletResponse resp) throws WebdavException;

    void handleHead(HttpServletRequest req,
                    HttpServletResponse resp) throws WebdavException;

    void handleMove(HttpServletRequest req,
                    HttpServletResponse resp) throws WebdavException;

    void handleMkCol(HttpServletRequest req,
                     HttpServletResponse resp) throws WebdavException;

    void handleOptions(HttpServletRequest req,
                       HttpServletResponse resp) throws WebdavException;

    void handlePut(HttpServletRequest req,
                   HttpServletResponse resp) throws WebdavException;

    void handlePropFind(HttpServletRequest req,
                        HttpServletResponse resp) throws WebdavException;

    void handlePropPatch(HttpServletRequest req,
                         HttpServletResponse resp) throws WebdavException;

}
