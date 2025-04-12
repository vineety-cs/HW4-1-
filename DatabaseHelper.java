package databasePart1;
import java.util.UUID;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import application.User;
import application.Question;
import application.Review;
import application.Answer;
import application.Message;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
	    try {
	        Class.forName(JDBC_DRIVER);
	        System.out.println("Connecting to database...");
	        connection = DriverManager.getConnection(DB_URL, USER, PASS);
	        statement = connection.createStatement();

	        // 1. Create the tables
	        createTables();
	        
	        updateQuestionsTableSchema();
	        updatePrivateMessagesTableSchema();

	        // 2. Attempt to create a default instructor if they don't already exist
	        if (!doesUserExist("TestStaff")) {
	            register(new User(
	                "TestStaff",
	                "Test Staff",
	                "staff@test.com",
	                "StaffPass!",
	                new String[]{"Staff"}
	            ));
	            System.out.println("Default instructor account created: TestStaff / StaffPass1!");
	        }
	        if (!doesUserExist("TestInstructor")) {
	            register(new User(
	                "TestInstructor",
	                "Test Instructor",
	                "instructor@test.com",
	                "InstructorPass1!",
	                new String[]{"Instructor"}
	            ));
	            System.out.println("Default instructor account created: TestInstructor / InstructorPass1!");
	        }

	    } catch (ClassNotFoundException e) {
	        System.err.println("JDBC Driver not found: " + e.getMessage());
	    }
	}


	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "name VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "roles VARCHAR(63), "
				+ "isTempPassword BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "roles VARCHAR(63), "
	            + "expiration VARCHAR(63), "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the questions table
	    String questionTable = "CREATE TABLE IF NOT EXISTS questionsAsked ("
	            + "questionID VARCHAR(255) PRIMARY KEY, "
	            + "userName VARCHAR(255), "
	            + "question VARCHAR(255) NOT NULL, "
	            + "flagged BOOLEAN DEFAULT FALSE)";
	    statement.execute(questionTable);

		
		// Create the answers table
	    String answersTable = "CREATE TABLE IF NOT EXISTS answersGiven ("
	            + "answerID VARCHAR(255) PRIMARY KEY, "
	            + "questionID VARCHAR(255), "
	            + "userName VARCHAR(255), "
	            + "answerText VARCHAR(255) NOT NULL, "
	            + "resolution BOOLEAN DEFAULT FALSE, "
	            + "flagged BOOLEAN DEFAULT FALSE)";
	    statement.execute(answersTable);

	    
	    String reviewsTable = "CREATE TABLE IF NOT EXISTS reviewsGiven ("
	    		+ "reviewID VARCHAR(255) PRIMARY KEY, "
	    		+ "subjectID VARCHAR(255), "
	    		+ "userName VARCHAR(255), "
	    		+ "reviewText VARCHAR(255) NOT NULL, "
	    		+ "ratingScore INT DEFAULT 0)";
	    statement.execute(reviewsTable);
	    
	    // Create the reviewVotes table – one vote per review per student.
	    String reviewVotesTable = "CREATE TABLE IF NOT EXISTS reviewVotes ("
	            + "reviewID VARCHAR(255), "
	            + "voterUserName VARCHAR(255), "
	            + "vote INT, "      // +1 for upvote, -1 for downvote
	            + "PRIMARY KEY (reviewID, voterUserName))";
	    statement.execute(reviewVotesTable);

	    
	    // Create the student review rating table
	    String studentReviewerRankingTable = "CREATE TABLE IF NOT EXISTS studentReviewerRanking ("
	            + "studentUserName VARCHAR(255), "
	            + "reviewerUserName VARCHAR(255), "
	            + "rankingScore INT, "
	            + "PRIMARY KEY (studentUserName, reviewerUserName))";
	    statement.execute(studentReviewerRankingTable);
	    
	    // Create the pm table
	    String privateMessagesTable = "CREATE TABLE IF NOT EXISTS privateMessages ("
	            + "messageID VARCHAR(255) PRIMARY KEY, "
	            + "sender VARCHAR(255), "
	            + "recipient VARCHAR(255), "
	            + "questionID VARCHAR(255), "
	            + "message VARCHAR(255) NOT NULL, "
	            + "read BOOLEAN DEFAULT FALSE, "
	            + "flagged BOOLEAN DEFAULT FALSE)";
	    statement.execute(privateMessagesTable);
	 // Create the moderationNotes table
	    String moderationNotesTable = "CREATE TABLE IF NOT EXISTS moderationNotes ("
	            + "noteID VARCHAR(255) PRIMARY KEY, "       // Unique ID for the note
	            + "userName VARCHAR(255), "                  // The user this note is about
	            + "noteText VARCHAR(255), "                  // The content of the note
	            + "createdBy VARCHAR(255), "                 // Who created the note (e.g., staff)
	            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"  // When the note was created
	            + ")";
	    statement.execute(moderationNotesTable);


	    
	    // Create the role_requests table
	    String roleRequestsTable = "CREATE TABLE IF NOT EXISTS role_requests ("
	            + "id VARCHAR(255) PRIMARY KEY, "
	            + "username VARCHAR(255) NOT NULL, "
	            + "requested_role VARCHAR(255) NOT NULL CHECK(requested_role = 'Reviewer'), "
	            + "status VARCHAR(255) NOT NULL CHECK(status IN ('pending', 'approved', 'rejected'))"
	            + ")";

	    statement.execute(roleRequestsTable);
	    
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, name, email, password, roles) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getPassword());
			pstmt.setString(5, user.getRoleList());
			pstmt.executeUpdate();
		}
	}
	/**
	 * Alters the privateMessages table to add the flagged column.
	 * This method safely attempts to add the column; if it already exists, the error is caught and ignored.
	 */
	public void updatePrivateMessagesTableSchema() {
	    // SQL command to add the flagged column to privateMessages table
	    String alterTableQuery = "ALTER TABLE privateMessages ADD COLUMN flagged BOOLEAN DEFAULT FALSE";
	    try (Statement stmt = connection.createStatement()){
	         stmt.execute(alterTableQuery);
	         System.out.println("Column 'flagged' successfully added to privateMessages table.");
	    } catch (SQLException e) {
	         // If the error message indicates the column already exists, ignore it.
	         if (e.getMessage().contains("Column \"FLAGGED\" already exists")) {
	             System.out.println("Column 'flagged' already exists in privateMessages table.");
	         } else {
	             e.printStackTrace();
	         }
	    }
	}

	/**
	 * Alters the questionsAsked table to add the flagged column.
	 * This method safely attempts to add the column; if it already exists, the error is caught and ignored.
	 */
	public void updateQuestionsTableSchema() {
	    // SQL command to add the flagged column
	    String alterTableQuery = "ALTER TABLE questionsAsked ADD COLUMN flagged BOOLEAN DEFAULT FALSE";
	    try (Statement stmt = connection.createStatement()) {
	         stmt.execute(alterTableQuery);
	         System.out.println("Column 'flagged' successfully added to questionsAsked table.");
	    } catch (SQLException e) {
	         // If the error message indicates the column already exists, ignore it.
	         if (e.getMessage().contains("Column \"FLAGGED\" already exists")) {
	             System.out.println("Column 'flagged' already exists in questionsAsked table.");
	         } else {
	             e.printStackTrace();
	         }
	    }
	}

	
	// Deletes a user.
	public String delete(User user) throws SQLException {
		if (user.getRoleList() != null && user.getRoleList().contains("Admin")) return "Error: cannot remove admins.";
		String deleteUser = "DELETE FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.executeUpdate();
			return user.getUserName() + " deleted.";
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Retrieves every user as a User object
	public ArrayList<User> getAllUsers() {
	    ArrayList<User> users = new ArrayList<>();
	    String query = "SELECT * FROM cse360users";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                String userName = rs.getString("userName");
	                String name = rs.getString("name");
	                String email = rs.getString("email");
	                String password = rs.getString("password");
	                String rolesStr = rs.getString("roles");
	                String[] roles = rolesStr != null ? rolesStr.replaceAll("\\s", "").split(",") : new String[0];
	                for (String role : roles) {
	                	System.out.println("\"" + role + "\"");
	                }
	                // Assuming your User constructor is: User(String userName, String name, String email, String password, String[] roles)
	                users.add(new User(userName, name, email, password, roles));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	
	// Lists the information for all users
	public String getUserList() throws SQLException {
		String query = "SELECT * FROM cse360users";
		String userList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					userList += "Username: " + rs.getString("username");
					userList += "\tName: " + rs.getString("name");
					userList += "\tEmail: " + rs.getString("email");
					userList += "\tRoles: " + (rs.getString("roles") == null ? "None" : rs.getString("roles"));
				    userList += "\n";
				}
			}
		}
		return userList;
	}
	
	// Lists the information for a single user
	public String getUserInfo(String username) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ?";
		String userInfo = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					userInfo += "Username: " + rs.getString("username");
					userInfo += "\tName: " + rs.getString("name");
					userInfo += "\tEmail: " + rs.getString("email");
					userInfo += "\tRoles: " + (rs.getString("roles") == null ? "None" : rs.getString("roles"));
					userInfo += "\n";
				}
			}
		}
		return userInfo;
	}
	
	// Lists a user's info after changing their info
	public String getNewUserInfo(String userName) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ?";
		String userList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					userList += "New Username: " + rs.getString("username");
					userList += "\tNew Name: " + rs.getString("name");
					userList += "\tNew Email: " + rs.getString("email");
				}
				else return "USER NOT FOUND";
			}
		}
		return userList;
	}
	
	// Gets a user's username when they log in with email
	public String emailToUsername(String email) throws SQLException {
		String query = "SELECT username FROM cse360users WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("username");
				}
				else return "USER NOT FOUND";
			}
		}
	}
	
	// Returns a User object that represents a user from the database
	public User getUser(String userName) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString("name");
					String email = rs.getString("email");
					String password = rs.getString("password");
					String[] roles = getUserRoles(userName);
					return new User(userName, name, email, password, roles);
				}
				else return null;
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRoleList(String userName) {
	    String query = "SELECT roles FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("roles"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return ""; // If no user exists or an error occurs
	}
	
	// Convert the String representation of roles from the database to the array representation used by the User class
	public String[] getUserRoles(String userName) {
		String roleList = this.getUserRoleList(userName);
		String[] roles = new String[8];
		if (roleList == null || roleList == "") return roles;
		int n = 0;
		String currentRole = "";
		for (int i = 0; i < roleList.length(); i++) {
			if (roleList.charAt(i) == ',') {
				roles[n] = currentRole;
				currentRole = "";
				i++; n++;
				continue;
			}
			else {
				currentRole = currentRole + roleList.charAt(i);
			}
		}
		roles[n] = currentRole;
		currentRole = "";

		return roles;
	}
	
	// Gives a user a role
	public String addRole(String userName, String role) throws SQLException {
		User user = getUser(userName);
		String errorCode = user.addRole(role);
		
	    String query = "UPDATE cse360users SET roles = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getRoleList());
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return errorCode;
	}
	
	// Removes a role from a user
	public String removeRole(String userName, String role) throws SQLException {
		User user = getUser(userName);
		String errorCode = user.removeRole(role);
		
	    String query = "UPDATE cse360users SET roles = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getRoleList());
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return errorCode;
	}
	
	public String requestReviewerRole(String userName) {
	    String id = UUID.randomUUID().toString();
	    String query = "MERGE INTO role_requests (id, username, requested_role, status) "
	                 + "KEY(username, requested_role) VALUES (?, ?, 'Reviewer', 'pending')";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, id);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return query;
	}

	// Retrieves a list of usernames who have requested the Reviewer role and are pending.
	public ArrayList<String> getAllPendingReviewerRequests() {
	    ArrayList<String> pendingUsers = new ArrayList<>();
	    String query = "SELECT username FROM role_requests WHERE requested_role = 'Reviewer' AND status = 'pending'";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	       ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            pendingUsers.add(rs.getString("username"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return pendingUsers;
	}
	
	// Approves the Reviewer role request for the specified user and updates their roles.
	public void approveReviewerRole(String userName) {
	    try {
	    	addRole(userName, "Reviewer");
	    	// Update request status
	        String updateRequest = "UPDATE role_requests SET status = 'approved' WHERE username = ? AND requested_role = 'Reviewer'";
	        try (PreparedStatement pstmt = connection.prepareStatement(updateRequest)) {
	            pstmt.setString(1, userName);
	            pstmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	// Changes a user's username
	public String setUserName(String userName, String newUserName) throws SQLException {

	    String query = "UPDATE cse360users SET username = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newUserName);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	        return userName + " has been given the username " + newUserName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return "SQL Error";
	}
	
	// Changes a user's name
	public String setName(String userName, String name) throws SQLException {

	    String query = "UPDATE cse360users SET name = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, name);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	        return userName + " has been given the name " + name;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return "SQL Error";
	}
	
	// Changes a user's email
	public String setEmail(String userName, String email) throws SQLException {

	    String query = "UPDATE cse360users SET email = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	        return userName + " has been given the email " + email;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return "SQL Error";
	}
	
	// Changes a user's password
	public String setPassword(String userName, String password, boolean isTemp) throws SQLException {

	    String query = "UPDATE cse360users SET password = ?, isTempPassword = ? WHERE username = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setBoolean(2, isTemp);
	        pstmt.setString(3, userName);
	        pstmt.executeUpdate();
	        return userName + " has been given the password " + password;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		return "SQL Error";
	}
	
	// Determines whether or not a user has a one-time password and needs to provide a new one
	public boolean resetPassword(String username) throws SQLException {
	    
		String query = "SELECT isTempPassword FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("isTempPassword");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return true;
	    
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String roleList) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code, expiration, roles) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, Instant.now().plus(15, ChronoUnit.valueOf("MINUTES")).toString());
	        pstmt.setString(3, roleList);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused and not expired.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            
	            // Determine if code is expired
	            Instant attemptTime = Instant.now();
	            Instant expirationTime = Instant.parse(rs.getString("expiration"));
	            return (attemptTime.compareTo(expirationTime) == -1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Returns a list of a user's roles
	public String getRoleListFromCode(String code) {
		String query = "SELECT roles FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("roles");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "None";
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
			
	// Insert question into database  - Aracely Update - format (userName, name, email, password, roles) VALUES (?, ?, ?, ?, ?)
	public String insertNewQuestion(String questionID, String userName, String question) {
		String query = "INSERT INTO questionsAsked (questionID, userName, question) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, questionID);
			pstmt.setString(2, userName);
			pstmt.setString(3, question);
				
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				return "Question Posted success";
			}
 		} catch (SQLException e) {
			e.printStackTrace();
			return "Error - question did not post";
		}
		return "Question Posted";
	}
		
	// Lists all questions
	public String getQuestionList() throws SQLException {
		String query = "SELECT * FROM questionsAsked";
		String questionList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					
					//questionList += rs.getString("userName") + ": " + rs.getString("question") + "\n";
					questionList += "Question ID: " + rs.getString("questionID");
					questionList += "\tUsername: " + rs.getString("userName");
					questionList += "\tquestion: " + rs.getString("question");
				    questionList += "\n";
				}
			} 
		}
		return questionList;
	}
	
	public ArrayList<Question> getAllQuestions() {
		String query = "SELECT * FROM questionsAsked";
		ArrayList<Question> questions = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					questions.add(new Question(rs.getString("questionID"), rs.getString("userName"), rs.getString("question")));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return questions;
	}
	
	public Question getQuestion(String questionID) {
		String query = "SELECT * FROM questionsAsked WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, questionID);
			System.out.println("\"" + questionID + "\"");
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Question(rs.getString("questionID"), rs.getString("userName"), rs.getString("question"));
				}
				else {
					System.out.println("question not found");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public Answer getAnswer(String answerID) {
		String query = "SELECT * FROM answersGiven WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answerID);
			System.out.println("\"" + answerID + "\"");
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Answer(rs.getString("answerID"), rs.getString("questionID"), rs.getString("userName"), rs.getString("answerText"), rs.getBoolean("resolution"));
				}
				else {
					System.out.println("question not found");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public Review getReview(String reviewID) {
		String query = "SELECT * FROM reviewsGiven WHERE reviewID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reviewID);
			System.out.println("\"" + reviewID + "\"");
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Review(rs.getString("reviewID"), rs.getString("subjectID"), rs.getString("userName"), rs.getString("reviewText"));
				}
				else {
					System.out.println("review not found");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public Message getMessage(String messageID) {
		String query = "SELECT * FROM privateMessages WHERE messageID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, messageID);
			System.out.println("\"" + messageID + "\"");
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Message(rs.getString("messageID"), rs.getString("sender"), rs.getString("recipient"), rs.getString("questionID"), rs.getString("message"));
				}
				else {
					System.out.println("question not found");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public String setUserInstructorReviewer(String userName, boolean isInstructorReviewer) throws SQLException {
	    // Retrieve the user from the database.
	    User user = getUser(userName);
	    if (user == null) {
	        return "User not found.";
	    }
	    
	    // Update the user object (assumes your User class has a setUserInstructor method).
	    user.setInstructorReviewer(isInstructorReviewer);
	    
	    // Update the database.
	    String query = "UPDATE cse360users SET instructorReviewer = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, isInstructorReviewer);
	        pstmt.setString(2, userName);
	        int rows = pstmt.executeUpdate();
	        if (rows > 0) {
	            return "InstructorReviewer flag updated successfully for user " + userName;
	        } else {
	            return "Update failed for user " + userName;
	        }
	    }
	}
	
	public String voteForReview(String reviewID, String voterUserName, int vote) throws SQLException {
	    // Check if the student has already voted on this review.
	    String checkQuery = "SELECT vote FROM reviewVotes WHERE reviewID = ? AND voterUserName = ?";
	    boolean alreadyVoted = false;
	    int existingVote = 0;
	    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	        checkStmt.setString(1, reviewID);
	        checkStmt.setString(2, voterUserName);
	        try (ResultSet rs = checkStmt.executeQuery()){
	            if (rs.next()){
	                alreadyVoted = true;
	                existingVote = rs.getInt("vote");
	            }
	        }
	    }
	    
	    if (!alreadyVoted) {
	        // Insert the new vote.
	        String insertQuery = "INSERT INTO reviewVotes (reviewID, voterUserName, vote) VALUES (?, ?, ?)";
	        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
	            insertStmt.setString(1, reviewID);
	            insertStmt.setString(2, voterUserName);
	            insertStmt.setInt(3, vote);
	            insertStmt.executeUpdate();
	        }
	    } else {
	        if (existingVote == vote) {
	            return "You have already voted on this review with the same vote.";
	        } else {
	            // Update the existing vote to the new value.
	            String updateQuery = "UPDATE reviewVotes SET vote = ? WHERE reviewID = ? AND voterUserName = ?";
	            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	                updateStmt.setInt(1, vote);
	                updateStmt.setString(2, reviewID);
	                updateStmt.setString(3, voterUserName);
	                updateStmt.executeUpdate();
	            }
	        }
	    }
	    
	    // Retrieve the review to get the reviewer's username.
	    Review review = getReview(reviewID); // Assumes this method exists and returns a Review object.
	    if (review == null) {
	        return "Vote recorded, but review not found.";
	    }
	    String reviewerUserName = review.getUserName();
	    
	    // Calculate the total score this student has given to reviews written by the reviewer.
	    String calcQuery = "SELECT COALESCE(SUM(rv.vote), 0) AS totalScore " +
	                       "FROM reviewVotes rv " +
	                       "JOIN reviewsGiven r ON rv.reviewID = r.reviewID " +
	                       "WHERE rv.voterUserName = ? AND r.userName = ?";
	    int totalScore = 0;
	    try (PreparedStatement calcStmt = connection.prepareStatement(calcQuery)) {
	         calcStmt.setString(1, voterUserName);
	         calcStmt.setString(2, reviewerUserName);
	         try (ResultSet rs = calcStmt.executeQuery()){
	             if (rs.next()){
	                 totalScore = rs.getInt("totalScore");
	             }
	         }
	    }
	    
	    return "Vote recorded. Your current ranking for reviewer " + reviewerUserName + " is " + totalScore + ".";
	}



	public void updateStudentReviewerScore(String studentUserName, String reviewerUserName) throws SQLException {
	    // Calculate the total score given by the student for reviews written by the reviewer.
	    String calcQuery = "SELECT COALESCE(SUM(rv.vote), 0) AS totalScore " +
	                       "FROM reviewVotes rv " +
	                       "JOIN reviewsGiven r ON rv.reviewID = r.reviewID " +
	                       "WHERE rv.voterUserName = ? AND r.userName = ?";
	    int totalScore = 0;
	    try (PreparedStatement pstmt = connection.prepareStatement(calcQuery)) {
	        pstmt.setString(1, studentUserName);
	        pstmt.setString(2, reviewerUserName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                totalScore = rs.getInt("totalScore");
	            }
	        }
	    }
	    
	    // Update (or insert) the student's ranking for that reviewer in the studentReviewerRanking table.
	    String updateQuery = "MERGE INTO studentReviewerRanking (studentUserName, reviewerUserName, rankingScore) " +
	                         "KEY(studentUserName, reviewerUserName) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, studentUserName);
	        pstmt.setString(2, reviewerUserName);
	        pstmt.setInt(3, totalScore);
	        pstmt.executeUpdate();
	    }
	}
	
	// Returns the body of a single question
	public String getQuestionContent(String questionID) {
		String query = "SELECT question FROM questionsAsked WHERE questionID = ?";
		String questionList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, questionID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString(0);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return questionList;
	}
			
	// Get questions tied to a specific user
	public String getUserQuestions(String userName) throws SQLException {
		String query = "SELECT * FROM questionsAsked WHERE userName = ?";
		String userQuestionList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1,userName);
			try (ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					userQuestionList += "Question ID: " + rs.getString("questionID");
					userQuestionList += "\tUsername: " + rs.getString("userName");
					userQuestionList += "\tQuestion: " + rs.getString("question");
				    userQuestionList += "\n";
				}
			}
			return userQuestionList;
		}
	}
	
	// Get answers tied to a specific user
	public String getUserAnswers(String userName) throws SQLException {
		String query = "SELECT * FROM answersGiven WHERE userName = ?";
		String userAnswerList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1,userName);
			try (ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					userAnswerList += "Answer ID: " + rs.getString("answerID");
					userAnswerList += "\tUsername: " + rs.getString("userName");
					userAnswerList += "\tAnswer: " + rs.getString("answerText");
					userAnswerList += "\n";
				}
			}
			return userAnswerList;
		}
	}
	
	// Get reviews tied to a specific user
	public String getUserReviews(String userName) throws SQLException {
		String query = "SELECT * FROM reviewsGiven WHERE userName = ?";
		String userReviewList = "";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
		pstmt.setString(1, userName);
			try (ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					userReviewList += "Review ID: " + rs.getString("reviewID");
					userReviewList += "\tUsername: " + rs.getString("userName");
					userReviewList += "\tReview: " + rs.getString("reviewText");
					userReviewList += "\n";
				}
			}
			return userReviewList;
		}
	}

	// Update userQuestion query
	public void updateQuestion(String questionID, String question) {
		String query = "UPDATE questionsAsked SET question = ? WHERE questionID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, question);
			pstmt.setString(2, questionID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}
	
	public void updateAnswer(String answerID, String answerText) {
		String query = "UPDATE answersGiven SET answerText = ? WHERE answerID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, answerText);
			pstmt.setString(2, answerID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}
	
	// Update a Review 
	public void updateReview(String reviewID, String reviewText) {
		String query = "UPDATE reviewsGiven SET reviewText = ? WHERE reviewID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, reviewText);
			pstmt.setString(2, reviewID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}
	
	public void setResolution(String answerID, boolean resolution) {
		String query = "UPDATE answersGiven SET resolution = ? WHERE answerID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setBoolean(1, resolution);
			pstmt.setString(2, answerID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}			
	}
	
	public void deleteQuestion(String questionID) {
		
		ArrayList<Answer> answers = getAnswersForQuestion(getQuestion(questionID));
		
		for (Answer a : answers) {
			deleteAnswer(a.getAnswerID());
		}
		
		String query = "DELETE FROM questionsAsked WHERE questionID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, questionID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteAnswer(String answerID) {
		String query = "DELETE FROM answersGiven WHERE answerID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, answerID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	// Delete the Review of an Answer
	public void deleteReview(String reviewID) {
		String query = "DELETE FROM reviewsGiven WHERE reviewID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, reviewID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void deleteMessage(String messageID) {
		String query = "DELETE FROM privateMessages WHERE messageID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, messageID);
			pstmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	// inserts a new answer into the database 
	public String insertNewAnswer(String answerID, String questionID, String userName, String answerText, boolean resolution) {
		String sql = "INSERT INTO answersGiven (answerID, questionID, userName, answerText, resolution) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, answerID);
			pstmt.setString(2, questionID);
			pstmt.setString(3, userName);
			pstmt.setString(4, answerText);
			pstmt.setBoolean(5, resolution);
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				return "Answer inserted successfully";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error inserting answer: " + e.getMessage();
		}
		return "Insert operation completed, but no rows affected.";
	}
	
	// inserts a new Review 
	public String insertNewReview(String reviewID, String subjectID, String userName, String reviewText) {
		String sql = "INSERT INTO reviewsGiven (reviewID, subjectID, userName, reviewText) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, reviewID);
			pstmt.setString(2, subjectID);
			pstmt.setString(3, userName);
			pstmt.setString(4, reviewText);
			
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				return "Review inserted successfully";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error inserting Review: " + e.getMessage();
		}
		return "Insert operation completed, but no rows affected.";
			
	}
	
	public String sendMessage(String messageID, String senderName, String recipientName, String questionID, String message) {
		String sql = "INSERT INTO privateMessages (messageID, sender, recipient, questionID, message) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, messageID);
			pstmt.setString(2, senderName);
			pstmt.setString(3, recipientName);
			pstmt.setString(4, questionID);
			pstmt.setString(5, message);
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				return "Message inserted successfully";
			}
		} catch (SQLException e) {
		e.printStackTrace();
			return "Error inserting message: " + e.getMessage();
		}
		return "Insert operation completed, but no rows affected.";
	}
			
	// returns an array of answers corresponding to a question ID
	public ArrayList<Answer> getAnswersForQuestion(Question q) {
		ArrayList<Answer> answers = new ArrayList<>();
		String sql = "SELECT * FROM answersGiven WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, q.getQuestionID());
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String answerID = rs.getString("answerID");
					String qID = rs.getString("questionID");
					String userName = rs.getString("userName");
					String answerText = rs.getString("answerText");
					boolean resolution = rs.getBoolean("resolution");
					Answer answer = new Answer(answerID, qID, userName, answerText, resolution);
					if (resolution) answers.add(0, answer);
					else answers.add(answer);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answers;
	}
	
	// returns an array of reviews corresponding to a question ID
	public ArrayList<Review> getReviewsForQuestion(Question q) {
		return getReviewsForSubject(q.getQuestionID());
	}
	
	// returns an array of reviews corresponding to an Answer ID
	public ArrayList<Review> getReviewsForAnswer(Answer a) {
		return getReviewsForSubject(a.getAnswerID());
	}
	
	public ArrayList<Review> getReviewsForSubject(String sID) {
		ArrayList<Review> reviews = new ArrayList<>();
		String sql = "SELECT * FROM reviewsGiven WHERE subjectID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, sID);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String reviewID = rs.getString("reviewID");
					String subjectID = rs.getString("subjectID");
					String userName = rs.getString("userName");
					String reviewText = rs.getString("reviewText");
					Review review = new Review(reviewID, subjectID, userName, reviewText);
					reviews.add(review);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reviews;
	}
	
	// returns an array of reviews corresponding to a question ID
	public ArrayList<Review> getReviewsForQuestion(Question q, String userName) {
		return getReviewsForSubject(q.getQuestionID(), userName);
	}
	
	// returns an array of reviews corresponding to an Answer ID
	public ArrayList<Review> getReviewsForAnswer(Answer a, String userName) {
		return getReviewsForSubject(a.getAnswerID(), userName);
	}
	
	public ArrayList<Review> getReviewsForSubject(String sID, String voterUserName) {
	    ArrayList<Review> reviews = new ArrayList<>();
	    // Join reviewsGiven with reviewVotes filtered by the current student.
	    // Reviews that the student upvoted (positive vote) will appear first.
	    String sql = "SELECT r.*, rv.vote " +
	                 "FROM reviewsGiven r " +
	                 "LEFT JOIN reviewVotes rv ON r.reviewID = rv.reviewID AND rv.voterUserName = ? " +
	                 "WHERE r.subjectID = ? " +
	                 "ORDER BY rv.vote DESC NULLS LAST";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	         pstmt.setString(1, voterUserName);
	         pstmt.setString(2, sID);
	         try (ResultSet rs = pstmt.executeQuery()) {
	             while (rs.next()){
	                String reviewID = rs.getString("reviewID");
	                String subjID = rs.getString("subjectID");
	                String userName = rs.getString("userName");
	                String reviewText = rs.getString("reviewText");
	                
	                Review review = new Review(reviewID, subjID, userName, reviewText);
	                reviews.add(review);
	             }
	         }
	    } catch(SQLException e) {
	         e.printStackTrace();
	    }
	    return reviews;
	}
	
	// returns a list of messages for a given recipient
		public ArrayList<Message> getInbox(String userName) {
			ArrayList<Message> messages = new ArrayList<>();
			String sql = "SELECT * FROM privateMessages WHERE recipient = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, userName);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String messageID = rs.getString("messageID");
						String questionID = rs.getString("questionID");
						String sender = rs.getString("sender");
						String recipient = rs.getString("recipient");
						String messageBody = rs.getString("message");
						Message message = new Message(messageID, sender, recipient, questionID, messageBody);
						messages.add(0, message);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return messages;
		}
		/**
		 * Inserts a moderation note for a given user.
		 * @param userName The user to whom the note applies.
		 * @param noteText The content of the note.
		 */
		public void insertModerationNote(String userName, String noteText) {
		    String sql = "INSERT INTO moderationNotes (noteID, userName, noteText, createdBy, timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        // Generate a unique noteID using UUID
		        String noteID = UUID.randomUUID().toString();
		        pstmt.setString(1, noteID);
		        pstmt.setString(2, userName);
		        pstmt.setString(3, noteText);
		        pstmt.setString(4, "Staff"); // Or the current staff user's username if available.
		        pstmt.executeUpdate();
		        System.out.println("Inserted moderation note with ID: " + noteID);
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}

		/**
		 * Retrieves all moderation notes for a given user.
		 * @param userName The user whose notes are to be retrieved.
		 * @return An ArrayList of Strings representing the notes.
		 */
		public ArrayList<String> getModerationNotes(String userName) {
		    ArrayList<String> notes = new ArrayList<>();
		    String sql = "SELECT noteText FROM moderationNotes WHERE userName = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        pstmt.setString(1, userName);
		        try (ResultSet rs = pstmt.executeQuery()){
		            while(rs.next()) {
		                notes.add(rs.getString("noteText"));
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return notes;
		}

		/**
		 * Flags a question in the database.
		 * @param questionID The ID of the question to flag.
		 */
		public void flagQuestion(String questionID) {
		    String sql = "UPDATE questionsAsked SET flagged = TRUE WHERE questionID = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        pstmt.setString(1, questionID);
		        pstmt.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}

		/**
		 * Flags an answer in the database.
		 * @param answerID The ID of the answer to flag.
		 */
		public void flagAnswer(String answerID) {
		    String sql = "UPDATE answersGiven SET flagged = TRUE WHERE answerID = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        pstmt.setString(1, answerID);
		        pstmt.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}

		/**
		 * Flags a private message in the database.
		 * @param messageID The ID of the message to flag.
		 */
		public void flagMessage(String messageID) {
		    String sql = "UPDATE privateMessages SET flagged = TRUE WHERE messageID = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        pstmt.setString(1, messageID);
		        pstmt.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		/**
		 * Retrieves all private messages from the database.
		 * @return An ArrayList of all Message objects.
		 */
		public ArrayList<Message> getAllPrivateMessages() {
		    ArrayList<Message> messages = new ArrayList<>();
		    String sql = "SELECT * FROM privateMessages";
		    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                String messageID = rs.getString("messageID");
		                String questionID = rs.getString("questionID");
		                String sender = rs.getString("sender");
		                String recipient = rs.getString("recipient");
		                String messageBody = rs.getString("message");
		                Message message = new Message(messageID, sender, recipient, questionID, messageBody);
		                messages.add(message);
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return messages;
		}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}