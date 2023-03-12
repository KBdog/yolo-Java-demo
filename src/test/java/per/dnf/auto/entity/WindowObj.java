package per.dnf.auto.entity;

import com.sun.jna.platform.win32.WinDef;
import lombok.Data;

import java.awt.*;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 实体
 * @date 2023/3/6 19:20
 */
@Data
public class WindowObj {
    private WinDef.HWND hwnd;
    private Integer width;
    private Integer height;
    private String title;
    private String filePath;
    private String rectangleString;
    private Rectangle rectangle;
    private int processId;

}
