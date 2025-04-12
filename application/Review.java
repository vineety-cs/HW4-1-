package application;

// Review object that stores review info 
public class Review {

	private final String reviewID;
	private final String subjectID;
	private final String userName;
	private String reviewText;
	
	
	public Review(String reviewID, String subjectID, String userName, String reviewText) {
		this.reviewID = reviewID;
		this.subjectID = subjectID;
		this.userName = userName;
		this.reviewText = reviewText;
	}
	
	@Override
	public String toString() {
		return("Reviewer: " + userName + " " + reviewText);
	}
	
	public String getReviewID() { return reviewID; }
	public String getSubjectID() { return subjectID; }
	public String getUserName() { return userName; }
	public String getReviewText() { return reviewText; }
	
}