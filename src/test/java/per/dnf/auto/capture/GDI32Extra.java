package per.dnf.auto.capture;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description
 * @date 2023/3/6 19:10
 */
public interface GDI32Extra extends GDI32 {

    GDI32Extra INSTANCE = (GDI32Extra) Native.loadLibrary("gdi32", GDI32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

    public boolean BitBlt(WinDef.HDC hObject, int nXDest, int nYDest, int nWidth, int nHeight, WinDef.HDC hObjectSource, int nXSrc, int nYSrc, WinDef.DWORD dwRop);

}
