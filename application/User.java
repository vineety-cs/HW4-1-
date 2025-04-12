package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
	
    private String userName;
    private String name;
    private String email;
    private String password;
    private String[] roles = new String[8];

    // Constructor to initialize a new User object with userName, name, email, password, and 0-5 roles.
    public User(String userName, String name, String email, String password, String[] roles) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.email = email;
        if (roles != null) {
	        for (int i = 0; i < 7; i++) {
	        	if (i < roles.length) this.roles[i] = roles[i];
	        	else this.roles[i] = "";
	        }
        }
    }
    
    // Sets email
    public void setEmail(String email) {
    	this.email = email;
    }
    
    // Sets name
    public void setName(String name) {
    	this.name = name;
    }
    
    // Sets the role of the user.
    public void setRoles(String[] roles) {
        for (int i = 0; i < 7; i++) {
        	if (i < roles.length) this.roles[i] = roles[i];
        	else this.roles[i] = "";
        }
    }
    
    // Gives the user a role if it is a valid role and they do not already have it.
    public String addRole(String role) {
    	if (!role.equals("Admin") && !role.equals("Student") && !role.equals("Instructor") && !role.equals("Staff") && !role.equals("Reviewer")) {
    		return ("\"" + role + "\" is not a valid role. Valid roles are Admin, Student, Instructor, Staff, and Reviewer.");
    	}
    	for (int i = 0; i < roles.length; i++) {
    		if (role.equals(roles[i])) return (userName + " already has the " + role + " role.");
    		if (roles[i] == "" || roles[i] == null) {
    			roles[i] = role;
    			return (userName + " has been given the " + role + " role.");
    		}
    	}
    	return "An error occurred while attempting to add the role.";
    }
    
    // Takes a role away from the user if the role is valid and not the Admin role.
    public String removeRole(String role) {
    	if (!role.equals("Admin") && !role.equals("Student") && !role.equals("Instructor") && !role.equals("Staff") && !role.equals("Reviewer")) {
    		return ("\"" + role + "\" is not a valid role. Valid roles are Admin, Student, Instructor, Staff, and Reviewer.");
    	}
    	if (role == "Admin") {
    		return "You cannot remove the Admin role.";
    	}
    	for (int i = 0; i < roles.length; i++) {
    		System.out.println(roles[i] + " " + role);
    		if (role.equals(roles[i])) {
    			for (int j = i; j < roles.length - 1; j++) {
    				roles[j] = roles[j + 1];
    			}
    			return ("Success: " + userName + " no longer has the " + role + " role.");
    		}
    	}
    	return ("Error: " + userName + " does not have the " + role + " role.");
    }
    

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String[] getRoles() { return roles; } // Returns the user's roles as an array
    public String getRoleList() {  // Returns the user's roles as a String for the purposes of storing in SQL database
    	String roleList = roles[0];
    	for (int i = 1; i < roles.length; i++) {
    		if (roles[i] != "" && roles[i] != null) roleList = roleList + ", " + roles[i];
    	}
    	return roleList; 
    }
}
