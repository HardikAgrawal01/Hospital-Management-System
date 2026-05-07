package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

import java.sql.SQLException;
import java.sql.Connection;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "8130775835";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctors doctor = new Doctors(connection);
            while(true){
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice : ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patient
                        patient.ViewPatient();
                        System.out.println();
                        break;
                    case 3:
//                      // View Doctors
                        doctor.ViewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointment
                        BookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                         return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void BookAppointment(Patient patient,Doctors doctor,Connection connection, Scanner scanner){
        System.out.println("Enter Patient ID : ");
        int patientID = scanner.nextInt();
        System.out.println("Enter Doctor ID : ");
        int doctorID = scanner.nextInt();
        System.out.println("Enter Appointment Date(YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if (patient.getPatientById(patientID) && doctor.getDoctorsById(doctorID)){
            if(checkDoctorAvailability(doctorID,appointmentDate,connection)){
                String appointmentQuery = "Insert Into appointments(patient_id,doctor_id,appointment_date) VALUES (?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected>0){
                        System.out.println("Appointment booked successfully");
                    }else{
                        System.out.println("Appointment not booked");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("doctor is not available on this date!");
            }
        }else{
            System.out.println("Either Doctor ID or Patient ID doesn't exist");
        }
    }
    public static boolean checkDoctorAvailability(int doctorID,String appointmentDate,Connection connection){
        String query = "Select count(*) from appointments where doctor_id=? and appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorID);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();

        }
        return false;
    }
}
