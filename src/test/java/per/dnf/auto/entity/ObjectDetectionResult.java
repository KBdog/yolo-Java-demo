package per.dnf.auto.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author kbdog
 * @package per.dnf.auto.capture
 * @description 推理实体
 * @date 2023/3/10 0:15
 */
@Data
@AllArgsConstructor
public class ObjectDetectionResult {
    // 类别索引
    int classId;
    // 类别名称
    String className;
    // 置信度
    float confidence;
    // 物体在照片中的横坐标
    int x;
    // 物体在照片中的纵坐标
    int y;
    // 物体宽度
    int width;
    // 物体高度
    int height;
}
