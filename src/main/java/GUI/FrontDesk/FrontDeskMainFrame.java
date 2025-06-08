/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI.FrontDesk;


import customElements.drawer.DrawerLayout;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.CardLayout;
import java.awt.Component;
import raven.drawer.Drawer;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
/**
 *
 * @author ADMIN
 */
public class FrontDeskMainFrame extends javax.swing.JFrame {
    CardLayout cardLayout;
    
    public FrontDeskMainFrame() {
        Notifications.getInstance().setJFrame(this);
        initComponents();
        initDrawer();
        initLayout();
        cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, "Dashboard");
    }
    
    
    private void initDrawer(){
        GlassPanePopup.install(this);
        DrawerLayout drawer = new DrawerLayout(this);
        Drawer.getInstance().setDrawerBuilder(drawer);
    }
    
    public void showPanel(String name) {
    contentPanel.remove(getPanelByName(name));
    switch (name) {
        case "Dashboard":
            dashboard1 = new Dashboard();
            contentPanel.add(dashboard1, "Dashboard");
            break;
        case "checkIn":
            checkIn1 = new GUI.FrontDesk.Management.GuestManagement.CheckIn();
            contentPanel.add(checkIn1, "checkIn");
            break;
        case "checkOut":
            checkOut1 = new GUI.FrontDesk.Management.GuestManagement.CheckOut();
            contentPanel.add(checkOut1, "checkOut");
            break;
        case "guestRecords":
            guestRecords1 = new GUI.FrontDesk.Management.GuestManagement.GuestRecords();
            contentPanel.add(guestRecords1, "guestRecords");
            break;
        case "Reservations":
            reservations2 = new GUI.FrontDesk.Management.Reservations();
            contentPanel.add(reservations2, "Reservations");
            break;
    }
    cardLayout.show(contentPanel, name);
    contentPanel.revalidate();
    contentPanel.repaint();
}
    
    private Component getPanelByName(String name) {
    for (java.awt.Component comp : contentPanel.getComponents()) {
        if (contentPanel.getLayout() instanceof CardLayout) {
            if (((java.awt.CardLayout) contentPanel.getLayout()).toString().contains(name)) {
                return comp;
            }
        }
    }
    switch (name) {
        case "Dashboard":
            return dashboard1;
        case "checkIn":
            return checkIn1;
        case "checkOut":
            return checkOut1;
        case "guestRecords":
            return guestRecords1;
        case "Reservations":
            return reservations2;
        default:
            return null;
        }
    }

    public void initLayout(){
        contentPanel.add(checkIn1, "checkIn");
        contentPanel.add(checkOut1, "checkOut");
        contentPanel.add(guestRecords1, "guestRecords");
        contentPanel.add(dashboard1, "Dashboard");
        contentPanel.add(reservations2,"Reservations");
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        reservations1 = new GUI.FrontDesk.Management.Reservations();
        header = new javax.swing.JPanel();
        CompanyLogo = new javax.swing.JLabel();
        LogoLabel = new javax.swing.JLabel();
        MenuButton = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        checkIn1 = new GUI.FrontDesk.Management.GuestManagement.CheckIn();
        checkOut1 = new GUI.FrontDesk.Management.GuestManagement.CheckOut();
        guestRecords1 = new GUI.FrontDesk.Management.GuestManagement.GuestRecords();
        dashboard1 = new GUI.FrontDesk.Dashboard();
        reservations2 = new GUI.FrontDesk.Management.Reservations();
        divider = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        header.setBackground(new java.awt.Color(19, 19, 19));

        CompanyLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/70x70-logo.png"))); // NOI18N

        LogoLabel.setFont(new java.awt.Font("Elephant", 1, 18)); // NOI18N
        LogoLabel.setForeground(new java.awt.Color(212, 171, 97));
        LogoLabel.setText("Hôtel La Souverain");

        MenuButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menu-icon.png"))); // NOI18N
        MenuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuButtonMousePressed(evt);
            }
        });

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(MenuButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CompanyLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoLabel)
                .addContainerGap(1001, Short.MAX_VALUE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CompanyLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(LogoLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MenuButton)
                .addGap(19, 19, 19))
        );

        contentPanel.setBackground(new java.awt.Color(19, 19, 19));
        contentPanel.setLayout(new java.awt.CardLayout());
        contentPanel.add(checkIn1, "card2");
        contentPanel.add(checkOut1, "card3");
        contentPanel.add(guestRecords1, "card4");
        contentPanel.add(dashboard1, "card5");
        contentPanel.add(reservations2, "card6");

        divider.setBackground(new java.awt.Color(212, 171, 97));

        javax.swing.GroupLayout dividerLayout = new javax.swing.GroupLayout(divider);
        divider.setLayout(dividerLayout);
        dividerLayout.setHorizontalGroup(
            dividerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dividerLayout.setVerticalGroup(
            dividerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(divider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(divider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void MenuButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuButtonMousePressed
        Drawer.getInstance().showDrawer();
    }//GEN-LAST:event_MenuButtonMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrontDeskMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrontDeskMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrontDeskMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrontDeskMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatDarkLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrontDeskMainFrame().setVisible(true);
            }
        });
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CompanyLogo;
    private javax.swing.JLabel LogoLabel;
    private javax.swing.JLabel MenuButton;
    private GUI.FrontDesk.Management.GuestManagement.CheckIn checkIn1;
    private GUI.FrontDesk.Management.GuestManagement.CheckOut checkOut1;
    private javax.swing.JPanel contentPanel;
    private GUI.FrontDesk.Dashboard dashboard1;
    private javax.swing.JPanel divider;
    private GUI.FrontDesk.Management.GuestManagement.GuestRecords guestRecords1;
    private javax.swing.JPanel header;
    private GUI.FrontDesk.Management.Reservations reservations1;
    private GUI.FrontDesk.Management.Reservations reservations2;
    // End of variables declaration//GEN-END:variables
}

