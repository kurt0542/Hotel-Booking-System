/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.FrontDesk.Management;

import Database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.toast.Notifications;

/**
 *
 * @author ADMIN
 */
public class Reservations extends javax.swing.JPanel {
    private final DatePicker datePicker1 = new DatePicker();
    private final DatePicker datePicker2 = new DatePicker();
    private final TimePicker timePicker = new TimePicker();
    private boolean isEditMode = false;
    private int selectedReservationId = -1;
    /**
     * Creates new form Reservations
     */
    Connection conn;
    public Reservations() {
     conn = DBConnection.connectDB();
        initComponents();
        datePicker1.setEditor(checkOutDate);
        datePicker2.setEditor(checkInDate);
        timePicker.setEditor(estimatedArrivalPicker);
        displayData();
    }
    
    private void errorNotif(String message){
        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, message);
    }
    
    private void addReservation() {
    String fullName = guestName.getText();
    String contact = contactField.getText();
    String email = emailField.getText();
    String checkIn = checkInDate.getText();
    String checkOut = checkOutDate.getText();
    String arrivalTime = estimatedArrivalPicker.getText();
    int numberOfRooms = (int) roomSpinner.getValue();
    int numberOfGuests = (int) guestSpinner.getValue();
    String note = notesTextArea.getText();

    // room type
    String isSingle = String.valueOf(singleCheck.isSelected());
    String isDouble = String.valueOf(doubleCheck.isSelected());
    String isTwin = String.valueOf(twinCheck.isSelected());
    String isExecutive = String.valueOf(executiveCheck.isSelected());
    String isDeluxe = String.valueOf(deluxeCheck.isSelected());
    String isSuite = String.valueOf(suiteCheck.isSelected());
    String isPresidential = String.valueOf(presidentialCheck.isSelected());

    // room add ons
    String isLaundry = String.valueOf(laundryCheck.isSelected());
    String isBar = String.valueOf(barCheck.isSelected());
    String isParking = String.valueOf(parkingCheck.isSelected());
    String isAirport = String.valueOf(airportCheck.isSelected());
    int extraBed = (int) extraBedSpinner.getValue();
    int extraLinen = (int) extraLinenSpinner.getValue();

    boolean hasRoomType = singleCheck.isSelected() || doubleCheck.isSelected() || 
                         twinCheck.isSelected() || executiveCheck.isSelected() || 
                         deluxeCheck.isSelected() || suiteCheck.isSelected() || 
                         presidentialCheck.isSelected();

    // Validation (same as before)
    if (fullName.isEmpty() || contact.isEmpty() || email.isEmpty()) {
        errorNotif("Please fill all of the fields");
        return;
    } else {
        if (fullName.length() < 2 || fullName.length() > 100) {
            errorNotif("Full name must be between 2-100 characters");
            return;
        } else if (!contact.matches("^09[0-9]{9}$")) {
            errorNotif("Invalid contact number format");
            return;
        } else if (!email.matches(".*@.*\\..*")) {
            errorNotif("Invalid email format");
            return;
        }
    }

    if (checkIn.isEmpty()) {
        errorNotif("Check-in date is required");
        return;
    }

    if (checkOut.isEmpty()) {
        errorNotif("Check-out date is required");
        return;
    }

    if (!checkIn.isEmpty() && !checkOut.isEmpty() && 
        !checkIn.equals("--/--/----") && !checkOut.equals("--/--/----")) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate checkInLocalDate = LocalDate.parse(checkIn, formatter);
            LocalDate checkOutLocalDate = LocalDate.parse(checkOut, formatter);
            LocalDate today = LocalDate.now();

            if (checkInLocalDate.isBefore(today)) {
                errorNotif("Check-in date cannot be in the past");
                return;
            }

            if (checkOutLocalDate.isBefore(checkInLocalDate) || checkOutLocalDate.isEqual(checkInLocalDate)) {
                errorNotif("Check-out date must be after check-in date");
                return;
            }
        } catch (DateTimeParseException e) {
            errorNotif("Invalid date format");
            return;
        }
    } else {
        errorNotif("Please enter both check-in and check-out dates");
        return;
    }

    if (numberOfRooms <= 0) {
        errorNotif("Number of rooms must be greater than 0");
        return;
    }
    if (numberOfGuests <= 0) {
        errorNotif("Number of guests must be greater than 0");
        return;
    }
    if (!hasRoomType) {
        errorNotif("Please select at least one room type");
        return;
    }
    if (extraBed < 0 || extraBed > 3) {
        errorNotif("Extra bed must be between 0-3");
        return;
    }
    if (extraLinen < 0 || extraLinen > 6) {
        errorNotif("Extra linen must be between 0-6");
        return;
    }

    
    String sql;
    if (isEditMode && selectedReservationId != -1) {
        
        sql = "UPDATE Guest_Reservation SET FullName=?, ContactNumber=?, Email=?, CheckIn_Date=?, CheckOut_Date=?, " +
              "Estimated_Arrival=?, NumberOfRooms=?, NumberOfGuests=?, Notes=?, Single=?, Double=?, Twin=?, Executive=?, " +
              "Deluxe=?, Suite=?, Presidential=?, Laundry=?, Parking=?, MiniBar=?, AirportPickUp=?, ExtraBed=?, ExtraLinen=? " +
              "WHERE ReservationID=?";
    } else {
        // Insert new reservation
        sql = "INSERT INTO Guest_Reservation (FullName, ContactNumber, Email, CheckIn_Date, CheckOut_Date, " +
              "Estimated_Arrival, NumberOfRooms, NumberOfGuests, Notes, Single, Double, Twin, Executive, " +
              "Deluxe, Suite, Presidential, Laundry, Parking, MiniBar, AirportPickUp, ExtraBed, ExtraLinen) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, fullName);
        pst.setString(2, contact);
        pst.setString(3, email);
        pst.setString(4, checkIn);
        pst.setString(5, checkOut);
        pst.setString(6, arrivalTime);
        pst.setInt(7, numberOfRooms);
        pst.setInt(8, numberOfGuests);
        pst.setString(9, note);
        pst.setString(10, isSingle);
        pst.setString(11, isDouble);
        pst.setString(12, isTwin);
        pst.setString(13, isExecutive);
        pst.setString(14, isDeluxe);
        pst.setString(15, isSuite);
        pst.setString(16, isPresidential);
        pst.setString(17, isLaundry);
        pst.setString(18, isParking);
        pst.setString(19, isBar);
        pst.setString(20, isAirport);
        pst.setInt(21, extraBed);
        pst.setInt(22, extraLinen);

        if (isEditMode && selectedReservationId != -1) {
            pst.setInt(23, selectedReservationId);
        }

        pst.executeUpdate();
        clearForm();
        displayData();
        
        // Reset edit mode
        if (isEditMode) {
            isEditMode = false;
            jToggleButton1.setSelected(false);
            jToggleButton1.setText("Edit Mode");
            ReserveBtn.setText("Reserve");
            selectedReservationId = -1;
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Reservation updated successfully!");
        } else {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Successfully added Reservation!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        errorNotif("Error saving reservation");
    }
}
     
    private void clearForm() {
    guestName.setText("");
    contactField.setText("");
    emailField.setText("");
    checkInDate.setText("");
    checkOutDate.setText("");
    estimatedArrivalPicker.setText("");
    roomSpinner.setValue(1);
    guestSpinner.setValue(1);
    notesTextArea.setText("");
    
    // Clear checkboxes
    singleCheck.setSelected(false);
    doubleCheck.setSelected(false);
    twinCheck.setSelected(false);
    executiveCheck.setSelected(false);
    deluxeCheck.setSelected(false);
    suiteCheck.setSelected(false);
    presidentialCheck.setSelected(false);
    
    // Clear add-ons
    laundryCheck.setSelected(false);
    barCheck.setSelected(false);
    parkingCheck.setSelected(false);
    airportCheck.setSelected(false);
    extraBedSpinner.setValue(0);
    extraLinenSpinner.setValue(0);
}
    
    private void displayData(){
        String sql = "SELECT * FROM Guest_Reservation";
        try(PreparedStatement pst = conn.prepareStatement(sql)){
            DefaultTableModel dt = (DefaultTableModel) reservedTable.getModel();
            dt.setRowCount(0);
                    ResultSet rs = pst.executeQuery();

            while(rs.next()){
                String reservationNumber = String.format("R%05d", rs.getInt("ReservationID"));
                String guestName = rs.getString("FullName");
                String checkInDate = rs.getString("CheckIn_Date");
                String checkOutDate = rs.getString("CheckOut_Date");


                dt.addRow(new Object[]{reservationNumber, guestName, checkInDate,  checkOutDate});
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < reservedTable.getColumnCount(); i++) {
                reservedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private void deleteFunction() {
        int rowcount = reservedTable.getSelectedRow();

        // Check if a row is selected
        if (rowcount == -1) {
            errorNotif("Please select a reservation to delete.");
            return;
        }

        String selection = reservedTable.getModel().getValueAt(rowcount, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete reservation " + selection + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM Guest_Reservation WHERE ReservationID = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int rawId = Integer.parseInt(selection.substring(1)); 

            pst.setInt(1, rawId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Reservation deleted successfully!");
                displayData(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorNotif("Error deleting reservation.");
        }
    }
    
    private void loadReservationForEdit(int row) {
    String selection = reservedTable.getModel().getValueAt(row, 0).toString();
    selectedReservationId = Integer.parseInt(selection.substring(1));
    
    String sql = "SELECT * FROM Guest_Reservation WHERE ReservationID = ?";
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setInt(1, selectedReservationId);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            // Load basic information
            guestName.setText(rs.getString("FullName"));
            contactField.setText(rs.getString("ContactNumber"));
            emailField.setText(rs.getString("Email"));
            checkInDate.setText(rs.getString("CheckIn_Date"));
            checkOutDate.setText(rs.getString("CheckOut_Date"));
            estimatedArrivalPicker.setText(rs.getString("Estimated_Arrival"));
            roomSpinner.setValue(rs.getInt("NumberOfRooms"));
            guestSpinner.setValue(rs.getInt("NumberOfGuests"));
            notesTextArea.setText(rs.getString("Notes"));
            
            // Load room types
            singleCheck.setSelected(rs.getString("Single").equals("true"));
            doubleCheck.setSelected(rs.getString("Double").equals("true"));
            twinCheck.setSelected(rs.getString("Twin").equals("true"));
            executiveCheck.setSelected(rs.getString("Executive").equals("true"));
            deluxeCheck.setSelected(rs.getString("Deluxe").equals("true"));
            suiteCheck.setSelected(rs.getString("Suite").equals("true"));
            presidentialCheck.setSelected(rs.getString("Presidential").equals("true"));
            
            // Load add-ons
            laundryCheck.setSelected(rs.getString("Laundry").equals("true"));
            barCheck.setSelected(rs.getString("MiniBar").equals("true"));
            parkingCheck.setSelected(rs.getString("Parking").equals("true"));
            airportCheck.setSelected(rs.getString("AirportPickUp").equals("true"));
            extraBedSpinner.setValue(rs.getInt("ExtraBed"));
            extraLinenSpinner.setValue(rs.getInt("ExtraLinen"));
        }
    } catch (Exception e) {
        e.printStackTrace();
        errorNotif("Error loading reservation data");
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reservedTable = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        checkOutLbl = new javax.swing.JLabel();
        guestNameLbl = new javax.swing.JLabel();
        guestName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        contactField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        guestSpinner = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        roomSpinner = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        doubleCheck = new javax.swing.JCheckBox();
        singleCheck = new javax.swing.JCheckBox();
        twinCheck = new javax.swing.JCheckBox();
        executiveCheck = new javax.swing.JCheckBox();
        deluxeCheck = new javax.swing.JCheckBox();
        suiteCheck = new javax.swing.JCheckBox();
        presidentialCheck = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        uncheckBtn = new javax.swing.JButton();
        laundryCheck = new javax.swing.JCheckBox();
        barCheck = new javax.swing.JCheckBox();
        parkingCheck = new javax.swing.JCheckBox();
        airportCheck = new javax.swing.JCheckBox();
        extraLinenSpinner = new javax.swing.JSpinner();
        extraBedSpinner = new javax.swing.JSpinner();
        clearBtn = new javax.swing.JButton();
        ReserveBtn = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        reservationIDLbl = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        checkInLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notesArea = new javax.swing.JTextArea();
        contactLbl = new javax.swing.JLabel();
        emailLbl = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        notesTextArea = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        checkOutDate = new javax.swing.JFormattedTextField();
        checkInDate = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        addOnsArea = new javax.swing.JTextArea();
        estimatedArrivalPicker = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        clearBtn1 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();

        jCheckBox1.setText("jCheckBox1");

        setMaximumSize(new java.awt.Dimension(1280, 639));
        setMinimumSize(new java.awt.Dimension(1280, 639));

        jPanel1.setBackground(new java.awt.Color(19, 19, 19));

        reservedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Guest Name", "Check-In Date", "Check-Out Date"
            }
        ));
        reservedTable.setFocusable(false);
        reservedTable.getTableHeader().setReorderingAllowed(false);
        reservedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reservedTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(reservedTable);

        jLabel10.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(212, 171, 97));
        jLabel10.setText("Reserved Guests:");

        jLabel11.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(212, 171, 97));
        jLabel11.setText("New Reservation:");

        jLabel12.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(212, 171, 97));
        jLabel12.setText("Reservation Details:");

        jButton2.setBackground(new java.awt.Color(212, 171, 97));
        jButton2.setForeground(new java.awt.Color(19, 19, 19));
        jButton2.setText("Remove Reservation");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton2MousePressed(evt);
            }
        });

        checkOutLbl.setForeground(new java.awt.Color(212, 171, 97));
        checkOutLbl.setText("Check-Out Date:");

        guestNameLbl.setForeground(new java.awt.Color(212, 171, 97));
        guestNameLbl.setText("Guest Name:");

        jLabel3.setForeground(new java.awt.Color(212, 171, 97));
        jLabel3.setText("Full Name:");

        jLabel4.setForeground(new java.awt.Color(212, 171, 97));
        jLabel4.setText("Contact Number:");

        jLabel5.setForeground(new java.awt.Color(212, 171, 97));
        jLabel5.setText("Email:");

        jLabel17.setForeground(new java.awt.Color(212, 171, 97));
        jLabel17.setText("Number of Guests:");

        jLabel13.setForeground(new java.awt.Color(212, 171, 97));
        jLabel13.setText("Room Types:");

        jLabel18.setForeground(new java.awt.Color(212, 171, 97));
        jLabel18.setText("Number of Rooms:");

        jLabel6.setForeground(new java.awt.Color(212, 171, 97));
        jLabel6.setText("Check-In Date:");

        jLabel7.setForeground(new java.awt.Color(212, 171, 97));
        jLabel7.setText("Check-Out Date:");

        jLabel8.setForeground(new java.awt.Color(212, 171, 97));
        jLabel8.setText("Estimated Arrival:");

        doubleCheck.setForeground(new java.awt.Color(212, 171, 97));
        doubleCheck.setText("Double");
        doubleCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleCheckActionPerformed(evt);
            }
        });

        singleCheck.setForeground(new java.awt.Color(212, 171, 97));
        singleCheck.setText("Single");

        twinCheck.setForeground(new java.awt.Color(212, 171, 97));
        twinCheck.setText("Twin");

        executiveCheck.setForeground(new java.awt.Color(212, 171, 97));
        executiveCheck.setText("Executive");

        deluxeCheck.setForeground(new java.awt.Color(212, 171, 97));
        deluxeCheck.setText("Deluxe");

        suiteCheck.setForeground(new java.awt.Color(212, 171, 97));
        suiteCheck.setText("Suite");

        presidentialCheck.setForeground(new java.awt.Color(212, 171, 97));
        presidentialCheck.setText("Presidential");

        jLabel20.setForeground(new java.awt.Color(212, 171, 97));
        jLabel20.setText("Add-Ons / Extra Services:");

        uncheckBtn.setBackground(new java.awt.Color(212, 171, 97));
        uncheckBtn.setForeground(new java.awt.Color(19, 19, 19));
        uncheckBtn.setText("Uncheck All");
        uncheckBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                uncheckBtnMousePressed(evt);
            }
        });

        laundryCheck.setForeground(new java.awt.Color(212, 171, 97));
        laundryCheck.setText("Laundry Service");

        barCheck.setForeground(new java.awt.Color(212, 171, 97));
        barCheck.setText("Mini Bar Access");
        barCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barCheckActionPerformed(evt);
            }
        });

        parkingCheck.setForeground(new java.awt.Color(212, 171, 97));
        parkingCheck.setText("Parking");

        airportCheck.setForeground(new java.awt.Color(212, 171, 97));
        airportCheck.setText("Airport Pick-up");

        clearBtn.setBackground(new java.awt.Color(212, 171, 97));
        clearBtn.setForeground(new java.awt.Color(19, 19, 19));
        clearBtn.setText("Process Payment");
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clearBtnMousePressed(evt);
            }
        });

        ReserveBtn.setBackground(new java.awt.Color(212, 171, 97));
        ReserveBtn.setForeground(new java.awt.Color(19, 19, 19));
        ReserveBtn.setText("Reserve");
        ReserveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ReserveBtnMousePressed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(212, 171, 97));
        jButton7.setForeground(new java.awt.Color(19, 19, 19));
        jButton7.setText("Check Availablity");
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton7MousePressed(evt);
            }
        });

        reservationIDLbl.setForeground(new java.awt.Color(212, 171, 97));
        reservationIDLbl.setText("Reservation ID:");

        jLabel15.setForeground(new java.awt.Color(212, 171, 97));
        jLabel15.setText("Notes:");

        checkInLbl.setForeground(new java.awt.Color(212, 171, 97));
        checkInLbl.setText("Check-In Date:");

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        notesArea.setEditable(false);
        notesArea.setBackground(new java.awt.Color(19, 19, 19));
        notesArea.setColumns(20);
        notesArea.setForeground(new java.awt.Color(212, 171, 97));
        notesArea.setRows(5);
        notesArea.setBorder(null);
        notesArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        notesArea.setFocusable(false);
        jScrollPane2.setViewportView(notesArea);

        contactLbl.setForeground(new java.awt.Color(212, 171, 97));
        contactLbl.setText("Contact Number:");

        emailLbl.setForeground(new java.awt.Color(212, 171, 97));
        emailLbl.setText("Email:");

        notesTextArea.setColumns(20);
        notesTextArea.setRows(5);
        jScrollPane3.setViewportView(notesTextArea);

        jLabel22.setForeground(new java.awt.Color(212, 171, 97));
        jLabel22.setText("Add-Ons / Extra Services:");

        jLabel23.setForeground(new java.awt.Color(212, 171, 97));
        jLabel23.setText("Notes:");

        jScrollPane4.setBackground(new java.awt.Color(19, 19, 19));
        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        addOnsArea.setEditable(false);
        addOnsArea.setBackground(new java.awt.Color(19, 19, 19));
        addOnsArea.setColumns(20);
        addOnsArea.setForeground(new java.awt.Color(212, 171, 97));
        addOnsArea.setRows(5);
        addOnsArea.setBorder(null);
        addOnsArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addOnsArea.setFocusable(false);
        jScrollPane4.setViewportView(addOnsArea);

        jLabel25.setForeground(new java.awt.Color(212, 171, 97));
        jLabel25.setText("Extra Linen");

        jLabel26.setForeground(new java.awt.Color(212, 171, 97));
        jLabel26.setText("Extra Bed");

        clearBtn1.setBackground(new java.awt.Color(212, 171, 97));
        clearBtn1.setForeground(new java.awt.Color(19, 19, 19));
        clearBtn1.setText("Clear all");
        clearBtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clearBtn1MousePressed(evt);
            }
        });

        jToggleButton1.setBackground(new java.awt.Color(212, 171, 97));
        jToggleButton1.setForeground(new java.awt.Color(19, 19, 19));
        jToggleButton1.setText("Edit Mode");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(129, 129, 129)
                        .addComponent(jLabel4)
                        .addGap(101, 101, 101)
                        .addComponent(jLabel5)
                        .addGap(161, 161, 161)
                        .addComponent(jLabel6)
                        .addGap(115, 115, 115)
                        .addComponent(jLabel7)
                        .addGap(105, 105, 105)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(guestName, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkInDate, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkOutDate, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(estimatedArrivalPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(302, 302, 302)
                        .addComponent(jLabel12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reservationIDLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkInLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20))))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(guestNameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(31, 31, 31)
                                    .addComponent(contactLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(checkOutLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(58, 58, 58)
                                    .addComponent(emailLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(twinCheck)
                                .addGap(72, 72, 72)
                                .addComponent(suiteCheck)
                                .addGap(521, 521, 521)
                                .addComponent(clearBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(ReserveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(roomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13)
                                    .addComponent(singleCheck)
                                    .addComponent(doubleCheck))
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(guestSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(executiveCheck)
                                    .addComponent(deluxeCheck))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton7)
                                    .addComponent(presidentialCheck)
                                    .addComponent(uncheckBtn))
                                .addGap(89, 89, 89)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(laundryCheck)
                                    .addComponent(barCheck)
                                    .addComponent(parkingCheck))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(airportCheck)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(extraLinenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(extraBedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(56, 56, 56)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(78, 78, 78))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel11)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guestName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkInDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkOutDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(estimatedArrivalPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(4, 4, 4)
                        .addComponent(roomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(14, 14, 14)
                        .addComponent(singleCheck)
                        .addGap(10, 10, 10)
                        .addComponent(doubleCheck))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(guestSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7))
                        .addGap(47, 47, 47)
                        .addComponent(executiveCheck)
                        .addGap(10, 10, 10)
                        .addComponent(deluxeCheck))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(presidentialCheck)
                        .addGap(10, 10, 10)
                        .addComponent(uncheckBtn))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel22)
                        .addGap(14, 14, 14)
                        .addComponent(laundryCheck)
                        .addGap(10, 10, 10)
                        .addComponent(barCheck)
                        .addGap(10, 10, 10)
                        .addComponent(parkingCheck))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(airportCheck)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(extraBedSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26))
                                .addGap(8, 8, 8)
                                .addComponent(extraLinenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel25))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel15)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(twinCheck)
                    .addComponent(suiteCheck)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(clearBtn)
                        .addComponent(clearBtn1))
                    .addComponent(ReserveBtn))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jToggleButton1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(reservationIDLbl)
                        .addGap(15, 15, 15)
                        .addComponent(checkInLbl)
                        .addGap(47, 47, 47)
                        .addComponent(jLabel20)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guestNameLbl)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(contactLbl)))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkOutLbl)
                            .addComponent(emailLbl))
                        .addGap(44, 44, 44)
                        .addComponent(jLabel23)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
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

    private void doubleCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_doubleCheckActionPerformed

    private void uncheckBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uncheckBtnMousePressed

       doubleCheck.setSelected(false);
       singleCheck.setSelected(false);
       twinCheck.setSelected(false);
       executiveCheck.setSelected(false);
       deluxeCheck.setSelected(false);
       suiteCheck.setSelected(false);
       presidentialCheck.setSelected(false);
    }//GEN-LAST:event_uncheckBtnMousePressed

    private void barCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barCheckActionPerformed

    private void jButton7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7MousePressed

    private void clearBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearBtnMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearBtnMousePressed

    private void ReserveBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReserveBtnMousePressed
        addReservation();
    }//GEN-LAST:event_ReserveBtnMousePressed

    private void clearBtn1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearBtn1MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearBtn1MousePressed

    private void jButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MousePressed
        deleteFunction();
    }//GEN-LAST:event_jButton2MousePressed

    private void reservedTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reservedTableMouseClicked
      addOnsArea.setText(null);
    int row = reservedTable.getSelectedRow();
    
    if (row == -1) return;
    
    String selection = reservedTable.getModel().getValueAt(row, 0).toString();
    String sql = "SELECT * FROM Guest_Reservation WHERE ReservationID = " + Integer.parseInt(selection.substring(1));
    
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            reservationIDLbl.setText("Reservation ID: " + String.format("R%05d", rs.getInt("ReservationID")));
            guestNameLbl.setText("Guest Name: " + rs.getString("FullName"));
            contactLbl.setText("Contact Number : " + rs.getString("ContactNumber"));
            checkInLbl.setText("Check-In Date: " + rs.getString("CheckIn_Date"));
            checkOutLbl.setText("Check-Out Date: " + rs.getString("CheckOut_Date"));
            emailLbl.setText("Email : " + rs.getString("Email"));

            if (rs.getString("Laundry").equals("true")) {
                addOnsArea.append("● Laundry \n");
            }
            if (rs.getString("MiniBar").equals("true")) {
                addOnsArea.append("● Mini-Bar Access \n");
            }
            if (rs.getString("Parking").equals("true")) {
                addOnsArea.append("● Parking \n");
            }
            if (rs.getString("AirportPickup").equals("true")) {
                addOnsArea.append("● Airport Pickup \n");
            }
            if (rs.getInt("ExtraBed") >= 1) {
                addOnsArea.append("● Extra Bed - " + rs.getInt("ExtraBed") + " \n");
            }
            if (rs.getInt("ExtraLinen") >= 1) {
                addOnsArea.append("● Extra Linen - " + rs.getInt("ExtraLinen") + " \n");
            }

            notesArea.setText(rs.getString("Notes"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // If in edit mode, load the data into form fields
    if (isEditMode) {
        loadReservationForEdit(row);
    }
    }//GEN-LAST:event_reservedTableMouseClicked

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
         isEditMode = jToggleButton1.isSelected();
    
    if (isEditMode) {
        jToggleButton1.setText("Cancel Edit");
        ReserveBtn.setText("Update");
        
        // Check if a reservation is selected
        int row = reservedTable.getSelectedRow();
        if (row != -1) {
            loadReservationForEdit(row);
        } else {
            errorNotif("Please select a reservation to edit");
            jToggleButton1.setSelected(false);
            isEditMode = false;
            jToggleButton1.setText("Edit Mode");
            ReserveBtn.setText("Reserve");
        }
    } else {
        jToggleButton1.setText("Edit Mode");
        ReserveBtn.setText("Reserve");
        clearForm();
        selectedReservationId = -1;
    }
    }//GEN-LAST:event_jToggleButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ReserveBtn;
    private javax.swing.JTextArea addOnsArea;
    private javax.swing.JCheckBox airportCheck;
    private javax.swing.JCheckBox barCheck;
    private javax.swing.JFormattedTextField checkInDate;
    private javax.swing.JLabel checkInLbl;
    private javax.swing.JFormattedTextField checkOutDate;
    private javax.swing.JLabel checkOutLbl;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton clearBtn1;
    private javax.swing.JTextField contactField;
    private javax.swing.JLabel contactLbl;
    private javax.swing.JCheckBox deluxeCheck;
    private javax.swing.JCheckBox doubleCheck;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLbl;
    private javax.swing.JFormattedTextField estimatedArrivalPicker;
    private javax.swing.JCheckBox executiveCheck;
    private javax.swing.JSpinner extraBedSpinner;
    private javax.swing.JSpinner extraLinenSpinner;
    private javax.swing.JTextField guestName;
    private javax.swing.JLabel guestNameLbl;
    private javax.swing.JSpinner guestSpinner;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton7;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JCheckBox laundryCheck;
    private javax.swing.JTextArea notesArea;
    private javax.swing.JTextArea notesTextArea;
    private javax.swing.JCheckBox parkingCheck;
    private javax.swing.JCheckBox presidentialCheck;
    private javax.swing.JLabel reservationIDLbl;
    private javax.swing.JTable reservedTable;
    private javax.swing.JSpinner roomSpinner;
    private javax.swing.JCheckBox singleCheck;
    private javax.swing.JCheckBox suiteCheck;
    private javax.swing.JCheckBox twinCheck;
    private javax.swing.JButton uncheckBtn;
    // End of variables declaration//GEN-END:variables
}
