package scu.edu;


import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class HttpRequestParser {
    private Hashtable<String, String> requestHeaders;
    private StringBuffer messageBody;
    private ArrayList<String> requestLineElements;
    private String originalRequest;

    private static final int cURL = 1;
    private static final int cMethod = 0;
    private String domain_name;
    private int port_value;

    HttpRequestParser() {
        requestHeaders = new Hashtable<>();
        messageBody = new StringBuffer();
        requestLineElements = new ArrayList<>();

    }

    void parseRequest(String request) throws Exception {
        originalRequest = request;

        BufferedReader reader = new BufferedReader(new StringReader(request));

        setRequestLine(reader.readLine());

        String header = reader.readLine();
        while (header.length() > 0) {
            System.out.printf("Header Found: %s\n\r", header);
            appendHeaderParameter(header);
            header = reader.readLine();
        }

        String bodyLine = reader.readLine();
        while (bodyLine != null) {
            appendMessageBody(bodyLine);
            bodyLine = reader.readLine();
        }
    }

    public String getRequestPath(){
        switch (port_value){
            case 443:
                return "https:" + domain_name;
            default:
                return "http:" + domain_name;
        }
    }

    String getOriginalRequest(){
        return originalRequest;
    }

    int getRequestPort(){
        return port_value;
    }

    private void setRequestLine(String requestLine) {

        StringTokenizer st = new StringTokenizer(requestLine);
        while (st.hasMoreTokens()) {
            requestLineElements.add(st.nextToken());
        }

        StringTokenizer st_port = new StringTokenizer(requestLineElements.get(cURL), ":");

        int count = 0;
        while(st_port.hasMoreTokens()){
            String token = st_port.nextToken();

            // Check for http or https
            if ("https".equals(token)){
                port_value = 443;
            }
            else if ("http".equals(token))
            {
                port_value = 80;
            }
            else
            {
                switch(count){
                    case 0:
                        if (token.startsWith("//"))
                            domain_name = token;
                        else
                            domain_name = "//" + token;
                        break;
                    case 1:
                        port_value = Integer.parseInt(token);
                        break;
                    default:
                        System.out.printf("Unknown Thing:", token);
                }
                count++;

            }
        }
    }

    private void appendHeaderParameter(String header) throws Exception {
        int idx = header.indexOf(":");
        if (idx == -1) {
            throw new Exception("Invalid Header Parameter: " + header);
        }
        requestHeaders.put(header.substring(0, idx), header.substring(idx + 1, header.length()));
    }

    private void appendMessageBody(String bodyLine) {
        messageBody.append(bodyLine).append("\r\n");
    }

    public String getMethod() {
        return requestLineElements.get(cMethod);
    }
}
