package per.dnf.auto;

import com.sun.jna.platform.win32.WinDef;
import lombok.Data;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description
 * @date 2023/3/6 19:20
 */
@Data
public class WindowObj {
    private WinDef.HWND hwnd;
    private Integer width;
    private Integer height;
    private String title;
    private String filePath;
    private String rectangle;
    private int processId;

}
