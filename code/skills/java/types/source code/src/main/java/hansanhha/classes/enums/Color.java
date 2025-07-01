package hansanhha.classes.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Color {

    RED, GREEN, BLUE;

    static final Map<String,Color> colorMap = new HashMap<>();

    // 생성자에서 참조 타입의 static 멤버 필드에 접근할 수 없다
//    Color() {
//        colorMap.put(toString(), this);
//    }

    static {
        for (Color c : Color.values())
            colorMap.put(c.toString(), c);
    }
}
