/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainActionView.java
 *
 * Created on 14 mai 2009, 19:27:13
 */

package fr.xlim.ssd.opalgui.view.mainaction;

import fr.xlim.ssd.opal.library.params.CardConfigNotFoundException;
import fr.xlim.ssd.opalgui.controller.MainActionController;
import fr.xlim.ssd.opalgui.controller.authentication.AuthenticationController;
import fr.xlim.ssd.opalgui.io.terminal.TerminalConnection;
import fr.xlim.ssd.opalgui.io.terminal.TerminalConnectionEvent;
import fr.xlim.ssd.opalgui.io.terminal.TerminalConnectionListener;
import fr.xlim.ssd.opalgui.model.authenticate.AuthenticationModel;
import fr.xlim.ssd.opalgui.model.mode.ActionModel;
import fr.xlim.ssd.opalgui.model.mode.ActionModelChangedEvent;
import fr.xlim.ssd.opalgui.model.mode.ActionModelListener;
import fr.xlim.ssd.opalgui.model.mode.enumeration.ActionEnum;
import fr.xlim.ssd.opalgui.view.delete.DeletePanelView;
import fr.xlim.ssd.opalgui.view.getstatus.GetStatusView;
import fr.xlim.ssd.opalgui.view.installforinstall.InstallForInstallView;
import fr.xlim.ssd.opalgui.view.installforload.InstallForLoadView;
import fr.xlim.ssd.opalgui.view.load.LoadView;
import fr.xlim.ssd.opalgui.view.select.SelectView;
import fr.xlim.ssd.opalgui.view.sendapducommand.SendAPDUCommandPanelView;
import java.awt.CardLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Fox
 */
public class MainActionView extends javax.swing.JPanel implements ActionModelListener, TerminalConnectionListener {
    private ActionModel              actionModel         = null ;
    private MainActionController     actionController    = null ;
    private AuthenticationModel      authenticationModel = null ;
    private TerminalConnection       terminalConnection  = null ;

