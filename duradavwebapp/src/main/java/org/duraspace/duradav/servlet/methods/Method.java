package org.duraspace.duradav.servlet.methods;

public enum Method {

    COPY      ("COPY",      new CopyHandler(),      true),
    DELETE    ("DELETE",    new DeleteHandler(),    true),
    GET       ("GET",       new GetHandler(),       true),
    HEAD      ("HEAD",      new HeadHandler(),      true),
    LOCK      ("LOCK",      new LockHandler(),      true),
    MKCOL     ("MKCOL",     new MkColHandler(),     false),
    MOVE      ("MOVE",      new MoveHandler(),      true),
    OPTIONS   ("OPTIONS",   new OptionsHandler(),   true),
    PROPFIND  ("PROPFIND",  new PropFindHandler(),  true),
    PROPPATCH ("PROPPATCH", new PropPatchHandler(), true),
    PUT       ("PUT",       new PutHandler(),       false),
    UNLOCK    ("UNLOCK",    new UnlockHandler(),    true);

    private final String name;

    private final MethodHandler handler;

    private final boolean requiresExistingResource;

    Method(String name,
           MethodHandler handler,
           boolean requiresExistingResource) {
        this.name = name;
        this.handler = handler;
        this.requiresExistingResource = requiresExistingResource;
    }

    public String getName() {
        return name;
    }

    public MethodHandler getHandler() {
        return handler;
    }

    public boolean requiresExistingResource() {
        return requiresExistingResource;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Method fromName(String name) {
        for (Method method : values()) {
            if (method.getName().equals(name)) return method;
        }
        return null;
    }

}