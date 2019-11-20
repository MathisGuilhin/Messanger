package stream;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InterfaceClient extends JFrame implements ActionListener, KeyListener {

    static String userName = "";

    static JTextArea zoneHistoriqueMessage = new JTextArea();//100, 50);
    static JTextArea zoneEnvoiMessage = new JTextArea();//100, 50);
    JButton envoyerMessage = new JButton("Envoyer");
    JScrollPane panneauScroll1, panneauScroll2;
 
    JMenuBar barreDeMenu = new JMenuBar();
 
    JMenu menuMessenger = new JMenu("Menu");
    JMenuItem deconnection = new JMenuItem("Déconnexion");

 
    public InterfaceClient() {
        super("Messenger");
        setBounds(0, 0, 500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLayout(null);
 
        zoneHistoriqueMessage.setEditable(false);
        zoneHistoriqueMessage.setBackground(Color.BLACK);
        zoneHistoriqueMessage.setForeground(Color.WHITE);
        //msgRec.addFocusListener(this);
        zoneHistoriqueMessage.setText("");
 
        zoneHistoriqueMessage.setWrapStyleWord(true);
        zoneHistoriqueMessage.setLineWrap(true);
 
        panneauScroll1 = new JScrollPane(zoneHistoriqueMessage);
        panneauScroll1.setBounds(0, 0, 400, 200);
        panneauScroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(panneauScroll1);
 
        zoneEnvoiMessage.setBackground(Color.LIGHT_GRAY);
        zoneEnvoiMessage.setForeground(Color.BLACK);
        zoneEnvoiMessage.setLineWrap(true);
        zoneEnvoiMessage.setWrapStyleWord(true);
 
        zoneEnvoiMessage.setText("Write Message here");
        //msgSend.addFocusListener(this);
        zoneEnvoiMessage.addKeyListener(this);
 
        panneauScroll2 = new JScrollPane(zoneEnvoiMessage);
        panneauScroll2.setBounds(0, 200, 400, 200);
        panneauScroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(panneauScroll2);
 
        envoyerMessage.setBounds(0, 400, 400, 40);
        add(envoyerMessage);
        envoyerMessage.addActionListener(this);
 
        barreDeMenu.add(menuMessenger);
        menuMessenger.add(deconnection);
        deconnection.addActionListener(this);
 
        setJMenuBar(barreDeMenu);
        
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
 
        if (scr == envoyerMessage) {
            sendMessage();
        } else if (scr == deconnection) {
 
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
        //writer.println(userName + " :" + msgSend.getText());
 
        zoneHistoriqueMessage.append("\nMe: " + zoneEnvoiMessage.getText());
        //writer.flush();
        //cursorUpdate();
 
        zoneEnvoiMessage.setText("");
        zoneEnvoiMessage.setCaretPosition(0);
    }

}
