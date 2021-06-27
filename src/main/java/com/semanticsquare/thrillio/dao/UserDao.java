package com.semanticsquare.thrillio.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.semanticsquare.thrillio.DataStore;
import com.semanticsquare.thrillio.constants.Gender;
import com.semanticsquare.thrillio.constants.UserType;
import com.semanticsquare.thrillio.entities.User;
import com.semanticsquare.thrillio.managers.UserManager;

public class UserDao {
	public List<User> getUsers() {
		return DataStore.getUsers();
	}

	public User getUser(int userId) {
		User user = null;
		
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			
			String query = "select * from User where id = " + userId;
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next()) {
				int id = rs.getInt("id");
				String email = rs.getString("email");
				String password = rs.getString("password");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				int genderId = rs.getInt("gender_id");
				Gender gender = Gender.values()[genderId];
				int userTypeId = rs.getInt("user_type_id");
				UserType userType = UserType.values()[userTypeId];
				
				user = UserManager.getInstance().createUser(id, email, password, firstName, lastName, gender, userType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return user;
	}

	public int authenticateUser(String email, String password) {
		
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jid_thrillio?useSSL=false", "root", "Spa0920Mys@wor");
				Statement stmt = conn.createStatement();) {
			
			String query = "select id from User where email = '" + email + "' and password = '" + password + "'";
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
