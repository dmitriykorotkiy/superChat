package chat_network;


import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;


public class TCPConnection {

    private  final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionList eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;


    public TCPConnection(TCPConnectionList eventListener, String ipAddr , int port) throws IOException {
             this(eventListener,new Socket(ipAddr,port));
    }

    public TCPConnection(TCPConnectionList eventListener ,Socket socket) throws IOException{
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }
                    String msg = in.readLine();
                } catch (IOException e) {
                    eventListener.onExeption(TCPConnection.this,e);
                }finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();

    }

    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onExeption(TCPConnection.this,e);
            disconnect();
        }
    }

    public void addToMsgHistory(String value){
        try {
            FileWriter writer = new FileWriter("history.txt" ,true);
            String lineSeparator = System.getProperty("line.separator");
            String msg = value + lineSeparator;
            writer.write(msg);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExeption(TCPConnection.this,e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}

