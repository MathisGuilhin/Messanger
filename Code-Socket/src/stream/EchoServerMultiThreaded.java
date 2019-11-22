/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class EchoServerMultiThreaded {

    private static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    private static String historique = "";
    private static String relativePath = "tmp" + System.getProperty("file.separator") + "historique.txt";
    private static boolean choix = false;
    private static File sauvegarde;
    private static FileWriter writerSauvegarde;


    public static void main(String args[]) {
        System.out.println("Voulez vous activer l'historique permanent? (o/n)");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        sauvegarde = new File(relativePath);
        try {
            if (line.equals("o")) {
                choix = true;
                if (sauvegarde.createNewFile()) {
                    System.out.println("Fichier de sauvegarde créé");
                } else {
                    System.out.println("Fichier de sauvegarde chargé");
                    Scanner reader = new Scanner(sauvegarde);
                    while (reader.hasNextLine()) {
                        historique += reader.nextLine()+"\n";
                    }
                }
            } else {
                if(sauvegarde.delete()){
                    System.out.println("Fichier de sauvegarde supprimé");
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

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

    public static void appendMessageHistorique(String message) {
        //message += '\n';
        historique += message;
        if (choix) {
            try {
                Files.write(Paths.get(relativePath), (message + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getHistorique() {
        return historique;
    }

    public static void removeClient(ClientThread ct) {
        clients.remove(ct);
    }

    public static void addClient(ClientThread ct) {
        clients.add(ct);
    }

}

  
