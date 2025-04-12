package application;

// Answer object that stores the necessary info
public class Question {
    private final String questionID;
    private final String userName;
    private String content;
    private boolean resolution;

    public Question(String questionID, String userName, String content) {
        this.questionID = questionID;
        this.userName = userName;
        this.content = content;
        this.resolution = false;
    }

    @Override
    public String toString() {
    	return userName + ": " + content;
    }
    
    public String getQuestionID() { return questionID; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getQuestionText() { return content; }
    public boolean getResolution() { return resolution; }
    public void setResolution(boolean a) { resolution = a; }
}