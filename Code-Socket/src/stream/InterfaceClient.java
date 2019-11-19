package stream;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
 
public class InterfaceClient extends JFrame implements ActionListener, KeyListener {
 
    // Extra variables
    static String message = "";
    static String userName = "";
 
    // Networking Variables
    static Socket socket = null;
    static PrintWriter writer = null;
 
    // // Graphics Variables
    static JTextArea msgRec = new JTextArea(100, 50);
    static JTextArea msgSend = new JTextArea(100, 50);
    JButton send = new JButton("Send");
    JScrollPane pane2, pane1;
 
    JMenuBar bar = new JMenuBar();
 
    JMenu messanger = new JMenu("Messanger");
    JMenuItem logOut = new JMenuItem("Log Out");
 
    JMenuItem s_keys = new JMenuItem("Shortcut Keys");

 
    public InterfaceClient() {
        super("Java Client");
        setBounds(0, 0, 407, 495);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
 
        msgRec.setEditable(false);
        msgRec.setBackground(Color.BLACK);
        msgRec.setForeground(Color.WHITE);
        //msgRec.addFocusListener(this);
        msgRec.setText("");
 
        msgRec.setWrapStyleWord(true);
        msgRec.setLineWrap(true);
 
        pane2 = new JScrollPane(msgRec);
        pane2.setBounds(0, 0, 400, 200);
        pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane2);
 
        msgSend.setBackground(Color.LIGHT_GRAY);
        msgSend.setForeground(Color.BLACK);
        msgSend.setLineWrap(true);
        msgSend.setWrapStyleWord(true);
 
        msgSend.setText("Write Message here");
        //msgSend.addFocusListener(this);
        msgSend.addKeyListener(this);
 
        pane1 = new JScrollPane(msgSend);
        pane1.setBounds(0, 200, 400, 200);
        pane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane1);
 
        send.setBounds(0, 400, 400, 40);
        add(send);
        send.addActionListener(this);
 
        bar.add(messanger);
        messanger.add(logOut);
        logOut.addActionListener(this);
 

        s_keys.addActionListener(this);
 
        setJMenuBar(bar);
        
        userName = JOptionPane.showInputDialog("User Name (Client)");
 
        if ((userName) != null) {
            setVisible(true);
        } else {
            System.exit(0);
        }
    }
 
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object scr = e.getSource();
 
        if (scr == send) {
            sendMessage();
        } else if (scr == logOut) {
 
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
            msgSend.append("\n");
 
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
 
        else if ((e.getKeyCode() == KeyEvent.VK_X) && e.isControlDown()) {
            System.exit(0);
        }
    }

 
    private void sendMessage() {
        //writer.println(userName + " :" + msgSend.getText());
 
        msgRec.append("\nMe: " + msgSend.getText());
        //writer.flush();
        //cursorUpdate();
 
        msgSend.setText("");
        msgSend.setCaretPosition(0);
    }

}
