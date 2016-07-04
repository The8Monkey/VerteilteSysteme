package ubg4;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintWriter os = null;
    // The input stream
    private static BufferedReader is = null;

    private JSONParser parser = new JSONParser();
    private static int seq = (int) Math.random()*1000;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        // The default port.
        int portNumber = 8090;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java Client <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintWriter(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNumber.
     */
        if (clientSocket != null && os != null && is != null) {
            try {

        /* Create a thread to read from the server. */
                new Thread(new Client()).start();
                while (!closed) {
                    String in[] = inputLine.readLine().split(" ");
                    String msg[] = new String[in.length-1];
                    for (int i = 1; i < in.length; i++) {
                        if (i == 1) {
                            msg[0] = in[1];
                        } else {
                            msg[i-1] = in[i];
                        }
                    }
                    Gson gson = new Gson();
                    Request req = new Request(seq ,in[0],msg);
                    String json = gson.toJson(req);
                    os.println("{\"req\":"+json+"}");
                    os.flush();
                    seq++;
                }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:test  " + e);
            }
        }
    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
    /*
     * Keep on reading from the socket till we receive "Bye" from the
     * server. Once we received that then we want to break.
     */
        String input;
        try {
            while ((input = is.readLine()) != null) {
                Object o = parser.parse(input);
                JSONObject json = (JSONObject) o;
                //String input = json.get("req").getAsString();
                JSONObject gson = (JSONObject) json.get("res");

                JSONArray text = (JSONArray) gson.get("data");
                if(text.size()>0){
                    for (Object s: text) {
                        System.out.println(s.toString());
                    }
                }
                if (gson.get("status").toString()=="204"){
                    break;
                }
            }
            closed = true;
        } catch (Exception e) {
            System.err.println("IOException:  zujktzj" + e);
        }
    }
}