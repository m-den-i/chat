import org.json.simple.*;
import org.json.simple.parser.*;

import java.util.ArrayList;

public class Message {
    private final String ID;
    private String senderName;
    private String messageText;
    private boolean isDeleted;

    public Message(String ID, String senderName, String messageText, boolean isDeleted) {
        this.ID = ID;
        this.senderName = senderName;
        this.messageText = messageText;
        this.isDeleted = isDeleted;
    }

    public Message(Message message) {
        this.ID = message.ID;
        this.senderName = message.senderName;
        this.messageText = message.messageText;
        this.isDeleted = message.isDeleted;
    }

    public Message(String string) throws org.json.simple.parser.ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(string.trim());
        this.ID = (String)obj.get("id");
        this.senderName = (String)obj.get("senderName");
        this.messageText = (String)obj.get("messageText");
        this.isDeleted = "true".equals((String)obj.get("isDeleted"));
    }

    public void delete() {
        isDeleted = true;
        messageText = "deleted";
    }

    public String getId() {
        return ID;
    }

    public String getMessageText() {
        return messageText;
    }
	public void setText(String message) {
        messageText = message;
    }
	
	public String getUser() {
        return senderName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":\"").append(ID)
        .append("\", \"senderName\":\"").append(senderName)
        .append("\", \"messageText\":\"").append(messageText)
        .append("\", \"isDeleted\":\"").append(isDeleted).append("\"}");
        return sb.toString();
    }
	

    public String getReadableView() {
        StringBuilder sb = new StringBuilder("Message from ");
        sb.append(senderName)
        .append(" : ")
        .append(getMessageText());
        return sb.toString();
    }
}
