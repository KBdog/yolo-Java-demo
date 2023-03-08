package per.dnf.auto;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 测试获取当前鼠标焦点处窗口应用的绝对路径
 * @date 2023/3/9 5:38
 */
public class GetAliveWindowApplication {
        public static void main(String[] args) throws Exception {
            WinDef.HWND prevFg = null;

            while (true) {
                Thread.sleep(200);

                WinDef.HWND fg = User32.INSTANCE.GetForegroundWindow();

                // don't print the name if it's still the same window as previously
//                if (fg.equals(prevFg)) {
//                    continue;
//                }

                String fgImageName = getImageName(fg);
                if (fgImageName == null) {
                    System.out.println("Failed to get the image name!");
                } else {
                    System.out.println(fgImageName);
                }

                prevFg = fg;
            }
        }

        private static String getImageName(WinDef.HWND window) {
            // Get the process ID of the window
            IntByReference procId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(window, procId);

            // Open the process to get permissions to the image name
            WinNT.HANDLE procHandle = Kernel32.INSTANCE.OpenProcess(
                    Kernel32.PROCESS_QUERY_LIMITED_INFORMATION,
                    false,
                    procId.getValue()
            );

            // Get the image name
            char[] buffer = new char[4096];
            IntByReference bufferSize = new IntByReference(buffer.length);
            boolean success = Kernel32.INSTANCE.QueryFullProcessImageName(procHandle, 0, buffer, bufferSize);

            // Clean up: close the opened process
            Kernel32.INSTANCE.CloseHandle(procHandle);

            return success ? new String(buffer, 0, bufferSize.getValue()) : null;
        }
}
