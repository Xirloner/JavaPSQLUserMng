package interfaz;

import java.sql.*;
//as of now, your table must have the next tables: USER PASSWORD ROLE(int)
//apache codecs library (1.10) must be in your path, if not you wont be able to run the digestutils methods
import org.apache.commons.codec.digest.*;

private String USER_ID;
private String PWD;
private String TABLE;
private String Port;
private String Host;

public class UserMng {
	/**int createUser(String user) <p>
	 * Tries to create a new User</p>
	 *@param user id of the new user (String)
	 *@param pass password of the user, we store the sha256 hash of this value in the database
	 *@param role authorization level to use in your application
	 *@return if it fails we get a -1, if it success a 0. This is going too be changed in the future to boolean
	 * **/
	public static int createUser(String user, String pass, int role){
		Connection sqlcon = null;
        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD");
			Statement st = sqlcon.createStatement();
			pass=org.apache.commons.codec.digest.DigestUtils.sha256Hex(pass);
			String query = "INSERT INTO USERS VALUES ('" + user  + "', '' , '' , '" + pass + "','" + Integer.toString(role) +"')";
			int rowsAffected = st.executeUpdate(query); 
			if(rowsAffected != 1){
					System.out.println("Failed to create the user");
				return -1;
			}	
		} catch (SQLException e1) {
					System.out.println("Failed to connect");
		}
		return 0;
	}
	
	/**int delUser(String user) <p> 
	 * Tries to delete an user from the table</p>
	 *@param user  id of the user that needs to be deleted (String)
	 *@return if it fails we get a -1, if it success a 0. This is going too be changed in the future to boolean
	 * **/
	public static int delUser(String user){
		Connection sqlcon = null;
        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD");
			Statement st = sqlcon.createStatement();
			String query = "DELETE FROM USERS WHERE user_id = '" + user + "'";
			int rowsAffected = st.executeUpdate(query); 
			if(rowsAffected != 1){
					System.out.println("Failed to delete the user");
				return -1;
			}	
		} catch (SQLException e1) {
					System.out.println("Failed to connect");
		}
		return 0;
	}
	
	/**int modUser(String oldUser, String newUser, String pass, int role) <p>
	 * Tries to modify the information for oldUser, in the end the id of the user becomes newUser </p>
	 *@param oldUser  id of the user we want to modify (String)
	 *@param newUser new id for the user  (String)
	 *@param pass new password for the user
	 *@param role new role for the user
	 *@return if it fails we get a -1, if it success a 0. This is going too be changed in the future to boolean
	 * **/
	public static int modUser(String oldUser, String newUser, String pass, int role){
			Connection sqlcon = null;
	        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD);
				Statement st = sqlcon.createStatement();
				String query="";
				if(pass.equals("")){
				query = "UPDATE USERS SET user_id = '" + newUser +  "', level = ' " + Integer.toString(role) + "' WHERE user_id = '" + oldUser + "'";
				}else{
				query = "UPDATE USERS SET user_id = '" + newUser + "',Pwd = '" + pass + "', level = ' " + Integer.toString(role) + "' WHERE user_id = '" + oldUser + "'";
				}
				int rowsAffected = st.executeUpdate(query); 
				if(rowsAffected != 1){
					System.out.println("Failed to modify user");
					return -1;
				}	
			} catch (SQLException e1) {
					System.out.println("Failed to connect");
			}
			return 0;
	}

	
	/**int checkRole(String user) <p>
	 * Gets the role of the user</p>
	 *@param user  id of the user we want to get the role from
	 *@return returns the role in case of success, -1 if we get an error, or -2 if something weird happens
	 * **/
	
	public static int checkRole(String user){
		int role = -2;
		Connection sqlcon = null;
        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD);
			Statement st = sqlcon.createStatement();
			String query = "SELECT level FROM USERS WHERE user_id = '" + user + "'";
			ResultSet reader = st.executeQuery(query); 
			boolean hasRows = reader.next();
			if(hasRows != true){
					System.out.println("Failed to query the user's role");
				return -1;
			}else{
				role = reader.getInt(1);
			}
		} catch (SQLException e1) {
					System.out.println("Failed to connect");
			return -1;
		}
		return role;
	}
	
	/**int checkUser(String user, String pass) <p>
	 * Tries to check for the legitamcy of an user comparing a given password with the matching stored password</p>
	 *@param user  user we want to validate  (String)
	 *@param pass password we are trying to validate
	 *@return if it fails we get a false, we get true otherwise
	 * **/
	public static boolean checkUser(String user, String pass){
		Connection sqlcon = null;
        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD);
			Statement st = sqlcon.createStatement();
			String query = "SELECT * FROM USERS WHERE user_id =" + "'" + user + "'";
			ResultSet answer = st.executeQuery(query);
			boolean hasRows = answer.next();
			if(hasRows == true){
				String accpwd = answer.getString(4).trim();
				String encpwd = org.apache.commons.codec.digest.DigestUtils.sha256Hex(pass); 
				if(accpwd.equalsIgnoreCase(encpwd) == true){	
					System.out.println("continue");
					return true;
				}else{
					System.out.println("Password doesn't match");
					return false;
				}
			}else{
					System.out.println("The user doesn't exists");
				return false;
			}
		} catch (SQLException e1) {
					System.out.println("Failed to connect");
			return false;
		}    
	}
	
	/**int changePass(String user, String newPass) <p>
	 * We tried to change and user password</p>
	 *@param user  id of the user we want to change its password
	 *@param newPass new password for the user 
	 *@return if it fails we get a false, we get true otherwise
	 * **/
	
	public static boolean changePass(String user, String newPass){
		Connection sqlcon = null;
        try {
			sqlcon = DriverManager.getConnection("jdbc:postgresql://" + Host + ":" + Port + "/" + TABLE, USER_ID, PWD);
			Statement st = sqlcon.createStatement();
			newPass=org.apache.commons.codec.digest.DigestUtils.sha256Hex(newPass);
			String	query = "UPDATE USERS SET pwd = '" + newPass +  "' WHERE user_id = '" + user + "'";
			int rowsAffected = st.executeUpdate(query); 
			if(rowsAffected != 1){
					System.out.println("Failed to change the password");
				return false;
			}else{
				return true;
			}
		} catch (SQLException e1) {
					System.out.println("Failed to connect");
			return false;
		}
	}
}
