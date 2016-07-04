package ubg4;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


class ClientThread extends Thread {

    final int lifeTime = 100000;
    private String clientName = null;
    private BufferedReader is = null;
    private PrintWriter os = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;
    private boolean login = false;
    private JSONParser parser = new JSONParser();

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        String name = null;
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        try {
      /*
       * Create input and output streams for this client.
       */
            try {
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                os = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            loop:
            while (true) {
                Object o = parser.parse(is.readLine());
                JSONObject json = (JSONObject) o;
                //String input = json.get("req").getAsString();
                JSONObject gson = (JSONObject) json.get("req");
                switch (gson.get("cmd").toString()) {
                    case "help":
                        String com[] = {"Commands:", "login <username>: to login.", "logout: to logout.", "who: to get the list of all user.",
                                "time: to get the time.", "ls <path>: to show all files in the path.", "chat <username> <message>: to send th message to a specific user.",
                                "notify <message>: to notify all user.", "note <text>: to leave a note.", "notes: to see all notes."};
                        send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), com));
                        break;
                    case "login":
                        if (login) {
                            String[] con = {"You are already connected."};
                            send(new Answer(402, java.lang.Math.toIntExact((Long) gson.get("seq")), con));
                            break;
                        }
                        JSONArray data = (JSONArray) gson.get("params");
                        name = data.get(0).toString();
                        if (Server.clientName.contains(name)) {
                            String[] con = {"The name you try to use is already taken. Please try an other name."};
                            send(new Answer(400, java.lang.Math.toIntExact((Long) gson.get("seq")), con));
                            continue loop;
                        } else if (name.contains("@")) {
                            String[] con = {"The name should not contain '@' character."};
                            send(new Answer(400, java.lang.Math.toIntExact((Long) gson.get("seq")), con));
                        }

                        Server.clientName.add(Server.clientName.size(), name);
                        /* Welcome the new the client. */
                        login = true;
                        Server.log("Client with the name:" + name + " has connected to the server.");
                        String[] con = {"Welcome " + name + " to our chat room.\n" + "Type help to see the commands."};
                        send(new Answer(400, java.lang.Math.toIntExact((Long) gson.get("seq")), con));
                        synchronized (this) {
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i] == this) {
                                    clientName = name;
                                    break;
                                }
                            }
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i] != this && threads[i].login) {
                                    String[] con1 = {"*** A new user " + name + " entered the chat room !!! ***"};
                                    threads[i].send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                                }
                            }
                        }
                        break;
                    case "logout":
                        if (!login) {
                            String[] con1 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                            break;
                        }
                        break loop;
                    case "notify":
                        if (!login) {
                            String[] con1 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                            break;
                        }
                        /* The message is public, broadcast it to all other clients. */

                        synchronized (this) {
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i].clientName != null && threads[i].login) {
                                    JSONArray message = (JSONArray) gson.get("params");
                                    String s = "";
                                    for (int j = 0; j < message.size() + 1; j++) {
                                        if (j == 0) {
                                            s = "<" + name + "> ";
                                        } else {
                                            s += message.get(j - 1).toString() + " ";
                                        }
                                    }
                                    String[] con1 = {s};
                                    threads[i].send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                                }
                            }
                        }
                        break;
                    case "chat":
                        if (!login) {
                            String[] con1 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                            break;
                        }
                        String user = null;
                        JSONArray message = (JSONArray) gson.get("params");
                        String s = "<" + name + "> ";
                        for (int j = 0; j < message.size() + 1; j++) {
                            if (j == 0) {
                                user = message.get(j).toString();
                            } else {
                                s += message.get(j - 1).toString() + " ";
                            }
                        }
                        String[] con1 = {s};

                        if (con1.length == 1 && user != null) {

                            synchronized (this) {
                                if (Server.clientName.contains(user)) {
                                    int index = Server.clientName.indexOf(user);
                                    threads[index].send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), con1));
                                    /**
                                     * Echo this message to let the client know the private message was sent.
                                     */
                                    String[] con3 = {">" + name + "> " + con1};
                                    send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), con3));
                                } else {
                                    String[] con4 = {"No user found with the name:" + user};
                                    send(new Answer(400, java.lang.Math.toIntExact((Long) gson.get("seq")), con4));
                                    break;
                                }
                            }
                        }

                        break;
                    case "who":
                        if (!login) {
                            String[] con4 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con4));
                            break;
                        }
                        String[] users = new String[Server.clientName.size()];
                        for (int i = 0; i < Server.clientName.size(); i++) {
                            users[i] = Server.clientName.get(i);
                        }
                        send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), users));
                        break;
                    case "time":
                        if (!login) {
                            String[] con5 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con5));
                            break;
                        }
                        String[] con4 = {new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime())};
                        send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con4));
                        break;
                    case "ls":
                        if (!login) {
                            String[] con6 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con6));
                            break;
                        }
                        JSONArray para = (JSONArray) gson.get("params");

                        File folder = new File((String) para.get(0));
                        if (folder.exists()) {
                            File[] listOfFiles = folder.listFiles();

                            String[] dir = new String[listOfFiles.length];

                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isFile()) {
                                    dir[i] = "File " + listOfFiles[i].getName();
                                } else if (listOfFiles[i].isDirectory()) {
                                    dir[i] = "Directory " + listOfFiles[i].getName();
                                }
                            }
                            send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), dir));
                        } else {
                            String[] con6 = {"Wrong path:" + para.get(0)};
                            send(new Answer(404, java.lang.Math.toIntExact((Long) gson.get("seq")), con6));
                        }

                        break;
                    case "note":
                        if (!login) {
                            String[] con7 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con7));
                            break;
                        }
                        String note = "";
                        JSONArray notes = (JSONArray) gson.get("params");
                        if (notes.size() > 0) {
                            for (Object s1 : notes) {
                                note += s1.toString() + " ";
                            }
                        }
                        putMessage(note);
                        break;
                    case "notes":
                        if (!login) {
                            String[] con8 = {"You need to login first!"};
                            send(new Answer(401, java.lang.Math.toIntExact((Long) gson.get("seq")), con8));
                            break;
                        }
                        updateMsgs();
                        send(new Answer(200, java.lang.Math.toIntExact((Long) gson.get("seq")), getMessages()));
                        break;
                    default:
                        String[] con7 = {"Type help to see all commands."};
                        send(new Answer(400, java.lang.Math.toIntExact((Long) gson.get("seq")), con7));
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null && threads[i].login) {
                        String[] con7 = {"*** The user " + name + " is leaving the chat room !!! ***"};
                        threads[i].send(new Answer(200, 000, con7));
                    }
                }
            }
            String[] con7 = {"*** Bye " + name + " ***"};
            send(new Answer(204, 000, con7));
            Server.log("The client " + name + " has left the server.");
            Server.clientName.remove(name);

            /**
             * Clean up. Set the current thread variable to null so that a new client could be accepted by the server.
             **/
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            /**
             * Close the output stream, close the input stream, close the socket.
             **/
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void send(Answer a) {
        Gson send1 = new Gson();
        String json1 = send1.toJson(a);
        os.println("{\"res\":" + json1 + "}");
        os.flush();
    }

    private String[] getMessages() throws RemoteException {
        return Server.messages.values().toArray(new String[Server.messages.values().size()]);
    }

    private boolean putMessage(String msg) throws RemoteException {

        Server.messages.put(System.currentTimeMillis(), msg);

        return true;
    }

    private void updateMsgs() throws RemoteException {
        Long current = System.currentTimeMillis();

        Iterator it = Server.messages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (current - (Long) pair.getKey() > lifeTime) {
                it.remove();
            }
        }
    }
}