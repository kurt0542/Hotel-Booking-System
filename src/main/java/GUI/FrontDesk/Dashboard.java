/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.FrontDesk;

import Database.DBConnection;
import java.awt.CardLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ADMIN
 */
public class Dashboard extends javax.swing.JPanel {
    private int currentFloor = 2;
    /**
     * Creates new form Dashboard
     */
    Connection conn;
    CardLayout cardLayout;
    public Dashboard() {
        conn = DBConnection.connectDB();
        initComponents();
        initTable();
        initLayout();
        initCounter();
        cardLayout = (CardLayout) jPanel2.getLayout();
        cardLayout.show(jPanel2, "Floor1");
        pageCounter1.setText("Floor " + currentFloor);
    }
    
    
    public void initTable(){
        DefaultTableModel dt = (DefaultTableModel) jTable1.getModel();
        
        dt.setRowCount(0);
        String sql = "SELECT rt.Room_Number, rt.Status, rt.Room_Type, rr.Price_Rate " +
                     "FROM RoomList AS rt " +
                     "INNER JOIN Room_Rate AS rr ON rt.Room_Type = rr.Room_Type";

        try(PreparedStatement pst = conn.prepareStatement(sql)){
            ResultSet rs = pst.executeQuery();

            while(rs.next()){
                String roomNumber = rs.getString("Room_Number");
                String status = rs.getString("Status");
                String roomType = rs.getString("Room_Type");
                double price = rs.getDouble("Price_Rate");


                dt.addRow(new Object[]{roomNumber, roomType, status,  String.format("₱%.2f",price)});
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void initLayout(){
        jPanel2.add(floor11, "Floor2");
        jPanel2.add(floor21,"Floor3");
        jPanel2.add(floor41,"Floor4");
        jPanel2.add(floor51,"Floor5");
    }
    
    public void initCounter() {
        String availableStatusSQL = "SELECT COUNT(*) FROM RoomList WHERE Status = 'Available'";
        String reservationSQL = "SELECT COUNT(*) FROM Guest_Reservation";
        String arrivalSQL = "SELECT CheckIn_Date FROM Guest_Reservation";

        try (
            PreparedStatement pst1 = conn.prepareStatement(availableStatusSQL);
            PreparedStatement pst2 = conn.prepareStatement(reservationSQL);
            PreparedStatement pst3 = conn.prepareStatement(arrivalSQL);
        ) {
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                int count = rs1.getInt(1);
                vacantCount.setText(String.valueOf(count));
            }

            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                int count = rs2.getInt(1);
                reservationsCount.setText(String.valueOf(count));
            }

            ResultSet rs3 = pst3.executeQuery();
            int arrivalTodayCount = 0;
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            while (rs3.next()) {
                String checkInDateStr = rs3.getString("CheckIn_Date");
                if (checkInDateStr != null && !checkInDateStr.trim().isEmpty()) {
                    try {
                        LocalDate checkInDate = LocalDate.parse(checkInDateStr, formatter);
                        if (checkInDate.equals(today)) {
                            arrivalTodayCount++;
                        }
                    } catch (Exception dateParseException) {
                        
                    }
                }
            }

            arrivalCount.setText(String.valueOf(arrivalTodayCount));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comboFilter(String sql){
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        circularProgressBar2 = new CustomElements.CircularProgressBar();
        curvedPanel1 = new CustomElements.CurvedPanel();
        jLabel3 = new javax.swing.JLabel();
        arrivalCount = new javax.swing.JLabel();
        curvedPanel3 = new CustomElements.CurvedPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        curvedPanel4 = new CustomElements.CurvedPanel();
        jLabel5 = new javax.swing.JLabel();
        vacantCount = new javax.swing.JLabel();
        curvedPanel5 = new CustomElements.CurvedPanel();
        jLabel6 = new javax.swing.JLabel();
        reservationsCount = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        floor11 = new GUI.FrontDesk.DashboardPanels.Floor2();
        floor21 = new GUI.FrontDesk.DashboardPanels.Floor3();
        floor41 = new GUI.FrontDesk.DashboardPanels.Floor4();
        floor51 = new GUI.FrontDesk.DashboardPanels.Floor5();
        pageCounter1 = new javax.swing.JLabel();
        leftBtn = new javax.swing.JLabel();
        rightBtn = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jRadioButton1.setText("jRadioButton1");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setBackground(new java.awt.Color(19, 19, 19));
        setPreferredSize(new java.awt.Dimension(1280, 639));

        curvedPanel1.setBackground(new java.awt.Color(212, 175, 55));
        curvedPanel1.setForeground(new java.awt.Color(255, 255, 255));
        curvedPanel1.setPreferredSize(new java.awt.Dimension(275, 120));
        curvedPanel1.setRoundBottomLeft(20);
        curvedPanel1.setRoundBottomRight(20);
        curvedPanel1.setRoundTopLeft(20);
        curvedPanel1.setRoundTopRight(20);

        jLabel3.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Arrivals");

        arrivalCount.setFont(new java.awt.Font("Cambria", 1, 48)); // NOI18N
        arrivalCount.setForeground(new java.awt.Color(255, 255, 255));
        arrivalCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        arrivalCount.setText("00");

        javax.swing.GroupLayout curvedPanel1Layout = new javax.swing.GroupLayout(curvedPanel1);
        curvedPanel1.setLayout(curvedPanel1Layout);
        curvedPanel1Layout.setHorizontalGroup(
            curvedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(arrivalCount, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        curvedPanel1Layout.setVerticalGroup(
            curvedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addContainerGap(84, Short.MAX_VALUE))
            .addComponent(arrivalCount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        curvedPanel3.setBackground(new java.awt.Color(45, 45, 45));
        curvedPanel3.setPreferredSize(new java.awt.Dimension(275, 120));
        curvedPanel3.setRoundBottomLeft(20);
        curvedPanel3.setRoundBottomRight(20);
        curvedPanel3.setRoundTopLeft(20);
        curvedPanel3.setRoundTopRight(20);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Room List:");

        jTable1.setBackground(new java.awt.Color(45, 45, 45));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Room No.", "Type", "Status", "Rate"
            }
        ));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout curvedPanel3Layout = new javax.swing.GroupLayout(curvedPanel3);
        curvedPanel3.setLayout(curvedPanel3Layout);
        curvedPanel3Layout.setHorizontalGroup(
            curvedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(curvedPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        curvedPanel3Layout.setVerticalGroup(
            curvedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );

        curvedPanel4.setBackground(new java.awt.Color(39, 174, 96));
        curvedPanel4.setPreferredSize(new java.awt.Dimension(282, 120));
        curvedPanel4.setRoundBottomLeft(20);
        curvedPanel4.setRoundBottomRight(20);
        curvedPanel4.setRoundTopLeft(20);
        curvedPanel4.setRoundTopRight(20);

        jLabel5.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Vacant");

        vacantCount.setFont(new java.awt.Font("Cambria", 1, 48)); // NOI18N
        vacantCount.setForeground(new java.awt.Color(255, 255, 255));
        vacantCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        vacantCount.setText("00");

        javax.swing.GroupLayout curvedPanel4Layout = new javax.swing.GroupLayout(curvedPanel4);
        curvedPanel4.setLayout(curvedPanel4Layout);
        curvedPanel4Layout.setHorizontalGroup(
            curvedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addContainerGap(186, Short.MAX_VALUE))
            .addGroup(curvedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, curvedPanel4Layout.createSequentialGroup()
                    .addContainerGap(145, Short.MAX_VALUE)
                    .addComponent(vacantCount, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)))
        );
        curvedPanel4Layout.setVerticalGroup(
            curvedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel5)
                .addContainerGap(85, Short.MAX_VALUE))
            .addGroup(curvedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(curvedPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(vacantCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        curvedPanel5.setBackground(new java.awt.Color(230, 126, 34));
        curvedPanel5.setPreferredSize(new java.awt.Dimension(282, 120));
        curvedPanel5.setRoundBottomLeft(20);
        curvedPanel5.setRoundBottomRight(20);
        curvedPanel5.setRoundTopLeft(20);
        curvedPanel5.setRoundTopRight(20);

        jLabel6.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Reservations");

        reservationsCount.setFont(new java.awt.Font("Cambria", 1, 48)); // NOI18N
        reservationsCount.setForeground(new java.awt.Color(255, 255, 255));
        reservationsCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reservationsCount.setText("00");

        javax.swing.GroupLayout curvedPanel5Layout = new javax.swing.GroupLayout(curvedPanel5);
        curvedPanel5.setLayout(curvedPanel5Layout);
        curvedPanel5Layout.setHorizontalGroup(
            curvedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel6)
                .addContainerGap(119, Short.MAX_VALUE))
            .addGroup(curvedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, curvedPanel5Layout.createSequentialGroup()
                    .addContainerGap(146, Short.MAX_VALUE)
                    .addComponent(reservationsCount, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(11, 11, 11)))
        );
        curvedPanel5Layout.setVerticalGroup(
            curvedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(curvedPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(curvedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(curvedPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(reservationsCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel2.setBackground(new java.awt.Color(45, 45, 45));
        jPanel2.setMaximumSize(new java.awt.Dimension(869, 439));
        jPanel2.setMinimumSize(new java.awt.Dimension(869, 439));
        jPanel2.setPreferredSize(new java.awt.Dimension(869, 439));
        jPanel2.setLayout(new java.awt.CardLayout());
        jPanel2.add(floor11, "card2");
        jPanel2.add(floor21, "card3");
        jPanel2.add(floor41, "card4");
        jPanel2.add(floor51, "card5");

        pageCounter1.setBackground(new java.awt.Color(212, 175, 55));
        pageCounter1.setForeground(new java.awt.Color(212, 175, 55));
        pageCounter1.setText("Floor 2");

        leftBtn.setBackground(new java.awt.Color(212, 175, 55));
        leftBtn.setForeground(new java.awt.Color(212, 175, 55));
        leftBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-chevron-left-26.png"))); // NOI18N
        leftBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        leftBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                leftBtnMousePressed(evt);
            }
        });

        rightBtn.setBackground(new java.awt.Color(212, 175, 55));
        rightBtn.setForeground(new java.awt.Color(212, 175, 55));
        rightBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-chevron-right-26.png"))); // NOI18N
        rightBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rightBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rightBtnMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(curvedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(curvedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(curvedPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(curvedPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(leftBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pageCounter1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightBtn)
                        .addGap(407, 407, 407))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(curvedPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(curvedPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(curvedPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rightBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(pageCounter1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(leftBtn)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(curvedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 609, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void leftBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftBtnMousePressed
        if (currentFloor > 2) {
        currentFloor--;
        cardLayout.show(jPanel2, "Floor" + currentFloor);
        pageCounter1.setText("Floor " + currentFloor);
        }
    }//GEN-LAST:event_leftBtnMousePressed

    private void rightBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightBtnMousePressed
        if (currentFloor < 5) {
        currentFloor++;
        cardLayout.show(jPanel2, "Floor" + currentFloor);
        pageCounter1.setText("Floor " + currentFloor);
    }
    }//GEN-LAST:event_rightBtnMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel arrivalCount;
    private CustomElements.CircularProgressBar circularProgressBar2;
    private CustomElements.CurvedPanel curvedPanel1;
    private CustomElements.CurvedPanel curvedPanel3;
    private CustomElements.CurvedPanel curvedPanel4;
    private CustomElements.CurvedPanel curvedPanel5;
    private GUI.FrontDesk.DashboardPanels.Floor2 floor11;
    private GUI.FrontDesk.DashboardPanels.Floor3 floor21;
    private GUI.FrontDesk.DashboardPanels.Floor4 floor41;
    private GUI.FrontDesk.DashboardPanels.Floor5 floor51;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel leftBtn;
    private javax.swing.JLabel pageCounter1;
    private javax.swing.JLabel reservationsCount;
    private javax.swing.JLabel rightBtn;
    private javax.swing.JLabel vacantCount;
    // End of variables declaration//GEN-END:variables
}
