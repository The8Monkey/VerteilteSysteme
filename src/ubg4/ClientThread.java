package ubg4;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;


class ClientThread extends Thread {

    final int lifeTime = 100000;
    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;
    private boolean login = false;

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
                is = new DataInputStream(clientSocket.getInputStream());
                os = new PrintStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            loop:
            while (true) {
                String in[] = is.readLine().split(" ");
                switch (in[0].toLowerCase()) {
                    case "help":
                        os.println("Commands:");
                        os.println("login <username>: to login.");
                        os.println("logout: to logout.");
                        os.println("who: to get the list of all user.");
                        os.println("time: to get the time.");
                        os.println("ls <path>: to show all files in the path.");
                        os.println("chat <username> <message>: to send th message to a specific user.");
                        os.println("notify <message>: to notify all user.");
                        os.println("note <text>: to leave a note.");
                        os.println("notes: to see all notes.");
                        break;
                    case "login":
                        if (login) {
                            os.println("You are already connected.");
                            break;
                        }
                        name = in[1];
                        if (Server.clientName.contains(name)) {
                            os.println("The name you try to use is already taken. Please try an other name.");
                            continue loop;
                        } else if (name.contains("@")) {
                            os.println("The name should not contain '@' character.");
                        }

                        Server.clientName.add(Server.clientName.size(), name);
                        /* Welcome the new the client. */
                        login = true;
                        Server.log("Client with the name:" + name + " has connected to the server.");
                        os.println("Welcome " + name
                                + " to our chat room.\n Type help to see the commands.");
                        synchronized (this) {
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i] == this) {
                                    clientName = "@" + name;
                                    break;
                                }
                            }
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i] != this) {
                                    threads[i].os.println("*** A new user " + name
                                            + " entered the chat room !!! ***");
                                }
                            }
                        }
                        break;
                    case "logout":
                        if (!login) {
                            os.println("Please login before you logout.");
                            break;
                        }
                        break loop;
                    case "notify":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        /* The message is public, broadcast it to all other clients. */
                        String msgAll = "";
                        for (int i = 1; i < in.length; i++) {
                            if (i == 1) {
                                msgAll = in[1];
                            } else {
                                msgAll += " " + in[i];
                            }
                        }
                        synchronized (this) {
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i].clientName != null) {
                                    threads[i].os.println("<" + name + "> " + msgAll);
                                }
                            }
                        }
                        break;
                    case "chat":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        String[] words = new String[2];
                        words[0] = in[1];
                        String msg = "";
                        for (int i = 2; i < in.length; i++) {
                            if (i == 2) {
                                msg = in[2];
                            } else {
                                msg += " " + in[i];
                            }
                        }
                        words[1] = msg;
                        if (words.length > 1 && words[1] != null) {
                            if (!words[1].isEmpty()) {
                                synchronized (this) {
                                    int index = Server.clientName.indexOf(words[0]);
                                    threads[index].os.println("<" + name + "> " + words[1]);
                                    /**
                                     * Echo this message to let the client know the private message was sent.
                                     */
                                    this.os.println(">" + name + "> " + words[1]);
                                }
                            }
                        }
                        break;
                    case "who":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        for (String s : Server.clientName) {
                            os.println(s);
                        }
                        break;
                    case "time":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        os.println(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime()));
                        break;
                    case "ls":
                        File folder = new File(in[1]);
                        File[] listOfFiles = folder.listFiles();

                        for (int i = 0; i < listOfFiles.length; i++) {
                            if (listOfFiles[i].isFile()) {
                                os.println("File " + listOfFiles[i].getName());
                            } else if (listOfFiles[i].isDirectory()) {
                                os.println("Directory " + listOfFiles[i].getName());
                            }
                        }
                        break;
                    case "note":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        String note = "";
                        for (int i = 1; i < in.length; i++) {
                            if (i == 1) {
                                note = in[1];
                            } else {
                                note += " " + in[i];
                            }
                        }
                        putMessage(note);
                        break;
                    case "notes":
                        if (!login) {
                            os.println("You need to login first!");
                            break;
                        }
                        updateMsgs();
                        os.println("All Messages:");
                        for (String s : getMessages()) {
                            os.println(s);
                        }
                        break;
                    default:
                        os.println("Type \"help\" to see all commands.");
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].os.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            os.println("*** Bye " + name + " ***");
            Server.log("The client " + name + " has left the server.");
            Server.clientName.remove(name);

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
      /*
       * Close the output stream, close the input stream, close the socket.
       */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
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