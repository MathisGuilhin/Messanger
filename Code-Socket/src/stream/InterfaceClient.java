package stream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static com.sun.webkit.graphics.WCImage.getImage;

public class InterfaceClient extends JFrame implements ActionListener, KeyListener {

    static String userName = "";
    static HashSet<String> utilisateursConnectes = new HashSet<>();
    static String message = "";
    static JTextArea zoneHistoriqueMessage = new JTextArea();//100, 50);
    static JTextArea zoneEnvoiMessage = new JTextArea();//100, 50);
    JPanel messagePanel = new JPanel();
    JPanel mainPanel = new JPanel();
    static JLabel utilisateurs = new JLabel("<html><h3>Utilisateurs connectés : <h3></html>",SwingConstants.LEFT);
    static JLabel infoConnection = new JLabel("",SwingConstants.LEFT);
    JButton envoyerMessage = new JButton("Envoyer");
    JScrollPane panneauScroll1, panneauScroll2;
 
    JMenuBar barreDeMenu = new JMenuBar();
 
    JMenu menuMessenger = new JMenu("Menu");
    JMenuItem deconnection = new JMenuItem("Déconnexion");
    JMenuItem connection = new JMenuItem("Connexion");
    JMenuItem fermer = new JMenuItem("Fermer");

    static PrintStream socOut = null;

    static InetAddress groupAddr;
    static int groupPort;
    static MulticastSocket s;

    static Thread readingThread;

    static boolean nonVu = false;
    static HashSet<String> ontVuDernierMessage = new HashSet<>();
    static BufferedImage img = null;
    static BufferedImage imgNotif = null;
 
