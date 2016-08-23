package scu.edu;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

public class HttpRequestForwarder {
    public byte[] forwardRequest(HttpRequestParser httpRequestParser) {

        byte[] response = new byte[0];


        try {
            // convert string to URL
            URL url = new URL(httpRequestParser.getRequestPath());

            // create socket for url with and without SSL for http and https connections
            // port 443 needed for HTTPS connections

            Socket socket;
            switch (url.getProtocol()) {
                case "http":
                    socket = new Socket(url.getHost(), httpRequestParser.getRequestPort());
                    break;
                case "https":
                    socket = SSLSocketFactory.getDefault().createSocket(url.getHost(), httpRequestParser.getRequestPort());
                    break;
                default:
                    return response;
            }

            // get object into output stream
            OutputStream output = socket.getOutputStream();
            byte[] bytes = httpRequestParser.getOriginalRequest().getBytes();
            System.out.println(Arrays.toString(bytes));

            // Output the Response.
            output.write(bytes);
            output.flush();

            try (InputStream input = socket.getInputStream()) {
                // create reader from input
                //
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = input.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                response = buffer.toByteArray();
            }

            socket.close();
        }

        catch (IOException e) {
            // handles bad URLs and timeouts etc.
            System.out.println(e.getLocalizedMessage());
        }

        return response;
    }
}
