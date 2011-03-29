package gui;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;



/**
 *
 * @author razaina
 */
public class SelectPanel extends JPanel{

    public String title = "Select";
    public SelectPanel()
    {
        JPanel jplPanel = new JPanel();
	JLabel jlbDisplay = new JLabel("Delete view");
	jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
	jplPanel.setLayout(new GridLayout(1, 1));
	jplPanel.add(jlbDisplay);

        add(jplPanel);
    }
}
