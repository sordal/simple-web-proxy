package scu.edu;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        try (ServerSocket listener = new ServerSocket(8080)) {
            HttpResponseCache httpResponseCache = new HttpResponseCache();
            while (true) {
                String incomingRequest;

                try (Socket socket = listener.accept()) {

                    System.out.printf("\n\r========== Started Request ==========\n\r");

                    // get object input from socket
                    //
                    try (InputStream input = socket.getInputStream()) {
                        // create reader from input
                        //
                        try (InputStreamReader inputStreamReader = new InputStreamReader(input)) {
                            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                String line;

                                while (bufferedReader.ready() && (line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line).append("\r\n");
                                }
                                incomingRequest = stringBuilder.toString();

                                // OK output our Request:
                                System.out.printf("Incoming Request: %s\n\r", incomingRequest);

                                if (incomingRequest.length() > 0)
                                {
                                    // Need parse HTTP request.
                                    HttpRequestParser httpRequestParser = new HttpRequestParser();
                                    httpRequestParser.parseRequest(incomingRequest);

                                    byte[] response;

                                        // Check the Cache
                                    if (httpResponseCache.isInCache(httpRequestParser) && !httpResponseCache.isModifiedSince(httpRequestParser))
                                    {
                                        response = httpResponseCache.getCachedResponse(httpRequestParser);
                                    }
                                    else {
                                        // Forward request
                                        HttpRequestForwarder httpRequestForwarder = new HttpRequestForwarder();
                                        response = httpRequestForwarder.forwardRequest(httpRequestParser);

                                        httpResponseCache.store(httpRequestParser, response);
                                    }

                                    // Write Response back to client.
                                    OutputStream outputStream = socket.getOutputStream();
                                    outputStream.write(response);
                                    outputStream.flush();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.printf("\n\r========== Finished Request ==========\n\r");
                }
            }
        }
    }
}