    /** Creates new form MainActionView */
    public MainActionView( MainActionController actionController,
                           ActionModel          actionModel,
                           TerminalConnection   terminalConnection ) {
        try {
            this.authenticationModel = AuthenticationModel.getInstance() ;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MainActionView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CardConfigNotFoundException ex) {
            Logger.getLogger(MainActionView.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.actionController = actionController ;
        this.actionModel      = actionModel ;
        this.terminalConnection = terminalConnection ;
        initComponents();
        selectAction( actionModel.getCurrentAction() ) ;
        updateTerminalConnectionStatus();
    }

    private void selectAction( ActionEnum action ) {
        CardLayout cards = (CardLayout) actionsContainerPanel.getLayout() ;
        if ( action == ActionEnum.AUTHENTIFICATE ) {
            cards.show(actionsContainerPanel, "Authenticate");
        } else if ( action == ActionEnum.DELETE_ADVANCED ) {
            cards.show(actionsContainerPanel, "DeleteAdvanced");
        } else if ( action == ActionEnum.GET_STATUS ) {
            cards.show(actionsContainerPanel, "GetStatus");
        } else if ( action == ActionEnum.INSTALL_FOR_INSTALL ) {
            cards.show(actionsContainerPanel, "InstallForInstall");
        } else if ( action == ActionEnum.INSTALL_FOR_LOAD ) {
            cards.show(actionsContainerPanel, "InstallForLoad");
        } else if ( action == ActionEnum.LOAD ) {
            cards.show(actionsContainerPanel, "Load");
        } else if ( action == ActionEnum.SELECT ) {
            cards.show(actionsContainerPanel, "Select");
        } else if ( action == ActionEnum.SEND_APDU_COMMAND ) {
            cards.show(actionsContainerPanel, "SendAPDUCommand");
        } else if ( action == ActionEnum.DELETE_STANDARD ||
                    action == ActionEnum.DELETE_STANDARD_GENERAL ) {
            cards.show(actionsContainerPanel, "DeleteStandard");
            deleteStandardJTabbedPane.setSelectedIndex(0);
        } else if ( action == ActionEnum.DELETE_STANDARD_AUTHENTICATE ) {
            cards.show(actionsContainerPanel, "DeleteStandard");
            deleteStandardJTabbedPane.setSelectedIndex(1);
        } else if ( action == ActionEnum.QUICK_LOAD ||
                    action == ActionEnum.QUICK_LOAD_LOAD ) {
            cards.show(actionsContainerPanel, "QuickLoad");
            quickLoadJTabbedPane.setSelectedIndex(0);
        } else if ( action == ActionEnum.QUICK_LOAD_INSTALL_FOR_LOAD ) {
            cards.show(actionsContainerPanel, "QuickLoad");
            quickLoadJTabbedPane.setSelectedIndex(1);
        } else if ( action == ActionEnum.QUICK_LOAD_AUTHENTICATE ) {
            cards.show(actionsContainerPanel, "QuickLoad");
            quickLoadJTabbedPane.setSelectedIndex(2);
        } else {
            throw new UnsupportedOperationException("Opération non supportée");
        }
    }

    private void updateTerminalConnectionStatus() {
        if ( TerminalConnection.isTerminalPlugged() &&
                TerminalConnection.isCardPlugged()    ) {
            this.terminalConnectionLabel.setText("Terminal and Card plugged");
        } else if ( TerminalConnection.isTerminalPlugged() &&
                !TerminalConnection.isCardPlugged()) {
            this.terminalConnectionLabel.setText("Terminal plugged. Please insert a card");
        } else {
            this.terminalConnectionLabel.setText("No Terminal found");
        }
    }

    @Override
    public void actionChanged( ActionModelChangedEvent evt ) {
        selectAction( evt.getAction() );
    }

    @Override
    public void terminalStatusChanged( TerminalConnectionEvent evt ) {
        System.out.println("Event received");
        updateTerminalConnectionStatus();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        proceedButton = new javax.swing.JButton();
        actionsContainerPanel = new javax.swing.JPanel();
        loadScrollPane = new javax.swing.JScrollPane();
        installForLoadScrollPane = new javax.swing.JScrollPane();
        installForInstallScrollPane = new javax.swing.JScrollPane();
        getStatusScrollPane = new javax.swing.JScrollPane();
        deleteAdvancedScrollPane = new javax.swing.JScrollPane();
        authenticateScrollPane = new javax.swing.JScrollPane();
        sendAPDUCommandScrollPane = new javax.swing.JScrollPane();
        selectScrollPane = new javax.swing.JScrollPane();
        quickLoadJTabbedPane = new javax.swing.JTabbedPane();
        quickLoadLoadJScrollPane = new javax.swing.JScrollPane();
        quickLoadInstallForLoadJScrollPane = new javax.swing.JScrollPane();
        quickLoadAuthenticateJScrollPane = new javax.swing.JScrollPane();
        deleteStandardJTabbedPane = new javax.swing.JTabbedPane();
        deleteStandardScrollPane = new javax.swing.JScrollPane();
        deleteAuthenticateScrollPane = new javax.swing.JScrollPane();
        terminalConnectionLabel = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(fr.xlim.ssd.opalgui.OpalguiApp.class).getContext().getResourceMap(MainActionView.class);
        proceedButton.setText(resourceMap.getString("proceedButton.text")); // NOI18N
        proceedButton.setName("proceedButton"); // NOI18N
        proceedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });

        actionsContainerPanel.setName("actionsContainerPanel"); // NOI18N
        actionsContainerPanel.setLayout(new java.awt.CardLayout());

        loadScrollPane.setName("loadScrollPane"); // NOI18N
        actionsContainerPanel.add(loadScrollPane, "Load");
        loadScrollPane.setViewportView( new LoadView() ) ;

        installForLoadScrollPane.setName("installForLoadScrollPane"); // NOI18N
        actionsContainerPanel.add(installForLoadScrollPane, "InstallForLoad");
        installForLoadScrollPane.setViewportView(new InstallForLoadView() ) ;

        installForInstallScrollPane.setName("installForInstallScrollPane"); // NOI18N
        actionsContainerPanel.add(installForInstallScrollPane, "InstallForInstall");
        installForInstallScrollPane.setViewportView( new InstallForInstallView() ) ;

        getStatusScrollPane.setName("getStatusScrollPane"); // NOI18N
        actionsContainerPanel.add(getStatusScrollPane, "GetStatus");
        getStatusScrollPane.setViewportView(new GetStatusView());

        deleteAdvancedScrollPane.setName("deleteAdvancedScrollPane"); // NOI18N
        actionsContainerPanel.add(deleteAdvancedScrollPane, "DeleteAdvanced");
        deleteAdvancedScrollPane.setViewportView(new DeletePanelView());

        authenticateScrollPane.setName("authenticateScrollPane"); // NOI18N
        actionsContainerPanel.add(authenticateScrollPane, "Authenticate");
        authenticateScrollPane.setViewportView( (new AuthenticationController(this.authenticationModel)).getPanel() ) ;

        sendAPDUCommandScrollPane.setName("sendAPDUCommandScrollPane"); // NOI18N
        actionsContainerPanel.add(sendAPDUCommandScrollPane, "SendAPDUCommand");
        sendAPDUCommandScrollPane.setViewportView( new SendAPDUCommandPanelView() ) ;

        selectScrollPane.setName("selectScrollPane"); // NOI18N
        actionsContainerPanel.add(selectScrollPane, "Select");
        selectScrollPane.setViewportView(new SelectView()) ;

        quickLoadJTabbedPane.setName("quickLoadJTabbedPane"); // NOI18N

        quickLoadLoadJScrollPane.setName("quickLoadLoadJScrollPane"); // NOI18N
        quickLoadJTabbedPane.addTab(resourceMap.getString("quickLoadLoadJScrollPane.TabConstraints.tabTitle"), quickLoadLoadJScrollPane); // NOI18N
        quickLoadLoadJScrollPane.setViewportView(new LoadView()) ;

        quickLoadInstallForLoadJScrollPane.setName("quickLoadInstallForLoadJScrollPane"); // NOI18N
        quickLoadJTabbedPane.addTab(resourceMap.getString("quickLoadInstallForLoadJScrollPane.TabConstraints.tabTitle"), quickLoadInstallForLoadJScrollPane); // NOI18N
        quickLoadInstallForLoadJScrollPane.setViewportView(new InstallForLoadView()) ;

        quickLoadAuthenticateJScrollPane.setName("quickLoadAuthenticateJScrollPane"); // NOI18N
        quickLoadJTabbedPane.addTab(resourceMap.getString("quickLoadAuthenticateJScrollPane.TabConstraints.tabTitle"), quickLoadAuthenticateJScrollPane); // NOI18N
        quickLoadAuthenticateJScrollPane.setViewportView( (new AuthenticationController(authenticationModel)).getPanel() ) ; ;

        actionsContainerPanel.add(quickLoadJTabbedPane, "QuickLoad");

        deleteStandardJTabbedPane.setName("deleteStandardJTabbedPane"); // NOI18N

        deleteStandardScrollPane.setName("deleteStandardScrollPane"); // NOI18N
        deleteStandardJTabbedPane.addTab(resourceMap.getString("deleteStandardScrollPane.TabConstraints.tabTitle"), deleteStandardScrollPane); // NOI18N
        deleteStandardScrollPane.setViewportView( new DeletePanelView() ) ;

        deleteAuthenticateScrollPane.setName("deleteAuthenticateScrollPane"); // NOI18N
        deleteStandardJTabbedPane.addTab(resourceMap.getString("deleteAuthenticateScrollPane.TabConstraints.tabTitle"), deleteAuthenticateScrollPane); // NOI18N
        deleteAuthenticateScrollPane.setViewportView((new AuthenticationController(authenticationModel)).getPanel()) ;

        actionsContainerPanel.add(deleteStandardJTabbedPane, "DeleteStandard");

        terminalConnectionLabel.setText(resourceMap.getString("terminalConnectionLabel.text")); // NOI18N
        terminalConnectionLabel.setName("terminalConnectionLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proceedButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                .addComponent(terminalConnectionLabel)
                .addContainerGap())
            .addComponent(actionsContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(actionsContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proceedButton)
                    .addComponent(terminalConnectionLabel))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void proceedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedButtonActionPerformed
        this.actionController.proceed() ;
    }//GEN-LAST:event_proceedButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionsContainerPanel;
    private javax.swing.JScrollPane authenticateScrollPane;
    private javax.swing.JScrollPane deleteAdvancedScrollPane;
    private javax.swing.JScrollPane deleteAuthenticateScrollPane;
    private javax.swing.JTabbedPane deleteStandardJTabbedPane;
    private javax.swing.JScrollPane deleteStandardScrollPane;
    private javax.swing.JScrollPane getStatusScrollPane;
    private javax.swing.JScrollPane installForInstallScrollPane;
    private javax.swing.JScrollPane installForLoadScrollPane;
    private javax.swing.JScrollPane loadScrollPane;
    private javax.swing.JButton proceedButton;
    private javax.swing.JScrollPane quickLoadAuthenticateJScrollPane;
    private javax.swing.JScrollPane quickLoadInstallForLoadJScrollPane;
    private javax.swing.JTabbedPane quickLoadJTabbedPane;
    private javax.swing.JScrollPane quickLoadLoadJScrollPane;
    private javax.swing.JScrollPane selectScrollPane;
    private javax.swing.JScrollPane sendAPDUCommandScrollPane;
    private javax.swing.JLabel terminalConnectionLabel;
    // End of variables declaration//GEN-END:variables

}
