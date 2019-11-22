/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread
	extends Thread {
	PrintStream socOut;
	private Socket clientSocket;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		socOut = new PrintStream(clientSocket.getOutputStream());
    		while (true) {
    		  String line = socIn.readLine();
                  System.out.println(line);
                  if(line.equals("!quit")){
                      EchoServerMultiThreaded.removeClient(this);
                  } else if(line.equals("!join")){
                      EchoServerMultiThreaded.addClient(this);
                      socOut.println(EchoServerMultiThreaded.getHistorique());
				  } else if(EchoServerMultiThreaded.getClients().contains(this)) {
					  EchoServerMultiThreaded.appendMessageHistorique(line);
					  for(ClientThread ct : EchoServerMultiThreaded.getClients()) {
							if(this != ct) {
							  ct.envoyerMessage(line);
							} else {
								if(line.contains("a rejoint le salon.")){
									ct.envoyerMessage(line);
								}
							}
					}
				  }
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
        
        public void envoyerMessage(String msg) {
            socOut.println(msg);
        }
  
  }

  
