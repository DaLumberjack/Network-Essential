package CS460NonBlcokingServer;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPResponse {                                                     // defines the HTTPResponse class
 
        private String version = "HTTP/1.1";                                    // initilizes the version to HTTP/1.1
        private int responseCode = 200;                                         // initializes the responseCode to 200
        private String responseReason = "OK";                                   // initializes the responseReason to OK
        private Map<String, String> headers = new LinkedHashMap<String, String>(); // creates the headers
        private byte[] content;                                                 // initlizes the content of the response to a byte array
 
        private void addDefaultHeaders() {                                      // defines the addDefaultHeaders method
            headers.put("Date", new Date().toString());                         // puts the date into the headers
            headers.put("Server", "Java NIO Webserver by md_5");                // puts the server name into the headers
            headers.put("Connection", "close");                                 // puts the status of the connection into the headers
            headers.put("Content-Length", Integer.toString(content.length));    // puts the length of the content into the headers
        }
 
        public int getResponseCode() {                                          // defines the getResponseCode method
            return responseCode;                                                // returns the responseCode
        }
 
        public String getResponseReason() {                                     // defines the getResponseReason method
            return responseReason;                                              // returns the responseReason
        }
 
        public String getHeader(String header) {                                // defines the getHeader method
            return headers.get(header);                                         // returns the headers
        }
 
        public byte[] getContent() {                                            // defines the getContent method
            return content;                                                     // returns the content
        }
 
        public void setResponseCode(int responseCode) {                         // defines the setResponseCode method
            this.responseCode = responseCode;                                   // sets the responseCode
        }
 
        public void setResponseReason(String responseReason) {                  // defines the setResponseReason method
            this.responseReason = responseReason;                               // sets the responseReason
        }
 
        public void setContent(byte[] content) {                                // defines the setContent method
            this.content = content;                                             // sets the content
        }
 
        public void setHeader(String key, String value) {                       // defines the setHeader method
            headers.put(key, value);                                            // sets the headers
        }
    }