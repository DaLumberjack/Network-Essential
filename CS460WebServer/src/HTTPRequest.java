
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HTTPRequest {                                                      // defines the HTTPRequest class
 
        private final String raw;                                               // creates the raw string
        private String method;                                                  // creates the method string
        private String location;                                                // creates the location string
        private String version;                                                 // creates the version string
        private Map<String, String> headers = new HashMap<String, String>();    // creates the headers
 
        public HTTPRequest(String raw) {                                        // defines the HTTPRequest constructor
            this.raw = raw;                                                     // sets the raw for this request
            parse();                                                            // parses through the request
        }
 
        private void parse() {                                                  // defines the parse method
            // parse the first line
            StringTokenizer tokenizer = new StringTokenizer(raw);               // creates a new tokenizer
            method = tokenizer.nextToken().toUpperCase();                       // sets method to the next token and capitalizes it
            location = tokenizer.nextToken();                                   // sets location to the next token
            version = tokenizer.nextToken();                                    // sets version to the next token
            // parse the headers
            String[] lines = raw.split("\r\n");                                 // creates an array of strings made from raw
            for (int i = 1; i < lines.length; i++) {                            // executes for each line
                String[] keyVal = lines[i].split(":", 2);                       // creates an array of strings made from those lines
                headers.put(keyVal[0], keyVal[1]);                              // puts the values from that array into headers
            }
        }
 
        public String getMethod() {                                             // defines the getMethod method
            return method;                                                      // returns the method
        }
 
        public String getLocation() {                                           // defines the getLocation method
            return location;                                                    // returns the location
        }
 
        public String getHead(String key) {                                     // defines the getHead method
            return headers.get(key);                                            // returns the keys of the headers
        }
    }