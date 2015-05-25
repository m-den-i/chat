package messaging;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
//import org.json.simple.*;
//import org.json.simple.parser.*;
//import org.json.simple.parser.JSONParser;

import java.io.IOException;

public class Message {
    private String ID;
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

    public Message(String string) throws IOException
    {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(string.trim());
            this.ID = actualObj.get("id").getTextValue();
            this.senderName = actualObj.get("senderName").getTextValue();
            this.messageText = actualObj.get("messageText").getTextValue();
            this.isDeleted = "true".equals(actualObj.get("isDeleted").getTextValue());
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
        StringBuilder sb = new StringBuilder("messaging.Message from ");
        sb.append(senderName)
        .append(" : ")
        .append(getMessageText());
        return sb.toString();
    }
}