    public InterfaceClient() {
        super("Messenger");
        ArrayList icons = new ArrayList<Image>();
        try {
            img = ImageIO.read(new File("messanger.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            imgNotif = ImageIO.read(new File("messangerNotif.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        this.setIconImage(img);

        setBounds(0, 0, 1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        messagePanel.setLayout(new GridLayout(2,1));
        mainPanel.setLayout(new BorderLayout());
 
        zoneHistoriqueMessage.setEditable(false);
        zoneHistoriqueMessage.setBackground(Color.BLACK);
        zoneHistoriqueMessage.setForeground(Color.WHITE);
        //msgRec.addFocusListener(this);
        zoneHistoriqueMessage.setText("");
 
        zoneHistoriqueMessage.setWrapStyleWord(true);
        zoneHistoriqueMessage.setLineWrap(true);
 
        panneauScroll1 = new JScrollPane(zoneHistoriqueMessage);
        panneauScroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePanel.add(panneauScroll1);
 
        zoneEnvoiMessage.setBackground(Color.LIGHT_GRAY);
        zoneEnvoiMessage.setForeground(Color.BLACK);
        zoneEnvoiMessage.setLineWrap(true);
        zoneEnvoiMessage.setWrapStyleWord(true);
 
        zoneEnvoiMessage.setText("Write Message here");
        //msgSend.addFocusListener(this);
        zoneEnvoiMessage.addKeyListener(this);

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if(nonVu==true) {
                    nonVu = false;
                    messagesVU();
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }

        });

        //Nous redefinissons la touche entrée pour qu'elle ne saute plus de ligne à l'envoi d'un message
        Action enter = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        zoneEnvoiMessage.getActionMap().put("insert-break", enter);
 
        panneauScroll2 = new JScrollPane(zoneEnvoiMessage);
        panneauScroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePanel.add(panneauScroll2);


        envoyerMessage.addActionListener(this);
 
        barreDeMenu.add(menuMessenger);
        menuMessenger.add(connection);
        connection.addActionListener(this);
        menuMessenger.add(deconnection);
        deconnection.addActionListener(this);
        menuMessenger.add(fermer);
        fermer.addActionListener(this);

        utilisateurs.setVerticalAlignment(SwingConstants.NORTH);

        userName = JOptionPane.showInputDialog("User Name (Client)");
        infoConnection.setText("<html><h3>Pseudo : "+ userName + "</h3></html>");

        mainPanel.add(messagePanel,BorderLayout.CENTER);
        mainPanel.add(envoyerMessage, BorderLayout.SOUTH);
        mainPanel.add(utilisateurs, BorderLayout.EAST);
        mainPanel.add(infoConnection, BorderLayout.NORTH);
        this.setContentPane(mainPanel);

        setJMenuBar(barreDeMenu);




        if ((userName) != null) {
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException {

        //Swing
        (new Thread(new Runnable() {
            public void run() {
                new InterfaceClient();
            }
        })).start();
    }

    public void startReadingThread() {
        try {
            //Thread reading
            Runnable runnableReading = () -> {
                while(true) {
                    if(s != null) {
                        // Build a datagram packet for response
                        byte[] buf = new byte[100000];
                        DatagramPacket recu = new
                                DatagramPacket(buf, buf.length);
                        // Receive a datagram packet response
                        try {
                            s.receive(recu);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String msgRec = new String(buf, 0, recu.getLength());
                        Message msg = Message.toMessage(buf);
                        if (!msg.getEmissaire().equals(userName)){
                            if (msg.getTypeMessage() == Message.MessageType.MESSAGE) {
                                ontVuDernierMessage.clear();
                                removeVus();
                                if(this.isFocused()) {
                                    messagesVU();
                                } else {
                                    nonVu = true;
                                    this.setIconImage(imgNotif);
                                }
                                zoneHistoriqueMessage.append(msg.getEmissaire() + " : " + msg.getContenu() + "\n");
                                updateScrollbar();
                            } else if (msg.getTypeMessage() == Message.MessageType.CONNEXION) {
                                removeVus();
                                ontVuDernierMessage.clear();
                                zoneHistoriqueMessage.append(msg.getEmissaire() + " a rejoint le salon." + "\n");
                                updateScrollbar();
                                messagesCP();
                                utilisateursConnectes.add(msg.getEmissaire());
                                updateConnected();
                            } else if (msg.getTypeMessage() == Message.MessageType.DECONNEXION) {
                                removeVus();
                                ontVuDernierMessage.clear();
                                utilisateursConnectes.remove(msg.getEmissaire());
                                updateConnected();
                                zoneHistoriqueMessage.append(msg.getEmissaire() + " a quitté le salon." + "\n");
                                updateScrollbar();
                            } else if(msg.getTypeMessage() == Message.MessageType.CP) {
                                System.out.println("reçu cp");
                                utilisateursConnectes.add(msg.getEmissaire());
                                updateConnected();
                            } else if(msg.getTypeMessage() == Message.MessageType.VU) {
                                ontVuDernierMessage.add(msg.getEmissaire());
                                updateVus();
                        }
                        }
                    }
                }
            };
            readingThread = new Thread(runnableReading);
            readingThread.start();
            // OK, I'm done talking - leave the group
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }




    public void stopReadingThread() {
        readingThread.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object scr = e.getSource();

        if (scr == envoyerMessage) {
            sendMessage();
        } else if (scr == deconnection) {
            messagesDeconnexion();
            stopReadingThread();
            s = null;
            connection.setEnabled(true);
            utilisateursConnectes.clear();
            updateConnected();
            infoConnection.setText("<html><h3>Pseudo : "+ userName + "</h3></html>");
        } else if (scr == connection) {
            String[] hostAndPost = multiInputJOptionPane();
            if(hostAndPost != null && !hostAndPost[1].equals("") && isInteger(hostAndPost[1],10)) {
                try {
                    groupAddr = InetAddress.getByName(hostAndPost[0]);
                    try {
                        groupPort = Integer.parseInt(hostAndPost[1]);
                        s = new MulticastSocket(groupPort);
                        s.joinGroup(groupAddr);
                        startReadingThread();
                        zoneHistoriqueMessage.setText("");
                        updateScrollbar();
                        messagesConnexion();
                        utilisateursConnectes.add(userName);
                        updateConnected();
                        connection.setEnabled(false);
                        infoConnection.setText("<html><h3>Pseudo : "+ userName + ", Host : "+hostAndPost[0] + ", Port : " + hostAndPost[0] + "</h3></html>");
                    } catch (NumberFormatException ne) {
                        ne.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (scr == fermer) {
            messagesDeconnexion();
            System.exit(0);
        }
    }

    private void updateConnected() {
        String newText = "<html><h3>Utilisateurs connectés : <h3>" ;
        for(String s : utilisateursConnectes) {
            newText += "<br/> " + s + "\n";
        }
        newText+="</html>";
        utilisateurs.setText(newText);
    }

    private void updateVus() {
        removeVus();
        String texteActuel = zoneHistoriqueMessage.getText();

        String derniereLigneTmp = texteActuel.substring(texteActuel.lastIndexOf("\n"));
        derniereLigneTmp.trim();
        derniereLigneTmp += " (Vu par ";
        for(String s : ontVuDernierMessage) {
            derniereLigneTmp += s + ", ";
        }
        String derniereLigne = null;
        if ((derniereLigneTmp != null) && (derniereLigneTmp.length() > 0)) {
            derniereLigne = derniereLigneTmp.substring(0, derniereLigneTmp.length() - 2);
        }
        derniereLigne += ")";

        String nouveauTexte = texteActuel.substring(0,texteActuel.lastIndexOf("\n"));
        nouveauTexte += derniereLigne + '\n';

        zoneHistoriqueMessage.setText(nouveauTexte);
        updateScrollbar();
    }

    private void removeVus() {
        String texteActuel = zoneHistoriqueMessage.getText();
        String nouveauTexte = texteActuel;
        if(texteActuel.contains(" (Vu par"))
            nouveauTexte = texteActuel.substring(0,texteActuel.indexOf(" (Vu par"));
        zoneHistoriqueMessage.setText(nouveauTexte);
        updateScrollbar();
    }

    private void messagesConnexion() {
        if(s != null) {
            zoneHistoriqueMessage.append("Vous avez rejoint le salon."  + "\n");
            updateScrollbar();
            Message msg = new Message(userName, "", Message.MessageType.CONNEXION);
            DatagramPacket hi = new
                    DatagramPacket(msg.toStream(),
                    msg.toStream().length, groupAddr, groupPort);
            // Send a multicast message to the group
            try {
                s.send(hi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void messagesDeconnexion() {
        if(s != null) {
            zoneHistoriqueMessage.append("Vous avez quitté le salon." + "\n");
            updateScrollbar();
            Message msg = new Message(userName, "", Message.MessageType.DECONNEXION);
            DatagramPacket hi = new
                    DatagramPacket(msg.toStream(),
                    msg.toStream().length, groupAddr, groupPort);
            // Send a multicast message to the group
            try {
                s.send(hi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void messagesCP() {
        if(s != null) {
            Message msg = new Message(userName, "", Message.MessageType.CP);
            DatagramPacket hi = new
                    DatagramPacket(msg.toStream(),
                    msg.toStream().length, groupAddr, groupPort);
            // Send a multicast message to the group
            try {
                s.send(hi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void messagesVU() {
        this.setIconImage(img);
        if(s != null) {
            Message msg = new Message(userName, "", Message.MessageType.VU);
            DatagramPacket hi = new
                    DatagramPacket(msg.toStream(),
                    msg.toStream().length, groupAddr, groupPort);
            // Send a multicast message to the group
            try {
                s.send(hi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    public String[] multiInputJOptionPane () {
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Host:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Port:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Host and Port Values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] returnValue = new String[2];
            returnValue[0] = xField.getText();
            returnValue[1] = yField.getText();
            return returnValue;
        } else {
            return null;
        }
    }
    // / KeyBoardEvents
 
    @Override
    public void keyTyped(KeyEvent e) {
    }
 
    @Override
    public void keyReleased(KeyEvent e) {
    }
 
    @Override
    public void keyPressed(KeyEvent e) {
 
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isShiftDown()) {
            zoneEnvoiMessage.append("\n");
 
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
 
        else if ((e.getKeyCode() == KeyEvent.VK_X) && e.isControlDown()) {
            System.exit(0);
        }
    }

 
    private void sendMessage() {
        if(s != null) {
            removeVus();
            ontVuDernierMessage.clear();
            String messageAEnvoyer = zoneEnvoiMessage.getText();
            zoneHistoriqueMessage.append("Me (" + userName + "): " + messageAEnvoyer  + "\n");
            Message msg = new Message(userName, messageAEnvoyer, Message.MessageType.MESSAGE);
            DatagramPacket hi = new
                    DatagramPacket(msg.toStream(),
                    msg.toStream().length, groupAddr, groupPort);
            // Send a multicast message to the group
            try {
                s.send(hi);
            } catch (IOException e) {
                e.printStackTrace();
            }
            zoneEnvoiMessage.setText("");
            zoneEnvoiMessage.setCaretPosition(0);
            updateScrollbar();
        }
    }

    private static void updateScrollbar() {
        //Met en bat la scrollbar de la zone historique message
        DefaultCaret caret = (DefaultCaret) zoneHistoriqueMessage.getCaret();
        caret.setDot(zoneHistoriqueMessage.getDocument().getLength());

        DefaultCaret caret2 = (DefaultCaret) zoneHistoriqueMessage.getCaret();
        caret2.setDot(zoneHistoriqueMessage.getDocument().getLength());
    }





}

