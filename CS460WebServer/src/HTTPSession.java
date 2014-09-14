
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public final class HTTPSession {                                                // defines the HTTPSession class
 
        private final SocketChannel channel;                                    // creates the channel
        private final ByteBuffer buffer = ByteBuffer.allocate(2048);            // creates the buffer
        private final StringBuilder readLines = new StringBuilder();            // creates the reader
        private int mark = 0;                                                   // initializes mark to 0
 
        public HTTPSession(SocketChannel channel) {                             // defines the HTTPSession constructor
            this.channel = channel;                                             // sets the channel for the session
        }
 
        /**
         * Try to read a line.
         */
        public String readLine() throws IOException {                           // defines the readLine method
            StringBuilder sb = new StringBuilder();                             // creates a new StringBuilder
            int l = -1;                                                         // initializes an integer l as -1
            while (buffer.hasRemaining()) {                                     // executes while there is space in the buffer
                char c = (char) buffer.get();                                   // initializes a character c to the next character in the buffer
                sb.append(c);                                                   // appends c to the StringBuilder
                if (c == '\n' && l == '\r') {                                   // executes if c is a new line character and l is a return character
                    // mark our position
                    mark = buffer.position();                                   // sets mark to the current position in the buffer
                    // append to the total
                    readLines.append(sb);                                       // appends the StringBuilder to the reader
                    // return with no line separators
                    return sb.substring(0, sb.length() - 2);                    // returns a substring of the StringBuilder without \n
                }
                l = c;                                                          // sets l equal to c to remember the starting index in the buffer
            }
            return null;                                                        // returns null if there is nothing to read in the buffer
        }
 
        /**
         * Get more data from the stream.
         */
        public void readData() throws IOException {                             // defines the readData method
            buffer.limit(buffer.capacity());                                    // sets the limit of the buffer to the capacity of the buffer
            int read = channel.read(buffer);                                    // initializes an integer read to the buffer
            if (read == -1) {                                                   // executes if read is -1
                throw new IOException("End of stream");                         // throws an IOException
            }
            buffer.flip();                                                      // flips the buffer
            buffer.position(mark);                                              // sets the position to mark
        }
 
        private void writeLine(String line) throws IOException {                // defines the writeLine method
            channel.write(encoder.encode(CharBuffer.wrap(line + "\r\n")));      // writes a line to the buffer
        }
 
        public void sendResponse(WebServer.HTTPResponse response) {             // defines the sendResponse method
            response.addDefaultHeaders();                                       // adds default headers to the response
            try {                                                               // tries the following code
                writeLine(response.version + " " + response.responseCode + " " + response.responseReason); // writes the version, code, and reason to the response
                for (Map.Entry<String, String> header : response.headers.entrySet()) { // executes for each header
                    writeLine(header.getKey() + ": " + header.getValue());      // writes the key and value to the response
                }
                writeLine("");                                                  // writes an empty space
                channel.write(ByteBuffer.wrap(response.content));               // writes the content of the response to the buffer
            } catch (IOException ex) {                                          // executes if an IOException is thrown
                // slow silently
            }
        }
 
        public void close() {                                                   // defines the close method
            try {                                                               // tries the following code
                channel.close();                                                // closes the channel
            } catch (IOException ex) {                                          // executes if an IOException is thrown
            }
        }
    }