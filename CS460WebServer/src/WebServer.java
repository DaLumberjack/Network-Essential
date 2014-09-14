/**
 * CS 460 Team Project Part 4
 * Ryan Buckingham, Alex Farmer, Andrew Hodel
 */

 
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
 
/**
 * Simple Java non-blocking NIO web server.
 *
 * @author md_5
 */
public class WebServer implements Runnable {                                    // defines the WebServer class
 
    private Charset charset = Charset.forName("UTF-8");                         // creates the charset
    private CharsetEncoder encoder = charset.newEncoder();                      // creates the encoder
    private Selector selector = Selector.open();                                // creates the selector
    private ServerSocketChannel server = ServerSocketChannel.open();            // creates the server socket channel
    private boolean isRunning = true;                                           // initializes isRunning to true
    private boolean debug = true;                                               // initializes debug to true
 
    /**
     * Create a new server and immediately binds it.
     *
     * @param address the address to bind on
     * @throws IOException if there are any errors creating the server.
     */
    protected WebServer(InetSocketAddress address) throws IOException {         // defines the WebServer constructor
        server.socket().bind(address);                                          // binds the server socket to a specific address
        server.configureBlocking(false);                                        // sets the server to non-blocking
        server.register(selector, SelectionKey.OP_ACCEPT);                      // registers the server with the selector
    }
 
    /**
     * Core run method. This is not a thread safe method, however it is non
     * blocking. If an exception is encountered it will be thrown wrapped in a
     * RuntimeException, and the server will automatically be {@link #shutDown}
     */
    @Override
    public final void run() {                                                   // defines the run method
        if (isRunning) {                                                        // executes if isRunning is true
            try {                                                               // tries the following code
                selector.selectNow();                                           // returns immediately with what channels are ready
                Iterator<SelectionKey> i = selector.selectedKeys().iterator();  // creates an iterator to look through the channels
                while (i.hasNext()) {                                           // executes while there is another channel connected to check
                    SelectionKey key = i.next();                                // moves the iterator to the next channel
                    i.remove();                                                 // removes the previously checked channel
                    if (!key.isValid()) {                                       // executes if the channel is not valid
                        continue;                                               // returns to the top of the while loop
                    }
                    try {                                                       // tries the following code
                        // get a new connection
                        if (key.isAcceptable()) {                               // executes if the channel is ready to connect to the server
                            // accept them
                            SocketChannel client = server.accept();             // creates the client
                            // non blocking please
                            client.configureBlocking(false);                    // sets the client to non-blocking
                            // show out intentions
                            client.register(selector, SelectionKey.OP_READ);    // registers the client with the selector
                            // read from the connection
                        } else if (key.isReadable()) {                          // executes if the client is ready to be read from
                            //  get the client
                            SocketChannel client = (SocketChannel) key.channel(); // gets the client
                            // get the session
                            HTTPSession session = (HTTPSession) key.attachment(); // gets the HTTP session
                            // create it if it doesnt exist
                            if (session == null) {                              // executes if the session does not exist
                                session = new HTTPSession(client);              // creates the session
                                key.attach(session);                            // attaches the session to the client
                            }
                            // get more data
                            session.readData();                                 // reads the data from the client to the session
                            // decode the message
                            String line;                                        // creates a string
                            while ((line = session.readLine()) != null) {       // executes while there is still data to be read
                                // check if we have got everything
                                if (line.isEmpty()) {                           // executes if there is still data to be read
                                    HTTPRequest request = new HTTPRequest(session.readLine()); // creates a request to the session
                                    session.sendResponse(handle(session, request)); // sends a response from the session
                                    session.close();                            // closes the session
                                }
                            }
                        }
                    } catch (Exception ex) {                                    // executes if an Exception is thrown
                        System.err.println("Error handling client: " + key.channel()); // prints an error
                        if (debug) {                                            // executes if debug is true
                            ex.printStackTrace();                               // prints a stack trace
                        } else {                                                // executes if debug is false
                            System.err.println(ex);                             // prints the exception
                            System.err.println("\tat " + ex.getStackTrace()[0]); // prints an error
                        }
                        if (key.attachment() instanceof HTTPSession) {          // executes if the attachment of the client is a session
                            ((HTTPSession) key.attachment()).close();           // closes the session
                        }
                    }
                }
            } catch (IOException ex) {                                          // executes if an IOException is thrown
                // call it quits
                shutdown();                                                     // shuts down the server
                // throw it as a runtime exception so that Bukkit can handle it
                throw new RuntimeException(ex);                                 // throws a RuntimeException
            }
        }
    }
 
    /**
     * Handle a web request.
     *
     * @param session the entire HTTP session
     * @return the handled request
     */
    protected HTTPResponse handle(HTTPSession session, HTTPRequest request) throws IOException { // defines the HTTPResponse handle method
        HTTPResponse response = new HTTPResponse();                             // creates a new response
        response.setContent("I liek cates".getBytes());                         // sets the content of the response
        return response;                                                        // returns the response
    }
 
    /**
     * Shutdown this server, preventing it from handling any more requests.
     */
    public final void shutdown() {                                              // defines the shutdown method
        isRunning = false;                                                      // sets isRunning to false
        try {                                                                   // tries the following code
            selector.close();                                                   // closes the selector
            server.close();                                                     // closes the server
        } catch (IOException ex) {                                              // executes if an IOException is thrown
            // do nothing, its game over
        }
    }
    
    public static void main(String[] args) throws Exception {                   // defines the main method
        WebServer server = new WebServer(new InetSocketAddress(5555));          // creates the WebServer
        while (true) {                                                          // executes while true
            server.run();                                                       // runs the server
            Thread.sleep(100);                                                  // sets a timeout on the server
        }
    }
}