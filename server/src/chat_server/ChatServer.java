package chat_server;


import chat_network.TCPConnection;
import chat_network.TCPConnectionList;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionList{

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server Running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try {
                    new TCPConnection(this,serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exeption : " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(chat_network.TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected : " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(chat_network.TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(chat_network.TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected : " + tcpConnection);
    }

    @Override
    public synchronized void onExeption(chat_network.TCPConnection tcpConnection, Exception ex) {
        System.out.println("TCPConnetion exception : " + ex);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendString(value);
        }
    }
}
