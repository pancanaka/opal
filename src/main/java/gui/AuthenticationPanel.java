package gui;
 
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.GridLayout; 
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



/**
 *
 * @author razaina
 */
public class AuthenticationPanel extends JPanel{

    public String title = "Authentication";
 
    public AuthenticationPanel()
    {
        JPanel jplPanel = new JPanel();
	//JLabel jlbDisplay = new JLabel("Authentication view");
	//jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new FlowLayout());
	//jplPanel.add(jlbDisplay);
 
        add(jplPanel);
 
        JPanel jpl1 = new JPanel();
        GroupLayout layout = new GroupLayout(jpl1);
        jpl1.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel ISDAII = new JLabel("Issuer Security Domain AII");
        JTextField tf1 = new JTextField(20);

        jpl1.add(ISDAII);
        jpl1.add(tf1);


        add(jpl1);
    }
}
