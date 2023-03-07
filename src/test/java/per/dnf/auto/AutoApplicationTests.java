package per.dnf.auto;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class AutoApplicationTests {

    @Test
    void contextLoads(){
        User32 instance = User32.INSTANCE;
        List<DesktopWindow> windowList = WindowUtils.getAllWindows(true);
        for (DesktopWindow desktopWindow : windowList) {
            String filePath = desktopWindow.getFilePath();
            String title = desktopWindow.getTitle();
            Rectangle rectangle = desktopWindow.getLocAndSize();
            //窗口handle
            WinDef.HWND hwnd = desktopWindow.getHWND();
            int processId = instance.GetWindowThreadProcessId(hwnd, null);
            if(!"".equals(title)){
//                Kernel32 kernel = Kernel32.INSTANCE;
//                int i = kernel.GetProcessId(hwnd);
//                System.out.println(i);
                System.out.println("窗口标题:"+title);
                System.out.println("文件路径:"+filePath);
                System.out.println("窗口矩形:"+rectangle.toString());
                System.out.println("进程线程id:"+processId);
                System.out.println();
            }

        }
    }

}
