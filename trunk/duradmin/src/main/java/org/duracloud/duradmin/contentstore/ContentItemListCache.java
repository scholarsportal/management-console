package org.duracloud.duradmin.contentstore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class ContentItemListCache {
    public static ContentItemList get(HttpServletRequest request,
                                                     String spaceId, ContentStoreProvider contentStoreProvider) {
              HttpSession session = request.getSession();
              ContentItemList list =
                      (ContentItemList) session.getAttribute("content-list-"
                              + spaceId);
              if (list == null) {
                  list = new ContentItemList(spaceId, contentStoreProvider);
                  session.setAttribute("content-list-" + spaceId, list);
              }
              return list;
          }
}
