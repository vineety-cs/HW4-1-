package application;

// Answer object that stores the necessary info
public class Message {
    private final String messageID;
    private final String subjectID;
    private final String senderUserName;
    private final String recipientUserName;
    private final String messageText;
    private boolean read;

    public Message(String messageID, String source, String destination, String subjectID, String messageText) {
    		this.messageID = messageID;
    		this.senderUserName = source;
    		this.recipientUserName = destination;
    		this.subjectID = subjectID;
    		this.messageText = messageText;
    }

    @Override
    public String toString() {
    	return senderUserName + ": " + messageText;
    }
    
    public String getMessageID() { return messageID; }
    public String getSubjectID() { return subjectID; }
    public String getSender() { return senderUserName; }
    public String getRecipient() { return recipientUserName; }
    public String getMessageText() { return messageText; }
    public boolean isRead() { return read; }
}