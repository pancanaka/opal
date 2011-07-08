package fr.xlim.ssd.opal.gui.controller;

import javax.swing.*;

/**
 * Methods to interact with browsers.
 *
 * @author David Pequegnot
 */
public class Browser {
    private static final String MICROSOFT_WINDOWS_RUN_URL_DLL = "rundll32 url.dll,FileProtocolHandler ";
    private static final String[] BROWSERS_LIST =
            {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "google-chrome", "chromium"};

    public static void openUrl(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec(Browser.MICROSOFT_WINDOWS_RUN_URL_DLL + url);
            } else {
                String browser = null;
                for (int idx = 0; idx < Browser.BROWSERS_LIST.length && browser == null; idx++) {
                    if (Runtime.getRuntime().exec(new String[] {"which", Browser.BROWSERS_LIST[idx]}).waitFor() == 0) {
                        browser = Browser.BROWSERS_LIST[idx];
                    }
                }
                Runtime.getRuntime().exec(new String[] {browser, url});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while opening browser:\n" + e.getLocalizedMessage());
        }
    }
}
