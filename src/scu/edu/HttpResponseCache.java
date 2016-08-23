package scu.edu;

import java.util.HashMap;

/**
 * Created by SamuelKelly on 5/30/16.
 */
public class HttpResponseCache {
    HashMap<String, byte[]> cache;

    HttpResponseCache(){
        cache = new HashMap<>();
    }

    public boolean isInCache(HttpRequestParser httpRequestParser) {
        String key = createKey(httpRequestParser);
        return cache.containsKey(key);
    }
    private String createKey(HttpRequestParser httpRequestParser) {
        return httpRequestParser.getRequestPath() + ":" + httpRequestParser.getMethod();
    }
    public boolean isModifiedSince(HttpRequestParser httpRequestParser) {
        String requestPath = httpRequestParser.getRequestPath();
        String method = httpRequestParser.getMethod();
        int requestPort = httpRequestParser.getRequestPort();
        return false;
    }

    public byte[] getCachedResponse(HttpRequestParser httpRequestParser) {
        String key = createKey(httpRequestParser);
        return cache.get(key);
    }

    public void store(HttpRequestParser httpRequestParser, byte[] response) {
        String key = createKey(httpRequestParser);
        cache.put(key,response);
    }
}
