/*
    ===================================================================================
    빌드 도구를 사용하지 않으면 IDE 상에서 지원하는 자동 완성 및 자동 임포트 등의 기능을 사용하지 못한다
    ===================================================================================
*/

package hansanhha;

import com.google.gson.Gson;

public class App {

    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = gson.toJson(new String[]{"hello", "not using build tool application"});
        System.out.println(json);
    }
}