package per.dnf.auto;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description
 * @date 2023/3/6 19:12
 */
public interface WinGDIExtra extends WinGDI {

    public WinDef.DWORD SRCCOPY = new WinDef.DWORD(0x00CC0020);

}
