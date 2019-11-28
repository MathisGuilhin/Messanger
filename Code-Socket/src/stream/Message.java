package stream;

import java.io.*;

public class Message implements Serializable{

    public enum MessageType {MESSAGE, CONNEXION, DECONNEXION, CP, VU} ;
    private String emissaire;
    private String contenu;
    private MessageType typeMessage;

    public Message(String emissaire, String contenu, MessageType typeMessage) {
        this.emissaire = emissaire;
        this.contenu = contenu;
        this.typeMessage = typeMessage;
    }

    public String getEmissaire() {
        return emissaire;
    }

    public void setEmissaire(String emissaire) {
        this.emissaire = emissaire;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public MessageType getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(MessageType typeMessage) {
        this.typeMessage = typeMessage;
    }

    public byte[] toStream() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            yourBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return yourBytes;
    }

    public static Message toMessage(byte[] stream) {
        ByteArrayInputStream bis = new ByteArrayInputStream(stream);
        ObjectInput in = null;
        Message msg = null;
        try {
            in = new ObjectInputStream(bis);
            msg = (Message)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return msg;
    }


}
