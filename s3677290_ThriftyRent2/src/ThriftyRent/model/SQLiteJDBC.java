package ThriftyRent.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ThriftyRent.util.DateTime;
import ThriftyRent.util.Utils;
import javafx.collections.FXCollections;
/*
 * helper class for accessing database
 * */
public class SQLiteJDBC {

	private static final String connectionString = "jdbc:sqlite:database/ThriftyRent.db";

	private void createTable() throws Exception {
		Connection c = null;
		Statement stmt = null;
		Exception exception = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(connectionString);
			//System.out.println("Opened database successfully for create");
			stmt = c.createStatement();
			String vehiclesTable = "CREATE TABLE if not exists vehicles"
					+ "(ID                  TEXT    PRIMARY KEY NOT NULL," + " make                TEXT   NOT NULL,"
					+ " model               TEXT   NOT NULL," + " image               TEXT   NOT NULL,"
					+ " status              TEXT   NOT NULL," + " lastMaintenanceDate TEXT ,"
					+ " year                integer," + " numberOfPassengers  integer)";

			String recordsTable = "CREATE TABLE if not exists rentalrecords"
					+ "(ID                  TEXT    PRIMARY KEY NOT NULL," + " VehicleID           TEXT  NOT NULL,"
					+ " rentDate            TEXT   NOT NULL," + " estimatedReturnDate TEXT   NOT NULL,"
					+ " actualReturnDate    TEXT," + " rentalFee           TEXT," + " lateFee             TEXT )";
			stmt.executeUpdate(vehiclesTable);
			stmt.executeUpdate(recordsTable);
		} catch (Exception e) {
			exception = e;
			//System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			if (stmt != null && !stmt.isClosed())
				stmt.close();
			if (c != null && !c.isClosed())
				c.close();
		}
		//.out.println("Table created successfully");
		if (exception != null)
			throw exception;
	}

	public void insertIntoTable(List<Vehicle> vehicleList) throws Exception {
		Connection c = null;
		Statement stmt = null;
		Exception exception = null;
		try {
			createTable();
			//System.out.println("start saveing to database");
			if (vehicleList == null || vehicleList.size() == 0)
				throw new Exception("No Vehciles to save");
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(connectionString);
			c.setAutoCommit(false);
			//System.out.println("Opened database successfully for insert");
			stmt = c.createStatement();
			for (int i = 0; i < vehicleList.size(); i++) {
				Vehicle v = vehicleList.get(i);
				//use replace command instead of insert to update vehicle if t alread exist
				//replace command delete old data first and then insert new  one
				//so it doesn't throw primary key already exist exception
				String vehicleInsert = "REPLACE INTO vehicles (ID,make,model,image,status,lastMaintenanceDate,year,numberOfPassengers)"
						+ "VALUES ('" + v.getVehicle_id() + "' , '" + v.getMake() + "' , '" + v.getModel() + "' , '"
						+ v.getImage() + "' , '" + v.getStatus() + "' , '" + v.getLastMaintenanceDate() + "' , '"
						+ v.getYear() + "' , '" + v.getNumberOfPassengers() + "');";
				stmt.executeUpdate(vehicleInsert);
				//System.out.println(i + " " + v.toString() + ":" + v.getImage());
				if (v.getRentalRecords() != null && v.getRentalRecords().size() > 0) {
					for (int j = v.getRentalRecords().size() - 1; j > -1; j--) {
						//System.out.print(j + " :  ");
						RentalRecord r = v.getRentalRecords().get(j);
						String rentalRecordInsert = "REPLACE INTO rentalrecords "
								+ "(ID,VehicleID,rentDate,estimatedReturnDate,actualReturnDate" + ",rentalFee,lateFee)"
								+ "VALUES ('" + r.getRecord_id() + "' , '" + v.getVehicle_id() + "' , '"
								+ r.getRentDate() + "' , '" + r.getEstimatedReturnDate() + "' , '"
								+ r.getActualReturnDate() + "' , '" + r.getRentalFee() + "' , '" + r.getLateFee()
								+ "');";
						stmt.executeUpdate(rentalRecordInsert);
						//System.out.print(r.toString());
						//System.out.println("");
					}
				}
			}

			//System.out.println("done saveing to database : " + vehicleList.size() + " vehicle were saved.");
			c.commit();
		} catch (Exception e) {
			//System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//System.exit(0);
			exception = e;
		} finally {
			if (stmt != null && !stmt.isClosed())
				stmt.close();
			if (c != null && !c.isClosed())
				c.close();
		}
		if (exception != null)
			throw exception;

	}

	public List<Vehicle> selectFromDB() throws Exception {
		Exception exception = null;
		Connection c = null;
		Statement stmt = null;
		List<Vehicle> vehicles = new ArrayList<>();
		try {
			createTable();
			//System.out.println("Read started");
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(connectionString);
			c.setAutoCommit(false);
			stmt = c.createStatement();
			List<RentalRecord> rentalRecords = new ArrayList<>();
			ResultSet rentRecordsResultSet = stmt.executeQuery(" SELECT * FROM rentalrecords;");
			while (rentRecordsResultSet.next()) {
				String rid = rentRecordsResultSet.getString("ID");
				String vehicle_id = rentRecordsResultSet.getString("VehicleID");
				DateTime rentDate = new DateTime(rentRecordsResultSet.getString("rentDate"));
				DateTime estimatedReturnDate = new DateTime(rentRecordsResultSet.getString("estimatedReturnDate"));
				DateTime actualReturnDate = null;
				if (!Utils.isEmpty(rentRecordsResultSet.getString("actualReturnDate")))
					actualReturnDate = new DateTime(rentRecordsResultSet.getString("actualReturnDate"));
				String rentalFee = rentRecordsResultSet.getString("rentalFee");
				String lateFee = rentRecordsResultSet.getString("lateFee");
				RentalRecord r = new RentalRecord();
				r.setRecord_id(rid);
				r.setVehicle_id(vehicle_id);
				r.setRentDate(rentDate);
				r.setActualReturnDate(actualReturnDate);
				r.setEstimatedReturnDate(estimatedReturnDate);
				r.setLateFee(Utils.toDouble(lateFee));
				r.setRentalFee(Utils.toDouble(rentalFee));
				rentalRecords.add(r);
			}
			rentRecordsResultSet.close();

			ResultSet vehiclesResultSet = stmt.executeQuery(" SELECT * FROM vehicles");
			while (vehiclesResultSet.next()) {
				String id = vehiclesResultSet.getString("ID");
				String make = vehiclesResultSet.getString("make");
				String model = vehiclesResultSet.getString("model");
				String image = vehiclesResultSet.getString("image");
				String status = vehiclesResultSet.getString("status");
				String lastMaintenanceDate = vehiclesResultSet.getString("lastMaintenanceDate");
				int year = vehiclesResultSet.getInt("year");
				int numberOfPassengers = vehiclesResultSet.getInt("numberOfPassengers");
				Vehicle v;
				if ("V".equalsIgnoreCase(id.charAt(0) + ""))
					v = new Van();
				else
					v = new Car();
				v.setImage(image);
				v.setVehicle_id(id);
				v.setYear(year);
				v.setMake(make);
				v.setModel(model);
				v.setNumberOfPassengers(numberOfPassengers);
				v.setStatus(Vehicle.VehicleStatus.from(status));
				if (!"null".equalsIgnoreCase(lastMaintenanceDate) && lastMaintenanceDate.trim().length() > 8) {
					String[] s = lastMaintenanceDate.split("/");
					if (s != null && s.length > 2) {
						int day = Integer.parseInt(s[0]);
						int mon = Integer.parseInt(s[1]);
						int y = Integer.parseInt(s[2]);
						v.setLastMaintenanceDate(new DateTime(day, mon, y));
					}
				}
				v.setRentalRecords(FXCollections.observableArrayList());
				for (RentalRecord r : rentalRecords) {
					if (!Utils.isEmpty(r.getVehicle_id()) && r.getVehicle_id().equalsIgnoreCase(v.getVehicle_id()))
						v.getRentalRecords().add(r);
				}
				//v.getRentalRecords().sorted();
				vehicles.add(v);
			}
			vehiclesResultSet.close();
			//System.out.println("Read done successfully : " + vehicles.size() + " vehicle were loaded");
		} catch (Exception e) {
			// TODO: handle exception
			exception = e;
			if (e.getSuppressed() != null)
				for (Throwable t : e.getSuppressed()) {
					//System.err.println(t.getMessage());
				}

		} finally {

			if (stmt != null && !stmt.isClosed())
				stmt.close();
			if (c != null && !c.isClosed())
				c.close();
		}
		if (exception != null)
			throw exception;
		return vehicles;
	}
}