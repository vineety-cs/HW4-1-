package application;

// Answer object that stores the necessary info
public class Answer {
    private final String answerID;
    private final String questionID;
    private final String userName;
    private String answerText;
    private boolean resolution;

    public Answer(String answerID, String questionID, String userName, String answerText, boolean resolution) {
        this.answerID = answerID;
        this.questionID = questionID;
        this.userName = userName;
        this.answerText = answerText;
        this.resolution = resolution;
    }

    @Override
    public String toString() {
    	return (resolution ? "☆ " : "") + userName + ": " + answerText;
    }
    
    public String getAnswerID() { return answerID; }
    public String getQuestionID() { return questionID; }
    public String getUserName() { return userName; }
    public String getAnswerText() { return answerText; }
    public boolean getResolution() { return resolution; }
    public void setResolution(boolean a) { resolution = a;}
}