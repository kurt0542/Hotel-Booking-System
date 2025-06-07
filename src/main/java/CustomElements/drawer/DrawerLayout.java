package customElements.drawer;

import GUI.FrontDesk.ChangePassword;
import GUI.FrontDesk.Dashboard;
import GUI.FrontDesk.FrontDeskMainFrame;
import GUI.LoginPage;
import javax.swing.JOptionPane;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.menu.MenuAction;
import raven.drawer.component.menu.MenuEvent;
import raven.drawer.component.menu.MenuValidation;
import raven.drawer.component.menu.SimpleMenuOption;

/**
 *
 * @author RAVEN
 */
public class DrawerLayout extends SimpleDrawerBuilder {
    private FrontDeskMainFrame mainFrame;
    private String employeeName;
    public String getEmployeeName(){
        return employeeName;
    }
    public DrawerLayout(FrontDeskMainFrame mainFrame) {
        this.mainFrame = mainFrame;

    }
    
    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        return new SimpleHeaderData()
              //  .setIcon(new AvatarIcon(getClass().getResource("/raven/image/profile.png"), 60, 60, 999))
                .setTitle(LoginPage.getEmployeeName())
                .setDescription(LoginPage.getRole());
            
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"~MAIN~"},
            {"Dashboard"},
            {"~MANAGEMENT~"},
            {"Guest Management", "Check-In","Check-Out", "Guest Records"},
           // {"Room Management"},
            {"Reservations"},
//            {"~BILLING~"},
//            {"Generate Invoice"},
//            {"Payments"},
//            {"Refunds"},
            {"~SETTINGS~"},
            {"Create an Account"},
            {"Change Password"},
            {"Logout"}};

        return new SimpleMenuOption()
                .setMenus(menus)
                .setIconScale(0.45f)
                .addMenuEvent(new MenuEvent() {
                    @Override
                    public void selected(MenuAction action, int index, int subIndex) {
                        if (index == 0 && subIndex == 0) {
                            mainFrame.getDashboardPanel().refresh();
                            mainFrame.showPanel("Dashboard");
                        }else if (index == 1 && subIndex == 1) {
                            mainFrame.showPanel("checkIn");
                        }
                        else if (index == 1 && subIndex == 2) {
                            mainFrame.showPanel("checkOut");
                        }
                        else if (index == 1 && subIndex == 3) {
                            mainFrame.showPanel("guestRecords");  
                        }
                        else if(index == 2 && subIndex == 0){
                            mainFrame.showPanel("Reservations");
                        }
                        else if (index == 3 && subIndex == 0) {    
                          if(LoginPage.getRole().equals("Manager")){
                              
                          }else{
                              JOptionPane.showMessageDialog(mainFrame, 
                            "You do not have enough permission to access this feature.", 
                            "Access Denied", 
                            JOptionPane.ERROR_MESSAGE);
                          }
                        }
                        else if (index == 4 && subIndex == 0) {    
                          new ChangePassword().setVisible(true);
                        }
                        else if (index == 5 && subIndex == 0) {    
                          new LoginPage().setVisible(true);
                            mainFrame.dispose();
                        }
                        if(index == 1 && subIndex == 0){
                            
                        }
                        else{
                            raven.drawer.Drawer.getInstance().closeDrawer();
                        }
                        System.out.println("Menu selected " + index + " " + subIndex);
                    }
                })
                .setMenuValidation(new MenuValidation() {
                    @Override
                    public boolean menuValidation(int index, int subIndex) {
//                        if(index==0){
//                            return false;
//                        }else if(index==3){
//                            return false;
//                        }
                        return true;
                    }

                });
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("OOP Final Project")
                .setDescription("Submitted by: Kurt Justin Aquino");
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }
}
