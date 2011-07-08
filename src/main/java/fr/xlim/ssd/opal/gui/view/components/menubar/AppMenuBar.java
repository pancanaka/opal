package fr.xlim.ssd.opal.gui.view.components.menubar;

import fr.xlim.ssd.opal.gui.event.locale.LocaleChangedEvent;
import fr.xlim.ssd.opal.gui.event.locale.LocaleListener;
import fr.xlim.ssd.opal.gui.view.about.AboutDialog;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;

/**
 * Application menu bar
 *
 * @author David Pequegnot
 */
public class AppMenuBar extends JMenuBar implements LocaleListener {
    private JMenu fileMenu;
    private JMenuItem exitMenuItem;

    private JMenu optionMenu;
    private JMenuItem manageProfilesMenuItem;
    private JMenuItem settingsMenuItem;

    private JMenu helpMenu;
    private JMenuItem helpMenuItem;
    private JMenuItem aboutMenuItem;

    private AboutDialog aboutDialog;

    /**
     * Default constructor.
     *
     * Initialize the view corresponding to the application menu bar
     */
    public AppMenuBar() {
        this.drawComponents();
    }

    /**
     * Refresh the string translations.
     *
     * @param event the event corresponding to the new locale
     */
    @Override
    public void localeChanged(LocaleChangedEvent event) {

    }

    @Action
    public void showProfilesManagement() {

    }

    @Action
    public void showSettings() {

    }

    @Action
    public void showHelp() {

    }

    @Action
    public void showAbout() {
        if (aboutDialog == null) {
            JFrame mainFrame = Application.getInstance(fr.xlim.ssd.opal.gui.App.class).getMainFrame();
            aboutDialog = new AboutDialog(mainFrame);
            aboutDialog.setLocationRelativeTo(mainFrame);
        }
        Application.getInstance(fr.xlim.ssd.opal.gui.App.class).show(aboutDialog);
    }

    /**
     * Initialize the application menu bar components.
     */
    private void drawComponents() {
        this.fileMenu = new JMenu();
        this.fileMenu.setName("fileMenu");

        this.exitMenuItem = new JMenuItem();
        this.exitMenuItem.setName("exitMenuItem");

        this.fileMenu.add(this.exitMenuItem);

        this.optionMenu = new JMenu();
        this.optionMenu.setName("optionMenu");

        this.manageProfilesMenuItem = new JMenuItem();
        this.manageProfilesMenuItem.setName("manageProfilesMenuItem");

        this.settingsMenuItem = new JMenuItem();
        this.settingsMenuItem.setName("settingsMenuItem");

        this.optionMenu.add(this.manageProfilesMenuItem);
        this.optionMenu.add(this.settingsMenuItem);

        this.helpMenu = new JMenu();
        this.helpMenu.setName("helpMenu");

        this.helpMenuItem = new JMenuItem();
        this.helpMenuItem.setName("helpMenuItem");

        this.aboutMenuItem = new JMenuItem();
        this.aboutMenuItem.setName("aboutMenuItem");

        this.helpMenu.add(this.helpMenuItem);
        this.helpMenu.addSeparator();
        this.helpMenu.add(this.aboutMenuItem);

        this.add(fileMenu);
        this.add(optionMenu);
        this.add(helpMenu);

        this.refreshResources();
    }

    /**
     * Refresh menu bar resources.
     *
     * All properties from resource bundles can be refreshed using this method.
     * It is a convenient method for translate purposes.
     */
    private void refreshResources() {
        ResourceMap resourceMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getResourceMap(AppMenuBar.class);

        resourceMap.injectComponents(this);

        ActionMap actionMap = Application
                .getInstance(fr.xlim.ssd.opal.gui.App.class)
                .getContext()
                .getActionMap(AppMenuBar.class, this);
        this.exitMenuItem.setAction(actionMap.get("quit"));
        this.manageProfilesMenuItem.setAction(actionMap.get("showProfilesManagement"));
        this.settingsMenuItem.setAction(actionMap.get("showSettings"));
        this.helpMenuItem.setAction(actionMap.get("showHelp"));
        this.aboutMenuItem.setAction(actionMap.get("showAbout"));
    }
}
