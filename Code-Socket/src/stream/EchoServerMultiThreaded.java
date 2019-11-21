/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class EchoServerMultiThreaded  {
        
    private static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
 	public static void main(String args[]){
        ServerSocket listenSocket;
        
        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
 	}

    public static ArrayList<ClientThread> getClients() {
        return clients;
    }
    
    public static void removeClient(ClientThread ct) {
        clients.remove(ct);
    }
    
    public static void addClient(ClientThread ct) {
        clients.add(ct);
    }
       
  }

  
