package fr.xlim.ssd.opal.gui.view.components.tab;


import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;



/**
 * @author Thibault
 * @author razaina
 */
public class AuthenticationPanel extends JPanel{

    public String title = "Authentication";

    public AuthenticationPanel()
    {
        setLayout(new BorderLayout());

        CardXMLManager cardManager = new CardXMLManager("JCOP20");

        //first panel container
        JPanel jpl1 = new JPanel();
        jpl1.setLayout(new GridLayout(0,2));

        //components definitions
        JLabel ISDAII = new JLabel("Issuer Security Domain AII :");
        JTextField tf1 = new JTextField(20);


        //Card ISD display test
        tf1.setText(cardManager.getISDAID());

        JLabel scpMode = new JLabel("SCP Mode : ");
        JComboBox scpModeCB = new JComboBox();
        scpModeCB.addItem(cardManager.getScpMode());

        JLabel securityLevel = new JLabel("Security level : ");
        JComboBox securityLevelCB = new JComboBox();
        securityLevelCB.addItem("NO SECURITY LEVEL");

        JLabel transProto = new JLabel("Transmission protocol : ");
        JComboBox transProtoCB = new JComboBox();
        transProtoCB.addItem(cardManager.getTransmissionProtocol());

        //add components to panel container
        jpl1.add(ISDAII);
        jpl1.add(tf1);
        jpl1.add(scpMode);
        jpl1.add(scpModeCB);
        jpl1.add(securityLevel);
        jpl1.add(securityLevelCB);
        jpl1.add(transProto);
        jpl1.add(transProtoCB);

        //Second panel
        JPanel jpl2 = new JPanel();
        TitledBorder t1 = new TitledBorder("Keys");
        jpl2.setBorder(t1);

        //Third panel
        JPanel jpl3 = new JPanel();
        jpl3.setLayout(new GridLayout(0,2));
        JLabel implementation = new JLabel("Implementation : ");
        JComboBox implementationCB = new JComboBox();
        implementationCB.addItem(cardManager.getImplementation());

        jpl3.add(implementation);
        jpl3.add(implementationCB);


       /* JPanel jpl4 = new JPanel();
        jpl4.setLayout(new BorderLayout());

        JButton authenticate = new JButton("Authenticate");
        jpl4.add(authenticate, BorderLayout.WEST);*/

        add(jpl1, BorderLayout.NORTH);
        add(jpl2, BorderLayout.CENTER);
        add(jpl3, BorderLayout.SOUTH);
    }
}
