package per.dnf.auto.capture;

import com.sun.jna.Memory;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import per.dnf.auto.entity.WindowObj;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author kbdog
 * @package per.dnf.auto.capture
 * @description 对windows窗口进行操作工具类
 * @date 2023/3/10 0:15
 */
@Slf4j
public class CaptureWindowTools {
    //使用jna遍历窗口获取目标窗口
    public static WindowObj getGoalWindows(String className, String windowsName){
        User32 instance = User32.INSTANCE;
        //有className和title
        if(null!=className&&!"".equals(className)){
            WinDef.HWND hwnd = instance.FindWindow(className, windowsName);
            String title = WindowUtils.getWindowTitle(hwnd);
            WinDef.RECT rect=new WinDef.RECT();
            instance.GetWindowRect(hwnd,rect);
            Rectangle rectangle = rect.toRectangle();
            String filePath = "";
            try {
                filePath = WindowUtils.getProcessFilePath(hwnd);
            }catch (Win32Exception e){
                return null;
            }
            if(null!=hwnd){
                int processId = instance.GetWindowThreadProcessId(hwnd, null);
                if(!"".equals(title)&&windowsName!=null&&title.indexOf(windowsName)!=-1){
                    WindowObj windowObj=new WindowObj();
//                    Integer x=(int)rectangle.getX();
//                    Integer y=(int)rectangle.getY();
                    Integer width=(int) rectangle.getWidth();
                    Integer height=(int) rectangle.getHeight();
//                    windowObj.setX(x);
//                    windowObj.setY(y);
                    windowObj.setRectangle(rectangle);
                    windowObj.setHwnd(hwnd);
                    windowObj.setWidth(width);
                    windowObj.setHeight(height);
                    windowObj.setTitle(title);
                    windowObj.setFilePath(filePath);
                    windowObj.setRectangleString(rectangle.toString());
                    windowObj.setProcessId(processId);
                    return windowObj;
                }
            }else {
                return null;
            }
        }else {
            if(null!=windowsName&&!"".equals(windowsName)){
                //无className有title
                List<DesktopWindow> windowList = WindowUtils.getAllWindows(true);
                for (DesktopWindow desktopWindow : windowList) {
                    String filePath = desktopWindow.getFilePath();
                    String title = desktopWindow.getTitle();
                    Rectangle rectangle = desktopWindow.getLocAndSize();
                    //窗口handle
                    WinDef.HWND hwnd = desktopWindow.getHWND();
                    int processId = instance.GetWindowThreadProcessId(hwnd, null);
                    if(!"".equals(title)&&windowsName!=null&&title.indexOf(windowsName)!=-1){
                        WindowObj windowObj=new WindowObj();
                        Integer width=(int) rectangle.getWidth();
                        Integer height=(int) rectangle.getHeight();
                        windowObj.setRectangle(rectangle);
                        windowObj.setHwnd(hwnd);
                        windowObj.setWidth(width);
                        windowObj.setHeight(height);
                        windowObj.setTitle(title);
                        windowObj.setFilePath(filePath);
                        windowObj.setRectangleString(rectangle.toString());
                        windowObj.setProcessId(processId);
                        return windowObj;
                    }
                }
            }
        }
        return null;
    }

    //截取当前窗口图片
    public static BufferedImage captureWindows(WinDef.HWND hWnd){
        WinDef.HDC hdcWindow = User32.INSTANCE.GetDC(hWnd);
        WinDef.HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        WinDef.RECT bounds = new WinDef.RECT();
        User32Extra.INSTANCE.GetClientRect(hWnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        WinNT.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, WinGDIExtra.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return image;
    }
}
