package fr.xlim.ssd.opal.gui.tools;

/**
 * Created by IntelliJ IDEA.
 * User: fox
 * Date: 19/07/11
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class HexadecimalTools {
    public static boolean isHexadecimalValue(String value) {
        return value.matches("^((0x[0-9A-Fa-f]{0,2}|[0-9A-Fa-f]{0,2})(:|\\s)?)*((0x[0-9A-Fa-f]{0,2}|[0-9A-Fa-f]{0,2}))$");
    }
}
