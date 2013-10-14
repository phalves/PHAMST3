package controller.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.User;

public class DBUtils {
	private static DBUtils instance = null;

	public static final int STATE_1 = 1;
	public static final int STATE_2 = 2;

	public static DBUtils getDBUtilsInstace() {
		if (instance == null) {
			instance = new DBUtils();
		}

		return instance;
	}

	private DBUtils() {}

	private static final String mdbFile = "C:\\Users\\Paulo\\workspace\\T1_Seg_20131.mdb";
	private Connection connection;

	public void connect() {
		Connection connection = null;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			String connectionString = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + mdbFile + ";";
			connection = DriverManager.getConnection(connectionString, "", "");

			this.connection = connection;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't stablish connection to " + mdbFile);
		}
	}

	public void disconnect() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to disconnect from " + mdbFile);
		}
	}

	//Input: user to be saved and his role Id
	public void saveUser(User user, int role_Id) {
		String sql = "INSERT INTO Usuarios(UserName, Nome, SALT, Passwd, PublicKey, Grupos_Id)" +
				" VALUES('"+ user.getName() + "', '" + user.getNomeProprio() + "', " +
				user.getSALT() + ", '" + user.getPasswd() + "', '" +
				user.getPublicKey() + "', " + role_Id + ");";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
		//System.out.println(sql);
	}

	public void updateUserPasswdAndPublicKey(User user) {
		String sql = "UPDATE Usuarios SET Passwd = '" + user.getPasswd() + "', "
				+ "PublicKey = '" + user.getPublicKey() + "', "
				+ "SALT = '" + user.getSALT() + "' WHERE UserName = '" +
				user.getName() + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
		//System.out.println(sql);
	}

	public String selectName(String name) {
		String returningName = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningName = resultSet.getString("UserName");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningName;
	}

	public String selectNomeProprio(String name) {
		String returningName = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningName = resultSet.getString("Nome");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningName;
	}

	public String getUserRole(String name) {
		String role = "";
		String sql = "SELECT * FROM Usuarios " +
				"INNER JOIN Grupos ON Usuarios.Grupos_Id = Grupos.Id " +
				"WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				role = resultSet.getString("Description");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return role;
	}

	public void incrementAccess(String name) {
		String sql = "UPDATE Usuarios SET Access = Access + 1 WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}
	
	public void incrementNumOfQueries(String name) {
		String sql = "UPDATE Usuarios SET NumOfQueries = NumOfQueries + 1 WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public int getNumberOfAccess(String name) {
		int n = 0;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				n = resultSet.getInt("Access");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return n;
	}

	public boolean isUserBlocked(String name) {
		Date blockedTime = null;
		Date blockedDate = null;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				blockedDate = resultSet.getDate("BlockedDate");
				blockedTime = resultSet.getTime("BlockedTime");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		if( blockedTime != null && blockedDate != null ) {

			Calendar dateBlocked = Calendar.getInstance();
			Calendar timeBlocked = Calendar.getInstance();
			timeBlocked.setTime( blockedTime );	
			dateBlocked.setTime( blockedDate );

			dateBlocked.set(dateBlocked.get(Calendar.YEAR),
					dateBlocked.get(Calendar.MONTH),
					dateBlocked.get(Calendar.DAY_OF_MONTH),
					timeBlocked.get(Calendar.HOUR_OF_DAY),
					timeBlocked.get(Calendar.MINUTE), 
					timeBlocked.get(Calendar.SECOND));

			Calendar dateTimeNow =  Calendar.getInstance();			
			dateBlocked.add( Calendar.MINUTE, 2 );

			if( dateBlocked.after( dateTimeNow ) ) {
				return true;
			}
		}
		return false;		
	}

	public int getNumberOfAttempts(String name, int state) {
		String column;
		if (state == STATE_1) {
			column = "NumberOfAttempts";
		} else {
			column = "Attempts2";
		}
		int numberOfAttempts = 0;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				numberOfAttempts = resultSet.getInt(column);
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return numberOfAttempts;
	}

	public void setNumberOfAttempts(int numberOfAttempts, String name, int state) {
		String table;
		if (state == STATE_1) {
			table = "NumberOfAttempts";
		} else {
			table = "Attempts2";
		}
		String sql = "UPDATE Usuarios SET " + table + " = " + numberOfAttempts + " WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public String selectSALT(String name) {
		String returningSALT = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningSALT = resultSet.getString("SALT");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningSALT;
	}

	public String selectPasswd(String name) {
		String returningPasswd = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningPasswd = resultSet.getString("Passwd");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningPasswd;
	}

	public String selectPublicKey(String name) {
		String returningPublicKey = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningPublicKey = resultSet.getString("PublicKey");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningPublicKey;
	}

	public void blockUser(String name) {
		Date now = new Date();
		String date = new SimpleDateFormat("dd/MM/yyyy").format( now );
		String time = new SimpleDateFormat("HH:mm:ss").format( now );
		String sql = "UPDATE Usuarios SET BlockedDate = '" + date + "'," +
				" BlockedTime = '" + time + "' WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public int getNumOfEdits(String name) {
		int numOfEdits = 0;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				numOfEdits = resultSet.getInt("NumOfEdits");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
		return numOfEdits;
	}
	
	public int getNumOfQueries(String name) {
		int numOfQueries = 0;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				numOfQueries = resultSet.getInt("NumOfQueries");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return numOfQueries;
	}

	public void incrementNumOfEdits(String name) {
		String sql = "UPDATE Usuarios SET NumOfEdits = NumOfEdits + 1 WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public int totalUsers() {
		int n = 0;
		String sql = "SELECT COUNT(*) AS N FROM Usuarios;";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				n = resultSet.getInt("N");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return n;
	}

	public void logMessage(int messageCode, String userName, String arqName) {
		Date now = new Date();
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( now );
		String sql = "INSERT INTO Registros(Mensagens_Code, Usuarios_UserName, Data, ArqName)" +
				" VALUES("+ messageCode + ", '" + userName + "', '" + date + "', '" + arqName + "');";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public void logMessage(int messageCode, String userName) {
		Date now = new Date();
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( now );
		String sql = "INSERT INTO Registros(Mensagens_Code, Usuarios_UserName, Data)" +
				" VALUES("+ messageCode + ", '" + userName + "', '" + date + "');";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}

	public void logMessage(int messageCode) {
		Date now = new Date();
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format( now );
		String sql = "INSERT INTO Registros(Mensagens_Code, Data)" +
				" VALUES("+ messageCode + ", '" + date + "');";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}
}
