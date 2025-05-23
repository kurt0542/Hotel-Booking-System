package customElements.drawer;

import GUI.FrontDesk.FrontDeskMainFrame;
import GUI.LoginPage;
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

    public DrawerLayout(FrontDeskMainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    
    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        return new SimpleHeaderData()
              //  .setIcon(new AvatarIcon(getClass().getResource("/raven/image/profile.png"), 60, 60, 999))
                .setTitle("employee name")
                .setDescription("FrontDesk");
            
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"~MAIN~"},
            {"Dashboard"},
            {"Notifications"},
            {"~MANAGEMENT~"},
            {"Guest Management", "Check-In","Check-Out", "Guest Records"},
            {"Room Management"},
            {"Reservations"},
            {"~BILLING~"},
            {"Generate Invoice"},
            {"Payments"},
            {"Refunds"},
            {"~SETTINGS~"},
            {"Change Password"},
            {"Logout"}};

        String icons[] = {
            "dashboard.svg",
            "email.svg",
            "chat.svg",
            "calendar.svg",
            "ui.svg",
            "forms.svg",
            "chart.svg",
            "icon.svg",
            "page.svg",
            "logout.svg"};

        return new SimpleMenuOption()
                .setMenus(menus)
//                .setIcons(icons)
//                .setBaseIconPath("CustomElements/drawer/icon")
                .setIconScale(0.45f)
                .addMenuEvent(new MenuEvent() {
                    @Override
                    public void selected(MenuAction action, int index, int subIndex) {
                        if (index == 0) {
                            
                        }else if (index == 2 && subIndex == 1) {
                            mainFrame.showPanel("checkIn");
                        }
                        else if (index == 2 && subIndex == 2) {
                            mainFrame.showPanel("checkOut");
                        }
                        else if (index == 2 && subIndex == 3) {
                            mainFrame.showPanel("guestRecords");
                            
                        }else if (index == 9 && subIndex == 0) {
                          mainFrame.dispose();
                          new LoginPage().setVisible(true);
                        }
                        if(index == 2 && subIndex == 0){
                            
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
                .setTitle("Java Swing Drawer")
                .setDescription("Version 1.1.0");
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }
}
