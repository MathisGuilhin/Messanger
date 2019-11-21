/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;



public class EchoClient {


  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
            
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
	    socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    
	    socOut= new PrintStream(echoSocket.getOutputStream());
	    stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
        
        
        //Interface graphique
        InterfaceClient interfaceClient = new InterfaceClient();
        
               
        final BufferedReader socInFinal = socIn;
        //thread pour print les msg recus
        Runnable ThreadReading = () -> {
            while(true) {
                try {
                    String message = socInFinal.readLine();
                    interfaceClient.receiveMessage(message);
                } catch (IOException e) {
                    
                }
            }
        };
        Thread thread1 = new Thread(ThreadReading);
        thread1.start();
        //ThreadReading.run();
        
        String line;
        //thread actuel qui envoie les msg ecrits
        while (true) {
            /*
        	line=stdIn.readLine();
        	if (line.equals(".")) break;
        	socOut.println(line);
        	//System.out.println("echo: " + socIn.readLine());
        	*/
            String message = interfaceClient.getMessageAEnvoyer();
            if (!message.equals("")){
                if (message.equals(".")){
                     break;
                }
                socOut.println(message);
            }

        }

      socOut.close();
      socIn.close();
      stdIn.close();
      echoSocket.close();
    }
}


