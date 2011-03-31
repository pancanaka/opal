package fr.xlim.ssd.opalgui.controller;

import fr.xlim.ssd.opalgui.model.mode.ActionModel;
import fr.xlim.ssd.opalgui.model.mode.enumeration.ModeEnum;
import fr.xlim.ssd.opalgui.model.mode.ModeModel;
import fr.xlim.ssd.opalgui.model.mode.enumeration.ActionEnum;
import fr.xlim.ssd.opalgui.view.mainaction.MainActionView;
import fr.xlim.ssd.opalgui.view.mainmenu.MainMenuView;

/**
 *
 * @author David Pequegnot, Julie Rispal
 * @author Julien Iguchi-Cartigny
 */
public class MainMenuController {

    private ModeModel    modeModel = null;
    private MainMenuView menuView  = null;
    private ActionModel  actionModel = null ;
    private MainActionView actionView = null ;

    public MainMenuController(ModeModel modeModel, ActionModel actionModel) {
        this.modeModel = modeModel;
        this.actionModel = actionModel ;
        menuView = new MainMenuView(this, this.modeModel, this.actionModel);
        this.modeModel.addModeListener(menuView);
    }

    public javax.swing.JPanel getMenuPanel() {
        return menuView;
    }

    public void notifyModeChanged(ModeEnum mode) {
        modeModel.setMode(mode);
    }

    public void notifyActionChanged(ActionEnum action) {
        actionModel.setAction(action);
    }
}
