package stream;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

public class InterfaceClient extends JFrame implements ActionListener, KeyListener {

    static String userName = "";
    static String message = "";
    static JTextArea zoneHistoriqueMessage = new JTextArea();//100, 50);
    static JTextArea zoneEnvoiMessage = new JTextArea();//100, 50);
    JPanel messagePanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JButton envoyerMessage = new JButton("Envoyer");
    JScrollPane panneauScroll1, panneauScroll2;
 
    JMenuBar barreDeMenu = new JMenuBar();
 
    JMenu menuMessenger = new JMenu("Menu");
    JMenuItem deconnection = new JMenuItem("Déconnexion");
    JMenuItem connection = new JMenuItem("Connexion");
    JMenuItem fermer = new JMenuItem("Fermer");

    static PrintStream socOut = null;
 
    public InterfaceClient() {
        super("Messenger");
        setBounds(0, 0, 500, 600);
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

        mainPanel.add(messagePanel,BorderLayout.CENTER);
        mainPanel.add(envoyerMessage, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);

        setJMenuBar(barreDeMenu);

        userName = JOptionPane.showInputDialog("User Name (Client)");


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

        Socket echoSocket = null;
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


        //Thread reading
        final BufferedReader socInFinal = socIn;
        Runnable runnableReading = () -> {
            while(true) {
                try {
                    String line = socInFinal.readLine();
                    System.out.println(line);
                    zoneHistoriqueMessage.append("\n" + line);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        };
        Thread readingThread = new Thread(runnableReading);
        readingThread.start();

        String line;
        while (true) {
            line=stdIn.readLine();
            if (line.equals(".")) break;
        }
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object scr = e.getSource();

        if (scr == envoyerMessage) {
            sendMessage();
        } else if (scr == deconnection) {
            socOut.println(userName + " a quitté le salon.");
            socOut.println("!quit");
            connection.setEnabled(true);
        } else if (scr == connection) {
            zoneHistoriqueMessage.setText("");
            socOut.println("!join");
            socOut.println(userName + " a rejoint le salon.");
            connection.setEnabled(false);
        } else if (scr == fermer) {
            socOut.println(userName + " a quitté le salon.");
            socOut.println("!quit");
            System.exit(0);
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
        String messageAEnvoyer = zoneEnvoiMessage.getText();
        zoneHistoriqueMessage.append("\nMe ("+ userName + "): " + messageAEnvoyer);
        socOut.println(userName + " :" + messageAEnvoyer);
        zoneEnvoiMessage.setText("");
        zoneEnvoiMessage.setCaretPosition(0);
        updateScrollbar();
    }

    private static void updateScrollbar() {
        //Met en bat la scrollbar de la zone historique message
        DefaultCaret caret = (DefaultCaret) zoneHistoriqueMessage.getCaret();
        caret.setDot(zoneHistoriqueMessage.getDocument().getLength());

        DefaultCaret caret2 = (DefaultCaret) zoneHistoriqueMessage.getCaret();
        caret2.setDot(zoneHistoriqueMessage.getDocument().getLength());
    }

}

