/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.FrontDesk.Management.GuestManagement;

import Database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import raven.datetime.DatePicker;

/**
 *
 * @author ADMIN
 */
public class CheckIn extends javax.swing.JPanel {
    private final DatePicker datePicker = new DatePicker();
    private final DatePicker checkOutPicker = new DatePicker();
    private final DatePicker checkInPicker = new DatePicker();
    /**
     * Creates new form CheckIn
     */
    Connection conn;
    public CheckIn() {
        conn = DBConnection.connectDB();
        initComponents();
        datePicker.setEditor(birthField);
        checkInPicker.setEditor(checkInField);
        checkOutPicker.setEditor(checkOutField);
        ButtonGroup group = new ButtonGroup();
        group.add(femaleRadio);
        group.add(maleRadio);
        initTables();
    }
    
    private void clearGuestInfo(){
        guestField.setText("");
        contactField.setText("");
        emailField.setText("");
        birthField.setText("");
        nationalityField.setText("");
        femaleRadio.setSelected(false);
        maleRadio.setSelected(false);
        idNumberField.setText("");
        idTypeCombo.setSelectedIndex(0);
    }
    
    private void initTables(){
        String reservationSQL = "SELECT * FROM Guest_Reservation";
        String roomSQL = "SELECT rt.Room_Number, rt.Room_Type, rr.Price_Rate " +
                     "FROM RoomList AS rt " +
                     "INNER JOIN Room_Rate AS rr ON rt.Room_Type = rr.Room_Type";;
        try {
        PreparedStatement pst1 = conn.prepareStatement(reservationSQL);
        ResultSet rs1 = pst1.executeQuery();
        DefaultTableModel dt1 = (DefaultTableModel) reservationTable.getModel();
        dt1.setRowCount(0);

        while (rs1.next()) {
            String reservationNumber = String.format("R%05d", rs1.getInt("ReservationID"));
            String guestName = rs1.getString("FullName");
            String checkInDate = rs1.getString("CheckIn_Date");
            String arrival = rs1.getString("Estimated_Arrival");

            dt1.addRow(new Object[]{reservationNumber, guestName, checkInDate, arrival});
        }

        PreparedStatement pst2 = conn.prepareStatement(roomSQL);
        ResultSet rs2 = pst2.executeQuery();
        DefaultTableModel dt2 = (DefaultTableModel) roomTable.getModel();
        dt2.setRowCount(0);

        while (rs2.next()) {
            String roomNumber = rs2.getString("Room_Number");
            String roomType = rs2.getString("Room_Type");
            Double price = rs2.getDouble("Price_Rate");

            dt2.addRow(new Object[]{roomNumber, roomType, String.format("₱%.2f",price)});
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < reservationTable.getColumnCount(); i++) {
            reservationTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        for (int i = 0; i < roomTable.getColumnCount(); i++) {
            roomTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
private void checkInFunction() {
    String fullname = guestField.getText();
    String contact = contactField.getText();
    String email = emailField.getText();
    String birthDate = birthField.getText();
    String nationality = nationalityField.getText();
    String gender = "";
    if (maleRadio.isSelected()) {
        gender = "Male";
    } else if (femaleRadio.isSelected()) {
        gender = "Female";
    }
    String idNumber = idNumberField.getText();
    String idType = (String) idTypeCombo.getSelectedItem();
    String roomType = (String) roomTypeCombo.getSelectedItem();
    String roomNumber = (String) roomNumberCombo.getSelectedItem();
    String checkIn = checkInField.getText();
    String checkOut = checkOutField.getText();
    int numberOfGuest = (int) guestSpinner.getValue();
    int extraLinen = (int) linenSpinner.getValue();
    int extraBed = (int) bedSpinner.getValue();
    boolean isLaundry = laundryCheck.isSelected();
    boolean isBarAccessible = barCheck.isSelected();
    boolean isEarly = earlyCheck.isSelected();
    boolean isParking = parkingCheck.isSelected();

    if (fullname.trim().isEmpty() || contact.trim().isEmpty() ||
        email.trim().isEmpty() || birthDate.trim().isEmpty() ||
        nationality.trim().isEmpty() || gender.isEmpty() ||
        idNumber.trim().isEmpty() || roomNumber == null ||
        checkIn.trim().isEmpty() || checkOut.trim().isEmpty()) {

        JOptionPane.showMessageDialog(this, "Please fill in all required fields!",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {

        String checkRoomSql = "SELECT Status FROM RoomList WHERE Room_Number = ? AND Room_Type = ?";
        PreparedStatement checkPst = conn.prepareStatement(checkRoomSql);
        checkPst.setString(1, roomNumber);
        checkPst.setString(2, roomType);
        ResultSet rs = checkPst.executeQuery();

        if (rs.next()) {
            String currentStatus = rs.getString("Status");
            if (!"Available".equals(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Room " + roomNumber + " is currently " + currentStatus + "!",
                        "Room Not Available", JOptionPane.WARNING_MESSAGE);
                conn.rollback();
                conn.setAutoCommit(true);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Room not found in the system!",
                    "Room Error", JOptionPane.ERROR_MESSAGE);
            conn.rollback();
            conn.setAutoCommit(true);
            return;
        }

        // Insert guest info and get generated BookingID
        String insertGuestSql = "INSERT INTO Guest_Information (FullName, ContactNumber, Email, BirthDate, " +
                "Gender, Nationality, IDType, IDNumber, RoomNumber, RoomType, CheckIn, CheckOut, " +
                "NumberOfGuest, ExtraLinen, ExtraBed, Laundry, MiniBar, EarlyIn, Parking) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement insertPst = conn.prepareStatement(insertGuestSql);
        insertPst.setString(1, fullname);
        insertPst.setString(2, contact);
        insertPst.setString(3, email);
        insertPst.setString(4, birthDate);
        insertPst.setString(5, gender);
        insertPst.setString(6, nationality);
        insertPst.setString(7, idType);
        insertPst.setString(8, idNumber);
        insertPst.setString(9, roomNumber);
        insertPst.setString(10, roomType);
        insertPst.setString(11, checkIn);
        insertPst.setString(12, checkOut);
        insertPst.setInt(13, numberOfGuest);
        insertPst.setInt(14, extraLinen);
        insertPst.setInt(15, extraBed);
        insertPst.setBoolean(16, isLaundry);
        insertPst.setBoolean(17, isBarAccessible);
        insertPst.setBoolean(18, isEarly);
        insertPst.setBoolean(19, isParking);

        int guestResult = insertPst.executeUpdate();

        if (guestResult > 0) {
            ResultSet generatedKeys = insertPst.getGeneratedKeys();
            int guestID = -1;
            if (generatedKeys.next()) {
                guestID = generatedKeys.getInt(1);
            }

            // Update RoomList with guest ID and dates
            String updateRoomSql = "UPDATE RoomList SET Status = 'Occupied', GuestID = ?, CheckIn = ?, CheckOut = ? WHERE Room_Number = ? AND Room_Type = ?";
            PreparedStatement updatePst = conn.prepareStatement(updateRoomSql);
            updatePst.setInt(1, guestID);
            updatePst.setString(2, checkIn);
            updatePst.setString(3, checkOut);
            updatePst.setString(4, roomNumber);
            updatePst.setString(5, roomType);

            int roomResult = updatePst.executeUpdate();

            if (roomResult > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Guest checked in successfully!\nRoom " + roomNumber + " is now occupied.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Failed to update room status!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Failed to check in guest!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        conn.setAutoCommit(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void clearForm() {
        guestField.setText("");
        contactField.setText("");
        emailField.setText("");
        birthField.setText("");
        nationalityField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        idNumberField.setText("");
        idTypeCombo.setSelectedIndex(0);
        roomTypeCombo.setSelectedIndex(0);
        roomNumberCombo.setSelectedIndex(0);
        checkInField.setText("");
        checkOutField.setText("");
        guestSpinner.setValue(1);
        linenSpinner.setValue(0);
        bedSpinner.setValue(0);
        laundryCheck.setSelected(false);
        barCheck.setSelected(false);
        earlyCheck.setSelected(false);
        parkingCheck.setSelected(false);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel11 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        guestField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        contactField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        maleRadio = new javax.swing.JRadioButton();
        femaleRadio = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        nationalityField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        idTypeCombo = new javax.swing.JComboBox<>();
        idNumberField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        roomTypeCombo = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        linenSpinner = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        earlyCheck = new javax.swing.JCheckBox();
        laundryCheck = new javax.swing.JCheckBox();
        parkingCheck = new javax.swing.JCheckBox();
        guestSpinner = new javax.swing.JSpinner();
        bedSpinner = new javax.swing.JSpinner();
        barCheck = new javax.swing.JCheckBox();
        infoClearBtn = new javax.swing.JButton();
        birthField = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        checkInField = new javax.swing.JFormattedTextField();
        checkOutField = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        reservationTable = new javax.swing.JTable();
        reservationCheckInBtn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        roomTable = new javax.swing.JTable();
        roomNumberCombo = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();

        jLabel11.setText("jLabel11");

        jLabel18.setText("jLabel18");

        jButton3.setText("jButton3");

        jButton5.setText("jButton5");

        jPanel1.setBackground(new java.awt.Color(19, 19, 19));

        jLabel1.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(212, 171, 97));
        jLabel1.setText("Room Details:");

        jLabel2.setForeground(new java.awt.Color(212, 171, 97));
        jLabel2.setText("Full Name:");

        jLabel3.setForeground(new java.awt.Color(212, 171, 97));
        jLabel3.setText("Contact Number:");

        jLabel4.setForeground(new java.awt.Color(212, 171, 97));
        jLabel4.setText("Email Address:");

        maleRadio.setForeground(new java.awt.Color(212, 171, 97));
        maleRadio.setText("Male");

        femaleRadio.setForeground(new java.awt.Color(212, 171, 97));
        femaleRadio.setText("Female");

        jLabel5.setForeground(new java.awt.Color(212, 171, 97));
        jLabel5.setText("Gender:");

        jLabel6.setForeground(new java.awt.Color(212, 171, 97));
        jLabel6.setText("Date of Birth:");

        jLabel7.setForeground(new java.awt.Color(212, 171, 97));
        jLabel7.setText("Nationality:");

        jLabel8.setForeground(new java.awt.Color(212, 171, 97));
        jLabel8.setText("ID Type:");

        idTypeCombo.setForeground(new java.awt.Color(212, 171, 97));
        idTypeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Passport", "National ID ", "Driver’s License", "SSS ID / UMID Card ", "GSIS ID / UMID Card", "PRC ID", "Voter’s ID / Voter’s Certification " }));

        jLabel9.setForeground(new java.awt.Color(212, 171, 97));
        jLabel9.setText("ID Number:");

        jLabel10.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(212, 171, 97));
        jLabel10.setText("Guest List:");

        roomTypeCombo.setForeground(new java.awt.Color(212, 171, 97));
        roomTypeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Single", "Twin", "Double", "Deluxe", "Executive", "Suite", "Presidential" }));
        roomTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomTypeComboActionPerformed(evt);
            }
        });

        jLabel13.setForeground(new java.awt.Color(212, 171, 97));
        jLabel13.setText("Room Type:");

        jLabel15.setForeground(new java.awt.Color(212, 171, 97));
        jLabel15.setText("Check-In Date:");

        jLabel16.setForeground(new java.awt.Color(212, 171, 97));
        jLabel16.setText("Check-Out Date:");

        jLabel17.setForeground(new java.awt.Color(212, 171, 97));
        jLabel17.setText("Number of Guests:");

        jButton1.setBackground(new java.awt.Color(212, 171, 97));
        jButton1.setForeground(new java.awt.Color(19, 19, 19));
        jButton1.setText("Confirm Check-In");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel20.setForeground(new java.awt.Color(212, 171, 97));
        jLabel20.setText("Add-Ons / Extra Services:");

        earlyCheck.setForeground(new java.awt.Color(212, 171, 97));
        earlyCheck.setText("Early Check In");

        laundryCheck.setForeground(new java.awt.Color(212, 171, 97));
        laundryCheck.setText("Laundry Service");

        parkingCheck.setForeground(new java.awt.Color(212, 171, 97));
        parkingCheck.setText("Parking");
        parkingCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parkingCheckActionPerformed(evt);
            }
        });

        barCheck.setForeground(new java.awt.Color(212, 171, 97));
        barCheck.setText("Mini Bar Access");

        infoClearBtn.setBackground(new java.awt.Color(212, 171, 97));
        infoClearBtn.setForeground(new java.awt.Color(19, 19, 19));
        infoClearBtn.setText("Clear All");
        infoClearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoClearBtnActionPerformed(evt);
            }
        });

        jLabel24.setForeground(new java.awt.Color(212, 171, 97));
        jLabel24.setText("Extra Linen");

        jLabel26.setForeground(new java.awt.Color(212, 171, 97));
        jLabel26.setText("Extra Bed");

        jLabel12.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(212, 171, 97));
        jLabel12.setText("Available Rooms:");

        reservationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Guest Name", "Check-In Date", "Estimated Arrival"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        reservationTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(reservationTable);
        if (reservationTable.getColumnModel().getColumnCount() > 0) {
            reservationTable.getColumnModel().getColumn(0).setResizable(false);
            reservationTable.getColumnModel().getColumn(1).setResizable(false);
            reservationTable.getColumnModel().getColumn(2).setResizable(false);
            reservationTable.getColumnModel().getColumn(3).setResizable(false);
        }

        reservationCheckInBtn.setBackground(new java.awt.Color(212, 171, 97));
        reservationCheckInBtn.setForeground(new java.awt.Color(19, 19, 19));
        reservationCheckInBtn.setText("Check-In");
        reservationCheckInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservationCheckInBtnActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(212, 171, 97));
        jLabel14.setText("Reservation List:");

        roomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Room No.", "Type", "Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        roomTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(roomTable);
        if (roomTable.getColumnModel().getColumnCount() > 0) {
            roomTable.getColumnModel().getColumn(0).setResizable(false);
            roomTable.getColumnModel().getColumn(1).setResizable(false);
            roomTable.getColumnModel().getColumn(2).setResizable(false);
        }

        roomNumberCombo.setForeground(new java.awt.Color(212, 171, 97));

        jLabel19.setForeground(new java.awt.Color(212, 171, 97));
        jLabel19.setText("Room Number:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(guestField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(birthField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(nationalityField, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel5)
                        .addGap(200, 200, 200)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(maleRadio)
                        .addGap(60, 60, 60)
                        .addComponent(femaleRadio)
                        .addGap(71, 71, 71)
                        .addComponent(idNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(idTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel13)
                        .addGap(178, 178, 178)
                        .addComponent(jLabel15)
                        .addGap(176, 176, 176)
                        .addComponent(jLabel16)
                        .addGap(157, 157, 157)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(40, 40, 40)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(laundryCheck)
                                .addComponent(barCheck)
                                .addComponent(earlyCheck)
                                .addComponent(parkingCheck)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(90, 90, 90)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(linenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(bedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGap(76, 76, 76)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(32, 32, 32)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel19)
                                        .addComponent(roomNumberCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(31, 31, 31)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(149, 149, 149)
                                    .addComponent(reservationCheckInBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(30, 30, 30)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(roomTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(31, 31, 31)
                                    .addComponent(checkInField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(44, 44, 44)
                                    .addComponent(checkOutField, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(35, 35, 35)
                                    .addComponent(guestSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(134, 134, 134)
                                    .addComponent(infoClearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(8, 8, 8))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel10)
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guestField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(birthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nationalityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(maleRadio))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(femaleRadio))
                    .addComponent(idNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(idTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel13))
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel17)))
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(roomTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkInField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkOutField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(guestSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(infoClearBtn))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jButton1)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(reservationCheckInBtn)
                                    .addComponent(jLabel12))))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(4, 4, 4)
                        .addComponent(roomNumberCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel20)
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(linenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(bedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laundryCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(earlyCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(parkingCheck)
                        .addGap(0, 108, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void parkingCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parkingCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_parkingCheckActionPerformed

    private void roomTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomTypeComboActionPerformed
        String type = (String) roomTypeCombo.getSelectedItem();
        String sql = "SELECT * FROM RoomList WHERE Room_Type = '" + type +"' AND Status = 'Available'";
        
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            ResultSet rs = pst.executeQuery();
            roomNumberCombo.removeAllItems();

        while (rs.next()) {
            String roomNumber = rs.getString("Room_Number");
            if (roomNumber != null && !roomNumber.trim().isEmpty()) {
                roomNumberCombo.addItem(roomNumber);
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_roomTypeComboActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        checkInFunction();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void reservationCheckInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservationCheckInBtnActionPerformed
      int row = reservationTable.getSelectedRow();
      String selection = reservationTable.getModel().getValueAt(row, 0).toString();
      String sql = "SELECT * FROM Guest_Reservation WHERE ReservationId = " + Integer.parseInt(selection.substring(1));
      try(PreparedStatement pst = conn.prepareStatement(sql)){
          ResultSet rs = pst.executeQuery();
          while(rs.next()){
              guestField.setText(rs.getString("FullName"));
              contactField.setText(rs.getString("ContactNumber"));
              emailField.setText(rs.getString("Email"));
              checkInField.setText(rs.getString("CheckIn_Date"));
              checkOutField.setText(rs.getString("CheckOut_Date"));
              guestSpinner.setValue(rs.getInt("NumberOfGuests"));
              linenSpinner.setValue(rs.getInt("ExtraLinen"));
              bedSpinner.setValue(rs.getInt("ExtraBed"));
              laundryCheck.setSelected(rs.getBoolean("Laundry"));
              barCheck.setSelected(rs.getBoolean("MiniBar"));
              parkingCheck.setSelected(rs.getBoolean("Parking"));
          }
      }catch(Exception e){
          e.printStackTrace();
      }
    }//GEN-LAST:event_reservationCheckInBtnActionPerformed

    private void infoClearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoClearBtnActionPerformed
        clearForm();
    }//GEN-LAST:event_infoClearBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox barCheck;
    private javax.swing.JSpinner bedSpinner;
    private javax.swing.JFormattedTextField birthField;
    private javax.swing.JFormattedTextField checkInField;
    private javax.swing.JFormattedTextField checkOutField;
    private javax.swing.JTextField contactField;
    private javax.swing.JCheckBox earlyCheck;
    private javax.swing.JTextField emailField;
    private javax.swing.JRadioButton femaleRadio;
    private javax.swing.JTextField guestField;
    private javax.swing.JSpinner guestSpinner;
    private javax.swing.JTextField idNumberField;
    private javax.swing.JComboBox<String> idTypeCombo;
    private javax.swing.JButton infoClearBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox laundryCheck;
    private javax.swing.JSpinner linenSpinner;
    private javax.swing.JRadioButton maleRadio;
    private javax.swing.JTextField nationalityField;
    private javax.swing.JCheckBox parkingCheck;
    private javax.swing.JButton reservationCheckInBtn;
    private javax.swing.JTable reservationTable;
    private javax.swing.JComboBox<String> roomNumberCombo;
    private javax.swing.JTable roomTable;
    private javax.swing.JComboBox<String> roomTypeCombo;
    // End of variables declaration//GEN-END:variables
}
