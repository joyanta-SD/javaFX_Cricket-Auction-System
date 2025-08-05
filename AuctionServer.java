package Network;

import controller.AuctionData;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionServer {
    private static final int port = 12345;
    private ServerSocket serverSocket;
    private List<ClientConnection> clients;
    private AuctionData currentAuctionData;
    private boolean isRunning;

    public AuctionServer() {
        clients = new CopyOnWriteArrayList<>();
        isRunning = false;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("Auction Server started on port " + port);
            System.out.println("Waiting for clients to connect...");
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientConnection clientConn = new ClientConnection(clientSocket, this);
                    clients.add(clientConn);
                    new Thread(clientConn).start();

                    System.out.println("New client connected from: " +
                            clientSocket.getInetAddress().getHostAddress() +
                            " | Total clients: " + clients.size());

                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(" Server failed to start: " + e.getMessage());
        }
    }

    public synchronized void broadcastAuctionData(AuctionData auctionData) {
        currentAuctionData = auctionData;

        System.out.println("Broadcasting to " + clients.size() + " clients: " +
                "Bid=$" + String.format("%,d", auctionData.getCurrentBid()) +
                " by " + auctionData.getCurrentBidder() +
                " | Timer: " + auctionData.getTimeLeft() + "s");

        for (ClientConnection client : clients) {
            if (!client.sendAuctionData(auctionData)) {
                clients.remove(client);
                System.out.println(" Removed disconnected client. Active clients: " + clients.size());
            }
        }
    }

    public synchronized AuctionData getCurrentAuctionData() {
        return currentAuctionData;
    }

    public synchronized void removeClient(ClientConnection client) {
        clients.remove(client);
        System.out.println("🔌 Client disconnected. Active clients: " + clients.size());
    }

    public void stop() {
        isRunning = false;

        for (ClientConnection client : clients) {
            client.disconnect();
        }
        clients.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }

        System.out.println("🛑 Server stopped");
    }

    public static void main(String[] args) {
        AuctionServer server = new AuctionServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server gracefully...");
            server.stop();
        }));
        server.start();
    }

    private static class ClientConnection implements Runnable {
        private Socket socket;
        private AuctionServer server;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String clientId;
        private boolean isConnected;

        public ClientConnection(Socket socket, AuctionServer server) {
            this.socket = socket;
            this.server = server;
            this.clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            this.isConnected = true;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Client streams initialized: " + clientId);
                AuctionData currentData = server.getCurrentAuctionData();
                if (currentData != null) {
                    sendAuctionData(currentData);
                }
                while (isConnected) {
                    try {
                        Object message = in.readObject();
                        handleClientMessage(message);
                    } catch (EOFException e) {
                        break;
                    } catch (ClassNotFoundException | IOException e) {
                        if (isConnected) {
                            System.err.println("Error reading from client " + clientId + ": " + e.getMessage());
                        }
                        break;
                    }
                }

            } catch (IOException e) {
                System.err.println("Error handling client " + clientId + ": " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleClientMessage(Object message) {
            if (message instanceof AuctionData) {
                AuctionData auctionData = (AuctionData) message;
                System.out.println(STR."\uD83D\uDCE8 Received auction data from \{clientId}: Bid=$\{String.format("%,d", auctionData.getCurrentBid())} by \{auctionData.getCurrentBidder()}");

                server.broadcastAuctionData(auctionData);
            }
        }

        public boolean sendAuctionData(AuctionData data) {
            try {
                if (out != null && isConnected) {
                    out.writeObject(data);
                    out.flush();
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error sending data to client " + clientId + ": " + e.getMessage());
                isConnected = false;
            }
            return false;
        }

        public void disconnect() {
            isConnected = false;
        }

        private void cleanup() {
            isConnected = false;
            server.removeClient(this);

            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error cleaning up client " + clientId + ": " + e.getMessage());
            }

            System.out.println(" Client " + clientId + " cleaned up");
        }
    }
}